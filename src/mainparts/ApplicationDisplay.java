package mainparts;

import basestation.TestSensor;
import controllers.BarChartAverageWithLabel;
import controllers.DisplayDatabase;
import controllers.DisplaySearchingDatabase;
import controllers.ComboBoxFilling;
import controllers.LineChartTempREALTIME;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import controllers.CurrentLabel;
import controllers.BarChartExt;
import controllers.ControlPanelConfiguration;
import controllers.LineChartLightREALTIME;
import controllers.ToggleSwitch;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * ApplicationDisplay contains main class to display the stage
 *
 * @author Severin Simko
 */
public class ApplicationDisplay extends Application {

    private static final Logger log = Logger.getLogger(ApplicationDisplay.class.getName());
    private Button button;
    private Object selectedCombo;
    private Button realTime;
    private Button realTime2;
    public static Stage newStage = new Stage();

    public static ControlPanelConfiguration config;

    //will set the whole stage
    public void start(Stage stage) throws Exception {
         stage.setFullScreen(true);
        stage.setTitle("WSN application");
        // tableview display all the infos from DB
        // the result of SELECT * from measureddata;
        TableView tableview;
        tableview = new TableView();
        DisplayDatabase.buildData(tableview);

        // tableview2 display all the infos from DB according to given SQL Query
        TableView tableview2 = new TableView();

        // Basic Panel
        BorderPane borderPane = new BorderPane();

        //HBox at the top of BorderPane
        HBox hBox = new HBox();
        borderPane.setAlignment(hBox, Pos.CENTER);
        borderPane.setMargin(hBox, new Insets(8, 8, 8, 8));
        hBox.setSpacing(5);

        // Elements at the top -Search button and TextField fro SQL Query
        button = new Button("Search");
        TextField textField = new TextField();
        textField.setPrefWidth(400);
        textField.setPromptText("SQL QUERY");

        hBox.getChildren().addAll(textField, button);
        //Setting an action for the Search button

        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                if ((textField.getText() != null && !textField.getText().isEmpty())) {

                    DisplaySearchingDatabase.buildData(tableview2, textField.getText());

                }

            }
        });
        tableview2.setMaxWidth(750);
