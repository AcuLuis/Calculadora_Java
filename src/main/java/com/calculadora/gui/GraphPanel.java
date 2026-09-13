package com.calculadora.gui;

import com.calculadora.math.EvaluadorMath;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

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
        String func = funcInput.getText();
        double minX;
        double maxX;
        try {
            minX = Double.parseDouble(minXInput.getText());
            maxX = Double.parseDouble(maxXInput.getText());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Límites de X inválidos.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (minX >= maxX) {
            JOptionPane.showMessageDialog(this, "El límite mínimo debe ser menor al máximo.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int points = 800; // Más puntos para mayor suavidad
        double[] xData = new double[points];
        double[] yData = new double[points];
        double step = (maxX - minX) / (points - 1);
        boolean hasValidPoints = false;

        for (int i = 0; i < points; i++) {
            double x = minX + i * step;
            xData[i] = x;
            try {
                double y = EvaluadorMath.evaluarFuncion(func, x);
                // Evitar NaN e Infinity en las gráficas
                if (Double.isNaN(y) || Double.isInfinite(y)) {
                    yData[i] = 0; 
                } else {
                    yData[i] = y;
                    hasValidPoints = true;
                }
            } catch (Exception ex) {
                if (e != null) { // Solo mostrar error si fue provocado por el botón
                    JOptionPane.showMessageDialog(this, "Sintaxis inválida en la función.", "Error", JOptionPane.ERROR_MESSAGE);
                }
                return;
            }
        }

        if (!hasValidPoints && e != null) {
            JOptionPane.showMessageDialog(this, "La función no produjo puntos reales en este rango.", "Advertencia", JOptionPane.WARNING_MESSAGE);
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
        chart.getStyler().setSeriesColors(new Color[] { new Color(0, 200, 255) }); // Línea azul neón

        chart.addSeries(func, xData, yData);

        chartContainer.removeAll();
        chartContainer.add(new XChartPanel<>(chart), BorderLayout.CENTER);
        chartContainer.revalidate();
        chartContainer.repaint();
    }
}
