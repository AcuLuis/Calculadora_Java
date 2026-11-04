package com.calculadora.math;

import net.objecthunter.exp4j.Expression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Calculus {

    /** Raíz cúbica del épsilon de máquina: paso óptimo para diferencias centrales. */
    private static final double PASO_RELATIVO = Math.cbrt(Math.ulp(1.0));

    /** Raíz cuadrada del épsilon de máquina: paso óptimo para diferencias laterales. */
    private static final double PASO_LATERAL = Math.sqrt(Math.ulp(1.0));

    // ================================================================== derivada

    /** Resultado del análisis de la derivada en un punto. */
    public static final class ResultadoDerivada {

        public enum Estado {
            /** La derivada existe y vale {@code valor}. */
            EXISTE,
            /** f(x) no está definida en el punto. */
            NO_DEFINIDA,
            /** f(x) salta en el punto (por ejemplo floor(x) en un entero). */
            DISCONTINUA,
            /** Las derivadas laterales no coinciden (por ejemplo abs(x) en 0). */
            ESQUINA,
            /** La pendiente crece sin límite (por ejemplo cbrt(x) en 0). */
            TANGENTE_VERTICAL,
            /** f(x) solo existe a un lado del punto (por ejemplo sqrt(x) en 0). */
            SOLO_LATERAL
        }

        public final Estado estado;
        /** Derivada (o derivada lateral cuando solo existe por un lado). */
        public final double valor;
        public final double izquierda;
        public final double derecha;
        /** f(x) en el punto, útil para dibujar la recta tangente. */
        public final double fx;

        ResultadoDerivada(Estado estado, double valor, double izquierda, double derecha, double fx) {
            this.estado = estado;
            this.valor = valor;
            this.izquierda = izquierda;
            this.derecha = derecha;
            this.fx = fx;
        }

        public boolean existe() {
            return estado == Estado.EXISTE;
        }
    }

    /**
     * Calcula la derivada numérica de f(x) en el punto x usando el método de
     * diferencias finitas centrales. El paso h se escala con |x| para mantener
     * la precisión tanto en valores pequeños como grandes.
     */
    public static double derivada(String expresion, double x) {
        Expression e = EvaluadorMath.compilar(expresion, "x");
        double h = ajustarPaso(x, PASO_RELATIVO * Math.max(1.0, Math.abs(x)));
        double fxMasH = e.setVariable("x", x + h).evaluate();
        double fxMenosH = e.setVariable("x", x - h).evaluate();
        return (fxMasH - fxMenosH) / (2 * h);
    }

    /**
     * Calcula la derivada en x y comprueba si realmente existe: compara las
     * derivadas por la izquierda y por la derecha, y detecta saltos y
     * pendientes infinitas.
     *
     * @throws IllegalArgumentException si la sintaxis es inválida
     */
    public static ResultadoDerivada analizarDerivada(String expresion, double x) {
        Expression e = EvaluadorMath.compilar(expresion, "x");
        double escala = Math.max(1.0, Math.abs(x));
        double h = ajustarPaso(x, PASO_RELATIVO * escala);
        double hl = ajustarPaso(x, PASO_LATERAL * escala);

        double fx = Muestreo.evaluarSeguro(e, x);
        if (Double.isNaN(fx)) {
            return resultado(ResultadoDerivada.Estado.NO_DEFINIDA, Double.NaN, Double.NaN, Double.NaN, fx);
        }

        double fIzq = Muestreo.evaluarSeguro(e, x - hl);
        double fDer = Muestreo.evaluarSeguro(e, x + hl);
        double izquierda = (fx - fIzq) / hl;
        double derecha = (fDer - fx) / hl;

        if (Double.isNaN(fIzq) && Double.isNaN(fDer)) {
            return resultado(ResultadoDerivada.Estado.NO_DEFINIDA, Double.NaN, Double.NaN, Double.NaN, fx);
        }
        if (Double.isNaN(fIzq) || Double.isNaN(fDer)) {
            double lado = Double.isNaN(fIzq) ? 1 : -1;
            double lateral = Double.isNaN(fIzq) ? derecha : izquierda;
            // Si al acercarse la pendiente lateral sigue creciendo, es infinita (sqrt(x) en 0)
            double hFino = ajustarPaso(x, hl / 256);
            double lateralFina = lado * (Muestreo.evaluarSeguro(e, x + lado * hFino) - fx) / hFino;
            if (Math.abs(lateralFina) > 3 * Math.abs(lateral) && Math.abs(lateralFina) > 1) {
                lateral = Math.copySign(Double.POSITIVE_INFINITY, lateralFina);
            }
            return resultado(ResultadoDerivada.Estado.SOLO_LATERAL, lateral, izquierda, derecha, fx);
        }

        // Un salto en la función: un lado cambia muchísimo más que el otro
        double saltoIzq = Math.abs(fx - fIzq);
        double saltoDer = Math.abs(fDer - fx);
        if (Math.max(saltoIzq, saltoDer) > 1e3 * Math.min(saltoIzq, saltoDer) + 1e-9 * (1 + Math.abs(fx))) {
            return resultado(ResultadoDerivada.Estado.DISCONTINUA, Double.NaN, izquierda, derecha, fx);
        }

        // Esquina: las pendientes laterales no coinciden
        if (Math.abs(izquierda - derecha) > 1e-3 * (1 + Math.abs(izquierda) + Math.abs(derecha))) {
            return resultado(ResultadoDerivada.Estado.ESQUINA, Double.NaN, izquierda, derecha, fx);
        }

        double central = cociente(e, x, h);
        double centralFino = cociente(e, x, ajustarPaso(x, h / 16));
        // Si al acercarse la pendiente sigue creciendo, la tangente es vertical
        if (Math.abs(centralFino) > 3 * Math.abs(central) && Math.abs(centralFino) > 1) {
            return resultado(ResultadoDerivada.Estado.TANGENTE_VERTICAL, Double.NaN, izquierda, derecha, fx);
        }
        if (Double.isNaN(central)) {
            return resultado(ResultadoDerivada.Estado.NO_DEFINIDA, Double.NaN, izquierda, derecha, fx);
        }
        return resultado(ResultadoDerivada.Estado.EXISTE, central, izquierda, derecha, fx);
    }

    private static double cociente(Expression e, double x, double h) {
        return (Muestreo.evaluarSeguro(e, x + h) - Muestreo.evaluarSeguro(e, x - h)) / (2 * h);
    }

    /** Ajusta h para que x + h sea exactamente representable y no sume error de redondeo. */
    private static double ajustarPaso(double x, double h) {
        double xMasH = x + h;
        return xMasH - x;
    }

    private static ResultadoDerivada resultado(ResultadoDerivada.Estado estado, double valor,
                                               double izquierda, double derecha, double fx) {
        return new ResultadoDerivada(estado, valor, izquierda, derecha, fx);
    }

    // ================================================================== integral

    /** Resultado de una integral definida con su error estimado. */
    public static final class ResultadoIntegral {

        public enum Estado {
            /** El valor converge con la tolerancia pedida. */
            CONVERGE,
            /** El método no logró la precisión: posible asíntota o integral impropia divergente. */
            NO_CONVERGE,
            /** f(x) no tiene valores reales en parte del intervalo. */
            NO_DEFINIDA
        }

        public final Estado estado;
        public final double valor;
        public final double errorEstimado;
        public final int evaluaciones;

        ResultadoIntegral(Estado estado, double valor, double errorEstimado, int evaluaciones) {
            this.estado = estado;
            this.valor = valor;
            this.errorEstimado = errorEstimado;
            this.evaluaciones = evaluaciones;
        }

        public boolean converge() {
            return estado == Estado.CONVERGE;
        }
    }

    private static final int TRAMOS_INICIALES = 16;
    private static final int PROFUNDIDAD_MAXIMA = 40;
    private static final int MAX_EVALUACIONES = 400_000;
    private static final double TOLERANCIA_RELATIVA = 1e-11;

    /**
     * Integral definida de f(x) en [a, b] con la regla de Simpson adaptativa:
     * subdivide solo donde la función lo necesita hasta alcanzar la tolerancia,
     * y estima el error cometido. Las singularidades evitables en los extremos
     * (como sin(x)/x en 0) se resuelven evaluando muy cerca del punto.
     *
     * @throws IllegalArgumentException si la sintaxis es inválida
     */
    public static ResultadoIntegral integrar(String expresion, double a, double b) {
        Expression e = EvaluadorMath.compilar(expresion, "x");
        if (a == b) {
            return new ResultadoIntegral(ResultadoIntegral.Estado.CONVERGE, 0, 0, 0);
        }
        double signo = 1;
        if (a > b) {
            double t = a;
            a = b;
            b = t;
            signo = -1;
        }

        Integrador integrador = new Integrador(e);
        double ancho = (b - a) / TRAMOS_INICIALES;

        // Estimación gruesa para fijar una tolerancia relativa al tamaño del resultado
        double[] fx = new double[2 * TRAMOS_INICIALES + 1];
        for (int i = 0; i < fx.length; i++) {
            double x = (i == fx.length - 1) ? b : a + i * ancho / 2;
            fx[i] = integrador.f(x);
        }
        // Se usa ∫|f| y no ∫f: en ∫sin(x) de 0 a 2π el valor neto es 0 y una
        // tolerancia relativa a él sería imposible de alcanzar
        double estimacionAbs = 0;
        for (int i = 0; i < TRAMOS_INICIALES; i++) {
            estimacionAbs += ancho / 6 * (Math.abs(fx[2 * i]) + 4 * Math.abs(fx[2 * i + 1]) + Math.abs(fx[2 * i + 2]));
        }
        if (integrador.indefinida) {
            return new ResultadoIntegral(ResultadoIntegral.Estado.NO_DEFINIDA, Double.NaN, Double.NaN, integrador.evaluaciones);
        }
        double tolerancia = Math.max(1e-14, TOLERANCIA_RELATIVA * estimacionAbs);

        double suma = 0;
        for (int i = 0; i < TRAMOS_INICIALES; i++) {
            double x0 = a + i * ancho;
            double x1 = (i == TRAMOS_INICIALES - 1) ? b : x0 + ancho;
            double fa = fx[2 * i];
            double fm = fx[2 * i + 1];
            double fb = fx[2 * i + 2];
            double entero = (x1 - x0) / 6 * (fa + 4 * fm + fb);
            suma += integrador.simpson(x0, x1, fa, fm, fb, entero, tolerancia / TRAMOS_INICIALES, PROFUNDIDAD_MAXIMA);
        }

        if (integrador.indefinida) {
            return new ResultadoIntegral(ResultadoIntegral.Estado.NO_DEFINIDA, Double.NaN, Double.NaN, integrador.evaluaciones);
        }

        // Se juzga por el error total: un salto (floor(x)) obliga a subdividir mucho
        // cerca del salto, pero su aporte al error es despreciable
        boolean simpsonValido = !integrador.sinPresupuesto && Double.isFinite(suma)
                && integrador.error <= MARGEN_ERROR * tolerancia;
        boolean haySingularidades = !integrador.singularidades.isEmpty();
        if (simpsonValido && !haySingularidades) {
            return new ResultadoIntegral(ResultadoIntegral.Estado.CONVERGE, signo * suma, integrador.error, integrador.evaluaciones);
        }

        // Cerca de una singularidad el error estimado por Simpson no es confiable:
        // se usa tanh-sinh partiendo el intervalo en esos puntos (1/sqrt(x), ln(x), 1/sqrt(|x|))
        double[] ts = tanhSinhPorTramos(integrador, a, b);
        // Sin singularidades detectadas se exige además que coincida con Simpson,
        // para no aceptar un valor falso cuando hay un polo que ningún punto tocó (tan(x))
        boolean tsValido = ts != null && (haySingularidades
                || (Double.isFinite(suma) && Math.abs(ts[0] - suma) <= 1e-4 * Math.max(1, Math.abs(ts[0]))));
        if (tsValido) {
            return new ResultadoIntegral(ResultadoIntegral.Estado.CONVERGE, signo * ts[0], ts[1], integrador.evaluaciones);
        }
        return new ResultadoIntegral(ResultadoIntegral.Estado.NO_CONVERGE, signo * suma, integrador.error, integrador.evaluaciones);
    }

    /** Aplica tanh-sinh en cada tramo entre singularidades, para que queden siempre en un extremo. */
    private static double[] tanhSinhPorTramos(Integrador integrador, double a, double b) {
        List<Double> cortes = new ArrayList<>();
        cortes.add(a);
        for (double s : integrador.singularidades) {
            if (s > a && s < b) {
                cortes.add(s);
            }
        }
        cortes.add(b);
        Collections.sort(cortes);

        double valor = 0;
        double error = 0;
        int tramos = cortes.size() - 1;
        for (int i = 0; i < tramos; i++) {
            double desde = cortes.get(i);
            double hasta = cortes.get(i + 1);
            if (hasta <= desde) {
                continue;
            }
            double[] parcial = tanhSinh(integrador, desde, hasta);
            if (parcial == null) {
                return null;
            }
            valor += parcial[0];
            error += parcial[1];
        }
        return new double[]{valor, error};
    }

    /** Cuánto puede superar el error estimado a la tolerancia pedida y seguir aceptándose. */
    private static final double MARGEN_ERROR = 100;

    /**
     * Cuadratura tanh-sinh (doble exponencial): concentra los puntos cerca de los
     * extremos, por lo que integra bien funciones que se disparan en a o en b.
     *
     * @return {valor, error estimado} o null si no converge o la cola no decae
     *         (señal de que la integral diverge, como 1/x en [0, 1])
     */
    private static double[] tanhSinh(Integrador integrador, double a, double b) {
        double centro = (a + b) / 2;
        double radio = (b - a) / 2;
        double h = 1;
        double suma = radio * Math.PI / 2 * integrador.f(centro); // t = 0
        double anterior = Double.NaN;

        for (int nivel = 0; nivel <= 10; nivel++) {
            int paso = nivel == 0 ? 1 : 2; // en cada nivel solo se agregan los puntos nuevos (impares)
            // Cada lado avanza hasta donde la precisión doble permite acercarse a su extremo:
            // junto a x = 0 se puede llegar a 1e-270, junto a x = 1 solo a 1e-16
            Lado izquierdo = new Lado();
            Lado derecho = new Lado();
            for (int k = 1; izquierdo.activo || derecho.activo; k += paso) {
                double t = k * h;
                double u = Math.PI / 2 * Math.sinh(t);
                double coshU = Math.cosh(u);
                double distancia = radio / (Math.exp(u) * coshU); // radio * (1 - tanh(u)), sin cancelación
                double peso = radio * Math.PI / 2 * Math.cosh(t) / (coshU * coshU);
                boolean fin = t > 6 || distancia == 0 || peso == 0;
                suma += izquierdo.sumar(integrador, a + distancia, a, peso, distancia, fin);
                suma += derecho.sumar(integrador, b - distancia, b, peso, distancia, fin);
            }
            if (integrador.indefinida || integrador.evaluaciones > 2 * MAX_EVALUACIONES) {
                return null;
            }
            double integral = h * suma;
            double diferencia = Math.abs(integral - anterior);
            double cola = h * (izquierdo.ultimoTermino + derecho.ultimoTermino);
            double omitido = izquierdo.omitido + derecho.omitido;
            double escala = Math.max(1, Math.abs(integral));
            // Si la cola o la parte omitida no son despreciables, la integral diverge (1/x en 0)
            boolean colaDespreciable = cola <= 1e-6 * escala && omitido <= 1e-6 * escala;
            // Cuando la singularidad está en un extremo distinto de 0 la precisión queda
            // limitada por lo que no se pudo integrar junto a él
            boolean estable = diferencia <= Math.max(1e-12 * escala, 4 * omitido);
            if (nivel >= 3 && estable && colaDespreciable) {
                return new double[]{integral, diferencia + cola + omitido};
            }
            anterior = integral;
            h /= 2;
        }
        return null;
    }

    /** Recorrido de tanh-sinh hacia uno de los extremos del intervalo. */
    private static final class Lado {
        boolean activo = true;
        double ultimoTermino = 0;
        double omitido = 0;

        double sumar(Integrador integrador, double x, double extremo, double peso, double distancia, boolean fin) {
            if (!activo) {
                return 0;
            }
            if (fin || x == extremo) { // ya no hay números double entre x y el extremo
                activo = false;
                return 0;
            }
            double fx = integrador.f(x);
            double termino = peso * fx;
            ultimoTermino = Math.abs(termino);
            omitido = distancia * Math.abs(fx); // lo que queda sin integrar hasta el extremo
            return termino;
        }
    }

    /** Estado de una integración adaptativa en curso. */
    private static final class Integrador {
        private final Expression expresion;
        int evaluaciones = 0;
        double error = 0;
        boolean sinPresupuesto = false;
        boolean indefinida = false;
        /** Puntos donde f no estaba definida y hubo que evaluar al lado. */
        final List<Double> singularidades = new ArrayList<>();

        Integrador(Expression expresion) {
            this.expresion = expresion;
        }

        /**
         * f(x) protegida: si el punto exacto no está definido (p. ej. sin(x)/x en 0)
         * se evalúa un poquito a la derecha o a la izquierda.
         */
        double f(double x) {
            evaluaciones++;
            double y = Muestreo.evaluarSeguro(expresion, x);
            if (Double.isNaN(y)) {
                if (singularidades.size() < 8 && !singularidades.contains(x)) {
                    singularidades.add(x);
                }
                double delta = 1e-9 * Math.max(1, Math.abs(x));
                y = Muestreo.evaluarSeguro(expresion, x + delta);
                if (Double.isNaN(y)) {
                    y = Muestreo.evaluarSeguro(expresion, x - delta);
                }
                if (Double.isNaN(y)) {
                    indefinida = true;
                }
            }
            return y;
        }

        double simpson(double a, double b, double fa, double fm, double fb, double entero, double tol, int profundidad) {
            if (indefinida) {
                return Double.NaN;
            }
            double m = (a + b) / 2;
            double fIzq = f((a + m) / 2);
            double fDer = f((m + b) / 2);
            double izq = (m - a) / 6 * (fa + 4 * fIzq + fm);
            double der = (b - m) / 6 * (fm + 4 * fDer + fb);
            double delta = izq + der - entero;

            // También se acepta cuando la diferencia ya es solo ruido de redondeo
            boolean precisionAlcanzada = Math.abs(delta) <= 15 * tol
                    || Math.abs(delta) <= 64 * Math.ulp(Math.abs(izq) + Math.abs(der));
            if (evaluaciones > MAX_EVALUACIONES) {
                sinPresupuesto = true;
            }
            boolean sinRecursos = profundidad <= 0 || sinPresupuesto || m <= a || m >= b;
            if (precisionAlcanzada || sinRecursos || !Double.isFinite(delta)) {
                error += Math.abs(delta) / 15;
                return izq + der + delta / 15; // corrección de Richardson
            }
            return simpson(a, m, fa, fIzq, fm, izq, tol / 2, profundidad - 1)
                    + simpson(m, b, fm, fDer, fb, der, tol / 2, profundidad - 1);
        }
    }
}
