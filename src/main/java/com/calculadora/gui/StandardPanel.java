package com.calculadora.gui;

import com.calculadora.math.EvaluadorMath;
import com.calculadora.math.EvaluadorMath.ModoAngular;
import com.calculadora.math.FormatoNumero;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.HierarchyEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StandardPanel extends JPanel {

    /** Tipo de tecla: define cómo se comporta y de qué color se pinta. */
    private enum Tipo { NUMERO, OPERADOR, FUNCION, CONSTANTE, LIMPIAR, BORRAR, IGUAL, SIGNO, MODO }

    /** Una tecla del teclado en pantalla: lo que se ve, lo que escribe y su ayuda. */
    private static final class Tecla {
        final String etiqueta;
        final String token;
        final Tipo tipo;
        final String ayuda;

        Tecla(String etiqueta, String token, Tipo tipo, String ayuda) {
            this.etiqueta = etiqueta;
            this.token = token;
            this.tipo = tipo;
            this.ayuda = ayuda;
        }
    }

    private static Tecla t(String etiqueta, String token, Tipo tipo) {
        return new Tecla(etiqueta, token, tipo, null);
    }

    private static Tecla t(String etiqueta, String token, Tipo tipo, String ayuda) {
        return new Tecla(etiqueta, token, tipo, ayuda);
    }

    private static final Tecla[] TECLAS = {
        t("RAD", "", Tipo.MODO, "Cambiar entre radianes y grados"),
        t("(", "(", Tipo.NUMERO), t(")", ")", Tipo.OPERADOR),
        t("%", "%", Tipo.OPERADOR, "Porcentaje: 200×10% = 20"),
        t("C", "", Tipo.LIMPIAR, "Borrar todo (Esc)"), t("DEL", "", Tipo.BORRAR, "Borrar el último carácter (Retroceso)"),

        t("sin", "sin(", Tipo.FUNCION), t("x²", "^2", Tipo.OPERADOR, "Elevar al cuadrado"),
        t("7", "7", Tipo.NUMERO), t("8", "8", Tipo.NUMERO), t("9", "9", Tipo.NUMERO), t("÷", "÷", Tipo.OPERADOR),

        t("cos", "cos(", Tipo.FUNCION), t("xʸ", "^", Tipo.OPERADOR, "Potencia"),
        t("4", "4", Tipo.NUMERO), t("5", "5", Tipo.NUMERO), t("6", "6", Tipo.NUMERO), t("×", "×", Tipo.OPERADOR),

        t("tan", "tan(", Tipo.FUNCION), t("√", "√(", Tipo.FUNCION, "Raíz cuadrada"),
        t("1", "1", Tipo.NUMERO), t("2", "2", Tipo.NUMERO), t("3", "3", Tipo.NUMERO), t("−", "−", Tipo.OPERADOR),

        t("ln", "ln(", Tipo.FUNCION, "Logaritmo natural"), t("n!", "!", Tipo.OPERADOR, "Factorial"),
        t("0", "0", Tipo.NUMERO), t(".", ".", Tipo.NUMERO), t("±", "", Tipo.SIGNO, "Cambiar el signo"),
        t("+", "+", Tipo.OPERADOR),

        t("log", "log(", Tipo.FUNCION, "Logaritmo en base 10"), t("π", "π", Tipo.CONSTANTE),
        t("e", "e", Tipo.CONSTANTE), t("x⁻¹", "^(−1)", Tipo.OPERADOR, "Inverso: 1/x"),
        t("ANS", "ANS", Tipo.CONSTANTE, "Último resultado"), t("=", "", Tipo.IGUAL, "Calcular (Enter)")
    };

    /** Tokens que el Retroceso borra completos en vez de letra por letra. */
    private static final Pattern TOKEN_FINAL = Pattern.compile("(?:asin|acos|atan|sin|cos|tan|log|ln|sqrt|abs|√)\\($|ANS$");

    /** Último número del display, opcionalmente ya negado como (−n). */
    private static final Pattern NUMERO_FINAL = Pattern.compile("(\\(−)?(\\d*\\.?\\d+(?:E-?\\d+)?)(\\))?$");

    private static final String COLOR_BASE = "colorBase";

    // Colores para el diseño
    private final Color COLOR_BG = new Color(30, 30, 30);
    private final Color COLOR_DISPLAY_BG = new Color(20, 20, 20);
    private final Color COLOR_DISPLAY_FG = new Color(0, 255, 150); // Verde estilo terminal/retro
    private final Color COLOR_BTN_NUM = new Color(60, 63, 65);
    private final Color COLOR_BTN_OP = new Color(255, 152, 0); // Naranja para operadores
    private final Color COLOR_BTN_FUNC = new Color(43, 43, 43); // Gris oscuro para funciones
    private final Color COLOR_BTN_CLEAR = new Color(244, 67, 54); // Rojo para limpiar
    private final Color COLOR_BTN_EQ = new Color(76, 175, 80); // Verde para igual
    private final Color COLOR_BTN_MODO = new Color(33, 150, 243); // Azul para el modo angular
    private final Color COLOR_INFO = new Color(150, 150, 150);
    private final Color COLOR_ERROR = new Color(255, 99, 99);

    private final Border bordeDisplay = crearBordeDisplay(new Color(100, 100, 100));
    private final Border bordeDisplayError = crearBordeDisplay(COLOR_ERROR);

    private JTextField display;
    private JLabel infoLabel;
    private JButton botonModo;
    private final Map<String, JButton> botonesPorToken = new HashMap<>();

    /** Operaciones calculadas, la más reciente primero: {expresión, resultado}. */
    private final DefaultListModel<String[]> historial = new DefaultListModel<>();
    private JList<String[]> listaHistorial;
    /** Posición al recorrer el historial con ↑ / ↓ (-1 = no se está recorriendo). */
    private int posicionHistorial = -1;
    private static final int MAX_HISTORIAL = 100;

    private boolean mostrandoResultado = false;
    private boolean mostrandoError = false;
    private ModoAngular modo = ModoAngular.RADIANES;
    private double ans = 0;

    public StandardPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(COLOR_BG);
        setBorder(new EmptyBorder(20, 25, 25, 25));
        setFocusable(true);

        add(crearPantalla(), BorderLayout.NORTH);
        add(crearTeclado(), BorderLayout.CENTER);
        add(crearHistorial(), BorderLayout.EAST);

        instalarAtajosDeTeclado();

        // El panel recibe el foco al mostrarse para que el teclado funcione de inmediato
        addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 && isShowing()) {
                SwingUtilities.invokeLater(this::requestFocusInWindow);
            }
        });
        MouseAdapter recuperarFoco = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                requestFocusInWindow();
            }
        };
        addMouseListener(recuperarFoco);
        display.addMouseListener(recuperarFoco);
    }

    // ------------------------------------------------------------------ construcción

    private JPanel crearPantalla() {
        infoLabel = new JLabel(" ");
        infoLabel.setFont(new Font("Consolas", Font.PLAIN, 18));
        infoLabel.setForeground(COLOR_INFO);
        infoLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        infoLabel.setBorder(new EmptyBorder(0, 5, 6, 8));

        display = new JTextField();
        display.setFont(new Font("Consolas", Font.BOLD, 44));
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setBackground(COLOR_DISPLAY_BG);
        display.setForeground(COLOR_DISPLAY_FG);
        display.setBorder(bordeDisplay);
        display.setEditable(false);
        display.setFocusable(false); // el panel maneja el teclado
        display.setToolTipText("Ctrl+C copia el contenido, Ctrl+V pega una expresión");

        JPanel pantalla = new JPanel(new BorderLayout());
        pantalla.setBackground(COLOR_BG);
        pantalla.add(infoLabel, BorderLayout.NORTH);
        pantalla.add(display, BorderLayout.CENTER);
        return pantalla;
    }

    private JPanel crearTeclado() {
        JPanel teclado = new JPanel(new GridLayout(6, 6, 8, 8));
        teclado.setBackground(COLOR_BG);
        for (Tecla tecla : TECLAS) {
            JButton boton = crearBoton(tecla);
            teclado.add(boton);
            if (!tecla.token.isEmpty()) {
                botonesPorToken.put(tecla.token, boton);
            }
            if (tecla.tipo == Tipo.MODO) {
                botonModo = boton;
            }
        }
        return teclado;
    }

    private JButton crearBoton(Tecla tecla) {
        JButton button = new JButton(tecla.etiqueta);
        button.setFont(new Font("Segoe UI", Font.BOLD, 22));
        button.setFocusPainted(false);
        button.setFocusable(false); // así el foco (y el teclado) se quedan en el panel
        button.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80), 1, true));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setToolTipText(tecla.ayuda);
        button.setForeground(Color.WHITE);

        switch (tecla.tipo) {
            case NUMERO:
                button.setBackground(COLOR_BTN_NUM);
                break;
            case OPERADOR:
                // ")" se comporta como operador (continúa la expresión) pero se pinta como "("
                button.setBackground(tecla.token.equals(")") ? COLOR_BTN_NUM : COLOR_BTN_OP);
                break;
            case LIMPIAR:
            case BORRAR:
                button.setBackground(COLOR_BTN_CLEAR);
                break;
            case IGUAL:
                button.setBackground(COLOR_BTN_EQ);
                break;
            case MODO:
                button.setBackground(COLOR_BTN_MODO);
                break;
            default:
                button.setBackground(COLOR_BTN_FUNC);
                button.setForeground(Color.LIGHT_GRAY);
                break;
        }

        button.putClientProperty(COLOR_BASE, button.getBackground());
        button.addActionListener(e -> procesar(tecla));
        return button;
    }

    private Border crearBordeDisplay(Color color) {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 2, true),
                new EmptyBorder(12, 15, 12, 15));
    }

    // ------------------------------------------------------------------ teclado físico

    private void instalarAtajosDeTeclado() {
        InputMap im = getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        ActionMap am = getActionMap();

        // Caracteres que se escriben tal cual o con su símbolo de pantalla
        String desde = "0123456789.,+-*/^()%!";
        String hacia = "0123456789..+−×÷^()%!";
        for (int i = 0; i < desde.length(); i++) {
            String token = String.valueOf(hacia.charAt(i));
            registrar(im, am, KeyStroke.getKeyStroke(desde.charAt(i)), "tecla-" + i, () -> pulsarToken(token));
        }
        // Letras: permiten escribir funciones (sin, sqrt, ...) y constantes (pi, e)
        for (char c = 'a'; c <= 'z'; c++) {
            String letra = String.valueOf(c);
            registrar(im, am, KeyStroke.getKeyStroke(c), "letra-" + c, () -> pulsarToken(letra));
        }

        Runnable igual = () -> pulsar(Tipo.IGUAL);
        registrar(im, am, KeyStroke.getKeyStroke('='), "igual", igual);
        registrar(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "enter", igual);
        registrar(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), "borrar", () -> pulsar(Tipo.BORRAR));
        registrar(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "limpiar", () -> pulsar(Tipo.LIMPIAR));
        registrar(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "suprimir", () -> pulsar(Tipo.LIMPIAR));
        registrar(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK), "copiar", this::copiar);
        registrar(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK), "pegar", this::pegar);
        registrar(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "historial-anterior", () -> navegarHistorial(1));
        registrar(im, am, KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "historial-siguiente", () -> navegarHistorial(-1));
    }

    private void registrar(InputMap im, ActionMap am, KeyStroke ks, String nombre, Runnable accion) {
        im.put(ks, nombre);
        am.put(nombre, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                accion.run();
            }
        });
    }

    /** Simula la pulsación de la tecla con ese token (o escribe el texto si no hay tecla). */
    private void pulsarToken(String token) {
        for (Tecla tecla : TECLAS) {
            if (tecla.token.equals(token)) {
                pulsarTecla(tecla);
                return;
            }
        }
        procesar(t(token, token, Tipo.FUNCION)); // letras sueltas: inician una nueva entrada
    }

    private void pulsar(Tipo tipo) {
        for (Tecla tecla : TECLAS) {
            if (tecla.tipo == tipo) {
                pulsarTecla(tecla);
                return;
            }
        }
    }

    private void pulsarTecla(Tecla tecla) {
        procesar(tecla);
        resaltar(tecla);
    }

    /** Ilumina brevemente el botón en pantalla para dar respuesta visual al teclado. */
    private void resaltar(Tecla tecla) {
        JButton boton = tecla.token.isEmpty() ? buscarBoton(tecla) : botonesPorToken.get(tecla.token);
        if (boton == null) {
            return;
        }
        Color original = (Color) boton.getClientProperty(COLOR_BASE);
        boton.setBackground(original.brighter());
        Timer timer = new Timer(110, e -> boton.setBackground(original));
        timer.setRepeats(false);
        timer.start();
    }

    private JButton buscarBoton(Tecla tecla) {
        Container teclado = (Container) getComponent(1);
        for (Component c : teclado.getComponents()) {
            if (c instanceof JButton && ((JButton) c).getText().equals(tecla.etiqueta)) {
                return (JButton) c;
            }
        }
        return null;
    }

    private void copiar() {
        String texto = display.getText();
        if (!texto.isEmpty()) {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(texto), null);
            mostrarInfo("Copiado al portapapeles", false);
        }
    }

    private void pegar() {
        try {
            String texto = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
            texto = texto.replaceAll("\\s+", "").replace('*', '×').replace('/', '÷').replace('-', '−');
            if (!texto.isEmpty()) {
                procesar(t(texto, texto, Tipo.NUMERO));
            }
        } catch (Exception ex) {
            mostrarInfo("No se pudo pegar el contenido", true);
        }
    }

    // ------------------------------------------------------------------ lógica

    private void procesar(Tecla tecla) {
        posicionHistorial = -1;
        if (mostrandoError) {
            limpiarError();
        }
        switch (tecla.tipo) {
            case IGUAL:
                evaluar();
                return;
            case MODO:
                cambiarModo();
                return;
            case SIGNO:
                cambiarSigno();
                return;
            default:
                break;
        }

        if (mostrandoResultado) {
            prepararTrasResultado(tecla);
        }
        String actual = display.getText();

        switch (tecla.tipo) {
            case LIMPIAR:
                display.setText("");
                infoLabel.setText(" ");
                break;
            case BORRAR:
                Matcher m = TOKEN_FINAL.matcher(actual);
                if (m.find()) {
                    display.setText(actual.substring(0, m.start()));
                } else if (!actual.isEmpty()) {
                    display.setText(actual.substring(0, actual.length() - 1));
                }
                break;
            default:
                display.setText(actual + tecla.token);
                break;
        }
        mostrandoResultado = false;
    }

    /**
     * Decide qué hacer con el resultado que está en pantalla cuando se pulsa
     * una nueva tecla: un operador continúa la operación usando el resultado,
     * cualquier otra entrada empieza una operación nueva.
     */
    private void prepararTrasResultado(Tecla tecla) {
        String resultado = display.getText();
        if (tecla.tipo == Tipo.OPERADOR && FormatoNumero.esNumero(resultado)) {
            // Un negativo se envuelve en paréntesis para que "-4" seguido de "^2" sea 16 y no -16
            if (resultado.startsWith("-")) {
                display.setText("(" + resultado.replace('-', '−') + ")");
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
        expr = cerrarParentesis(expr);
        try {
            double result = EvaluadorMath.evaluar(expr, modo, ans);
            String texto = FormatoNumero.formatear(result);
            mostrarInfo(expr + " =", false);
            display.setText(texto);
            mostrandoResultado = true;
            if (Double.isFinite(result)) {
                ans = result;
            }
            agregarAlHistorial(expr, texto);
        } catch (ArithmeticException ex) {
            mostrarError("No se puede dividir entre cero");
        } catch (IllegalArgumentException ex) {
            mostrarError(describirError(ex));
        }
    }

    /** Agrega los paréntesis de cierre que falten: "sin(30" se evalúa como "sin(30)". */
    private static String cerrarParentesis(String expr) {
        int abiertos = 0;
        for (char c : expr.toCharArray()) {
            if (c == '(') {
                abiertos++;
            } else if (c == ')') {
                abiertos--;
            }
        }
        StringBuilder sb = new StringBuilder(expr);
        for (int i = 0; i < abiertos; i++) {
            sb.append(')');
        }
        return sb.toString();
    }

    private static String describirError(IllegalArgumentException ex) {
        String msg = ex.getMessage() == null ? "" : ex.getMessage();
        if (msg.contains("Unknown function or variable")) {
            Matcher m = Pattern.compile("'([^']*)'").matcher(msg);
            return m.find() ? "No reconozco \"" + m.group(1) + "\"" : "Función o variable desconocida";
        }
        if (msg.startsWith(EvaluadorMath.PARENTESIS_DESBALANCEADOS)) {
            return "Los paréntesis no están balanceados";
        }
        return "Expresión incompleta o inválida";
    }

    private void cambiarModo() {
        modo = (modo == ModoAngular.RADIANES) ? ModoAngular.GRADOS : ModoAngular.RADIANES;
        botonModo.setText(modo == ModoAngular.RADIANES ? "RAD" : "DEG");
        botonModo.setToolTipText(modo == ModoAngular.RADIANES ? "Ángulos en radianes. Clic para usar grados"
                : "Ángulos en grados. Clic para usar radianes");
        mostrarInfo(modo == ModoAngular.RADIANES ? "Modo radianes" : "Modo grados", false);
    }

    /** Cambia el signo del último número escrito, o del resultado mostrado. */
    private void cambiarSigno() {
        String actual = display.getText();
        if (mostrandoResultado) {
            if (FormatoNumero.esNumero(actual) && !actual.equals("0")) {
                display.setText(actual.startsWith("-") ? actual.substring(1) : "-" + actual);
                ans = -ans;
            }
            return;
        }
        Matcher m = NUMERO_FINAL.matcher(actual);
        if (!m.find()) {
            display.setText(actual + "−");
            return;
        }
        String numero = m.group(2);
        boolean yaNegado = m.group(1) != null && m.group(3) != null;
        String reemplazo = yaNegado ? numero : "(−" + numero + ")";
        int inicio = yaNegado ? m.start() : m.start(2);
        int fin = yaNegado ? m.end() : m.end(2);
        display.setText(actual.substring(0, inicio) + reemplazo + actual.substring(fin));
    }

    // ------------------------------------------------------------------ historial

    private JPanel crearHistorial() {
        JLabel titulo = new JLabel("Historial");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titulo.setForeground(Color.LIGHT_GRAY);

        JButton borrar = new JButton("Borrar");
        borrar.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        borrar.setFocusable(false);
        borrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        borrar.setToolTipText("Vaciar el historial");
        borrar.addActionListener(e -> {
            historial.clear();
            posicionHistorial = -1;
        });

        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.setOpaque(false);
        cabecera.setBorder(new EmptyBorder(0, 2, 8, 0));
        cabecera.add(titulo, BorderLayout.WEST);
        cabecera.add(borrar, BorderLayout.EAST);

        listaHistorial = new JList<>(historial);
        listaHistorial.setFocusable(false); // el teclado sigue funcionando en la calculadora
        listaHistorial.setBackground(COLOR_DISPLAY_BG);
        listaHistorial.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaHistorial.setToolTipText("Clic para reutilizar la operación  ·  También con las flechas ↑ ↓");
        listaHistorial.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                String[] entrada = (String[]) value;
                String html = "<html><div style='text-align:right; width:170px'>"
                        + "<font color='#9a9a9a'>" + escaparHtml(entrada[0]) + " =</font><br>"
                        + "<font size='+1' color='#00ff96'><b>" + escaparHtml(entrada[1]) + "</b></font></div></html>";
                JLabel label = (JLabel) super.getListCellRendererComponent(list, html, index, isSelected, false);
                label.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(45, 45, 45)),
                        new EmptyBorder(6, 8, 6, 8)));
                label.setFont(new Font("Consolas", Font.PLAIN, 14));
                return label;
            }
        });
        listaHistorial.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int indice = listaHistorial.locationToIndex(e.getPoint());
                if (indice >= 0 && listaHistorial.getCellBounds(indice, indice).contains(e.getPoint())) {
                    cargarDelHistorial(indice);
                }
                requestFocusInWindow();
            }
        });

        JScrollPane scroll = new JScrollPane(listaHistorial);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(70, 70, 70)));
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(230, 0));
        panel.add(cabecera, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private void agregarAlHistorial(String expresion, String resultado) {
        // No repetir la misma operación si se calcula dos veces seguidas
        if (!historial.isEmpty() && historial.get(0)[0].equals(expresion)) {
            return;
        }
        historial.add(0, new String[]{expresion, resultado});
        if (historial.size() > MAX_HISTORIAL) {
            historial.remove(historial.size() - 1);
        }
    }

    /** Recorre el historial con ↑ (más antiguo) y ↓ (más reciente). */
    private void navegarHistorial(int direccion) {
        if (historial.isEmpty()) {
            return;
        }
        int nueva = posicionHistorial + direccion;
        if (nueva < 0) {
            posicionHistorial = -1;
            listaHistorial.clearSelection();
            display.setText("");
            mostrandoResultado = false;
            return;
        }
        cargarDelHistorial(Math.min(nueva, historial.size() - 1));
    }

    /** Pone la expresión de esa entrada en pantalla para editarla o recalcularla. */
    private void cargarDelHistorial(int indice) {
        if (mostrandoError) {
            limpiarError();
        }
        posicionHistorial = indice;
        String[] entrada = historial.get(indice);
        display.setText(entrada[0]);
        mostrandoResultado = false;
        mostrarInfo("Del historial: " + entrada[0] + " = " + entrada[1], false);
        listaHistorial.setSelectedIndex(indice);
        listaHistorial.ensureIndexIsVisible(indice);
    }

    private static String escaparHtml(String texto) {
        return texto.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    // ------------------------------------------------------------------ mensajes

    private void mostrarInfo(String texto, boolean error) {
        infoLabel.setText(texto);
        infoLabel.setForeground(error ? COLOR_ERROR : COLOR_INFO);
    }

    private void mostrarError(String mensaje) {
        mostrarInfo(mensaje, true);
        display.setBorder(bordeDisplayError);
        mostrandoError = true;
    }

    private void limpiarError() {
        infoLabel.setText(" ");
        infoLabel.setForeground(COLOR_INFO);
        display.setBorder(bordeDisplay);
        mostrandoError = false;
    }
}
