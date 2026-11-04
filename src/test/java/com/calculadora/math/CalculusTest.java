package com.calculadora.math;

import com.calculadora.math.Calculus.ResultadoDerivada;
import com.calculadora.math.Calculus.ResultadoIntegral;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class CalculusTest {

    @Nested
    class Derivada {

        @ParameterizedTest(name = "d/dx {0} en x = {1} es {2}")
        @CsvSource(delimiter = '|', value = {
            "x^2            | 3          | 6",
            "x^3 - 2*x      | 2          | 10",
            "x^3            | 1000000    | 3e12",
            "sin(x)         | 0          | 1",
            "exp(10*x)      | 1          | 220264.65794806718",
            "tan(x)         | 1.5        | 199.85004573911",
            "x^2            | 0          | 0",
            "abs(x)         | 2          | 1",
            "floor(x)       | 1.5        | 0",
            "sqrt(x)        | 4          | 0.25",
            "1e-8*x^2       | 1          | 2e-8",
            "1e8*x^2        | 1          | 2e8"
        })
        void existe(String f, double x, double esperado) {
            ResultadoDerivada r = Calculus.analizarDerivada(f, x);
            assertEquals(ResultadoDerivada.Estado.EXISTE, r.estado);
            assertEquals(esperado, r.valor, 1e-6 * Math.max(1, Math.abs(esperado)));
            assertEquals(esperado, Calculus.derivada(f, x), 1e-6 * Math.max(1, Math.abs(esperado)));
        }

        @ParameterizedTest(name = "d/dx {0} en x = {1}: {2}")
        @CsvSource(delimiter = '|', value = {
            "abs(x)        | 0   | ESQUINA",
            "sqrt(abs(x))  | 0   | ESQUINA",
            "floor(x)      | 1   | DISCONTINUA",
            "cbrt(x)       | 0   | TANGENTE_VERTICAL",
            "sqrt(x)       | 0   | SOLO_LATERAL",
            "1/x           | 0   | NO_DEFINIDA",
            "ln(x)         | -1  | NO_DEFINIDA",
            "sin(x)/x      | 0   | NO_DEFINIDA"
        })
        void noExiste(String f, double x, ResultadoDerivada.Estado estado) {
            ResultadoDerivada r = Calculus.analizarDerivada(f, x);
            assertEquals(estado, r.estado);
            assertFalse(r.existe());
        }

        @Test
        void laEsquinaInformaLasDerivadasLaterales() {
            ResultadoDerivada r = Calculus.analizarDerivada("abs(x)", 0);
            assertEquals(-1, r.izquierda, 1e-6);
            assertEquals(1, r.derecha, 1e-6);
        }

        @Test
        void derivadaLateralInfinita() {
            ResultadoDerivada r = Calculus.analizarDerivada("sqrt(x)", 0);
            assertEquals(Double.POSITIVE_INFINITY, r.valor);
        }

        @Test
        void sintaxisInvalida() {
            assertThrows(IllegalArgumentException.class, () -> Calculus.analizarDerivada("sin(x", 1));
        }
    }

    @Nested
    class Integral {

        @ParameterizedTest(name = "∫ {0} de {1} a {2} = {3}")
        @CsvSource(delimiter = '|', value = {
            "x^2              | 0     | 1      | 0.3333333333333333",
            "x^2              | 1     | 0      | -0.3333333333333333",
            "sin(x)           | 0     | pi     | 2",
            "sin(x)           | 0     | 2*pi   | 0",
            "sin(x)           | 0     | 100*pi | 0",
            "exp(-x^2)        | -10   | 10     | 1.7724538509055159",
            "abs(x)           | -1    | 2      | 2.5",
            "floor(x)         | 0     | 3      | 3",
            "1/(1+x^2)        | -1000 | 1000   | 3.1395926542565598",
            "x^3              | 0     | 0      | 0",
            "1e6*x            | 0     | 1      | 500000",
            "exp(x)           | 0     | 50     | 5.184705528587072E21",
            "0                | 0     | 5      | 0",
            "sin(x)/x         | 0     | 1      | 0.946083070367183",
            "sin(x)/x         | -1    | 1      | 1.892166140734366",
            "1/sqrt(x)        | 0     | 1      | 2",
            "ln(x)            | 0     | 1      | -1",
            "x*ln(x)          | 0     | 2      | 0.38629436111989057",
            "1/sqrt(abs(x))   | -1    | 1      | 4",
            "1/sqrt(1-x)      | 0     | 1      | 2"
        })
        void converge(String f, String a, String b, double esperado) {
            ResultadoIntegral r = Calculus.integrar(f, EvaluadorMath.evaluar(a), EvaluadorMath.evaluar(b));
            assertEquals(ResultadoIntegral.Estado.CONVERGE, r.estado);
            double tolerancia = Math.max(1e-9 * Math.max(1, Math.abs(esperado)), 2 * r.errorEstimado);
            assertEquals(esperado, r.valor, tolerancia);
        }

        @ParameterizedTest(name = "∫ {0} de {1} a {2} no converge")
        @CsvSource(delimiter = '|', value = {
            "1/x              | 0    | 1",
            "1/x              | -1   | 1",
            "1/x^2            | 0    | 1",
            "1/abs(x)         | -1   | 1",
            "tan(x)           | 0    | 2"
        })
        void noConverge(String f, double a, double b) {
            assertEquals(ResultadoIntegral.Estado.NO_CONVERGE, Calculus.integrar(f, a, b).estado);
        }

        @Test
        void funcionSinValoresRealesEnElIntervalo() {
            ResultadoIntegral r = Calculus.integrar("sqrt(x)", -1, 1);
            assertEquals(ResultadoIntegral.Estado.NO_DEFINIDA, r.estado);
            assertTrue(Double.isNaN(r.valor));
        }

        @Test
        void elErrorEstimadoEsPequenoParaFuncionesSuaves() {
            ResultadoIntegral r = Calculus.integrar("sin(x)", 0, Math.PI);
            assertTrue(r.errorEstimado < 1e-9, "error estimado: " + r.errorEstimado);
            assertTrue(r.evaluaciones > 0);
        }

        @Test
        void sintaxisInvalida() {
            assertThrows(IllegalArgumentException.class, () -> Calculus.integrar("x^", 0, 1));
        }
    }
}
