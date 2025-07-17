
package proyecto_final_tp;
import java.time.LocalDate;

public class OrdenDeSalida {
    private Producto producto;
    private int cantidad;
    private LocalDate fecha;

    public OrdenDeSalida(Producto producto, int cantidad, LocalDate fecha) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.fecha = fecha;
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

    public LocalDate getFecha() {
        return fecha;
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
