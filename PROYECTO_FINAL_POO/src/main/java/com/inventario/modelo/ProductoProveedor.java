package com.inventario.modelo;

public class ProductoProveedor {
    private int id; // ID único que se obtiene de BD
    private Proveedor proveedor;
    private Producto producto;
    private double precioCompra; // Precio al que compramos al proveedor
    private int tiempoEntrega; // Días para entrega
    private boolean esPreferido; // Si es el proveedor preferido para este producto
    private boolean esActivo;

    // Constructor vacío
    public ProductoProveedor() {
        this.esActivo = true;
    }

    // Constructor con parámetros
    public ProductoProveedor(Proveedor proveedor, Producto producto, double precioCompra, int tiempoEntrega) {
        this();
        this.proveedor = proveedor;
        this.producto = producto;
        this.precioCompra = precioCompra;
        this.tiempoEntrega = tiempoEntrega;
        this.esPreferido = false; // Por defecto no es preferido
    }

    // Constructor completo
    public ProductoProveedor(Proveedor proveedor, Producto producto, double precioCompra,
            int tiempoEntrega, boolean esPreferido, boolean esActivo) {
        this.proveedor = proveedor;
        this.producto = producto;
        this.precioCompra = precioCompra;
        this.tiempoEntrega = tiempoEntrega;
        this.esPreferido = esPreferido;
        this.esActivo = esActivo;
    }

    // Método para calcular margen de ganancia
    public double calcularMargen() {
        if (producto != null && precioCompra > 0) {
            return ((producto.getPrecioVenta() - precioCompra) / precioCompra) * 100;
        }
        return 0.0;
    }

    // Método para verificar si es rentable
    public boolean esRentable(double margenMinimo) {
        return calcularMargen() >= margenMinimo;
    }

    // Getters
    public int getId() {
        return id;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public Producto getProducto() {
        return producto;
    }

    public double getPrecioCompra() {
        return precioCompra;
    }

    public int getTiempoEntrega() {
        return tiempoEntrega;
    }

    public boolean isEsPreferido() {
        return esPreferido;
    }

    public boolean isEsActivo() {
        return esActivo;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public void setPrecioCompra(double precioCompra) {
        this.precioCompra = precioCompra;
    }

    public void setTiempoEntrega(int tiempoEntrega) {
        this.tiempoEntrega = tiempoEntrega;
    }

    public void setEsPreferido(boolean esPreferido) {
        this.esPreferido = esPreferido;
    }

    public void setEsActivo(boolean esActivo) {
        this.esActivo = esActivo;
    }

    @Override
    public String toString() {
        return "ProductoProveedor{" +
                "id=" + id +
                ", proveedor=" + (proveedor != null ? proveedor.getNombre() : "Sin proveedor") +
                ", producto=" + (producto != null ? producto.getNombre() : "Sin producto") +
                ", precioCompra=" + precioCompra +
                ", tiempoEntrega=" + tiempoEntrega +
                ", esPreferido=" + esPreferido +
                ", margen=" + String.format("%.2f", calcularMargen()) + "%" +
                ", activo=" + esActivo +
                '}';
    }
}
