
package proyecto_final_tp;

import java.util.Scanner;
import java.time.LocalDate;
public class Proyecto_Final_TP {

   
    public static void main(String[] args) {
      
          Scanner scanner = new Scanner(System.in,"UTF-8");
        Inventario inventario = new Inventario();

        while (true) {
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
            System.out.print("Selecciona una opción: ");
            int opcion = scanner.nextInt();
            scanner.nextLine();  

            switch (opcion) {
                case 1:
                    System.out.print("Nombre del producto: ");
                    String nombreProducto = scanner.nextLine();
                    System.out.print("Precio del producto: ");
                    double precioProducto = scanner.nextDouble();
                    System.out.print("Cantidad del producto: ");
                    int cantidadProducto = scanner.nextInt();
                    scanner.nextLine();  

                    System.out.print("Nombre del proveedor: ");
                    String nombreProveedor = scanner.nextLine();
                    System.out.print("Dirección del proveedor: ");
                    String direccionProveedor = scanner.nextLine();
                    System.out.print("Teléfono del proveedor: ");
                    String telefonoProveedor = scanner.nextLine();
                    
                    Proveedor proveedor = new Proveedor(nombreProveedor, direccionProveedor, telefonoProveedor);
                    inventario.agregarProveedor(proveedor);

                    Producto producto = new Producto(nombreProducto, precioProducto, cantidadProducto, proveedor);
                    inventario.agregarProducto(producto);
                    break;
                case 2:
                    System.out.print("Nombre del producto a eliminar: ");
                    String nombreProductoEliminar = scanner.nextLine();
                    inventario.eliminarProducto(nombreProductoEliminar);
                    break;
                case 3:
                    inventario.mostrarInventario();
                    break;
                case 4:
                    System.out.print("Nombre del proveedor: ");
                    nombreProveedor = scanner.nextLine();
                    System.out.print("Dirección del proveedor: ");
                    direccionProveedor = scanner.nextLine();
                    System.out.print("Teléfono del proveedor: ");
                    telefonoProveedor = scanner.nextLine();
                    
                    proveedor = new Proveedor(nombreProveedor, direccionProveedor, telefonoProveedor);
                    inventario.agregarProveedor(proveedor);
                    break;
                case 5:
                    inventario.mostrarProveedores();
                    break;
                case 6:
                    System.out.print("Nombre del producto para la orden de compra: ");
                    nombreProducto = scanner.nextLine();
                    Producto prodCompra = inventario.buscarProducto(nombreProducto);
                    if (prodCompra != null) {
                        System.out.print("Cantidad: ");
                        int cantidadCompra = scanner.nextInt();
                        scanner.nextLine();  
                        OrdenDeCompra ordenCompra = new OrdenDeCompra(prodCompra, cantidadCompra, prodCompra.getProveedor());
                        inventario.agregarOrdenDeCompra(ordenCompra);
                    } else {
                        System.out.println("Producto no encontrado.");
                    }
                    break;
                case 7:
                    inventario.mostrarOrdenesDeCompra();
                    break;
                case 8:
                    System.out.print("Nombre del producto para la orden de salida: ");
                    nombreProducto = scanner.nextLine();
                    Producto prodSalida = inventario.buscarProducto(nombreProducto);
                    if (prodSalida != null) {
                        System.out.print("Cantidad: ");
                        int cantidadSalida = scanner.nextInt();
                        scanner.nextLine();  
                        OrdenDeSalida ordenSalida = new OrdenDeSalida(prodSalida, cantidadSalida, LocalDate.now());
                        inventario.agregarOrdenDeSalida(ordenSalida);
                    } else {
                        System.out.println("Producto no encontrado.");
                    }
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
                    System.out.println("Saliendo...");
                    scanner.close();
                    System.exit(0);
                    break;
                default:
                    System.out.println("Opción no válida.");
            }
        }
    }
}
  
 
