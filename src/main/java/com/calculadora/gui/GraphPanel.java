package com.calculadora.gui;

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
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.List;

public class GraphPanel extends JPanel {

    /** Máximo de funciones que se pueden dibujar a la vez (una por color de la paleta). */
    private static final int MAX_FUNCIONES = Tema.PALETA.length;
    private static final int PUNTOS = 800; // más puntos, curva más suave
    private static final double FACTOR_ZOOM = 1.2;
    private static final String AYUDA = "Rueda del ratón: zoom  ·  Arrastrar: mover  ·  Doble clic: vista inicial  ·  Separa varias funciones con ;";

    private JTextField funcInput;
    private JTextField minXInput;
    private JTextField maxXInput;
    private JLabel estadoLabel;
    private JLabel coordenadasLabel;
    private GraficaFuncion grafica;


    /** Funciones dibujadas actualmente y su texto original. */
    private final List<Expression> expresiones = new ArrayList<>();
    private final List<String> textos = new ArrayList<>();
    /** Ventana visible y la que se fijó al pulsar "Trazar" (para volver a ella). */
    private double vistaMin;
    private double vistaMax;
    private double inicialMin;
    private double inicialMax;
    private double[] rangoYActual;

    public GraphPanel() {
        setLayout(new BorderLayout());
        setBackground(Tema.FONDO_PANEL);

        add(crearControles(), BorderLayout.NORTH);

        grafica = new GraficaFuncion();
        grafica.setBorder(new EmptyBorder(6, 20, 0, 20));
        add(grafica, BorderLayout.CENTER);
        instalarInteraccionRaton();

        coordenadasLabel = new JLabel(AYUDA);
        coordenadasLabel.setFont(Tema.fuente(Font.PLAIN, 14));
        coordenadasLabel.setForeground(Tema.TEXTO_SECUNDARIO);
        coordenadasLabel.setBorder(new EmptyBorder(6, 24, 12, 20));
        add(coordenadasLabel, BorderLayout.SOUTH);

        // Graficar algo por defecto al iniciar
        SwingUtilities.invokeLater(() -> plotFunction(null));
    }

    // ------------------------------------------------------------------ construcción

