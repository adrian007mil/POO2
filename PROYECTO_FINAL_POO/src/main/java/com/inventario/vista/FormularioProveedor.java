package com.inventario.vista;

import com.inventario.dao.ConexionBD;
import com.inventario.modelo.Proveedor;
import com.inventario.modelo.Producto;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

/**
 * Formulario para la gestión de proveedores y asociación con productos
 * 
 * @author El~Nicolays
 */
public class FormularioProveedor extends javax.swing.JFrame {

    private DefaultTableModel modeloTablaProveedores;
    private DefaultTableModel modeloTablaProductosDisponibles;
    private DefaultTableModel modeloTablaProductosAsociados;

    // Componentes del formulario
    private JTextField txtCodigo;
    private JTextField txtNombre;
    private JTextField txtRuc;
    private JTextField txtTelefono;
    private JTextField txtEmail;
    private JTextArea txtDireccion;
    private JCheckBox chkEsActivo;
    private JButton btnGuardar;
    private JButton btnLimpiar;
    private JButton btnVolver;
    private JButton btnEliminar;
    private JButton btnModificar;
    private JButton btnAsociarProducto;
    private JButton btnDesasociarProducto;
    private JTable tablaProveedores;
    private JTable tablaProductosDisponibles;
    private JTable tablaProductosAsociados;
    private JScrollPane scrollTablaProveedores;
    private JScrollPane scrollTablaProductosDisponibles;
    private JScrollPane scrollTablaProductosAsociados;
    private JScrollPane scrollDireccion;
    private JTextField txtPrecioCompra;
    private JTextField txtTiempoEntrega;

    /**
     * Creates new form FormularioProveedor
     */
    public FormularioProveedor() {
        initComponents();
        setupCustomDesign();
        cargarProveedores();
        cargarProductosDisponibles();
    }

    /**
     * Inicializa los componentes del formulario
     */
    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Gestión de Proveedores - Sistema de Inventario");
        setResizable(false);
        setLocationRelativeTo(null);

        // Inicializar componentes
        txtCodigo = new JTextField();
        txtNombre = new JTextField();
        txtRuc = new JTextField();
        txtTelefono = new JTextField();
        txtEmail = new JTextField();
        txtDireccion = new JTextArea(3, 20);
        chkEsActivo = new JCheckBox("Proveedor activo");
        txtPrecioCompra = new JTextField();
        txtTiempoEntrega = new JTextField("7");

        btnGuardar = new JButton("Guardar");
        btnLimpiar = new JButton("Limpiar");
        btnVolver = new JButton("Volver");
        btnEliminar = new JButton("Eliminar");
        btnModificar = new JButton("Modificar");
        btnAsociarProducto = new JButton("Asociar Producto");
        btnDesasociarProducto = new JButton("Desasociar Producto");

