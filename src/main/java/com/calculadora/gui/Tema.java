package com.calculadora.gui;

import java.awt.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Colores y fuentes de toda la aplicación en un solo lugar.
 *
 * <p>Las fuentes se eligen entre las instaladas: en Windows se usan Segoe UI y
 * Consolas, y en macOS o Linux la primera equivalente disponible, o las
 * genéricas de Java si no hay ninguna.</p>
 */
public final class Tema {

    // ------------------------------------------------------------------ fondos

    /** Fondo de la ventana y de la calculadora. */
    public static final Color FONDO = new Color(30, 30, 30);
    /** Fondo de las pestañas de gráficas y cálculo numérico. */
    public static final Color FONDO_PANEL = new Color(40, 42, 44);
    /** Fondo de la barra de pestañas. */
    public static final Color FONDO_PESTANAS = new Color(45, 45, 45);
    /** Fondo de la pantalla de la calculadora y del historial. */
    public static final Color FONDO_PANTALLA = new Color(20, 20, 20);

    // ------------------------------------------------------------------ texto

    /** Verde estilo terminal de la pantalla. */
    public static final Color TEXTO_PANTALLA = new Color(0, 255, 150);
    public static final Color TEXTO_SECUNDARIO = new Color(160, 160, 160);

    // ------------------------------------------------------------------ botones y acentos

    public static final Color BOTON_NUMERO = new Color(60, 63, 65);
    public static final Color BOTON_FUNCION = new Color(43, 43, 43);
    public static final Color BOTON_SECUNDARIO = new Color(70, 73, 75);
    public static final Color NARANJA = new Color(255, 152, 0);
    public static final Color ROJO = new Color(244, 67, 54);
    public static final Color VERDE = new Color(76, 175, 80);
    public static final Color AZUL = new Color(33, 150, 243);
    public static final Color PURPURA = new Color(156, 39, 176);

    // ------------------------------------------------------------------ estados

    public static final Color OK = VERDE;
    public static final Color AVISO = new Color(255, 167, 38);
    public static final Color ERROR = new Color(255, 99, 99);

    // ------------------------------------------------------------------ bordes

    public static final Color BORDE = new Color(100, 100, 100);
    public static final Color BORDE_SUAVE = new Color(80, 80, 80);
    public static final Color BORDE_TENUE = new Color(70, 70, 70);
    public static final Color SEPARADOR = new Color(45, 45, 45);

    // ------------------------------------------------------------------ gráficas

    public static final Color GRAFICA_FONDO = new Color(43, 43, 43);
    public static final Color GRAFICA_AREA_TRAZADO = new Color(30, 30, 30);
    public static final Color GRAFICA_CUADRICULA = new Color(80, 80, 80);
    public static final Color GRAFICA_BORDE = new Color(90, 90, 90);
    public static final Color GRAFICA_LEYENDA = new Color(43, 43, 43, 220);

    /** Colores de las curvas, en orden. */
    public static final Color[] PALETA = {
        new Color(0, 200, 255),   // azul neón
        new Color(255, 152, 0),   // naranja
        new Color(102, 187, 106), // verde
        new Color(240, 98, 146),  // rosa
        new Color(255, 238, 88),  // amarillo
        new Color(179, 136, 255)  // lila
    };

    /** Relleno semitransparente del área de una integral. */
    public static final Color AREA = new Color(0, 200, 255, 70);

    // ------------------------------------------------------------------ fuentes

    private static final String[] FAMILIAS_TEXTO = {
        "Segoe UI", "Inter", "SF Pro Text", "Helvetica Neue", "Ubuntu", "Cantarell", "Noto Sans", "DejaVu Sans"
    };
    private static final String[] FAMILIAS_MONO = {
        "Consolas", "Cascadia Mono", "JetBrains Mono", "SF Mono", "Menlo", "Ubuntu Mono", "DejaVu Sans Mono", "Liberation Mono"
    };

    private static final String FAMILIA_TEXTO = elegirFamilia(FAMILIAS_TEXTO, Font.SANS_SERIF);
    private static final String FAMILIA_MONO = elegirFamilia(FAMILIAS_MONO, Font.MONOSPACED);

    private Tema() {
    }

    /** Fuente para textos de la interfaz. */
    public static Font fuente(int estilo, int tamano) {
        return new Font(FAMILIA_TEXTO, estilo, tamano);
    }

    /** Fuente de ancho fijo para expresiones y números. */
    public static Font fuenteMono(int estilo, int tamano) {
        return new Font(FAMILIA_MONO, estilo, tamano);
    }

    /** Primera familia instalada de la lista, o la genérica de Java si no hay ninguna. */
    static String elegirFamilia(String[] preferidas, String generica) {
        Set<String> instaladas = new HashSet<>(Arrays.asList(
                GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));
        for (String familia : preferidas) {
            if (instaladas.contains(familia)) {
                return familia;
            }
        }
        return generica;
    }
}
