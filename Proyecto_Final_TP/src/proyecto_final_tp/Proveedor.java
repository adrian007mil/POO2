
package proyecto_final_tp;

import java.util.List;

public class Proveedor {
    private int idproveedor;   
    private String nombre;
    private String direccion;
    private String telefono;
    private List<ProveedorProducto> productos;

    public Proveedor(int idproveedor, String nombre, String direccion, String telefono) {
        this.idproveedor = idproveedor;
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public int getIdproveedor() {
        return idproveedor;
    }

    public void setIdproveedor(int idproveedor) {
        this.idproveedor = idproveedor;
    }

    public List<ProveedorProducto> getProductos() {
        return productos;
    }

    public void setProductos(List<ProveedorProducto> productos) {
        this.productos = productos;
    }
    
    
    @Override
    public String toString() {
        return "Proveedor{" +
                "nombre='" + nombre + '\'' +
                ", direccion='" + direccion + '\'' +
                ", telefono='" + telefono + '\'' +
                '}';
    }
}
