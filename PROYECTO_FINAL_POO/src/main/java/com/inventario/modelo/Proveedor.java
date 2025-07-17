
package com.inventario.modelo;

import java.time.LocalDate;

public class Proveedor {
    private String codigoProveedor;
    private String nombre;
    private String direccion;
    private String telefono;
    private String email;
    private String ruc;
    private String contactoPrincipal;
    private double calificacion; // De 1 a 5
    private boolean esActivo;
    private LocalDate fechaRegistro;
    private int totalOrdenes;
    private double montoTotalCompras;
    private static int contadorID = 1;
    
    // Constructor vacío
    public Proveedor() {
        this.codigoProveedor = generarCodigoProveedor();
        this.esActivo = true;
        this.fechaRegistro = LocalDate.now();
        this.calificacion = 5.0; // Calificación inicial perfecta
        this.totalOrdenes = 0;
        this.montoTotalCompras = 0.0;
    }
    
    // Constructor con parámetros principales
    public Proveedor(String nombre, String direccion, String telefono, String email, String ruc) {
        this();
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
        this.email = email;
        this.ruc = ruc;
    }
    
    // Constructor completo
    public Proveedor(String codigoProveedor, String nombre, String direccion, String telefono, 
                    String email, String ruc, String contactoPrincipal, double calificacion, 
                    boolean esActivo) {
        this.codigoProveedor = codigoProveedor;
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
        this.email = email;
        this.ruc = ruc;
        this.contactoPrincipal = contactoPrincipal;
        this.calificacion = calificacion;
        this.esActivo = esActivo;
        this.fechaRegistro = LocalDate.now();
        this.totalOrdenes = 0;
        this.montoTotalCompras = 0.0;
    }
    
    // Método para generar código automáticamente
    private String generarCodigoProveedor() {
        return "PROV" + String.format("%04d", contadorID++);
    }
    
    // Método para actualizar estadísticas del proveedor
    public void actualizarEstadisticas(double montoOrden) {
        this.totalOrdenes++;
        this.montoTotalCompras += montoOrden;
    }
    
    // Método para calcular promedio de compra
    public double getPromedioCompra() {
        return totalOrdenes > 0 ? montoTotalCompras / totalOrdenes : 0.0;
    }
    
    // Método para obtener clasificación del proveedor
    public String getClasificacion() {
        if (calificacion >= 4.5) return "EXCELENTE";
        if (calificacion >= 4.0) return "BUENO";
        if (calificacion >= 3.0) return "REGULAR";
        return "DEFICIENTE";
    }
    
    // Getters
    public String getCodigoProveedor() { return codigoProveedor; }
    public String getNombre() { return nombre; }
    public String getDireccion() { return direccion; }
    public String getTelefono() { return telefono; }
    public String getEmail() { return email; }
    public String getRuc() { return ruc; }
    public String getContactoPrincipal() { return contactoPrincipal; }
    public double getCalificacion() { return calificacion; }
    public boolean isEsActivo() { return esActivo; }
    public LocalDate getFechaRegistro() { return fechaRegistro; }
    public int getTotalOrdenes() { return totalOrdenes; }
    public double getMontoTotalCompras() { return montoTotalCompras; }
    
    // Setters
    public void setCodigoProveedor(String codigoProveedor) { this.codigoProveedor = codigoProveedor; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public void setEmail(String email) { this.email = email; }
    public void setRuc(String ruc) { this.ruc = ruc; }
    public void setContactoPrincipal(String contactoPrincipal) { this.contactoPrincipal = contactoPrincipal; }
    public void setCalificacion(double calificacion) { this.calificacion = Math.max(1.0, Math.min(5.0, calificacion)); }
    public void setEsActivo(boolean esActivo) { this.esActivo = esActivo; }
    public void setFechaRegistro(LocalDate fechaRegistro) { this.fechaRegistro = fechaRegistro; }
    public void setTotalOrdenes(int totalOrdenes) { this.totalOrdenes = totalOrdenes; }
    public void setMontoTotalCompras(double montoTotalCompras) { this.montoTotalCompras = montoTotalCompras; }
    
    // Método para obtener el siguiente ID que se generará
    public static String obtenerSiguienteID() {
        return "PROV" + String.format("%04d", contadorID);
    }
    
    @Override
    public String toString() {
        return String.format("Proveedor{codigo='%s', nombre='%s', ruc='%s', calificacion=%.1f, " +
                           "ordenes=%d, montoTotal=S/%.2f, clasificacion='%s'}",
                codigoProveedor, nombre, ruc, calificacion, totalOrdenes, 
                montoTotalCompras, getClasificacion());
    }
}
