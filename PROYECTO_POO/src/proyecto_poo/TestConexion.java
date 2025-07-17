
package proyecto_poo;

import java.sql.*;

public class TestConexion {
    
    public static void main(String[] args) {
        probarConexion();
    }
    
    public static void probarConexion() {
        try {
            // Cargar el driver JDBC-ODBC
            Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
            
            // Establecer conexión
            Connection cn = DriverManager.getConnection("jdbc:odbc:BDVentas", "sa", "sa");
            
            // Crear statement para ejecutar consultas
            Statement st = cn.createStatement();
            
            // Ejecutar consulta (exactamente como en las diapositivas del profesor)
            ResultSet rs = st.executeQuery("SELECT CodTipoProducto AS Codigo, " +
                                          "DescTipoProducto AS Descripcion FROM TipoProducto");
            
            // Mostrar datos usando el método del profesor
            ConexionBD.mostrarDatos(rs);
            
            // Cerrar conexión
            cn.close();
            
            System.out.println("\n¡Conexión exitosa con la base de datos!");
            
        } catch (Exception ex) {
            System.err.println("Error de conexión: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
