package controllers;

import underdevelopment.LabelDisplay;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.Axis;
import javafx.scene.chart.BarChart;
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
import underdevelopment.LineChartOutput;

/**
 * Class responsible for average values-min,max, average + labeling at the top
 * of the chart
 *
 * @author Severin Simko
 */
public class BarChartAverageWithLabel {

    private static String path;
    private static TextField pathToExport;

    //Max values
    private static Integer lightMax = null;
    private static Integer acceYMax = null;
    private static Integer acceXMax = null;
    private static Integer acceZMax = null;

    //Min values
    private static Integer lightMin = null;
    private static Integer acceYMin = null;
    private static Integer acceXMin = null;
    private static Integer acceZMin = null;

    private static final Logger log = Logger.getLogger(LineChartOutput.class.getName());

    private static XYChart.Data<String, Number> acceXAverageXY;
    private static XYChart.Data<String, Number> acceXMinXY;
    private static XYChart.Data<String, Number> acceXMaxXY;

    private static XYChart.Data<String, Number> lightAverageXY;
    private static XYChart.Data<String, Number> lightMinXY;
    private static XYChart.Data<String, Number> lightMaxXY;

    private static XYChart.Data<String, Number> acceYAverageXY;
    private static XYChart.Data<String, Number> acceYMinXY;
    private static XYChart.Data<String, Number> acceYMaxXY;

    private static XYChart.Data<String, Number> acceZAverageXY;
    private static XYChart.Data<String, Number> acceZMinXY;
    private static XYChart.Data<String, Number> acceZMaxXY;

