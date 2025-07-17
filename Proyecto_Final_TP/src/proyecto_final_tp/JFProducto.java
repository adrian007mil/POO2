package proyecto_final_tp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class JFProducto extends JFrame {
    
    // Componentes del formulario
    private JTextField txtCodigoProducto;
    private JTextField txtNombre;
    private JTextField txtPrecio;
    private JTextField txtCantidad;
    private JCheckBox chkEsActivo;
    private JButton btnRegistrar;
    private JButton btnRefresh;
    private JTextArea txtAreaResultado;
    
    // Inventario para gestionar productos
    private Inventario inventario;
    
    public JFProducto() {
        inventario = new Inventario();
        inicializarComponentes();
        configurarVentana();
        configurarEventos();
    }
    
    private void inicializarComponentes() {
        // Crear componentes
        txtCodigoProducto = new JTextField(15);
        txtCodigoProducto.setEditable(false); // No editable porque es autoincremento
        txtCodigoProducto.setBackground(Color.LIGHT_GRAY);
        
        txtNombre = new JTextField(20);
        txtPrecio = new JTextField(10);
        txtCantidad = new JTextField(10);
        
        chkEsActivo = new JCheckBox("Producto Activo");
        chkEsActivo.setSelected(true); // Por defecto true
        chkEsActivo.setEnabled(false); // No editable porque siempre es true por defecto
        
        btnRegistrar = new JButton("Registrar Producto");
        btnRefresh = new JButton("Limpiar Campos");
        
        txtAreaResultado = new JTextArea(10, 40);
        txtAreaResultado.setEditable(false);
        txtAreaResultado.setBackground(Color.WHITE);
        txtAreaResultado.setBorder(BorderFactory.createTitledBorder("Productos Registrados"));
        
        // Mostrar el próximo ID que se generará
        actualizarProximoID();
    }
    
    private void configurarVentana() {
        setTitle("WIN EMPRESAS - Registro de Productos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Panel principal
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        
        // Panel de formulario
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Registro de Producto"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Fila 0: Código de Producto (solo lectura)
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        panelFormulario.add(new JLabel("ID Producto:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        panelFormulario.add(txtCodigoProducto, gbc);
        
        // Fila 1: Nombre
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        panelFormulario.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        panelFormulario.add(txtNombre, gbc);
        
        // Fila 2: Precio
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
        panelFormulario.add(new JLabel("Precio:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        panelFormulario.add(txtPrecio, gbc);
        
        // Fila 3: Cantidad
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE;
        panelFormulario.add(new JLabel("Cantidad:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        panelFormulario.add(txtCantidad, gbc);
        
        // Fila 4: CheckBox Activo (solo lectura)
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE;
        panelFormulario.add(new JLabel("Estado:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        panelFormulario.add(chkEsActivo, gbc);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout());
        panelBotones.add(btnRegistrar);
        panelBotones.add(btnRefresh);
        
        // Agregar componentes al panel principal
        panelPrincipal.add(panelFormulario, BorderLayout.NORTH);
        panelPrincipal.add(panelBotones, BorderLayout.CENTER);
        
        // Panel de resultados
        JScrollPane scrollResultados = new JScrollPane(txtAreaResultado);
        
        // Agregar todo a la ventana
        add(panelPrincipal, BorderLayout.NORTH);
        add(scrollResultados, BorderLayout.CENTER);
        
        // Configurar ventana
        pack();
        setLocationRelativeTo(null);
        setResizable(true);
    }
    
    private void configurarEventos() {
        // Evento del botón Registrar
        btnRegistrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registrarProducto();
            }
        });
        
        // Evento del botón Refresh
        btnRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                limpiarCampos();
            }
        });
    }
    
    private void registrarProducto() {
        try {
            // Validar campos
            if (txtNombre.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "El nombre del producto es obligatorio", 
                                            "Error de Validación", JOptionPane.ERROR_MESSAGE);
                txtNombre.requestFocus();
                return;
            }
            
            if (txtPrecio.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "El precio del producto es obligatorio", 
                                            "Error de Validación", JOptionPane.ERROR_MESSAGE);
                txtPrecio.requestFocus();
                return;
            }
            
            if (txtCantidad.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "La cantidad del producto es obligatoria", 
                                            "Error de Validación", JOptionPane.ERROR_MESSAGE);
                txtCantidad.requestFocus();
                return;
            }
            
            // Obtener y validar datos
            String nombre = txtNombre.getText().trim();
            double precio = Double.parseDouble(txtPrecio.getText().trim());
            int cantidad = Integer.parseInt(txtCantidad.getText().trim());
            
            if (precio <= 0) {
                JOptionPane.showMessageDialog(this, "El precio debe ser mayor a 0", 
                                            "Error de Validación", JOptionPane.ERROR_MESSAGE);
                txtPrecio.requestFocus();
                return;
            }
            
            if (cantidad < 0) {
                JOptionPane.showMessageDialog(this, "La cantidad no puede ser negativa", 
                                            "Error de Validación", JOptionPane.ERROR_MESSAGE);
                txtCantidad.requestFocus();
                return;
            }
            
            // Crear producto (el código y estado activo se generan automáticamente)
            Producto nuevoProducto = new Producto(nombre, precio, cantidad);
            
            // Agregar al inventario
            inventario.agregarProducto(nuevoProducto);
            
            // Mostrar mensaje de éxito
            JOptionPane.showMessageDialog(this, 
                "Producto registrado exitosamente!\nCódigo: " + nuevoProducto.getCodigoProducto(), 
                "Registro Exitoso", 
                JOptionPane.INFORMATION_MESSAGE);
            
            // Actualizar área de resultados
            actualizarListaProductos();
            
            // Limpiar campos para el siguiente registro
            limpiarCampos();
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, 
                "Por favor ingrese valores numéricos válidos para precio y cantidad", 
                "Error de Formato", 
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error al registrar producto: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void limpiarCampos() {
        txtNombre.setText("");
        txtPrecio.setText("");
        txtCantidad.setText("");
        chkEsActivo.setSelected(true); // Siempre vuelve a true
        
        // Actualizar el próximo ID
        actualizarProximoID();
        
        // Enfocar el primer campo editable
        txtNombre.requestFocus();
    }
    
    private void actualizarProximoID() {
        txtCodigoProducto.setText(Producto.obtenerSiguienteID());
    }
    
    private void actualizarListaProductos() {
        StringBuilder sb = new StringBuilder();
        sb.append("PRODUCTOS REGISTRADOS:\n");
        sb.append("==================================================\n");
        
        for (Producto producto : inventario.getProductos()) {
            sb.append(String.format("ID: %s | Nombre: %s | Precio: S/%.2f | Stock: %d | Activo: %s\n",
                    producto.getCodigoProducto(),
                    producto.getNombre(),
                    producto.getPrecio(),
                    producto.getCantidad(),
                    producto.isEsActivo() ? "Sí" : "No"));
        }
        
        if (inventario.getProductos().isEmpty()) {
            sb.append("No hay productos registrados.\n");
        }
        
        txtAreaResultado.setText(sb.toString());
        
        // Scroll al final
        txtAreaResultado.setCaretPosition(txtAreaResultado.getDocument().getLength());
    }
    
    public static void main(String[] args) {
        // Configurar Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Crear y mostrar formulario
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new JFProducto().setVisible(true);
            }
        });
    }
}