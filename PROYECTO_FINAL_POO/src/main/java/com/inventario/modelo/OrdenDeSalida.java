package com.inventario.modelo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrdenDeSalida {
    private String codigoOrden;
    private String tipoSalida; // VENTA, CONSUMO_INTERNO, TRANSFERENCIA, BAJA
    private String destino; // Cliente, área interna, etc.
    private LocalDateTime fechaOrden;
    private LocalDateTime fechaSalida;
    private String estado; // PENDIENTE, APROBADA, ENTREGADA, CANCELADA
    private double montoTotal;
    private String usuarioCreador;
    private boolean requiereAprobacion;
    private List<ItemOrdenSalida> items;
    private static int contadorID = 1;

    // Constructor vacío
    public OrdenDeSalida() {
        this.codigoOrden = generarCodigoOrden();
        this.fechaOrden = LocalDateTime.now();
        this.estado = "PENDIENTE";
        this.tipoSalida = "VENTA";
        this.items = new ArrayList<>();
        this.montoTotal = 0.0;
        this.requiereAprobacion = true;
    }

    // Constructor con tipo de salida
    public OrdenDeSalida(String tipoSalida, String destino) {
        this();
        this.tipoSalida = tipoSalida;
        this.destino = destino;
        // Determinar si requiere aprobación según el tipo
        this.requiereAprobacion = !"VENTA".equals(tipoSalida);
    }

    // Constructor completo
    public OrdenDeSalida(String codigoOrden, String tipoSalida, String destino,
            LocalDateTime fechaOrden, String estado, String usuarioCreador) {
        this.codigoOrden = codigoOrden;
        this.tipoSalida = tipoSalida;
        this.destino = destino;
        this.fechaOrden = fechaOrden;
        this.estado = estado;
        this.usuarioCreador = usuarioCreador;
        this.items = new ArrayList<>();
        this.requiereAprobacion = !"VENTA".equals(tipoSalida);
        calcularTotal();
    }

    // Método para generar código automáticamente
    private String generarCodigoOrden() {
        return "OS" + String.format("%06d", contadorID++);
    }

    // Método para agregar item a la orden
    public boolean agregarItem(Producto producto, int cantidad) {
        // Verificar stock disponible
        if (producto.getCantidad() < cantidad) {
            return false; // No hay suficiente stock
        }

        // Verificar si el producto ya está en la orden
        for (ItemOrdenSalida item : items) {
            if (item.getProducto().getCodigoProducto().equals(producto.getCodigoProducto())) {
                // Verificar si la nueva cantidad total no excede el stock
                int nuevaCantidad = item.getCantidad() + cantidad;
                if (producto.getCantidad() < nuevaCantidad) {
                    return false;
                }
                item.setCantidad(nuevaCantidad);
                calcularTotal();
                return true;
            }
        }

        // Si no existe, crear nuevo item
        ItemOrdenSalida nuevoItem = new ItemOrdenSalida(producto, cantidad, producto.getPrecio());
        items.add(nuevoItem);
        calcularTotal();
        return true;
    }

    // Método para remover item
    public boolean removerItem(String codigoProducto) {
        boolean removido = items.removeIf(item -> item.getProducto().getCodigoProducto().equals(codigoProducto));
        if (removido) {
            calcularTotal();
        }
        return removido;
    }

    // Método para calcular total
    public void calcularTotal() {
        this.montoTotal = items.stream()
                .mapToDouble(ItemOrdenSalida::getSubtotal)
                .sum();
    }

    // Método para aprobar orden
    public boolean aprobarOrden(String usuarioAprobador) {
        if ("PENDIENTE".equals(this.estado) && requiereAprobacion) {
            this.estado = "APROBADA";
            return true;
        } else if (!requiereAprobacion && "PENDIENTE".equals(this.estado)) {
            // Para ventas que no requieren aprobación, pasar directo a aprobada
            this.estado = "APROBADA";
            return true;
        }
        return false;
    }

    // Método para entregar orden (actualiza stock)
    public boolean entregarOrden(String usuarioEntrega) {
        if ("APROBADA".equals(this.estado)) {
            // Verificar stock disponible antes de procesar
            for (ItemOrdenSalida item : items) {
                if (item.getProducto().getCantidad() < item.getCantidad()) {
                    return false; // Stock insuficiente
                }
            }

            // Actualizar stock de productos
            for (ItemOrdenSalida item : items) {
                Producto producto = item.getProducto();
                producto.setCantidad(producto.getCantidad() - item.getCantidad());
            }

            this.estado = "ENTREGADA";
            this.fechaSalida = LocalDateTime.now();
            return true;
        }
        return false;
    }

    // Método para cancelar orden
    public boolean cancelarOrden(String motivo, String usuarioCancelacion) {
        if (!"ENTREGADA".equals(this.estado)) {
            this.estado = "CANCELADA";
            return true;
        }
        return false;
    }

    // Método para verificar si necesita reposición urgente
    public List<Producto> getProductosStockBajo() {
        List<Producto> productosStockBajo = new ArrayList<>();
        for (ItemOrdenSalida item : items) {
            Producto producto = item.getProducto();
            int stockRestante = producto.getCantidad() - item.getCantidad();
            if (stockRestante <= 10) { // Umbral configurable
                productosStockBajo.add(producto);
            }
        }
        return productosStockBajo;
    }

    // Método para obtener resumen de la orden
    public String getResumen() {
        return String.format("Orden %s - %s - %s - %d items - S/%.2f - %s",
                codigoOrden,
                tipoSalida,
                destino != null ? destino : "N/A",
                items.size(),
                montoTotal,
                estado);
    }

    // Getters
    public String getCodigoOrden() {
        return codigoOrden;
    }

    public String getTipoSalida() {
        return tipoSalida;
    }

    public String getDestino() {
        return destino;
    }

    public LocalDateTime getFechaOrden() {
        return fechaOrden;
    }

    public LocalDateTime getFechaSalida() {
        return fechaSalida;
    }

    public String getEstado() {
        return estado;
    }

    public double getMontoTotal() {
        return montoTotal;
    }

    public String getUsuarioCreador() {
        return usuarioCreador;
    }

    public boolean isRequiereAprobacion() {
        return requiereAprobacion;
    }

    public List<ItemOrdenSalida> getItems() {
        return new ArrayList<>(items);
    }

    public int getCantidadItems() {
        return items.size();
    }

    // Setters
    public void setCodigoOrden(String codigoOrden) {
        this.codigoOrden = codigoOrden;
    }

    public void setTipoSalida(String tipoSalida) {
        this.tipoSalida = tipoSalida;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public void setFechaOrden(LocalDateTime fechaOrden) {
        this.fechaOrden = fechaOrden;
    }

    public void setFechaSalida(LocalDateTime fechaSalida) {
        this.fechaSalida = fechaSalida;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void setUsuarioCreador(String usuarioCreador) {
        this.usuarioCreador = usuarioCreador;
    }

    public void setRequiereAprobacion(boolean requiereAprobacion) {
        this.requiereAprobacion = requiereAprobacion;
    }

    // Método para obtener el siguiente ID que se generará
    public static String obtenerSiguienteID() {
        return "OS" + String.format("%06d", contadorID);
    }

    @Override
    public String toString() {
        return String.format("OrdenSalida{codigo='%s', tipo='%s', destino='%s', fecha=%s, " +
                "estado='%s', items=%d, total=S/%.2f}",
                codigoOrden,
                tipoSalida,
                destino != null ? destino : "N/A",
                fechaOrden.toLocalDate(),
                estado,
                items.size(),
                montoTotal);
    }
}