package com.calculadora.math;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.function.Function;
import net.objecthunter.exp4j.operator.Operator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EmptyStackException;
import java.util.List;

public class EvaluadorMath {

    /** Unidad en la que se interpretan los ángulos de las funciones trigonométricas. */
    public enum ModoAngular { RADIANES, GRADOS }

    /** Nombre de la variable que guarda el último resultado. */
    public static final String VARIABLE_ANS = "ANS";

    /** Mensaje (en el formato de exp4j) para paréntesis que no cierran bien. */
    public static final String PARENTESIS_DESBALANCEADOS = "Mismatched parentheses detected";

    /** Por debajo de este error relativo un resultado trigonométrico se considera cero. */
    private static final double TOLERANCIA_TRIG = 1e-15;

    // ---------------------------------------------------------------- funciones

    /** Logaritmo en base 10 (exp4j trae "log" como logaritmo natural, aquí se redefine). */
    private static final Function LOG10 = new Function("log", 1) {
        @Override
        public double apply(double... args) {
            return Math.log10(args[0]);
        }
    };

    private static final Function LN = new Function("ln", 1) {
        @Override
        public double apply(double... args) {
            return Math.log(args[0]);
        }
    };

    // En radianes se limpia el ruido de coma flotante: sin(pi) da 0 y no 1.22E-16
    private static final Function SIN_RAD = new Function("sin", 1) {
        @Override
        public double apply(double... args) {
            return sinRad(args[0]);
        }
    };

    private static final Function COS_RAD = new Function("cos", 1) {
        @Override
        public double apply(double... args) {
            return cosRad(args[0]);
        }
    };

    private static final Function TAN_RAD = new Function("tan", 1) {
        @Override
        public double apply(double... args) {
            double x = args[0];
            if (cosRad(x) == 0) {
                return Double.NaN; // tan(pi/2) no está definida
            }
            return sinRad(x) == 0 ? 0 : Math.tan(x);
        }
    };

    private static final Function SIN_GRAD = new Function("sin", 1) {
        @Override
        public double apply(double... args) {
            return sinGrados(args[0]);
        }
    };

    private static final Function COS_GRAD = new Function("cos", 1) {
        @Override
        public double apply(double... args) {
            return sinGrados(args[0] + 90);
        }
    };

    private static final Function TAN_GRAD = new Function("tan", 1) {
        @Override
        public double apply(double... args) {
            double c = sinGrados(args[0] + 90);
            return c == 0 ? Double.NaN : sinGrados(args[0]) / c;
        }
    };

    private static final Function ASIN_GRAD = new Function("asin", 1) {
        @Override
        public double apply(double... args) {
            return Math.toDegrees(Math.asin(args[0]));
        }
    };

    private static final Function ACOS_GRAD = new Function("acos", 1) {
        @Override
        public double apply(double... args) {
            return Math.toDegrees(Math.acos(args[0]));
        }
    };

    private static final Function ATAN_GRAD = new Function("atan", 1) {
        @Override
        public double apply(double... args) {
            return Math.toDegrees(Math.atan(args[0]));
        }
    };

    private static final List<Function> FUNCIONES_RAD =
            Arrays.asList(LOG10, LN, SIN_RAD, COS_RAD, TAN_RAD);
    private static final List<Function> FUNCIONES_GRAD =
            Arrays.asList(LOG10, LN, SIN_GRAD, COS_GRAD, TAN_GRAD, ASIN_GRAD, ACOS_GRAD, ATAN_GRAD);

    // ---------------------------------------------------------------- operadores

    /** Porcentaje postfijo: 50% = 0.5, 200*10% = 20. */
    private static final Operator PORCENTAJE = new Operator("%", 1, true, Operator.PRECEDENCE_POWER + 1) {
        @Override
        public double apply(double... args) {
            return args[0] / 100;
        }
    };

    /** Factorial postfijo para enteros no negativos: 5! = 120. */
    private static final Operator FACTORIAL = new Operator("!", 1, true, Operator.PRECEDENCE_POWER + 1) {
        @Override
        public double apply(double... args) {
            double n = args[0];
            if (n < 0 || n != Math.floor(n)) {
                return Double.NaN;
            }
            if (n > 170) {
                return Double.POSITIVE_INFINITY; // 171! ya no cabe en un double
            }
            double resultado = 1;
            for (int i = 2; i <= n; i++) {
                resultado *= i;
            }
            return resultado;
        }
    };

    // ---------------------------------------------------------------- API

