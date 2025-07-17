package com.inventario.ejemplo;

import com.inventario.vista.FormularioMostrarOrdenes;
import com.inventario.dao.ConexionBD;
import com.inventario.modelo.*;
import java.time.LocalDateTime;

/**
 * Prueba del FormularioMostrarOrdenes con las nuevas funcionalidades
 * de envío y recepción de órdenes
 */
public class PruebaFormularioOrdenesCompleto {

    public static void main(String[] args) {
        System.out.println("=== PRUEBA FORMULARIO MOSTRAR ÓRDENES CON ENVÍO Y RECEPCIÓN ===\n");

        // Verificar conexión a BD
        if (!ConexionBD.probarConexion()) {
            System.err.println("❌ No se pudo conectar a la base de datos");
            return;
        }

        // Crear datos de prueba si no existen
        crearDatosPrueba();

        // Abrir formulario
        javax.swing.SwingUtilities.invokeLater(() -> {
            FormularioMostrarOrdenes formulario = new FormularioMostrarOrdenes();
            formulario.setVisible(true);

            System.out.println("✅ Formulario abierto exitosamente");
            System.out.println("\n📋 FUNCIONALIDADES DISPONIBLES:");
            System.out.println("1. Filtro por estados: PENDIENTE, ENVIADO, RECIBIDA, etc.");
            System.out.println("2. Búsqueda por texto en cualquier campo");
            System.out.println("3. Botón 'Enviar a Proveedor' (PENDIENTE → ENVIADO)");
            System.out.println("4. Botón 'Recibir Orden' (ENVIADO → RECIBIDA + actualizar stocks)");
            System.out.println("5. Confirmaciones con popup para cada acción");
            System.out.println("\n🔄 FLUJO DE PRUEBA:");
            System.out.println("• Seleccionar una orden PENDIENTE");
            System.out.println("• Hacer clic en 'Enviar a Proveedor'");
            System.out.println("• Confirmar en el popup");
            System.out.println("• Seleccionar una orden ENVIADO");
            System.out.println("• Hacer clic en 'Recibir Orden'");
            System.out.println("• Confirmar en el popup");
            System.out.println("• Verificar actualización de stocks");
        });
    }

    /**
     * Crea datos de prueba para demostrar las funcionalidades
     */
    private static void crearDatosPrueba() {
        try {
            System.out.println("📦 Creando datos de prueba...");

            // Crear productos de prueba
            Producto producto1 = new Producto("Mouse Gaming", 45.00, 10);
            producto1.setCodigoProducto("GAM001");
            producto1.setDescripcion("Mouse gaming con RGB");
            producto1.setCantidadMinima(5);

            Producto producto2 = new Producto("Teclado Mecánico", 120.00, 5);
            producto2.setCodigoProducto("GAM002");
            producto2.setDescripcion("Teclado mecánico switch azul");
            producto2.setCantidadMinima(3);

            // Crear proveedor de prueba
            Proveedor proveedor = new Proveedor("Gaming Store", "Av. Gaming 123", "01-555-0123",
                    "ventas@gaming.com", "20555666777");
            proveedor.setCodigoProveedor("GAM_STORE");

            // Insertar en BD
            ConexionBD.insertarProducto(producto1);
            ConexionBD.insertarProducto(producto2);
            ConexionBD.insertarProveedor(proveedor);

            // Crear relaciones producto-proveedor
            ProductoProveedor pp1 = new ProductoProveedor(proveedor, producto1, 40.00, 5);
            ProductoProveedor pp2 = new ProductoProveedor(proveedor, producto2, 100.00, 7);

            ConexionBD.insertarProductoProveedor(pp1);
            ConexionBD.insertarProductoProveedor(pp2);

            // Crear órdenes de prueba en diferentes estados

            // Orden 1: PENDIENTE (para probar envío)
            OrdenDeEntrada orden1 = new OrdenDeEntrada();
            orden1.setCodigoOrden("ORD-TEST-001");
            orden1.setProveedor(proveedor);
            orden1.setFechaOrden(LocalDateTime.now().minusDays(1));
            orden1.setEstado(EstadoOrden.PENDIENTE);
            orden1.setUsuarioCreador("usuario_prueba");

            orden1.agregarItem(producto1, 5, 40.00);
            orden1.agregarItem(producto2, 2, 100.00);

            ConexionBD.insertarOrdenEntrada(orden1);

            // Orden 2: ENVIADO (para probar recepción)
            OrdenDeEntrada orden2 = new OrdenDeEntrada();
            orden2.setCodigoOrden("ORD-TEST-002");
            orden2.setProveedor(proveedor);
            orden2.setFechaOrden(LocalDateTime.now().minusDays(2));
            orden2.setEstado(EstadoOrden.ENVIADO);
            orden2.setUsuarioCreador("usuario_prueba");

            orden2.agregarItem(producto1, 3, 40.00);
            orden2.agregarItem(producto2, 1, 100.00);

            ConexionBD.insertarOrdenEntrada(orden2);

            // Orden 3: RECIBIDA (para mostrar estado final)
            OrdenDeEntrada orden3 = new OrdenDeEntrada();
            orden3.setCodigoOrden("ORD-TEST-003");
            orden3.setProveedor(proveedor);
            orden3.setFechaOrden(LocalDateTime.now().minusDays(3));
            orden3.setEstado(EstadoOrden.RECIBIDA);
            orden3.setUsuarioCreador("usuario_prueba");

            orden3.agregarItem(producto1, 2, 40.00);

            ConexionBD.insertarOrdenEntrada(orden3);

            System.out.println("✅ Datos de prueba creados exitosamente");
            System.out.println("• Orden ORD-TEST-001: PENDIENTE (lista para enviar)");
            System.out.println("• Orden ORD-TEST-002: ENVIADO (lista para recibir)");
            System.out.println("• Orden ORD-TEST-003: RECIBIDA (completada)");

        } catch (Exception e) {
            System.err.println("⚠️ Error creando datos de prueba: " + e.getMessage());
            System.out.println("Continuando con datos existentes...");
        }
    }
}
