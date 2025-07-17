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
    private String observaciones;
    private double montoTotal;
    private String usuarioCreador;
    private String usuarioAprobador;
    private boolean requiereAprobacion;
    private List<DetalleSalida> detalles;
    private static int contadorID = 1;
    
    // Constructor vacío
    public OrdenDeSalida() {
        this.codigoOrden = generarCodigoOrden();
        this.fechaOrden = LocalDateTime.now();
        this.estado = "PENDIENTE";
        this.tipoSalida = "VENTA";
        this.detalles = new ArrayList<>();
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
        this.detalles = new ArrayList<>();
        this.requiereAprobacion = !"VENTA".equals(tipoSalida);
        calcularTotal();
    }
    
    // Método para generar código automáticamente
    private String generarCodigoOrden() {
        return "OS" + String.format("%06d", contadorID++);
    }
    
    // Método para agregar detalle a la orden
    public boolean agregarDetalle(Producto producto, int cantidad, String motivoSalida) {
        // Verificar stock disponible
        if (producto.getCantidad() < cantidad) {
            return false; // No hay suficiente stock
        }
        
        // Verificar si el producto ya está en la orden
        for (DetalleSalida detalle : detalles) {
            if (detalle.getProducto().getCodigoProducto().equals(producto.getCodigoProducto())) {
                // Verificar si la nueva cantidad total no excede el stock
                int nuevaCantidad = detalle.getCantidad() + cantidad;
                if (producto.getCantidad() < nuevaCantidad) {
                    return false;
                }
                detalle.setCantidad(nuevaCantidad);
                detalle.calcularSubtotal();
                calcularTotal();
                return true;
            }
        }
        
        // Si no existe, crear nuevo detalle
        DetalleSalida nuevoDetalle = new DetalleSalida(producto, cantidad, motivoSalida);
        detalles.add(nuevoDetalle);
        calcularTotal();
        return true;
    }
    
    // Método para remover detalle
    public boolean removerDetalle(String codigoProducto) {
        boolean removido = detalles.removeIf(detalle -> 
            detalle.getProducto().getCodigoProducto().equals(codigoProducto));
        if (removido) {
            calcularTotal();
        }
        return removido;
    }
    
    // Método para calcular total
    public void calcularTotal() {
        this.montoTotal = detalles.stream()
                .mapToDouble(DetalleSalida::getSubtotal)
                .sum();
    }
    
    // Método para aprobar orden
    public boolean aprobarOrden(String usuarioAprobador) {
        if ("PENDIENTE".equals(this.estado) && requiereAprobacion) {
            this.estado = "APROBADA";
            this.usuarioAprobador = usuarioAprobador;
            this.observaciones += "\nAprobada por: " + usuarioAprobador + " el " + LocalDateTime.now();
            return true;
        } else if (!requiereAprobacion && "PENDIENTE".equals(this.estado)) {
            // Para ventas que no requieren aprobación, pasar directo a aprobada
            this.estado = "APROBADA";
            this.usuarioAprobador = this.usuarioCreador;
            return true;
        }
        return false;
    }
    
    // Método para entregar orden (actualiza stock)
    public boolean entregarOrden(String usuarioEntrega) {
        if ("APROBADA".equals(this.estado)) {
            // Verificar stock disponible antes de procesar
            for (DetalleSalida detalle : detalles) {
                if (detalle.getProducto().getCantidad() < detalle.getCantidad()) {
                    return false; // Stock insuficiente
                }
            }
            
            // Actualizar stock de productos
            for (DetalleSalida detalle : detalles) {
                Producto producto = detalle.getProducto();
                producto.setCantidad(producto.getCantidad() - detalle.getCantidad());
            }
            
            this.estado = "ENTREGADA";
            this.fechaSalida = LocalDateTime.now();
            this.observaciones += "\nEntregada por: " + usuarioEntrega + " el " + fechaSalida;
            return true;
        }
        return false;
    }
    
    // Método para cancelar orden
    public boolean cancelarOrden(String motivo, String usuarioCancelacion) {
        if (!"ENTREGADA".equals(this.estado)) {
            this.estado = "CANCELADA";
            this.observaciones += "\nCancelada por: " + usuarioCancelacion + 
                                " el " + LocalDateTime.now() + 
                                "\nMotivo: " + motivo;
            return true;
        }
        return false;
    }
    
    // Método para verificar si necesita reposición urgente
    public List<Producto> getProductosStockBajo() {
        List<Producto> productosStockBajo = new ArrayList<>();
        for (DetalleSalida detalle : detalles) {
            Producto producto = detalle.getProducto();
            int stockRestante = producto.getCantidad() - detalle.getCantidad();
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
                detalles.size(),
                montoTotal,
                estado);
    }
    
    // Getters
    public String getCodigoOrden() { return codigoOrden; }
    public String getTipoSalida() { return tipoSalida; }
    public String getDestino() { return destino; }
    public LocalDateTime getFechaOrden() { return fechaOrden; }
    public LocalDateTime getFechaSalida() { return fechaSalida; }
    public String getEstado() { return estado; }
    public String getObservaciones() { return observaciones; }
    public double getMontoTotal() { return montoTotal; }
    public String getUsuarioCreador() { return usuarioCreador; }
    public String getUsuarioAprobador() { return usuarioAprobador; }
    public boolean isRequiereAprobacion() { return requiereAprobacion; }
    public List<DetalleSalida> getDetalles() { return new ArrayList<>(detalles); }
    public int getCantidadItems() { return detalles.size(); }
    
    // Setters
    public void setCodigoOrden(String codigoOrden) { this.codigoOrden = codigoOrden; }
    public void setTipoSalida(String tipoSalida) { this.tipoSalida = tipoSalida; }
    public void setDestino(String destino) { this.destino = destino; }
    public void setFechaOrden(LocalDateTime fechaOrden) { this.fechaOrden = fechaOrden; }
    public void setFechaSalida(LocalDateTime fechaSalida) { this.fechaSalida = fechaSalida; }
    public void setEstado(String estado) { this.estado = estado; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public void setUsuarioCreador(String usuarioCreador) { this.usuarioCreador = usuarioCreador; }
    public void setUsuarioAprobador(String usuarioAprobador) { this.usuarioAprobador = usuarioAprobador; }
    public void setRequiereAprobacion(boolean requiereAprobacion) { this.requiereAprobacion = requiereAprobacion; }
    
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
                detalles.size(),
                montoTotal);
    }
    
    // Clase interna para detalles de salida
    public static class DetalleSalida {
        private Producto producto;
        private int cantidad;
        private double precioUnitario;
        private double subtotal;
        private String motivoSalida;
        private LocalDateTime fechaDetalle;
        
        public DetalleSalida(Producto producto, int cantidad, String motivoSalida) {
            this.producto = producto;
            this.cantidad = cantidad;
            this.precioUnitario = producto.getPrecio();
            this.motivoSalida = motivoSalida;
            this.fechaDetalle = LocalDateTime.now();
            calcularSubtotal();
        }
        
        public void calcularSubtotal() {
            this.subtotal = cantidad * precioUnitario;
        }
        
        // Getters y Setters
        public Producto getProducto() { return producto; }
        public int getCantidad() { return cantidad; }
        public double getPrecioUnitario() { return precioUnitario; }
        public double getSubtotal() { return subtotal; }
        public String getMotivoSalida() { return motivoSalida; }
        public LocalDateTime getFechaDetalle() { return fechaDetalle; }
        
        public void setProducto(Producto producto) { this.producto = producto; }
        public void setCantidad(int cantidad) { 
            this.cantidad = cantidad; 
            calcularSubtotal();
        }
        public void setPrecioUnitario(double precioUnitario) { 
            this.precioUnitario = precioUnitario; 
            calcularSubtotal();
        }
        public void setMotivoSalida(String motivoSalida) { this.motivoSalida = motivoSalida; }
        public void setFechaDetalle(LocalDateTime fechaDetalle) { this.fechaDetalle = fechaDetalle; }
        
        @Override
        public String toString() {
            return String.format("DetalleSalida{%s x%d @S/%.2f = S/%.2f - %s}",
                    producto.getNombre(), cantidad, precioUnitario, subtotal, motivoSalida);
        }
    }
}