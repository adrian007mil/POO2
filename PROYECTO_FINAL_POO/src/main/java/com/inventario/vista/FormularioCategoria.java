package com.inventario.vista;

import com.inventario.dao.ConexionBD;
import com.inventario.modelo.Categoria;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

/**
 * Formulario para la gestión de categorías
 * 
 * @author El~Nicolays
 */
public class FormularioCategoria extends javax.swing.JFrame {

    private DefaultTableModel modeloTabla;

    // Componentes del formulario
    private JTextField txtCodigo;
    private JTextField txtNombre;
    private JTextArea txtDescripcion;
    private JCheckBox chkEsActivo;
    private JButton btnGuardar;
    private JButton btnLimpiar;
    private JButton btnVolver;
    private JButton btnEliminar;
    private JButton btnModificar;
    private JTable tablaCategorias;
    private JScrollPane scrollTabla;
    private JScrollPane scrollDescripcion;

    /**
     * Creates new form FormularioCategoria
     */
    public FormularioCategoria() {
        initComponents();
        setupCustomDesign();
        cargarCategorias();
    }

    /**
     * Inicializa los componentes del formulario
     */
    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Gestión de Categorías - Sistema de Inventario");
        setResizable(false);
        setLocationRelativeTo(null);

        // Inicializar componentes
        txtCodigo = new JTextField();
        txtNombre = new JTextField();
        txtDescripcion = new JTextArea(3, 20);
        chkEsActivo = new JCheckBox("Categoría activa");

        btnGuardar = new JButton("Guardar");
        btnLimpiar = new JButton("Limpiar");
        btnVolver = new JButton("Volver");
        btnEliminar = new JButton("Eliminar");
        btnModificar = new JButton("Modificar");

