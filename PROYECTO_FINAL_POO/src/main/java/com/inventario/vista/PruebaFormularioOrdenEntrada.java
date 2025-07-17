package com.inventario.vista;

import javax.swing.*;

/**
 * Clase para probar el formulario de orden de entrada
 */
public class PruebaFormularioOrdenEntrada {
    public static void main(String[] args) {
        // Configurar Look and Feel
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("No se pudo establecer el Look and Feel Nimbus");
        }

        // Crear y mostrar el formulario
        SwingUtilities.invokeLater(() -> {
            try {
                FormularioOrdenEntrada formulario = new FormularioOrdenEntrada();
                formulario.setVisible(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                        "Error al crear el formulario: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        });
    }
}
