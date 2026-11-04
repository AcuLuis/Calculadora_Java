package com.calculadora.math;

import com.calculadora.math.EvaluadorMath.ModoAngular;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class EvaluadorMathTest {

    private static final double EPS = 1e-12;

    @ParameterizedTest(name = "{0} = {1}")
    @CsvSource(delimiter = '|', value = {
        "2+3*4          | 14",
        "2^10           | 1024",
        "-2^2           | -4",
        "(-2)^2         | 4",
        "2(3+1)         | 8",
        "2pi            | 6.283185307179586",
        "log(1000)      | 3",
        "ln(e^2)        | 2",
        "log2(8)        | 3",
        "sqrt(16)       | 4",
        "√(16)          | 4",
        "6×7            | 42",
        "1÷4            | 0.25",
        "5−8            | -3",
        "50%            | 0.5",
        "200*10%        | 20",
        "5!             | 120",
        "0!             | 1",
        "pow(2,3)       | 8",
        "π              | 3.141592653589793"
    })
    void evaluaExpresiones(String expresion, double esperado) {
        assertEquals(esperado, EvaluadorMath.evaluar(expresion), EPS);
    }

    @Test
    @DisplayName("log es base 10 y ln es natural")
    void logaritmos() {
        assertEquals(2, EvaluadorMath.evaluar("log(100)"), EPS);
        assertEquals(1, EvaluadorMath.evaluar("ln(e)"), EPS);
    }

    @Test
    void factorialDeNoEnterosONegativosNoExiste() {
        assertTrue(Double.isNaN(EvaluadorMath.evaluar("3.5!")));
        assertTrue(Double.isNaN(EvaluadorMath.evaluar("(-1)!")));
        assertTrue(Double.isInfinite(EvaluadorMath.evaluar("171!")));
    }

    @Test
    @DisplayName("En radianes se limpia el ruido: sin(π) = 0 y tan(π/2) no existe")
    void trigonometriaEnRadianes() {
        double[] ceros = {
            EvaluadorMath.evaluar("sin(pi)"), EvaluadorMath.evaluar("cos(pi/2)"), EvaluadorMath.evaluar("sin(-3*pi)")
        };
        for (double cero : ceros) {
            assertEquals(0.0, cero, 0.0);
        }
        assertTrue(Double.isNaN(EvaluadorMath.evaluar("tan(pi/2)")));
        assertEquals(1, EvaluadorMath.evaluar("tan(pi/4)"), EPS);
        assertEquals(1e-16, EvaluadorMath.evaluar("sin(1e-16)"), 1e-30, "los argumentos pequeños no se tocan");
    }

    @ParameterizedTest(name = "{0} = {1} en grados")
    @CsvSource(delimiter = '|', value = {
        "sin(30)    | 0.5",
        "sin(90)    | 1",
        "cos(90)    | 0",
        "cos(180)   | -1",
        "sin(-270)  | 1",
        "cos(36000) | 1",
        "tan(45)    | 1",
        "asin(1)    | 90",
        "acos(0)    | 90",
        "atan(1)    | 45"
    })
    void trigonometriaEnGrados(String expresion, double esperado) {
        assertEquals(esperado, EvaluadorMath.evaluar(expresion, ModoAngular.GRADOS, 0), EPS);
    }

    @Test
    void tangenteDe90GradosNoExiste() {
        assertTrue(Double.isNaN(EvaluadorMath.evaluar("tan(90)", ModoAngular.GRADOS, 0)));
    }

    @Test
    void variableAns() {
        assertEquals(14, EvaluadorMath.evaluar("ANS*2", ModoAngular.RADIANES, 7), EPS);
        assertEquals(14, EvaluadorMath.evaluar("ans*2", ModoAngular.RADIANES, 7), EPS);
    }

    @Test
    void evaluaFuncionesDeX() {
        assertEquals(9, EvaluadorMath.evaluarFuncion("x^2", 3), EPS);
        assertThrows(IllegalArgumentException.class, () -> EvaluadorMath.evaluarFuncion("y+1", 3));
    }

    @Test
    void divisionEntreCeroLanzaArithmeticException() {
        assertThrows(ArithmeticException.class, () -> EvaluadorMath.evaluar("1/0"));
    }

    @ParameterizedTest(name = "\"{0}\" es inválida")
    @ValueSource(strings = {"", "   ", "3+", "sin(", "2+3)", ")(", "()", "sin()", "(,)", "foo(2)", "2**3"})
    void expresionesInvalidasLanzanIllegalArgument(String expresion) {
        assertThrows(IllegalArgumentException.class, () -> EvaluadorMath.evaluar(expresion));
    }

    @Test
    void parentesisDesbalanceadosTienenSuMensaje() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> EvaluadorMath.evaluar("2+3)"));
        assertTrue(ex.getMessage().startsWith(EvaluadorMath.PARENTESIS_DESBALANCEADOS));
    }

    @Test
    @DisplayName("Ninguna entrada mal formada produce una excepción inesperada")
    void fuzzing() {
        String alfabeto = "0123456789.+−×÷^()%!πe√sincoatlgxANS,E ";
        Random azar = new Random(2026);
        for (int i = 0; i < 100_000; i++) {
            StringBuilder sb = new StringBuilder();
            int largo = 1 + azar.nextInt(8);
            for (int j = 0; j < largo; j++) {
                sb.append(alfabeto.charAt(azar.nextInt(alfabeto.length())));
            }
            String expresion = sb.toString();
            for (ModoAngular modo : ModoAngular.values()) {
                try {
                    EvaluadorMath.evaluar(expresion, modo, 1);
                } catch (IllegalArgumentException | ArithmeticException esperado) {
                    // errores de sintaxis o matemáticos: es lo correcto
                } catch (RuntimeException ex) {
                    fail("\"" + expresion + "\" lanzó " + ex);
                }
            }
        }
    }
}
