package com.inventario.ejemplo;

import com.inventario.modelo.*;
import java.time.LocalDateTime;

/**
 * Ejemplo de uso del sistema de inventario conforme a los requisitos
 * especificados
 */
public class EjemploUsoSistema {

    public static void main(String[] args) {
        // Crear instancia del inventario
        Inventario inventario = new Inventario();

        System.out.println("=== SISTEMA DE INVENTARIO WIN EMPRESAS ===\n");

        // 1. AGREGAR PRODUCTOS
        System.out.println("1. Agregando productos...");
        Producto producto1 = new Producto("Laptop Dell", 2500.00, 0);
        producto1.setCantidadMinima(5);

        Producto producto2 = new Producto("Mouse Logitech", 25.50, 0);
        producto2.setCantidadMinima(10);

        inventario.agregarProducto(producto1);
        inventario.agregarProducto(producto2);
        System.out.println("✅ Productos agregados al inventario\n");

        // 2. AGREGAR PROVEEDORES
        System.out.println("2. Agregando proveedores...");
        Proveedor proveedor1 = new Proveedor("TechSupply SAC", "Av. Arequipa 123", "01-234-5678",
                "ventas@techsupply.com", "20123456789");
        Proveedor proveedor2 = new Proveedor("DistribuTech EIRL", "Jr. Comercio 456", "01-876-5432",
                "contacto@distributech.com", "20987654321");

        inventario.agregarProveedor(proveedor1);
        inventario.agregarProveedor(proveedor2);
        System.out.println("✅ Proveedores agregados al sistema\n");

        // 3. AÑADIR PRODUCTOS A PROVEEDORES
        System.out.println("3. Añadiendo productos a proveedores...");
        ProductoProveedor pp1 = new ProductoProveedor(proveedor1, producto1, 2200.00, 7); // Proveedor 1 ofrece laptop
        ProductoProveedor pp2 = new ProductoProveedor(proveedor1, producto2, 20.00, 3); // Proveedor 1 ofrece mouse
        ProductoProveedor pp3 = new ProductoProveedor(proveedor2, producto2, 22.00, 5); // Proveedor 2 ofrece mouse

        inventario.agregarProductoProveedor(pp1);
        inventario.agregarProductoProveedor(pp2);
        inventario.agregarProductoProveedor(pp3);
        System.out.println("✅ Relaciones producto-proveedor establecidas\n");

        // 4. CREAR ORDEN DE ENTRADA
        System.out.println("4. Creando orden de entrada...");
        OrdenDeEntrada orden = new OrdenDeEntrada();
        orden.setCodigoOrden("OC-2023-001");
        orden.setProveedor(proveedor1);
        orden.setFechaOrden(LocalDateTime.now());
        orden.setEstado(EstadoOrden.PENDIENTE);

        // Agregar items a la orden
        orden.agregarItem(producto1, 100, pp1.getPrecioCompra());
        orden.agregarItem(producto2, 50, pp2.getPrecioCompra());

        inventario.agregarOrdenDeEntrada(orden);
        System.out.println("✅ Orden de entrada creada: " + orden.getCodigoOrden());
        System.out.println("   Estado: " + orden.getEstado().getDescripcion());
        System.out.println("   Total: S/" + orden.getMontoTotal() + "\n");

        // 5. ENVIAR PRODUCTO A PROVEEDOR
        System.out.println("5. Enviando orden a proveedor...");
        boolean enviado = orden.enviarProductoAProveedor();
        if (enviado) {
            System.out.println("✅ Orden enviada al proveedor");
            System.out.println("   Nuevo estado: " + orden.getEstado().getDescripcion() + "\n");
        }

        // 6. RECIBIR PRODUCTOS
        System.out.println("6. Recibiendo productos...");

        // Recibir producto específico
        boolean recibido1 = orden.recibirProducto(producto1, 100);
        boolean recibido2 = orden.recibirProducto(producto2, 50);

        if (recibido1 && recibido2) {
            System.out.println("✅ Todos los productos recibidos");

            // Verificar si está completa
            if (orden.estaCompleta()) {
                orden.setEstado(EstadoOrden.RECIBIDA);
                orden.setFechaRecepcion(LocalDateTime.now());

                // Actualizar inventario
                inventario.actualizarInventario(orden);

                System.out.println("✅ Orden completada y inventario actualizado");
                System.out.println("   Estado final: " + orden.getEstado().getDescripcion());
            } else {
                orden.setEstado(EstadoOrden.PARCIAL);
                System.out.println("⚠️  Orden parcialmente recibida");
                System.out.println("   Estado: " + orden.getEstado().getDescripcion());
            }
        }

        // 7. VERIFICAR STOCKS ACTUALIZADOS
        System.out.println("\n7. Verificando stocks actualizados...");
        Producto productoActualizado1 = inventario.buscarProducto(producto1.getCodigoProducto());
        Producto productoActualizado2 = inventario.buscarProducto(producto2.getCodigoProducto());

        if (productoActualizado1 != null) {
            System.out.println("📦 " + productoActualizado1.getNombre() + " - Stock: "
                    + productoActualizado1.getCantidadDisponible());
        }
        if (productoActualizado2 != null) {
            System.out.println("📦 " + productoActualizado2.getNombre() + " - Stock: "
                    + productoActualizado2.getCantidadDisponible());
        }

        System.out.println("\n=== PROCESO COMPLETADO EXITOSAMENTE ===");
    }
}
