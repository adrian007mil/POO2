package com.inventario.modelo;

public class Cliente {
    private int id; // ID único que se registra en BD
    private String codigo;
    private String nombre;
    private static int contadorID = 1;

    // Constructor vacío
    public Cliente() {
        this.id = contadorID++;
        this.codigo = generarCodigo();
    }

    // Constructor con parámetros
    public Cliente(String nombre) {
        this();
        this.nombre = nombre;
    }

    // Constructor completo
    public Cliente(String codigo, String nombre) {
        this.id = contadorID++;
        this.codigo = codigo;
        this.nombre = nombre;
    }

    // Método para generar código automáticamente
    private String generarCodigo() {
        return "CLI" + String.format("%04d", this.id);
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    // Método para obtener el siguiente ID que se generará
    public static int obtenerSiguienteID() {
        return contadorID;
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "id=" + id +
                ", codigo='" + codigo + '\'' +
                ", nombre='" + nombre + '\'' +
                '}';
    }
}
