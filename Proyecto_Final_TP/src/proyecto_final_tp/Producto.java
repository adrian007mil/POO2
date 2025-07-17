
package proyecto_final_tp;


public class Producto {
    private String nombre;
    private double precio;
    private int cantidad;
    private String codigoProducto;
    private boolean esActivo; // sirve para ver qué productos tienes activos

    public Producto(String nombre, double precio, int cantidad, Proveedor proveedor, String codigoProducto) {
        this.nombre = nombre;
        this.precio = precio;
        this.cantidad = cantidad;
        this.codigoProducto = codigoProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getCodigoProducto() {
        return codigoProducto;
    }

    public void setCodigoProducto(String codigoProducto) {
        this.codigoProducto = codigoProducto;
    }

    public boolean isEsActivo() {
        return esActivo;
    }

    public void setEsActivo(boolean esActivo) {
        this.esActivo = esActivo;
    }   
    
    @Override
    public String toString() {
        return "Producto{" +
                "nombre='" + nombre + '\'' +
                ", precio=" + precio +
                ", cantidad=" + cantidad +
                ", codigo=" + codigoProducto +
                '}';
    }
    //Sobreescribe el metodo reservado toString.
}