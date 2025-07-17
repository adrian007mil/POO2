package com.inventario.vista;

import com.inventario.dao.ConexionBD;
import com.inventario.modelo.OrdenDeEntrada;
import com.inventario.modelo.EstadoOrden;
import com.inventario.modelo.Proveedor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Formulario para mostrar y gestionar las órdenes de compra
 */
public class FormularioMostrarOrdenes extends JFrame {

    // Componentes principales
    private JTable tablaOrdenes;
    private JTable tablaDetalles;
    private DefaultTableModel modeloOrdenes;
    private DefaultTableModel modeloDetalles;
    private JTextField txtBuscar;
    private JComboBox<String> cbFiltroEstado;
    private JButton btnVer;
    private JButton btnEnviar;
    private JButton btnRecibir;
    private JLabel lblTotal;
    private JLabel lblContador;

    // Datos
    private List<OrdenDeEntrada> ordenes;
    private OrdenDeEntrada ordenSeleccionada;

    public FormularioMostrarOrdenes() {
        initComponents();
        setupTables();
        cargarOrdenes();
        setupEventListeners();
    }

    private void initComponents() {
        setTitle("Órdenes de Compra - Sistema de Inventario");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Panel superior - Controles
        JPanel panelControles = createPanelControles();
        add(panelControles, BorderLayout.NORTH);

        // Panel central - Tablas
        JPanel panelTablas = createPanelTablas();
        add(panelTablas, BorderLayout.CENTER);

        // Panel inferior - Información
        JPanel panelInfo = createPanelInfo();
        add(panelInfo, BorderLayout.SOUTH);
    }

    private JPanel createPanelControles() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(new Color(240, 248, 255));
        panel.setBorder(BorderFactory.createTitledBorder("Controles"));

        // Campo de búsqueda
        panel.add(new JLabel("Buscar:"));
        txtBuscar = new JTextField(20);
        txtBuscar.setPreferredSize(new Dimension(200, 30));
        panel.add(txtBuscar);

        // Filtro por estado
        panel.add(new JLabel("Estado:"));
        cbFiltroEstado = new JComboBox<>(new String[] {
                "TODOS", "PENDIENTE", "ENVIADO", "APROBADA", "RECIBIDA", "PARCIAL", "CANCELADA"
        });
        cbFiltroEstado.setPreferredSize(new Dimension(120, 30));
        panel.add(cbFiltroEstado);

        // Botones
        btnVer = new JButton("Ver Detalles");
        btnVer.setBackground(new Color(76, 175, 80));
        btnVer.setForeground(Color.WHITE);
        btnVer.setPreferredSize(new Dimension(120, 30));
        btnVer.setEnabled(false);
        panel.add(btnVer);

        // Separador
        panel.add(new JLabel(" | "));

        btnEnviar = new JButton("Enviar a Proveedor");
        btnEnviar.setBackground(new Color(255, 193, 7));
        btnEnviar.setForeground(Color.BLACK);
        btnEnviar.setPreferredSize(new Dimension(150, 30));
        btnEnviar.setEnabled(false);
        panel.add(btnEnviar);

        btnRecibir = new JButton("Recibir Orden");
        btnRecibir.setBackground(new Color(139, 195, 74));
        btnRecibir.setForeground(Color.WHITE);
        btnRecibir.setPreferredSize(new Dimension(120, 30));
        btnRecibir.setEnabled(false);
        panel.add(btnRecibir);