        // Configurar tabla de proveedores
        String[] columnasProveedores = { "ID", "Código", "Nombre", "RUC", "Email" };
        modeloTablaProveedores = new DefaultTableModel(columnasProveedores, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaProveedores = new JTable(modeloTablaProveedores);
        scrollTablaProveedores = new JScrollPane(tablaProveedores);

        // Configurar tabla de productos disponibles
        String[] columnasProductosDisponibles = { "ID", "Código", "Nombre", "Precio Venta" };
        modeloTablaProductosDisponibles = new DefaultTableModel(columnasProductosDisponibles, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaProductosDisponibles = new JTable(modeloTablaProductosDisponibles);
        scrollTablaProductosDisponibles = new JScrollPane(tablaProductosDisponibles);

        // Configurar tabla de productos asociados
        String[] columnasProductosAsociados = { "ID", "Código", "Nombre", "Precio Compra", "Tiempo Entrega" };
        modeloTablaProductosAsociados = new DefaultTableModel(columnasProductosAsociados, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaProductosAsociados = new JTable(modeloTablaProductosAsociados);
        scrollTablaProductosAsociados = new JScrollPane(tablaProductosAsociados);

        scrollDireccion = new JScrollPane(txtDireccion);

        // Configurar eventos
        setupEventHandlers();

        // Layout
        setupLayout();
    }

    /**
     * Configura el diseño visual del formulario
     */
    private void setupCustomDesign() {
        // Configurar ventana
        setSize(1200, 800);
        getContentPane().setBackground(new Color(245, 245, 245));

        // Configurar campos de texto
        Font campoFont = new Font("Arial", Font.PLAIN, 12);
        txtCodigo.setFont(campoFont);
        txtNombre.setFont(campoFont);
        txtRuc.setFont(campoFont);
        txtTelefono.setFont(campoFont);
        txtEmail.setFont(campoFont);
        txtDireccion.setFont(campoFont);
        txtPrecioCompra.setFont(campoFont);
        txtTiempoEntrega.setFont(campoFont);

        // Configurar botones
        configurarBoton(btnGuardar, new Color(76, 175, 80), Color.WHITE);
        configurarBoton(btnLimpiar, new Color(255, 193, 7), Color.BLACK);
        configurarBoton(btnVolver, new Color(108, 117, 125), Color.WHITE);
        configurarBoton(btnEliminar, new Color(244, 67, 54), Color.WHITE);
        configurarBoton(btnModificar, new Color(33, 150, 243), Color.WHITE);
        configurarBoton(btnAsociarProducto, new Color(156, 39, 176), Color.WHITE);
        configurarBoton(btnDesasociarProducto, new Color(255, 87, 34), Color.WHITE);

        // Configurar tablas
        configurarTabla(tablaProveedores);
        configurarTabla(tablaProductosDisponibles);
        configurarTabla(tablaProductosAsociados);

        // Configurar checkbox
        chkEsActivo.setSelected(true);
        chkEsActivo.setFont(campoFont);
        chkEsActivo.setBackground(new Color(245, 245, 245));

        // Configurar scrolls
        scrollTablaProveedores.setPreferredSize(new Dimension(550, 200));
        scrollTablaProductosDisponibles.setPreferredSize(new Dimension(550, 200));
        scrollTablaProductosAsociados.setPreferredSize(new Dimension(550, 200));
        scrollDireccion.setPreferredSize(new Dimension(300, 60));
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
    }

    /**
     * Configura los manejadores de eventos
     */
    private void setupEventHandlers() {
        btnGuardar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                guardarProveedor();
            }
        });

