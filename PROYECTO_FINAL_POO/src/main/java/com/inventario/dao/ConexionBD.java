package com.inventario.dao;

import com.inventario.modelo.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

public class ConexionBD {

    // Configuración directa de base de datos MySQL
    private static final String DB_URL = "jdbc:mysql://localhost:3306/BDVentas?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "tu_password"; // Cambiar por tu password real
    private static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";

    // Método principal para obtener conexión
    public static Connection getConexion() throws SQLException {
        try {
            Class.forName(DB_DRIVER);
            
            // Intentar conexión normal primero
            try {
                return DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            } catch (SQLException e) {
                // Si falla porque no existe la BD, intentar crearla
                if (e.getMessage().contains("Unknown database")) {
                    System.out.println("⚠️ Base de datos no encontrada. Creando automáticamente...");
                    if (crearBaseDatosSegunNecesario()) {
                        return DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
                    }
                }
                throw e;
            }
            
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver MySQL no encontrado: " + e.getMessage());
        }
    }

    // ================== OPERACIONES DE PRODUCTOS ==================

    public static boolean insertarProducto(Producto producto) {
        String sql = "INSERT INTO Producto (CodProducto, DescProducto, Precio, Stock, CodTipoProducto, Indicador) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConexion();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, producto.getCodigoProducto());
            pstmt.setString(2, producto.getNombre());
            pstmt.setDouble(3, producto.getPrecio());
            pstmt.setInt(4, producto.getCantidad());
            pstmt.setString(5, "TP2011000001"); // Tipo por defecto
            pstmt.setString(6, producto.isEsActivo() ? "S" : "N");

            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al insertar producto: " + e.getMessage());
            return false;
        }
    }

    public static List<Producto> obtenerProductos() {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM Producto WHERE Indicador = 'S' ORDER BY DescProducto";

        try (Connection conn = getConexion();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Producto producto = new Producto();
                producto.setCodigoProducto(rs.getString("CodProducto"));
                producto.setNombre(rs.getString("DescProducto"));
                producto.setPrecio(rs.getDouble("Precio"));
                producto.setCantidad(rs.getInt("Stock"));
                producto.setEsActivo("S".equals(rs.getString("Indicador")));
                productos.add(producto);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener productos: " + e.getMessage());
        }

        return productos;
    }

    public static boolean actualizarProducto(Producto producto) {
        String sql = "UPDATE Producto SET DescProducto = ?, Precio = ?, Stock = ?, Indicador = ? WHERE CodProducto = ?";

        try (Connection conn = getConexion();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, producto.getNombre());
            pstmt.setDouble(2, producto.getPrecio());
            pstmt.setInt(3, producto.getCantidad());
            pstmt.setString(4, producto.isEsActivo() ? "S" : "N");
            pstmt.setString(5, producto.getCodigoProducto());

            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar producto: " + e.getMessage());
            return false;
        }
    }

    public static boolean eliminarProducto(String codigoProducto) {
        String sql = "UPDATE Producto SET Indicador = 'N' WHERE CodProducto = ?";

        try (Connection conn = getConexion();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, codigoProducto);
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar producto: " + e.getMessage());
            return false;
        }
    }

    public static Producto buscarProductoPorCodigo(String codigo) {
        String sql = "SELECT * FROM Producto WHERE CodProducto = ? AND Indicador = 'S'";

        try (Connection conn = getConexion();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, codigo);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Producto producto = new Producto();
                producto.setCodigoProducto(rs.getString("CodProducto"));
                producto.setNombre(rs.getString("DescProducto"));
                producto.setPrecio(rs.getDouble("Precio"));
                producto.setCantidad(rs.getInt("Stock"));
                producto.setEsActivo("S".equals(rs.getString("Indicador")));
                return producto;
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar producto: " + e.getMessage());
        }

        return null;
    }

    // ================== REPORTES ==================

    public static List<Producto> obtenerProductosMayorCantidad() {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM Producto WHERE Indicador = 'S' ORDER BY Stock DESC LIMIT 10";

        try (Connection conn = getConexion();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Producto producto = new Producto();
                producto.setCodigoProducto(rs.getString("CodProducto"));
                producto.setNombre(rs.getString("DescProducto"));
                producto.setPrecio(rs.getDouble("Precio"));
                producto.setCantidad(rs.getInt("Stock"));
                productos.add(producto);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener productos por cantidad: " + e.getMessage());
        }

        return productos;
    }

    public static List<Producto> obtenerProductosMayorPrecio() {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM Producto WHERE Indicador = 'S' ORDER BY Precio DESC LIMIT 10";

        try (Connection conn = getConexion();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Producto producto = new Producto();
                producto.setCodigoProducto(rs.getString("CodProducto"));
                producto.setNombre(rs.getString("DescProducto"));
                producto.setPrecio(rs.getDouble("Precio"));
                producto.setCantidad(rs.getInt("Stock"));
                productos.add(producto);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener productos por precio: " + e.getMessage());
        }

        return productos;
    }

    public static List<Producto> obtenerProductosStockBajo() {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM Producto WHERE Stock <= 10 AND Indicador = 'S' ORDER BY Stock ASC";

        try (Connection conn = getConexion();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Producto producto = new Producto();
                producto.setCodigoProducto(rs.getString("CodProducto"));
                producto.setNombre(rs.getString("DescProducto"));
                producto.setPrecio(rs.getDouble("Precio"));
                producto.setCantidad(rs.getInt("Stock"));
                productos.add(producto);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener productos con stock bajo: " + e.getMessage());
        }

        return productos;
    }

    public static double calcularValorTotalInventario() {
        String sql = "SELECT SUM(Precio * Stock) as ValorTotal FROM Producto WHERE Indicador = 'S'";

        try (Connection conn = getConexion();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getDouble("ValorTotal");
            }

        } catch (SQLException e) {
            System.err.println("Error al calcular valor del inventario: " + e.getMessage());
        }

        return 0.0;
    }

    public static int contarProductosActivos() {
        String sql = "SELECT COUNT(*) as Total FROM Producto WHERE Indicador = 'S'";

        try (Connection conn = getConexion();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("Total");
            }

        } catch (SQLException e) {
            System.err.println("Error al contar productos: " + e.getMessage());
        }

        return 0;
    }

    // ================== UTILIDADES ==================

    public static boolean probarConexion() {
        try (Connection conn = getConexion()) {
            System.out.println("✅ Conexión exitosa a MySQL!");

            // Probar con una consulta simple
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as Total FROM Producto");

            if (rs.next()) {
                System.out.println("📊 Total de productos en BD: " + rs.getInt("Total"));
            }

            return true;

        } catch (SQLException e) {
            System.err.println("❌ Error de conexión: " + e.getMessage());
            return false;
        }
    }

    public static String obtenerEstadisticasInventario() {
        StringBuilder stats = new StringBuilder();

        try (Connection conn = getConexion()) {
            Statement stmt = conn.createStatement();

            // Total productos
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as Total FROM Producto WHERE Indicador = 'S'");
            if (rs.next()) {
                stats.append("Total productos activos: ").append(rs.getInt("Total")).append("\n");
            }

            // Valor total
            rs = stmt.executeQuery("SELECT SUM(Precio * Stock) as ValorTotal FROM Producto WHERE Indicador = 'S'");
            if (rs.next()) {
                stats.append("Valor total inventario: S/").append(String.format("%.2f", rs.getDouble("ValorTotal")))
                        .append("\n");
            }

            // Stock bajo
            rs = stmt.executeQuery("SELECT COUNT(*) as StockBajo FROM Producto WHERE Stock <= 10 AND Indicador = 'S'");
            if (rs.next()) {
                stats.append("Productos con stock bajo: ").append(rs.getInt("StockBajo")).append("\n");
            }

            // Información de conexión
            stats.append("Base de datos: ").append(DB_URL).append("\n");
            stats.append("Usuario: ").append(DB_USERNAME).append("\n");

        } catch (SQLException e) {
            stats.append("Error al obtener estadísticas: ").append(e.getMessage());
        }

        return stats.toString();
    }

    // Getters para información de conexión
    public static String getDbUrl() {
        return DB_URL;
    }

    public static String getDbUsername() {
        return DB_USERNAME;
    }

    public static String getDbDriver() {
        return DB_DRIVER;
    }

    public static boolean actualizarStock(String codigoProducto, int nuevaCantidad) {
        String sql = "UPDATE Producto SET Stock = ? WHERE CodProducto = ? AND Indicador = 'S'";

        try (Connection conn = getConexion();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, nuevaCantidad);
            pstmt.setString(2, codigoProducto);

            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar stock: " + e.getMessage());
            return false;
        }
    }

    // Método para crear la base de datos si no existe
    public static boolean crearBaseDatosSegunNecesario() {
        String urlSinBD = "jdbc:mysql://localhost:3306?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";

        try (Connection conn = DriverManager.getConnection(urlSinBD, DB_USERNAME, DB_PASSWORD)) {
            Statement stmt = conn.createStatement();

            // Crear la base de datos si no existe
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS BDVentas");
            System.out.println("✅ Base de datos BDVentas verificada/creada");

            // Usar la base de datos
            stmt.executeUpdate("USE BDVentas");

            // Crear tablas si no existen
            crearTablasSegunNecesario(conn);

            return true;

        } catch (SQLException e) {
            System.err.println("❌ Error al crear base de datos: " + e.getMessage());
            return false;
        }
    }    // Método para crear tablas si no existen
    private static void crearTablasSegunNecesario(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        
        // Crear tabla TipoProducto
        stmt.executeUpdate(
            "CREATE TABLE IF NOT EXISTS TipoProducto (" +
            "CodTipoProducto VARCHAR(15) PRIMARY KEY," +
            "DescTipoProducto VARCHAR(100) NOT NULL," +
            "Indicador CHAR(1) DEFAULT 'S'" +
            ")"
        );
        
        // Crear tabla Producto
        stmt.executeUpdate(
            "CREATE TABLE IF NOT EXISTS Producto (" +
            "CodProducto VARCHAR(15) PRIMARY KEY," +
            "DescProducto VARCHAR(255) NOT NULL," +
            "Precio DECIMAL(10,2) NOT NULL DEFAULT 0.00," +
            "Stock INT NOT NULL DEFAULT 0," +
            "CodTipoProducto VARCHAR(15) NOT NULL," +
            "FechaCreacion DATETIME DEFAULT CURRENT_TIMESTAMP," +
            "FechaModificacion DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
            "Indicador CHAR(1) DEFAULT 'S'," +
            "FOREIGN KEY (CodTipoProducto) REFERENCES TipoProducto(CodTipoProducto)" +
            ")"
        );
        
        // Insertar datos iniciales si no existen
        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM TipoProducto");
        rs.next();
        if (rs.getInt(1) == 0) {
            stmt.executeUpdate(
                "INSERT INTO TipoProducto (CodTipoProducto, DescTipoProducto) VALUES " +
                "('TP2011000001', 'Producto General')," +
                "('TP2011000002', 'Producto Electrónico')," +
                "('TP2011000003', 'Producto Alimentario')," +
                "('TP2011000004', 'Producto de Limpieza')," +
                "('TP2011000005', 'Producto de Oficina')"
            );
            System.out.println("✅ Datos iniciales insertados");
        }
    }

}