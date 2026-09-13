package com.calculadora.gui;

import com.calculadora.math.Calculus;

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
        try {
            String func = funcInput.getText();
            double x = Double.parseDouble(pointInput.getText());
            double res = Calculus.derivada(func, x);
            derivativeResult.setText(String.format("Resultado: %.6f", res));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Sintaxis o valor incorrecto.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void calculateIntegral(ActionEvent e) {
        try {
            String func = funcIntegralInput.getText();
            double a = Double.parseDouble(limitA.getText());
            double b = Double.parseDouble(limitB.getText());
            double res = Calculus.integral(func, a, b, 1000); 
            integralResult.setText(String.format("Resultado: %.6f", res));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Sintaxis o límites incorrectos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
