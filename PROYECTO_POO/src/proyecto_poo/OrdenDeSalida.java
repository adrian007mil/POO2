
package proyecto_poo;

import java.time.LocalDate;

public class OrdenDeSalida {
    private Producto producto;
    private int cantidad;
    private LocalDate fecha;
    
    // Constructor
    public OrdenDeSalida() {
    }
    
    public OrdenDeSalida(Producto producto, int cantidad, LocalDate fecha) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.fecha = fecha;
    }
    
    // Getters
    public Producto getProducto() {
        return producto;
    }
    
    public int getCantidad() {
        return cantidad;
    }
    
    public LocalDate getFecha() {
        return fecha;
    }
    
    // Setters
    public void setProducto(Producto producto) {
        this.producto = producto;
    }
    
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
    
    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }
    
    @Override
    public String toString() {
        return "OrdenDeSalida{" +
                "producto=" + producto +
                ", cantidad=" + cantidad +
                ", fecha=" + fecha +
                '}';
    }
}