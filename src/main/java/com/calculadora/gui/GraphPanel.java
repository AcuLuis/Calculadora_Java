package com.calculadora.gui;

import com.calculadora.math.EvaluadorMath;
import net.objecthunter.exp4j.Expression;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class GraphPanel extends JPanel {

    private JTextField funcInput;
    private JTextField minXInput;
    private JTextField maxXInput;
    private JPanel chartContainer;
    
    private final Color COLOR_BG = new Color(40, 42, 44);
    private final Color COLOR_BTN = new Color(156, 39, 176); // Púrpura moderno

    public GraphPanel() {
        setLayout(new BorderLayout());
        setBackground(COLOR_BG);

        // Panel superior de controles
        JPanel controlPanel = new JPanel(new GridBagLayout());
        controlPanel.setBackground(COLOR_BG);
        controlPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        Font labelFont = new Font("Segoe UI", Font.BOLD, 16);
        Font inputFont = new Font("Consolas", Font.PLAIN, 18);

        JLabel lblFunc = new JLabel("f(x) =");
        lblFunc.setFont(labelFont);
        gbc.gridx = 0; gbc.gridy = 0;
        controlPanel.add(lblFunc, gbc);

        funcInput = new JTextField("sin(x)*x", 20);
        funcInput.setFont(inputFont);
        gbc.gridx = 1; gbc.gridy = 0; gbc.gridwidth = 5;
        controlPanel.add(funcInput, gbc);

        JLabel lblMin = new JLabel("X Mín:");
        lblMin.setFont(labelFont);
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        controlPanel.add(lblMin, gbc);

        minXInput = new JTextField("-10", 5);
        minXInput.setFont(inputFont);
        gbc.gridx = 1; gbc.gridy = 1;
        controlPanel.add(minXInput, gbc);

        JLabel lblMax = new JLabel("X Máx:");
        lblMax.setFont(labelFont);
        gbc.gridx = 2; gbc.gridy = 1;
        controlPanel.add(lblMax, gbc);

        maxXInput = new JTextField("10", 5);
        maxXInput.setFont(inputFont);
        gbc.gridx = 3; gbc.gridy = 1;
        controlPanel.add(maxXInput, gbc);

        JButton plotBtn = new JButton("  Trazar Gráfica  ");
        plotBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        plotBtn.setBackground(COLOR_BTN);
        plotBtn.setForeground(Color.WHITE);
        plotBtn.setFocusPainted(false);
        plotBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        plotBtn.addActionListener(this::plotFunction);
        
        gbc.gridx = 4; gbc.gridy = 1; gbc.gridwidth = 2;
        controlPanel.add(plotBtn, gbc);

        add(controlPanel, BorderLayout.NORTH);

        // Contenedor de la gráfica
        chartContainer = new JPanel(new BorderLayout());
        chartContainer.setBackground(COLOR_BG);
        chartContainer.setBorder(new EmptyBorder(10, 20, 20, 20));
        add(chartContainer, BorderLayout.CENTER);
        
        // Graficar algo por defecto al iniciar
        SwingUtilities.invokeLater(() -> plotFunction(null));
    }

    private void plotFunction(ActionEvent e) {
        String func = funcInput.getText().trim();
        double minX;
        double maxX;
        try {
            // Se aceptan expresiones como "-2*pi" o "pi/2" además de números
            minX = EvaluadorMath.evaluar(minXInput.getText());
            maxX = EvaluadorMath.evaluar(maxXInput.getText());
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Límites de X inválidos.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!Double.isFinite(minX) || !Double.isFinite(maxX)) {
            JOptionPane.showMessageDialog(this, "Los límites de X deben ser números finitos.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (minX >= maxX) {
            JOptionPane.showMessageDialog(this, "El límite mínimo debe ser menor al máximo.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Expression expresion;
        try {
            expresion = EvaluadorMath.compilar(func, "x");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Sintaxis inválida en la función.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int points = 800; // Más puntos para mayor suavidad
        List<Double> xData = new ArrayList<>(points + 16);
        List<Double> yData = new ArrayList<>(points + 16);
        double step = (maxX - minX) / (points - 1);
        boolean hasValidPoints = false;
        double prevX = Double.NaN;
        double prevY = Double.NaN;

        try {
            for (int i = 0; i < points; i++) {
                double x = minX + i * step;
                double y = evaluarSeguro(expresion, x);

                // Si entre dos puntos hay una asíntota (p. ej. 1/x en 0) se corta la línea
                if (!Double.isNaN(y) && !Double.isNaN(prevY) && hayAsintota(expresion, prevX, prevY, x, y)) {
                    xData.add((prevX + x) / 2);
                    yData.add(Double.NaN);
                }

                xData.add(x);
                yData.add(y); // NaN deja un hueco en la gráfica en vez de dibujar un 0 falso
                hasValidPoints |= !Double.isNaN(y);
                prevX = x;
                prevY = y;
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Sintaxis inválida en la función.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!hasValidPoints) {
            if (e != null) {
                JOptionPane.showMessageDialog(this, "La función no produjo puntos reales en este rango.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
            return;
        }

        // Configurar estilo de XChart
        XYChart chart = new XYChartBuilder()
                .width(600).height(400)
                .title("f(x) = " + func)
                .xAxisTitle("Eje X")
                .yAxisTitle("Eje Y")
                .build();
                
        // Darle un look oscuro a la gráfica para que combine con FlatDarkLaf
        chart.getStyler().setChartBackgroundColor(new Color(43, 43, 43));
        chart.getStyler().setPlotBackgroundColor(new Color(30, 30, 30));
        chart.getStyler().setPlotGridLinesColor(new Color(80, 80, 80));
        chart.getStyler().setChartFontColor(Color.WHITE);
        chart.getStyler().setAxisTickLabelsColor(Color.LIGHT_GRAY);
        chart.getStyler().setLegendVisible(false);
        chart.getStyler().setMarkerSize(0);
        chart.getStyler().setXAxisDecimalPattern("#.##"); // evita etiquetas como -6.39549436795995
        chart.getStyler().setSeriesColors(new Color[] { new Color(0, 200, 255) }); // Línea azul neón

        chart.addSeries(func, xData, yData);

        chartContainer.removeAll();
        chartContainer.add(new XChartPanel<>(chart), BorderLayout.CENTER);
        chartContainer.revalidate();
        chartContainer.repaint();
    }

    /** Evalúa f(x) devolviendo NaN cuando el punto no tiene un valor real finito. */
    private static double evaluarSeguro(Expression expresion, double x) {
        try {
            double y = expresion.setVariable("x", x).evaluate();
            return Double.isFinite(y) ? y : Double.NaN;
        } catch (ArithmeticException ex) {
            return Double.NaN; // división entre cero en este punto
        }
    }

    /**
     * Detecta un polo entre dos puntos consecutivos: si la función cambia de signo
     * y en el punto medio su magnitud es mayor que en ambos extremos, no es un
     * cruce continuo por cero sino una asíntota vertical.
     */
    private static boolean hayAsintota(Expression expresion, double x1, double y1, double x2, double y2) {
        if (y1 * y2 >= 0) {
            return false;
        }
        double yMedio = evaluarSeguro(expresion, (x1 + x2) / 2);
        return Double.isNaN(yMedio) || Math.abs(yMedio) > Math.max(Math.abs(y1), Math.abs(y2));
    }
}
