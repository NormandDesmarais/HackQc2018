package com.example.payne.simpletestapp;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONObject;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow;

import java.io.File;
import java.util.ArrayList;

import static com.example.payne.simpletestapp.MapDisplay.QUEBEC_BOUNDING_BOX;

public class Manager {

    public static final String SERVER_ADDR = "https://hackqc.herokuapp.com/api";
    public static final int PORT = 8080;
    public static final String NOTIFICATION_FILE_PATH = "/notifications.json";
    public static final String ALERT_FILE_PATH = "alertes";

    public ArrayList<BoundingBox> alertesAbonnees = new ArrayList<>();

    public ServerConnection mainServer;
    public MainActivity mainActivity;
    public MapDisplay myMap;

    public String polygone;

    public static final String pushURL = "https://hackqc.herokuapp.com/api";


    public Manager(MainActivity act, final MapDisplay myMap) {

        this.mainActivity = act;
        this.myMap = myMap;

        this.setupStorage();
        this.getPinsFromServer();

        this.addListenerToPins(myMap.userPins);

        myMap.map.invalidate();
        myMap.redrawScreen();


    }

//MarkerInfoWindow is the default implementation of InfoWindow for a Marker. It handles R.id.bubble_title = OverlayWithIW.getTitle(), R.id.bubble_subdescription = OverlayWithIW.getSubDescription(), R.id.bubble_description = OverlayWithIW.getSnippet(), R.id.bubble_image = Marker.getImage()

    public void addListenerToPins(ArrayList<Marker> pins){

        for (final Marker pin : pins){
            pin.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker, MapView mapView) {
                    return true;
                }
            });
        }

    }


    public void getPinsFromServer(){

        this.mainServer = new ServerConnection(Manager.SERVER_ADDR, Manager.PORT);

        String quebec, userPins, histo;

        try {

            quebec = mainServer.ping(QUEBEC_BOUNDING_BOX);
            userPins = mainServer.getRequest("/getUserAlerts", "");
            String paramHisto = "?nord="  + QUEBEC_BOUNDING_BOX.getLatNorth() +
                                "&sud="   + QUEBEC_BOUNDING_BOX.getLatSouth() +
                                "&est="   + QUEBEC_BOUNDING_BOX.getLonEast() +
                                "&ouest=" + QUEBEC_BOUNDING_BOX.getLonWest();
            histo = mainServer.getRequest("/getHisto", paramHisto);

            this.generatePins(new JSONObject(quebec), new JSONObject(userPins), new JSONObject(histo));

        } catch (Exception e){
            Log.w("PING", "failed to ping server" + Manager.SERVER_ADDR);
        }

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

            result = mainServer.ping(QUEBEC_BOUNDING_BOX);

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

    private void generatePins(JSONObject serverPins, JSONObject userPins, JSONObject histoPins){

        try {
            myMap.updateLists(serverPins, userPins, histoPins);
        } catch (Exception e){
            Log.w("PIN", "could not load new icons");
        }

    }

    public void queryNewPins(){

        // reset all the arrays
        myMap.terrainAlerts = new ArrayList<>();
        myMap.feuAlerts = new ArrayList<>();
        myMap.eauAlerts = new ArrayList<>();
        myMap.meteoAlerts = new ArrayList<>();
        myMap.userPins = new ArrayList<>();
        myMap.historique = new ArrayList<>();

        // request new pins
        this.getPinsFromServer();
        myMap.redrawScreen();

    }

}
