package com.inventario.vista;

import com.inventario.dao.ConexionBD;
import com.inventario.modelo.Categoria;
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
 * Formulario para el registro de productos con lista integrada
 * 
 * @author El~Nicolays
 */
public class FormularioRegistroProducto extends javax.swing.JFrame {

    private DefaultTableModel modeloTabla;

    // Componentes del formulario
    private JTextField txtCodigo;
    private JTextField txtNombre;
    private JTextArea txtDescripcion;
    private JTextField txtPrecio;
    private JTextField txtCantidad;
    private JTextField txtCantidadMinima;
    private JComboBox<Categoria> cmbCategoria;
    private JCheckBox chkEsActivo;
    private JButton btnGuardar;
    private JButton btnLimpiar;
    private JButton btnVolver;
    private JButton btnEliminar;
    private JButton btnModificar;
    private JTable tablaProductos;
    private JScrollPane scrollTabla;
    private JScrollPane scrollDescripcion;

    /**
     * Creates new form FormularioRegistroProducto
     */
    public FormularioRegistroProducto() {
        initComponents();
        setupCustomDesign();
        cargarCategorias();
        cargarProductos();
    }

    /**
     * Inicializa los componentes del formulario
     */
    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Registro de Productos - Sistema de Inventario");
        setResizable(false);

        // Inicializar componentes
        txtCodigo = new JTextField();
        txtNombre = new JTextField();
        txtDescripcion = new JTextArea(3, 20);
        txtPrecio = new JTextField();
        txtCantidad = new JTextField();
        txtCantidadMinima = new JTextField();
        cmbCategoria = new JComboBox<>();
        chkEsActivo = new JCheckBox("Producto activo");

        btnGuardar = new JButton("Guardar");
        btnLimpiar = new JButton("Limpiar");
        btnVolver = new JButton("Volver");
        btnEliminar = new JButton("Eliminar");
        btnModificar = new JButton("Modificar");

