package com.example.payne.simpletestapp;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.payne.simpletestapp.MainActivities.MainActivity;
import com.example.payne.simpletestapp.Map.MapDisplay;
import com.example.payne.simpletestapp.Objects.Alerte;
import com.example.payne.simpletestapp.Server.ServerConnection;

import org.json.JSONArray;
import org.json.JSONObject;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.io.File;
import java.util.ArrayList;

import static com.example.payne.simpletestapp.Map.MapDisplay.QUEBEC_BOUNDING_BOX;
import static com.example.payne.simpletestapp.Map.MapDisplay.eauIcon;
import static com.example.payne.simpletestapp.Map.MapDisplay.feuIcon;
import static com.example.payne.simpletestapp.Map.MapDisplay.histoClusterIcon;
import static com.example.payne.simpletestapp.Map.MapDisplay.meteoIcon;
import static com.example.payne.simpletestapp.Map.MapDisplay.terrainIcon;

public class Manager {

    public static final String SERVER_ADDR = "https://hackqc.herokuapp.com/api";
    public static final int PORT = 8080;
    public static final String NOTIFICATION_FILE_PATH = "/notifications.json";
    public static final String ALERT_FILE_PATH = "alertes";

    public ArrayList<BoundingBox> alertesAbonnees = new ArrayList<>();

    public ServerConnection mainServer;
    public MainActivity mainActivity;
    public MapDisplay myMap;


    public Manager(MainActivity act, MapDisplay myMap) {

        this.mainActivity = act;
        this.myMap = myMap;

        this.setupStorage();
        this.getPinsFromServer();

        myMap.redrawScreen();
        myMap.map.invalidate();


        for (final Marker pin : myMap.userPins){
            pin.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker, MapView mapView) {
                    MainActivity.pin_on_focus = pin;
                    MainActivity.mainActivity.findViewById(R.id.confirm_dialog).setVisibility(View.VISIBLE);
                    marker.showInfoWindow();
                    mapView.getController().animateTo(marker.getPosition());
                    return true;
                }
            });
        }

    }


    public void getPinsFromServer(){

        this.mainServer = new ServerConnection(Manager.SERVER_ADDR, Manager.PORT);

        String quebec, userPins, histoPins;

        try {

            quebec = mainServer.ping(QUEBEC_BOUNDING_BOX);
            Log.w("QUEBEC", quebec);
            userPins = mainServer.getRequest("/getUserAlerts", "");
            Log.w("userPIns", userPins);

            this.generatePins(new JSONObject(quebec), new JSONObject(userPins));


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
                Toast.makeText(mainActivity, "Fichier d'alertes dÈtectÈ", Toast.LENGTH_SHORT).show();
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

    private void generatePins(JSONObject serverPins, JSONObject userPins){

        try {
            myMap.updateLists(serverPins, userPins);
        } catch (Exception e){
            Log.w("Manager.generatePins", "Could not load new icons");
        }

    }

    public void queryNewPins(){

        myMap.terrainAlerts = new ArrayList<>();
        myMap.feuAlerts = new ArrayList<>();
        myMap.eauAlerts = new ArrayList<>();
        myMap.meteoAlerts = new ArrayList<>();
        myMap.userPins = new ArrayList<>();
        myMap.histoPins = new ArrayList<>();

        this.getPinsFromServer();
        myMap.redrawScreen();

    }

    public void getHistorique () {

        String histoParam = "?nord=" + QUEBEC_BOUNDING_BOX.getLatNorth() +
                "&sud=" + QUEBEC_BOUNDING_BOX.getLatSouth() +
                "&est=" + QUEBEC_BOUNDING_BOX.getLonEast() +
                "&ouest=" + QUEBEC_BOUNDING_BOX.getLonWest();

        try {

            String histoPins = mainServer.getRequest("/getHisto", histoParam);
            JSONArray allHistoAlerts = new JSONObject(histoPins).getJSONArray("alertes");

            for (int i = 0; i < allHistoAlerts.length(); i++) {

                JSONObject histoAlert = allHistoAlerts.getJSONObject(i).getJSONObject("alerte");

                Drawable currentIcon;

                switch (histoAlert.getString("type")) {
                    case "Eau":
                        currentIcon = eauIcon;
                        break;
                    case "Feu":
                        currentIcon = feuIcon;
                        break;
                    case "Terrain":
                        currentIcon = terrainIcon;
                        break;
                    case "Meteo":
                    default:
                        currentIcon = meteoIcon;
                        break;
                }

                // TODO: Create another type of Pin for HISTO !!
                myMap.histoPins.add(myMap.createAlertPin(new Alerte(histoAlert), currentIcon));
//                for(Marker m : myMap.histoPins) // TODO: related to tests on re-adding... remove?
//                    myMap.histoClusters.add(m);
            }
        } catch (Exception e){
            e.printStackTrace();
        }




    }

}
