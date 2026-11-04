package com.calculadora.gui;

import com.calculadora.math.Muestreo;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.XYStyler;
import org.knowm.xchart.style.lines.SeriesLines;
import org.knowm.xchart.style.markers.SeriesMarkers;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Gráfica cartesiana con el tema oscuro de la aplicación. La usan el graficador
 * y las pestañas de cálculo numérico; las series se reemplazan sin recrear el
 * componente, así no parpadea al hacer zoom o al recalcular.
 */
public class GraficaFuncion extends JPanel {

    private final XYChart chart;
    private final XChartPanel<XYChart> panelGrafica;

    public GraficaFuncion() {
        super(new BorderLayout());
        setOpaque(false);

        chart = new XYChartBuilder().width(600).height(400).xAxisTitle("x").yAxisTitle("y").build();
        XYStyler styler = chart.getStyler();
        styler.setChartBackgroundColor(Tema.GRAFICA_FONDO);
        styler.setPlotBackgroundColor(Tema.GRAFICA_AREA_TRAZADO);
        styler.setPlotGridLinesColor(Tema.GRAFICA_CUADRICULA);
        styler.setPlotBorderColor(Tema.GRAFICA_BORDE);
        styler.setChartFontColor(Color.WHITE);
        styler.setAxisTickLabelsColor(Color.LIGHT_GRAY);
        styler.setXAxisDecimalPattern("#.##"); // evita etiquetas como -6.39549436795995
        styler.setLegendVisible(false);
        styler.setLegendPosition(Styler.LegendPosition.InsideNW);
        styler.setLegendBackgroundColor(Tema.GRAFICA_LEYENDA);
        styler.setLegendBorderColor(Tema.GRAFICA_BORDE);
        styler.setLegendFont(Tema.fuente(Font.PLAIN, 13));
        styler.setMarkerSize(10); // solo lo usan los puntos marcados; las curvas no llevan marcadores

        panelGrafica = new XChartPanel<>(chart);
        add(panelGrafica, BorderLayout.CENTER);
    }

    public XYChart getChart() {
        return chart;
    }

    /** Componente donde se dibuja; útil para escuchar eventos del ratón. */
    public JComponent getLienzo() {
        return panelGrafica;
    }

    public void setTitulo(String titulo) {
        chart.setTitle(titulo);
    }

    public void setLeyendaVisible(boolean visible) {
        chart.getStyler().setLegendVisible(visible);
    }

    /** Quita todas las series. Hay que agregar al menos una antes de {@link #refrescar()}. */
    public void limpiar() {
        for (String nombre : new ArrayList<>(chart.getSeriesMap().keySet())) {
            chart.removeSeries(nombre);
        }
    }

    public void agregarCurva(String nombre, Muestreo.Curva curva, Color color) {
        XYSeries serie = chart.addSeries(nombre, curva.x, curva.y);
        serie.setXYSeriesRenderStyle(XYSeriesRenderStyle.Line);
        serie.setMarker(SeriesMarkers.NONE);
        serie.setLineColor(color);
        serie.setLineWidth(2.2f);
    }

    /** Segmento recto, por ejemplo la recta tangente. */
    public void agregarRecta(String nombre, double x1, double y1, double x2, double y2, Color color, boolean discontinua) {
        XYSeries serie = chart.addSeries(nombre, new double[]{x1, x2}, new double[]{y1, y2});
        serie.setXYSeriesRenderStyle(XYSeriesRenderStyle.Line);
        serie.setMarker(SeriesMarkers.NONE);
        serie.setLineColor(color);
        serie.setLineWidth(2f);
        if (discontinua) {
            serie.setLineStyle(SeriesLines.DASH_DASH);
        }
    }

    public void agregarPunto(String nombre, double x, double y, Color color) {
        XYSeries serie = chart.addSeries(nombre, new double[]{x}, new double[]{y});
        serie.setXYSeriesRenderStyle(XYSeriesRenderStyle.Scatter);
        serie.setMarker(SeriesMarkers.CIRCLE);
        serie.setMarkerColor(color);
        serie.setShowInLegend(false);
    }

    /** Región rellena entre la curva y el eje X (por ejemplo, el área de una integral). */
    public void agregarArea(String nombre, List<Double> xs, List<Double> ys, Color relleno) {
        List<Double> px = new ArrayList<>(xs.size() + 2);
        List<Double> py = new ArrayList<>(ys.size() + 2);
        px.add(xs.get(0));
        py.add(0.0);
        for (int i = 0; i < xs.size(); i++) {
            if (!Double.isNaN(ys.get(i))) {
                px.add(xs.get(i));
                py.add(ys.get(i));
            }
        }
        px.add(xs.get(xs.size() - 1));
        py.add(0.0);

        XYSeries serie = chart.addSeries(nombre, px, py);
        serie.setXYSeriesRenderStyle(XYSeriesRenderStyle.PolygonArea);
        serie.setMarker(SeriesMarkers.NONE);
        serie.setFillColor(relleno);
        serie.setLineColor(relleno);
        serie.setShowInLegend(false);
    }

    /** Fija la ventana visible; con rangoY null el eje Y se ajusta solo. */
    public void setRangos(double xMin, double xMax, double[] rangoY) {
        XYStyler styler = chart.getStyler();
        styler.setXAxisMin(xMin);
        styler.setXAxisMax(xMax);
        styler.setYAxisMin(rangoY == null ? null : rangoY[0]);
        styler.setYAxisMax(rangoY == null ? null : rangoY[1]);
    }

    /** Rango vertical recomendado para un conjunto de curvas (ver {@link Muestreo#rangoY}). */
    public static double[] rangoY(Muestreo.Curva... curvas) {
        return Muestreo.rangoY(Arrays.asList(curvas));
    }

    public void refrescar() {
        panelGrafica.revalidate();
        panelGrafica.repaint();
    }

    /** Convierte una coordenada horizontal del lienzo en un valor de x. */
    public double xDesdePixel(int px) {
        return chart.getChartXFromCoordinate(px);
    }

    /** Convierte una coordenada vertical del lienzo en un valor de y. */
    public double yDesdePixel(int py) {
        return chart.getChartYFromCoordinate(py);
    }
}