        // Configurar tabla
        String[] columnas = { "ID", "Código", "Nombre", "Cantidad", "Precio", "Categoría", "Estado" };
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hacer la tabla no editable
            }
        };
        tablaProductos = new JTable(modeloTabla);
        scrollTabla = new JScrollPane(tablaProductos);
        scrollDescripcion = new JScrollPane(txtDescripcion);

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
        setSize(900, 700);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(245, 245, 245));

        // Configurar campos de texto
        Font campoFont = new Font("Arial", Font.PLAIN, 12);
        txtCodigo.setFont(campoFont);
        txtNombre.setFont(campoFont);
        txtDescripcion.setFont(campoFont);
        txtPrecio.setFont(campoFont);
        txtCantidad.setFont(campoFont);
        txtCantidadMinima.setFont(campoFont);

        // Configurar botones
        configurarBoton(btnGuardar, new Color(76, 175, 80), Color.WHITE);
        configurarBoton(btnLimpiar, new Color(255, 193, 7), Color.BLACK);
        configurarBoton(btnVolver, new Color(108, 117, 125), Color.WHITE);
        configurarBoton(btnEliminar, new Color(244, 67, 54), Color.WHITE);
        configurarBoton(btnModificar, new Color(33, 150, 243), Color.WHITE);

        // Configurar tabla
        tablaProductos.setFont(new Font("Arial", Font.PLAIN, 11));
        tablaProductos.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tablaProductos.getTableHeader().setBackground(new Color(63, 81, 181));
        tablaProductos.getTableHeader().setForeground(Color.WHITE);
        tablaProductos.setRowHeight(25);
        tablaProductos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Configurar checkbox
        chkEsActivo.setSelected(true);
        chkEsActivo.setFont(campoFont);
        chkEsActivo.setBackground(new Color(245, 245, 245));

        // Configurar scrolls
        scrollTabla.setPreferredSize(new Dimension(850, 250));
        scrollDescripcion.setPreferredSize(new Dimension(300, 60));

        // Configurar campos numéricos
        txtCantidadMinima.setText("5"); // Valor por defecto
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
     * Configura los manejadores de eventos
     */
    private void setupEventHandlers() {
        btnGuardar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                guardarProducto();
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
                eliminarProducto();
            }
        });

        btnModificar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modificarProducto();
            }
        });

        // Evento para seleccionar fila de la tabla
        tablaProductos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarProductoSeleccionado();
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

        // Panel del formulario
        JPanel panelFormulario = crearPanelFormulario();

        // Panel de botones
        JPanel panelBotones = crearPanelBotones();

        // Panel de la tabla
        JPanel panelTabla = crearPanelTabla();

        // Agregar paneles al panel principal
        panelPrincipal.add(panelFormulario, BorderLayout.NORTH);
        panelPrincipal.add(panelTabla, BorderLayout.CENTER);
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
                "Datos del Producto",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14),
                new Color(63, 81, 181)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Fila 1: Código y Nombre
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Código:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        txtCodigo.setPreferredSize(new Dimension(150, 25));
        panel.add(txtCodigo, gbc);

        gbc.gridx = 2;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        txtNombre.setPreferredSize(new Dimension(200, 25));
        panel.add(txtNombre, gbc);

        // Fila 2: Precio y Cantidad
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Precio:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        txtPrecio.setPreferredSize(new Dimension(120, 25));
        panel.add(txtPrecio, gbc);

        gbc.gridx = 2;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Cantidad:"), gbc);
        gbc.gridx = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        txtCantidad.setPreferredSize(new Dimension(100, 25));
        panel.add(txtCantidad, gbc);

        // Fila 3: Cantidad Mínima y Categoría
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Cantidad Mínima:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        txtCantidadMinima.setPreferredSize(new Dimension(100, 25));
        panel.add(txtCantidadMinima, gbc);

        gbc.gridx = 2;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Categoría:"), gbc);
        gbc.gridx = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        cmbCategoria.setPreferredSize(new Dimension(200, 25));
        panel.add(cmbCategoria, gbc);

        // Fila 4: Descripción
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Descripción:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(scrollDescripcion, gbc);

        // Fila 5: Estado
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(chkEsActivo, gbc);

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
        panel.add(btnVolver);

        return panel;
    }

    /**
     * Crea el panel de la tabla
     */
    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(63, 81, 181), 2),
                "Lista de Productos Registrados",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14),
                new Color(63, 81, 181)));

        panel.add(scrollTabla, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Carga las categorías activas en el combo box
     */
    private void cargarCategorias() {
        try {
            List<Categoria> categorias = ConexionBD.obtenerCategoriasActivas();
            cmbCategoria.removeAllItems();

            // Agregar opción por defecto
            cmbCategoria.addItem(null);

            for (Categoria categoria : categorias) {
                cmbCategoria.addItem(categoria);
            }

            // Configurar renderer para mostrar nombre de categoría
            cmbCategoria.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value,
                        int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof Categoria) {
                        setText(((Categoria) value).getNombre());
                    } else {
                        setText("-- Seleccionar categoría --");
                    }
                    return this;
                }
            });

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar categorías: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Carga los productos activos en la tabla
     */
    private void cargarProductos() {
        try {
            List<Producto> productos = ConexionBD.obtenerProductosActivos();
            modeloTabla.setRowCount(0); // Limpiar tabla

            for (Producto producto : productos) {
                Object[] fila = {
                        producto.getId(),
                        producto.getCodigoProducto(),
                        producto.getNombre(),
                        producto.getCantidadDisponible(),
                        String.format("S/%.2f", producto.getPrecioVenta()),
                        producto.getCategoria() != null ? producto.getCategoria().getNombre() : "Sin categoría",
                        producto.isEsActivo() ? "Activo" : "Inactivo"
                };
                modeloTabla.addRow(fila);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar productos: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Guarda o actualiza un producto
     */
    private void guardarProducto() {
        if (!validarCampos()) {
            return;
        }

        try {
            Producto producto = crearProductoDesdeFormulario();

            if (producto.getId() == 0) {
                // Nuevo producto
                boolean resultado = ConexionBD.insertarProducto(producto);
                if (resultado) {
                    JOptionPane.showMessageDialog(this,
                            "Producto guardado exitosamente",
                            "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "No se pudo guardar el producto",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else {
                // Actualizar producto existente
                boolean resultado = ConexionBD.actualizarProducto(producto);
                if (resultado) {
                    JOptionPane.showMessageDialog(this,
                            "Producto actualizado exitosamente",
                            "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "No se pudo actualizar el producto",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            limpiarCampos();
            cargarProductos();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al guardar producto: " + e.getMessage(),
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

        if (txtPrecio.getText().trim().isEmpty()) {
            errores.append("- El precio es obligatorio\n");
        } else {
            try {
                double precio = Double.parseDouble(txtPrecio.getText().trim());
                if (precio < 0) {
                    errores.append("- El precio debe ser mayor o igual a 0\n");
                }
            } catch (NumberFormatException e) {
                errores.append("- El precio debe ser un número válido\n");
            }
        }

        if (txtCantidad.getText().trim().isEmpty()) {
            errores.append("- La cantidad es obligatoria\n");
        } else {
            try {
                int cantidad = Integer.parseInt(txtCantidad.getText().trim());
                if (cantidad < 0) {
                    errores.append("- La cantidad debe ser mayor o igual a 0\n");
                }
            } catch (NumberFormatException e) {
                errores.append("- La cantidad debe ser un número entero válido\n");
            }
        }

        if (!txtCantidadMinima.getText().trim().isEmpty()) {
            try {
                int cantidadMinima = Integer.parseInt(txtCantidadMinima.getText().trim());
                if (cantidadMinima < 0) {
                    errores.append("- La cantidad mínima debe ser mayor o igual a 0\n");
                }
            } catch (NumberFormatException e) {
                errores.append("- La cantidad mínima debe ser un número entero válido\n");
            }
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
     * Crea un objeto Producto desde los datos del formulario
     */
    private Producto crearProductoDesdeFormulario() {
        Producto producto = new Producto();

        // Solo asignar ID si estamos editando
        if (tablaProductos.getSelectedRow() != -1) {
            int id = (Integer) modeloTabla.getValueAt(tablaProductos.getSelectedRow(), 0);
            producto.setId(id);
        }

        producto.setCodigoProducto(txtCodigo.getText().trim());
        producto.setNombre(txtNombre.getText().trim());
        producto.setDescripcion(txtDescripcion.getText().trim());
        producto.setPrecioVenta(Double.parseDouble(txtPrecio.getText().trim()));
        producto.setCantidadDisponible(Integer.parseInt(txtCantidad.getText().trim()));

        if (!txtCantidadMinima.getText().trim().isEmpty()) {
            producto.setCantidadMinima(Integer.parseInt(txtCantidadMinima.getText().trim()));
        } else {
            producto.setCantidadMinima(5); // Valor por defecto
        }

        producto.setEsActivo(chkEsActivo.isSelected());
        producto.setCategoria((Categoria) cmbCategoria.getSelectedItem());

        return producto;
    }

    /**
     * Limpia todos los campos del formulario
     */
    private void limpiarCampos() {
        txtCodigo.setText("");
        txtNombre.setText("");
        txtDescripcion.setText("");
        txtPrecio.setText("");
        txtCantidad.setText("");
        txtCantidadMinima.setText("5");
        cmbCategoria.setSelectedIndex(0);
        chkEsActivo.setSelected(true);
        tablaProductos.clearSelection();
    }

    /**
     * Carga los datos del producto seleccionado en la tabla
     */
    private void cargarProductoSeleccionado() {
        int filaSeleccionada = tablaProductos.getSelectedRow();
        if (filaSeleccionada != -1) {
            try {
                int id = (Integer) modeloTabla.getValueAt(filaSeleccionada, 0);
                Producto producto = ConexionBD.obtenerProductoPorId(id);

                if (producto != null) {
                    txtCodigo.setText(producto.getCodigoProducto());
                    txtNombre.setText(producto.getNombre());
                    txtDescripcion.setText(producto.getDescripcion());
                    txtPrecio.setText(String.valueOf(producto.getPrecioVenta()));
                    txtCantidad.setText(String.valueOf(producto.getCantidadDisponible()));
                    txtCantidadMinima.setText(String.valueOf(producto.getCantidadMinima()));
                    chkEsActivo.setSelected(producto.isEsActivo());

                    // Seleccionar categoría
                    if (producto.getCategoria() != null) {
                        for (int i = 0; i < cmbCategoria.getItemCount(); i++) {
                            Categoria categoria = cmbCategoria.getItemAt(i);
                            if (categoria != null && categoria.getId() == producto.getCategoria().getId()) {
                                cmbCategoria.setSelectedIndex(i);
                                break;
                            }
                        }
                    } else {
                        cmbCategoria.setSelectedIndex(0);
                    }
                }

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                        "Error al cargar producto: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Modifica el producto seleccionado
     */
    private void modificarProducto() {
        if (tablaProductos.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(this,
                    "Por favor seleccione un producto para modificar",
                    "Selección requerida", JOptionPane.WARNING_MESSAGE);
            return;
        }

        guardarProducto(); // Reutilizar la lógica de guardar
    }

    /**
     * Desactiva el producto seleccionado (eliminación lógica)
     */
    private void eliminarProducto() {
        int filaSeleccionada = tablaProductos.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                    "Por favor seleccione un producto para desactivar",
                    "Selección requerida", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro que desea desactivar este producto?\n" +
                        "El producto no se eliminará permanentemente, solo se marcará como inactivo.",
                "Confirmar desactivación", JOptionPane.YES_NO_OPTION);

        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                int id = (Integer) modeloTabla.getValueAt(filaSeleccionada, 0);
                boolean resultado = ConexionBD.desactivarProducto(id);

                if (resultado) {
                    JOptionPane.showMessageDialog(this,
                            "Producto desactivado exitosamente",
                            "Éxito", JOptionPane.INFORMATION_MESSAGE);

                    limpiarCampos();
                    cargarProductos();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "No se pudo desactivar el producto",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Error al desactivar producto: " + e.getMessage(),
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
            java.util.logging.Logger.getLogger(FormularioRegistroProducto.class.getName())
                    .log(java.util.logging.Level.SEVERE, null, ex);
        }

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new FormularioRegistroProducto().setVisible(true);
        });
    }
}
