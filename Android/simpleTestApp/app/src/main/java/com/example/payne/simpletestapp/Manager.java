package com.example.payne.simpletestapp;

import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;
import org.osmdroid.util.BoundingBox;

import java.io.File;
import java.util.ArrayList;

public class Manager {

    public static final String SERVER_ADDR = "https://hackqc.herokuapp.com/api";
    public static final int PORT = 8080;
    public static final String NOTIFICATION_FILE_PATH = "/notifications.json";
    public static final String ALERT_FILE_PATH = "alertes";

    public ArrayList<BoundingBox> alertesAbonnées = new ArrayList<>();

    public ServerConnection mainServer;
    public MainActivity mainActivity;
    public MapDisplay myMap;

    public String polygone;

    public static final String pushURL = "https://hackqc.herokuapp.com/api";


    public Manager(MainActivity act, MapDisplay myMap) {

        this.mainActivity = act;
        this.myMap = myMap;

        this.setupStorage();

        // test server setup
        this.mainServer = new ServerConnection(Manager.SERVER_ADDR, Manager.PORT);

        String quebec;

        try {

            quebec = mainServer.ping(MapDisplay.QUEBEC_BOUNDING_BOX);
            this.generatePins(new JSONObject(quebec));

        } catch (Exception e){
            Log.w("PING", "failed to ping server" + Manager.SERVER_ADDR);
        }

        myMap.map.invalidate();
        myMap.redrawScreen();


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
            Log.w(
            "STORAGE",
            mainActivity.getApplicationContext().getFilesDir() + Manager.NOTIFICATION_FILE_PATH);
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

        mainServer.postAlert(alerte);

    }

    private void generatePins(JSONObject obj){

        try {
            myMap.updateLists(obj);
        } catch (Exception e){
            Log.w("PIN", "could not load new icons");
        }

    }




}
