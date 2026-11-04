package com.calculadora.math;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Convierte resultados double en texto legible: redondea el ruido de coma
 * flotante (0.1 + 0.2 = 0.3), elimina ceros sobrantes y usa notación
 * científica solo para números muy grandes o muy pequeños.
 */
public final class FormatoNumero {

    public static final String INDEFINIDO = "Indefinido";
    public static final String INFINITO = "Infinito";

    private static final int DIGITOS_POR_DEFECTO = 12;
    private static final int EXPONENTE_MAX_PLANO = 15;
    private static final int EXPONENTE_MIN_PLANO = -6;

    private FormatoNumero() {
    }

    public static String formatear(double valor) {
        return formatear(valor, DIGITOS_POR_DEFECTO);
    }

    public static String formatear(double valor, int digitosSignificativos) {
        if (Double.isNaN(valor)) {
            return INDEFINIDO;
        }
        if (Double.isInfinite(valor)) {
            return valor > 0 ? INFINITO : "-" + INFINITO;
        }
        if (valor == 0) {
            return "0"; // también cubre -0.0
        }

        BigDecimal redondeado = new BigDecimal(valor)
                .round(new MathContext(digitosSignificativos, RoundingMode.HALF_EVEN))
                .stripTrailingZeros();
        if (redondeado.signum() == 0) {
            return "0";
        }

        int exponente = redondeado.precision() - redondeado.scale() - 1;
        if (exponente >= EXPONENTE_MAX_PLANO || exponente < EXPONENTE_MIN_PLANO) {
            return notacionCientifica(redondeado, exponente);
        }
        return redondeado.toPlainString();
    }

    /** Indica si el texto es un resultado numérico (no "Indefinido" ni "Infinito"). */
    public static boolean esNumero(String texto) {
        try {
            double d = Double.parseDouble(texto);
            return !Double.isNaN(d) && !Double.isInfinite(d);
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    private static String notacionCientifica(BigDecimal valor, int exponente) {
        BigDecimal mantisa = valor.movePointLeft(exponente).stripTrailingZeros();
        return mantisa.toPlainString() + "E" + exponente;
    }
}
