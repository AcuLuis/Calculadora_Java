package com.calculadora.math;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class Calculus {
    
    /**
     * Calcula la derivada numérica de f(x) en el punto x usando el método de diferencias finitas centrales.
     */
    public static double derivada(String expresion, double x) throws Exception {
        double h = 1e-5;
        Expression e = new ExpressionBuilder(expresion).variables("x").build();
        
        e.setVariable("x", x + h);
        double f_x_plus_h = e.evaluate();
        
        e.setVariable("x", x - h);
        double f_x_minus_h = e.evaluate();
        
        return (f_x_plus_h - f_x_minus_h) / (2 * h);
    }
    
    /**
     * Calcula la integral definida de f(x) en el intervalo [a, b] usando la Regla de Simpson 1/3.
     */
    public static double integral(String expresion, double a, double b, int n) throws Exception {
        if (n % 2 != 0) {
            n++; // n debe ser par para la regla de Simpson
        }
        
        double h = (b - a) / n;
        Expression e = new ExpressionBuilder(expresion).variables("x").build();
        
        e.setVariable("x", a);
        double sum = e.evaluate();
        
        e.setVariable("x", b);
        sum += e.evaluate();
        
        for (int i = 1; i < n; i++) {
            double x = a + i * h;
            e.setVariable("x", x);
            if (i % 2 == 0) {
                sum += 2 * e.evaluate(); // Términos pares
            } else {
                sum += 4 * e.evaluate(); // Términos impares
            }
        }
        
        return sum * h / 3.0;
    }
}