//****************************************************************************************
//BOTTON
        // HBox at the botton of the panel
        HBox boxBotton = new HBox();
        //Line Chart at the botton of the Pane and located on the boxBotton

        // NumberAxis yAxisAverage = new NumberAxis(0, 100000, 5);
        NumberAxis yAxisAverage = new NumberAxis();
        double lower = yAxisAverage.getLowerBound();
        double upper = yAxisAverage.getUpperBound();
        double tick = yAxisAverage.getTickUnit();

        CategoryAxis xAxisAverage = new CategoryAxis();

        BarChart<String, Number> barChart = new BarChart<String, Number>(xAxisAverage, yAxisAverage);

        //VBox located on the left of the botton on boxBotton 
        // It contains the ChoiceBoxes and Button to submit the choices
        VBox vbox = new VBox();

        ComboBox choice = new ComboBox();
        ComboBoxFilling.buildData(choice,"add");
        choice.prefWidth(150);
        choice.setPromptText("All");

        ComboBox choice2 = new ComboBox();
        choice2.prefWidth(150);

        for (int i = 1; i <= 100; i++) {
            choice2.getItems().add(i);
        }
        choice2.setPromptText("Number");

        ComboBox date = new ComboBox();
        date.setPromptText("Period of time");
        date.getItems().addAll("Minutes", "Hours", "Days");

        Text text = new Text("Choose sensor");
        Text text2 = new Text("Choose period of time");

        CheckBox checkAccelX = new CheckBox("Acceleration -X");
        CheckBox checkAccelY = new CheckBox("Acceleration -Y");
        CheckBox checkAccelZ = new CheckBox("Acceleration -Z");
        CheckBox light = new CheckBox("Light");

        NumberAxis yAxisNew = new NumberAxis(0, 10000, 100);

        CategoryAxis xAxisNew = new CategoryAxis();

        BarChart<String, Number> bar = new BarChart<String, Number>(xAxisNew, yAxisNew);

        BarChartExt<String, Number> bar2 = new BarChartExt<String, Number>(xAxisNew, yAxisNew);

        Button showMeTheGraph = new Button("Show me the chart");
        showMeTheGraph.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {

                Object sensorId = choice.getValue();
                Object chartLength = choice2.getValue();
                Object chartPeriod = date.getValue();
                Boolean lightOn = light.isSelected();
                Boolean AcceYOn = checkAccelY.isSelected();
                Boolean AcceXOn = checkAccelX.isSelected();
                Boolean AcceZOn = checkAccelZ.isSelected();

                BarChartAverageWithLabel.buildData(bar2, sensorId, chartLength, chartPeriod, lightOn, AcceXOn, AcceYOn, AcceZOn);

            }
        });

        HBox numberAndDate = new HBox();

        numberAndDate.getChildren().addAll(choice2, date);
        numberAndDate.setSpacing(5);
        //EXPORT TO PNG
        Button export = new Button();
        export.setText("Export to PNG");
        export.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                BarChartAverageWithLabel.saveAsPng(bar2);

            }
        });

        vbox.getChildren().addAll(text, choice, text2, numberAndDate, light, checkAccelX, checkAccelY, checkAccelZ, showMeTheGraph, export);
        vbox.setSpacing(5);
        vbox.setMargin(text, new Insets(8, 8, 8, 8));
        vbox.setMargin(text2, new Insets(8, 8, 8, 8));
        vbox.setMargin(choice, new Insets(8, 8, 8, 8));
        vbox.setMargin(choice2, new Insets(8, 8, 8, 8));
        vbox.setMargin(light, new Insets(8, 8, 8, 8));
        vbox.setMargin(checkAccelX, new Insets(8, 8, 8, 8));
        vbox.setMargin(checkAccelY, new Insets(8, 8, 8, 8));
        vbox.setMargin(checkAccelZ, new Insets(8, 8, 8, 8));
        vbox.setMargin(numberAndDate, new Insets(8, 8, 8, 8));
        vbox.setMargin(showMeTheGraph, new Insets(8, 8, 8, 8));
        vbox.setMargin(export, new Insets(8, 8, 8, 8));

        VBox bottonLeft = new VBox();

        VBox bottonRight = new VBox();

        realTime = new Button("Light real-time diagram");
        realTime.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
           
                LineChartLightREALTIME.showStage();
            }
        });

        realTime2 = new Button("Temperature real-time diagram");
        realTime2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                //Light realtime graph
                LineChartTempREALTIME.showStage();

            }
        });

        HBox commonInfoUp2 = new HBox();
        commonInfoUp2.setMinHeight(50);
        Text currentLight2 = new Text("Average light for last 3 second from sensor 1 is: ");
        Label currentLightLabel2 = new Label();
        currentLightLabel2.setTextFill(Color.RED);
        CurrentLabel.etwas(currentLightLabel2, 1, "Light");

        //Common info - on the right botton
        HBox commonInfoUp1 = new HBox();
        // commonInfoUp1.setMinWidth(200);
        commonInfoUp1.setMinHeight(50);
        Text currentLight1 = new Text("Average light for last 3 second from sensor 2 is: ");
        Label currentLightLabel = new Label();
        currentLightLabel.setTextFill(Color.RED);
        CurrentLabel.etwas(currentLightLabel, 2, "Light");

    

        
        commonInfoUp1.getChildren().addAll(currentLight1, currentLightLabel);
        commonInfoUp1.setMargin(currentLight1, new Insets(8, 8, 8, 8));
        commonInfoUp1.setMargin(currentLightLabel, new Insets(8, 8, 8, 8));
        commonInfoUp1.setMargin(realTime2, new Insets(8, 8, 8, 8));

        commonInfoUp2.getChildren().addAll(currentLight2, currentLightLabel2, realTime);
        commonInfoUp2.setMargin(currentLight2, new Insets(8, 8, 8, 8));
        commonInfoUp2.setMargin(currentLightLabel2, new Insets(8, 8, 8, 8));
        commonInfoUp2.setMargin(realTime, new Insets(8, 8, 8, 8));


        HBox commonInfoDown = new HBox();
        commonInfoDown.setMinHeight(100);
        commonInfoDown.getChildren().addAll(realTime);

   
        bottonRight.getChildren().addAll(commonInfoUp2,commonInfoUp1, commonInfoDown);
        barChart.setAnimated(false);
        bottonLeft.getChildren().addAll(bottonRight);
        VBox bottonCenter = new VBox();
        Text centerText = new Text("Statistics");
        bottonCenter.getChildren().addAll(centerText, bar2);
        bar2.setPrefHeight(370);
        bottonCenter.setMargin(centerText, new Insets(8, 8, 0, 8));
        bottonCenter.setMargin(bar2,new Insets(0,8,8,8));
        
        
        boxBotton.getChildren().addAll(vbox, bottonCenter, bottonLeft);

        //************************
        // RIGHT
        //borderPane.setAlignment(tableview2, Pos.CENTER_LEFT);
        VBox centerRight = new VBox();
        Text controlPanel = new Text("Control Panel");
        controlPanel.setStyle("-fx-font: 18 arial;");
        ComboBox controlCombo = new ComboBox();
        ComboBoxFilling.buildData(controlCombo,"nothing");

        ToggleSwitch switchBut = new ToggleSwitch();

        HBox toggle0 = new HBox();
        Text idSensor = new Text("Sensor id ");
        toggle0.getChildren().addAll(idSensor, controlCombo);
        toggle0.setSpacing(5);

        HBox toggle5 = new HBox();
        Text sensorState = new Text("Sensor State");
        ToggleSwitch switchBut6 = new ToggleSwitch();
        toggle5.getChildren().addAll(sensorState, switchBut6);
        toggle5.setSpacing(5);

        HBox toggle = new HBox();
        Text novy = new Text("Sensor light ");
        toggle.getChildren().addAll(novy, switchBut);
        toggle.setSpacing(5);

        HBox toggle1 = new HBox();
        Text novy2 = new Text("Sensor acceleration X,Y, Z");
        ToggleSwitch switchBut2 = new ToggleSwitch();
        ToggleSwitch switchBut3 = new ToggleSwitch();
        ToggleSwitch switchBut4 = new ToggleSwitch();
        toggle1.getChildren().addAll(novy2, switchBut2, switchBut3, switchBut4);
        toggle1.setSpacing(5);

        HBox toggle2 = new HBox();
        Text novy3 = new Text("Sensor sampling rate");
        TextField field = new TextField();
        field.setMaxWidth(50);
        toggle2.getChildren().addAll(novy3, field);
        toggle2.setSpacing(5);

        Button setSensor = new Button("Submit");

        setSensor.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {

                Object sensorIdConf = controlCombo.getValue();
                String sensorLightConf = switchBut.getLabel();
                String sensorAcceXConf = switchBut2.getLabel();
                String sensorAcceYConf = switchBut3.getLabel();
                String sensorAcceZConf = switchBut4.getLabel();
                String sensorSamplingConf = field.getText();
                String sensorState = switchBut6.getLabel();

                if (sensorSamplingConf.isEmpty() || sensorIdConf == null || !isInteger(sensorSamplingConf)) {

                    System.out.println(isInteger(sensorSamplingConf));

                    Alert alert = new Alert(AlertType.WARNING);
                    alert.setTitle("Incorrect Value");
                    //   alert.setHeaderText("Look, an Information Dialog");
                    alert.setContentText("Even sensor id or sampling rate is not set corrctly!");

                    alert.showAndWait();

                } else {
                    config.createMessage(sensorIdConf, sensorState, sensorLightConf, sensorAcceXConf, sensorAcceYConf, sensorAcceZConf, sensorSamplingConf);
                }

            }
        });

        centerRight.setMargin(toggle, new Insets(8, 8, 8, 8));
        centerRight.setMargin(toggle0, new Insets(8, 8, 8, 8));
        centerRight.setMargin(toggle1, new Insets(8, 8, 8, 8));
        centerRight.setMargin(toggle2, new Insets(8, 8, 8, 8));
        centerRight.setMargin(toggle5, new Insets(8, 8, 8, 8));
        centerRight.setMargin(setSensor, new Insets(20, 8, 8, 8));

        centerRight.getChildren().addAll(controlPanel, toggle0, toggle5, toggle, toggle1, toggle2, setSensor);

        centerRight.setSpacing(5);

        HBox boxCenter = new HBox();
        boxCenter.setSpacing(5);
        boxCenter.getChildren().addAll(tableview2, centerRight);

        tableview2.setPrefWidth(1000);

        //borderPane.setRight(pane);
        borderPane.setTop(hBox);
        borderPane.setBottom(boxBotton);
        borderPane.setCenter(boxCenter);

        Scene scene = new Scene(borderPane);
        setGlobalEventHandler(borderPane);
        // App Icon
        Image imageStage = new Image("/resources/WSN_logo.png");
        stage.getIcons().add(imageStage);
       // scene.getStylesheets().add("/resources/newCascadeStyleSheet.css");

        //Light realtime running in the backround
        LineChartLightREALTIME lightReal = new LineChartLightREALTIME(new Stage());
        stage.setScene(scene);
        stage.show();
    }

    //Search by using enter
    private void setGlobalEventHandler(BorderPane p) {
        p.addEventHandler(KeyEvent.KEY_PRESSED, ev -> {
            if (ev.getCode() == KeyCode.ENTER) {
                button.fire();
            }
            ev.consume();
        });
    }

    public static void main(String args[]) {
        //when running the APP with testing data from script then
        //try block has to be commented
        TestSensor testSens = new TestSensor();

      /*  try {

            testSens.start();
            config = new ControlPanelConfiguration(testSens);
        } catch (Error e) {

            e.printStackTrace();
        } finally {
*/
            launch(null);
      //  }
       

    }

    //checks whether the string is integer and returns boolean
    public static boolean isInteger(String s) {
        return isInteger(s, 10);
    }

    public static boolean isInteger(String s, int radix) {
        if (s.isEmpty()) {
            return false;
        }
        for (int i = 0; i < s.length(); i++) {
            if (i == 0 && s.charAt(i) == '-') {
                if (s.length() == 1) {
                    return false;
                } else {
                    continue;
                }
            }
            if (Character.digit(s.charAt(i), radix) < 0) {
                return false;
            }
        }
        return true;
    }

}
