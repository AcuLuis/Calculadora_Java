package com.calculadora.math;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class EvaluadorMath {

    /**
     * Evalúa una expresión matemática simple (sin variables)
     */
    public static double evaluar(String expresion) throws Exception {
        Expression e = new ExpressionBuilder(expresion).build();
        return e.evaluate();
    }
    
    /**
     * Evalúa una función f(x) en un valor de x específico
     */
    public static double evaluarFuncion(String expresion, double x) throws Exception {
        Expression e = new ExpressionBuilder(expresion).variables("x").build();
        e.setVariable("x", x);
        return e.evaluate();
    }
}
