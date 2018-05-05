package com.example.payne.simpletestapp;

import android.util.Log;

import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;

import java.io.File;

public class Manager {

    public static final String SERVER_ADDR = "10.240.201.81";
    public static final int PORT = 8080;
    public static final String NOTIFICATION_FILE_PATH = "acclimate/notifications";

    public ServerConnection mainServer;
    public MainActivity mainActivity;

    public static final String testURL = "https://hackqc.herokuapp.com/api/testPoint";

    public Manager(MainActivity act) throws Exception {

        this.mainActivity = act;

        this.setupStorage();

        // test server setup
        mainServer = new ServerConnection(Manager.SERVER_ADDR, Manager.PORT);
        ServerConnection testServer = new ServerConnection(testURL);

        String response = testServer.getRequest();
        // test
        Log.w("test server : ", response) ;
        JSONObject JSONtest = JSONWrapper.createJSON(response);

        Alerte testAlerte = JSONWrapper.parseGEOJson(JSONtest);

        Log.w("testAlerte : ", testAlerte.toString());

        mainActivity.myMap.addAlertPin(testAlerte);
        mainActivity.myMap.map.setExpectedCenter(
                new GeoPoint(testAlerte.getLattitude(), testAlerte.getLongitude()));
    }

    public void drawPolygon(JSONObject polyPoints){

        this.mainActivity.myMap.drawPolygon(polyPoints);


    }

    private void setupStorage(){

        // check if alert file exist on device
        File file = new File(
                        mainActivity.getApplicationContext().getFilesDir(),
                        Manager.NOTIFICATION_FILE_PATH);
        if(file.exists()){

        }
        else {
            JSONWrapper.createNotificationFile(mainActivity);
        }

    }


}
