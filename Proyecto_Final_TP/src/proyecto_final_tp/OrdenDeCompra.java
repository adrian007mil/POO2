
package proyecto_final_tp;


public class OrdenDeCompra {
    
    
    private Producto producto;
    private int cantidad;
    private Proveedor proveedor;

    public OrdenDeCompra(Producto producto, int cantidad, Proveedor proveedor) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.proveedor = proveedor;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    @Override
    public String toString() {
        return "OrdenDeCompra{" +
                "producto=" + producto +
                ", cantidad=" + cantidad +
                ", proveedor=" + proveedor +
                '}';
    }
}
