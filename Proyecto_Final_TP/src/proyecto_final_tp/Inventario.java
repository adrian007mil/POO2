
package proyecto_final_tp;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;

public class Inventario {
    private List<Producto> productos;
    private List<Proveedor> proveedores;
    private List<OrdenDeCompra> ordenesDeCompra;
    private List<OrdenDeSalida> ordenesDeSalida;

    public Inventario() {
        this.productos = new ArrayList<>();
        this.proveedores = new ArrayList<>();
        this.ordenesDeCompra = new ArrayList<>();
        this.ordenesDeSalida = new ArrayList<>();
    }

    // Métodos para manejar productos
    public void agregarProducto(Producto producto) {
        productos.add(producto);
    }

    public void eliminarProducto(String nombre) {
        productos.removeIf(producto -> producto.getNombre().equalsIgnoreCase(nombre));
    }

    public void actualizarCantidad(String nombre, int nuevaCantidad) {
        for (Producto producto : productos) {
            if (producto.getNombre().equalsIgnoreCase(nombre)) {
                producto.setCantidad(nuevaCantidad);
                break;
            }
        }
    }

    public void mostrarInventario() {
        System.out.println("Inventario:");
        for (Producto producto : productos) {
            System.out.println(producto);
        }
    }

    public Producto buscarProducto(String nombre) {
        for (Producto producto : productos) {
            if (producto.getNombre().equalsIgnoreCase(nombre)) {
                return producto;
            }
        }
        return null;
    }

    // Métodos para manejar proveedores
    public void agregarProveedor(Proveedor proveedor) {
        proveedores.add(proveedor);
    }

    public void mostrarProveedores() {
        System.out.println("Proveedores:");
        for (Proveedor proveedor : proveedores) {
            System.out.println(proveedor);
        }
    }

    // Métodos para manejar Órdenes de compra
    public void agregarOrdenDeCompra(OrdenDeCompra orden) {
        ordenesDeCompra.add(orden);
    }

    public void mostrarOrdenesDeCompra() {
        System.out.println("Órdenes de Compra:");
        for (OrdenDeCompra orden : ordenesDeCompra) {
            System.out.println(orden);
        }
    }

    // Métodos para manejar Órdenes de salida
    public void agregarOrdenDeSalida(OrdenDeSalida orden) {
        ordenesDeSalida.add(orden);
        actualizarCantidad(orden.getProducto().getNombre(), orden.getProducto().getCantidad() - orden.getCantidad());
    }
     public void mostrarOrdenesDeSalida() {
        System.out.println("Órdenes de Salida:");
        for (OrdenDeSalida orden : ordenesDeSalida) {
            System.out.println(orden);
        }
    }

    // Métodos para reportes
    public void reporteProductosMayorCantidad() {
        productos.stream()
                .sorted(Comparator.comparingInt(Producto::getCantidad).reversed())
                .forEach(System.out::println);
    }

    public void reporteProductosMayorPrecio() {
        productos.stream()
                .sorted(Comparator.comparingDouble(Producto::getPrecio).reversed())
                .forEach(System.out::println);
    }

    public void reporteProductosMayorOrdenCompra() {
        productos.stream()
                .sorted(Comparator.comparingInt(p -> getCantidadOrdenCompra((Producto) p)).reversed())
                .forEach(System.out::println);
    }

    private int getCantidadOrdenCompra(Producto producto) {
        return (int) ordenesDeCompra.stream()
                .filter(orden -> orden.getProducto().getNombre().equalsIgnoreCase(producto.getNombre()))
                .mapToInt(OrdenDeCompra::getCantidad)
                .sum();
    }

    
}
