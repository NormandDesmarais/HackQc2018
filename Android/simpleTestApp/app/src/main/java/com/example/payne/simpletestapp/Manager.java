package com.example.payne.simpletestapp;

import android.util.JsonReader;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.BoundingBox;

import java.io.File;

public class Manager {

    public static final boolean USE_TEST_SERVER_POST = true;

    public static final String SERVER_ADDR = "https://hackqc.herokuapp.com/api/alertes";
    public static final int PORT = 8080;
    public static final String NOTIFICATION_FILE_PATH = "notifications";
    public static final String ALERT_FILE_PATH = "alertes";

    public ServerConnection mainServer;
    public ServerConnection testServer;
    public MainActivity mainActivity;
    public MapDisplay myMap;

    public static final String testPushURL = "https://hackqc.herokuapp.com/api/putAlert";


    public Manager(MainActivity act, MapDisplay myMap) {

        this.mainActivity = act;
        this.myMap = myMap;


        this.setupStorage();

        // test server setup
        this.mainServer = new ServerConnection(Manager.SERVER_ADDR, Manager.PORT);
        this.testServer = new ServerConnection(testPushURL);

        String quebec;

        try {

            quebec = mainServer.ping(MapDisplay.QUEBEC_BOUNDING_BOX);
            Log.w("QC : ", quebec);
            this.generatePins(new JSONObject(quebec));

        } catch (Exception e){
            Log.w("PING", "failed to ping server" + Manager.SERVER_ADDR);
        }


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
            Log.w("STORAGE : ", "OK notif file already exist");
        }
        else {
            Log.w("STORAGE : ", "creating notif file");
            JSONWrapper.createNotificationFile(mainActivity);
        }

        // get all alerts from server
        String result;
        try {

            result = mainServer.ping(MapDisplay.QUEBEC_BOUNDING_BOX);

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
            Log.w("STORAGE : ", "could not setup alert file");
        }

    }

    public void addUserNotification(BoundingBox boundingBox){

        JSONWrapper.addUserNotificationToFile(boundingBox, mainActivity);

    }

    public String AlertFile(String newFileFromServer){

        String currentFile;
        String result = "";

        // get file from storage
        try {
            currentFile = (new JSONWrapper(Manager.ALERT_FILE_PATH)).getStringContent();



            // merge file

            JSONObject jsonServer = new JSONObject(newFileFromServer);

            return result;

        } catch (Exception e){
            e.printStackTrace();
        }

        return result;

    }

    public void postAlert(Alerte alerte){

        // DEBUG
        if (USE_TEST_SERVER_POST) {
            testServer.postAlert(alerte);
        } else mainServer.postAlert(alerte);

    }

    private void generatePins(JSONObject obj){

        try {
            myMap.updateLists(obj);
        } catch (Exception e){
            Log.w("PIN", "could not load new icons");
        }

    }




}
