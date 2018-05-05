package com.example.payne.simpletestapp;

import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;

import java.io.File;

public class Manager {

    public static final String SERVER_ADDR = "10.240.201.81";
    public static final int PORT = 8080;
    public static final String NOTIFICATION_FILE_PATH = "acclimate/notifications";
    public static final String ALERT_FILE_PATH = "acclimate/alertes";

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

        // check if notification File exist on device and create one if needed
        File notif = new File(
                        mainActivity.getApplicationContext().getFilesDir(),
                        Manager.NOTIFICATION_FILE_PATH);
        if(notif.exists()){
            Toast.makeText(mainActivity, "Fichier de notification détecté", Toast.LENGTH_SHORT).show();
        }
        else {
            JSONWrapper.createNotificationFile(mainActivity);
        }

        // get alerts from server
        String result;
        try {
            result = mainServer.ping();

            // check if alert File exist on device and create one if needed
            File alertes = new File(
                    mainActivity.getApplicationContext().getFilesDir(),
                    Manager.ALERT_FILE_PATH);
            if(alertes.exists()){
                Toast.makeText(mainActivity, "Fichier d'alertes détecté", Toast.LENGTH_SHORT).show();
            }
            else {
                JSONWrapper.createAlertFile(mainActivity, result);
            }
        } catch (Exception e){
            Toast.makeText(mainActivity, "impossible de créer le fichier d'alerte", Toast.LENGTH_SHORT).show();
        }

    }

    public void addUserNotification(BoundingBox boundingBox, String type){

        JSONWrapper.addUserNotificationToFile(boundingBox, type, mainActivity);

    }

    public String mergeAlertFile(String newFileFromServer){

        String currentFile;

        // get file from storage
        try {
            currentFile = (new JSONWrapper(Manager.ALERT_FILE_PATH)).getStringContent();

        } catch (Exception e){
            e.printStackTrace();
        }

        // merge file
        String result = "";

        JSONObject jsonServer = new JSONObject();

        return result;

    }

}
