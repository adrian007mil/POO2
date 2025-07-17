package com.inventario.ejemplo;

import com.inventario.modelo.*;
import com.inventario.dao.ConexionBD;
import java.util.List;

/**
 * Ejemplo de uso para asociar productos existentes con proveedores existentes
 * y actualizar esas asociaciones en la base de datos
 */
public class EjemploAsociacionProductoProveedor {

    public static void main(String[] args) {
        System.out.println("=== EJEMPLO: ASOCIACIÓN DE PRODUCTOS Y PROVEEDORES EXISTENTES ===\n");

        // 1. INICIALIZAR BASE DE DATOS
        System.out.println("1. Inicializando base de datos...");
        ConexionBD.ejecutarEsquemaActualizado();

        if (!ConexionBD.probarConexion()) {
            System.err.println("❌ No se pudo conectar a la base de datos. Terminando...");
            return;
        }

        // 2. VERIFICAR PRODUCTOS Y PROVEEDORES EXISTENTES
        System.out.println("\n2. Verificando productos y proveedores existentes...");
        List<Producto> productos = ConexionBD.obtenerProductos();
        List<Proveedor> proveedores = ConexionBD.obtenerProveedores();

        System.out.println("Productos disponibles:");
        for (Producto p : productos) {
            System.out.println(
                    "   - " + p.getCodigoProducto() + ": " + p.getNombre() + " (S/" + p.getPrecioVenta() + ")");
        }

        System.out.println("\nProveedores disponibles:");
        for (Proveedor pr : proveedores) {
            System.out.println("   - " + pr.getCodigoProveedor() + ": " + pr.getNombre());
        }

        // 3. ASOCIAR PRODUCTOS EXISTENTES CON PROVEEDORES EXISTENTES
        System.out.println("\n3. Asociando productos existentes con proveedores...");

        // Asociar Laptop Dell con TechSupply
        boolean resultado1 = ConexionBD.asociarProductoExistenteConProveedor(
                "PROD0001", "PROV0001", 2000.00, 5, true);

        // Asociar Mouse Logitech con TechSupply
        boolean resultado2 = ConexionBD.asociarProductoExistenteConProveedor(
                "PROD0002", "PROV0001", 20.00, 3, false);

        // Asociar Laptop Dell con DistribuTech (segundo proveedor)
        boolean resultado3 = ConexionBD.asociarProductoExistenteConProveedor(
                "PROD0001", "PROV0002", 2100.00, 7, false);

        // Asociar Papel Bond con Suministros Globales
        boolean resultado4 = ConexionBD.asociarProductoExistenteConProveedor(
                "PROD0003", "PROV0003", 12.00, 2, true);

        System.out.println("Resultado de asociaciones:");
        System.out.println("   - Laptop Dell + TechSupply: " + (resultado1 ? "✅ EXITOSO" : "❌ FALLÓ"));
        System.out.println("   - Mouse Logitech + TechSupply: " + (resultado2 ? "✅ EXITOSO" : "❌ FALLÓ"));
        System.out.println("   - Laptop Dell + DistribuTech: " + (resultado3 ? "✅ EXITOSO" : "❌ FALLÓ"));
        System.out.println("   - Papel Bond + Suministros Globales: " + (resultado4 ? "✅ EXITOSO" : "❌ FALLÓ"));

        // 4. VERIFICAR ASOCIACIONES CREADAS
        System.out.println("\n4. Verificando asociaciones creadas...");

        // Buscar asociación específica
        ProductoProveedor pp = ConexionBD.buscarProductoProveedor("PROD0001", "PROV0001");
        if (pp != null) {
            System.out.println("Asociación encontrada: " + pp.getProducto().getNombre() +
                    " - " + pp.getProveedor().getNombre() +
                    " (Precio: S/" + pp.getPrecioCompra() +
                    ", Entrega: " + pp.getTiempoEntrega() + " días" +
                    (pp.isEsPreferido() ? ", PREFERIDO" : "") + ")");
        }

        // 5. OBTENER PROVEEDORES POR PRODUCTO
        System.out.println("\n5. Consultando proveedores para Laptop Dell (PROD0001)...");
        List<Proveedor> proveedoresDeLaptop = ConexionBD.obtenerProveedoresPorProducto("PROD0001");
        for (Proveedor p : proveedoresDeLaptop) {
            System.out.println("   - " + p.getNombre());
        }

        // 6. OBTENER PRODUCTOS POR PROVEEDOR
        System.out.println("\n6. Consultando productos de TechSupply (PROV0001)...");
        List<Producto> productosDeTechSupply = ConexionBD.obtenerProductosPorProveedor("PROV0001");
        for (Producto p : productosDeTechSupply) {
            System.out.println("   - " + p.getNombre() + " | " + p.getDescripcion());
        }

        // 7. ACTUALIZAR ASOCIACIÓN EXISTENTE
        System.out.println("\n7. Actualizando asociación existente...");
        // Cambiar precio de compra del Mouse Logitech de TechSupply
        boolean actualizado = ConexionBD.actualizarProductoProveedor(
                "PROD0002", "PROV0001", 18.50, 2, true, true);
        System.out.println("Actualización de Mouse Logitech: " + (actualizado ? "✅ EXITOSO" : "❌ FALLÓ"));

        // Verificar la actualización
        ProductoProveedor ppActualizado = ConexionBD.buscarProductoProveedor("PROD0002", "PROV0001");
        if (ppActualizado != null) {
            System.out.println("Nuevos datos: Precio S/" + ppActualizado.getPrecioCompra() +
                    ", Entrega " + ppActualizado.getTiempoEntrega() + " días" +
                    (ppActualizado.isEsPreferido() ? ", PREFERIDO" : ""));
        }

        // 8. VERIFICAR SI EXISTE ASOCIACIÓN
        System.out.println("\n8. Verificando existencia de asociaciones...");
        boolean existe1 = ConexionBD.verificarAsociacionExiste("PROD0001", "PROV0001");
        boolean existe2 = ConexionBD.verificarAsociacionExiste("PROD0004", "PROV0001");

        System.out.println("   - Laptop Dell + TechSupply: " + (existe1 ? "✅ EXISTE" : "❌ NO EXISTE"));
        System.out.println("   - Detergente Ariel + TechSupply: " + (existe2 ? "✅ EXISTE" : "❌ NO EXISTE"));

        // 9. ELIMINAR ASOCIACIÓN
        System.out.println("\n9. Eliminando asociación...");
        boolean eliminado = ConexionBD.eliminarProductoProveedor("PROD0001", "PROV0002");
        System.out.println("Eliminación Laptop Dell + DistribuTech: " + (eliminado ? "✅ EXITOSO" : "❌ FALLÓ"));

        // 10. RESUMEN FINAL
        System.out.println("\n10. RESUMEN FINAL:");
        System.out.println("========================");
        System.out.println("✅ Métodos disponibles para asociar productos existentes con proveedores:");
        System.out.println("   • asociarProductoExistenteConProveedor() - Crea o actualiza asociación");
        System.out.println("   • actualizarProductoProveedor() - Actualiza asociación existente");
        System.out.println("   • buscarProductoProveedor() - Busca asociación específica");
        System.out.println("   • obtenerProveedoresPorProducto() - Lista proveedores de un producto");
        System.out.println("   • obtenerProductosPorProveedor() - Lista productos de un proveedor");
        System.out.println("   • verificarAsociacionExiste() - Verifica si existe asociación");
        System.out.println("   • eliminarProductoProveedor() - Elimina asociación");
        System.out.println("\n🎯 Todas las operaciones de asociación producto-proveedor implementadas correctamente!");
    }
}
