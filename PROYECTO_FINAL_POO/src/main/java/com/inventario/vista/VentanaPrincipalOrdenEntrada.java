package com.inventario.vista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Ventana principal para acceder al formulario de orden de entrada
 * 
 * @author El~Nicolays
 */
public class VentanaPrincipalOrdenEntrada extends JFrame {

    private JButton btnRegistrarOrdenEntrada;
    private JButton btnSalir;

    public VentanaPrincipalOrdenEntrada() {
        initComponents();
        setupDesign();
    }

    private void initComponents() {
        setTitle("Sistema de Inventario - Gestión de Órdenes");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 200);
        setLocationRelativeTo(null);

        btnRegistrarOrdenEntrada = new JButton("Registrar Orden de Entrada");
        btnSalir = new JButton("Salir");

        // Configurar eventos
        btnRegistrarOrdenEntrada.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirFormularioOrdenEntrada();
            }
        });

        btnSalir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        setupLayout();
    }

    private void setupLayout() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Título
        JLabel lblTitulo = new JLabel("Sistema de Gestión de Inventario");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(lblTitulo, gbc);

        // Botón de orden de entrada
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.5;
        add(btnRegistrarOrdenEntrada, gbc);

        // Botón de salir
        gbc.gridx = 1;
        gbc.gridy = 1;
        add(btnSalir, gbc);
    }

    private void setupDesign() {
        Color colorFondo = new Color(240, 248, 255);
        Color colorBoton = new Color(70, 130, 180);
        Font fuenteBoton = new Font("Arial", Font.BOLD, 12);

        getContentPane().setBackground(colorFondo);

        btnRegistrarOrdenEntrada.setBackground(colorBoton);
        btnRegistrarOrdenEntrada.setForeground(Color.WHITE);
        btnRegistrarOrdenEntrada.setFont(fuenteBoton);
        btnRegistrarOrdenEntrada.setFocusPainted(false);

        btnSalir.setBackground(Color.GRAY);
        btnSalir.setForeground(Color.WHITE);
        btnSalir.setFont(fuenteBoton);
        btnSalir.setFocusPainted(false);
    }

    private void abrirFormularioOrdenEntrada() {
        try {
            FormularioOrdenEntrada formulario = new FormularioOrdenEntrada();
            formulario.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al abrir el formulario: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new VentanaPrincipalOrdenEntrada().setVisible(true);
        });
    }
}
