/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import basestation.TestSensor;
import java.util.logging.Level;
import java.util.logging.Logger;
import mainparts.ApplicationDisplay;
import org.json.simple.JSONObject;

/**
 * Class shoud prepare the message for sensor in JSON format JSON EXAMPLE:
 * {"sensorSampling":"400","sensorAcceY":"ON","sensorAcceX":"ON","sensorLight":"ON","sensorId":1,"sensorAcceZ":"OFF"}
 *
 * @author Severin Simko
 */
public class ControlPanelConfiguration {

    private static final Logger log = Logger.getLogger(ControlPanelConfiguration.class.getName());
    public JSONObject sensorMessage = new JSONObject();
    public TestSensor testSensor;

    public ControlPanelConfiguration() {

    }

    public ControlPanelConfiguration(TestSensor testSensor) {
        this.testSensor = testSensor;

    }

    public void createMessage(Object sensorIdConf, String sensorState, String sensorLightConf,
            String sensorAcceXConf, String sensorAcceYConf, String sensorAcceZConf,
            String sensorSamplingConf) {

        int sensorId = (int) sensorIdConf;
        int sensorSampling = Integer.valueOf(sensorSamplingConf);

        boolean sensorS;
        if (sensorState.equals("OFF")) {
            sensorS = false;
        } else {
            sensorS = true;
        }

        boolean light;
        if (sensorLightConf.equals("OFF")) {
            light = false;
        } else {
            light = true;
        }

        boolean acceX;
        if (sensorAcceXConf.equals("OFF")) {
            acceX = false;
        } else {
            acceX = true;
        }

        boolean acceY;
        if (sensorAcceYConf.equals("OFF")) {
            acceY = false;
        } else {
            acceY = true;
        }

        boolean acceZ;
        if (sensorAcceZConf.equals("OFF")) {
            acceZ = false;
        } else {
            acceZ = true;
        }

        sensorMessage.put("sensorId", sensorId);
        sensorMessage.put("sensorState", sensorS);
        sensorMessage.put("sensorLight", light);
        sensorMessage.put("sensorAcceX", acceX);
        sensorMessage.put("sensorAcceY", acceY);
        sensorMessage.put("sensorAcceZ", acceZ);
        sensorMessage.put("sensorSampling", sensorSampling);
        // return sensorMessage;
        forwardMessage();

    }

    public void forwardMessage() {

        testSensor.sendControlPaketJSON(sensorMessage);

    }

}
