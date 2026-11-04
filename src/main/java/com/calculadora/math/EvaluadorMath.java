package com.calculadora.math;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.function.Function;

public class EvaluadorMath {

    /** Logaritmo en base 10 (exp4j trae "log" como logaritmo natural, aquí se redefine). */
    private static final Function LOG10 = new Function("log", 1) {
        @Override
        public double apply(double... args) {
            return Math.log10(args[0]);
        }
    };

    /** Logaritmo natural. */
    private static final Function LN = new Function("ln", 1) {
        @Override
        public double apply(double... args) {
            return Math.log(args[0]);
        }
    };

    /**
     * Construye una expresión lista para evaluarse, con las funciones propias
     * de la calculadora (log en base 10 y ln).
     *
     * @throws IllegalArgumentException si la sintaxis es inválida
     */
    public static Expression compilar(String expresion, String... variables) {
        if (expresion == null || expresion.trim().isEmpty()) {
            throw new IllegalArgumentException("La expresión está vacía");
        }
        return new ExpressionBuilder(expresion.trim())
                .functions(LOG10, LN)
                .variables(variables)
                .build();
    }

    /**
     * Evalúa una expresión matemática sin variables.
     *
     * @throws IllegalArgumentException si la sintaxis es inválida
     * @throws ArithmeticException      si hay una división entre cero
     */
    public static double evaluar(String expresion) {
        return compilar(expresion).evaluate();
    }

    /**
     * Evalúa una función f(x) en un valor de x específico.
     */
    public static double evaluarFuncion(String expresion, double x) {
        return compilar(expresion, "x").setVariable("x", x).evaluate();
    }
}