    private JPanel crearControles() {
        JPanel controlPanel = new JPanel(new GridBagLayout());
        controlPanel.setBackground(Tema.FONDO_PANEL);
        controlPanel.setBorder(new EmptyBorder(16, 20, 0, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = Tema.fuente(Font.BOLD, 16);
        Font inputFont = Tema.fuenteMono(Font.PLAIN, 18);

        JLabel lblFunc = new JLabel("f(x) =");
        lblFunc.setFont(labelFont);
        gbc.gridx = 0; gbc.gridy = 0;
        controlPanel.add(lblFunc, gbc);

        funcInput = crearCampo("sin(x)*x; cos(x)", 20, "ej: sin(x); x^2/10; ln(x)", inputFont);
        // Al cambiar de pestaña no se selecciona todo el texto de la función
        funcInput.putClientProperty(FlatClientProperties.SELECT_ALL_ON_FOCUS_POLICY,
                FlatClientProperties.SELECT_ALL_ON_FOCUS_POLICY_NEVER);
        funcInput.setToolTipText("Usa x como variable. Separa hasta " + MAX_FUNCIONES + " funciones con punto y coma: sin(x); cos(x)");
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1;
        controlPanel.add(funcInput, gbc);

        // Segunda fila en un panel de flujo para que los botones conserven su tamaño
        JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        fila.setOpaque(false);

        JLabel lblMin = new JLabel("X Mín:  ");
        lblMin.setFont(labelFont);
        fila.add(lblMin);

        minXInput = crearCampo("-10", 8, "-10", inputFont);
        minXInput.setToolTipText("Número o expresión, ej: -2*pi");
        fila.add(minXInput);

        JLabel lblMax = new JLabel("     X Máx:  ");
        lblMax.setFont(labelFont);
        fila.add(lblMax);

        maxXInput = crearCampo("10", 8, "10", inputFont);
        maxXInput.setToolTipText("Número o expresión, ej: 2*pi");
        fila.add(maxXInput);

        fila.add(Box.createHorizontalStrut(20));
        JButton plotBtn = crearBoton("Trazar Gráfica", Tema.PURPURA);
        plotBtn.addActionListener(this::plotFunction);
        fila.add(plotBtn);

        fila.add(Box.createHorizontalStrut(10));
        JButton resetBtn = crearBoton("Vista inicial", Tema.BOTON_SECUNDARIO);
        resetBtn.setToolTipText("Vuelve al rango de X Mín a X Máx (también con doble clic en la gráfica)");
        resetBtn.addActionListener(e -> restablecerVista());
        fila.add(resetBtn);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2; gbc.weightx = 0;
        controlPanel.add(fila, gbc);

        // Línea de estado: muestra los errores sin abrir ventanas emergentes
        estadoLabel = new JLabel(" ");
        estadoLabel.setFont(Tema.fuente(Font.PLAIN, 15));
        estadoLabel.setForeground(Tema.ERROR);
        gbc.gridx = 1; gbc.gridy = 2; gbc.gridwidth = 1;
        controlPanel.add(estadoLabel, gbc);
        return controlPanel;
    }

    private JButton crearBoton(String texto, Color fondo) {
        JButton boton = new JButton(texto);
        boton.setFont(Tema.fuente(Font.BOLD, 16));
        boton.setBackground(fondo);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return boton;
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

    // ------------------------------------------------------------------ trazado

    private void plotFunction(ActionEvent e) {
        limpiarErrores();

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

        List<String> funciones = separarFunciones(funcInput.getText());
        if (funciones.isEmpty()) {
            mostrarError("Escribe una función de x, por ejemplo sin(x).", funcInput);
            return;
        }
        if (funciones.size() > MAX_FUNCIONES) {
            mostrarError("Puedes graficar hasta " + MAX_FUNCIONES + " funciones a la vez.", funcInput);
            return;
        }

        List<Expression> compiladas = new ArrayList<>();
        for (int i = 0; i < funciones.size(); i++) {
            try {
                compiladas.add(EvaluadorMath.compilar(funciones.get(i), "x"));
            } catch (IllegalArgumentException ex) {
                String cual = funciones.size() > 1 ? "la función " + (i + 1) + " (" + funciones.get(i) + ")" : "la función";
                mostrarError("Sintaxis inválida en " + cual + ". Usa x como variable.", funcInput);
                return;
            }
        }

        expresiones.clear();
        expresiones.addAll(compiladas);
        textos.clear();
        textos.addAll(funciones);
        inicialMin = minX;
        inicialMax = maxX;
        dibujar(minX, maxX, false);
    }

    /** Divide "sin(x); cos(x)" en sus funciones, ignorando las vacías. */
    private static List<String> separarFunciones(String texto) {
        List<String> funciones = new ArrayList<>();
        for (String parte : texto.split(";")) {
            if (!parte.trim().isEmpty()) {
                funciones.add(parte.trim());
            }
        }
        return funciones;
    }

    /**
     * Dibuja las funciones actuales en [min, max]. Con {@code actualizarCampos}
     * los campos X Mín y X Máx reflejan la nueva ventana (zoom y desplazamiento).
     */
    private void dibujar(double min, double max, boolean actualizarCampos) {
        List<Muestreo.Curva> curvas = new ArrayList<>();
        try {
            for (Expression expresion : expresiones) {
                curvas.add(Muestreo.muestrear(expresion, min, max, PUNTOS));
            }
        } catch (IllegalArgumentException ex) {
            mostrarError("Expresión incompleta en la función. Revisa operadores y paréntesis.", funcInput);
            return;
        }

        boolean algunPunto = false;
        for (Muestreo.Curva c : curvas) {
            algunPunto |= c.tienePuntos;
        }
        if (!algunPunto) {
            mostrarError("La función no tiene valores reales entre X Mín y X Máx.", funcInput);
            return;
        }

        boolean varias = curvas.size() > 1;
        grafica.limpiar();
        for (int i = 0; i < curvas.size(); i++) {
            grafica.agregarCurva(nombreSerie(i, varias), curvas.get(i), Tema.PALETA[i]);
        }
        rangoYActual = Muestreo.rangoY(curvas);
        grafica.setRangos(min, max, rangoYActual);
        grafica.setTitulo(varias ? "Funciones" : "f(x) = " + textos.get(0));
        grafica.setLeyendaVisible(varias);
        grafica.refrescar();

        vistaMin = min;
        vistaMax = max;
        if (actualizarCampos) {
            int digitos = digitosParaVentana(min, max);
            mostrarLimite(minXInput, FormatoNumero.formatear(min, digitos));
            mostrarLimite(maxXInput, FormatoNumero.formatear(max, digitos));
            limpiarErrores();
        }
    }

    /** Escribe el límite y deja visible su inicio (si no, un campo estrecho muestra solo el final). */
    private static void mostrarLimite(JTextField campo, String texto) {
        campo.setText(texto);
        campo.setCaretPosition(0);
    }

    /**
     * Dígitos suficientes para distinguir los límites: con mucho zoom alrededor de
     * x = 3 hacen falta más cifras que con la vista de -10 a 10.
     */
    private static int digitosParaVentana(double min, double max) {
        double magnitud = Math.max(Math.abs(min), Math.abs(max));
        double ancho = max - min;
        int digitos = (int) Math.ceil(Math.log10(Math.max(magnitud, ancho) / ancho)) + 3;
        return Math.max(3, Math.min(15, digitos));
    }

    private String nombreSerie(int indice, boolean varias) {
        return varias ? "f" + (indice + 1) + "(x) = " + textos.get(indice) : "f(x) = " + textos.get(indice);
    }

    private void restablecerVista() {
        if (!expresiones.isEmpty()) {
            dibujar(inicialMin, inicialMax, true);
        }
    }

    // ------------------------------------------------------------------ zoom, desplazamiento y coordenadas

    private void instalarInteraccionRaton() {
        JComponent lienzo = grafica.getLienzo();
        lienzo.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        MouseAdapter raton = new MouseAdapter() {
            private int pixelInicial;
            private double minInicial;
            private double maxInicial;
            private double unidadesPorPixel;

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (expresiones.isEmpty()) {
                    return;
                }
                double centro = grafica.xDesdePixel(e.getX());
                if (centro < vistaMin || centro > vistaMax) {
                    centro = (vistaMin + vistaMax) / 2;
                }
                // Rueda hacia arriba acerca, hacia abajo aleja, manteniendo fijo el punto bajo el cursor
                double factor = Math.pow(FACTOR_ZOOM, e.getPreciseWheelRotation());
                double nuevoMin = centro - (centro - vistaMin) * factor;
                double nuevoMax = centro + (vistaMax - centro) * factor;
                double ancho = nuevoMax - nuevoMin;
                if (ancho > 1e-9 && ancho < 1e9) {
                    dibujar(nuevoMin, nuevoMax, true);
                    mostrarCoordenadas(e);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (!SwingUtilities.isLeftMouseButton(e) || expresiones.isEmpty()) {
                    return;
                }
                pixelInicial = e.getX();
                minInicial = vistaMin;
                maxInicial = vistaMax;
                unidadesPorPixel = (grafica.xDesdePixel(e.getX() + 100) - grafica.xDesdePixel(e.getX())) / 100;
                lienzo.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (!SwingUtilities.isLeftMouseButton(e) || expresiones.isEmpty()) {
                    return;
                }
                double desplazamiento = (pixelInicial - e.getX()) * unidadesPorPixel;
                dibujar(minInicial + desplazamiento, maxInicial + desplazamiento, true);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                lienzo.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {
                    restablecerVista();
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                mostrarCoordenadas(e);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                coordenadasLabel.setText(AYUDA);
            }
        };
        lienzo.addMouseListener(raton);
        lienzo.addMouseMotionListener(raton);
        lienzo.addMouseWheelListener(raton);
    }

    /** Muestra x bajo el cursor y el valor de cada función en ese punto, con su color. */
    private void mostrarCoordenadas(MouseEvent e) {
        double x = grafica.xDesdePixel(e.getX());
        double y = grafica.yDesdePixel(e.getY());
        boolean dentro = x >= vistaMin && x <= vistaMax && rangoYActual != null
                && y >= rangoYActual[0] && y <= rangoYActual[1];
        if (expresiones.isEmpty() || !dentro) {
            coordenadasLabel.setText(AYUDA);
            return;
        }
        StringBuilder sb = new StringBuilder("<html>x = <b>").append(FormatoNumero.formatear(x, 6)).append("</b>");
        boolean varias = expresiones.size() > 1;
        for (int i = 0; i < expresiones.size(); i++) {
            double fx = Muestreo.evaluarSeguro(expresiones.get(i), x);
            String color = String.format("#%06x", Tema.PALETA[i].getRGB() & 0xFFFFFF);
            sb.append("&nbsp;&nbsp;&nbsp;<font color='").append(color).append("'>")
                    .append(varias ? "f" + (i + 1) : "f").append("(x) = <b>")
                    .append(FormatoNumero.formatear(fx, 6)).append("</b></font>");
        }
        coordenadasLabel.setText(sb.append("</html>").toString());
    }

    // ------------------------------------------------------------------ errores

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
}
