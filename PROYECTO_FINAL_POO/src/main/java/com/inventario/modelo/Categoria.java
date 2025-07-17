package com.inventario.modelo;

public class Categoria {
    private int id; // ID único que se registra en BD
    private String codigo;
    private String nombre;
    private String descripcion;
    private boolean esActivo;
    private static int contadorID = 1;

    // Constructor vacío
    public Categoria() {
        this.id = contadorID++;
        this.codigo = generarCodigo();
        this.esActivo = true;
    }

    // Constructor con parámetros
    public Categoria(String nombre, String descripcion) {
        this();
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    // Constructor completo
    public Categoria(String codigo, String nombre, String descripcion, boolean esActivo) {
        this.id = contadorID++;
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.esActivo = esActivo;
    }

    // Método para generar código automáticamente
    private String generarCodigo() {
        return "CAT" + String.format("%04d", this.id);
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

    public String getDescripcion() {
        return descripcion;
    }

    public boolean isEsActivo() {
        return esActivo;
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

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setEsActivo(boolean esActivo) {
        this.esActivo = esActivo;
    }

    // Método para obtener el siguiente ID que se generará
    public static int obtenerSiguienteID() {
        return contadorID;
    }

    @Override
    public String toString() {
        return "Categoria{" +
                "id=" + id +
                ", codigo='" + codigo + '\'' +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", activo=" + esActivo +
                '}';
    }
}
