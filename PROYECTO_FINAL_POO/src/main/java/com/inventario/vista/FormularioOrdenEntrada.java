package com.inventario.vista;

import com.inventario.dao.ConexionBD;
import com.inventario.modelo.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Formulario para el registro de órdenes de entrada con gestión de productos
 * 
 * @author El~Nicolays
 */
public class FormularioOrdenEntrada extends javax.swing.JFrame {

    private DefaultTableModel modeloTablaProductosProveedor;
    private DefaultTableModel modeloTablaItemsOrden;
    private OrdenDeEntrada ordenActual;

    // Componentes del formulario
    private JTextField txtCodigoOrden;
    private JComboBox<Proveedor> cmbProveedor;
    private JTextField txtUsuarioCreador;
    private JTextField txtEstado;
    private JLabel lblFechaOrden;
    private JLabel lblMontoTotal;

    // Componentes para agregar productos
    private JTable tablaProductosProveedor;
    private JTable tablaItemsOrden;
    private JScrollPane scrollProductosProveedor;
    private JScrollPane scrollItemsOrden;
    private JSpinner spinnerCantidad;
    private JButton btnAgregarProducto;
    private JButton btnRemoverProducto;

    // Botones principales
    private JButton btnGuardarOrden;
    private JButton btnLimpiarOrden;
    private JButton btnVolver;
    private JButton btnCalcularTotal;

    /**
     * Creates new form FormularioOrdenEntrada
     */
    public FormularioOrdenEntrada() {
        initComponents();
        setupCustomDesign();
        cargarProveedores();
        inicializarOrdenActual();
    }

    /**
     * Inicializa los componentes del formulario
     */
    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Registro de Orden de Entrada - Sistema de Inventario");
        setResizable(false);
        // Inicializar componentes principales
        txtCodigoOrden = new JTextField(20);
        cmbProveedor = new JComboBox<>();
        txtUsuarioCreador = new JTextField("admin", 15);
        txtEstado = new JTextField("PENDIENTE", 15);
        lblFechaOrden = new JLabel(LocalDateTime.now().toString());
        lblMontoTotal = new JLabel("S/ 0.00");

        // Configurar campos no editables
        txtUsuarioCreador.setEditable(false);
        txtEstado.setEditable(false);

        // Componentes para productos
        spinnerCantidad = new JSpinner(new SpinnerNumberModel(1, 1, 99999, 1));
        btnAgregarProducto = new JButton("Agregar Producto");
        btnRemoverProducto = new JButton("Remover Producto");

        // Botones principales
        btnGuardarOrden = new JButton("Guardar Orden");
        btnLimpiarOrden = new JButton("Limpiar Formulario");
        btnVolver = new JButton("Volver");
        btnCalcularTotal = new JButton("Calcular Total");

        // Configurar tablas
        setupTables();

        // Configurar eventos
        setupEventHandlers();

