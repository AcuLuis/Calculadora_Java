package com.calculadora.math;

import net.objecthunter.exp4j.Expression;

public class Calculus {

    /** Raíz cúbica del épsilon de máquina: paso óptimo para diferencias centrales. */
    private static final double PASO_RELATIVO = Math.cbrt(Math.ulp(1.0));

    /**
     * Calcula la derivada numérica de f(x) en el punto x usando el método de
     * diferencias finitas centrales. El paso h se escala con |x| para mantener
     * la precisión tanto en valores pequeños como grandes.
     */
    public static double derivada(String expresion, double x) {
        Expression e = EvaluadorMath.compilar(expresion, "x");

        double h = PASO_RELATIVO * Math.max(1.0, Math.abs(x));
        // Ajusta h para que x + h sea exactamente representable y no sume error de redondeo
        double xMasH = x + h;
        h = xMasH - x;

        double fxMasH = e.setVariable("x", x + h).evaluate();
        double fxMenosH = e.setVariable("x", x - h).evaluate();

        return (fxMasH - fxMenosH) / (2 * h);
    }

    /**
     * Calcula la integral definida de f(x) en el intervalo [a, b] usando la Regla de Simpson 1/3.
     */
    public static double integral(String expresion, double a, double b, int n) {
        if (n % 2 != 0) {
            n++; // n debe ser par para la regla de Simpson
        }

        double h = (b - a) / n;
        Expression e = EvaluadorMath.compilar(expresion, "x");

        double sum = e.setVariable("x", a).evaluate();
        sum += e.setVariable("x", b).evaluate();

        for (int i = 1; i < n; i++) {
            double x = a + i * h;
            double fx = e.setVariable("x", x).evaluate();
            sum += (i % 2 == 0) ? 2 * fx : 4 * fx; // pares x2, impares x4
        }

        return sum * h / 3.0;
    }
}
