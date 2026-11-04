package com.calculadora.gui;

import com.calculadora.math.Calculus;
import com.calculadora.math.Calculus.ResultadoDerivada;
import com.calculadora.math.Calculus.ResultadoIntegral;
import com.calculadora.math.EvaluadorMath;
import com.calculadora.math.FormatoNumero;
import com.calculadora.math.Muestreo;
import com.formdev.flatlaf.FlatClientProperties;
import net.objecthunter.exp4j.Expression;

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
    private JLabel derivativeDetail;
    private GraficaFuncion derivativeChart;

    private JTextField funcIntegralInput;
    private JTextField limitA;
    private JTextField limitB;
    private JLabel integralResult;
    private JLabel integralDetail;
    private GraficaFuncion integralChart;

    private final Color COLOR_BG = new Color(40, 42, 44);
    private final Color COLOR_BTN = new Color(33, 150, 243); // Azul moderno
    private final Color COLOR_OK = new Color(76, 175, 80);
    private final Color COLOR_AVISO = new Color(255, 167, 38);
    private final Color COLOR_ERROR = new Color(255, 99, 99);
    private final Color COLOR_DETALLE = new Color(160, 160, 160);
    private final Color COLOR_CURVA = GraficaFuncion.PALETA[0];
    private final Color COLOR_TANGENTE = GraficaFuncion.PALETA[1];
    private final Color COLOR_AREA = new Color(0, 200, 255, 70);

    private final Font labelFont = new Font("Segoe UI", Font.PLAIN, 17);
    private final Font inputFont = new Font("Consolas", Font.BOLD, 19);
    private final Font resultFont = new Font("Segoe UI", Font.BOLD, 21);
    private final Font detailFont = new Font("Segoe UI", Font.PLAIN, 14);

    // Dígitos significativos que se muestran según la precisión de cada método numérico
    private static final int DIGITOS_DERIVADA = 9;
    private static final int DIGITOS_INTEGRAL_MAX = 10;
    private static final int PUNTOS_GRAFICA = 400;

    public CalculusPanel() {
        setLayout(new BorderLayout());
        setBackground(COLOR_BG);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 16));

        tabs.addTab("  Derivada  ", createDerivativePanel());
        tabs.addTab("  Integral Definida  ", createIntegralPanel());

        add(tabs, BorderLayout.CENTER);

        // Calcular los ejemplos al iniciar para que las gráficas no aparezcan vacías
        SwingUtilities.invokeLater(() -> {
            calculateDerivative();
            calculateIntegral();
        });
    }

    private JPanel createDerivativePanel() {
        JPanel formulario = crearFormulario();
        ActionListener calcular = e -> calculateDerivative();

        funcInput = crearCampo("x^3 - 2*x", "ej: x^3 - 2*x", calcular);
        pointInput = crearCampo("1", "ej: 2 o pi/4", calcular);
        agregarCampo(formulario, 0, 0, "f(x) =", funcInput, 4);
        agregarCampo(formulario, 1, 0, "x =", pointInput, 1);
        agregarBoton(formulario, 1, 2, createStyledButton("Calcular f'(x)", calcular));

        derivativeResult = crearEtiqueta(resultFont, COLOR_OK);
        derivativeDetail = crearEtiqueta(detailFont, COLOR_DETALLE);
        agregarResultado(formulario, 2, derivativeResult, derivativeDetail);

        derivativeChart = new GraficaFuncion();
        return envolver(formulario, derivativeChart);
    }

    private JPanel createIntegralPanel() {
        JPanel formulario = crearFormulario();
        ActionListener calcular = e -> calculateIntegral();

        funcIntegralInput = crearCampo("sin(x)", "ej: sin(x)", calcular);
        limitA = crearCampo("0", "ej: 0", calcular);
        limitB = crearCampo("pi", "ej: pi", calcular);
        agregarCampo(formulario, 0, 0, "f(x) =", funcIntegralInput, 4);
        agregarCampo(formulario, 1, 0, "a =", limitA, 1);
        agregarCampo(formulario, 1, 2, "b =", limitB, 1);
        agregarBoton(formulario, 1, 4, createStyledButton("Calcular ∫ f(x) dx", calcular));

        integralResult = crearEtiqueta(resultFont, COLOR_OK);
        integralDetail = crearEtiqueta(detailFont, COLOR_DETALLE);
        agregarResultado(formulario, 2, integralResult, integralDetail);

        integralChart = new GraficaFuncion();
        return envolver(formulario, integralChart);
    }

    // ------------------------------------------------------------------ construcción

    private JPanel crearFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COLOR_BG);
        panel.setBorder(new EmptyBorder(14, 24, 4, 24));
        return panel;
    }

    private JPanel envolver(JPanel formulario, GraficaFuncion grafica) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_BG);
        panel.add(formulario, BorderLayout.NORTH);
        grafica.setBorder(new EmptyBorder(4, 20, 16, 20));
        panel.add(grafica, BorderLayout.CENTER);
        return panel;
    }

    /** Etiqueta + campo en la fila y columna indicadas; la etiqueta toma siempre su ancho completo. */
    private void agregarCampo(JPanel panel, int fila, int columna, String texto, JTextField campo, int anchoCampo) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.gridy = fila;

        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(labelFont);
        etiqueta.setLabelFor(campo);
        etiqueta.setMinimumSize(etiqueta.getPreferredSize());
        gbc.gridx = columna;
        gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(etiqueta, gbc);

        gbc.gridx = columna + 1;
        gbc.gridwidth = anchoCampo;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(campo, gbc);
    }

    private void agregarBoton(JPanel panel, int fila, int columna, JButton boton) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.gridx = columna;
        gbc.gridy = fila;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(boton, gbc);
    }

    private void agregarResultado(JPanel panel, int fila, JLabel resultado, JLabel detalle) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 8, 0, 8);
        gbc.gridx = 0;
        gbc.gridy = fila;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(resultado, gbc);
        gbc.gridy = fila + 1;
        gbc.insets = new Insets(0, 8, 4, 8);
        panel.add(detalle, gbc);
    }

    private JTextField crearCampo(String valor, String ejemplo, ActionListener alPulsarEnter) {
        JTextField campo = new JTextField(valor, 8);
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

    private JLabel crearEtiqueta(Font fuente, Color color) {
        JLabel label = new JLabel(" ");
        label.setFont(fuente);
        label.setForeground(color);
        return label;
    }

    private JButton createStyledButton(String text, ActionListener accion) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setBackground(COLOR_BTN);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btn.addActionListener(accion);
        return btn;
    }

    // ------------------------------------------------------------------ derivada

    private void calculateDerivative() {
        marcarError(funcInput, false);
        Double x = leerValor(pointInput, "el punto x", derivativeResult, derivativeDetail);
        if (x == null) {
            return;
        }
        String texto = funcInput.getText();
        ResultadoDerivada r;
        Expression expresion;
        try {
            r = Calculus.analizarDerivada(texto, x);
            expresion = EvaluadorMath.compilar(texto, "x");
        } catch (IllegalArgumentException ex) {
            mostrarError(derivativeResult, derivativeDetail, "Sintaxis inválida en f(x). Usa x como variable.", funcInput);
            return;
        }

        String punto = FormatoNumero.formatear(x);
        switch (r.estado) {
            case EXISTE:
                mostrar(derivativeResult, COLOR_OK, "f'(" + punto + ") ≈ " + FormatoNumero.formatear(r.valor, DIGITOS_DERIVADA));
                derivativeDetail.setText("Recta tangente: " + ecuacionRecta(r.valor, r.fx - r.valor * x));
                break;
            case NO_DEFINIDA:
                mostrar(derivativeResult, COLOR_AVISO, "f'(" + punto + ") no existe");
                derivativeDetail.setText("f(x) no está definida en x = " + punto + ".");
                break;
            case DISCONTINUA:
                mostrar(derivativeResult, COLOR_AVISO, "f'(" + punto + ") no existe");
                derivativeDetail.setText("f(x) da un salto en x = " + punto + ": no es continua ahí.");
                break;
            case ESQUINA:
                mostrar(derivativeResult, COLOR_AVISO, "f'(" + punto + ") no existe");
                derivativeDetail.setText("Las derivadas laterales no coinciden: por la izquierda ≈ "
                        + FormatoNumero.formatear(r.izquierda, 6) + ", por la derecha ≈ " + FormatoNumero.formatear(r.derecha, 6) + ".");
                break;
            case TANGENTE_VERTICAL:
                mostrar(derivativeResult, COLOR_AVISO, "f'(" + punto + ") no existe (tangente vertical)");
                derivativeDetail.setText("La pendiente crece sin límite al acercarse a x = " + punto + ".");
                break;
            case SOLO_LATERAL:
                boolean porDerecha = Double.isNaN(r.izquierda);
                mostrar(derivativeResult, COLOR_AVISO, "f'(" + punto + ") solo existe por la " + (porDerecha ? "derecha" : "izquierda"));
                derivativeDetail.setText("f(x) no está definida del otro lado. Derivada lateral ≈ "
                        + FormatoNumero.formatear(r.valor, 6) + ".");
                break;
            default:
                break;
        }
        graficarDerivada(expresion, x, r);
    }

    private void graficarDerivada(Expression expresion, double x0, ResultadoDerivada r) {
        double mitad = Math.max(3, 0.5 * Math.abs(x0));
        double min = x0 - mitad;
        double max = x0 + mitad;
        Muestreo.Curva curva = Muestreo.muestrear(expresion, min, max, PUNTOS_GRAFICA);
        if (!curva.tienePuntos) {
            return; // se conserva la gráfica anterior
        }
        double[] rangoY = GraficaFuncion.rangoY(curva);

        derivativeChart.limpiar();
        derivativeChart.agregarCurva("f(x)", curva, COLOR_CURVA);
        boolean puntoDefinido = !Double.isNaN(r.fx);
        if (r.existe()) {
            double b = r.fx - r.valor * x0;
            derivativeChart.agregarRecta("Tangente", min, r.valor * min + b, max, r.valor * max + b, COLOR_TANGENTE, true);
        } else if (r.estado == ResultadoDerivada.Estado.TANGENTE_VERTICAL) {
            derivativeChart.agregarRecta("Tangente vertical", x0, rangoY[0], x0, rangoY[1], COLOR_TANGENTE, true);
        }
        if (puntoDefinido) {
            derivativeChart.agregarPunto("Punto", x0, r.fx, COLOR_TANGENTE);
        }
        derivativeChart.setRangos(min, max, rangoY);
        derivativeChart.setTitulo("f(x) = " + funcInput.getText().trim());
        derivativeChart.setLeyendaVisible(r.existe() || r.estado == ResultadoDerivada.Estado.TANGENTE_VERTICAL);
        derivativeChart.refrescar();
    }

    /** "y = 10x − 16" con los signos y coeficientes simplificados. */
    private static String ecuacionRecta(double m, double b) {
        StringBuilder sb = new StringBuilder("y = ");
        boolean hayTermino = false;
        if (Math.abs(m) > 1e-12) {
            String pendiente = FormatoNumero.formatear(m, 6);
            sb.append(pendiente.equals("1") ? "" : pendiente.equals("-1") ? "−" : pendiente.replace('-', '−')).append("x");
            hayTermino = true;
        }
        if (Math.abs(b) > 1e-12 || !hayTermino) {
            String constante = FormatoNumero.formatear(Math.abs(b) > 1e-12 ? Math.abs(b) : 0, 6);
            if (hayTermino) {
                sb.append(b < 0 ? " − " : " + ").append(constante);
            } else {
                sb.append(b < 0 ? "−" : "").append(constante);
            }
        }
        return sb.toString();
    }

    // ------------------------------------------------------------------ integral

    private void calculateIntegral() {
        marcarError(funcIntegralInput, false);
        Double a = leerValor(limitA, "el límite inferior (a)", integralResult, integralDetail);
        if (a == null) {
            return;
        }
        Double b = leerValor(limitB, "el límite superior (b)", integralResult, integralDetail);
        if (b == null) {
            return;
        }
        String texto = funcIntegralInput.getText();
        ResultadoIntegral r;
        Expression expresion;
        try {
            r = Calculus.integrar(texto, a, b);
            expresion = EvaluadorMath.compilar(texto, "x");
        } catch (IllegalArgumentException ex) {
            mostrarError(integralResult, integralDetail, "Sintaxis inválida en f(x). Usa x como variable.", funcIntegralInput);
            return;
        }

        String intervalo = "de a = " + FormatoNumero.formatear(a) + " a b = " + FormatoNumero.formatear(b);
        switch (r.estado) {
            case CONVERGE:
                mostrar(integralResult, COLOR_OK, "∫ f(x) dx ≈ " + FormatoNumero.formatear(r.valor, digitosSegunError(r)));
                String error = r.errorEstimado < 1e-15 ? "< 1E-15" : "≈ " + FormatoNumero.formatear(r.errorEstimado, 2);
                integralDetail.setText(intervalo + "  ·  error estimado " + error
                        + "  ·  " + r.evaluaciones + " evaluaciones de f(x)");
                break;
            case NO_CONVERGE:
                mostrar(integralResult, COLOR_AVISO, "La integral no converge");
                integralDetail.setText("f(x) parece tener una asíntota en el intervalo. Valor aproximado ≈ "
                        + FormatoNumero.formatear(r.valor, 6) + ", pero no es confiable.");
                break;
            case NO_DEFINIDA:
                mostrar(integralResult, COLOR_AVISO, "No se puede integrar en ese intervalo");
                integralDetail.setText("f(x) no tiene valores reales en parte del intervalo " + intervalo.substring(3) + ".");
                break;
            default:
                break;
        }
        graficarIntegral(expresion, a, b, r);
    }

    /** Muestra solo los dígitos que el error estimado garantiza. */
    private static int digitosSegunError(ResultadoIntegral r) {
        if (r.valor == 0 || r.errorEstimado <= 0) {
            return DIGITOS_INTEGRAL_MAX;
        }
        int digitos = (int) Math.floor(-Math.log10(r.errorEstimado / Math.abs(r.valor)));
        return Math.max(3, Math.min(DIGITOS_INTEGRAL_MAX, digitos));
    }

    private void graficarIntegral(Expression expresion, double a, double b, ResultadoIntegral r) {
        double desde = Math.min(a, b);
        double hasta = Math.max(a, b);
        double margen = desde == hasta ? 1 : 0.25 * (hasta - desde);
        double min = desde - margen;
        double max = hasta + margen;

        Muestreo.Curva curva = Muestreo.muestrear(expresion, min, max, PUNTOS_GRAFICA);
        if (!curva.tienePuntos) {
            return; // se conserva la gráfica anterior
        }
        integralChart.limpiar();
        if (r.converge() && desde < hasta) {
            Muestreo.Curva region = Muestreo.muestrear(expresion, desde, hasta, PUNTOS_GRAFICA);
            integralChart.agregarArea("Área", region.x, region.y, COLOR_AREA);
        }
        integralChart.agregarCurva("f(x)", curva, COLOR_CURVA);
        integralChart.setRangos(min, max, GraficaFuncion.rangoY(curva));
        integralChart.setTitulo("f(x) = " + funcIntegralInput.getText().trim());
        integralChart.refrescar();
    }

    // ------------------------------------------------------------------ utilidades

    /**
     * Lee un campo numérico aceptando expresiones como "pi/2" o "2*e".
     * Devuelve null (y avisa en la etiqueta de resultado) si el valor no es válido.
     */
    private Double leerValor(JTextField campo, String nombre, JLabel resultado, JLabel detalle) {
        try {
            double valor = EvaluadorMath.evaluar(campo.getText());
            if (Double.isFinite(valor)) {
                marcarError(campo, false);
                return valor;
            }
        } catch (RuntimeException ex) {
            // se informa abajo
        }
        mostrarError(resultado, detalle, "Revisa " + nombre + ": usa un número o una expresión como pi/2.", campo);
        return null;
    }

    private void mostrar(JLabel label, Color color, String texto) {
        label.setFont(resultFont);
        label.setForeground(color);
        label.setText(texto);
    }

    private void mostrarError(JLabel label, JLabel detalle, String mensaje, JTextField campo) {
        mostrar(label, COLOR_ERROR, mensaje);
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        detalle.setText(" ");
        marcarError(campo, true);
        campo.requestFocusInWindow();
    }

    private static void marcarError(JTextField campo, boolean error) {
        campo.putClientProperty(FlatClientProperties.OUTLINE, error ? FlatClientProperties.OUTLINE_ERROR : null);
    }
}
