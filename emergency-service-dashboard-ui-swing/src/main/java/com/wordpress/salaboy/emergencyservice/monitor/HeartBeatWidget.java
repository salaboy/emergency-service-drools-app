/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.emergencyservice.monitor;

import java.awt.Color;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.RectangleInsets;

/**
 *
 * @author esteban
 */
public class HeartBeatWidget {

    private static String DATE_PATTERN = "hh:mm:ss";
    private final TimeSeriesCollection dataset = new TimeSeriesCollection();
    private TimeSeries pulseTimeSeries = new TimeSeries("Pulse");
    private ChartPanel chartPanel;

    public HeartBeatWidget() {
        pulseTimeSeries.setMaximumItemCount(20);
        dataset.addSeries(pulseTimeSeries);
        final JFreeChart chart = createTimeSeriesChart();
        chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(220, 200));
        chartPanel.setName("Monitor");
    }

    private JFreeChart createTimeSeriesChart() {
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Pulse",
                "X",
                "Y",
                dataset,
                true,
                true,
                false);

        chart.setBackgroundPaint(Color.white);

        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
        plot.setDomainCrosshairVisible(true);
        plot.setRangeCrosshairVisible(true);

        XYItemRenderer r = plot.getRenderer();
        if (r instanceof XYLineAndShapeRenderer) {
            XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
            renderer.setBaseShapesVisible(true);
            renderer.setBaseShapesFilled(true);
        }

        DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setDateFormatOverride(new SimpleDateFormat(DATE_PATTERN));

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        NumberFormat numberformat = NumberFormat.getInstance();
        numberformat.setMaximumFractionDigits(0);
        numberformat.setMinimumFractionDigits(0);
        rangeAxis.setNumberFormatOverride(numberformat);

        return chart;
    }

    public void updateMonitorGraph(double pulse) {
        pulseTimeSeries.removeAgedItems(false);
        pulseTimeSeries.addOrUpdate(new Millisecond(), pulse);
    }
    
    public ChartPanel getChartPanel(){
        return this.chartPanel;
    }
}
