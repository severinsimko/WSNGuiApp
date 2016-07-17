
package controllers;

                

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.AnimationTimer;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.stage.Stage;
import mainparts.DBConnection;

/**
 * A chart that fills in the area between a line of data points and the axes.
 * Good for comparing accumulated totals over time.
 */
public class LineChartTempREALTIME extends Application {
    private static final int MAX_DATA_POINTS = 50;
 private static final Logger log = Logger.getLogger(LineChartLightREALTIME.class.getName());
    private Series series;
    private int xSeriesData = 0;
    private ConcurrentLinkedQueue<Number> dataQ = new ConcurrentLinkedQueue<Number>();
    private ExecutorService executor;
    private AddToQueue addToQueue;
    private Timeline timeline2;
    private NumberAxis xAxis;

    private static Stage secondStage;
    
    public LineChartTempREALTIME(Stage secondStage){
    log.log(Level.WARNING, "LINECHARTLIGHTREALTIME constructor was called");
        this.secondStage=secondStage;
    
        try {
            start(secondStage);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    
    private void init(Stage primaryStage) {
        xAxis = new NumberAxis(0,MAX_DATA_POINTS,MAX_DATA_POINTS/10);
        xAxis.setForceZeroInRange(true);
        xAxis.setAutoRanging(false);
        xAxis.setLabel("Period of time");
        
        primaryStage.setTitle("Temperature real-time graph");
        
        NumberAxis yAxis = new NumberAxis();
        yAxis.setAutoRanging(true);
        yAxis.setLabel("Temp value");
        
        //-- Chart
        final LineChart<Number, Number> sc = new LineChart<Number, Number>(xAxis, yAxis) {
            // Override to remove symbols on each data point
            @Override protected void dataItemAdded(Series<Number, Number> series, int itemIndex, Data<Number, Number> item) {}
        };
        sc.setAnimated(false);
        
        
        
        sc.setStyle("-fx-stroke: black;");

        //-- Chart Series
        series = new LineChart.Series<Number, Number>();
        series.setName("Temperature");
        sc.getData().add(series);

        primaryStage.setScene(new Scene(sc));
    }

    @Override public void start(Stage primaryStage) throws Exception {
        init(primaryStage);
       // primaryStage.show();
       
        //-- Prepare Executor Services
        executor = Executors.newCachedThreadPool();
        addToQueue = new AddToQueue();
        executor.execute(addToQueue);
        //-- Prepare Timeline
        prepareTimeline();
    }
    
    
    public static void showStage(){
        log.log(Level.WARNING, "SHOW STAGE");
        secondStage.show();
    
    }
    
    private class AddToQueue implements Runnable {
        public void run() {
            
            Connection c;
        String SQL;
        SQL = "SELECT * from measureddata WHERE date > NOW() - INTERVAL 5 second;";
        try {

            c = DBConnection.connect();
            log.log(Level.WARNING, "Vykonavam pripojenie " + new Date());
            

            ResultSet rs = c.createStatement().executeQuery(SQL);
            int i = 0;
            while (rs.next()) {
     
                int light = rs.getInt(8);
        
                log.log(Level.WARNING, " LIGHT: " + light);
                
                
                // add a item of random data to queue
                dataQ.add(light);
                
            
                
                
               }
            
            rs.close();
            log.log(Level.WARNING, "Connection by TEMP REAL" +c +" closed");
            c.close();
            
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error on LineChartOutput buildingdata");

        }
        try{
       Thread.sleep(5000);
                executor.execute(this);
        } catch (InterruptedException ex) {
                Logger.getLogger(LineChartLightREALTIME.class.getName()).log(Level.SEVERE, null, ex);
            }
        
            
     }      
  }

    //-- Timeline gets called in the JavaFX Main thread
    private void prepareTimeline() {
        // Every frame to take any data from queue and add to chart
        new AnimationTimer() {
            @Override public void handle(long now) {
                addDataToSeries();
            }
        }.start();
    }

    private void addDataToSeries() {
        for (int i = 0; i < 20; i++) { //-- add 20 numbers to the plot+
            if (dataQ.isEmpty()) break;
            series.getData().add(new LineChart.Data(xSeriesData++, dataQ.remove()));
        }
        // remove points to keep us at no more than MAX_DATA_POINTS
        if (series.getData().size() > MAX_DATA_POINTS) {
            series.getData().remove(0, series.getData().size() - MAX_DATA_POINTS);
        }
        // update 
        xAxis.setLowerBound(xSeriesData-MAX_DATA_POINTS);
        xAxis.setUpperBound(xSeriesData-1);
    }
}