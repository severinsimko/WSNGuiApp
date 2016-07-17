package controllers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import mainparts.DBConnection;
/**
 * Real time label that changes every 3 seconds
 * 
 */
public class CurrentLabel {

    private static Task<Date> task;
    private static final Logger log = Logger.getLogger(CurrentLabel.class.getName());

    public static void etwas(Label currentLightLabel, int sensorIdInt,String type) {

      //  log.log(Level.WARNING, "Method etwas was called ");
        
        task = new Task<Date>() {
            @Override
            protected Date call() throws Exception {
                while (true) {
                    try {
                        Thread.sleep(3000);
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

              // log.log(Level.WARNING, "Method changed was called ");
                
                Connection c;
                String SQL;
                //Everything for last 24 hours
                SQL = "SELECT avg(light) from measureddata WHERE date >= NOW() - INTERVAL 3 second AND id =" + sensorIdInt + ";";
                try {

                    c = DBConnection.connect();

                    ResultSet rs = c.createStatement().executeQuery(SQL);

                    
                    
                    while (rs.next()) {
                        
                        if(type.equals("Light")){                       
                        currentLightLabel.setText(String.valueOf(rs.getInt(1)));
                        }
                        if(type.equals("Temperature")){                       
                        currentLightLabel.setText(String.valueOf(rs.getInt(8)));
                        }
                        

                    }

                    
                    
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(task);

    }
}
