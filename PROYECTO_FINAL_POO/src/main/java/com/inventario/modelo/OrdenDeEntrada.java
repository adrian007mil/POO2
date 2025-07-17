package com.inventario.modelo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrdenDeEntrada {
    private int id; // ID único que se obtiene de BD
    private String codigoOrden;
    private Proveedor proveedor;
    private LocalDateTime fechaOrden;
    private LocalDateTime fechaEntrega;
    private EstadoOrden estado; // PENDIENTE, APROBADA, RECIBIDA, CANCELADA
    private double montoTotal;
    private String usuarioCreador;
    private List<ItemOrdenCompra> items;

    // Constructor vacío
    public OrdenDeEntrada() {
        this.fechaOrden = LocalDateTime.now();
        this.estado = EstadoOrden.PENDIENTE;
        this.items = new ArrayList<>();
        this.montoTotal = 0.0;
    }

    // Constructor con proveedor
    public OrdenDeEntrada(Proveedor proveedor) {
        this();
        this.proveedor = proveedor;
    }

    // Constructor completo
    public OrdenDeEntrada(String codigoOrden, Proveedor proveedor, LocalDateTime fechaOrden,
            EstadoOrden estado, String usuarioCreador) {
        this.codigoOrden = codigoOrden;
        this.proveedor = proveedor;
        this.fechaOrden = fechaOrden;
        this.estado = estado;
        this.usuarioCreador = usuarioCreador;
        this.items = new ArrayList<>();
        calcularTotales();
    }

    // Método para agregar item a la orden
    public void agregarItem(Producto producto, int cantidad, double precioUnitario) {
        // Verificar si el producto ya está en la orden
        for (ItemOrdenCompra item : items) {
            if (item.getProducto().getCodigoProducto().equals(producto.getCodigoProducto())) {
                // Si ya existe, actualizar cantidad
                item.setCantidad(item.getCantidad() + cantidad);
                calcularTotales();
                return;
            }
        }

        // Si no existe, crear nuevo item
        ItemOrdenCompra nuevoItem = new ItemOrdenCompra(producto, cantidad, precioUnitario);
        items.add(nuevoItem);
        calcularTotales();
    }

    // Método para remover item
    public boolean removerItem(String codigoProducto) {
        boolean removido = items.removeIf(item -> item.getProducto().getCodigoProducto().equals(codigoProducto));
        if (removido) {
            calcularTotales();
        }
        return removido;
    }

    // Método para calcular totales
    public void calcularTotales() {
        this.montoTotal = items.stream()
                .mapToDouble(ItemOrdenCompra::getSubtotal)
                .sum();
    }

    // Método para aprobar orden
    public boolean aprobarOrden(String usuarioAprobador) {
        if (EstadoOrden.PENDIENTE.equals(this.estado)) {
            this.estado = EstadoOrden.APROBADA;
            return true;
        }
        return false;
    }

    // Método para recibir orden (actualiza stock)
    public boolean recibirOrden(String usuarioRecepcion) {
        if (EstadoOrden.ENVIADO.equals(this.estado) || EstadoOrden.APROBADA.equals(this.estado)) {
            this.estado = EstadoOrden.RECIBIDA;
            this.fechaEntrega = LocalDateTime.now();

            // Actualizar stock de productos
            for (ItemOrdenCompra item : items) {
                Producto producto = item.getProducto();
                producto.setCantidad(producto.getCantidad() + item.getCantidad());
            }

            // Actualizar estadísticas del proveedor
            if (proveedor != null) {
                proveedor.actualizarEstadisticas(montoTotal);
            }

            return true;
        }
        return false;
    }

    // Método para cancelar orden
    public boolean cancelarOrden(String motivo, String usuarioCancelacion) {
        if (!EstadoOrden.RECIBIDA.equals(this.estado)) {
            this.estado = EstadoOrden.CANCELADA;
            return true;
        }
        return false;
    }

    // Método para enviar producto a proveedor (cambiar estado de PENDIENTE a
    // ENVIADO)
    public boolean enviarProductoAProveedor() {
        if (EstadoOrden.PENDIENTE.equals(this.estado)) {
            this.estado = EstadoOrden.ENVIADO;
            return true;
        }
        return false;
    }

    // Método para recibir producto específico con cantidad
    public boolean recibirProducto(Producto producto, int cantidadRecibida) {
        for (ItemOrdenCompra item : items) {
            if (item.getProducto().getCodigoProducto().equals(producto.getCodigoProducto())) {
                if (cantidadRecibida <= item.getCantidad()) {
                    // Actualizar stock del producto
                    producto.actualizarStock(cantidadRecibida);

                    // Aquí podrías implementar lógica adicional para manejar cantidades parciales
                    // Por ejemplo, añadir un campo cantidadRecibida en ItemOrdenCompra

                    return true;
                } else {
                    System.out.println("❌ Cantidad recibida excede la cantidad ordenada");
                    return false;
                }
            }
        }
        return false;
    }

    // Método para verificar si la orden está completa
    public boolean estaCompleta() {
        // Por simplicidad, asumimos que está completa si el estado es RECIBIDA
        // En una implementación más compleja, verificaríamos si todos los productos
        // han sido recibidos en las cantidades correctas
        return EstadoOrden.RECIBIDA.equals(this.estado);
    }

    // Método para establecer fecha de recepción
    public void setFechaRecepcion(LocalDateTime fechaRecepcion) {
        this.fechaEntrega = fechaRecepcion;
    }

    // Método para obtener resumen de la orden
    public String getResumen() {
        return String.format("Orden %s - %s - %d items - S/%.2f - %s",
                codigoOrden,
                proveedor != null ? proveedor.getNombre() : "Sin proveedor",
                items.size(),
                montoTotal,
                estado.getDescripcion());
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getCodigoOrden() {
        return codigoOrden;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public LocalDateTime getFechaOrden() {
        return fechaOrden;
    }

    public LocalDateTime getFechaEntrega() {
        return fechaEntrega;
    }

    public EstadoOrden getEstado() {
        return estado;
    }

    public double getMontoTotal() {
        return montoTotal;
    }

    public String getUsuarioCreador() {
        return usuarioCreador;
    }

    public List<ItemOrdenCompra> getItems() {
        return new ArrayList<>(items);
    }

    public int getCantidadItems() {
        return items.size();
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setCodigoOrden(String codigoOrden) {
        this.codigoOrden = codigoOrden;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    public void setFechaOrden(LocalDateTime fechaOrden) {
        this.fechaOrden = fechaOrden;
    }

    public void setFechaEntrega(LocalDateTime fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public void setEstado(EstadoOrden estado) {
        this.estado = estado;
    }

    public void setUsuarioCreador(String usuarioCreador) {
        this.usuarioCreador = usuarioCreador;
    }

    @Override
    public String toString() {
        return String.format("OrdenEntrada{codigo='%s', proveedor='%s', fecha=%s, " +
                "estado='%s', items=%d, total=S/%.2f}",
                codigoOrden,
                proveedor != null ? proveedor.getNombre() : "N/A",
                fechaOrden.toLocalDate(),
                estado.getDescripcion(),
                items.size(),
                montoTotal);
    }
}