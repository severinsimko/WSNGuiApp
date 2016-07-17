package underdevelopment;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
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
 * The logic of the linechart is implemented in this class According to the
 * customer options it will create required linechart
 *
 * @author Severin Simko
 */
public class LineChartOutput {

    private static final Logger log = Logger.getLogger(LineChartOutput.class.getName());

    private static String path;
    private static TextField pathToExport;

    //Main clas to fill the linechart with data
    public static void buildData(LineChart<Number, Number> lineChart, Object sensorId, Object sensorPeriodNumbers,
           Object sensorPeriod, Boolean lightOn, Boolean temperatureOn, Boolean soundOn) {

        
        
        // What sensor I want
        Integer sensorIdInt = (Integer) sensorId;
        //How many(days, hours,minutes) ago
        Integer sensorPeriodInt = (Integer) sensorPeriodNumbers;

        // What time period days, hours,minutes;
        String sensorPeriodString = (String) sensorPeriod;

        XYChart.Series series = new XYChart.Series();
        series.setName("Light");

        XYChart.Series series2 = new XYChart.Series();
        series2.setName("Sound");

        XYChart.Series series3 = new XYChart.Series();
        series3.setName("Temperature");

        //Refresh the chart data
        lineChart.getData().clear();

        Connection c;
        String SQL;
        SQL = "SELECT * from measureddata;";
        try {

            c = DBConnection.connect();

            //The SQL Query
            if (sensorIdInt == null) {

                if (sensorPeriodInt == null && sensorPeriodString == null) {
                    SQL = "SELECT * from measureddata;";
                    log.log(Level.INFO, "Executing:  " + SQL);
                }
                if (!(sensorPeriodString == null) && !(sensorPeriodInt == null)) {
                    if (sensorPeriodString.equals("Days")) {
                        SQL="SELECT * from measureddata WHERE laststatus > NOW() - INTERVAL " + sensorPeriodInt + " day;";
                        log.log(Level.INFO, "Executing:  " + SQL);
                    }
                    if (sensorPeriodString.equals("Minutes")) {
                         SQL="SELECT * from measureddata WHERE laststatus > NOW() - INTERVAL " + sensorPeriodInt + " minute;";
                        log.log(Level.INFO, "Executing:  " + SQL);
                    }
                    if (sensorPeriodString.equals("Hours")) {
                         SQL="SELECT * from measureddata WHERE laststatus > NOW() - INTERVAL " + sensorPeriodInt + " hour;";
                        log.log(Level.INFO, "Executing:  " + SQL);
                    }
                }

            } else {
                if (!(sensorPeriodString ==null) && !(sensorPeriodInt == null)) {
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

            ResultSet rs = c.createStatement().executeQuery(SQL);
            int i = 0;
            while (rs.next()) {

                int id = rs.getInt(1);
                String location = rs.getString(2);
                String sensors = rs.getString(3);
                Timestamp stamp = rs.getTimestamp(5);
                
                
              
                
                
                LocalDateTime inLocalTime = stamp.toLocalDateTime();
                
             

                int minutes = inLocalTime.getMinute();
                int hours = inLocalTime.getHour();
               // long mili = inLocalTime.getNano()/1000000;
               // log.log(Level.WARNING,"Timestamp: " +mili);
                
                long time = stamp.getTime();

                int day = inLocalTime.getDayOfMonth();
                int month = inLocalTime.getMonthValue();
                int light = rs.getInt(6);
                int sound = rs.getInt(7);
                int temp = rs.getInt(8);                

                if (lightOn) {
                    series.getData().add(new XYChart.Data(minutes, light));
                }
                if (soundOn) {
                    series2.getData().add(new XYChart.Data(minutes, sound));
                }
                if (temperatureOn) {
                    series3.getData().add(new XYChart.Data(minutes, temp));
                }
               }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error on LineChartOutput buildingdata");

        }
        lineChart.getData().addAll(series, series2, series3);

    }

    public static void saveAsPng(LineChart lineChart) {

        Stage newStage = new Stage();
        BorderPane border = new BorderPane();
        Text text = new Text("Choose the path");
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
                    WritableImage image = lineChart.snapshot(new SnapshotParameters(), null);
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
