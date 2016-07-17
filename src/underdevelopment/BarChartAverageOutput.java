package underdevelopment;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.logging.Logger;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import mainparts.DBConnection;

import java.util.List;
import java.util.Set;

/**
 * Average bar chart on botton left implemented in this class
 *
 * @author Severin Simko
 */
public class BarChartAverageOutput {

    //Max values
    private static Integer lightMax = Integer.MIN_VALUE;
    private static Integer soundMax = Integer.MIN_VALUE;
    private static Integer tempMax = Integer.MIN_VALUE;

    //Min values
    private static Integer lightMin = Integer.MAX_VALUE;
    private static Integer soundMin = Integer.MAX_VALUE;
    private static Integer tempMin = Integer.MAX_VALUE;

    private static final Logger log = Logger.getLogger(LineChartOutput.class.getName());

    //Charts -must be initialized like this because of the labeling
    private static XYChart.Data<String, Number> tempAverageXY;
    private static XYChart.Data<String, Number> tempMinXY;
    private static XYChart.Data<String, Number> tempMaxXY;

    private static XYChart.Data<String, Number> lightAverageXY;
    private static XYChart.Data<String, Number> lightMinXY;
    private static XYChart.Data<String, Number> lightMaxXY;

    private static XYChart.Data<String, Number> soundAverageXY;
    private static XYChart.Data<String, Number> soundMinXY;
    private static XYChart.Data<String, Number> soundMaxXY;

    //Main class to fill the linechart with data
    public static void buildData(BarChart<String, Number> barChart) {

        //  String selectedString = (String) selected;
        XYChart.Series series = new XYChart.Series();
        series.setName("Average");

        XYChart.Series series2 = new XYChart.Series();
        series2.setName("Max");

        XYChart.Series series3 = new XYChart.Series();
        series3.setName("Min");

        barChart.getData().clear();
        Connection c;
        String SQL;
        //Everything for last 24 hours
        SQL = "select * from measureddata where DATE_SUB(CURDATE(),INTERVAL 1 day) <= laststatus;";
        try {

            c = DBConnection.connect();
            
            

            ResultSet rs = c.createStatement().executeQuery(SQL);

            int temperaturTogether = 0;
            int lightTogether = 0;
            int soundTogether = 0;

            int i = 0;
            while (rs.next()) {

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

            series.getData().addAll(tempAverageXY, soundAverageXY, lightAverageXY);
            series2.getData().addAll(tempMinXY, soundMinXY, lightMinXY);
            series3.getData().addAll(tempMaxXY, soundMaxXY, lightMaxXY);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error on BarChartAverageOutput buildingdata");

        }

        Set<XYChart.Data<String,Number>> setOfCharts= new HashSet<>(Arrays.asList(tempAverageXY,soundAverageXY,lightAverageXY,
                tempMaxXY,soundMaxXY,lightMaxXY,tempMinXY,soundMinXY,lightMinXY));
        
        
     //   LabelDisplay.setLabel(setOfCharts);
       

        barChart.getData().addAll(series2, series, series3);

    }
    
 
    
}
