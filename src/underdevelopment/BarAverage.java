/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package underdevelopment;

import underdevelopment.LineChartOutput;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import mainparts.DBConnection;

/**
 *
 * @author xsimko3
 */
public class BarAverage {

    private static String path;
    private static TextField pathToExport;
    
    //Max values
    private static Integer lightMax = null;
    private static Integer soundMax = null;
    private static Integer tempMax = null;

    //Min values
    private static Integer lightMin = null;
    private static Integer soundMin = null;
    private static Integer tempMin = null;

    private static final Logger log = Logger.getLogger(LineChartOutput.class.getName());

    private static XYChart.Data<String, Number> tempAverageXY;
    private static XYChart.Data<String, Number> tempMinXY;
    private static XYChart.Data<String, Number> tempMaxXY;

    private static XYChart.Data<String, Number> lightAverageXY;
    private static XYChart.Data<String, Number> lightMinXY;
    private static XYChart.Data<String, Number> lightMaxXY;

    private static XYChart.Data<String, Number> soundAverageXY;
    private static XYChart.Data<String, Number> soundMinXY;
    private static XYChart.Data<String, Number> soundMaxXY;

    public static void buildData(BarChart<String, Number> barChart, Object sensorId, Object sensorPeriodNumbers,
            Object sensorPeriod, Boolean lightOn, Boolean temperatureOn, Boolean soundOn) {

        soundMin = Integer.MAX_VALUE;
        lightMin = Integer.MAX_VALUE;
        tempMin = Integer.MAX_VALUE;
        
        tempMax= Integer.MIN_VALUE;
        soundMax= Integer.MIN_VALUE;
        lightMax= Integer.MIN_VALUE;
        
        Integer sensorIdInt = null;
        String sensorIdString = null;
        
        // What sensor I want
        if(sensorId  instanceof Integer ){
            sensorIdInt = (Integer) sensorId;
            log.log(Level.WARNING, "SENSORID IS INTEGER");
        }        
        
        if(sensorId  instanceof String ){
            sensorIdString = (String) sensorId;
        }
        
        //How many(days, hours,minutes) ago
        Integer sensorPeriodInt = (Integer) sensorPeriodNumbers;
        // What time period days, hours,minutes;
        String sensorPeriodString = (String) sensorPeriod;

        XYChart.Series series = new XYChart.Series();
        series.setName("Average");
        XYChart.Series series2 = new XYChart.Series();
        series2.setName("Min");
        XYChart.Series series3 = new XYChart.Series();
        series3.setName("Max");

        barChart.getData().clear();
        barChart.layout();
        Connection c;
        String SQL;
        
        SQL = null;

        try {

            c = DBConnection.connect();

            //The SQL Query
            if (sensorIdInt == null || sensorIdString != null) {

                if (sensorPeriodInt == null && sensorPeriodString == null) {
                    SQL = "SELECT * from measureddata;";
                    log.log(Level.INFO, "Executing:  " + SQL);
                }
                if (!(sensorPeriodString == null) && !(sensorPeriodInt == null)) {
                    if (sensorPeriodString.equals("Days")) {
                        SQL = "SELECT * from measureddata WHERE laststatus > NOW() - INTERVAL " + sensorPeriodInt + " day;";
                        log.log(Level.INFO, "Executing:  " + SQL);
                    }
                    if (sensorPeriodString.equals("Minutes")) {
                        SQL = "SELECT * from measureddata WHERE laststatus > NOW() - INTERVAL " + sensorPeriodInt + " minute;";
                        log.log(Level.INFO, "Executing:  " + SQL);
                    }
                    if (sensorPeriodString.equals("Hours")) {
                        SQL = "SELECT * from measureddata WHERE laststatus > NOW() - INTERVAL " + sensorPeriodInt + " hour;";
                        log.log(Level.INFO, "Executing:  " + SQL);
                    }
                }

            } else {
                if (!(sensorPeriodString == null) && !(sensorPeriodInt == null)) {
                    if (sensorPeriodString.equals("Days")) {
                        SQL = "SELECT * from measureddata WHERE laststatus > NOW() - INTERVAL " + sensorPeriodInt + " day AND id =" + sensorIdInt;
                        log.log(Level.INFO, "Executing:  " + SQL);
                    }
                    if (sensorPeriodString.equals("Minutes")) {
                        SQL = "SELECT * from measureddata WHERE laststatus > NOW() - INTERVAL " + sensorPeriodInt + " minute AND id =" + sensorIdInt;
                        log.log(Level.INFO, "Executing:  " + SQL);
                    }
                    if (sensorPeriodString.equals("Hours")) {
                        SQL = "SELECT * from measureddata WHERE laststatus > NOW() - INTERVAL " + sensorPeriodInt + " hour AND id =" + sensorIdInt;
                        log.log(Level.INFO, "Executing:  " + SQL);
                    }
                } else {

                    // Specified sensor
                    SQL = "SELECT * from measureddata where id =" + sensorId;
                    log.log(Level.INFO, "Executing ELSE :  " + SQL);
                }
            }

            log.log(Level.INFO, "SQL NEW:  " + SQL);
            
            
            Statement s = c.createStatement();
            ResultSet rs = s.executeQuery(SQL);

           
            
            
            int temperaturTogether = 0;
            int lightTogether = 0;
            int soundTogether = 0;

            int i = 0;
            while (rs.next()) {
                
               // log.log(Level.INFO, "Light:  " + rs.getInt(6));
                
                int light = rs.getInt(6);
                int sound = rs.getInt(7);
                int temp = rs.getInt(8);

                //Light Min
                if (light < lightMin) {
                    lightMin = light;

                }
                //Light Max
                if (light > lightMax) {
                    lightMax = light;

                }

                //Temp min
                if (temp < tempMin) {
                    tempMin = temp;

                }
                //Temp max
                if (temp > tempMax) {
                    tempMax = temp;

                }
                //Sound min
                if (sound < soundMin) {
                    soundMin = sound;

                }
                //Sound max
                if (sound > soundMax) {
                    soundMax = sound;

                }

                temperaturTogether += temp;
                lightTogether += light;
                soundTogether += sound;

                i++;
               
                
            }
            //counting
            int averageTemp = temperaturTogether / i;
            int averageLight = lightTogether / i;
            int averageSound = soundTogether / i;

            String tempString = "Temperature";
            String soundString = "Sound";
            String lightString = "Light";

            tempMaxXY = new XYChart.Data(tempString, tempMax);
            soundMaxXY = new XYChart.Data(soundString, soundMax);
            lightMaxXY = new XYChart.Data(lightString, lightMax);

            tempAverageXY = new XYChart.Data(tempString, averageTemp);
            soundAverageXY = new XYChart.Data(soundString, averageSound);
            lightAverageXY = new XYChart.Data(lightString, averageLight);

            tempMinXY = new XYChart.Data(tempString, tempMin);
            soundMinXY = new XYChart.Data(soundString, soundMin);
            lightMinXY = new XYChart.Data(lightString, lightMin);

            log.log(Level.WARNING, "AVeTEMP: " + averageTemp +" AveSOUND " +averageSound + "MAXTEMP "+tempMax + "SOUNDMIN " + soundMin);
            
           
            
            if (lightOn) {
              series.getData().add(lightAverageXY);
              series2.getData().add(lightMinXY);
              series3.getData().add(lightMaxXY);
            
            }
            if(temperatureOn){
              series.getData().add(tempAverageXY);
              series2.getData().add(tempMinXY);
              series3.getData().add(tempMaxXY);
            }
            if(soundOn){
              series.getData().add(soundAverageXY);
              series2.getData().add(soundMinXY);
              series3.getData().add(soundMaxXY);
            }
            
            
            rs.close();
            s.close();
            log.log(Level.WARNING, "Connection by BARAVERAGE" +c +" closed");
            c.close();
            
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error on BarChartAverageOutput buildingdata");

        }

        
        Set<XYChart.Data<String, Number>> setOfCharts= new HashSet<>(Arrays.asList(tempAverageXY, soundAverageXY, lightAverageXY,
                tempMaxXY, soundMaxXY, lightMaxXY, tempMinXY, soundMinXY, lightMinXY));

        
        
       // LabelDisplay m = new LabelDisplay(setOfCharts);

        barChart.getData().addAll(series2, series, series3);        
      
        


    }

    
     public static void saveAsPng(BarChart barChart) {

        Stage newStage = new Stage();
        BorderPane border = new BorderPane();
        Text text = new Text("Choose the path (/path/nameofscreenshot)");
        pathToExport = new TextField();
        pathToExport.setPromptText("Path");
        Button export = new Button("Export");

        VBox boxAtTheTop = new VBox();
        boxAtTheTop.getChildren().setAll(text, pathToExport);

        border.setTop(boxAtTheTop);
        border.setBottom(export);

        border.setMargin(boxAtTheTop, new Insets(8, 8, 8, 8));

        border.setMargin(pathToExport, new Insets(8, 8, 8, 8));
        boxAtTheTop.setSpacing(5);
        export.setAlignment(Pos.CENTER);
        border.setMargin(pathToExport, new Insets(8, 8, 8, 8));

        Scene scena = new Scene(border);
        newStage.setScene(scena);
        newStage.initModality(Modality.APPLICATION_MODAL);
        newStage.show();

        // Pop-Up window is given SQLQuery is not correct
        export.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                path = pathToExport.getText();
                if (!path.isEmpty()) {
                    WritableImage image = barChart.snapshot(new SnapshotParameters(), null);
                    File file = new File(path);
                    try {
                        ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                    newStage.close();
                }
            }
        });

    }
    
    
    
}
