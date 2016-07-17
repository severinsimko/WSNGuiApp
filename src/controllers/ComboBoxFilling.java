package controllers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;
import javafx.scene.control.ComboBox;
import mainparts.DBConnection;

/**
 * ComboBoxFilling class should fill combobox with sensors id from the available
 * sensors
 *
 * @author Severin Simko
 */
public class ComboBoxFilling {

    /*
     Main method to build the Box with data
     */
    public static void buildData(ComboBox choice, String add) {

        // Set with no duplicates
        Set<Integer> ids = new HashSet<>();
        Connection c;

        try {

            c = DBConnection.connect();
            String SQL = "SELECT id from measureddata";

            ResultSet rs = c.createStatement().executeQuery(SQL);
            int i = 0;
            while (rs.next()) {
                int id = rs.getInt(1);
                ids.add(id);
            }

            rs.close();
            c.close();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error on DatabaseOutput buildingdata");

        }

        for (Integer i : ids) {
            choice.getItems().add(i);

        }

        if (add.equals("add")) {
            choice.getItems().add("All");
        }
    }

}
