package com.inventario.modelo;

import com.inventario.dao.ConexionBD;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Inventario {
    private List<Producto> productos;
    private List<Proveedor> proveedores;
    private List<OrdenDeEntrada> ordenesDeCompra;
    private List<OrdenDeSalida> ordenesDeSalida;
    private String nombreEmpresa;
    private String version;
    private LocalDate fechaCreacion;
    
    // Constructor
    public Inventario() {
        this.productos = new ArrayList<>();
        this.proveedores = new ArrayList<>();
        this.ordenesDeCompra = new ArrayList<>();
        this.ordenesDeSalida = new ArrayList<>();
        this.nombreEmpresa = "WIN EMPRESAS";
        this.version = "2.0";
        this.fechaCreacion = LocalDate.now();
        
        // Cargar datos desde la base de datos al inicializar
        cargarDatosDesdeBaseDatos();
    }
    
    // ================== MÉTODOS DE PRODUCTOS ==================
    
    public void agregarProducto(Producto producto) {
        // Intentar guardar en base de datos primero
        if (ConexionBD.insertarProducto(producto)) {
            // Si se guarda exitosamente en BD, agregarlo a la lista local
            productos.add(producto);
            System.out.println("✅ Producto agregado exitosamente a la base de datos.");
        } else {
            System.out.println("❌ Error al agregar producto a la base de datos.");
        }
    }
    
    public boolean eliminarProducto(String codigoProducto) {
        // Eliminar de base de datos (desactivación lógica)
        if (ConexionBD.eliminarProducto(codigoProducto)) {
            // Eliminar de la lista local
            productos.removeIf(p -> p.getCodigoProducto().equals(codigoProducto));
            System.out.println("✅ Producto eliminado exitosamente de la base de datos.");
            return true;
        } else {
            System.out.println("❌ Error al eliminar producto de la base de datos.");
            return false;
        }
    }
    
    public boolean actualizarProducto(Producto producto) {
        if (ConexionBD.actualizarProducto(producto)) {
            // Actualizar en la lista local
            for (int i = 0; i < productos.size(); i++) {
                if (productos.get(i).getCodigoProducto().equals(producto.getCodigoProducto())) {
                    productos.set(i, producto);
                    break;
                }
            }
            System.out.println("✅ Producto actualizado exitosamente.");
            return true;
        } else {
            System.out.println("❌ Error al actualizar producto.");
            return false;
        }
    }
    
    public Producto buscarProducto(String codigoProducto) {
        // Buscar primero en la base de datos para datos más actualizados
        Producto productoBD = ConexionBD.buscarProductoPorCodigo(codigoProducto);
        if (productoBD != null) {
            return productoBD;
        }
        
        // Si no se encuentra en BD, buscar en lista local
        return productos.stream()
                .filter(p -> p.getCodigoProducto().equals(codigoProducto))
                .findFirst()
                .orElse(null);
    }
    
    public List<Producto> buscarProductosPorNombre(String nombre) {
        return productos.stream()
                .filter(p -> p.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    public void actualizarStock(String codigoProducto, int nuevaCantidad) {
//        if (ConexionBD.actualizarStock(codigoProducto, nuevaCantidad)) {
//            // Actualizar en lista local
//            for (Producto p : productos) {
//                if (p.getCodigoProducto().equals(codigoProducto)) {
//                    p.setCantidad(nuevaCantidad);
//                    break;
//                }
//            }
//            System.out.println("✅ Stock actualizado exitosamente.");
//        } else {
//            System.out.println("❌ Error al actualizar stock.");
//        }
    }
    
    // ================== MÉTODOS DE PROVEEDORES ==================
    
    public void agregarProveedor(Proveedor proveedor) {
        proveedores.add(proveedor);
        System.out.println("✅ Proveedor agregado: " + proveedor.getNombre());
    }
    
    public boolean eliminarProveedor(String codigoProveedor) {
        boolean eliminado = proveedores.removeIf(p -> p.getCodigoProveedor().equals(codigoProveedor));
        if (eliminado) {
            System.out.println("✅ Proveedor eliminado exitosamente.");
        } else {
            System.out.println("❌ Proveedor no encontrado.");
        }
        return eliminado;
    }
    
    public Proveedor buscarProveedor(String codigoProveedor) {
        return proveedores.stream()
                .filter(p -> p.getCodigoProveedor().equals(codigoProveedor))
                .findFirst()
                .orElse(null);
    }
    
    public List<Proveedor> buscarProveedoresPorNombre(String nombre) {
        return proveedores.stream()
                .filter(p -> p.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    public List<Proveedor> obtenerProveedoresPorCalificacion(double calificacionMinima) {
        return proveedores.stream()
                .filter(p -> p.getCalificacion() >= calificacionMinima)
                .sorted((p1, p2) -> Double.compare(p2.getCalificacion(), p1.getCalificacion()))
                .collect(Collectors.toList());
    }
    
    // ================== MÉTODOS DE ÓRDENES DE ENTRADA ==================
    
    public void agregarOrdenDeCompra(OrdenDeEntrada orden) {
        ordenesDeCompra.add(orden);
        
        // Si la orden está recibida, actualizar stock automáticamente
        if ("RECIBIDA".equals(orden.getEstado())) {
            for (OrdenDeEntrada.DetalleOrdenEntrada detalle : orden.getDetalles()) {
                Producto producto = detalle.getProducto();
                int nuevoStock = producto.getCantidad() + detalle.getCantidad();
                actualizarStock(producto.getCodigoProducto(), nuevoStock);
            }
            
            // Actualizar estadísticas del proveedor
            if (orden.getProveedor() != null) {
                orden.getProveedor().actualizarEstadisticas(orden.getMontoFinal());
            }
        }
        
        System.out.println("✅ Orden de compra agregada: " + orden.getCodigoOrden());
    }
    
    public boolean procesarOrdenCompra(String codigoOrden, String accion, String usuario) {
        OrdenDeEntrada orden = buscarOrdenCompra(codigoOrden);
        if (orden == null) {
            System.out.println("❌ Orden no encontrada: " + codigoOrden);
            return false;
        }
        
        switch (accion.toUpperCase()) {
            case "APROBAR":
                return orden.aprobarOrden(usuario);
            case "RECIBIR":
                if (orden.recibirOrden(usuario)) {
                    // Actualizar stock en base de datos
                    for (OrdenDeEntrada.DetalleOrdenEntrada detalle : orden.getDetalles()) {
                        Producto producto = detalle.getProducto();
                        int nuevoStock = producto.getCantidad() + detalle.getCantidad();
                        actualizarStock(producto.getCodigoProducto(), nuevoStock);
                    }
                    return true;
                }
                return false;
            case "CANCELAR":
                String motivo = "Cancelación solicitada por usuario";
                return orden.cancelarOrden(motivo, usuario);
            default:
                System.out.println("❌ Acción no válida: " + accion);
                return false;
        }
    }
    
    public OrdenDeEntrada buscarOrdenCompra(String codigoOrden) {
        return ordenesDeCompra.stream()
                .filter(o -> o.getCodigoOrden().equals(codigoOrden))
                .findFirst()
                .orElse(null);
    }
    
    // ================== MÉTODOS DE ÓRDENES DE SALIDA ==================
    
    public void agregarOrdenDeSalida(OrdenDeSalida orden) {
        ordenesDeSalida.add(orden);
        
        // Si la orden está entregada, actualizar stock automáticamente
        if ("ENTREGADA".equals(orden.getEstado())) {
            for (OrdenDeSalida.DetalleSalida detalle : orden.getDetalles()) {
                Producto producto = detalle.getProducto();
                int nuevoStock = producto.getCantidad() - detalle.getCantidad();
                actualizarStock(producto.getCodigoProducto(), Math.max(0, nuevoStock));
            }
        }
        
        System.out.println("✅ Orden de salida agregada: " + orden.getCodigoOrden());
    }
    
    public boolean procesarOrdenSalida(String codigoOrden, String accion, String usuario) {
        OrdenDeSalida orden = buscarOrdenSalida(codigoOrden);
        if (orden == null) {
            System.out.println("❌ Orden no encontrada: " + codigoOrden);
            return false;
        }
        
        switch (accion.toUpperCase()) {
            case "APROBAR":
                return orden.aprobarOrden(usuario);
            case "ENTREGAR":
                if (orden.entregarOrden(usuario)) {
                    // Actualizar stock en base de datos
                    for (OrdenDeSalida.DetalleSalida detalle : orden.getDetalles()) {
                        Producto producto = detalle.getProducto();
                        int nuevoStock = producto.getCantidad() - detalle.getCantidad();
                        actualizarStock(producto.getCodigoProducto(), Math.max(0, nuevoStock));
                    }
                    return true;
                }
                return false;
            case "CANCELAR":
                String motivo = "Cancelación solicitada por usuario";
                return orden.cancelarOrden(motivo, usuario);
            default:
                System.out.println("❌ Acción no válida: " + accion);
                return false;
        }
    }
    
    public OrdenDeSalida buscarOrdenSalida(String codigoOrden) {
        return ordenesDeSalida.stream()
                .filter(o -> o.getCodigoOrden().equals(codigoOrden))
                .findFirst()
                .orElse(null);
    }
    
    // ================== MÉTODOS DE REPORTES ==================
    
    public void mostrarInventario() {
        System.out.println("=== INVENTARIO COMPLETO (DESDE BASE DE DATOS) ===");
        List<Producto> productosDB = ConexionBD.obtenerProductos();
        
        if (productosDB.isEmpty()) {
            System.out.println("No hay productos en el inventario.");
            return;
        }
        
        System.out.printf("%-12s %-30s %-10s %-8s %-15s%n", 
                "CÓDIGO", "NOMBRE", "PRECIO", "STOCK", "VALOR TOTAL");
        System.out.println("-".repeat(80));
        
        double valorTotal = 0;
        for (Producto p : productosDB) {
            double valorProducto = p.getPrecio() * p.getCantidad();
            valorTotal += valorProducto;
            
            System.out.printf("%-12s %-30s S/%-8.2f %-8d S/%-13.2f%n",
                    p.getCodigoProducto(),
                    p.getNombre().length() > 30 ? p.getNombre().substring(0, 27) + "..." : p.getNombre(),
                    p.getPrecio(),
                    p.getCantidad(),
                    valorProducto);
        }
        
        System.out.println("-".repeat(80));
        System.out.printf("TOTAL PRODUCTOS: %d | VALOR TOTAL: S/%.2f%n", productosDB.size(), valorTotal);
    }
    
    public void mostrarProveedores() {
        System.out.println("=== LISTA DE PROVEEDORES ===");
        if (proveedores.isEmpty()) {
            System.out.println("No hay proveedores registrados.");
            return;
        }
        
        System.out.printf("%-8s %-25s %-15s %-12s %-15s%n", 
                "CÓDIGO", "NOMBRE", "RUC", "TELÉFONO", "CALIFICACIÓN");
        System.out.println("-".repeat(80));
        
        for (Proveedor p : proveedores) {
            System.out.printf("%-8s %-25s %-15s %-12s %-15s%n",
                    p.getCodigoProveedor(),
                    p.getNombre().length() > 25 ? p.getNombre().substring(0, 22) + "..." : p.getNombre(),
                    p.getRuc(),
                    p.getTelefono(),
                    p.getClasificacion() + " (" + String.format("%.1f", p.getCalificacion()) + ")");
        }
    }
    
    public void mostrarOrdenesDeCompra() {
        System.out.println("=== ÓRDENES DE COMPRA ===");
        if (ordenesDeCompra.isEmpty()) {
            System.out.println("No hay órdenes de compra registradas.");
            return;
        }
        
        for (OrdenDeEntrada orden : ordenesDeCompra) {
            System.out.println(orden.getResumen());
        }
    }
    
    public void mostrarOrdenesDeSalida() {
        System.out.println("=== ÓRDENES DE SALIDA ===");
        if (ordenesDeSalida.isEmpty()) {
            System.out.println("No hay órdenes de salida registradas.");
            return;
        }
        
        for (OrdenDeSalida orden : ordenesDeSalida) {
            System.out.println(orden.getResumen());
        }
    }
    
    public void reporteProductosMayorCantidad() {
        System.out.println("=== REPORTE: PRODUCTOS MAYOR CANTIDAD ===");
        List<Producto> productos = ConexionBD.obtenerProductosMayorCantidad();
        
        for (int i = 0; i < productos.size(); i++) {
            Producto p = productos.get(i);
            System.out.printf("%d. %s - Stock: %d unidades%n", 
                    (i + 1), p.getNombre(), p.getCantidad());
        }
    }
    
    public void reporteProductosMayorPrecio() {
        System.out.println("=== REPORTE: PRODUCTOS MAYOR PRECIO ===");
        List<Producto> productos = ConexionBD.obtenerProductosMayorPrecio();
        
        for (int i = 0; i < productos.size(); i++) {
            Producto p = productos.get(i);
            System.out.printf("%d. %s - Precio: S/%.2f%n", 
                    (i + 1), p.getNombre(), p.getPrecio());
        }
    }
    
    public void reporteProductosStockBajo() {
        System.out.println("=== REPORTE: PRODUCTOS CON STOCK BAJO ===");
        List<Producto> productos = ConexionBD.obtenerProductosStockBajo();
        
        if (productos.isEmpty()) {
            System.out.println("✅ No hay productos con stock bajo.");
            return;
        }
        
        System.out.println("⚠️ PRODUCTOS QUE REQUIEREN REABASTECIMIENTO:");
        for (Producto p : productos) {
            String urgencia = p.getCantidad() <= 5 ? "CRÍTICA" : "MEDIA";
            System.out.printf("• %s - Stock: %d - Urgencia: %s%n", 
                    p.getNombre(), p.getCantidad(), urgencia);
        }
    }
    
    public void reporteValorInventario() {
        System.out.println("=== REPORTE: VALORIZACIÓN DEL INVENTARIO ===");
        double valorTotal = ConexionBD.calcularValorTotalInventario();
        int totalProductos = ConexionBD.contarProductosActivos();
        
        System.out.printf("Total de Productos: %d%n", totalProductos);
        System.out.printf("Valor Total del Inventario: S/%.2f%n", valorTotal);
        System.out.printf("Valor Promedio por Producto: S/%.2f%n", 
                totalProductos > 0 ? valorTotal / totalProductos : 0);
        
        // Estadísticas adicionales
        System.out.println("\nDETALLE POR CATEGORÍAS:");
        System.out.println(ConexionBD.obtenerEstadisticasInventario());
    }
    
    public void reporteProveedoresMejorCalificados() {
        System.out.println("=== REPORTE: PROVEEDORES MEJOR CALIFICADOS ===");
        List<Proveedor> mejoresProveedores = obtenerProveedoresPorCalificacion(4.0);
        
        if (mejoresProveedores.isEmpty()) {
            System.out.println("No hay proveedores con calificación >= 4.0");
            return;
        }
        
        for (int i = 0; i < mejoresProveedores.size(); i++) {
            Proveedor p = mejoresProveedores.get(i);
            System.out.printf("%d. %s - Calificación: %.1f (%s) - Órdenes: %d%n",
                    (i + 1), p.getNombre(), p.getCalificacion(), 
                    p.getClasificacion(), p.getTotalOrdenes());
        }
    }
    
    public void reporteResumenGeneral() {
        System.out.println("=== RESUMEN GENERAL DEL SISTEMA ===");
        System.out.printf("Empresa: %s%n", nombreEmpresa);
        System.out.printf("Versión del Sistema: %s%n", version);
        System.out.printf("Fecha de Creación: %s%n", fechaCreacion);
        System.out.println("-".repeat(50));
        
        // Estadísticas de productos
        int totalProductos = ConexionBD.contarProductosActivos();
        double valorInventario = ConexionBD.calcularValorTotalInventario();
        List<Producto> stockBajo = ConexionBD.obtenerProductosStockBajo();
        
        System.out.printf("📦 PRODUCTOS:%n");
        System.out.printf("   • Total activos: %d%n", totalProductos);
        System.out.printf("   • Valor total: S/%.2f%n", valorInventario);
        System.out.printf("   • Stock bajo: %d productos%n", stockBajo.size());
        
        // Estadísticas de proveedores
        System.out.printf("🏢 PROVEEDORES:%n");
        System.out.printf("   • Total registrados: %d%n", proveedores.size());
        
        if (!proveedores.isEmpty()) {
            double promedioCalificacion = proveedores.stream()
                    .mapToDouble(Proveedor::getCalificacion)
                    .average()
                    .orElse(0.0);
            System.out.printf("   • Calificación promedio: %.2f%n", promedioCalificacion);
            
            long proveedoresExcelentes = proveedores.stream()
                    .filter(p -> p.getCalificacion() >= 4.5)
                    .count();
            System.out.printf("   • Proveedores excelentes: %d%n", proveedoresExcelentes);
        }
        
        // Estadísticas de órdenes
        System.out.printf("📋 ÓRDENES:%n");
        System.out.printf("   • Órdenes de entrada: %d%n", ordenesDeCompra.size());
        System.out.printf("   • Órdenes de salida: %d%n", ordenesDeSalida.size());
        
        // Estado del sistema
        System.out.printf("🔧 SISTEMA:%n");
        System.out.printf("   • Estado BD: %s%n", ConexionBD.probarConexion() ? "Conectada" : "Desconectada");
        System.out.printf("   • Última actualización: %s%n", java.time.LocalDateTime.now());
        
        // Alertas importantes
        if (!stockBajo.isEmpty()) {
            System.out.println("\n⚠️ ALERTAS:");
            System.out.printf("   • %d productos necesitan reabastecimiento urgente%n", stockBajo.size());
        } else {
            System.out.println("\n✅ SISTEMA: Todo funcionando correctamente");
        }
    }
    
    // ================== MÉTODOS DE ANÁLISIS ==================
    
    public List<Producto> obtenerProductosPopulares() {
        // Basado en la cantidad de órdenes de salida
        return ordenesDeSalida.stream()
                .flatMap(orden -> orden.getDetalles().stream())
                .collect(Collectors.groupingBy(
                    detalle -> detalle.getProducto(),
                    Collectors.summingInt(OrdenDeSalida.DetalleSalida::getCantidad)
                ))
                .entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .map(entry -> entry.getKey())
                .limit(10)
                .collect(Collectors.toList());
    }
    
    public double calcularRotacionInventario() {
        // Cálculo simplificado: Total vendido / Stock promedio
        int totalVendido = ordenesDeSalida.stream()
                .filter(orden -> "ENTREGADA".equals(orden.getEstado()))
                .flatMap(orden -> orden.getDetalles().stream())
                .mapToInt(OrdenDeSalida.DetalleSalida::getCantidad)
                .sum();
        
        int stockTotal = productos.stream()
                .mapToInt(Producto::getCantidad)
                .sum();
        
        return stockTotal > 0 ? (double) totalVendido / stockTotal : 0;
    }
    
    public List<Proveedor> analizarDesempenoProveedores() {
        return proveedores.stream()
                .sorted((p1, p2) -> {
                    // Ordenar por calificación y número de órdenes
                    int comparacionCalificacion = Double.compare(p2.getCalificacion(), p1.getCalificacion());
                    if (comparacionCalificacion == 0) {
                        return Integer.compare(p2.getTotalOrdenes(), p1.getTotalOrdenes());
                    }
                    return comparacionCalificacion;
                })
                .collect(Collectors.toList());
    }
    
    // ================== MÉTODOS DE UTILIDAD ==================
    
    private void cargarDatosDesdeBaseDatos() {
        try {
            // Cargar productos desde la base de datos
            List<Producto> productosDB = ConexionBD.obtenerProductos();
            this.productos.clear();
            this.productos.addAll(productosDB);
            
            System.out.printf("✅ Cargados %d productos desde la base de datos.%n", productos.size());
        } catch (Exception e) {
            System.err.println("⚠️ Error al cargar datos desde BD: " + e.getMessage());
        }
    }
    
    public void sincronizarConBaseDatos() {
        cargarDatosDesdeBaseDatos();
        System.out.println("🔄 Sincronización con base de datos completada.");
    }
    
    public void generarBackup() {
        // Implementación simplificada de backup
        try {
            String backup = generarReporteCompleto();
            System.out.println("💾 Backup generado exitosamente.");
            System.out.println("Contenido del backup guardado en memoria.");
        } catch (Exception e) {
            System.err.println("❌ Error al generar backup: " + e.getMessage());
        }
    }
    
    public String generarReporteCompleto() {
        StringBuilder reporte = new StringBuilder();
        reporte.append("=== REPORTE COMPLETO DEL SISTEMA WIN EMPRESAS ===\n");
        reporte.append("Fecha de generación: ").append(java.time.LocalDateTime.now()).append("\n");
        reporte.append("Versión: ").append(version).append("\n\n");
        
        // Productos
        reporte.append("PRODUCTOS REGISTRADOS:\n");
        for (Producto p : productos) {
            reporte.append(p.toString()).append("\n");
        }
        
        // Proveedores
        reporte.append("\nPROVEEDORES REGISTRADOS:\n");
        for (Proveedor p : proveedores) {
            reporte.append(p.toString()).append("\n");
        }
        
        // Órdenes
        reporte.append("\nÓRDENES DE ENTRADA:\n");
        for (OrdenDeEntrada o : ordenesDeCompra) {
            reporte.append(o.toString()).append("\n");
        }
        
        reporte.append("\nÓRDENES DE SALIDA:\n");
        for (OrdenDeSalida o : ordenesDeSalida) {
            reporte.append(o.toString()).append("\n");
        }
        
        return reporte.toString();
    }
    
    public boolean validarIntegridadDatos() {
        boolean integridadCorrecta = true;
        
        // Validar que no hay productos con stock negativo
        for (Producto p : productos) {
            if (p.getCantidad() < 0) {
                System.err.printf("❌ Error: Producto %s tiene stock negativo: %d%n", 
                        p.getNombre(), p.getCantidad());
                integridadCorrecta = false;
            }
        }
        
        // Validar calificaciones de proveedores
        for (Proveedor p : proveedores) {
            if (p.getCalificacion() < 1.0 || p.getCalificacion() > 5.0) {
                System.err.printf("❌ Error: Proveedor %s tiene calificación inválida: %.2f%n", 
                        p.getNombre(), p.getCalificacion());
                integridadCorrecta = false;
            }
        }
        
        if (integridadCorrecta) {
            System.out.println("✅ Integridad de datos verificada correctamente.");
        }
        
        return integridadCorrecta;
    }
    
    // ================== GETTERS Y SETTERS ==================
    
    public List<Producto> getProductos() {
        // Retornar datos actualizados desde BD
        return ConexionBD.obtenerProductos();
    }
    
    public List<Proveedor> getProveedores() {
        return new ArrayList<>(proveedores);
    }
    
    public List<OrdenDeEntrada> getOrdenesDeCompra() {
        return new ArrayList<>(ordenesDeCompra);
    }
    
    public List<OrdenDeSalida> getOrdenesDeSalida() {
        return new ArrayList<>(ordenesDeSalida);
    }
    
    public String getNombreEmpresa() {
        return nombreEmpresa;
    }
    
    public void setNombreEmpresa(String nombreEmpresa) {
        this.nombreEmpresa = nombreEmpresa;
    }
    
    public String getVersion() {
        return version;
    }
    
    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }
    
    // ================== MÉTODOS ESTADÍSTICOS ==================
    
    public int getTotalProductosActivos() {
        return ConexionBD.contarProductosActivos();
    }
    
    public double getValorTotalInventario() {
        return ConexionBD.calcularValorTotalInventario();
    }
    
    public int getCantidadProveedoresActivos() {
        return (int) proveedores.stream().filter(Proveedor::isEsActivo).count();
    }
    
    public int getTotalOrdenesProcesadas() {
        return ordenesDeCompra.size() + ordenesDeSalida.size();
    }
    
    public double getPromedioCalificacionProveedores() {
        return proveedores.stream()
                .mapToDouble(Proveedor::getCalificacion)
                .average()
                .orElse(0.0);
    }
    
    @Override
    public String toString() {
        return String.format("Inventario{empresa='%s', productos=%d, proveedores=%d, " +
                           "ordenesEntrada=%d, ordenesSalida=%d, version='%s'}",
                nombreEmpresa, getTotalProductosActivos(), proveedores.size(),
                ordenesDeCompra.size(), ordenesDeSalida.size(), version);
    }
}