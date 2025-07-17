
package proyecto_poo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConexionBD {
    private static final String DSN = "jdbc:odbc:BDVentas";
    private static final String USUARIO = "sa";
    private static final String PASSWORD = "sa";
    
    // Método para obtener conexión
    public static Connection getConexion() throws SQLException {
        try {
            Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
            return DriverManager.getConnection(DSN, USUARIO, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver JDBC-ODBC no encontrado: " + e.getMessage());
        }
    }
    
    // Método para insertar producto en la base de datos
    public static boolean insertarProducto(Producto producto) {
        String sql = "INSERT INTO Producto VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, producto.getNombre()); // Como código
            pstmt.setString(2, producto.getNombre()); // Como descripción
            pstmt.setDouble(3, producto.getPrecio());
            pstmt.setInt(4, producto.getCantidad());
            pstmt.setString(5, "TP2011000001"); // Tipo por defecto
            pstmt.setString(6, "S"); // Indicador activo
            
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al insertar producto: " + e.getMessage());
            return false;
        }
    }
    
    // Método para obtener todos los productos
    public static List<Producto> obtenerProductos() {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM Producto WHERE Indicador = 'S'";
        
        try (Connection conn = getConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Producto producto = new Producto();
                producto.setNombre(rs.getString("DescProducto"));
                producto.setPrecio(rs.getDouble("Precio"));
                producto.setCantidad(rs.getInt("Stock"));
                productos.add(producto);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener productos: " + e.getMessage());
        }
        
        return productos;
    }
    
    // Método para eliminar producto
    public static boolean eliminarProducto(String nombre) {
        String sql = "UPDATE Producto SET Indicador = 'N' WHERE DescProducto = ?";
        
        try (Connection conn = getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, nombre);
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar producto: " + e.getMessage());
            return false;
        }
    }
    
    // Método para actualizar stock
    public static boolean actualizarStock(String nombre, int nuevaCantidad) {
        String sql = "UPDATE Producto SET Stock = ? WHERE DescProducto = ?";
        
        try (Connection conn = getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, nuevaCantidad);
            pstmt.setString(2, nombre);
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar stock: " + e.getMessage());
            return false;
        }
    }
    
    // Método para mostrar información como en las diapositivas
    public static void mostrarDatos(ResultSet rs) throws SQLException {
        ResultSetMetaData rmeta = rs.getMetaData();
        int numColumnas = rmeta.getColumnCount();
        
        // Mostrar cabeceras
        for (int i = 1; i <= numColumnas; i++) {
            System.out.print(rmeta.getColumnName(i) + "\t\t");
        }
        System.out.println("\n" + "=".repeat(60));
        
        // Mostrar datos
        while (rs.next()) {
            for (int j = 1; j <= numColumnas; j++) {
                System.out.print(rs.getString(j) + "\t\t");
            }
            System.out.println();
        }
    }
}