        btnLimpiar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                limpiarCampos();
            }
        });

        btnVolver.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        btnEliminar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eliminarProveedor();
            }
        });

        btnModificar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modificarProveedor();
            }
        });

        btnAsociarProducto.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                asociarProducto();
            }
        });

        btnDesasociarProducto.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                desasociarProducto();
            }
        });

        // Evento para seleccionar fila de la tabla de proveedores
        tablaProveedores.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarProveedorSeleccionado();
            }
        });
    }

    /**
     * Configura el layout del formulario
     */
    private void setupLayout() {
        setLayout(new BorderLayout());

        // Panel principal
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(new Color(245, 245, 245));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel superior con formulario y tabla de proveedores
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.add(crearPanelFormulario(), BorderLayout.WEST);
        panelSuperior.add(crearPanelTablaProveedores(), BorderLayout.CENTER);

        // Panel inferior con productos
        JPanel panelInferior = crearPanelProductos();

        // Panel de botones
        JPanel panelBotones = crearPanelBotones();

        // Agregar paneles al panel principal
        panelPrincipal.add(panelSuperior, BorderLayout.NORTH);
        panelPrincipal.add(panelInferior, BorderLayout.CENTER);
        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);

        add(panelPrincipal, BorderLayout.CENTER);
    }

    /**
     * Crea el panel del formulario
     */
    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(63, 81, 181), 2),
                "Datos del Proveedor",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14),
                new Color(63, 81, 181)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Fila 1: Código
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Código:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        txtCodigo.setPreferredSize(new Dimension(200, 25));
        panel.add(txtCodigo, gbc);

        // Fila 2: Nombre
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        txtNombre.setPreferredSize(new Dimension(200, 25));
        panel.add(txtNombre, gbc);

        // Fila 3: RUC
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("RUC:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        txtRuc.setPreferredSize(new Dimension(200, 25));
        panel.add(txtRuc, gbc);

        // Fila 4: Teléfono
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Teléfono:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        txtTelefono.setPreferredSize(new Dimension(200, 25));
        panel.add(txtTelefono, gbc);

        // Fila 5: Email
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        txtEmail.setPreferredSize(new Dimension(200, 25));
        panel.add(txtEmail, gbc);

        // Fila 6: Dirección
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Dirección:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(scrollDireccion, gbc);

        // Fila 7: Estado
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(chkEsActivo, gbc);

        return panel;
    }

    /**
     * Crea el panel de la tabla de proveedores
     */
    private JPanel crearPanelTablaProveedores() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(63, 81, 181), 2),
                "Proveedores Registrados",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14),
                new Color(63, 81, 181)));

        panel.add(scrollTablaProveedores, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Crea el panel de productos
     */
    private JPanel crearPanelProductos() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 245));

        // Panel de productos disponibles
        JPanel panelDisponibles = new JPanel(new BorderLayout());
        panelDisponibles.setBackground(Color.WHITE);
        panelDisponibles.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(63, 81, 181), 2),
                "Productos Disponibles",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14),
                new Color(63, 81, 181)));

        // Panel de campos para asociación
        JPanel panelAsociacion = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelAsociacion.setBackground(Color.WHITE);
        panelAsociacion.add(new JLabel("Precio Compra:"));
        txtPrecioCompra.setPreferredSize(new Dimension(100, 25));
        panelAsociacion.add(txtPrecioCompra);
        panelAsociacion.add(new JLabel("Tiempo Entrega (días):"));
        txtTiempoEntrega.setPreferredSize(new Dimension(50, 25));
        panelAsociacion.add(txtTiempoEntrega);

        panelDisponibles.add(panelAsociacion, BorderLayout.NORTH);
        panelDisponibles.add(scrollTablaProductosDisponibles, BorderLayout.CENTER);

        // Panel de productos asociados
        JPanel panelAsociados = new JPanel(new BorderLayout());
        panelAsociados.setBackground(Color.WHITE);
        panelAsociados.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(63, 81, 181), 2),
                "Productos Asociados al Proveedor",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14),
                new Color(63, 81, 181)));

        panelAsociados.add(scrollTablaProductosAsociados, BorderLayout.CENTER);

        // Panel que contiene ambas tablas
        JPanel panelTablas = new JPanel(new GridLayout(1, 2, 10, 0));
        panelTablas.setBackground(new Color(245, 245, 245));
        panelTablas.add(panelDisponibles);
        panelTablas.add(panelAsociados);

        panel.add(panelTablas, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Crea el panel de botones
     */
    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBackground(new Color(245, 245, 245));

        panel.add(btnGuardar);
        panel.add(btnModificar);
        panel.add(btnEliminar);
        panel.add(btnLimpiar);
        panel.add(btnAsociarProducto);
        panel.add(btnDesasociarProducto);
        panel.add(btnVolver);

        return panel;
    }

    /**
     * Carga los proveedores activos en la tabla
     */
    private void cargarProveedores() {
        try {
            List<Proveedor> proveedores = ConexionBD.obtenerProveedoresActivos();
            modeloTablaProveedores.setRowCount(0);

            for (Proveedor proveedor : proveedores) {
                Object[] fila = {
                        proveedor.getId(),
                        proveedor.getCodigoProveedor(),
                        proveedor.getNombre(),
                        proveedor.getRuc(),
                        proveedor.getEmail()
                };
                modeloTablaProveedores.addRow(fila);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar proveedores: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Carga los productos disponibles en la tabla
     */
    private void cargarProductosDisponibles() {
        try {
            List<Producto> productos = ConexionBD.obtenerProductosActivos();
            modeloTablaProductosDisponibles.setRowCount(0);

            for (Producto producto : productos) {
                Object[] fila = {
                        producto.getId(),
                        producto.getCodigoProducto(),
                        producto.getNombre(),
                        String.format("S/%.2f", producto.getPrecioVenta())
                };
                modeloTablaProductosDisponibles.addRow(fila);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar productos: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Carga los productos asociados al proveedor seleccionado
     */
    private void cargarProductosAsociados() {
        int filaSeleccionada = tablaProveedores.getSelectedRow();
        if (filaSeleccionada == -1) {
            modeloTablaProductosAsociados.setRowCount(0);
            return;
        }

        try {
            int proveedorId = (Integer) modeloTablaProveedores.getValueAt(filaSeleccionada, 0);
            List<Producto> productos = ConexionBD.obtenerProductosPorProveedor(proveedorId);
            modeloTablaProductosAsociados.setRowCount(0);

            for (Producto producto : productos) {
                Object[] fila = {
                        producto.getId(),
                        producto.getCodigoProducto(),
                        producto.getNombre(),
                        "S/0.00", // Precio de compra - necesitaríamos modificar el método para obtenerlo
                        "7 días" // Tiempo de entrega - necesitaríamos modificar el método para obtenerlo
                };
                modeloTablaProductosAsociados.addRow(fila);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar productos asociados: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Guarda o actualiza un proveedor
     */
    private void guardarProveedor() {
        if (!validarCampos()) {
            return;
        }

        try {
            Proveedor proveedor = crearProveedorDesdeFormulario();

            if (proveedor.getId() == 0) {
                // Nuevo proveedor
                boolean resultado = ConexionBD.insertarProveedor(proveedor);
                if (resultado) {
                    JOptionPane.showMessageDialog(this,
                            "Proveedor guardado exitosamente",
                            "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "No se pudo guardar el proveedor",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else {
                // Actualizar proveedor existente
                boolean resultado = ConexionBD.actualizarProveedor(proveedor);
                if (resultado) {
                    JOptionPane.showMessageDialog(this,
                            "Proveedor actualizado exitosamente",
                            "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "No se pudo actualizar el proveedor",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            limpiarCampos();
            cargarProveedores();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al guardar proveedor: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Valida los campos del formulario
     */
    private boolean validarCampos() {
        StringBuilder errores = new StringBuilder();

        if (txtCodigo.getText().trim().isEmpty()) {
            errores.append("- El código es obligatorio\n");
        }

        if (txtNombre.getText().trim().isEmpty()) {
            errores.append("- El nombre es obligatorio\n");
        }

        if (txtEmail.getText().trim().isEmpty()) {
            errores.append("- El email es obligatorio\n");
        } else if (!txtEmail.getText().trim().contains("@")) {
            errores.append("- El email debe tener un formato válido\n");
        }

        if (errores.length() > 0) {
            JOptionPane.showMessageDialog(this,
                    "Por favor corrija los siguientes errores:\n\n" + errores.toString(),
                    "Errores de validación", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    /**
     * Crea un objeto Proveedor desde los datos del formulario
     */
    private Proveedor crearProveedorDesdeFormulario() {
        Proveedor proveedor = new Proveedor();

        // Solo asignar ID si estamos editando
        if (tablaProveedores.getSelectedRow() != -1) {
            int id = (Integer) modeloTablaProveedores.getValueAt(tablaProveedores.getSelectedRow(), 0);
            proveedor.setId(id);
        }

        proveedor.setCodigoProveedor(txtCodigo.getText().trim());
        proveedor.setNombre(txtNombre.getText().trim());
        proveedor.setRuc(txtRuc.getText().trim());
        proveedor.setTelefono(txtTelefono.getText().trim());
        proveedor.setEmail(txtEmail.getText().trim());
        proveedor.setDireccion(txtDireccion.getText().trim());
        proveedor.setEsActivo(chkEsActivo.isSelected());

        return proveedor;
    }

    /**
     * Limpia todos los campos del formulario
     */
    private void limpiarCampos() {
        txtCodigo.setText("");
        txtNombre.setText("");
        txtRuc.setText("");
        txtTelefono.setText("");
        txtEmail.setText("");
        txtDireccion.setText("");
        chkEsActivo.setSelected(true);
        txtPrecioCompra.setText("");
        txtTiempoEntrega.setText("7");
        tablaProveedores.clearSelection();
        modeloTablaProductosAsociados.setRowCount(0);
    }

    /**
     * Carga los datos del proveedor seleccionado en la tabla
     */
    private void cargarProveedorSeleccionado() {
        int filaSeleccionada = tablaProveedores.getSelectedRow();
        if (filaSeleccionada != -1) {
            try {
                int id = (Integer) modeloTablaProveedores.getValueAt(filaSeleccionada, 0);
                Proveedor proveedor = ConexionBD.obtenerProveedorPorId(id);

                if (proveedor != null) {
                    txtCodigo.setText(proveedor.getCodigoProveedor());
                    txtNombre.setText(proveedor.getNombre());
                    txtRuc.setText(proveedor.getRuc());
                    txtTelefono.setText(proveedor.getTelefono());
                    txtEmail.setText(proveedor.getEmail());
                    txtDireccion.setText(proveedor.getDireccion());
                    chkEsActivo.setSelected(proveedor.isEsActivo());
                }

                // Cargar productos asociados
                cargarProductosAsociados();

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                        "Error al cargar proveedor: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Modifica el proveedor seleccionado
     */
    private void modificarProveedor() {
        if (tablaProveedores.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(this,
                    "Por favor seleccione un proveedor para modificar",
                    "Selección requerida", JOptionPane.WARNING_MESSAGE);
            return;
        }

        guardarProveedor();
    }

    /**
     * Desactiva el proveedor seleccionado (eliminación lógica)
     */
    private void eliminarProveedor() {
        int filaSeleccionada = tablaProveedores.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                    "Por favor seleccione un proveedor para desactivar",
                    "Selección requerida", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro que desea desactivar este proveedor?\n" +
                        "El proveedor no se eliminará permanentemente, solo se marcará como inactivo.",
                "Confirmar desactivación", JOptionPane.YES_NO_OPTION);

        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                int id = (Integer) modeloTablaProveedores.getValueAt(filaSeleccionada, 0);
                boolean resultado = ConexionBD.desactivarProveedor(id);

                if (resultado) {
                    JOptionPane.showMessageDialog(this,
                            "Proveedor desactivado exitosamente",
                            "Éxito", JOptionPane.INFORMATION_MESSAGE);

                    limpiarCampos();
                    cargarProveedores();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "No se pudo desactivar el proveedor",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Error al desactivar proveedor: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Asocia un producto con el proveedor seleccionado
     */
    private void asociarProducto() {
        int filaProveedor = tablaProveedores.getSelectedRow();
        int filaProducto = tablaProductosDisponibles.getSelectedRow();

        if (filaProveedor == -1) {
            JOptionPane.showMessageDialog(this,
                    "Por favor seleccione un proveedor",
                    "Selección requerida", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (filaProducto == -1) {
            JOptionPane.showMessageDialog(this,
                    "Por favor seleccione un producto para asociar",
                    "Selección requerida", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (txtPrecioCompra.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Por favor ingrese el precio de compra",
                    "Precio requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int proveedorId = (Integer) modeloTablaProveedores.getValueAt(filaProveedor, 0);
            int productoId = (Integer) modeloTablaProductosDisponibles.getValueAt(filaProducto, 0);
            double precioCompra = Double.parseDouble(txtPrecioCompra.getText().trim());
            int tiempoEntrega = Integer.parseInt(txtTiempoEntrega.getText().trim());

            boolean resultado = ConexionBD.asociarProductoProveedor(productoId, proveedorId, precioCompra,
                    tiempoEntrega);

            if (resultado) {
                JOptionPane.showMessageDialog(this,
                        "Producto asociado exitosamente",
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);

                txtPrecioCompra.setText("");
                txtTiempoEntrega.setText("7");
                cargarProductosAsociados();
            } else {
                JOptionPane.showMessageDialog(this,
                        "No se pudo asociar el producto",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Por favor ingrese valores numéricos válidos",
                    "Error de formato", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al asociar producto: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Desasocia un producto del proveedor seleccionado
     */
    private void desasociarProducto() {
        int filaProveedor = tablaProveedores.getSelectedRow();
        int filaProducto = tablaProductosAsociados.getSelectedRow();

        if (filaProveedor == -1) {
            JOptionPane.showMessageDialog(this,
                    "Por favor seleccione un proveedor",
                    "Selección requerida", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (filaProducto == -1) {
            JOptionPane.showMessageDialog(this,
                    "Por favor seleccione un producto para desasociar",
                    "Selección requerida", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro que desea desasociar este producto del proveedor?",
                "Confirmar desasociación", JOptionPane.YES_NO_OPTION);

        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                int proveedorId = (Integer) modeloTablaProveedores.getValueAt(filaProveedor, 0);
                int productoId = (Integer) modeloTablaProductosAsociados.getValueAt(filaProducto, 0);

                boolean resultado = ConexionBD.desasociarProductoProveedor(productoId, proveedorId);

                if (resultado) {
                    JOptionPane.showMessageDialog(this,
                            "Producto desasociado exitosamente",
                            "Éxito", JOptionPane.INFORMATION_MESSAGE);

                    cargarProductosAsociados();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "No se pudo desasociar el producto",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Error al desasociar producto: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FormularioProveedor.class.getName()).log(java.util.logging.Level.SEVERE,
                    null, ex);
        }

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new FormularioProveedor().setVisible(true);
        });
    }
}
