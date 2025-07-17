package com.inventario.dao;

import com.inventario.modelo.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConexionBD {

    // Configuración directa de base de datos MySQL
    private static final String DB_URL = "jdbc:mysql://localhost:3306/BDVentas?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "tatakae"; // Cambia esto
    private static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";

    // Método principal para obtener conexión
    public static Connection getConexion() throws SQLException {
        try {
            Class.forName(DB_DRIVER);
            return DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver MySQL no encontrado: " + e.getMessage());
        }
    }

    // ================== OPERACIONES DE PRODUCTOS ==================

    public static boolean insertarProducto(Producto producto) {
        String sql = "INSERT INTO Producto (CodigoProducto, Nombre, Descripcion, PrecioVenta, CantidadDisponible, CantidadMinima, EsActivo, CategoriaID) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConexion();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, producto.getCodigoProducto());
            pstmt.setString(2, producto.getNombre());
            pstmt.setString(3, producto.getDescripcion());
            pstmt.setDouble(4, producto.getPrecioVenta());
            pstmt.setInt(5, producto.getCantidadDisponible());
            pstmt.setInt(6, producto.getCantidadMinima());
            pstmt.setBoolean(7, producto.isEsActivo());
            pstmt.setInt(8, 1); // Categoría por defecto (Electrónicos)

            int filasAfectadas = pstmt.executeUpdate();

            // Obtener el ID generado automáticamente
            if (filasAfectadas > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    int idGenerado = rs.getInt(1);
                    System.out.println("✅ Producto insertado con ID: " + idGenerado);
                }
                return true;
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Error al insertar producto: " + e.getMessage());
            return false;
        }
    }

    public static boolean actualizarProducto(Producto producto) {
        String sql = "UPDATE Producto SET Nombre = ?, Descripcion = ?, PrecioVenta = ?, CantidadDisponible = ?, CantidadMinima = ?, EsActivo = ? WHERE CodigoProducto = ?";

        try (Connection conn = getConexion();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, producto.getNombre());
            pstmt.setString(2, producto.getDescripcion());
            pstmt.setDouble(3, producto.getPrecioVenta());
            pstmt.setInt(4, producto.getCantidadDisponible());
            pstmt.setInt(5, producto.getCantidadMinima());
            pstmt.setBoolean(6, producto.isEsActivo());
            pstmt.setString(7, producto.getCodigoProducto());

            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar producto: " + e.getMessage());
            return false;
        }
    }

    public static boolean eliminarProducto(String codigoProducto) {
        String sql = "UPDATE Producto SET EsActivo = FALSE WHERE CodigoProducto = ?";

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

    public static Producto buscarProductoPorCodigo(String codigoProducto) {
        String sql = "SELECT * FROM Producto WHERE CodigoProducto = ? AND EsActivo = TRUE";

        try (Connection conn = getConexion();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, codigoProducto);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Producto producto = new Producto();
                producto.setCodigoProducto(rs.getString("CodigoProducto"));
                producto.setNombre(rs.getString("Nombre"));
                producto.setDescripcion(rs.getString("Descripcion"));
                producto.setPrecioVenta(rs.getDouble("PrecioVenta"));
                producto.setCantidadDisponible(rs.getInt("CantidadDisponible"));
                producto.setCantidadMinima(rs.getInt("CantidadMinima"));
                producto.setEsActivo(rs.getBoolean("EsActivo"));
                return producto;
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar producto: " + e.getMessage());
        }
        return null;
    }

    public static List<Producto> obtenerProductos() {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM Producto WHERE EsActivo = TRUE ORDER BY Nombre";

        try (Connection conn = getConexion();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Producto producto = new Producto();
                producto.setCodigoProducto(rs.getString("CodigoProducto"));
                producto.setNombre(rs.getString("Nombre"));
                producto.setDescripcion(rs.getString("Descripcion"));
                producto.setPrecioVenta(rs.getDouble("PrecioVenta"));
                producto.setCantidadDisponible(rs.getInt("CantidadDisponible"));
                producto.setCantidadMinima(rs.getInt("CantidadMinima"));
                producto.setEsActivo(rs.getBoolean("EsActivo"));
                productos.add(producto);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener productos: " + e.getMessage());
        }
        return productos;
    }

    public static boolean actualizarStock(String codigoProducto, int nuevaCantidad) {
        String sql = "UPDATE Producto SET CantidadDisponible = ? WHERE CodigoProducto = ?";

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

    // ================== OPERACIONES DE PROVEEDORES ==================

    public static boolean insertarProveedor(Proveedor proveedor) {
        String sql = "INSERT INTO Proveedor (CodigoProveedor, Nombre, RUC, Telefono, Email, Direccion, EsActivo) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConexion();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, proveedor.getCodigoProveedor());
            pstmt.setString(2, proveedor.getNombre());
            pstmt.setString(3, proveedor.getRuc());
            pstmt.setString(4, proveedor.getTelefono());
            pstmt.setString(5, proveedor.getEmail());
            pstmt.setString(6, proveedor.getDireccion());
            pstmt.setBoolean(7, proveedor.isEsActivo());

            int filasAfectadas = pstmt.executeUpdate();

            // Obtener el ID generado automáticamente y asignarlo al objeto
            if (filasAfectadas > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    int idGenerado = rs.getInt(1);
                    // Asignar el ID al objeto proveedor (necesitarás agregar setId en la clase
                    // Proveedor)
                    // proveedor.setId(idGenerado);
                    System.out.println("✅ Proveedor insertado con ID: " + idGenerado);
                }
                return true;
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Error al insertar proveedor: " + e.getMessage());
            return false;
        }
    }

    public static Proveedor buscarProveedorPorCodigo(String codigoProveedor) {
        String sql = "SELECT * FROM Proveedor WHERE CodigoProveedor = ? AND EsActivo = TRUE";

        try (Connection conn = getConexion();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, codigoProveedor);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Proveedor proveedor = new Proveedor();
                proveedor.setCodigoProveedor(rs.getString("CodigoProveedor"));
                proveedor.setNombre(rs.getString("Nombre"));
                proveedor.setRuc(rs.getString("RUC"));
                proveedor.setTelefono(rs.getString("Telefono"));
                proveedor.setEmail(rs.getString("Email"));
                proveedor.setDireccion(rs.getString("Direccion"));
                proveedor.setEsActivo(rs.getBoolean("EsActivo"));
                return proveedor;
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar proveedor: " + e.getMessage());
        }
        return null;
    }

    public static List<Proveedor> obtenerProveedores() {
        List<Proveedor> proveedores = new ArrayList<>();
        String sql = "SELECT * FROM Proveedor WHERE EsActivo = TRUE ORDER BY Nombre";

        try (Connection conn = getConexion();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Proveedor proveedor = new Proveedor();
                proveedor.setCodigoProveedor(rs.getString("CodigoProveedor"));
                proveedor.setNombre(rs.getString("Nombre"));
                proveedor.setRuc(rs.getString("RUC"));
                proveedor.setTelefono(rs.getString("Telefono"));
                proveedor.setEmail(rs.getString("Email"));
                proveedor.setDireccion(rs.getString("Direccion"));
                proveedor.setEsActivo(rs.getBoolean("EsActivo"));
                proveedores.add(proveedor);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener proveedores: " + e.getMessage());
        }
        return proveedores;
    }

    public static boolean actualizarProveedor(Proveedor proveedor) {
        String sql = "UPDATE Proveedor SET Nombre = ?, RUC = ?, Telefono = ?, Email = ?, Direccion = ?, EsActivo = ? WHERE CodigoProveedor = ?";

        try (Connection conn = getConexion();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, proveedor.getNombre());
            pstmt.setString(2, proveedor.getRuc());
            pstmt.setString(3, proveedor.getTelefono());
            pstmt.setString(4, proveedor.getEmail());
            pstmt.setString(5, proveedor.getDireccion());
            pstmt.setBoolean(6, proveedor.isEsActivo());
            pstmt.setString(7, proveedor.getCodigoProveedor());

            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar proveedor: " + e.getMessage());
            return false;
        }
    }

    public static boolean eliminarProveedor(String codigoProveedor) {
        String sql = "UPDATE Proveedor SET EsActivo = FALSE WHERE CodigoProveedor = ?";

        try (Connection conn = getConexion();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, codigoProveedor);

            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar proveedor: " + e.getMessage());
            return false;
        }
    }

    // ================== OPERACIONES DE PRODUCTO-PROVEEDOR ==================

    // Método auxiliar para verificar que tanto el producto como el proveedor
    // existen
    public static boolean verificarProductoYProveedorExisten(String codigoProducto, String codigoProveedor) {
        String sql = "SELECT " +
                "(SELECT COUNT(*) FROM Producto WHERE CodigoProducto = ? AND EsActivo = TRUE) as ProductoExiste, " +
                "(SELECT COUNT(*) FROM Proveedor WHERE CodigoProveedor = ? AND EsActivo = TRUE) as ProveedorExiste";

        try (Connection conn = getConexion();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, codigoProducto);
            pstmt.setString(2, codigoProveedor);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int productoExiste = rs.getInt("ProductoExiste");
                int proveedorExiste = rs.getInt("ProveedorExiste");

                if (productoExiste == 0) {
                    System.err.println("❌ Error: No se encontró el producto con código: " + codigoProducto);
                }
                if (proveedorExiste == 0) {
                    System.err.println("❌ Error: No se encontró el proveedor con código: " + codigoProveedor);
                }

                return productoExiste > 0 && proveedorExiste > 0;
            }

        } catch (SQLException e) {
            System.err.println("Error al verificar producto y proveedor: " + e.getMessage());
        }
        return false;
    }

    public static boolean insertarProductoProveedor(ProductoProveedor pp) {
        // Verificar que tanto el producto como el proveedor existen
        if (!verificarProductoYProveedorExisten(pp.getProducto().getCodigoProducto(),
                pp.getProveedor().getCodigoProveedor())) {
            return false;
        }

        String sql = "INSERT INTO ProductoProveedor (ProductoID, ProveedorID, PrecioCompra, TiempoEntrega, EsPreferido, EsActivo) "
                +
                "VALUES ((SELECT ID FROM Producto WHERE CodigoProducto = ?), (SELECT ID FROM Proveedor WHERE CodigoProveedor = ?), ?, ?, ?, ?)";

        try (Connection conn = getConexion();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, pp.getProducto().getCodigoProducto());
            pstmt.setString(2, pp.getProveedor().getCodigoProveedor());
            pstmt.setDouble(3, pp.getPrecioCompra());
            pstmt.setInt(4, pp.getTiempoEntrega());
            pstmt.setBoolean(5, pp.isEsPreferido());
            pstmt.setBoolean(6, pp.isEsActivo());

            int filasAfectadas = pstmt.executeUpdate();

            // Obtener el ID generado automáticamente y asignarlo al objeto
            if (filasAfectadas > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    int idGenerado = rs.getInt(1);
                    // Asignar el ID al objeto ProductoProveedor (necesitarás agregar setId en la
                    // clase)
                    // pp.setId(idGenerado);
                    System.out.println("✅ ProductoProveedor insertado con ID: " + idGenerado);
                }
                return true;
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Error al insertar producto-proveedor: " + e.getMessage());
            return false;
        }
    }

    public static boolean actualizarProductoProveedor(String codigoProducto, String codigoProveedor,
            double nuevoPrecioCompra, int nuevoTiempoEntrega,
            boolean esPreferido, boolean esActivo) {
        String sql = "UPDATE ProductoProveedor SET PrecioCompra = ?, TiempoEntrega = ?, EsPreferido = ?, EsActivo = ? "
                +
                "WHERE ProductoID = (SELECT ID FROM Producto WHERE CodigoProducto = ?) " +
                "AND ProveedorID = (SELECT ID FROM Proveedor WHERE CodigoProveedor = ?)";

        try (Connection conn = getConexion();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, nuevoPrecioCompra);
            pstmt.setInt(2, nuevoTiempoEntrega);
            pstmt.setBoolean(3, esPreferido);
            pstmt.setBoolean(4, esActivo);
            pstmt.setString(5, codigoProducto);
            pstmt.setString(6, codigoProveedor);

            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar producto-proveedor: " + e.getMessage());
            return false;
        }
    }

    public static boolean actualizarProductoProveedor(ProductoProveedor pp) {
        return actualizarProductoProveedor(
                pp.getProducto().getCodigoProducto(),
                pp.getProveedor().getCodigoProveedor(),
                pp.getPrecioCompra(),
                pp.getTiempoEntrega(),
                pp.isEsPreferido(),
                pp.isEsActivo());
    }

    public static ProductoProveedor buscarProductoProveedor(String codigoProducto, String codigoProveedor) {
        String sql = "SELECT pp.*, p.CodigoProducto, p.Nombre as ProductoNombre, " +
                "pr.CodigoProveedor, pr.Nombre as ProveedorNombre " +
                "FROM ProductoProveedor pp " +
                "JOIN Producto p ON pp.ProductoID = p.ID " +
                "JOIN Proveedor pr ON pp.ProveedorID = pr.ID " +
                "WHERE p.CodigoProducto = ? AND pr.CodigoProveedor = ? AND pp.EsActivo = TRUE";

        try (Connection conn = getConexion();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, codigoProducto);
            pstmt.setString(2, codigoProveedor);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Crear objetos producto y proveedor básicos
                Producto producto = new Producto();
                producto.setCodigoProducto(rs.getString("CodigoProducto"));
                producto.setNombre(rs.getString("ProductoNombre"));

                Proveedor proveedor = new Proveedor();
                proveedor.setCodigoProveedor(rs.getString("CodigoProveedor"));
                proveedor.setNombre(rs.getString("ProveedorNombre"));

                // Crear ProductoProveedor
                ProductoProveedor pp = new ProductoProveedor();
                pp.setProducto(producto);
                pp.setProveedor(proveedor);
                pp.setPrecioCompra(rs.getDouble("PrecioCompra"));
                pp.setTiempoEntrega(rs.getInt("TiempoEntrega"));
                pp.setEsPreferido(rs.getBoolean("EsPreferido"));
                pp.setEsActivo(rs.getBoolean("EsActivo"));

                return pp;
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar producto-proveedor: " + e.getMessage());
        }
        return null;
    }

    public static List<Proveedor> obtenerProveedoresPorProducto(String codigoProducto) {
        List<Proveedor> proveedores = new ArrayList<>();
        String sql = "SELECT pr.*, pp.PrecioCompra, pp.TiempoEntrega, pp.EsPreferido " +
                "FROM ProductoProveedor pp " +
                "JOIN Proveedor pr ON pp.ProveedorID = pr.ID " +
                "JOIN Producto p ON pp.ProductoID = p.ID " +
                "WHERE p.CodigoProducto = ? AND pp.EsActivo = TRUE AND pr.EsActivo = TRUE " +
                "ORDER BY pp.EsPreferido DESC, pp.PrecioCompra ASC";

        try (Connection conn = getConexion();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, codigoProducto);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Proveedor proveedor = new Proveedor();
                proveedor.setCodigoProveedor(rs.getString("CodigoProveedor"));
                proveedor.setNombre(rs.getString("Nombre"));
                proveedor.setRuc(rs.getString("RUC"));
                proveedor.setTelefono(rs.getString("Telefono"));
                proveedor.setEmail(rs.getString("Email"));
                proveedor.setDireccion(rs.getString("Direccion"));
                proveedor.setEsActivo(rs.getBoolean("EsActivo"));

                // Agregar información adicional como comentario
                proveedor.setNombre(proveedor.getNombre() + " (Precio: S/" + rs.getDouble("PrecioCompra") +
                        ", Entrega: " + rs.getInt("TiempoEntrega") + " días" +
                        (rs.getBoolean("EsPreferido") ? ", PREFERIDO" : "") + ")");

                proveedores.add(proveedor);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener proveedores por producto: " + e.getMessage());
        }
        return proveedores;
    }

    public static List<Producto> obtenerProductosPorProveedor(String codigoProveedor) {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT p.*, pp.PrecioCompra, pp.TiempoEntrega, pp.EsPreferido " +
                "FROM ProductoProveedor pp " +
                "JOIN Producto p ON pp.ProductoID = p.ID " +
                "JOIN Proveedor pr ON pp.ProveedorID = pr.ID " +
                "WHERE pr.CodigoProveedor = ? AND pp.EsActivo = TRUE AND p.EsActivo = TRUE " +
                "ORDER BY pp.EsPreferido DESC, p.Nombre ASC";

        try (Connection conn = getConexion();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, codigoProveedor);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Producto producto = new Producto();
                producto.setCodigoProducto(rs.getString("CodigoProducto"));
                producto.setNombre(rs.getString("Nombre"));
                producto.setDescripcion(rs.getString("Descripcion"));
                producto.setPrecioVenta(rs.getDouble("PrecioVenta"));
                producto.setCantidadDisponible(rs.getInt("CantidadDisponible"));
                producto.setCantidadMinima(rs.getInt("CantidadMinima"));
                producto.setEsActivo(rs.getBoolean("EsActivo"));

                // Agregar información adicional como comentario
                producto.setDescripcion(
                        producto.getDescripcion() + " | Precio compra: S/" + rs.getDouble("PrecioCompra") +
                                ", Entrega: " + rs.getInt("TiempoEntrega") + " días" +
                                (rs.getBoolean("EsPreferido") ? ", PREFERIDO" : ""));

                productos.add(producto);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener productos por proveedor: " + e.getMessage());
        }
        return productos;
    }

    public static boolean eliminarProductoProveedor(String codigoProducto, String codigoProveedor) {
        String sql = "UPDATE ProductoProveedor SET EsActivo = FALSE " +
                "WHERE ProductoID = (SELECT ID FROM Producto WHERE CodigoProducto = ?) " +
                "AND ProveedorID = (SELECT ID FROM Proveedor WHERE CodigoProveedor = ?)";

        try (Connection conn = getConexion();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, codigoProducto);
            pstmt.setString(2, codigoProveedor);

            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar producto-proveedor: " + e.getMessage());
            return false;
        }
    }

    public static boolean verificarAsociacionExiste(String codigoProducto, String codigoProveedor) {
        String sql = "SELECT COUNT(*) FROM ProductoProveedor pp " +
                "JOIN Producto p ON pp.ProductoID = p.ID " +
                "JOIN Proveedor pr ON pp.ProveedorID = pr.ID " +
                "WHERE p.CodigoProducto = ? AND pr.CodigoProveedor = ? AND pp.EsActivo = TRUE";

        try (Connection conn = getConexion();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, codigoProducto);
            pstmt.setString(2, codigoProveedor);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.err.println("Error al verificar asociación: " + e.getMessage());
        }
        return false;
    }

    public static boolean asociarProductoExistenteConProveedor(String codigoProducto, String codigoProveedor,
            double precioCompra, int tiempoEntrega,
            boolean esPreferido) {
        // Verificar si ya existe la asociación
        if (verificarAsociacionExiste(codigoProducto, codigoProveedor)) {
            // Si existe, actualizar
            return actualizarProductoProveedor(codigoProducto, codigoProveedor, precioCompra, tiempoEntrega,
                    esPreferido, true);
        } else {
            // Si no existe, crear nueva asociación
            Producto producto = buscarProductoPorCodigo(codigoProducto);
            Proveedor proveedor = buscarProveedorPorCodigo(codigoProveedor);

            if (producto != null && proveedor != null) {
                ProductoProveedor pp = new ProductoProveedor();
                pp.setProducto(producto);
                pp.setProveedor(proveedor);
                pp.setPrecioCompra(precioCompra);
                pp.setTiempoEntrega(tiempoEntrega);
                pp.setEsPreferido(esPreferido);
                pp.setEsActivo(true);

                return insertarProductoProveedor(pp);
            }
        }
        return false;
    }

    // ================== OPERACIONES DE ÓRDENES DE ENTRADA ==================

    public static boolean insertarOrdenEntrada(OrdenDeEntrada orden) {
        String sql = "INSERT INTO OrdenEntrada (CodigoOrden, ProveedorID, EstadoOrden, MontoTotal, UsuarioCreacion) " +
                "VALUES (?, (SELECT ID FROM Proveedor WHERE CodigoProveedor = ?), ?, ?, ?)";

        try (Connection conn = getConexion();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, orden.getCodigoOrden());
            pstmt.setString(2, orden.getProveedor().getCodigoProveedor());
            pstmt.setString(3, orden.getEstado().name());
            pstmt.setDouble(4, orden.getMontoTotal());
            pstmt.setString(5, orden.getUsuarioCreador());

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    int ordenId = rs.getInt(1);

                    // Asignar el ID al objeto OrdenDeEntrada (necesitarás agregar setId en la
                    // clase)
                    // orden.setId(ordenId);
                    System.out.println("✅ OrdenDeEntrada insertada con ID: " + ordenId);

                    // Insertar detalles de la orden
                    for (ItemOrdenCompra item : orden.getItems()) {
                        insertarDetalleOrdenEntrada(ordenId, item);
                    }
                }
                return true;
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Error al insertar orden de entrada: " + e.getMessage());
            return false;
        }
    }

    private static boolean insertarDetalleOrdenEntrada(int ordenId, ItemOrdenCompra item) {
        String sql = "INSERT INTO DetalleOrdenEntrada (OrdenEntradaID, ProductoID, Cantidad, PrecioUnitario, Subtotal) "
                +
                "VALUES (?, (SELECT ID FROM Producto WHERE CodigoProducto = ?), ?, ?, ?)";

        try (Connection conn = getConexion();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, ordenId);
            pstmt.setString(2, item.getProducto().getCodigoProducto());
            pstmt.setInt(3, item.getCantidad());
            pstmt.setDouble(4, item.getPrecioUnitario());
            pstmt.setDouble(5, item.getSubtotal());

            int filasAfectadas = pstmt.executeUpdate();

            // Obtener el ID generado automáticamente y asignarlo al objeto
            if (filasAfectadas > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    int idGenerado = rs.getInt(1);
                    // Asignar el ID al objeto ItemOrdenCompra (necesitarás agregar setId en la
                    // clase)
                    // item.setId(idGenerado);
                    System.out.println("✅ DetalleOrdenEntrada insertado con ID: " + idGenerado);
                }
                return true;
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Error al insertar detalle de orden: " + e.getMessage());
            return false;
        }
    }

    // ================== OPERACIONES DE UTILIDAD ==================

    public static boolean probarConexion() {
        try (Connection conn = getConexion()) {
            System.out.println("✅ Conexión exitosa a MySQL!");
            return true;
        } catch (SQLException e) {
            System.err.println("❌ Error de conexión: " + e.getMessage());
            return false;
        }
    }

    public static void ejecutarEsquemaActualizado() {
        String[] comandos = {
                "CREATE DATABASE IF NOT EXISTS BDVentas",
                "USE BDVentas",

                // Eliminar tablas existentes
                "DROP TABLE IF EXISTS DetalleOrdenSalida",
                "DROP TABLE IF EXISTS DetalleOrdenEntrada",
                "DROP TABLE IF EXISTS OrdenSalida",
                "DROP TABLE IF EXISTS OrdenEntrada",
                "DROP TABLE IF EXISTS ProductoProveedor",
                "DROP TABLE IF EXISTS Producto",
                "DROP TABLE IF EXISTS Categoria",
                "DROP TABLE IF EXISTS TipoProducto",
                "DROP TABLE IF EXISTS Proveedor",
                "DROP TABLE IF EXISTS Cliente",

                // Crear tabla Categoria
                "CREATE TABLE Categoria (" +
                        "ID INT AUTO_INCREMENT PRIMARY KEY," +
                        "Codigo VARCHAR(15) UNIQUE NOT NULL," +
                        "Nombre VARCHAR(100) NOT NULL," +
                        "Descripcion TEXT," +
                        "EsActivo BOOLEAN DEFAULT TRUE," +
                        "FechaCreacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                        "FechaModificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP)",

                // Crear tabla Cliente
                "CREATE TABLE Cliente (" +
                        "ID INT AUTO_INCREMENT PRIMARY KEY," +
                        "Codigo VARCHAR(15) UNIQUE NOT NULL," +
                        "Nombre VARCHAR(255) NOT NULL," +
                        "RUC VARCHAR(15) UNIQUE," +
                        "Telefono VARCHAR(20)," +
                        "Email VARCHAR(100)," +
                        "Direccion TEXT," +
                        "EsActivo BOOLEAN DEFAULT TRUE," +
                        "FechaCreacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                        "FechaModificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP)",

                // Crear tabla Proveedor
                "CREATE TABLE Proveedor (" +
                        "ID INT AUTO_INCREMENT PRIMARY KEY," +
                        "CodigoProveedor VARCHAR(15) UNIQUE NOT NULL," +
                        "Nombre VARCHAR(255) NOT NULL," +
                        "RUC VARCHAR(15) UNIQUE," +
                        "Telefono VARCHAR(20)," +
                        "Email VARCHAR(100)," +
                        "Direccion TEXT," +
                        "EsActivo BOOLEAN DEFAULT TRUE," +
                        "FechaRegistro DATE DEFAULT (CURRENT_DATE)," +
                        "TotalOrdenes INT DEFAULT 0," +
                        "MontoTotalCompras DECIMAL(15,2) DEFAULT 0.00," +
                        "Calificacion DECIMAL(3,2) DEFAULT 5.00," +
                        "FechaCreacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                        "FechaModificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP)",

                // Crear tabla Producto
                "CREATE TABLE Producto (" +
                        "ID INT AUTO_INCREMENT PRIMARY KEY," +
                        "CodigoProducto VARCHAR(15) UNIQUE NOT NULL," +
                        "Nombre VARCHAR(255) NOT NULL," +
                        "Descripcion TEXT," +
                        "PrecioVenta DECIMAL(10,2) NOT NULL DEFAULT 0.00," +
                        "CantidadDisponible INT NOT NULL DEFAULT 0," +
                        "CantidadMinima INT NOT NULL DEFAULT 5," +
                        "EsActivo BOOLEAN DEFAULT TRUE," +
                        "CategoriaID INT," +
                        "FechaCreacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                        "FechaModificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                        "FOREIGN KEY (CategoriaID) REFERENCES Categoria(ID) ON DELETE SET NULL)",

                // Crear tabla ProductoProveedor
                "CREATE TABLE ProductoProveedor (" +
                        "ID INT AUTO_INCREMENT PRIMARY KEY," +
                        "ProductoID INT NOT NULL," +
                        "ProveedorID INT NOT NULL," +
                        "PrecioCompra DECIMAL(10,2) NOT NULL," +
                        "TiempoEntrega INT DEFAULT 7," +
                        "EsPreferido BOOLEAN DEFAULT FALSE," +
                        "EsActivo BOOLEAN DEFAULT TRUE," +
                        "FechaCreacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                        "FechaModificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                        "FOREIGN KEY (ProductoID) REFERENCES Producto(ID) ON DELETE CASCADE," +
                        "FOREIGN KEY (ProveedorID) REFERENCES Proveedor(ID) ON DELETE CASCADE," +
                        "UNIQUE KEY unique_producto_proveedor (ProductoID, ProveedorID))",

                // Crear tabla OrdenEntrada
                "CREATE TABLE OrdenEntrada (" +
                        "ID INT AUTO_INCREMENT PRIMARY KEY," +
                        "CodigoOrden VARCHAR(20) UNIQUE NOT NULL," +
                        "ProveedorID INT NOT NULL," +
                        "FechaOrden TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                        "FechaEntrega TIMESTAMP NULL," +
                        "EstadoOrden ENUM('PENDIENTE', 'ENVIADO', 'APROBADA', 'RECIBIDA', 'PARCIAL', 'CANCELADA') DEFAULT 'PENDIENTE',"
                        +
                        "MontoTotal DECIMAL(15,2) DEFAULT 0.00," +
                        "UsuarioCreacion VARCHAR(50)," +
                        "Observaciones TEXT," +
                        "EsActivo BOOLEAN DEFAULT TRUE," +
                        "FechaCreacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                        "FechaModificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                        "FOREIGN KEY (ProveedorID) REFERENCES Proveedor(ID) ON DELETE RESTRICT)",

                // Crear tabla DetalleOrdenEntrada
                "CREATE TABLE DetalleOrdenEntrada (" +
                        "ID INT AUTO_INCREMENT PRIMARY KEY," +
                        "OrdenEntradaID INT NOT NULL," +
                        "ProductoID INT NOT NULL," +
                        "Cantidad INT NOT NULL," +
                        "CantidadRecibida INT DEFAULT 0," +
                        "PrecioUnitario DECIMAL(10,2) NOT NULL," +
                        "Subtotal DECIMAL(12,2) NOT NULL," +
                        "FechaCreacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                        "FOREIGN KEY (OrdenEntradaID) REFERENCES OrdenEntrada(ID) ON DELETE CASCADE," +
                        "FOREIGN KEY (ProductoID) REFERENCES Producto(ID) ON DELETE RESTRICT)",

                // Insertar categorías por defecto
                "INSERT INTO Categoria (Codigo, Nombre, Descripcion) VALUES " +
                        "('CAT001', 'Electrónicos', 'Productos electrónicos y tecnológicos'), " +
                        "('CAT002', 'Oficina', 'Artículos de oficina y papelería'), " +
                        "('CAT003', 'Hogar', 'Productos para el hogar'), " +
                        "('CAT004', 'Consumibles', 'Productos consumibles y desechables')"
        };

        try (Connection conn = getConexion()) {
            conn.setAutoCommit(false);

            for (String comando : comandos) {
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute(comando);
                }
            }

            conn.commit();
            System.out.println("✅ Esquema actualizado ejecutado correctamente");

        } catch (SQLException e) {
            System.err.println("❌ Error al ejecutar esquema: " + e.getMessage());
        }
    }

    public static int contarProductosActivos() {
        String sql = "SELECT COUNT(*) FROM Producto WHERE EsActivo = TRUE";
        try (Connection conn = getConexion();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error al contar productos: " + e.getMessage());
        }
        return 0;
    }

    public static double calcularValorTotalInventario() {
        String sql = "SELECT SUM(PrecioVenta * CantidadDisponible) FROM Producto WHERE EsActivo = TRUE";
        try (Connection conn = getConexion();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.err.println("Error al calcular valor total: " + e.getMessage());
        }
        return 0.0;
    }

    public static List<Producto> obtenerProductosStockBajo() {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM Producto WHERE CantidadDisponible <= CantidadMinima AND EsActivo = TRUE";

        try (Connection conn = getConexion();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Producto producto = new Producto();
                producto.setCodigoProducto(rs.getString("CodigoProducto"));
                producto.setNombre(rs.getString("Nombre"));
                producto.setDescripcion(rs.getString("Descripcion"));
                producto.setPrecioVenta(rs.getDouble("PrecioVenta"));
                producto.setCantidadDisponible(rs.getInt("CantidadDisponible"));
                producto.setCantidadMinima(rs.getInt("CantidadMinima"));
                producto.setEsActivo(rs.getBoolean("EsActivo"));
                productos.add(producto);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener productos con stock bajo: " + e.getMessage());
        }
        return productos;
    }

    public static List<Producto> obtenerProductosMayorCantidad() {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM Producto WHERE EsActivo = TRUE ORDER BY CantidadDisponible DESC LIMIT 10";

        try (Connection conn = getConexion();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Producto producto = new Producto();
                producto.setCodigoProducto(rs.getString("CodigoProducto"));
                producto.setNombre(rs.getString("Nombre"));
                producto.setDescripcion(rs.getString("Descripcion"));
                producto.setPrecioVenta(rs.getDouble("PrecioVenta"));
                producto.setCantidadDisponible(rs.getInt("CantidadDisponible"));
                producto.setCantidadMinima(rs.getInt("CantidadMinima"));
                producto.setEsActivo(rs.getBoolean("EsActivo"));
                productos.add(producto);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener productos con mayor cantidad: " + e.getMessage());
        }
        return productos;
    }

    public static List<Producto> obtenerProductosMayorPrecio() {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM Producto WHERE EsActivo = TRUE ORDER BY PrecioVenta DESC LIMIT 10";

        try (Connection conn = getConexion();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Producto producto = new Producto();
                producto.setCodigoProducto(rs.getString("CodigoProducto"));
                producto.setNombre(rs.getString("Nombre"));
                producto.setDescripcion(rs.getString("Descripcion"));
                producto.setPrecioVenta(rs.getDouble("PrecioVenta"));
                producto.setCantidadDisponible(rs.getInt("CantidadDisponible"));
                producto.setCantidadMinima(rs.getInt("CantidadMinima"));
                producto.setEsActivo(rs.getBoolean("EsActivo"));
                productos.add(producto);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener productos con mayor precio: " + e.getMessage());
        }
        return productos;
    }

    public static String obtenerEstadisticasInventario() {
        StringBuilder stats = new StringBuilder();
        String sql = "SELECT c.Nombre, COUNT(p.ID) as TotalProductos, SUM(p.CantidadDisponible) as TotalStock, " +
                "SUM(p.PrecioVenta * p.CantidadDisponible) as ValorTotal " +
                "FROM Categoria c " +
                "LEFT JOIN Producto p ON c.ID = p.CategoriaID AND p.EsActivo = TRUE " +
                "WHERE c.EsActivo = TRUE " +
                "GROUP BY c.ID, c.Nombre";

        try (Connection conn = getConexion();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                stats.append(String.format("%s: %d productos, Stock: %d, Valor: S/%.2f\n",
                        rs.getString("Nombre"),
                        rs.getInt("TotalProductos"),
                        rs.getInt("TotalStock"),
                        rs.getDouble("ValorTotal")));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener estadísticas: " + e.getMessage());
        }
        return stats.toString();
    }
}
