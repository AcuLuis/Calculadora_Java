package com.calculadora.gui;

import com.calculadora.math.Calculus;
import com.calculadora.math.EvaluadorMath;
import com.calculadora.math.FormatoNumero;
import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionListener;

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
    private final Color COLOR_OK = new Color(76, 175, 80);
    private final Color COLOR_ERROR = new Color(255, 99, 99);

    private final Font labelFont = new Font("Segoe UI", Font.PLAIN, 18);
    private final Font inputFont = new Font("Consolas", Font.BOLD, 20);
    private final Font resultFont = new Font("Segoe UI", Font.BOLD, 22);
    private final Font errorFont = new Font("Segoe UI", Font.BOLD, 16);

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
        JPanel panel = crearFormulario();
        ActionListener calcular = e -> calculateDerivative();

        funcInput = crearCampo("x^2", "ej: x^3 - 2*x", calcular);
        pointInput = crearCampo("3", "ej: 2 o pi/4", calcular);
        agregarFila(panel, 0, "Función f(x):", funcInput);
        agregarFila(panel, 1, "Evaluar en x =", pointInput);

        derivativeResult = crearEtiquetaResultado();
        agregarAcciones(panel, 2, createStyledButton("Calcular Derivada f'(x)", calcular), derivativeResult);
        return panel;
    }

    private JPanel createIntegralPanel() {
        JPanel panel = crearFormulario();
        ActionListener calcular = e -> calculateIntegral();

        funcIntegralInput = crearCampo("x^2", "ej: sin(x)", calcular);
        limitA = crearCampo("0", "ej: 0", calcular);
        limitB = crearCampo("1", "ej: pi", calcular);
        agregarFila(panel, 0, "Función f(x):", funcIntegralInput);
        agregarFila(panel, 1, "Límite inferior (a):", limitA);
        agregarFila(panel, 2, "Límite superior (b):", limitB);

        integralResult = crearEtiquetaResultado();
        agregarAcciones(panel, 3, createStyledButton("Calcular Integral ∫f(x)dx", calcular), integralResult);
        return panel;
    }

    // ------------------------------------------------------------------ construcción

    private JPanel crearFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COLOR_BG);
        panel.setBorder(new EmptyBorder(30, 40, 30, 40));
        return panel;
    }

    /** Fila etiqueta + campo; la columna de etiquetas siempre toma su ancho completo. */
    private void agregarFila(JPanel panel, int fila, String texto, JTextField campo) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 15, 12, 15);
        gbc.gridy = fila;

        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(labelFont);
        etiqueta.setLabelFor(campo);
        etiqueta.setMinimumSize(etiqueta.getPreferredSize());
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.LINE_START;
        panel.add(etiqueta, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(campo, gbc);
    }

    private void agregarAcciones(JPanel panel, int fila, JButton boton, JLabel resultado) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 15, 12, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 1;
        gbc.gridy = fila;
        panel.add(boton, gbc);

        gbc.gridx = 0;
        gbc.gridy = fila + 1;
        gbc.gridwidth = 2;
        panel.add(resultado, gbc);
    }

    private JTextField crearCampo(String valor, String ejemplo, ActionListener alPulsarEnter) {
        JTextField campo = new JTextField(valor);
        campo.setFont(inputFont);
        campo.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, ejemplo);
        campo.addActionListener(alPulsarEnter); // Enter calcula
        campo.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                marcarError(campo, false);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                marcarError(campo, false);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });
        return campo;
    }

    private JLabel crearEtiquetaResultado() {
        JLabel label = new JLabel("Resultado: --");
        label.setFont(resultFont);
        label.setForeground(COLOR_OK);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }

    private JButton createStyledButton(String text, ActionListener accion) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btn.setBackground(COLOR_BTN);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        btn.addActionListener(accion);
        return btn;
    }

    // ------------------------------------------------------------------ cálculos

    private void calculateDerivative() {
        marcarError(funcInput, false);
        Double x = leerValor(pointInput, "el punto x", derivativeResult);
        if (x == null) {
            return;
        }
        try {
            double res = Calculus.derivada(funcInput.getText(), x);
            mostrarResultado(derivativeResult, "f'(" + FormatoNumero.formatear(x) + ") ≈ "
                    + FormatoNumero.formatear(res, DIGITOS_DERIVADA));
        } catch (ArithmeticException ex) {
            mostrarError(derivativeResult, "La función no está definida cerca de x = " + FormatoNumero.formatear(x), pointInput);
        } catch (IllegalArgumentException ex) {
            mostrarError(derivativeResult, "Sintaxis inválida en f(x). Usa x como variable.", funcInput);
        }
    }

    private void calculateIntegral() {
        marcarError(funcIntegralInput, false);
        Double a = leerValor(limitA, "el límite inferior (a)", integralResult);
        if (a == null) {
            return;
        }
        Double b = leerValor(limitB, "el límite superior (b)", integralResult);
        if (b == null) {
            return;
        }
        try {
            double res = Calculus.integral(funcIntegralInput.getText(), a, b, 1000);
            mostrarResultado(integralResult, "∫ f(x) dx ≈ " + FormatoNumero.formatear(res, DIGITOS_INTEGRAL));
        } catch (ArithmeticException ex) {
            mostrarError(integralResult, "f(x) no está definida en algún punto del intervalo (división entre cero).", funcIntegralInput);
        } catch (IllegalArgumentException ex) {
            mostrarError(integralResult, "Sintaxis inválida en f(x). Usa x como variable.", funcIntegralInput);
        }
    }

    /**
     * Lee un campo numérico aceptando expresiones como "pi/2" o "2*e".
     * Devuelve null (y avisa en la etiqueta de resultado) si el valor no es válido.
     */
    private Double leerValor(JTextField campo, String nombre, JLabel resultado) {
        try {
            double valor = EvaluadorMath.evaluar(campo.getText());
            if (Double.isFinite(valor)) {
                marcarError(campo, false);
                return valor;
            }
        } catch (RuntimeException ex) {
            // se informa abajo
        }
        mostrarError(resultado, "Revisa " + nombre + ": usa un número o una expresión como pi/2.", campo);
        return null;
    }

    private void mostrarResultado(JLabel label, String texto) {
        label.setFont(resultFont);
        label.setForeground(COLOR_OK);
        label.setText(texto);
    }

    private void mostrarError(JLabel label, String mensaje, JTextField campo) {
        label.setFont(errorFont);
        label.setForeground(COLOR_ERROR);
        label.setText(mensaje);
        marcarError(campo, true);
        campo.requestFocusInWindow();
    }

    private static void marcarError(JTextField campo, boolean error) {
        campo.putClientProperty(FlatClientProperties.OUTLINE, error ? FlatClientProperties.OUTLINE_ERROR : null);
    }
}
