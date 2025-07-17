package proyecto_poo;

import java.util.ArrayList;
import java.util.List;

public class Inventario {
    private List<Producto> productos;
    private List<Proveedor> proveedores;
    private List<OrdenDeEntrada> ordenesDeCompra;
    private List<OrdenDeSalida> ordenesDeSalida;
    
    // Constructor
    public Inventario() {
        this.productos = new ArrayList<>();
        this.proveedores = new ArrayList<>();
        this.ordenesDeCompra = new ArrayList<>();
        this.ordenesDeSalida = new ArrayList<>();
    }
    
    // Métodos para agregar
    public void agregarProducto(Producto producto) {
        productos.add(producto);
    }
    
    public void agregarProveedor(Proveedor proveedor) {
        proveedores.add(proveedor);
    }
    
    public void agregarOrdenDeCompra(OrdenDeEntrada orden) {
        ordenesDeCompra.add(orden);
        // Actualizar cantidad en inventario
        for (Producto p : productos) {
            if (p.getNombre().equals(orden.getProducto().getNombre())) {
                p.setCantidad(p.getCantidad() + orden.getCantidad());
                break;
            }
        }
    }
    
    public void agregarOrdenDeSalida(OrdenDeSalida orden) {
        ordenesDeSalida.add(orden);
        // Actualizar cantidad en inventario
        for (Producto p : productos) {
            if (p.getNombre().equals(orden.getProducto().getNombre())) {
                p.setCantidad(p.getCantidad() - orden.getCantidad());
                break;
            }
        }
    }
    
    // Método para eliminar producto (ahora con base de datos)
    public void eliminarProducto(String nombre) {
        if (ConexionBD.eliminarProducto(nombre)) {
            productos.removeIf(p -> p.getNombre().equals(nombre));
            System.out.println("Producto eliminado exitosamente de la base de datos.");
        } else {
            System.out.println("Error al eliminar producto de la base de datos.");
        }
    }
    
    // Método para actualizar cantidad
    public void actualizarCantidad(String nombre, int nuevaCantidad) {
        for (Producto p : productos) {
            if (p.getNombre().equals(nombre)) {
                p.setCantidad(nuevaCantidad);
                break;
            }
        }
    }
    
    // Método para buscar producto
    public Producto buscarProducto(String nombre) {
        for (Producto p : productos) {
            if (p.getNombre().equals(nombre)) {
                return p;
            }
        }
        return null;
    }
    
    // Método para mostrar inventario (ahora desde base de datos)
    public void mostrarInventario() {
        System.out.println("=== INVENTARIO (DESDE BASE DE DATOS) ===");
        List<Producto> productosDB = ConexionBD.obtenerProductos();
        for (Producto p : productosDB) {
            System.out.println(p);
        }
    }
    
    // Método para mostrar proveedores
    public void mostrarProveedores() {
        System.out.println("=== PROVEEDORES ===");
        for (Proveedor p : proveedores) {
            System.out.println(p);
        }
    }
    
    // Método para mostrar órdenes de compra
    public void mostrarOrdenesDeCompra() {
        System.out.println("=== ÓRDENES DE COMPRA ===");
        for (OrdenDeEntrada orden : ordenesDeCompra) {
            System.out.println(orden);
        }
    }
    
    // Método para mostrar órdenes de salida
    public void mostrarOrdenesDeSalida() {
        System.out.println("=== ÓRDENES DE SALIDA ===");
        for (OrdenDeSalida orden : ordenesDeSalida) {
            System.out.println(orden);
        }
    }
    
    // Reportes
    public void reporteProductosMayorCantidad() {
        System.out.println("=== REPORTE: PRODUCTOS MAYOR CANTIDAD ===");
        productos.stream()
                .sorted((p1, p2) -> Integer.compare(p2.getCantidad(), p1.getCantidad()))
                .forEach(System.out::println);
    }
    
    public void reporteProductosMayorPrecio() {
        System.out.println("=== REPORTE: PRODUCTOS MAYOR PRECIO ===");
        productos.stream()
                .sorted((p1, p2) -> Double.compare(p2.getPrecio(), p1.getPrecio()))
                .forEach(System.out::println);
    }
    
    public void reporteProductosMayorOrdenCompra() {
        System.out.println("=== REPORTE: PRODUCTOS MAYOR ORDEN COMPRA ===");
        ordenesDeCompra.stream()
                .sorted((o1, o2) -> Integer.compare(o2.getCantidad(), o1.getCantidad()))
                .forEach(System.out::println);
    }
    
    // Método para obtener cantidad de orden de compra por producto
    public int getCantidadOrdenCompra(Producto producto) {
        return ordenesDeCompra.stream()
                .filter(orden -> orden.getProducto().getNombre().equals(producto.getNombre()))
                .mapToInt(OrdenDeEntrada::getCantidad)
                .sum();
    }
    
    // Getters
    public List<Producto> getProductos() {
        return productos;
    }
    
    public List<Proveedor> getProveedores() {
        return proveedores;
    }
    
    public List<OrdenDeEntrada> getOrdenesDeCompra() {
        return ordenesDeCompra;
    }
    
    public List<OrdenDeSalida> getOrdenesDeSalida() {
        return ordenesDeSalida;
    }
}