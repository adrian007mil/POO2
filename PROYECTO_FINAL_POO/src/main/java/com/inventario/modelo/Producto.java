package com.inventario.modelo;

public class Producto {
    private int id; // ID único que se obtiene de BD
    private String codigoProducto;
    private String nombre;
    private String descripcion;
    private double precioVenta; // Precio al que vendemos a clientes
    private int cantidadDisponible;
    private int cantidadMinima; // Para alertas de reorden
    private boolean esActivo;
    private Categoria categoria; // Nueva clase para categorizar productos

    // Constructor vacío
    public Producto() {
        this.esActivo = true; // Por defecto siempre activo
        this.cantidadMinima = 5; // Cantidad mínima por defecto
    }

    // Constructor con parámetros básicos
    public Producto(String nombre, double precioVenta, int cantidadDisponible) {
        this();
        this.nombre = nombre;
        this.precioVenta = precioVenta;
        this.cantidadDisponible = cantidadDisponible;
    }

    // Constructor completo
    public Producto(String codigoProducto, String nombre, String descripcion, double precioVenta,
            int cantidadDisponible, int cantidadMinima, boolean esActivo, Categoria categoria) {
        this.codigoProducto = codigoProducto;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precioVenta = precioVenta;
        this.cantidadDisponible = cantidadDisponible;
        this.cantidadMinima = cantidadMinima;
        this.esActivo = esActivo;
        this.categoria = categoria;
    }

    // Método para verificar si necesita reorden
    public boolean necesitaReorden() {
        return cantidadDisponible <= cantidadMinima;
    }

    // Método para actualizar stock
    public void actualizarStock(int cantidad) {
        this.cantidadDisponible += cantidad;
    }

    // Método para reducir stock (para ventas)
    public boolean reducirStock(int cantidad) {
        if (cantidadDisponible >= cantidad) {
            cantidadDisponible -= cantidad;
            return true;
        }
        return false;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getCodigoProducto() {
        return codigoProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public double getPrecioVenta() {
        return precioVenta;
    }

    public int getCantidadDisponible() {
        return cantidadDisponible;
    }

    public int getCantidadMinima() {
        return cantidadMinima;
    }

    public boolean isEsActivo() {
        return esActivo;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    // Getters de compatibilidad con código existente
    public double getPrecio() {
        return precioVenta;
    }

    public int getCantidad() {
        return cantidadDisponible;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setCodigoProducto(String codigoProducto) {
        this.codigoProducto = codigoProducto;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setPrecioVenta(double precioVenta) {
        this.precioVenta = precioVenta;
    }

    public void setCantidadDisponible(int cantidadDisponible) {
        this.cantidadDisponible = cantidadDisponible;
    }

    public void setCantidadMinima(int cantidadMinima) {
        this.cantidadMinima = cantidadMinima;
    }

    public void setEsActivo(boolean esActivo) {
        this.esActivo = esActivo;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    // Setters de compatibilidad con código existente
    public void setPrecio(double precio) {
        this.precioVenta = precio;
    }

    public void setCantidad(int cantidad) {
        this.cantidadDisponible = cantidad;
    }

    @Override
    public String toString() {
        return "Producto{" +
                "id=" + id +
                ", codigo='" + codigoProducto + '\'' +
                ", nombre='" + nombre + '\'' +
                ", precio=" + precioVenta +
                ", cantidad=" + cantidadDisponible +
                ", minima=" + cantidadMinima +
                ", activo=" + esActivo +
                ", categoria=" + (categoria != null ? categoria.getNombre() : "Sin categoría") +
                '}';
    }
}