        // Configurar tabla
        String[] columnas = { "ID", "Código", "Nombre", "Descripción", "Estado" };
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hacer la tabla no editable
            }
        };
        tablaCategorias = new JTable(modeloTabla);
        scrollTabla = new JScrollPane(tablaCategorias);
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
        setSize(800, 600);
        getContentPane().setBackground(new Color(245, 245, 245));

        // Configurar campos de texto
        Font campoFont = new Font("Arial", Font.PLAIN, 12);
        txtCodigo.setFont(campoFont);
        txtNombre.setFont(campoFont);
        txtDescripcion.setFont(campoFont);

        // Configurar botones
        configurarBoton(btnGuardar, new Color(76, 175, 80), Color.WHITE);
        configurarBoton(btnLimpiar, new Color(255, 193, 7), Color.BLACK);
        configurarBoton(btnVolver, new Color(108, 117, 125), Color.WHITE);
        configurarBoton(btnEliminar, new Color(244, 67, 54), Color.WHITE);
        configurarBoton(btnModificar, new Color(33, 150, 243), Color.WHITE);

        // Configurar tabla
        tablaCategorias.setFont(new Font("Arial", Font.PLAIN, 11));
        tablaCategorias.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tablaCategorias.getTableHeader().setBackground(new Color(63, 81, 181));
        tablaCategorias.getTableHeader().setForeground(Color.WHITE);
        tablaCategorias.setRowHeight(25);
        tablaCategorias.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Configurar checkbox
        chkEsActivo.setSelected(true);
        chkEsActivo.setFont(campoFont);
        chkEsActivo.setBackground(new Color(245, 245, 245));

        // Configurar scrolls
        scrollTabla.setPreferredSize(new Dimension(750, 250));
        scrollDescripcion.setPreferredSize(new Dimension(300, 60));
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
                guardarCategoria();
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
                eliminarCategoria();
            }
        });

        btnModificar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modificarCategoria();
            }
        });

        // Evento para seleccionar fila de la tabla
        tablaCategorias.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarCategoriaSeleccionada();
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
                "Datos de la Categoría",
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

        // Fila 2: Descripción
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Descripción:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(scrollDescripcion, gbc);

        // Fila 3: Estado
        gbc.gridx = 0;
        gbc.gridy = 2;
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
                "Lista de Categorías Registradas",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14),
                new Color(63, 81, 181)));

        panel.add(scrollTabla, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Carga las categorías activas en la tabla
     */
    private void cargarCategorias() {
        try {
            List<Categoria> categorias = ConexionBD.obtenerCategoriasActivas();
            modeloTabla.setRowCount(0); // Limpiar tabla

            for (Categoria categoria : categorias) {
                Object[] fila = {
                        categoria.getId(),
                        categoria.getCodigo(),
                        categoria.getNombre(),
                        categoria.getDescripcion(),
                        categoria.isEsActivo() ? "Activa" : "Inactiva"
                };
                modeloTabla.addRow(fila);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar categorías: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Guarda o actualiza una categoría
     */
    private void guardarCategoria() {
        if (!validarCampos()) {
            return;
        }

        try {
            Categoria categoria = crearCategoriaDesdeFormulario();

            if (categoria.getId() == 0) {
                // Nueva categoría
                boolean resultado = ConexionBD.insertarCategoria(categoria);
                if (resultado) {
                    JOptionPane.showMessageDialog(this,
                            "Categoría guardada exitosamente",
                            "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "No se pudo guardar la categoría",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else {
                // Actualizar categoría existente
                boolean resultado = ConexionBD.actualizarCategoria(categoria);
                if (resultado) {
                    JOptionPane.showMessageDialog(this,
                            "Categoría actualizada exitosamente",
                            "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "No se pudo actualizar la categoría",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            limpiarCampos();
            cargarCategorias();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al guardar categoría: " + e.getMessage(),
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

        if (errores.length() > 0) {
            JOptionPane.showMessageDialog(this,
                    "Por favor corrija los siguientes errores:\n\n" + errores.toString(),
                    "Errores de validación", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    /**
     * Crea un objeto Categoria desde los datos del formulario
     */
    private Categoria crearCategoriaDesdeFormulario() {
        Categoria categoria = new Categoria();

        // Solo asignar ID si estamos editando
        if (tablaCategorias.getSelectedRow() != -1) {
            int id = (Integer) modeloTabla.getValueAt(tablaCategorias.getSelectedRow(), 0);
            categoria.setId(id);
        }

        categoria.setCodigo(txtCodigo.getText().trim());
        categoria.setNombre(txtNombre.getText().trim());
        categoria.setDescripcion(txtDescripcion.getText().trim());
        categoria.setEsActivo(chkEsActivo.isSelected());

        return categoria;
    }

    /**
     * Limpia todos los campos del formulario
     */
    private void limpiarCampos() {
        txtCodigo.setText("");
        txtNombre.setText("");
        txtDescripcion.setText("");
        chkEsActivo.setSelected(true);
        tablaCategorias.clearSelection();
    }

    /**
     * Carga los datos de la categoría seleccionada en la tabla
     */
    private void cargarCategoriaSeleccionada() {
        int filaSeleccionada = tablaCategorias.getSelectedRow();
        if (filaSeleccionada != -1) {
            txtCodigo.setText(String.valueOf(modeloTabla.getValueAt(filaSeleccionada, 1)));
            txtNombre.setText(String.valueOf(modeloTabla.getValueAt(filaSeleccionada, 2)));
            txtDescripcion.setText(String.valueOf(modeloTabla.getValueAt(filaSeleccionada, 3)));
            chkEsActivo.setSelected("Activa".equals(modeloTabla.getValueAt(filaSeleccionada, 4)));
        }
    }

    /**
     * Modifica la categoría seleccionada
     */
    private void modificarCategoria() {
        if (tablaCategorias.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(this,
                    "Por favor seleccione una categoría para modificar",
                    "Selección requerida", JOptionPane.WARNING_MESSAGE);
            return;
        }

        guardarCategoria(); // Reutilizar la lógica de guardar
    }

    /**
     * Desactiva la categoría seleccionada (eliminación lógica)
     */
    private void eliminarCategoria() {
        int filaSeleccionada = tablaCategorias.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                    "Por favor seleccione una categoría para desactivar",
                    "Selección requerida", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro que desea desactivar esta categoría?\n" +
                        "La categoría no se eliminará permanentemente, solo se marcará como inactiva.",
                "Confirmar desactivación", JOptionPane.YES_NO_OPTION);

        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                int id = (Integer) modeloTabla.getValueAt(filaSeleccionada, 0);
                boolean resultado = ConexionBD.desactivarCategoria(id);

                if (resultado) {
                    JOptionPane.showMessageDialog(this,
                            "Categoría desactivada exitosamente",
                            "Éxito", JOptionPane.INFORMATION_MESSAGE);

                    limpiarCampos();
                    cargarCategorias();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "No se pudo desactivar la categoría",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Error al desactivar categoría: " + e.getMessage(),
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
            java.util.logging.Logger.getLogger(FormularioCategoria.class.getName()).log(java.util.logging.Level.SEVERE,
                    null, ex);
        }

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new FormularioCategoria().setVisible(true);
        });
    }
}
