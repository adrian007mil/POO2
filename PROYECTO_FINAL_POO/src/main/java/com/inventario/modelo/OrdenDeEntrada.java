package com.inventario.modelo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrdenDeEntrada {
    private String codigoOrden;
    private Proveedor proveedor;
    private LocalDateTime fechaOrden;
    private LocalDateTime fechaEntrega;
    private String estado; // PENDIENTE, APROBADA, RECIBIDA, CANCELADA
    private String observaciones;
    private double montoTotal;
    private double montoIGV;
    private double montoFinal;
    private String usuarioCreador;
    private boolean esUrgente;
    private List<DetalleOrdenEntrada> detalles;
    private static int contadorID = 1;
    
    // Constructor vacío
    public OrdenDeEntrada() {
        this.codigoOrden = generarCodigoOrden();
        this.fechaOrden = LocalDateTime.now();
        this.estado = "PENDIENTE";
        this.detalles = new ArrayList<>();
        this.montoTotal = 0.0;
        this.montoIGV = 0.0;
        this.montoFinal = 0.0;
        this.esUrgente = false;
    }
    
    // Constructor con proveedor
    public OrdenDeEntrada(Proveedor proveedor) {
        this();
        this.proveedor = proveedor;
    }
    
    // Constructor completo
    public OrdenDeEntrada(String codigoOrden, Proveedor proveedor, LocalDateTime fechaOrden,
                         String estado, String observaciones, String usuarioCreador) {
        this.codigoOrden = codigoOrden;
        this.proveedor = proveedor;
        this.fechaOrden = fechaOrden;
        this.estado = estado;
        this.observaciones = observaciones;
        this.usuarioCreador = usuarioCreador;
        this.detalles = new ArrayList<>();
        this.esUrgente = false;
        calcularTotales();
    }
    
    // Método para generar código automáticamente
    private String generarCodigoOrden() {
        return "OE" + String.format("%06d", contadorID++);
    }
    
    // Método para agregar detalle a la orden
    public void agregarDetalle(Producto producto, int cantidad, double precioUnitario) {
        // Verificar si el producto ya está en la orden
        for (DetalleOrdenEntrada detalle : detalles) {
            if (detalle.getProducto().getCodigoProducto().equals(producto.getCodigoProducto())) {
                // Si ya existe, actualizar cantidad
                detalle.setCantidad(detalle.getCantidad() + cantidad);
                detalle.calcularSubtotal();
                calcularTotales();
                return;
            }
        }
        
        // Si no existe, crear nuevo detalle
        DetalleOrdenEntrada nuevoDetalle = new DetalleOrdenEntrada(producto, cantidad, precioUnitario);
        detalles.add(nuevoDetalle);
        calcularTotales();
    }
    
    // Método para remover detalle
    public boolean removerDetalle(String codigoProducto) {
        boolean removido = detalles.removeIf(detalle -> 
            detalle.getProducto().getCodigoProducto().equals(codigoProducto));
        if (removido) {
            calcularTotales();
        }
        return removido;
    }
    
    // Método para calcular totales
    public void calcularTotales() {
        this.montoTotal = detalles.stream()
                .mapToDouble(DetalleOrdenEntrada::getSubtotal)
                .sum();
        this.montoIGV = montoTotal * 0.18; // IGV 18%
        this.montoFinal = montoTotal + montoIGV;
    }
    
    // Método para aprobar orden
    public boolean aprobarOrden(String usuarioAprobador) {
        if ("PENDIENTE".equals(this.estado)) {
            this.estado = "APROBADA";
            this.observaciones += "\nAprobada por: " + usuarioAprobador + " el " + LocalDateTime.now();
            return true;
        }
        return false;
    }
    
    // Método para recibir orden (actualiza stock)
    public boolean recibirOrden(String usuarioRecepcion) {
        if ("APROBADA".equals(this.estado)) {
            this.estado = "RECIBIDA";
            this.fechaEntrega = LocalDateTime.now();
            this.observaciones += "\nRecibida por: " + usuarioRecepcion + " el " + fechaEntrega;
            
            // Actualizar stock de productos
            for (DetalleOrdenEntrada detalle : detalles) {
                Producto producto = detalle.getProducto();
                producto.setCantidad(producto.getCantidad() + detalle.getCantidad());
            }
            
            // Actualizar estadísticas del proveedor
            if (proveedor != null) {
                proveedor.actualizarEstadisticas(montoFinal);
            }
            
            return true;
        }
        return false;
    }
    
    // Método para cancelar orden
    public boolean cancelarOrden(String motivo, String usuarioCancelacion) {
        if (!"RECIBIDA".equals(this.estado)) {
            this.estado = "CANCELADA";
            this.observaciones += "\nCancelada por: " + usuarioCancelacion + 
                                " el " + LocalDateTime.now() + 
                                "\nMotivo: " + motivo;
            return true;
        }
        return false;
    }
    
    // Método para obtener resumen de la orden
    public String getResumen() {
        return String.format("Orden %s - %s - %d items - S/%.2f - %s",
                codigoOrden, 
                proveedor != null ? proveedor.getNombre() : "Sin proveedor",
                detalles.size(),
                montoFinal,
                estado);
    }
    
    // Getters
    public String getCodigoOrden() { return codigoOrden; }
    public Proveedor getProveedor() { return proveedor; }
    public LocalDateTime getFechaOrden() { return fechaOrden; }
    public LocalDateTime getFechaEntrega() { return fechaEntrega; }
    public String getEstado() { return estado; }
    public String getObservaciones() { return observaciones; }
    public double getMontoTotal() { return montoTotal; }
    public double getMontoIGV() { return montoIGV; }
    public double getMontoFinal() { return montoFinal; }
    public String getUsuarioCreador() { return usuarioCreador; }
    public boolean isEsUrgente() { return esUrgente; }
    public List<DetalleOrdenEntrada> getDetalles() { return new ArrayList<>(detalles); }
    public int getCantidadItems() { return detalles.size(); }
    
    // Setters
    public void setCodigoOrden(String codigoOrden) { this.codigoOrden = codigoOrden; }
    public void setProveedor(Proveedor proveedor) { this.proveedor = proveedor; }
    public void setFechaOrden(LocalDateTime fechaOrden) { this.fechaOrden = fechaOrden; }
    public void setFechaEntrega(LocalDateTime fechaEntrega) { this.fechaEntrega = fechaEntrega; }
    public void setEstado(String estado) { this.estado = estado; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public void setUsuarioCreador(String usuarioCreador) { this.usuarioCreador = usuarioCreador; }
    public void setEsUrgente(boolean esUrgente) { this.esUrgente = esUrgente; }
    
    // Método para obtener el siguiente ID que se generará
    public static String obtenerSiguienteID() {
        return "OE" + String.format("%06d", contadorID);
    }
    
    @Override
    public String toString() {
        return String.format("OrdenEntrada{codigo='%s', proveedor='%s', fecha=%s, " +
                           "estado='%s', items=%d, total=S/%.2f}",
                codigoOrden, 
                proveedor != null ? proveedor.getNombre() : "N/A",
                fechaOrden.toLocalDate(),
                estado,
                detalles.size(),
                montoFinal);
    }
    
    // Clase interna para detalles de orden
    public static class DetalleOrdenEntrada {
        private Producto producto;
        private int cantidad;
        private double precioUnitario;
        private double subtotal;
        private String observaciones;
        
        public DetalleOrdenEntrada(Producto producto, int cantidad, double precioUnitario) {
            this.producto = producto;
            this.cantidad = cantidad;
            this.precioUnitario = precioUnitario;
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
        public String getObservaciones() { return observaciones; }
        
        public void setProducto(Producto producto) { this.producto = producto; }
        public void setCantidad(int cantidad) { 
            this.cantidad = cantidad; 
            calcularSubtotal();
        }
        public void setPrecioUnitario(double precioUnitario) { 
            this.precioUnitario = precioUnitario; 
            calcularSubtotal();
        }
        public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
        
        @Override
        public String toString() {
            return String.format("DetalleProd{%s x%d @S/%.2f = S/%.2f}",
                    producto.getNombre(), cantidad, precioUnitario, subtotal);
        }
    }
}