        // Layout
        setupLayout();
    }

    /**
     * Configura las tablas del formulario
     */
    private void setupTables() {
        // Tabla de productos del proveedor
        String[] columnasProductos = { "ID", "Código", "Nombre", "Descripción", "Precio" };
        modeloTablaProductosProveedor = new DefaultTableModel(columnasProductos, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaProductosProveedor = new JTable(modeloTablaProductosProveedor);
        scrollProductosProveedor = new JScrollPane(tablaProductosProveedor);

        // Tabla de items de la orden
        String[] columnasItems = { "Código", "Nombre", "Cantidad", "Precio Unit.", "Subtotal" };
        modeloTablaItemsOrden = new DefaultTableModel(columnasItems, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaItemsOrden = new JTable(modeloTablaItemsOrden);
        scrollItemsOrden = new JScrollPane(tablaItemsOrden);

        // Configurar selección de tablas
        tablaProductosProveedor.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaItemsOrden.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    /**
     * Configura los event handlers
     */
    private void setupEventHandlers() {
        // Evento para cambio de proveedor
        cmbProveedor.addActionListener(e -> {
            if (cmbProveedor.getSelectedItem() != null) {
                cargarProductosProveedor();
            }
        });

        // Evento para agregar producto
        btnAgregarProducto.addActionListener(e -> agregarProductoAOrden());

        // Evento para remover producto
        btnRemoverProducto.addActionListener(e -> removerProductoDeOrden());

        // Evento para calcular total
        btnCalcularTotal.addActionListener(e -> calcularTotalOrden());

        // Evento para guardar orden
        btnGuardarOrden.addActionListener(e -> guardarOrden());

        // Evento para limpiar formulario
        btnLimpiarOrden.addActionListener(e -> limpiarFormulario());

        // Evento para volver
        btnVolver.addActionListener(e -> {
            dispose();
        });

        // Evento para auto-generar código de orden
        txtCodigoOrden.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (txtCodigoOrden.getText().trim().isEmpty()) {
                    generarCodigoOrden();
                }
            }
        });
    }

    /**
     * Configura el layout del formulario
     */
    private void setupLayout() {
        setLayout(new BorderLayout());

        // Panel principal
        JPanel panelPrincipal = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Panel de información de la orden
        JPanel panelInfoOrden = crearPanelInfoOrden();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        panelPrincipal.add(panelInfoOrden, gbc);

        // Panel de productos del proveedor
        JPanel panelProductosProveedor = crearPanelProductosProveedor();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.5;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panelPrincipal.add(panelProductosProveedor, gbc);

        // Panel de items de la orden
        JPanel panelItemsOrden = crearPanelItemsOrden();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.5;
        gbc.weighty = 1.0;
        panelPrincipal.add(panelItemsOrden, gbc);

        // Panel de botones
        JPanel panelBotones = crearPanelBotones();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelPrincipal.add(panelBotones, gbc);

        add(panelPrincipal, BorderLayout.CENTER);

        // Configurar tamaño de ventana
        setSize(1200, 700);
        setLocationRelativeTo(null);
    }

    /**
     * Crea el panel de información de la orden
     */
    private JPanel crearPanelInfoOrden() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Información de la Orden",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new Font("Arial", Font.BOLD, 14),
                new Color(63, 81, 181)));
        panel.setBackground(new Color(245, 245, 245));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // Fila 1
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(crearLabel("Código Orden:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.3;
        panel.add(txtCodigoOrden, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(crearLabel("Proveedor:"), gbc);
        gbc.gridx = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.7;
        cmbProveedor.setPreferredSize(new Dimension(300, 25));
        panel.add(cmbProveedor, gbc);

        // Fila 2
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        panel.add(crearLabel("Usuario Creador:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.3;
        panel.add(txtUsuarioCreador, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(crearLabel("Estado:"), gbc);
        gbc.gridx = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.3;
        panel.add(txtEstado, gbc);

        // Fila 3
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        panel.add(crearLabel("Fecha Orden:"), gbc);
        gbc.gridx = 1;
        panel.add(lblFechaOrden, gbc);

        gbc.gridx = 2;
        panel.add(crearLabel("Monto Total:"), gbc);
        gbc.gridx = 3;
        JLabel lblMontoFormateado = new JLabel();
        lblMontoFormateado.setText(lblMontoTotal.getText());
        lblMontoFormateado.setFont(new Font("Arial", Font.BOLD, 14));
        lblMontoFormateado.setForeground(new Color(76, 175, 80));
        panel.add(lblMontoFormateado, gbc);

        // Actualizar referencia para que se actualice correctamente
        lblMontoTotal = lblMontoFormateado;

        return panel;
    }

    /**
     * Crea un label con estilo consistente
     */
    private JLabel crearLabel(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        label.setForeground(new Color(33, 37, 41));
        return label;
    }

    /**
     * Crea el panel de productos del proveedor
     */
    private JPanel crearPanelProductosProveedor() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Productos del Proveedor",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new Font("Arial", Font.BOLD, 14),
                new Color(63, 81, 181)));
        panel.setBackground(new Color(245, 245, 245));

        // Panel superior con controles
        JPanel panelControles = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelControles.setBackground(new Color(245, 245, 245));
        panelControles.add(crearLabel("Cantidad:"));
        panelControles.add(spinnerCantidad);
        panelControles.add(btnAgregarProducto);

        panel.add(panelControles, BorderLayout.NORTH);
        panel.add(scrollProductosProveedor, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Crea el panel de items de la orden
     */
    private JPanel crearPanelItemsOrden() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Items de la Orden",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new Font("Arial", Font.BOLD, 14),
                new Color(63, 81, 181)));
        panel.setBackground(new Color(245, 245, 245));

        // Panel superior con controles
        JPanel panelControles = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelControles.setBackground(new Color(245, 245, 245));
        panelControles.add(btnRemoverProducto);
        panelControles.add(btnCalcularTotal);

        panel.add(panelControles, BorderLayout.NORTH);
        panel.add(scrollItemsOrden, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Crea el panel de botones principales
     */
    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.add(btnGuardarOrden);
        panel.add(btnLimpiarOrden);
        panel.add(btnVolver);

        return panel;
    }

    /**
     * Configura el diseño visual del formulario
     */
    private void setupCustomDesign() {
        // Configurar ventana principal
        setSize(1200, 700);
        getContentPane().setBackground(new Color(245, 245, 245));

        // Configurar fuentes
        Font fuenteTexto = new Font("Arial", Font.PLAIN, 12);

        // Configurar campos de texto
        txtCodigoOrden.setFont(fuenteTexto);
        txtUsuarioCreador.setFont(fuenteTexto);
        txtEstado.setFont(fuenteTexto);

        // Configurar campos no editables con color de fondo diferente
        txtUsuarioCreador.setBackground(new Color(240, 240, 240));
        txtEstado.setBackground(new Color(240, 240, 240));
        txtUsuarioCreador.setForeground(new Color(102, 102, 102));
        txtEstado.setForeground(new Color(102, 102, 102));

        // Configurar ComboBox
        cmbProveedor.setFont(fuenteTexto);
        cmbProveedor.setBackground(Color.WHITE);
        cmbProveedor.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Proveedor) {
                    Proveedor proveedor = (Proveedor) value;
                    setText(proveedor.getNombre() + " - " + proveedor.getRuc());
                }
                return this;
            }
        });

        // Configurar botones con colores consistentes
        configurarBoton(btnGuardarOrden, new Color(76, 175, 80), Color.WHITE);
        configurarBoton(btnLimpiarOrden, new Color(255, 193, 7), Color.BLACK);
        configurarBoton(btnVolver, new Color(108, 117, 125), Color.WHITE);
        configurarBoton(btnAgregarProducto, new Color(33, 150, 243), Color.WHITE);
        configurarBoton(btnRemoverProducto, new Color(244, 67, 54), Color.WHITE);
        configurarBoton(btnCalcularTotal, new Color(255, 152, 0), Color.WHITE);

        // Configurar tablas
        configurarTabla(tablaProductosProveedor);
        configurarTabla(tablaItemsOrden);

        // Configurar spinner
        spinnerCantidad.setFont(fuenteTexto);

        // Configurar labels de información
        lblFechaOrden.setFont(fuenteTexto);
        lblFechaOrden.setForeground(new Color(102, 102, 102));
    }

    /**
     * Configura la apariencia de un botón
     */
    private void configurarBoton(JButton boton, Color colorFondo, Color colorTexto) {
        boton.setBackground(colorFondo);
        boton.setForeground(colorTexto);
        boton.setFont(new Font("Arial", Font.BOLD, 12));
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    /**
     * Configura la apariencia de una tabla
     */
    private void configurarTabla(JTable tabla) {
        tabla.setFont(new Font("Arial", Font.PLAIN, 11));
        tabla.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tabla.getTableHeader().setBackground(new Color(63, 81, 181));
        tabla.getTableHeader().setForeground(Color.WHITE);
        tabla.setRowHeight(25);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setSelectionBackground(new Color(232, 245, 233));
        tabla.setSelectionForeground(Color.BLACK);
        tabla.setGridColor(new Color(224, 224, 224));
        tabla.setShowGrid(true);
    }

    /**
     * Carga los proveedores en el combo box
     */
    private void cargarProveedores() {
        try {
            List<Proveedor> proveedores = ConexionBD.obtenerProveedoresActivos();
            cmbProveedor.removeAllItems();
            for (Proveedor proveedor : proveedores) {
                cmbProveedor.addItem(proveedor);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar proveedores: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Carga los productos del proveedor seleccionado
     */
    private void cargarProductosProveedor() {
        Proveedor proveedorSeleccionado = (Proveedor) cmbProveedor.getSelectedItem();
        if (proveedorSeleccionado == null)
            return;

        try {
            // Limpiar tabla
            modeloTablaProductosProveedor.setRowCount(0);

            // Obtener productos del proveedor con precios
            List<ConexionBD.ProductoProveedorInfo> productos = ConexionBD
                    .obtenerProductosProveedorConPrecios(proveedorSeleccionado.getId());

            // Agregar productos a la tabla
            for (ConexionBD.ProductoProveedorInfo producto : productos) {
                Object[] fila = {
                        producto.getProductoId(),
                        producto.getCodigoProducto(),
                        producto.getNombre(),
                        producto.getDescripcion(),
                        String.format("S/ %.2f", producto.getPrecioCompra())
                };
                modeloTablaProductosProveedor.addRow(fila);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar productos del proveedor: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Inicializa una nueva orden actual
     */
    private void inicializarOrdenActual() {
        ordenActual = new OrdenDeEntrada();
        ordenActual.setUsuarioCreador("admin");
        ordenActual.setEstado(EstadoOrden.PENDIENTE);
        ordenActual.setFechaOrden(LocalDateTime.now());
        actualizarInterfazOrden();
    }

    /**
     * Actualiza la interfaz con los datos de la orden actual
     */
    private void actualizarInterfazOrden() {
        if (ordenActual != null) {
            txtUsuarioCreador.setText(ordenActual.getUsuarioCreador());
            txtEstado.setText(ordenActual.getEstado().getDescripcion());
            lblFechaOrden.setText(ordenActual.getFechaOrden().toString());
            lblMontoTotal.setText(String.format("S/ %.2f", ordenActual.getMontoTotal()));

            // Actualizar tabla de items
            actualizarTablaItems();
        }
    }

    /**
     * Actualiza la tabla de items de la orden
     */
    private void actualizarTablaItems() {
        modeloTablaItemsOrden.setRowCount(0);

        if (ordenActual != null) {
            for (ItemOrdenCompra item : ordenActual.getItems()) {
                Object[] fila = {
                        item.getProducto().getCodigoProducto(),
                        item.getProducto().getNombre(),
                        item.getCantidad(),
                        String.format("%.2f", item.getPrecioUnitario()),
                        String.format("%.2f", item.getSubtotal())
                };
                modeloTablaItemsOrden.addRow(fila);
            }
        }
    }

    /**
     * Genera un código único para la orden
     */
    private void generarCodigoOrden() {
        String codigo = "OE-" + System.currentTimeMillis();
        txtCodigoOrden.setText(codigo);
    }

    /**
     * Agrega un producto a la orden
     */
    private void agregarProductoAOrden() {
        int filaSeleccionada = tablaProductosProveedor.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                    "Por favor seleccione un producto de la lista",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validar cantidad
        int cantidad = (Integer) spinnerCantidad.getValue();
        if (cantidad <= 0) {
            JOptionPane.showMessageDialog(this,
                    "La cantidad debe ser mayor a 0",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Obtener datos del producto seleccionado
        int productoId = (Integer) modeloTablaProductosProveedor.getValueAt(filaSeleccionada, 0);
        String precioTexto = (String) modeloTablaProductosProveedor.getValueAt(filaSeleccionada, 4);

        // Extraer el precio del formato "S/ XX.XX"
        double precioUnitario;
        try {
            precioUnitario = Double.parseDouble(precioTexto.replace("S/ ", ""));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al obtener el precio del producto",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Producto producto = ConexionBD.obtenerProductoPorId(productoId);
            if (producto != null) {
                // Agregar el producto a la orden
                ordenActual.agregarItem(producto, cantidad, precioUnitario);

                // Actualizar interfaz
                actualizarInterfazOrden();

                // Limpiar campos
                spinnerCantidad.setValue(1);

                JOptionPane.showMessageDialog(this,
                        "Producto agregado a la orden exitosamente",
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al agregar producto: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Remueve un producto de la orden
     */
    private void removerProductoDeOrden() {
        int filaSeleccionada = tablaItemsOrden.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                    "Por favor seleccione un producto de la orden para remover",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String codigoProducto = (String) modeloTablaItemsOrden.getValueAt(filaSeleccionada, 0);

        int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de que desea remover este producto de la orden?",
                "Confirmar", JOptionPane.YES_NO_OPTION);

        if (respuesta == JOptionPane.YES_OPTION) {
            boolean removido = ordenActual.removerItem(codigoProducto);
            if (removido) {
                actualizarInterfazOrden();
                JOptionPane.showMessageDialog(this,
                        "Producto removido de la orden exitosamente",
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "No se pudo remover el producto",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Calcula el total de la orden
     */
    private void calcularTotalOrden() {
        if (ordenActual != null) {
            ordenActual.calcularTotales();
            lblMontoTotal.setText(String.format("S/ %.2f", ordenActual.getMontoTotal()));
        }
    }

    /**
     * Guarda la orden en la base de datos
     */
    private void guardarOrden() {
        // Validar datos
        if (txtCodigoOrden.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "El código de orden es requerido",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (cmbProveedor.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this,
                    "Debe seleccionar un proveedor",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (ordenActual.getItems().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "La orden debe tener al menos un producto",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Configurar orden
        ordenActual.setCodigoOrden(txtCodigoOrden.getText().trim());
        ordenActual.setProveedor((Proveedor) cmbProveedor.getSelectedItem());
        ordenActual.calcularTotales();

        try {
            // Guardar en base de datos
            boolean guardado = ConexionBD.insertarOrdenEntrada(ordenActual);

            if (guardado) {
                JOptionPane.showMessageDialog(this,
                        "Orden de entrada guardada exitosamente",
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                limpiarFormulario();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Error al guardar la orden",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al guardar la orden: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Limpia el formulario
     */
    private void limpiarFormulario() {
        txtCodigoOrden.setText("");
        cmbProveedor.setSelectedIndex(-1);
        spinnerCantidad.setValue(1);

        // Limpiar tablas
        modeloTablaProductosProveedor.setRowCount(0);
        modeloTablaItemsOrden.setRowCount(0);

        // Inicializar nueva orden
        inicializarOrdenActual();
    }

    /**
     * Método main para pruebas
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new FormularioOrdenEntrada().setVisible(true);
        });
    }
}
