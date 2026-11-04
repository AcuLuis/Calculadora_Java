package com.calculadora.math;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class FormatoNumeroTest {

    @ParameterizedTest(name = "{0} se muestra como {1}")
    @CsvSource(delimiter = '|', value = {
        "0.1+0.2          | 0.3",
        "1/3              | 0.333333333333",
        "-7/2             | -3.5",
        "2^70             | 1.18059162072E21",
        "2^-30            | 9.31322574615E-10",
        "10^15            | 1E15",
        "123456789*1000   | 123456789000",
        "0.000001         | 0.000001",
        "-0               | 0",
        "1e-300*1e-300    | 0"
    })
    void formateaResultados(String expresion, String esperado) {
        assertEquals(esperado, FormatoNumero.formatear(EvaluadorMath.evaluar(expresion)));
    }

    @Test
    void valoresEspeciales() {
        assertEquals(FormatoNumero.INDEFINIDO, FormatoNumero.formatear(Double.NaN));
        assertEquals(FormatoNumero.INFINITO, FormatoNumero.formatear(Double.POSITIVE_INFINITY));
        assertEquals("-" + FormatoNumero.INFINITO, FormatoNumero.formatear(Double.NEGATIVE_INFINITY));
    }

    @Test
    void respetaLosDigitosPedidos() {
        assertEquals("3.14", FormatoNumero.formatear(Math.PI, 3));
        assertEquals("3.14159265", FormatoNumero.formatear(Math.PI, 9));
    }

    @Test
    @DisplayName("El texto mostrado se puede reutilizar como expresión al continuar una operación")
    void elResultadoSePuedeVolverAEvaluar() {
        for (String expresion : new String[]{"2^70", "2^-30", "-7/2", "1/3"}) {
            String mostrado = FormatoNumero.formatear(EvaluadorMath.evaluar(expresion));
            double original = EvaluadorMath.evaluar(expresion);
            assertEquals(original, EvaluadorMath.evaluar(mostrado), Math.abs(original) * 1e-11, mostrado);
        }
    }

    @Test
    void esNumero() {
        assertTrue(FormatoNumero.esNumero("1.5E-7"));
        assertTrue(FormatoNumero.esNumero("-42"));
        assertFalse(FormatoNumero.esNumero(FormatoNumero.INDEFINIDO));
        assertFalse(FormatoNumero.esNumero(FormatoNumero.INFINITO));
    }
}
