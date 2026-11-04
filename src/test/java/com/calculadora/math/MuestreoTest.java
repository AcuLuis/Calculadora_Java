package com.calculadora.math;

import net.objecthunter.exp4j.Expression;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class MuestreoTest {

    private static Expression f(String expresion) {
        return EvaluadorMath.compilar(expresion, "x");
    }

    /** Segmentos que unirían un valor muy positivo con uno muy negativo: líneas verticales falsas. */
    private static int lineasFalsas(Muestreo.Curva c, double umbral) {
        int n = 0;
        for (int i = 1; i < c.y.size(); i++) {
            double a = c.y.get(i - 1);
            double b = c.y.get(i);
            if (!Double.isNaN(a) && !Double.isNaN(b) && a * b < 0 && Math.abs(a - b) > umbral) {
                n++;
            }
        }
        return n;
    }

    @Test
    @DisplayName("Las asíntotas se cortan sin importar dónde caiga el polo entre dos muestras")
    void cortaLasAsintotas() {
        for (int k = 0; k < 200; k++) {
            double min = -7 - k * 0.0137;
            double max = 7 + k * 0.0291;
            assertEquals(0, lineasFalsas(Muestreo.muestrear(f("tan(x)"), min, max, 800), 50), "tan(x) en " + min + ".." + max);
            assertEquals(0, lineasFalsas(Muestreo.muestrear(f("1/(x-0.37)"), min, max, 800), 50), "1/(x-0.37) en " + min + ".." + max);
        }
    }

    @Test
    @DisplayName("Una función continua no se corta")
    void noCortaFuncionesContinuas() {
        for (int k = 0; k < 50; k++) {
            Muestreo.Curva c = Muestreo.muestrear(f("sin(x)*x"), -7 - k * 0.1, 7 + k * 0.3, 800);
            assertFalse(c.y.stream().anyMatch(y -> Double.isNaN(y)));
        }
    }

    @Test
    void dejaHuecosDondeLaFuncionNoExiste() {
        Muestreo.Curva c = Muestreo.muestrear(f("sqrt(x)"), -4, 9, 800);
        assertTrue(c.tienePuntos);
        for (int i = 0; i < c.x.size(); i++) {
            assertEquals(c.x.get(i) < 0, Double.isNaN(c.y.get(i)), "x = " + c.x.get(i));
        }
        assertFalse(Muestreo.muestrear(f("sqrt(x)"), -10, -1, 100).tienePuntos);
    }

    @Test
    void divisionEntreCeroEnUnPuntoNoRompeLaCurva() {
        Muestreo.Curva c = Muestreo.muestrear(f("1/x"), 0, 10, 800);
        assertTrue(Double.isNaN(c.y.get(0)));
        assertTrue(c.tienePuntos);
    }

    @Test
    @DisplayName("El rango Y ignora los picos de tan(x) pero conserva el de una curva normal")
    void rangoY() {
        double[] tan = Muestreo.rangoY(Collections.singletonList(Muestreo.muestrear(f("tan(x)"), -7, 7, 800)));
        assertTrue(tan[1] < 100 && tan[0] > -100, Arrays.toString(tan));

        double[] seno = Muestreo.rangoY(Collections.singletonList(Muestreo.muestrear(f("sin(x)"), -7, 7, 800)));
        assertTrue(seno[0] < -1 && seno[0] > -1.3 && seno[1] > 1 && seno[1] < 1.3, Arrays.toString(seno));

        double[] constante = Muestreo.rangoY(Collections.singletonList(Muestreo.muestrear(f("5"), 0, 1, 10)));
        assertTrue(constante[0] < 5 && constante[1] > 5, Arrays.toString(constante));

        assertNull(Muestreo.rangoY(Collections.singletonList(Muestreo.muestrear(f("sqrt(x)"), -2, -1, 10))));
    }

    @Test
    @DisplayName("Las muestras no son equiespaciadas (para que XChart use marcas redondas)")
    void muestrasNoEquiespaciadas() {
        double[] m = Muestreo.generarMuestras(-10, 10, 800);
        assertEquals(801, m.length);
        assertEquals(-10, m[0]);
        assertEquals(10, m[m.length - 1], 1e-12);
        // El punto extra parte el primer intervalo en dos mitades; a partir de ahí el paso es completo
        assertEquals(m[2] - m[1], m[1] - m[0], 1e-12);
        assertEquals(2 * (m[2] - m[1]), m[3] - m[2], 1e-12);
    }
}
