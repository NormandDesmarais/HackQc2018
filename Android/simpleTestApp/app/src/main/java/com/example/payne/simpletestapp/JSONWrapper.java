package com.example.payne.simpletestapp;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.BoundingBox;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

public class JSONWrapper {

    public JSONObject jsonFile;

    public JSONWrapper(String fileUrl) throws Exception {

        jsonFile = new JSONObject(JSONWrapper.getStringFromFile(fileUrl));
        Log.w("jsonFile : ", jsonFile.toString());
    }


    public static JSONObject createJSON(String s) throws Exception{
        return new JSONObject(s);
    }

    /* https://stackoverflow.com/
    questions/12910503/read-file-as-string
    ?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
     */
    private static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }


    private static String getStringFromFile (String filePath) throws Exception {
        File fl = new File(filePath);
        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);
        //Make sure you close all streams.
        fin.close();
        return ret;
    }

    public static Alerte parseGEOJson(JSONObject geoJson){
        return new Alerte(geoJson);
    }

    /**
     * Pour obtenir les coordonnées centrées sur la recherche.
     *
     * @param query String qui correspond à la recherche.
     * @return      double[0] = latitude
     *              double[1] = longitude
     */
    public static double[] googleCenter (String query) {

        /* GOOGLE API (GeoCoding):
        https://developers.google.com/maps/documentation/geocoding/intro
         */

        double[] temp = new double[2];

        String tmp = "https://maps.googleapis.com/maps/api/geocode/json?address=";
        /* TODO: Add QUEBEC BOUNDARIES: (??)
        "https://maps.googleapis.com/maps/api/geocode/json?bounds=34.172684,-118.604794|34.236144,-118.500938&address="
        OR
        "https://maps.googleapis.com/maps/api/geocode/json?region=qc&address="
        */
        ServerConnection connection = new ServerConnection(tmp);

        try {
            String tmpJSON = connection.getRequest(query);

            /*
                "location" : {
                   "lat" : 45.521576,
                   "lng" : -73.73987319999999
                },
             */

            JSONObject temporaryJSON = (JSONObject) JSONWrapper.createJSON(tmpJSON).getJSONArray("results").get(0);
            temp[0] = temporaryJSON.getJSONObject("geometry").getJSONObject("viewport").getDouble("lat");
            temp[1] = temporaryJSON.getJSONObject("geometry").getJSONObject("viewport").getDouble("lng");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return temp;
    }

    /**
     * Pour obtenir les coordonnées qui entourent la recherche.
     *
     * @param query String qui correspond à la recherche.
     * @return  Le BoundingBox (coordonnées Nord/Est/Sud/Ouest)
     */
    public static BoundingBox googleBoundingBox (String query) {

        /* GOOGLE API (GeoCoding):
        https://developers.google.com/maps/documentation/geocoding/intro
         */

        BoundingBox bBox = null;
        String tmp = "https://maps.googleapis.com/maps/api/geocode/json?address=";
        /* TODO: Add QUEBEC BOUNDARIES: (??)
        "https://maps.googleapis.com/maps/api/geocode/json?bounds=34.172684,-118.604794|34.236144,-118.500938&address="
        OR
        "https://maps.googleapis.com/maps/api/geocode/json?region=qc&address="
        */
        ServerConnection connection = new ServerConnection(tmp);

        try {
            String tmpJSON = connection.getRequest(query);

            /*
                "viewport" : {
                   "northeast" : {
                      "lat" : 45.7058381,   = NORTH
                      "lng" : -73.47426     = EAST
                   },
                   "southwest" : {
                      "lat" : 45.410246,    = SOUTH
                      "lng" : -73.986345    = WEST
                   }
                }
             */

            // TODO: eventually remove this Log
            Log.w("JSON test",JSONWrapper.createJSON(tmpJSON).getJSONArray("results").toString());

            JSONObject temporaryJSON = (JSONObject) JSONWrapper.createJSON(tmpJSON).getJSONArray("results").get(0);
            JSONObject boundaryViewPort = temporaryJSON.getJSONObject("geometry").getJSONObject("viewport");
            double[] northEast = {boundaryViewPort.getJSONObject("northeast").getDouble("lat"),
                    boundaryViewPort.getJSONObject("northeast").getDouble("lng")};
            double[] southWest = {boundaryViewPort.getJSONObject("southwest").getDouble("lat"),
                    boundaryViewPort.getJSONObject("northeast").getDouble("lng")};

            double north = northEast[0];
            double south = southWest[0];
            double east = northEast[1];
            double west = southWest[1];

            /* TODO: adjust (??)
            // deux fois plus grand vers la gauche
            double horizOffset = (west-east)/2;
            east = east - horizOffset;
            west = west - (horizOffset);

            // deux fois plus grand vers le bas
            double vertOffset = (north-south)/2;
            south = south - vertOffset;
            */

            bBox = new BoundingBox(north, east, south, west);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bBox;
    }

    /**
     * Crée le fichier de notification contenant toutes les notifications ajoutés par l'utilisateur
     *
     * @param ctx
     */
    public static void createNotificationFile(MainActivity ctx){

        String fileContent = "";

        try {

            JSONObject file = new JSONObject();
            JSONArray notifications = new JSONArray();

            file.put("notifications", notifications);
            Log.w("JSON notification : ", file.toString());
            fileContent = file.toString();


        } catch (JSONException j){
            Toast.makeText(ctx, "Impossible de créer le fichier de notification", Toast.LENGTH_SHORT).show();
        }

        FileOutputStream outputStream;

        try {
            outputStream = ctx.openFileOutput(Manager.NOTIFICATION_FILE_PATH, Context.MODE_PRIVATE);
            outputStream.write(fileContent.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * Permet d'ajouter au fichier de notification un nouveau abonnement à une notification
     *
     * @param boundingBox
     * @param type
     * @param ctx
     */
    public static void addUserNotificationToFile (BoundingBox boundingBox, String type, Context ctx) {

        try {

            JSONObject notificationsFile = new JSONObject(JSONWrapper.getStringFromFile(Manager.NOTIFICATION_FILE_PATH));

            JSONObject notif = new JSONObject();
            JSONObject box = new JSONObject();
            box.put("north", boundingBox.getLatNorth());
            box.put("south", boundingBox.getLatSouth());
            box.put("east", boundingBox.getLonEast());
            box.put("west", boundingBox.getLonWest());

            notif.put("type", type);
            notif.put("box", box);

            JSONArray notifications = notificationsFile.getJSONArray("notifications");
            notifications.put(notif);

            // update content
            String fileContents = notificationsFile.toString();
            Log.w("fileContent : ", notificationsFile.toString());

            // write to File
            FileOutputStream outputStream;
            outputStream = ctx.openFileOutput(Manager.NOTIFICATION_FILE_PATH, Context.MODE_PRIVATE);
            outputStream.write(fileContents.getBytes());
            outputStream.close();

        } catch (Exception j){
            Toast.makeText(ctx, "Désolé : impossible de rajouter une nouvelle notification", Toast.LENGTH_SHORT).show();
        }

    }


    public static void createAlertFile(){



    }


}
