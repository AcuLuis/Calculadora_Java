package com.calculadora.math;

import net.objecthunter.exp4j.Expression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Convierte una función f(x) en puntos listos para graficar: deja huecos (NaN)
 * donde la función no existe y corta la curva en las asíntotas verticales.
 */
public final class Muestreo {

    /** Puntos de una curva; los NaN en y marcan huecos. */
    public static final class Curva {
        public final List<Double> x;
        public final List<Double> y;
        public final boolean tienePuntos;

        Curva(List<Double> x, List<Double> y, boolean tienePuntos) {
            this.x = x;
            this.y = y;
            this.tienePuntos = tienePuntos;
        }
    }

    private Muestreo() {
    }

    /**
     * Evalúa la función en [min, max].
     *
     * @throws IllegalArgumentException si la expresión está incompleta
     */
    public static Curva muestrear(Expression expresion, double min, double max, int cantidad) {
        double[] muestras = generarMuestras(min, max, cantidad);
        List<Double> xs = new ArrayList<>(muestras.length + 16);
        List<Double> ys = new ArrayList<>(muestras.length + 16);
        boolean tienePuntos = false;
        double prevX = Double.NaN;
        double prevY = Double.NaN;

        for (double x : muestras) {
            double y = evaluarSeguro(expresion, x);

            // Si entre dos puntos hay una asíntota (p. ej. 1/x en 0) se corta la línea
            if (!Double.isNaN(y) && !Double.isNaN(prevY) && hayAsintota(expresion, prevX, prevY, x, y)) {
                xs.add((prevX + x) / 2);
                ys.add(Double.NaN);
            }

            xs.add(x);
            ys.add(y); // NaN deja un hueco en la gráfica en vez de dibujar un 0 falso
            tienePuntos |= !Double.isNaN(y);
            prevX = x;
            prevY = y;
        }
        return new Curva(xs, ys, tienePuntos);
    }

    /** Evalúa f(x) devolviendo NaN cuando el punto no tiene un valor real finito. */
    public static double evaluarSeguro(Expression expresion, double x) {
        try {
            double y = expresion.setVariable("x", x).evaluate();
            return Double.isFinite(y) ? y : Double.NaN;
        } catch (ArithmeticException ex) {
            return Double.NaN; // división entre cero en este punto
        }
    }

    /**
     * Rango vertical que conviene mostrar: ignora los picos extremos (como los
     * de tan(x) cerca de sus asíntotas) para que no aplasten el resto de la curva.
     *
     * @return {mínimo, máximo} o null si no hay puntos
     */
    public static double[] rangoY(List<Curva> curvas) {
        List<Double> valores = new ArrayList<>();
        for (Curva c : curvas) {
            for (double y : c.y) {
                if (!Double.isNaN(y)) {
                    valores.add(y);
                }
            }
        }
        if (valores.isEmpty()) {
            return null;
        }
        double[] v = new double[valores.size()];
        for (int i = 0; i < v.length; i++) {
            v[i] = valores.get(i);
        }
        Arrays.sort(v);

        double minReal = v[0];
        double maxReal = v[v.length - 1];
        double p02 = v[(int) Math.floor(0.02 * (v.length - 1))];
        double p98 = v[(int) Math.ceil(0.98 * (v.length - 1))];
        double amplitud = Math.max(p98 - p02, 1e-9);

        // Se usa el valor real salvo que esté muy lejos del grueso de los datos
        double min = Math.max(minReal, p02 - 0.5 * amplitud);
        double max = Math.min(maxReal, p98 + 0.5 * amplitud);
        if (max - min < 1e-12) { // función constante
            double margen = Math.max(1, Math.abs(min) * 0.1);
            return new double[]{min - margen, max + margen};
        }
        double margen = 0.06 * (max - min);
        return new double[]{min - margen, max + margen};
    }

    /**
     * Puntos de muestreo uniformes en [min, max] más uno extra a mitad del primer
     * intervalo. XChart alinea las marcas del eje X a los datos cuando estos están
     * espaciados exactamente igual (y salen valores como -3.41, -2.83...); con ese
     * punto adicional calcula marcas redondas (-10, -5, 0, 5, 10).
     */
    static double[] generarMuestras(double min, double max, int cantidad) {
        double paso = (max - min) / (cantidad - 1);
        double[] muestras = new double[cantidad + 1];
        muestras[0] = min;
        muestras[1] = min + paso / 2;
        for (int i = 1; i < cantidad; i++) {
            muestras[i + 1] = min + i * paso;
        }
        return muestras;
    }

    /**
     * Detecta un polo entre dos puntos consecutivos donde la función cambia de
     * signo. Se acorrala el cambio de signo por bisección: si es una raíz, |f|
     * tiende a 0; si es una asíntota (tan(x) en π/2), |f| crece sin límite.
     * Funciona aunque el polo esté pegado a uno de los dos puntos.
     */
    static boolean hayAsintota(Expression expresion, double x1, double y1, double x2, double y2) {
        if (y1 * y2 >= 0) {
            return false;
        }
        double a = x1;
        double fa = y1;
        double b = x2;
        double fb = y2;
        for (int i = 0; i < 50 && b - a > Math.ulp(a) * 4; i++) {
            double m = (a + b) / 2;
            double fm = evaluarSeguro(expresion, m);
            if (Double.isNaN(fm)) {
                return true; // la función no existe en medio: hay que cortar la línea
            }
            if (fm == 0) {
                return false; // raíz exacta
            }
            if ((fm > 0) == (fa > 0)) {
                a = m;
                fa = fm;
            } else {
                b = m;
                fb = fm;
            }
        }
        return Math.min(Math.abs(fa), Math.abs(fb)) > Math.max(Math.abs(y1), Math.abs(y2));
    }
}