    /**
     * Construye una expresión en radianes lista para evaluarse.
     *
     * @throws IllegalArgumentException si la sintaxis es inválida
     */
    public static Expression compilar(String expresion, String... variables) {
        return compilar(expresion, ModoAngular.RADIANES, variables);
    }

    /**
     * Construye una expresión con las funciones y operadores propios de la
     * calculadora. Acepta también los símbolos que muestra la interfaz
     * (×, ÷, −, √).
     *
     * @throws IllegalArgumentException si la sintaxis es inválida
     */
    public static Expression compilar(String expresion, ModoAngular modo, String... variables) {
        if (expresion == null || expresion.trim().isEmpty()) {
            throw new IllegalArgumentException("La expresión está vacía");
        }
        String normalizada = normalizar(expresion);
        validarParentesis(normalizada);
        List<Function> funciones = modo == ModoAngular.GRADOS ? FUNCIONES_GRAD : FUNCIONES_RAD;
        try {
            return new ExpressionBuilder(normalizada)
                    .functions(new ArrayList<>(funciones))
                    .operator(PORCENTAJE, FACTORIAL)
                    .variables(variables)
                    .build();
        } catch (EmptyStackException ex) {
            // exp4j no valida algunos paréntesis mal cerrados y falla con esta excepción interna
            throw new IllegalArgumentException(PARENTESIS_DESBALANCEADOS, ex);
        }
    }

    /**
     * Un ")" sin su "(" correspondiente, o un argumento vacío como "()" o "(,)",
     * se informan como error de sintaxis (exp4j fallaría con una excepción interna).
     */
    private static void validarParentesis(String expresion) {
        int abiertos = 0;
        char anterior = ' ';
        for (int i = 0; i < expresion.length(); i++) {
            char c = expresion.charAt(i);
            if (Character.isWhitespace(c)) {
                continue;
            }
            boolean argumentoVacio = (c == ')' || c == ',') && (anterior == '(' || anterior == ',');
            if (argumentoVacio) {
                throw new IllegalArgumentException("Expresión incompleta: falta un valor entre paréntesis");
            }
            if (c == '(') {
                abiertos++;
            } else if (c == ')') {
                if (--abiertos < 0) {
                    throw new IllegalArgumentException(PARENTESIS_DESBALANCEADOS);
                }
            }
            anterior = c;
        }
    }

    /**
     * Evalúa una expresión matemática sin variables, en radianes.
     *
     * @throws IllegalArgumentException si la sintaxis es inválida
     * @throws ArithmeticException      si hay una división entre cero
     */
    public static double evaluar(String expresion) {
        return compilar(expresion).evaluate();
    }

    /**
     * Evalúa una expresión en el modo angular indicado, donde ANS vale el
     * último resultado.
     */
    public static double evaluar(String expresion, ModoAngular modo, double ans) {
        return compilar(expresion, modo, VARIABLE_ANS)
                .setVariable(VARIABLE_ANS, ans)
                .evaluate();
    }

    /**
     * Evalúa una función f(x) en un valor de x específico.
     */
    public static double evaluarFuncion(String expresion, double x) {
        return compilar(expresion, "x").setVariable("x", x).evaluate();
    }

    /** Traduce los símbolos de la interfaz a la sintaxis del evaluador. */
    public static String normalizar(String expresion) {
        return expresion.trim()
                .replace('×', '*')
                .replace('÷', '/')
                .replace('−', '-')
                .replace("√", "sqrt")
                .replace("ans", VARIABLE_ANS);
    }

    // ---------------------------------------------------------------- auxiliares

    private static double sinRad(double x) {
        return limpiarCero(Math.sin(x), x);
    }

    private static double cosRad(double x) {
        return limpiarCero(Math.cos(x), x);
    }

    /** Si el resultado es solo ruido de redondeo respecto al argumento, devuelve 0. */
    private static double limpiarCero(double resultado, double argumento) {
        double magnitud = Math.abs(argumento);
        return (magnitud >= 1 && Math.abs(resultado) < TOLERANCIA_TRIG * magnitud) ? 0 : resultado;
    }

    /** Seno en grados con valores exactos en los múltiplos de 90°. */
    private static double sinGrados(double grados) {
        double reducido = grados % 360; // reducir primero evita perder precisión con ángulos grandes
        if (reducido % 90 == 0) {
            int cuadrante = (((int) (reducido / 90)) % 4 + 4) % 4;
            return new double[]{0, 1, 0, -1}[cuadrante];
        }
        return Math.sin(Math.toRadians(reducido));
    }
}
