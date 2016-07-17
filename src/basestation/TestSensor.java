package basestation;

/*									tab:4
 * Copyright (c) 2005 The Regents of the University  of California.  
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * - Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the
 *   distribution.
 * - Neither the name of the copyright holders nor the names of
 *   its contributors may be used to endorse or promote products derived
 *   from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
/**
 * Java-side application for testing serial port communication.
 *
 *
 * @author Phil Levis <pal@cs.berkeley.edu>
 * @date August 12 2005
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import java.io.IOException;

import org.json.simple.JSONObject;

import net.tinyos.message.*;
import net.tinyos.packet.*;
import net.tinyos.util.*;

public class TestSensor extends Thread implements MessageListener {

    private MoteIF moteIF;
    private static Connection con;
    private static TestSensor serial;

    public TestSensor(MoteIF moteIF) {
        this.moteIF = moteIF;
        this.moteIF.registerListener(new TestSensorMsg(), this);
        System.out.print("Mote Inititialized");
    }

    public TestSensor() {
    }

    public void run() {

        try {
            main(new String[4]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendControlPaketJSON(JSONObject object) {
        
        System.out.print("I received the send control packet"+ object.toJSONString());
        
        // {"sensorSampling":10,"sensorAcceY":false,"sensorAcceX":true,"sensorLight":true,"sensorId":1,"sensorState":1,"sensorAcceZ":false}
        int nodeID = (int) object.get("sensorId");
        boolean nodeState = (boolean) object.get("sensorState");
        boolean lightState = (boolean) object.get("sensorLight");
        boolean xaccelState = (boolean) object.get("sensorAcceX");
        boolean yaccelState = (boolean) object.get("sensorAcceY");
        boolean zaccelState = (boolean) object.get("sensorAcceZ");
        int sampleRate = (int) object.get("sensorSampling");

        serial.sendControlPaket(nodeID, nodeState, lightState, xaccelState, yaccelState, zaccelState, sampleRate);

    }

    public void sendControlPaket(int nodeID, boolean nodeState, boolean lightState, boolean xaccelState,
            boolean yaccelState, boolean zaccelState, int sampleRate) {
        
        System.out.print("I'm going to send the control packet");
        System.out.println(nodeID+" -"+nodeState+" -"+lightState+" -"+xaccelState+" -"+yaccelState+" -"+zaccelState + " / "+sampleRate);
        TestSendPaket payload = new TestSendPaket();
        payload.set_node_id(nodeID);
        int state = 0;
        if (nodeState) {
            state += 1;
        }
        if (lightState) {
            state += 2;
        }
        if (xaccelState) {
            state += 4;
        }
        if (yaccelState) {
            state += 8;
        }
        if (zaccelState) {
            state += 16;
        }
        payload.set_state(state);
        payload.set_samplerate(sampleRate);
      
        if (moteIF == null){
            System.out.println("MOTE IF is NULL");
        }
        
                    
        try {
            // TODO die 0 steht f�r die nodeid oder was???
            
            moteIF.send(0, payload);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public void messageReceived(int to, Message message) {
        TestSensorMsg msg = (TestSensorMsg) message;
        System.out.println("ID: " + msg.get_node_id() + " time stamp: " + msg.get_time_stamp() + " Light: "
                + msg.get_value() + " X: " + msg.get_accelx() + " Y: " + msg.get_accely() + " Z: " + msg.get_accelz());

        Statement update;
        try {
            update = con.createStatement();

            // TODO use real date after timesync!
            String timestamp = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss.SSS", Locale.GERMANY).format(new Date());
            // System.out.println(timestamp);
            update.execute(
                    "insert into measureddata VALUES (" + msg.get_node_id() + ",'" + timestamp + "'," + msg.get_value()
                    + "," + msg.get_accelx() + "," + msg.get_accely() + "," + msg.get_accelz() + ", 0,0)");
            update.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        /*
         * try { TestSensorMsg new_msg = new TestSensorMsg();
         * new_msg.set_value(msg.get_value()); moteIF.send(0, new_msg); } catch
         * (IOException exception) { System.err.println(
         * "Exception thrown when sending packets. Exiting.");
         * System.err.println(exception); }
         */
    }

    private static void usage() {
        System.err.println("usage: TestSerial [-comm <source>]");
    }

    public static void main(String[] args) throws Exception {
        String source = null;
        if (args.length == 2) {
            if (!args[0].equals("-comm")) {
                usage();
                System.exit(1);
            }
            source = args[1];
        } else if (args.length == 4) {

        } else if (args.length != 0) {

            usage();
            System.exit(1);
        }

        // TODO for not usinng -comm on call
        source = "serial@/dev/ttyUSB1:intelmote2";

        PhoenixSource phoenix;

        if (source == null) {
            phoenix = BuildSource.makePhoenix(PrintStreamMessenger.err);
        } else {
            phoenix = BuildSource.makePhoenix(source, PrintStreamMessenger.err);
        }

        String url = "jdbc:mysql://localhost:3306/testdb";
        String user = "WSN";
        String password = "123";

        // Load the Connector/J driver
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (ClassNotFoundException cnfe) {
            System.err.println("Error: " + cnfe.getMessage());
        } catch (InstantiationException ie) {
            System.err.println("Error: " + ie.getMessage());
        } catch (IllegalAccessException iae) {
            System.err.println("Error: " + iae.getMessage());
        }

        final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        /*
         * TimerTask myTask = new TimerTask() { public void run() { // needs
         * table averagedata in MYSQL-DB // INT nodeID DATETIME date int light
         * int xaccel int yaccel int // zaccel int temperature int humidity
         * 
         * // now
         * System.out.println(dateFormatter.format(System.currentTimeMillis()));
         * 
         * // now before 5 seconds Date dateNow = new
         * Date(System.currentTimeMillis() - 5 * 1000);
         * 
         * System.out.println(dateFormatter.format(dateNow)); // lower border
         * (>=) // now before 5 seconds with 000 milliseconds String lowB =
         * dateFormatter.format(new Date(dateNow.getTime() / 1000 * 1000));
         * System.out.println(lowB); // upper border (<) String highB =
         * dateFormatter.format(new Date(dateNow.getTime() / 1000 * 1000 +
         * 1000)); System.out.println(highB);
         * 
         * 
         * String newTime = dateFormatter.format(new Date(dateNow.getTime() /
         * 1000 * 1000 + 500));
         * 
         * //TODO kl�ren ob das ganze zum zeitpunkt low oder high eingef�gt
         * werden soll
         * 
         * Statement stmt; try { stmt = con.createStatement(); ResultSet results
         * = stmt.executeQuery(
         * "select id, AVG(light), AVG(accelx), AVG(accely), AVG(accelz), AVG(temperature), AVG(humidity)"
         * + "from measureddata" + " where date >='" + lowB + "' and date <'" +
         * highB + "'" + " group by id"); while (results.next()) { Statement
         * insert = con.createStatement(); insert.execute(
         * "insert into averagedata VALUES (" + results.getInt(1) +
         * ",'"+newTime+"'," + results.getInt(2) + "," + results.getInt(3) + ","
         * + results.getInt(4) + "," + results.getInt(5) + "," +
         * results.getInt(6) + "," + results.getInt(7) + ")"); insert.close(); }
         * results.close(); stmt.close(); } catch (SQLException e) { // TODO
         * Auto-generated catch block e.printStackTrace(); } } };
         * 
         * Timer timer = new Timer(); timer.schedule(myTask, 1000, 1000);
         */
        // Establish connection to MySQL
        con = DriverManager.getConnection(url, user, password);

        MoteIF mif = new MoteIF(phoenix);
        serial = new TestSensor(mif);
    }

}
