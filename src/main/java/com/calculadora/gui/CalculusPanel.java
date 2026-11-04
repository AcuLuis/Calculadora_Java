package com.calculadora.gui;

import com.calculadora.math.Calculus;
import com.calculadora.math.EvaluadorMath;
import com.calculadora.math.FormatoNumero;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

public class CalculusPanel extends JPanel {

    private JTextField funcInput;
    private JTextField pointInput;
    private JLabel derivativeResult;
    
    private JTextField funcIntegralInput;
    private JTextField limitA;
    private JTextField limitB;
    private JLabel integralResult;
    
    private final Color COLOR_BG = new Color(40, 42, 44);
    private final Color COLOR_BTN = new Color(33, 150, 243); // Azul moderno

    // Dígitos significativos que se muestran según la precisión de cada método numérico
    private static final int DIGITOS_DERIVADA = 9;
    private static final int DIGITOS_INTEGRAL = 10;

    public CalculusPanel() {
        setLayout(new BorderLayout());
        setBackground(COLOR_BG);
        
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 16));
        
        tabs.addTab("  Derivada  ", createDerivativePanel());
        tabs.addTab("  Integral Definida  ", createIntegralPanel());
        
        add(tabs, BorderLayout.CENTER);
    }

    private JPanel createDerivativePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COLOR_BG);
        panel.setBorder(new EmptyBorder(30, 40, 30, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        Font labelFont = new Font("Segoe UI", Font.PLAIN, 18);
        Font inputFont = new Font("Consolas", Font.BOLD, 20);
        
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblFunc = new JLabel("Función f(x):");
        lblFunc.setFont(labelFont);
        panel.add(lblFunc, gbc);
        
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        funcInput = new JTextField("x^2");
        funcInput.setFont(inputFont);
        panel.add(funcInput, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        JLabel lblEval = new JLabel("Evaluar en x =");
        lblEval.setFont(labelFont);
        panel.add(lblEval, gbc);
        
        gbc.gridx = 1; gbc.gridy = 1;
        pointInput = new JTextField("3");
        pointInput.setFont(inputFont);
        panel.add(pointInput, gbc);
        
        gbc.gridx = 1; gbc.gridy = 2;
        JButton btn = createStyledButton("Calcular Derivada f'(x)");
        btn.addActionListener(this::calculateDerivative);
        panel.add(btn, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        derivativeResult = new JLabel("Resultado: --");
        derivativeResult.setFont(new Font("Segoe UI", Font.BOLD, 22));
        derivativeResult.setForeground(new Color(76, 175, 80));
        derivativeResult.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(derivativeResult, gbc);
        
        return panel;
    }

    private JPanel createIntegralPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COLOR_BG);
        panel.setBorder(new EmptyBorder(30, 40, 30, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 15, 12, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        Font labelFont = new Font("Segoe UI", Font.PLAIN, 18);
        Font inputFont = new Font("Consolas", Font.BOLD, 20);
        
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblFunc = new JLabel("Función f(x):");
        lblFunc.setFont(labelFont);
        panel.add(lblFunc, gbc);
        
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0; gbc.gridwidth = 2;
        funcIntegralInput = new JTextField("x^2");
        funcIntegralInput.setFont(inputFont);
        panel.add(funcIntegralInput, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; gbc.gridwidth = 1;
        JLabel lblA = new JLabel("Límite inferior (a):");
        lblA.setFont(labelFont);
        panel.add(lblA, gbc);
        
        gbc.gridx = 1; gbc.gridy = 1;
        limitA = new JTextField("0");
        limitA.setFont(inputFont);
        panel.add(limitA, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel lblB = new JLabel("Límite superior (b):");
        lblB.setFont(labelFont);
        panel.add(lblB, gbc);
        
        gbc.gridx = 1; gbc.gridy = 2;
        limitB = new JTextField("1");
        limitB.setFont(inputFont);
        panel.add(limitB, gbc);
        
        gbc.gridx = 1; gbc.gridy = 3; gbc.gridwidth = 2;
        JButton btn = createStyledButton("Calcular Integral ∫f(x)dx");
        btn.addActionListener(this::calculateIntegral);
        panel.add(btn, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 3;
        integralResult = new JLabel("Resultado: --");
        integralResult.setFont(new Font("Segoe UI", Font.BOLD, 22));
        integralResult.setForeground(new Color(76, 175, 80));
        integralResult.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(integralResult, gbc);
        
        return panel;
    }
    
    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btn.setBackground(COLOR_BTN);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        return btn;
    }

    private void calculateDerivative(ActionEvent e) {
        Double x = leerValor(pointInput, "el punto x");
        if (x == null) {
            return;
        }
        try {
            double res = Calculus.derivada(funcInput.getText(), x);
            derivativeResult.setText("Resultado: " + FormatoNumero.formatear(res, DIGITOS_DERIVADA));
        } catch (ArithmeticException ex) {
            mostrarError("La función no está definida cerca de x = " + FormatoNumero.formatear(x) + ".");
        } catch (IllegalArgumentException ex) {
            mostrarError("Sintaxis inválida en la función f(x).");
        }
    }

    private void calculateIntegral(ActionEvent e) {
        Double a = leerValor(limitA, "el límite inferior (a)");
        if (a == null) {
            return;
        }
        Double b = leerValor(limitB, "el límite superior (b)");
        if (b == null) {
            return;
        }
        try {
            double res = Calculus.integral(funcIntegralInput.getText(), a, b, 1000);
            integralResult.setText("Resultado: " + FormatoNumero.formatear(res, DIGITOS_INTEGRAL));
        } catch (ArithmeticException ex) {
            mostrarError("La función no está definida en algún punto del intervalo (división entre cero).");
        } catch (IllegalArgumentException ex) {
            mostrarError("Sintaxis inválida en la función f(x).");
        }
    }

    /**
     * Lee un campo numérico aceptando expresiones como "pi/2" o "2*e".
     * Devuelve null (y avisa al usuario) si el valor no es válido.
     */
    private Double leerValor(JTextField campo, String nombre) {
        try {
            double valor = EvaluadorMath.evaluar(campo.getText());
            if (Double.isFinite(valor)) {
                return valor;
            }
        } catch (RuntimeException ex) {
            // se informa abajo
        }
        mostrarError("Revisa " + nombre + ": debe ser un número o una expresión como pi/2.");
        campo.requestFocusInWindow();
        return null;
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
