
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
import static javafx.application.Application.launch;
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
public class LineChartLightREALTIME extends Application {
    private static final int MAX_DATA_POINTS = 500;
 private static final Logger log = Logger.getLogger(LineChartLightREALTIME.class.getName());
    private Series series;
    private Series series2;
    private int xSeriesData = 0;
    private ConcurrentLinkedQueue<Number> dataQ1 = new ConcurrentLinkedQueue<Number>();
    private ConcurrentLinkedQueue<Number> dataQ2 = new ConcurrentLinkedQueue<Number>();
    private ExecutorService executor;
    private AddToQueue addToQueue;
    private Timeline timeline2;
    private NumberAxis xAxis;

    private static Stage secondStage;
    
    public LineChartLightREALTIME(Stage secondStage){
   // log.log(Level.WARNING, "LINECHARTLIGHTREALTIME constructor was called");
        this.secondStage=secondStage;
    
        try {
            start(secondStage);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    
    private void init(Stage primaryStage) {
        xAxis = new NumberAxis(0,MAX_DATA_POINTS,MAX_DATA_POINTS/10);
        xAxis.setForceZeroInRange(false);
        xAxis.setAutoRanging(false);
        xAxis.setLabel("Period of time");
        
        primaryStage.setTitle("Light real-time graph");
        
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Light value");
        yAxis.setAutoRanging(true);

        //-- Chart
        final LineChart<Number, Number> sc = new LineChart<Number, Number>(xAxis, yAxis) {
            // Override to remove symbols on each data point
            @Override protected void dataItemAdded(Series<Number, Number> series, int itemIndex, Data<Number, Number> item) {}
        };
        sc.setAnimated(false);

        
        //-- Chart Series
        series = new LineChart.Series<Number, Number>();
        series.setName("ID 1");
         
        series2 = new LineChart.Series<Number, Number>();
        series2.setName("ID 2");
        sc.getData().addAll(series,series2);

        primaryStage.setScene(new Scene(sc));
    }

    @Override public void start(Stage primaryStage) throws Exception {
        init(primaryStage);
       // primaryStage.show();

        setUserAgentStylesheet(STYLESHEET_CASPIAN);
        //-- Prepare Executor Services
        executor = Executors.newCachedThreadPool();
        addToQueue = new AddToQueue();
        executor.execute(addToQueue);
        //-- Prepare Timeline
        prepareTimeline();
    }
    
    
    public static void showStage(){
       // log.log(Level.WARNING, "SHOW STAGE");
        secondStage.show();
    
    }
    
    private class AddToQueue implements Runnable {
        public void run() {
            
            Connection c;
        String SQL;
        SQL = "SELECT id, avg(light) from measureddata WHERE date >= NOW() - INTERVAL 1 second group by id;";
        try {

            c = DBConnection.connect();
          //  log.log(Level.WARNING, "Vykonavam pripojenie " + new Date());
            

            ResultSet rs = c.createStatement().executeQuery(SQL);
            int i = 0;
            while (rs.next()) {
                int nodeID =rs.getInt(1);
                int light = rs.getInt(2);
        
               // log.log(Level.WARNING, " LIGHT: " + light);
                
               
                // add a item of random data to queue
                if(nodeID==1){
                    dataQ1.add(light);
                }else if (nodeID==2){
                    dataQ2.add(light);
                }
            
                
                
               }
            rs.close();
         //   log.log(Level.WARNING, "Connection by LIGHTREALTIME" +c +" closed");
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error on LineChartOutput buildingdata");

        }
        try{
       Thread.sleep(100);
                executor.execute(this);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
             //   Logger.getLogger(LineChartLightREALTIME.class.getName()).log(Level.SEVERE, null, ex);
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
            if (dataQ1.isEmpty()) break;
            series.getData().add(new LineChart.Data(xSeriesData++, dataQ1.remove()));            
        }
        for (int i = 0; i < 20; i++) { //-- add 20 numbers to the plot+
            if (dataQ2.isEmpty()) break;
            series2.getData().add(new LineChart.Data(xSeriesData++, dataQ2.remove()));            
        }
        // remove points to keep us at no more than MAX_DATA_POINTS
        if (series.getData().size() > MAX_DATA_POINTS) {
            series.getData().remove(0, series.getData().size() - MAX_DATA_POINTS);
        }
        if (series2.getData().size() > MAX_DATA_POINTS) {
            series2.getData().remove(0, series2.getData().size() - MAX_DATA_POINTS);
        }
        // update 
        xAxis.setLowerBound(xSeriesData-MAX_DATA_POINTS);
        xAxis.setUpperBound(xSeriesData-1);
    }
}