        return panel;
    }

    private JPanel createPanelTablas() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Tabla de órdenes
        JPanel panelOrdenes = new JPanel(new BorderLayout());
        panelOrdenes.setBorder(BorderFactory.createTitledBorder("Órdenes de Compra"));

        tablaOrdenes = new JTable();
        tablaOrdenes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaOrdenes.setRowHeight(25);
        tablaOrdenes.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollOrdenes = new JScrollPane(tablaOrdenes);
        scrollOrdenes.setPreferredSize(new Dimension(1150, 250));
        panelOrdenes.add(scrollOrdenes, BorderLayout.CENTER);

        // Tabla de detalles
        JPanel panelDetalles = new JPanel(new BorderLayout());
        panelDetalles.setBorder(BorderFactory.createTitledBorder("Detalles de la Orden Seleccionada"));

        tablaDetalles = new JTable();
        tablaDetalles.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaDetalles.setRowHeight(25);
        tablaDetalles.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollDetalles = new JScrollPane(tablaDetalles);
        scrollDetalles.setPreferredSize(new Dimension(1150, 250));
        panelDetalles.add(scrollDetalles, BorderLayout.CENTER);

        panel.add(panelOrdenes);
        panel.add(panelDetalles);

        return panel;
    }

    private JPanel createPanelInfo() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createEtchedBorder());

        lblContador = new JLabel("Total de órdenes: 0");
        lblContador.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(lblContador);

        panel.add(new JLabel("  |  "));

        lblTotal = new JLabel("Valor total: S/ 0.00");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 12));
        lblTotal.setForeground(new Color(25, 118, 210));
        panel.add(lblTotal);

        return panel;
    }

    private void setupTables() {
        // Configurar modelo de tabla de órdenes
        String[] columnasOrdenes = {
                "ID", "Código", "Proveedor", "Fecha", "Estado", "Monto Total", "Usuario", "Observaciones"
        };
        modeloOrdenes = new DefaultTableModel(columnasOrdenes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaOrdenes.setModel(modeloOrdenes);

        // Configurar ancho de columnas de órdenes
        int[] anchosOrdenes = { 50, 100, 150, 120, 100, 100, 100, 200 };
        for (int i = 0; i < anchosOrdenes.length; i++) {
            tablaOrdenes.getColumnModel().getColumn(i).setPreferredWidth(anchosOrdenes[i]);
        }

        // Configurar modelo de tabla de detalles
        String[] columnasDetalles = {
                "ID", "Producto", "Código", "Cantidad", "Precio Unit.", "Subtotal", "Cant. Recibida"
        };
        modeloDetalles = new DefaultTableModel(columnasDetalles, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaDetalles.setModel(modeloDetalles);

        // Configurar ancho de columnas de detalles
        int[] anchosDetalles = { 50, 200, 100, 80, 100, 100, 100 };
        for (int i = 0; i < anchosDetalles.length; i++) {
            tablaDetalles.getColumnModel().getColumn(i).setPreferredWidth(anchosDetalles[i]);
        }

        // Configurar ordenamiento
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(modeloOrdenes);
        tablaOrdenes.setRowSorter(sorter);
    }

    private void setupEventListeners() {
        // Listener para selección de orden
        tablaOrdenes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int filaSeleccionada = tablaOrdenes.getSelectedRow();
                if (filaSeleccionada >= 0) {
                    int filaModelo = tablaOrdenes.convertRowIndexToModel(filaSeleccionada);
                    if (filaModelo >= 0 && filaModelo < ordenes.size()) {
                        ordenSeleccionada = ordenes.get(filaModelo);
                        cargarDetallesOrden(ordenSeleccionada);
                        btnVer.setEnabled(true);
                        actualizarEstadoBotones();
                    }
                } else {
                    limpiarDetalles();
                    btnVer.setEnabled(false);
                    btnEnviar.setEnabled(false);
                    btnRecibir.setEnabled(false);
                }
            }
        });

        // Listener para búsqueda
        txtBuscar.addActionListener(e -> filtrarOrdenes());

        // Listener para filtro de estado
        cbFiltroEstado.addActionListener(e -> filtrarOrdenes());

        // Listener para botón ver detalles
        btnVer.addActionListener(e -> verDetallesOrden());

        // Listener para botón enviar
        btnEnviar.addActionListener(e -> enviarOrdenAProveedor());

        // Listener para botón recibir
        btnRecibir.addActionListener(e -> recibirOrden());
    }

    private void cargarOrdenes() {
        ordenes = new ArrayList<>();
        modeloOrdenes.setRowCount(0);

        String sql = "SELECT oe.*, p.Nombre as ProveedorNombre, p.RUC " +
                "FROM OrdenEntrada oe " +
                "INNER JOIN Proveedor p ON oe.ProveedorID = p.ID " +
                "WHERE oe.EsActivo = true " +
                "ORDER BY oe.FechaOrden DESC";

        try (Connection conn = ConexionBD.getConexion();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            double montoTotal = 0;

            while (rs.next()) {
                // Crear objeto OrdenDeEntrada
                OrdenDeEntrada orden = new OrdenDeEntrada();
                orden.setCodigoOrden(rs.getString("CodigoOrden"));
                orden.setEstado(EstadoOrden.valueOf(rs.getString("EstadoOrden")));
                orden.setUsuarioCreador(rs.getString("UsuarioCreacion"));

                // Crear objeto Proveedor
                Proveedor proveedor = new Proveedor();
                proveedor.setNombre(rs.getString("ProveedorNombre"));
                proveedor.setRuc(rs.getString("RUC"));
                orden.setProveedor(proveedor);

                ordenes.add(orden);

                // Agregar fila a la tabla
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                double montoOrden = rs.getDouble("MontoTotal");
                Object[] fila = {
                        rs.getInt("ID"),
                        rs.getString("CodigoOrden"),
                        rs.getString("ProveedorNombre"),
                        sdf.format(rs.getTimestamp("FechaOrden")),
                        rs.getString("EstadoOrden"),
                        String.format("S/ %.2f", montoOrden),
                        rs.getString("UsuarioCreacion"),
                        rs.getString("Observaciones")
                };
                modeloOrdenes.addRow(fila);

                montoTotal += montoOrden;
            }

            // Actualizar información
            lblContador.setText("Total de órdenes: " + ordenes.size());
            lblTotal.setText(String.format("Valor total: S/ %.2f", montoTotal));

        } catch (SQLException e) {
            System.err.println("Error al cargar órdenes: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                    "Error al cargar las órdenes: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarDetallesOrden(OrdenDeEntrada orden) {
        modeloDetalles.setRowCount(0);

        String sql = "SELECT doe.*, p.Nombre as ProductoNombre, p.CodigoProducto " +
                "FROM DetalleOrdenEntrada doe " +
                "INNER JOIN Producto p ON doe.ProductoID = p.ID " +
                "INNER JOIN OrdenEntrada oe ON doe.OrdenEntradaID = oe.ID " +
                "WHERE oe.CodigoOrden = ? " +
                "ORDER BY p.Nombre";

        try (Connection conn = ConexionBD.getConexion();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, orden.getCodigoOrden());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Object[] fila = {
                        rs.getInt("ID"),
                        rs.getString("ProductoNombre"),
                        rs.getString("CodigoProducto"),
                        rs.getInt("Cantidad"),
                        String.format("S/ %.2f", rs.getDouble("PrecioUnitario")),
                        String.format("S/ %.2f", rs.getDouble("Subtotal")),
                        rs.getInt("CantidadRecibida")
                };
                modeloDetalles.addRow(fila);
            }

        } catch (SQLException e) {
            System.err.println("Error al cargar detalles: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                    "Error al cargar los detalles: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filtrarOrdenes() {
        String textoBuscar = txtBuscar.getText().toLowerCase();
        String estadoSeleccionado = (String) cbFiltroEstado.getSelectedItem();

        @SuppressWarnings("unchecked")
        TableRowSorter<DefaultTableModel> sorter = (TableRowSorter<DefaultTableModel>) tablaOrdenes.getRowSorter();

        if (textoBuscar.isEmpty() && "TODOS".equals(estadoSeleccionado)) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(new RowFilter<DefaultTableModel, Object>() {
                @Override
                public boolean include(Entry<? extends DefaultTableModel, ? extends Object> entry) {
                    // Filtrar por texto de búsqueda
                    boolean coincideTexto = textoBuscar.isEmpty();
                    if (!textoBuscar.isEmpty()) {
                        for (int i = 0; i < entry.getValueCount(); i++) {
                            if (entry.getStringValue(i).toLowerCase().contains(textoBuscar)) {
                                coincideTexto = true;
                                break;
                            }
                        }
                    }

                    // Filtrar por estado
                    boolean coincideEstado = "TODOS".equals(estadoSeleccionado) ||
                            entry.getStringValue(4).equals(estadoSeleccionado);

                    return coincideTexto && coincideEstado;
                }
            });
        }
    }

    private void verDetallesOrden() {
        if (ordenSeleccionada == null)
            return;

        JDialog dialog = new JDialog(this, "Detalles de la Orden", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Información de la orden
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Código:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(ordenSeleccionada.getCodigoOrden()), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Proveedor:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(ordenSeleccionada.getProveedor().getNombre()), gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Estado:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(ordenSeleccionada.getEstado().name()), gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Monto Total:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(String.format("S/ %.2f", ordenSeleccionada.getMontoTotal())), gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Usuario:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(ordenSeleccionada.getUsuarioCreador()), gbc);

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dialog.dispose());
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(btnCerrar, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    /**
     * Actualiza el estado de los botones según el estado de la orden seleccionada
     */
    private void actualizarEstadoBotones() {
        if (ordenSeleccionada == null) {
            btnEnviar.setEnabled(false);
            btnRecibir.setEnabled(false);
            return;
        }

        EstadoOrden estado = ordenSeleccionada.getEstado();

        // Botón enviar: solo habilitado si está PENDIENTE
        btnEnviar.setEnabled(EstadoOrden.PENDIENTE.equals(estado));

        // Botón recibir: solo habilitado si está ENVIADO
        btnRecibir.setEnabled(EstadoOrden.ENVIADO.equals(estado));
    }

    /**
     * Envía la orden al proveedor (cambia estado de PENDIENTE a ENVIADO)
     */
    private void enviarOrdenAProveedor() {
        if (ordenSeleccionada == null)
            return;

        if (!EstadoOrden.PENDIENTE.equals(ordenSeleccionada.getEstado())) {
            JOptionPane.showMessageDialog(this,
                    "Solo se pueden enviar órdenes en estado PENDIENTE",
                    "Estado incorrecto",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de que desea enviar la orden " + ordenSeleccionada.getCodigoOrden() + " al proveedor?",
                "Confirmar envío",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (opcion == JOptionPane.YES_OPTION) {
            // Simular envío usando el método de la clase
            boolean enviado = ordenSeleccionada.enviarProductoAProveedor();

            if (enviado) {
                // Actualizar estado en la base de datos
                if (actualizarEstadoOrdenEnBD(ordenSeleccionada.getCodigoOrden(), EstadoOrden.ENVIADO)) {
                    JOptionPane.showMessageDialog(this,
                            "Orden enviada a proveedor exitosamente",
                            "Envío Exitoso",
                            JOptionPane.INFORMATION_MESSAGE);

                    // Recargar datos y actualizar botones
                    cargarOrdenes();
                    actualizarEstadoBotones();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Error al actualizar el estado en la base de datos",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "No se pudo enviar la orden. Verifique el estado.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Recibe la orden del proveedor (cambia estado de ENVIADO a RECIBIDA y
     * actualiza stocks)
     */
    private void recibirOrden() {
        if (ordenSeleccionada == null)
            return;

        if (!EstadoOrden.ENVIADO.equals(ordenSeleccionada.getEstado())) {
            JOptionPane.showMessageDialog(this,
                    "Solo se pueden recibir órdenes en estado ENVIADO",
                    "Estado incorrecto",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de que desea recibir la orden " + ordenSeleccionada.getCodigoOrden() + " en almacén?\n" +
                        "Esto actualizará los stocks de todos los productos de la orden.",
                "Confirmar recepción",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (opcion == JOptionPane.YES_OPTION) {
            try {
                // Cargar los productos y cantidades de la orden desde la BD
                List<DetalleOrden> detalles = cargarDetallesOrdenParaRecepcion(ordenSeleccionada.getCodigoOrden());

                boolean todosRecibidos = true;
                StringBuilder productosActualizados = new StringBuilder();

                // Procesar cada producto de la orden
                for (DetalleOrden detalle : detalles) {
                    // Actualizar stock en la base de datos
                    boolean stockActualizado = ConexionBD.actualizarStock(
                            detalle.getCodigoProducto(),
                            detalle.getCantidadExistente() + detalle.getCantidad());

                    if (stockActualizado) {
                        productosActualizados.append("• ")
                                .append(detalle.getNombreProducto())
                                .append(": +")
                                .append(detalle.getCantidad())
                                .append(" unidades\n");
                    } else {
                        todosRecibidos = false;
                        break;
                    }
                }

                if (todosRecibidos) {
                    // Actualizar estado de la orden a RECIBIDA
                    if (actualizarEstadoOrdenEnBD(ordenSeleccionada.getCodigoOrden(), EstadoOrden.RECIBIDA)) {
                        ordenSeleccionada.setEstado(EstadoOrden.RECIBIDA);

                        // Mostrar mensaje de confirmación con detalles
                        JOptionPane.showMessageDialog(this,
                                "Orden de compra recibida en almacén exitosamente\n\n" +
                                        "Productos actualizados:\n" + productosActualizados.toString(),
                                "Recepción Exitosa",
                                JOptionPane.INFORMATION_MESSAGE);

                        // Recargar datos y actualizar botones
                        cargarOrdenes();
                        actualizarEstadoBotones();
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "Error al actualizar el estado de la orden en la base de datos",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Error al actualizar algunos stocks. Operación cancelada.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }

            } catch (Exception e) {
                System.err.println("Error al recibir orden: " + e.getMessage());
                JOptionPane.showMessageDialog(this,
                        "Error al procesar la recepción: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Actualiza el estado de una orden en la base de datos
     */
    private boolean actualizarEstadoOrdenEnBD(String codigoOrden, EstadoOrden nuevoEstado) {
        String sql = "UPDATE OrdenEntrada SET EstadoOrden = ? WHERE CodigoOrden = ?";

        try (Connection conn = ConexionBD.getConexion();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nuevoEstado.name());
            stmt.setString(2, codigoOrden);

            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar estado de orden: " + e.getMessage());
            return false;
        }
    }

    /**
     * Carga los detalles de una orden para el procesamiento de recepción
     */
    private List<DetalleOrden> cargarDetallesOrdenParaRecepcion(String codigoOrden) throws SQLException {
        List<DetalleOrden> detalles = new ArrayList<>();

        String sql = "SELECT doe.Cantidad, p.CodigoProducto, p.Nombre, p.CantidadDisponible " +
                "FROM DetalleOrdenEntrada doe " +
                "INNER JOIN Producto p ON doe.ProductoID = p.ID " +
                "INNER JOIN OrdenEntrada oe ON doe.OrdenEntradaID = oe.ID " +
                "WHERE oe.CodigoOrden = ?";

        try (Connection conn = ConexionBD.getConexion();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, codigoOrden);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                DetalleOrden detalle = new DetalleOrden();
                detalle.setCantidad(rs.getInt("Cantidad"));
                detalle.setCodigoProducto(rs.getString("CodigoProducto"));
                detalle.setNombreProducto(rs.getString("Nombre"));
                detalle.setCantidadExistente(rs.getInt("CantidadDisponible"));
                detalles.add(detalle);
            }

        }

        return detalles;
    }

    /**
     * Clase auxiliar para manejar detalles de orden durante la recepción
     */
    private static class DetalleOrden {
        private int cantidad;
        private String codigoProducto;
        private String nombreProducto;
        private int cantidadExistente;

        public int getCantidad() {
            return cantidad;
        }

        public void setCantidad(int cantidad) {
            this.cantidad = cantidad;
        }

        public String getCodigoProducto() {
            return codigoProducto;
        }

        public void setCodigoProducto(String codigoProducto) {
            this.codigoProducto = codigoProducto;
        }

        public String getNombreProducto() {
            return nombreProducto;
        }

        public void setNombreProducto(String nombreProducto) {
            this.nombreProducto = nombreProducto;
        }

        public int getCantidadExistente() {
            return cantidadExistente;
        }

        public void setCantidadExistente(int cantidadExistente) {
            this.cantidadExistente = cantidadExistente;
        }
    }

    private void limpiarDetalles() {
        modeloDetalles.setRowCount(0);
        ordenSeleccionada = null;
        btnEnviar.setEnabled(false);
        btnRecibir.setEnabled(false);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new FormularioMostrarOrdenes().setVisible(true);
        });
    }
}
