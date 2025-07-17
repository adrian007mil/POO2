
package proyecto_poo;

public class OrdenDeEntrada {
    private Producto producto;
    private int cantidad;
    private Proveedor proveedor;
    
    // Constructor
    public OrdenDeEntrada() {
    }
    
    public OrdenDeEntrada(Producto producto, int cantidad, Proveedor proveedor) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.proveedor = proveedor;
    }
    
    // Getters
    public Producto getProducto() {
        return producto;
    }
    
    public int getCantidad() {
        return cantidad;
    }
    
    public Proveedor getProveedor() {
        return proveedor;
    }
    
    // Setters
    public void setProducto(Producto producto) {
        this.producto = producto;
    }
    
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
    
    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }
    
    @Override
    public String toString() {
        return "OrdenDeEntrada{" +
                "producto=" + producto +
                ", cantidad=" + cantidad +
                ", proveedor=" + proveedor +
                '}';
    }
}
