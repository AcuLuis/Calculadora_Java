package com.calculadora.gui;

import com.calculadora.math.EvaluadorMath;
import com.formdev.flatlaf.FlatClientProperties;
import net.objecthunter.exp4j.Expression;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class GraphPanel extends JPanel {

    private JTextField funcInput;
    private JTextField minXInput;
    private JTextField maxXInput;
    private JPanel chartContainer;
    private JLabel estadoLabel;

    private final Color COLOR_BG = new Color(40, 42, 44);
    private final Color COLOR_BTN = new Color(156, 39, 176); // Púrpura moderno
    private final Color COLOR_ERROR = new Color(255, 99, 99);

    public GraphPanel() {
        setLayout(new BorderLayout());
        setBackground(COLOR_BG);

        // Panel superior de controles
        JPanel controlPanel = new JPanel(new GridBagLayout());
        controlPanel.setBackground(COLOR_BG);
        controlPanel.setBorder(new EmptyBorder(20, 20, 0, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        Font labelFont = new Font("Segoe UI", Font.BOLD, 16);
        Font inputFont = new Font("Consolas", Font.PLAIN, 18);

        JLabel lblFunc = new JLabel("f(x) =");
        lblFunc.setFont(labelFont);
        gbc.gridx = 0; gbc.gridy = 0;
        controlPanel.add(lblFunc, gbc);

        funcInput = crearCampo("sin(x)*x", 20, "ej: x^2 - 3*x + 1", inputFont);
        // Al cambiar de pestaña no se selecciona todo el texto de la función
        funcInput.putClientProperty(FlatClientProperties.SELECT_ALL_ON_FOCUS_POLICY,
                FlatClientProperties.SELECT_ALL_ON_FOCUS_POLICY_NEVER);
        funcInput.setToolTipText("Usa x como variable. Ej: sin(x), x^2, ln(x), 1/x");
        gbc.gridx = 1; gbc.gridy = 0; gbc.gridwidth = 5;
        controlPanel.add(funcInput, gbc);

        JLabel lblMin = new JLabel("X Mín:");
        lblMin.setFont(labelFont);
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        controlPanel.add(lblMin, gbc);

        minXInput = crearCampo("-10", 5, "-10", inputFont);
        minXInput.setToolTipText("Número o expresión, ej: -2*pi");
        gbc.gridx = 1; gbc.gridy = 1;
        controlPanel.add(minXInput, gbc);

        JLabel lblMax = new JLabel("X Máx:");
        lblMax.setFont(labelFont);
        gbc.gridx = 2; gbc.gridy = 1;
        controlPanel.add(lblMax, gbc);

        maxXInput = crearCampo("10", 5, "10", inputFont);
        maxXInput.setToolTipText("Número o expresión, ej: 2*pi");
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

        // Línea de estado: muestra los errores sin abrir ventanas emergentes
        estadoLabel = new JLabel(" ");
        estadoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        estadoLabel.setForeground(COLOR_ERROR);
        gbc.gridx = 1; gbc.gridy = 2; gbc.gridwidth = 5;
        controlPanel.add(estadoLabel, gbc);

        add(controlPanel, BorderLayout.NORTH);

        // Contenedor de la gráfica
        chartContainer = new JPanel(new BorderLayout());
        chartContainer.setBackground(COLOR_BG);
        chartContainer.setBorder(new EmptyBorder(10, 20, 20, 20));
        add(chartContainer, BorderLayout.CENTER);
        
        // Graficar algo por defecto al iniciar
        SwingUtilities.invokeLater(() -> plotFunction(null));
    }

    private JTextField crearCampo(String valor, int columnas, String ejemplo, Font fuente) {
        JTextField campo = new JTextField(valor, columnas);
        campo.setFont(fuente);
        campo.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, ejemplo);
        campo.addActionListener(this::plotFunction); // Enter traza la gráfica
        // Al corregir el campo se quita el contorno rojo de error
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

    private void plotFunction(ActionEvent e) {
        limpiarErrores();
        String func = funcInput.getText().trim();

        // Se aceptan expresiones como "-2*pi" o "pi/2" además de números
        Double minX = leerLimite(minXInput, "X Mín");
        if (minX == null) {
            return;
        }
        Double maxX = leerLimite(maxXInput, "X Máx");
        if (maxX == null) {
            return;
        }
        if (minX >= maxX) {
            mostrarError("X Mín debe ser menor que X Máx.", minXInput);
            marcarError(maxXInput, true);
            return;
        }

        Expression expresion;
        try {
            expresion = EvaluadorMath.compilar(func, "x");
        } catch (IllegalArgumentException ex) {
            mostrarError(func.isEmpty() ? "Escribe una función de x, por ejemplo sin(x)."
                    : "Sintaxis inválida en la función. Usa x como variable.", funcInput);
            return;
        }

        double[] muestras = generarMuestras(minX, maxX, 800); // más puntos, curva más suave
        List<Double> xData = new ArrayList<>(muestras.length + 16);
        List<Double> yData = new ArrayList<>(muestras.length + 16);
        boolean hasValidPoints = false;
        double prevX = Double.NaN;
        double prevY = Double.NaN;

        try {
            for (double x : muestras) {
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
            mostrarError("Expresión incompleta en la función. Revisa operadores y paréntesis.", funcInput);
            return;
        }

        if (!hasValidPoints) {
            mostrarError("La función no tiene valores reales entre X Mín y X Máx.", funcInput);
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

    /**
     * Puntos de muestreo uniformes en [min, max] más uno extra a mitad del primer
     * intervalo. XChart alinea las marcas del eje X a los datos cuando estos están
     * espaciados exactamente igual (y salen valores como -3.41, -2.83...); con ese
     * punto adicional calcula marcas redondas (-10, -5, 0, 5, 10).
     */
    private static double[] generarMuestras(double min, double max, int cantidad) {
        double paso = (max - min) / (cantidad - 1);
        double[] muestras = new double[cantidad + 1];
        muestras[0] = min;
        muestras[1] = min + paso / 2;
        for (int i = 1; i < cantidad; i++) {
            muestras[i + 1] = min + i * paso;
        }
        return muestras;
    }

    private Double leerLimite(JTextField campo, String nombre) {
        try {
            double valor = EvaluadorMath.evaluar(campo.getText());
            if (Double.isFinite(valor)) {
                return valor;
            }
        } catch (RuntimeException ex) {
            // se informa abajo
        }
        mostrarError(nombre + " debe ser un número o una expresión como -2*pi.", campo);
        return null;
    }

    private void mostrarError(String mensaje, JTextField campo) {
        estadoLabel.setText(mensaje);
        marcarError(campo, true);
        campo.requestFocusInWindow();
    }

    private void limpiarErrores() {
        estadoLabel.setText(" ");
        marcarError(funcInput, false);
        marcarError(minXInput, false);
        marcarError(maxXInput, false);
    }

    private static void marcarError(JTextField campo, boolean error) {
        campo.putClientProperty(FlatClientProperties.OUTLINE, error ? FlatClientProperties.OUTLINE_ERROR : null);
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
