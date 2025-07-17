
package proyecto_poo;

import java.time.LocalDate;
import java.util.Scanner;

public class SistemaInventario {
    private static Inventario inventario = new Inventario();
    private static Scanner scanner = new Scanner(System.in);
    
    public static void main(String[] args) {
        int opcion;
        
        do {
            mostrarMenu();
            opcion = scanner.nextInt();
            scanner.nextLine(); // Limpiar buffer
            
            switch (opcion) {
                case 1:
                    agregarProducto();
                    break;
                case 2:
                    eliminarProducto();
                    break;
                case 3:
                    inventario.mostrarInventario();
                    break;
                case 4:
                    agregarProveedor();
                    break;
                case 5:
                    inventario.mostrarProveedores();
                    break;
                case 6:
                    agregarOrdenDeCompra();
                    break;
                case 7:
                    inventario.mostrarOrdenesDeCompra();
                    break;
                case 8:
                    agregarOrdenDeSalida();
                    break;
                case 9:
                    inventario.mostrarOrdenesDeSalida();
                    break;
                case 10:
                    inventario.reporteProductosMayorCantidad();
                    break;
                case 11:
                    inventario.reporteProductosMayorPrecio();
                    break;
                case 12:
                    inventario.reporteProductosMayorOrdenCompra();
                    break;
                case 13:
                    System.out.println("Saliendo del sistema...");
                    break;
                default:
                    System.out.println("Opción no válida. Intente nuevamente.");
            }
            
            if (opcion != 13) {
                System.out.println("\nPresione Enter para continuar...");
                scanner.nextLine();
            }
            
        } while (opcion != 13);
        
        scanner.close();
    }
    
    private static void mostrarMenu() {
        System.out.println("\n=== PROYECTO FINAL TP (run) ===");
        System.out.println("run:");
        System.out.println("1. Agregar Producto");
        System.out.println("2. Eliminar Producto");
        System.out.println("3. Mostrar Inventario");
        System.out.println("4. Agregar Proveedor");
        System.out.println("5. Mostrar Proveedores");
        System.out.println("6. Agregar Orden de Compra");
        System.out.println("7. Mostrar Órdenes de Compra");
        System.out.println("8. Agregar Orden de Salida");
        System.out.println("9. Mostrar Órdenes de Salida");
        System.out.println("10. Reporte Productos Mayor Cantidad");
        System.out.println("11. Reporte Productos Mayor Precio");
        System.out.println("12. Reporte Productos Mayor Orden Compra");
        System.out.println("13. Salir");
        System.out.print("Seleccione una opción: ");
    }
    
    private static void agregarProducto() {
        System.out.println("\n=== AGREGAR PRODUCTO ===");
        System.out.print("Nombre del producto: ");
        String nombre = scanner.nextLine();
        
        System.out.print("Precio: ");
        double precio = scanner.nextDouble();
        
        System.out.print("Cantidad inicial: ");
        int cantidad = scanner.nextInt();
        scanner.nextLine();
        
        // Mostrar proveedores disponibles
        if (inventario.getProveedores().isEmpty()) {
            System.out.println("No hay proveedores registrados. Debe agregar un proveedor primero.");
            return;
        }
        
        System.out.println("Proveedores disponibles:");
        for (int i = 0; i < inventario.getProveedores().size(); i++) {
            System.out.println((i + 1) + ". " + inventario.getProveedores().get(i).getNombre());
        }
        
        System.out.print("Seleccione proveedor (número): ");
        int proveedorIndex = scanner.nextInt() - 1;
        scanner.nextLine();
        
        if (proveedorIndex >= 0 && proveedorIndex < inventario.getProveedores().size()) {
            Proveedor proveedor = inventario.getProveedores().get(proveedorIndex);
            Producto producto = new Producto(nombre, precio, cantidad, proveedor);
            inventario.agregarProducto(producto);
            System.out.println("Producto agregado exitosamente.");
        } else {
            System.out.println("Proveedor no válido.");
        }
    }
    
    private static void eliminarProducto() {
        System.out.println("\n=== ELIMINAR PRODUCTO ===");
        System.out.print("Nombre del producto a eliminar: ");
        String nombre = scanner.nextLine();
        
        inventario.eliminarProducto(nombre);
        System.out.println("Producto eliminado (si existía).");
    }
    
    private static void agregarProveedor() {
        System.out.println("\n=== AGREGAR PROVEEDOR ===");
        System.out.print("Nombre del proveedor: ");
        String nombre = scanner.nextLine();
        
        System.out.print("Dirección: ");
        String direccion = scanner.nextLine();
        
        System.out.print("Teléfono: ");
        String telefono = scanner.nextLine();
        
        Proveedor proveedor = new Proveedor(nombre, direccion, telefono);
        inventario.agregarProveedor(proveedor);
        System.out.println("Proveedor agregado exitosamente.");
    }
    
    private static void agregarOrdenDeCompra() {
        System.out.println("\n=== AGREGAR ORDEN DE COMPRA ===");
        
        if (inventario.getProductos().isEmpty()) {
            System.out.println("No hay productos registrados.");
            return;
        }
        
        System.out.println("Productos disponibles:");
        for (int i = 0; i < inventario.getProductos().size(); i++) {
            System.out.println((i + 1) + ". " + inventario.getProductos().get(i).getNombre());
        }
        
        System.out.print("Seleccione producto (número): ");
        int productoIndex = scanner.nextInt() - 1;
        
        System.out.print("Cantidad a comprar: ");
        int cantidad = scanner.nextInt();
        scanner.nextLine();
        
        if (productoIndex >= 0 && productoIndex < inventario.getProductos().size()) {
            Producto producto = inventario.getProductos().get(productoIndex);
            OrdenDeEntrada orden = new OrdenDeEntrada(producto, cantidad, producto.getProveedor());
            inventario.agregarOrdenDeCompra(orden);
            System.out.println("Orden de compra agregada exitosamente.");
        } else {
            System.out.println("Producto no válido.");
        }
    }
    
    private static void agregarOrdenDeSalida() {
        System.out.println("\n=== AGREGAR ORDEN DE SALIDA ===");
        
        if (inventario.getProductos().isEmpty()) {
            System.out.println("No hay productos registrados.");
            return;
        }
        
        System.out.println("Productos disponibles:");
        for (int i = 0; i < inventario.getProductos().size(); i++) {
            Producto p = inventario.getProductos().get(i);
            System.out.println((i + 1) + ". " + p.getNombre() + " (Stock: " + p.getCantidad() + ")");
        }
        
        System.out.print("Seleccione producto (número): ");
        int productoIndex = scanner.nextInt() - 1;
        
        System.out.print("Cantidad a sacar: ");
        int cantidad = scanner.nextInt();
        scanner.nextLine();
        
        if (productoIndex >= 0 && productoIndex < inventario.getProductos().size()) {
            Producto producto = inventario.getProductos().get(productoIndex);
            
            if (producto.getCantidad() >= cantidad) {
                OrdenDeSalida orden = new OrdenDeSalida(producto, cantidad, LocalDate.now());
                inventario.agregarOrdenDeSalida(orden);
                System.out.println("Orden de salida agregada exitosamente.");
            } else {
                System.out.println("No hay suficiente stock disponible.");
            }
        } else {
            System.out.println("Producto no válido.");
        }
    }
}