    public static void buildData(BarChartExt<String, Number> barChart, Object sensorId, Object sensorPeriodNumbers,
            Object sensorPeriod, Boolean lightOn, Boolean acceX, Boolean acceY, Boolean acceZ) {

        acceYMin = Integer.MAX_VALUE;
        lightMin = Integer.MAX_VALUE;
        acceXMin = Integer.MAX_VALUE;
        acceZMin = Integer.MAX_VALUE;

        acceXMax = Integer.MIN_VALUE;
        acceYMax = Integer.MIN_VALUE;
        lightMax = Integer.MIN_VALUE;
        acceZMax = Integer.MIN_VALUE;

        Integer sensorIdInt = null;
        String sensorIdString = null;

        // What sensor I want
        if (sensorId instanceof Integer) {
            sensorIdInt = (Integer) sensorId;
            log.log(Level.WARNING, "SENSORID IS INTEGER");
        }

        if (sensorId instanceof String) {
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
                        SQL = "SELECT * from measureddata WHERE date > NOW() - INTERVAL " + sensorPeriodInt + " day;";
                        log.log(Level.INFO, "Executing:  " + SQL);
                    }
                    if (sensorPeriodString.equals("Minutes")) {
                        SQL = "SELECT * from measureddata WHERE date > NOW() - INTERVAL " + sensorPeriodInt + " minute;";
                        log.log(Level.INFO, "Executing:  " + SQL);
                    }
                    if (sensorPeriodString.equals("Hours")) {
                        SQL = "SELECT * from measureddata WHERE date > NOW() - INTERVAL " + sensorPeriodInt + " hour;";
                        log.log(Level.INFO, "Executing:  " + SQL);
                    }
                }

            } else {
                if (!(sensorPeriodString == null) && !(sensorPeriodInt == null)) {
                    if (sensorPeriodString.equals("Days")) {
                        SQL = "SELECT * from measureddata WHERE date > NOW() - INTERVAL " + sensorPeriodInt + " day AND id =" + sensorIdInt;
                        log.log(Level.INFO, "Executing:  " + SQL);
                    }
                    if (sensorPeriodString.equals("Minutes")) {
                        SQL = "SELECT * from measureddata WHERE date > NOW() - INTERVAL " + sensorPeriodInt + " minute AND id =" + sensorIdInt;
                        log.log(Level.INFO, "Executing:  " + SQL);
                    }
                    if (sensorPeriodString.equals("Hours")) {
                        SQL = "SELECT * from measureddata WHERE date > NOW() - INTERVAL " + sensorPeriodInt + " hour AND id =" + sensorIdInt;
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

            int acceXTogether = 0;
            int lightTogether = 0;
            int acceYTogether = 0;
            int acceZTogether = 0;

            int i = 0;
            while (rs.next()) {

                // log.log(Level.INFO, "Light:  " + rs.getInt(6));
                //uz znemene
                int light = rs.getInt(3);
                int acceYRS = rs.getInt(5);
                int acceXRS = rs.getInt(4);
                int acceZRS = rs.getInt(6);

                //Light Min
                if (light < lightMin) {
                    lightMin = light;

                }
                //Light Max
                if (light > lightMax) {
                    lightMax = light;

                }

                //Acce Z min
                if (acceZRS < acceZMin) {
                    acceZMin = acceZRS;

                }
                //Acce Z max
                if (acceZRS > acceZMax) {
                    acceZMax = acceZRS;

                }

                //Acce X min
                if (acceXRS < acceXMin) {
                    acceXMin = acceXRS;

                }
                //Acce X max
                if (acceXRS > acceXMax) {
                    acceXMax = acceXRS;

                }
                //Acce Y min
                if (acceYRS < acceYMin) {
                    acceYMin = acceYRS;

                }
                //Acce Y max
                if (acceYRS > acceYMax) {
                    acceYMax = acceYRS;

                }

                acceXTogether += acceXRS;
                lightTogether += light;
                acceYTogether += acceYRS;
                acceZTogether += acceZRS;

                i++;

            }
            //counting
            int averageAcceX = acceXTogether / i;
            int averageLight = lightTogether / i;
            int averageAcceY = acceYTogether / i;
            int averageAcceZ = acceZTogether / i;

            String acceXString = "Acceleration-X";
            String acceZString = "Acceleration-Z";
            String acceYString = "Acceleration-Y";
            String lightString = "Light";

            acceXMaxXY = new XYChart.Data(acceXString, acceXMax);
            acceYMaxXY = new XYChart.Data(acceYString, acceYMax);
            lightMaxXY = new XYChart.Data(lightString, lightMax);
            acceZMaxXY = new XYChart.Data(acceZString, acceZMax);

            acceXAverageXY = new XYChart.Data(acceXString, averageAcceX);
            acceYAverageXY = new XYChart.Data(acceYString, averageAcceY);
            lightAverageXY = new XYChart.Data(lightString, averageLight);
            acceZAverageXY = new XYChart.Data(acceZString, averageAcceZ);

            acceXMinXY = new XYChart.Data(acceXString, acceXMin);
            acceZMinXY = new XYChart.Data(acceZString, acceZMin);
            acceYMinXY = new XYChart.Data(acceYString, acceYMin);
            lightMinXY = new XYChart.Data(lightString, lightMin);

            log.log(Level.WARNING, "AVeTEMP: " + averageAcceX + " AveSOUND " + averageAcceY);

            if (lightOn) {
                series.getData().add(lightAverageXY);
                series2.getData().add(lightMinXY);
                series3.getData().add(lightMaxXY);

            }
            if (acceX) {
                series.getData().add(acceXAverageXY);
                series2.getData().add(acceXMinXY);
                series3.getData().add(acceXMaxXY);
            }
            if (acceY) {
                series.getData().add(acceYAverageXY);
                series2.getData().add(acceYMinXY);
                series3.getData().add(acceYMaxXY);
            }

            if (acceZ) {
                series.getData().add(acceZAverageXY);
                series2.getData().add(acceZMinXY);
                series3.getData().add(acceZMaxXY);
            }

            rs.close();
            s.close();
            log.log(Level.WARNING, "Connection by BARAVERAGE" + c + " closed");
            c.close();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error on BarChartAverageOutput buildingdata");

        }

        Set<XYChart.Data<String, Number>> setOfCharts = new HashSet<>(Arrays.asList(acceXAverageXY, acceYAverageXY, acceZAverageXY, lightAverageXY,
                acceXMaxXY, acceYMaxXY, acceZMaxXY, lightMaxXY, acceXMinXY, acceYMinXY, acceZMinXY, lightMinXY));

        barChart.getData().clear();
        barChart.getData().addAll(series2, series, series3);

    }

    public static void saveAsPng(BarChartExt barChart) {

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
