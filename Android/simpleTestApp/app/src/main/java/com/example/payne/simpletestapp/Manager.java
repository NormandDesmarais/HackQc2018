package com.example.payne.simpletestapp;

import android.widget.Toast;

import org.json.JSONObject;
import org.osmdroid.util.BoundingBox;

import java.io.File;

public class Manager {

    public static final boolean USE_TEST_SERVER_POST = true;

    public static final String SERVER_ADDR = "10.240.201.81";
    public static final int PORT = 8080;
    public static final String NOTIFICATION_FILE_PATH = "acclimate/notifications";
    public static final String ALERT_FILE_PATH = "acclimate/alertes";

    public ServerConnection mainServer;
    public ServerConnection testServer;
    public MainActivity mainActivity;
    public MapDisplay myMap;

    public static final String testPushURL = "https://hackqc.herokuapp.com/api/putAlert";


    public Manager(MainActivity act, MapDisplay myMap) throws Exception {

        this.mainActivity = act;
        this.myMap = myMap;

        this.setupStorage();

        // test server setup
        this.mainServer = new ServerConnection(Manager.SERVER_ADDR, Manager.PORT);
        this.testServer = new ServerConnection(testPushURL);

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
            Toast.makeText(mainActivity, "Fichier de notification dÃ©tectÃ©", Toast.LENGTH_SHORT).show();
        }
        else {
            JSONWrapper.createNotificationFile(mainActivity);
        }

        // get all alerts from server
        String result;
        try {

            result = mainServer.ping("/latest", MapDisplay.QUEBEC_BOUNDING_BOX);

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
            Toast.makeText(mainActivity, "impossible de crÃ©er le fichier d'alerte", Toast.LENGTH_SHORT).show();
        }

    }

    public void addUserNotification(BoundingBox boundingBox, String type){

        JSONWrapper.addUserNotificationToFile(boundingBox, type, mainActivity);

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




}
