package com.wordpress.salaboy.ui;

import java.awt.Color;
import java.text.SimpleDateFormat;
import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.RectangleInsets;
import org.jfree.util.Rotation;

public class TestChart extends JFrame {

    private static final long serialVersionUID = 1L;
    
    private static String DATE_PATTERN = "hh:mm:ss";
    private final TimeSeriesCollection dataset = new TimeSeriesCollection();
    private TimeSeries pulseTimeSeries = new TimeSeries("Pulse");

    public TestChart(String applicationTitle, String chartTitle) {
        super(applicationTitle);
        dataset.addSeries(pulseTimeSeries);
        final JFreeChart chart = createTimeSeriesChart();
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        // add it to our application
        setContentPane(chartPanel);
        
        new Thread(new Runnable(){

            @Override
            public void run() {
                while (true){
                    try {
                        pulseTimeSeries.addOrUpdate(new Millisecond(), Math.random()*100);
                        TestChart.this.validateTree();
                        //ChartUtilities.saveChartAsJPEG(new File("/tmp/chart"+System.currentTimeMillis()), chart, 200, 200);
                        Thread.sleep(2000);
                        } catch (Exception ex) {
                        }
                }
            }
            
        }).start();

    }

    /**
     * Creates a sample dataset 
     */
    private PieDataset createDataset() {
        DefaultPieDataset result = new DefaultPieDataset();
        result.setValue("Linux", 29);
        result.setValue("Mac", 20);
        result.setValue("Windows", 51);
        return result;

    }

    /**
     * Creates a chart
     */
    private JFreeChart createChart(PieDataset dataset, String title) {

        JFreeChart chart = ChartFactory.createPieChart3D(
                title, // chart title
                dataset, // data
                true, // include legend
                true,
                false);

        PiePlot3D plot = (PiePlot3D) chart.getPlot();
        plot.setStartAngle(290);
        plot.setDirection(Rotation.CLOCKWISE);
        plot.setForegroundAlpha(0.5f);
        return chart;

    }
    
    private JFreeChart createTimeSeriesChart() {
		JFreeChart chart = ChartFactory.createTimeSeriesChart(
				"Some Title", 
				"X", 
				"Y",
				dataset, 
				true, 
				true, 
				false
		);

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

		return chart;
	}
}