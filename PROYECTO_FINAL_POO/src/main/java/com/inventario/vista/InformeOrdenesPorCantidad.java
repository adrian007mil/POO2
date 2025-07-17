package com.inventario.vista;

import com.inventario.dao.ConexionBD;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

/**
 * Formulario de informe que muestra las órdenes ordenadas por mayor cantidad de
 * productos
 */
public class InformeOrdenesPorCantidad extends JFrame {

    private JTable tablaOrdenes;
    private DefaultTableModel modeloTabla;
    private JLabel lblResumen;
    private JLabel lblTotal;

    public InformeOrdenesPorCantidad() {
        initComponents();
        cargarDatos();
    }

    private void initComponents() {
        setTitle("Informe: Órdenes por Mayor Cantidad de Productos");
        setSize(950, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Panel superior - Título
        JPanel panelTitulo = new JPanel();
        panelTitulo.setBackground(new Color(76, 175, 80));
        panelTitulo.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblTitulo = new JLabel("INFORME: ÓRDENES ORDENADAS POR MAYOR CANTIDAD DE PRODUCTOS");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        panelTitulo.add(lblTitulo);

        add(panelTitulo, BorderLayout.NORTH);

        // Panel central - Tabla
        JPanel panelTabla = new JPanel(new BorderLayout());
        panelTabla.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Crear tabla
        String[] columnas = { "Ranking", "Código Orden", "Proveedor", "Fecha", "Estado", "Cant. Productos",
                "Cant. Total Items", "Monto Total", "Usuario" };
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaOrdenes = new JTable(modeloTabla);
        tablaOrdenes.setRowHeight(25);
        tablaOrdenes.getTableHeader().setReorderingAllowed(false);
        tablaOrdenes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Configurar anchos de columnas
        int[] anchosColumnas = { 70, 120, 150, 100, 80, 100, 120, 100, 100 };
        for (int i = 0; i < anchosColumnas.length; i++) {
            tablaOrdenes.getColumnModel().getColumn(i).setPreferredWidth(anchosColumnas[i]);
        }

        JScrollPane scrollPane = new JScrollPane(tablaOrdenes);
        scrollPane.setPreferredSize(new Dimension(900, 400));
        panelTabla.add(scrollPane, BorderLayout.CENTER);

        add(panelTabla, BorderLayout.CENTER);

        // Panel inferior - Resumen
        JPanel panelResumen = createPanelResumen();
        add(panelResumen, BorderLayout.SOUTH);

        // Botón cerrar
        JPanel panelBoton = new JPanel();
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.setBackground(new Color(244, 67, 54));
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setPreferredSize(new Dimension(100, 35));
        btnCerrar.addActionListener(e -> dispose());
        panelBoton.add(btnCerrar);

        panelResumen.add(panelBoton, BorderLayout.SOUTH);
    }

    private JPanel createPanelResumen() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255));
        panel.setBorder(BorderFactory.createTitledBorder("Resumen del Informe"));

        // Panel información
        JPanel panelInfo = new JPanel(new GridLayout(2, 1, 5, 5));
        panelInfo.setBackground(new Color(240, 248, 255));
        panelInfo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        lblResumen = new JLabel("Total de órdenes: 0");
        lblResumen.setFont(new Font("Arial", Font.BOLD, 14));

        lblTotal = new JLabel("Total de productos ordenados: 0");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 14));
        lblTotal.setForeground(new Color(76, 175, 80));

        panelInfo.add(lblResumen);
        panelInfo.add(lblTotal);

        panel.add(panelInfo, BorderLayout.CENTER);

        return panel;
    }

    private void cargarDatos() {
        modeloTabla.setRowCount(0);

        String sql = "SELECT oe.CodigoOrden, p.Nombre as ProveedorNombre, oe.FechaOrden, " +
                "oe.EstadoOrden, oe.MontoTotal, oe.UsuarioCreacion, " +
                "COUNT(DISTINCT doe.ProductoID) as CantidadProductos, " +
                "SUM(doe.Cantidad) as CantidadTotalItems " +
                "FROM OrdenEntrada oe " +
                "INNER JOIN Proveedor p ON oe.ProveedorID = p.ID " +
                "INNER JOIN DetalleOrdenEntrada doe ON oe.ID = doe.OrdenEntradaID " +
                "WHERE oe.EsActivo = true " +
                "GROUP BY oe.ID, oe.CodigoOrden, p.Nombre, oe.FechaOrden, " +
                "oe.EstadoOrden, oe.MontoTotal, oe.UsuarioCreacion " +
                "ORDER BY CantidadTotalItems DESC, CantidadProductos DESC " +
                "LIMIT 20";

        try (Connection conn = ConexionBD.getConexion();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            int ranking = 1;
            int totalProductos = 0;
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

            while (rs.next()) {
                int cantidadProductos = rs.getInt("CantidadProductos");
                int cantidadTotalItems = rs.getInt("CantidadTotalItems");
                double monto = rs.getDouble("MontoTotal");

                totalProductos += cantidadTotalItems;

                Object[] fila = {
                        ranking++,
                        rs.getString("CodigoOrden"),
                        rs.getString("ProveedorNombre"),
                        sdf.format(rs.getDate("FechaOrden")),
                        rs.getString("EstadoOrden"),
                        cantidadProductos,
                        cantidadTotalItems,
                        String.format("S/ %.2f", monto),
                        rs.getString("UsuarioCreacion")
                };

                modeloTabla.addRow(fila);
            }

            // Actualizar resumen
            lblResumen.setText("Total de órdenes mostradas: " + (ranking - 1));
            lblTotal.setText("Total de productos ordenados: " + totalProductos);

        } catch (SQLException e) {
            System.err.println("Error al cargar datos del informe: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                    "Error al cargar los datos del informe: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new InformeOrdenesPorCantidad().setVisible(true);
        });
    }
}
