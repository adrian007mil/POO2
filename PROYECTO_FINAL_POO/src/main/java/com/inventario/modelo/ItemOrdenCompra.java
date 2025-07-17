package com.inventario.modelo;

public class ItemOrdenCompra {
    private int id; // ID único que se registra en BD
    private Producto producto;
    private int cantidad;
    private double precioUnitario;
    private double subtotal;

    // Constructor vacío
    public ItemOrdenCompra() {
        // El ID se asignará desde la base de datos
    } // Constructor con parámetros

    public ItemOrdenCompra(Producto producto, int cantidad, double precioUnitario) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = cantidad * precioUnitario;
    }

    // Constructor completo
    public ItemOrdenCompra(int id, Producto producto, int cantidad, double precioUnitario) {
        this.id = id;
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = cantidad * precioUnitario;
    }

    // Método para recalcular subtotal
    public void recalcularSubtotal() {
        this.subtotal = cantidad * precioUnitario;
    }

    // Getters
    public int getId() {
        return id;
    }

    public Producto getProducto() {
        return producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public double getSubtotal() {
        return subtotal;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
        recalcularSubtotal();
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
        recalcularSubtotal();
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    @Override
    public String toString() {
        return "ItemOrdenCompra{" +
                "id=" + id +
                ", producto=" + (producto != null ? producto.getNombre() : "Sin producto") +
                ", cantidad=" + cantidad +
                ", precioUnitario=" + precioUnitario +
                ", subtotal=" + subtotal +
                '}';
    }
}
