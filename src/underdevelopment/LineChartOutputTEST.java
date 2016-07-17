package underdevelopment;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
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
public class LineChartOutputTEST {

    private static final Logger log = Logger.getLogger(LineChartOutputTEST.class.getName());

    private static Task<Date> task;

    private static String path;
    private static TextField pathToExport;

    //Main clas to fill the linechart with data
    public static void buildData(LineChart<Number, Number> lineChart) {

        int[] array = new int[60];

        XYChart.Series series = new XYChart.Series();
        series.setName("Light");

        XYChart.Series series2 = new XYChart.Series();
        series2.setName("Sound");

        XYChart.Series series3 = new XYChart.Series();
        series3.setName("Temperature");

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

        //Refresh the chart data
                //   lineChart.getData().clear();
                Connection c;
                String SQL;
                //  SQL = "SELECT * from measureddata;";
                try {

                    c = DBConnection.connect();

                    SQL = "SELECT * from measureddata WHERE laststatus > NOW() - INTERVAL 1 minute ;";

                    ResultSet rs = c.createStatement().executeQuery(SQL);
                    int i = 0;

                    log.log(Level.WARNING, "TU" + rs);

                    while (rs.next()) {
                        int id = rs.getInt(1);
                        String location = rs.getString(2);
                        String sensors = rs.getString(3);
                        Timestamp stamp = rs.getTimestamp(5);
                        LocalDateTime inLocalTime = stamp.toLocalDateTime();

                        int minutes = inLocalTime.getMinute();
                        int hours = inLocalTime.getHour();
                        int seconds = inLocalTime.getSecond();

                        long time = stamp.getTime();

                        int day = inLocalTime.getDayOfMonth();
                        int month = inLocalTime.getMonthValue();
                        int light = rs.getInt(6);
                        int sound = rs.getInt(7);
                        int temp = rs.getInt(8);

                        array[seconds] = light;

                        log.log(Level.WARNING, "SEKUNDY  " + seconds + "VKLADAM HODNOTU " + light + " NA MIESTO POLA " + array[seconds]);

                    }

                    for (int is = 0; is < array.length; is++) {
                        log.log(Level.WARNING, "Nieco " + is + " Hodnota v ARRAY: " + array[is]);

                        if (array[is] != 0) {

                            series3.getData().add(new XYChart.Data(is, array[is]));

                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Error on LineChartOutput buildingdata");

                }

            }
        });

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(task);

        lineChart.getData().addAll(series3);

    }

}
