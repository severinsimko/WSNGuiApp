/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package underdevelopment;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import mainparts.DBConnection;

public class DynamicLineChart {

    static ObservableList<XYChart.Data<String, Integer>> xyList1 = FXCollections.observableArrayList();
    static ObservableList<XYChart.Data<String, Integer>> xyList2 = FXCollections.observableArrayList();

    static ObservableList<String> myXaxisCategories = FXCollections.observableArrayList();

    static int i;
    private static Task<Date> task;
    private static LineChart<String, Number> lineChart;
    private static XYChart.Series xySeries1;
    private static XYChart.Series xySeries2;
    private static CategoryAxis xAxis;
    private static int lastObservedSize;
   
    public static void etwas() {
        Stage newStage = new Stage();

        xyList1.addListener((ListChangeListener<XYChart.Data>) change -> {
            if (change.getList().size() - lastObservedSize > 10) {
                lastObservedSize += 10;
                xAxis.getCategories().remove(0, 10);
            }
        });

        xAxis = new CategoryAxis();
        xAxis.setLabel("Month");

        final NumberAxis yAxis = new NumberAxis();
        lineChart = new LineChart<>(xAxis, yAxis);

        lineChart.setTitle("Woohoo, 2010");
        lineChart.setAnimated(false);

        task = new Task<Date>() {
            @Override
            protected Date call() throws Exception {
                while (true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException iex) {
                        Thread.currentThread().interrupt();
                    }

                    if (isCancelled()) {
                        break;
                    }

                    updateValue(new Date());
                }
                return new Date();
            }
        };

        task.valueProperty().addListener(new ChangeListener<Date>() {
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
            Random random = new Random();

            @Override
  public void changed(ObservableValue<? extends Date> observableValue, Date oldDate, Date newDate) {

                 Connection c;
        String SQL;
        //Everything for last 24 hours
        SQL = "SELECT * from measureddata WHERE laststatus > NOW() - INTERVAL 10 minute ;";
        try {

            c = DBConnection.connect();

            ResultSet rs = c.createStatement().executeQuery(SQL);

          

            int i = 0;
            while (rs.next()) {

                int light = rs.getInt(6);
                int sound = rs.getInt(7);
                int temp = rs.getInt(8);

                
                i++;
            
                Timestamp stamp = rs.getTimestamp(5);
                
                
               // System.out.println("I got: " +stamp.getNanos());
                LocalDateTime inLocalTime = stamp.toLocalDateTime();
                Integer mili = inLocalTime.getNano()/1000000;
                int seconds =   stamp.getSeconds();
                
                
                
             String strDate = inLocalTime.toString();
//dateFormat.format(newDate);
                myXaxisCategories.add(strDate);

                xyList1.add(new XYChart.Data(strDate, light));
              //  xyList2.add(new XYChart.Data(strDate, temp));
                
                
                

            /* series.getData().add(new XYChart.Data("Temperatur", averageTemp));
             series2.getData().add(new XYChart.Data("Light", averageLight));
             series3.getData().add(new XYChart.Data("Sound", averageSound));*/
            String tempString = "Temperature";
            String soundString = "Sound";
            String lightString = "Light";
           /* series.getData().add(new XYChart.Data(tempString, temp));
            series.getData().add(new XYChart.Data(soundString, sound));
            series.getData().add(new XYChart.Data(lightString, light));*/
            }
/*  
            series2.getData().add(new XYChart.Data(tempString, tempMax));
            series2.getData().add(new XYChart.Data(soundString, soundMax));
            series2.getData().add(new XYChart.Data(lightString, lightMax));

            series3.getData().add(new XYChart.Data(tempString, tempMin));
            series3.getData().add(new XYChart.Data(soundString, soundMin));
            series3.getData().add(new XYChart.Data(lightString, lightMin));*/

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error on BarChartAverageOutput buildingdata");

        }
                
                
                
                
                
                
                
                
                
                
                
                
                
                
                
                
                
                
                
                
                
                
                
                
                
                
                
                
                
                
                
                
                
                
                
                

            }
        });

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(task);

        Scene scene2 = new Scene(lineChart, 800, 600);

        xAxis.setCategories(myXaxisCategories);
//        xAxis.setAutoRanging(false);

        xySeries1 = new XYChart.Series(xyList1);
        xySeries1.setName("Series 1");

        xySeries2 = new XYChart.Series(xyList2);
        xySeries2.setName("Series 2");

        lineChart.getData().addAll(xySeries1, xySeries2);

        i = 0;
        newStage.setScene(scene2);
        newStage.initModality(Modality.APPLICATION_MODAL);

        newStage.show();

    }
}
