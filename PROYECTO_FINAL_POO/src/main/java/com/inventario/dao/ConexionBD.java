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

            // Usar la categoría seleccionada del producto
            if (producto.getCategoria() != null) {
                pstmt.setInt(8, producto.getCategoria().getId());
            } else {
                pstmt.setInt(8, 1); // Categoría por defecto solo si no se seleccionó ninguna
            }

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
        String sql = "UPDATE Producto SET Nombre = ?, Descripcion = ?, PrecioVenta = ?, CantidadDisponible = ?, CantidadMinima = ?, EsActivo = ?, CategoriaID = ? WHERE CodigoProducto = ?";

        try (Connection conn = getConexion();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, producto.getNombre());
            pstmt.setString(2, producto.getDescripcion());
            pstmt.setDouble(3, producto.getPrecioVenta());
            pstmt.setInt(4, producto.getCantidadDisponible());
            pstmt.setInt(5, producto.getCantidadMinima());
            pstmt.setBoolean(6, producto.isEsActivo());

            // Incluir categoría en la actualización
            if (producto.getCategoria() != null) {
                pstmt.setInt(7, producto.getCategoria().getId());
            } else {
                pstmt.setInt(7, 1); // Categoría por defecto solo si no se seleccionó ninguna
            }

            pstmt.setString(8, producto.getCodigoProducto());

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
                producto.setId(rs.getInt("ID"));
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
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int idGenerado = rs.getInt(1);
                        proveedor.setId(idGenerado);
                        System.out.println("✅ Proveedor insertado con ID: " + idGenerado);
                    }
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

    /**
     * Obtiene todas las categorías de la base de datos
     * 
     * @return Lista de categorías
     * @throws SQLException Si hay error en la consulta
     */
    public static List<Categoria> obtenerCategorias() throws SQLException {
        List<Categoria> categorias = new ArrayList<>();
        try (Connection conn = getConexion();
                PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Categoria ORDER BY Nombre");
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Categoria categoria = new Categoria();
                categoria.setId(rs.getInt("ID"));
                categoria.setCodigo(rs.getString("Codigo"));
                categoria.setNombre(rs.getString("Nombre"));
                categoria.setDescripcion(rs.getString("Descripcion"));
                categoria.setEsActivo(rs.getBoolean("EsActivo"));
                categorias.add(categoria);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener categorías: " + e.getMessage());
            throw e;
        }

        return categorias;
    }

    /**
     * Obtiene un producto por su ID
     * 
     * @param id ID del producto
     * @return Producto encontrado o null si no existe
     * @throws SQLException Si hay error en la consulta
     */
    public static Producto obtenerProductoPorId(int id) throws SQLException {
        Producto producto = null;
        try (Connection conn = getConexion()) {
            String sql = "SELECT p.*, c.ID as categoria_id, c.Codigo as categoria_codigo, " +
                    "c.Nombre as categoria_nombre, c.Descripcion as categoria_descripcion, " +
                    "c.EsActivo as categoria_activo " +
                    "FROM Producto p " +
                    "LEFT JOIN Categoria c ON p.CategoriaID = c.ID " +
                    "WHERE p.ID = ?";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        producto = new Producto();
                        producto.setId(rs.getInt("ID"));
                        producto.setCodigoProducto(rs.getString("CodigoProducto"));
                        producto.setNombre(rs.getString("Nombre"));
                        producto.setDescripcion(rs.getString("Descripcion"));
                        producto.setPrecioVenta(rs.getDouble("PrecioVenta"));
                        producto.setCantidadDisponible(rs.getInt("CantidadDisponible"));
                        producto.setCantidadMinima(rs.getInt("CantidadMinima"));
                        producto.setEsActivo(rs.getBoolean("EsActivo"));

                        // Cargar categoría si existe
                        if (rs.getInt("categoria_id") != 0) {
                            Categoria categoria = new Categoria();
                            categoria.setId(rs.getInt("categoria_id"));
                            categoria.setCodigo(rs.getString("categoria_codigo"));
                            categoria.setNombre(rs.getString("categoria_nombre"));
                            categoria.setDescripcion(rs.getString("categoria_descripcion"));
                            categoria.setEsActivo(rs.getBoolean("categoria_activo"));
                            producto.setCategoria(categoria);
                        }
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener producto por ID: " + e.getMessage());
            throw e;
        }

        return producto;
    }

    /**
     * Elimina un producto por su ID
     * 
     * @param id ID del producto a eliminar
     * @return true si se eliminó exitosamente, false en caso contrario
     * @throws SQLException Si hay error en la operación
     */
    public static boolean eliminarProductoPorId(int id) throws SQLException {
        try (Connection conn = getConexion();
                PreparedStatement stmt = conn.prepareStatement("DELETE FROM Producto WHERE ID = ?")) {

            stmt.setInt(1, id);
            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar producto: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Inserta una nueva categoría en la base de datos
     * 
     * @param categoria Categoría a insertar
     * @return true si se insertó correctamente, false en caso contrario
     */
    public static boolean insertarCategoria(Categoria categoria) {
        try (Connection conn = getConexion();
                PreparedStatement stmt = conn.prepareStatement(
                        "INSERT INTO Categoria (Codigo, Nombre, Descripcion, EsActivo) VALUES (?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, categoria.getCodigo());
            stmt.setString(2, categoria.getNombre());
            stmt.setString(3, categoria.getDescripcion());
            stmt.setBoolean(4, categoria.isEsActivo());

            int filasAfectadas = stmt.executeUpdate();

            if (filasAfectadas > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        categoria.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error al insertar categoría: " + e.getMessage());
        }
        return false;
    }

    /**
     * Actualiza una categoría existente en la base de datos
     * 
     * @param categoria Categoría a actualizar
     * @return true si se actualizó correctamente, false en caso contrario
     */
    public static boolean actualizarCategoria(Categoria categoria) {
        try (Connection conn = getConexion();
                PreparedStatement stmt = conn.prepareStatement(
                        "UPDATE Categoria SET Codigo = ?, Nombre = ?, Descripcion = ?, EsActivo = ? WHERE ID = ?")) {

            stmt.setString(1, categoria.getCodigo());
            stmt.setString(2, categoria.getNombre());
            stmt.setString(3, categoria.getDescripcion());
            stmt.setBoolean(4, categoria.isEsActivo());
            stmt.setInt(5, categoria.getId());

            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar categoría: " + e.getMessage());
        }
        return false;
    }

    /**
     * Elimina una categoría por su ID
     * 
     * @param id ID de la categoría a eliminar
     * @return true si se eliminó correctamente, false en caso contrario
     */
    public static boolean eliminarCategoria(int id) {
        try (Connection conn = getConexion();
                PreparedStatement stmt = conn.prepareStatement("DELETE FROM Categoria WHERE ID = ?")) {

            stmt.setInt(1, id);
            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar categoría: " + e.getMessage());
        }
        return false;
    }

    /**
     * Desactiva una categoría (eliminación lógica)
     * 
     * @param id ID de la categoría a desactivar
     * @return true si se desactivó correctamente, false en caso contrario
     */
    public static boolean desactivarCategoria(int id) {
        try (Connection conn = getConexion();
                PreparedStatement stmt = conn.prepareStatement("UPDATE Categoria SET EsActivo = false WHERE ID = ?")) {

            stmt.setInt(1, id);
            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al desactivar categoría: " + e.getMessage());
        }
        return false;
    }

    /**
     * Obtiene solo las categorías activas
     * 
     * @return Lista de categorías activas
     * @throws SQLException Si hay error en la consulta
     */
    public static List<Categoria> obtenerCategoriasActivas() throws SQLException {
        List<Categoria> categorias = new ArrayList<>();

        try (Connection conn = getConexion();
                PreparedStatement stmt = conn
                        .prepareStatement("SELECT * FROM Categoria WHERE EsActivo = true ORDER BY Nombre");
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Categoria categoria = new Categoria();
                categoria.setId(rs.getInt("ID"));
                categoria.setCodigo(rs.getString("Codigo"));
                categoria.setNombre(rs.getString("Nombre"));
                categoria.setDescripcion(rs.getString("Descripcion"));
                categoria.setEsActivo(rs.getBoolean("EsActivo"));
                categorias.add(categoria);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener categorías activas: " + e.getMessage());
            throw e;
        }

        return categorias;
    }

    /**
     * Desactiva un producto (eliminación lógica)
     * 
     * @param id ID del producto a desactivar
     * @return true si se desactivó correctamente, false en caso contrario
     */
    public static boolean desactivarProducto(int id) {
        try (Connection conn = getConexion();
                PreparedStatement stmt = conn.prepareStatement("UPDATE Producto SET EsActivo = false WHERE ID = ?")) {

            stmt.setInt(1, id);
            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al desactivar producto: " + e.getMessage());
        }
        return false;
    }

    /**
     * Obtiene solo los productos activos
     * 
     * @return Lista de productos activos
     * @throws SQLException Si hay error en la consulta
     */
    public static List<Producto> obtenerProductosActivos() throws SQLException {
        List<Producto> productos = new ArrayList<>();

        try (Connection conn = getConexion();
                PreparedStatement stmt = conn.prepareStatement(
                        "SELECT p.*, c.Nombre as categoria_nombre FROM Producto p " +
                                "LEFT JOIN Categoria c ON p.CategoriaID = c.ID " +
                                "WHERE p.EsActivo = true ORDER BY p.Nombre");
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Producto producto = new Producto();
                producto.setId(rs.getInt("ID"));
                producto.setCodigoProducto(rs.getString("CodigoProducto"));
                producto.setNombre(rs.getString("Nombre"));
                producto.setDescripcion(rs.getString("Descripcion"));
                producto.setPrecioVenta(rs.getDouble("PrecioVenta"));
                producto.setCantidadDisponible(rs.getInt("CantidadDisponible"));
                producto.setCantidadMinima(rs.getInt("CantidadMinima"));
                producto.setEsActivo(rs.getBoolean("EsActivo"));

                // Configurar categoría
                if (rs.getInt("CategoriaID") != 0) {
                    Categoria categoria = new Categoria();
                    categoria.setId(rs.getInt("CategoriaID"));
                    categoria.setNombre(rs.getString("categoria_nombre"));
                    producto.setCategoria(categoria);
                }

                productos.add(producto);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener productos activos: " + e.getMessage());
            throw e;
        }

        return productos;
    }

    /**
     * Obtiene todos los proveedores activos
     * 
     * @return Lista de proveedores activos
     * @throws SQLException Si hay error en la consulta
     */
    public static List<Proveedor> obtenerProveedoresActivos() throws SQLException {
        List<Proveedor> proveedores = new ArrayList<>();

        try (Connection conn = getConexion();
                PreparedStatement stmt = conn.prepareStatement(
                        "SELECT * FROM Proveedor WHERE EsActivo = true ORDER BY Nombre");
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Proveedor proveedor = new Proveedor();
                proveedor.setId(rs.getInt("ID"));
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
            System.err.println("Error al obtener proveedores activos: " + e.getMessage());
            throw e;
        }

        return proveedores;
    }

    /**
     * Desactiva un proveedor (eliminación lógica)
     * 
     * @param id ID del proveedor a desactivar
     * @return true si se desactivó correctamente, false en caso contrario
     */
    public static boolean desactivarProveedor(int id) {
        try (Connection conn = getConexion();
                PreparedStatement stmt = conn.prepareStatement("UPDATE Proveedor SET EsActivo = false WHERE ID = ?")) {

            stmt.setInt(1, id);
            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al desactivar proveedor: " + e.getMessage());
        }
        return false;
    }

    /**
     * Obtiene un proveedor por su ID
     * 
     * @param id ID del proveedor
     * @return Proveedor encontrado o null si no existe
     * @throws SQLException Si hay error en la consulta
     */
    public static Proveedor obtenerProveedorPorId(int id) throws SQLException {
        try (Connection conn = getConexion();
                PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Proveedor WHERE ID = ?")) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Proveedor proveedor = new Proveedor();
                proveedor.setId(rs.getInt("ID"));
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
            System.err.println("Error al obtener proveedor por ID: " + e.getMessage());
            throw e;
        }
        return null;
    }

    /**
     * Asocia un producto con un proveedor
     * 
     * @param productoId    ID del producto
     * @param proveedorId   ID del proveedor
     * @param precioCompra  Precio de compra
     * @param tiempoEntrega Tiempo de entrega en días
     * @return true si se asoció correctamente, false en caso contrario
     */
    public static boolean asociarProductoProveedor(int productoId, int proveedorId, double precioCompra,
            int tiempoEntrega) {
        try (Connection conn = getConexion();
                PreparedStatement stmt = conn.prepareStatement(
                        "INSERT INTO ProductoProveedor (ProductoID, ProveedorID, PrecioCompra, TiempoEntrega, EsActivo) VALUES (?, ?, ?, ?, true) "
                                +
                                "ON DUPLICATE KEY UPDATE PrecioCompra = ?, TiempoEntrega = ?, EsActivo = true")) {

            stmt.setInt(1, productoId);
            stmt.setInt(2, proveedorId);
            stmt.setDouble(3, precioCompra);
            stmt.setInt(4, tiempoEntrega);
            stmt.setDouble(5, precioCompra);
            stmt.setInt(6, tiempoEntrega);

            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al asociar producto con proveedor: " + e.getMessage());
        }
        return false;
    }

    /**
     * Obtiene los productos asociados a un proveedor
     * 
     * @param proveedorId ID del proveedor
     * @return Lista de productos asociados
     * @throws SQLException Si hay error en la consulta
     */
    public static List<Producto> obtenerProductosPorProveedor(int proveedorId) throws SQLException {
        List<Producto> productos = new ArrayList<>();

        try (Connection conn = getConexion();
                PreparedStatement stmt = conn.prepareStatement(
                        "SELECT p.*, pp.PrecioCompra, pp.TiempoEntrega " +
                                "FROM Producto p " +
                                "INNER JOIN ProductoProveedor pp ON p.ID = pp.ProductoID " +
                                "WHERE pp.ProveedorID = ? AND pp.EsActivo = true AND p.EsActivo = true " +
                                "ORDER BY p.Nombre")) {

            stmt.setInt(1, proveedorId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Producto producto = new Producto();
                producto.setId(rs.getInt("ID"));
                producto.setCodigoProducto(rs.getString("CodigoProducto"));
                producto.setNombre(rs.getString("Nombre"));
                producto.setDescripcion(rs.getString("Descripcion"));
                producto.setPrecioVenta(rs.getDouble("PrecioVenta"));
                producto.setCantidadDisponible(rs.getInt("CantidadDisponible"));
                producto.setEsActivo(rs.getBoolean("EsActivo"));
                productos.add(producto);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener productos por proveedor: " + e.getMessage());
            throw e;
        }

        return productos;
    }

    /**
     * Desasocia un producto de un proveedor
     * 
     * @param productoId  ID del producto
     * @param proveedorId ID del proveedor
     * @return true si se desasocio correctamente, false en caso contrario
     */
    public static boolean desasociarProductoProveedor(int productoId, int proveedorId) {
        try (Connection conn = getConexion();
                PreparedStatement stmt = conn.prepareStatement(
                        "UPDATE ProductoProveedor SET EsActivo = false WHERE ProductoID = ? AND ProveedorID = ?")) {

            stmt.setInt(1, productoId);
            stmt.setInt(2, proveedorId);

            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al desasociar producto de proveedor: " + e.getMessage());
        }
        return false;
    }

    // Inner class to hold product-provider association data
    public static class ProductoProveedorInfo {
        private int productoId;
        private String codigoProducto;
        private String nombre;
        private String descripcion;
        private double precioVenta;
        private int cantidadDisponible;
        private boolean esActivo;
        private double precioCompra;
        private int tiempoEntrega;
        private boolean esPreferido;

        // Constructor
        public ProductoProveedorInfo(int productoId, String codigoProducto, String nombre, String descripcion,
                double precioVenta, int cantidadDisponible, boolean esActivo, double precioCompra, int tiempoEntrega,
                boolean esPreferido) {
            this.productoId = productoId;
            this.codigoProducto = codigoProducto;
            this.nombre = nombre;
            this.descripcion = descripcion;
            this.precioVenta = precioVenta;
            this.cantidadDisponible = cantidadDisponible;
            this.esActivo = esActivo;
            this.precioCompra = precioCompra;
            this.tiempoEntrega = tiempoEntrega;
            this.esPreferido = esPreferido;
        }

        // Getters
        public int getProductoId() {
            return productoId;
        }

        public String getCodigoProducto() {
            return codigoProducto;
        }

        public String getNombre() {
            return nombre;
        }

        public String getDescripcion() {
            return descripcion;
        }

        public double getPrecioVenta() {
            return precioVenta;
        }

        public int getCantidadDisponible() {
            return cantidadDisponible;
        }

        public boolean isEsActivo() {
            return esActivo;
        }

        public double getPrecioCompra() {
            return precioCompra;
        }

        public int getTiempoEntrega() {
            return tiempoEntrega;
        }

        public boolean isEsPreferido() {
            return esPreferido;
        }
    }

    /**
     * Obtiene los productos asociados a un proveedor con información de precios
     * 
     * @param proveedorId ID del proveedor
     * @return Lista de productos asociados con información de precios
     * @throws SQLException Si hay error en la consulta
     */
    public static List<ProductoProveedorInfo> obtenerProductosProveedorConPrecios(int proveedorId) throws SQLException {
        List<ProductoProveedorInfo> productos = new ArrayList<>();

        try (Connection conn = getConexion();
                PreparedStatement stmt = conn.prepareStatement(
                        "SELECT p.*, pp.PrecioCompra, pp.TiempoEntrega, pp.EsPreferido " +
                                "FROM Producto p " +
                                "INNER JOIN ProductoProveedor pp ON p.ID = pp.ProductoID " +
                                "WHERE pp.ProveedorID = ? AND pp.EsActivo = true AND p.EsActivo = true " +
                                "ORDER BY p.Nombre")) {

            stmt.setInt(1, proveedorId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ProductoProveedorInfo info = new ProductoProveedorInfo(
                        rs.getInt("ID"),
                        rs.getString("CodigoProducto"),
                        rs.getString("Nombre"),
                        rs.getString("Descripcion"),
                        rs.getDouble("PrecioVenta"),
                        rs.getInt("CantidadDisponible"),
                        rs.getBoolean("EsActivo"),
                        rs.getDouble("PrecioCompra"),
                        rs.getInt("TiempoEntrega"),
                        rs.getBoolean("EsPreferido"));
                productos.add(info);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener productos por proveedor con precios: " + e.getMessage());
            throw e;
        }

        return productos;
    }
}
