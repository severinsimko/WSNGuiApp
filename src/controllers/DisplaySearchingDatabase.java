package controllers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import mainparts.DBConnection;

/**
 * TableView according to customer search
 *
 * @author Severin Simko
 */
public class DisplaySearchingDatabase {

    private static ObservableList<ObservableList> data;

    public static void buildData(TableView tableview, String SQLQuery) {

        Connection c;
        data = FXCollections.observableArrayList();
        data.removeAll();
        try {
            c = DBConnection.connect();
            String SQL = SQLQuery;
            //ResultSet  
            ResultSet rs = null;

            try {
                rs = c.createStatement().executeQuery(SQL);
            } catch (SQLException e) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Incorrect SQL Query");
                //   alert.setHeaderText("Look, an Information Dialog");
                alert.setContentText("Your SQL Query is incorrect!");

                alert.showAndWait();

                // System.out.println("Dobre");
            }
            tableview.getColumns().clear();

            for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                //We are using non property style for making dynamic table 

                final int j = i;
                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i + 1));
                col.setCellValueFactory(new Callback<CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                    public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {
                        return new SimpleStringProperty(param.getValue().get(j).toString());
                    }
                });
                tableview.getColumns().addAll(col);
            }
            while (rs.next()) {
                //Iterate Row  
                ObservableList<String> row = FXCollections.observableArrayList();
                row.removeAll();
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    //Iterate Column  
                    row.add(rs.getString(i));
                }

                data.add(row);
            }

            rs.close();
            c.close();
            //FINALLY ADDED TO TableView  
            tableview.setItems(data);
        } catch (Exception e) {
            //   e.printStackTrace();
            System.out.println("Error on DisplaySearchingDatabase");
        }

    }
}
