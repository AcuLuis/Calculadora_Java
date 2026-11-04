package com.calculadora.gui;

import com.calculadora.math.EvaluadorMath;
import com.calculadora.math.FormatoNumero;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

public class StandardPanel extends JPanel {

    private JTextField display;
    private boolean mostrandoResultado = false;

    // Colores para el diseño
    private final Color COLOR_BG = new Color(30, 30, 30);
    private final Color COLOR_DISPLAY_BG = new Color(20, 20, 20);
    private final Color COLOR_DISPLAY_FG = new Color(0, 255, 150); // Verde estilo terminal/retro
    private final Color COLOR_BTN_NUM = new Color(60, 63, 65);
    private final Color COLOR_BTN_OP = new Color(255, 152, 0); // Naranja para operadores
    private final Color COLOR_BTN_FUNC = new Color(43, 43, 43); // Gris oscuro para funciones
    private final Color COLOR_BTN_CLEAR = new Color(244, 67, 54); // Rojo para limpiar
    private final Color COLOR_BTN_EQ = new Color(76, 175, 80); // Verde para igual

    public StandardPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(COLOR_BG);
        setBorder(new EmptyBorder(25, 25, 25, 25));

        // Display
        display = new JTextField();
        display.setFont(new Font("Consolas", Font.BOLD, 48));
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setBackground(COLOR_DISPLAY_BG);
        display.setForeground(COLOR_DISPLAY_FG);
        display.setCaretColor(COLOR_DISPLAY_FG);
        display.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 100, 100), 2, true),
                new EmptyBorder(15, 15, 15, 15)
        ));
        display.setEditable(false);
        add(display, BorderLayout.NORTH);

        // Botones
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(6, 5, 10, 10));
        buttonPanel.setBackground(COLOR_BG);

        String[] buttons = {
            "(", ")", "C", "DEL", "/",
            "7", "8", "9", "*", "sin",
            "4", "5", "6", "-", "cos",
            "1", "2", "3", "+", "tan",
            "0", ".", "sqrt", "^", "log",
            "pi", "e", "ln", "", "="
        };

        for (String text : buttons) {
            if (text.isEmpty()) {
                JPanel empty = new JPanel();
                empty.setBackground(COLOR_BG);
                buttonPanel.add(empty);
                continue;
            }
            JButton button = createStyledButton(text);
            buttonPanel.add(button);
        }

        add(buttonPanel, BorderLayout.CENTER);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 22));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80), 1, true));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Asignar colores según el tipo de botón
        if (text.matches("[0-9]") || text.equals(".")) {
            button.setBackground(COLOR_BTN_NUM);
            button.setForeground(Color.WHITE);
        } else if (text.matches("[+\\-*/^]")) {
            button.setBackground(COLOR_BTN_OP);
            button.setForeground(Color.WHITE);
        } else if (text.equals("C") || text.equals("DEL")) {
            button.setBackground(COLOR_BTN_CLEAR);
            button.setForeground(Color.WHITE);
        } else if (text.equals("=")) {
            button.setBackground(COLOR_BTN_EQ);
            button.setForeground(Color.WHITE);
        } else {
            button.setBackground(COLOR_BTN_FUNC);
            button.setForeground(Color.LIGHT_GRAY);
        }

        button.addActionListener(this::onButtonClicked);
        return button;
    }

    private void onButtonClicked(ActionEvent e) {
        String command = ((JButton) e.getSource()).getText();

        if (mostrandoResultado) {
            prepararTrasResultado(command);
        }
        String currentText = display.getText();

        switch (command) {
            case "=":
                evaluar();
                return;
            case "C":
                display.setText("");
                break;
            case "DEL":
                if (!currentText.isEmpty()) {
                    display.setText(currentText.substring(0, currentText.length() - 1));
                }
                break;
            case "sin":
            case "cos":
            case "tan":
            case "sqrt":
            case "log":
            case "ln":
                display.setText(currentText + command + "(");
                break;
            default:
                display.setText(currentText + command);
                break;
        }
        mostrandoResultado = false;
    }

    /**
     * Decide qué hacer con el resultado que está en pantalla cuando se pulsa
     * un nuevo botón: un operador continúa la operación usando el resultado,
     * cualquier otra entrada empieza una operación nueva.
     */
    private void prepararTrasResultado(String command) {
        String resultado = display.getText();
        boolean esOperador = command.matches("[+\\-*/^]");

        if (command.equals("=")) {
            return;
        }
        if (esOperador && FormatoNumero.esNumero(resultado)) {
            // Un negativo se envuelve en paréntesis para que "-4" seguido de "^2" sea 16 y no -16
            if (resultado.startsWith("-")) {
                display.setText("(" + resultado + ")");
            }
            return;
        }
        display.setText("");
    }

    private void evaluar() {
        String expr = display.getText();
        if (expr.isEmpty() || mostrandoResultado) {
            return;
        }
        try {
            double result = EvaluadorMath.evaluar(expr);
            display.setText(FormatoNumero.formatear(result));
            mostrandoResultado = true;
        } catch (ArithmeticException ex) {
            JOptionPane.showMessageDialog(this, "No se puede dividir entre cero.", "Error matemático", JOptionPane.WARNING_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Expresión inválida. Revisa los operadores y paréntesis.", "Error de sintaxis", JOptionPane.WARNING_MESSAGE);
        }
    }
}
