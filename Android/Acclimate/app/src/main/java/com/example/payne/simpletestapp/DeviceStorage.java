package com.example.payne.simpletestapp;


import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;


/**
 * https://developer.android.com/training/data-storage/shared-preferences#java
 */
    /*
    Android stores Shared Preferences settings as XML file in shared_prefs folder under
    DATA/data/{application package} directory.
    The DATA folder can be obtained by calling Environment.getDataDirectory().

    WRITE EXAMPLE:
        SharedPreferences.Editor editor = mZ_sP.edit();
        editor.putInt(key, newHighScore);
        editor.apply();

    READ EXAMPLE:
        int highScore = mZ_sP.getInt(key, defaultValue);
     */
public class DeviceStorage {

    /*
    https://developer.android.com/training/data-storage/files#WriteInternalStorage
    https://stackoverflow.com/questions/14376807/how-to-read-write-string-from-a-file-in-android?noredirect=1&lq=1

    public static final String APPLICATION_ID = "com.example.payne.simpletestapp"; // pour le Folder_Path
    public static final int MAX_SIZE = 1024; // Pour ne pas détruire la mémoire
    */

    private static int nbrZones = 0;
    private static ArrayList<MonitoredZone> monitoredZones = new ArrayList<>();

    public static final String KEY_SP_MONITORED_ZONES_DIR = "monitored";
    public static final String KEY_SP_MZs_list = "spmzlist";
    public static final String MZs_separator = "#";
    public static final String MZ_coord_separator = "&";

    //TODO: Ajouter la sauvegarde des préférences au niveau des FILTRES.

    private static SharedPreferences mZ_sP;


    public static ArrayList<MonitoredZone> getMonitoredZones() {
        return monitoredZones;
    }
    public static void setMonitoredZones(ArrayList<MonitoredZone> monitoredZones) {
        DeviceStorage.monitoredZones = monitoredZones;
    }


    /**
     * Initialise les paramètres des Monitored Zones lorsque l'application est lancée.
     *
     * @param context Home
     */
    public static void setUpMonitoredZonesSharedPrefs(Context context, MapDisplay myMap) {
        mZ_sP = context.getSharedPreferences(KEY_SP_MONITORED_ZONES_DIR, Context.MODE_PRIVATE);

        // Load up all saved Monitored Zones
        String masterMZ = mZ_sP.getString(KEY_SP_MZs_list, "");

        if(masterMZ.length() != 0) {
            String[] allMZ = masterMZ.split(MZs_separator);

            for (String str : allMZ) {
                String temp[] = str.split(MZ_coord_separator);
                double d0 = Double.parseDouble(temp[0]);
                double d1 = Double.parseDouble(temp[1]);
                double d2 = Double.parseDouble(temp[2]);
                double d3 = Double.parseDouble(temp[3]);

                MapDisplay.ajouterPolygon(myMap, d0, d1, d2, d3);
                monitoredZones.add(new MonitoredZone(d0, d1, d2, d3));
            }

            nbrZones = monitoredZones.size();
        }
    }

    /**
     * Pour ajouter la BoundingBox courante à la liste des MonitoredZones.
     *
     * @param myMap
     */
    public static void addCurrentBoundingBoxToMZSP(MapDisplay myMap) {

        // Extraction de la BoundingBox
        double[] coords = myMap.getBoundingBox();
        MonitoredZone newMZ = new MonitoredZone(coords[0], coords[1], coords[2], coords[3]);
        MapDisplay.ajouterPolygon(myMap, coords[0], coords[1], coords[2], coords[3]);
        String newSP = newMZ.toString();

        String masterMZ = mZ_sP.getString(KEY_SP_MZs_list, "");
        masterMZ += newSP;

        // Removing a possible first entry's erroneous character
        if((masterMZ.charAt(0)+"").equals(MZs_separator))
            masterMZ = masterMZ.substring(1);

        // Saving new MonitoredZones list in SP
        SharedPreferences.Editor editor = mZ_sP.edit();
        editor.putString(KEY_SP_MZs_list, masterMZ);
        editor.apply();

        nbrZones++;
        monitoredZones.add(newMZ);
    }
}