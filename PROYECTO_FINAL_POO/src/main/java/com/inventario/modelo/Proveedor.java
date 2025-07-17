package com.inventario.modelo;

import java.time.LocalDate;

public class Proveedor {
    private int id; // ID único que se obtiene de BD
    private String codigoProveedor;
    private String nombre;
    private String direccion;
    private String telefono;
    private String email;
    private String ruc;
    private boolean esActivo;
    private LocalDate fechaRegistro;
    private int totalOrdenes;
    private double montoTotalCompras;

    // Constructor vacío
    public Proveedor() {
        this.esActivo = true;
        this.fechaRegistro = LocalDate.now();
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
            String email, String ruc, boolean esActivo) {
        this.codigoProveedor = codigoProveedor;
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
        this.email = email;
        this.ruc = ruc;
        this.esActivo = esActivo;
        this.fechaRegistro = LocalDate.now();
        this.totalOrdenes = 0;
        this.montoTotalCompras = 0.0;
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

    // Getters
    public int getId() {
        return id;
    }

    public String getCodigoProveedor() {
        return codigoProveedor;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getEmail() {
        return email;
    }

    public String getRuc() {
        return ruc;
    }

    public boolean isEsActivo() {
        return esActivo;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    public int getTotalOrdenes() {
        return totalOrdenes;
    }

    public double getMontoTotalCompras() {
        return montoTotalCompras;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setCodigoProveedor(String codigoProveedor) {
        this.codigoProveedor = codigoProveedor;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public void setEsActivo(boolean esActivo) {
        this.esActivo = esActivo;
    }

    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public void setTotalOrdenes(int totalOrdenes) {
        this.totalOrdenes = totalOrdenes;
    }

    public void setMontoTotalCompras(double montoTotalCompras) {
        this.montoTotalCompras = montoTotalCompras;
    }

    @Override
    public String toString() {
        return String.format("Proveedor{id=%d, codigo='%s', nombre='%s', ruc='%s', " +
                "ordenes=%d, montoTotal=S/%.2f}",
                id, codigoProveedor, nombre, ruc, totalOrdenes, montoTotalCompras);
    }
}
