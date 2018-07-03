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
        editor.putString(key, "newHighScore");
        editor.apply();

    READ EXAMPLE:
        String highScore = mZ_sP.getString(key, defaultValue);
     */
public class Preferences {

    /*
    https://developer.android.com/training/data-storage/files#WriteInternalStorage
    https://stackoverflow.com/questions/14376807/how-to-read-write-string-from-a-file-in-android?noredirect=1&lq=1

    public static final String APPLICATION_ID = "com.example.payne.simpletestapp"; // pour le Folder_Path
    public static final int MAX_SIZE = 1024; // Pour ne pas détruire la mémoire
    */

    private static int nbrZones = 0;
    private static ArrayList<MonitoredZone> monitoredZones = new ArrayList<>();

    public static final String DIR_SP_MONITORED_ZONES = "monitored";
    public static final String DIR_SP_FILTERS = "filters";

    public static final String KEY_SP_MZS_LIST = "spmzlist";
    public static final String MZS_SEPARATOR = "#";
    public static final String MZ_COORD_SEPARATOR = "&";

    public static final String KEY_SP_FILTERS_LIST = "flist";

    //TODO: Ajouter la sauvegarde des préférences au niveau des FILTRES.

    private static SharedPreferences mZ_sP;
    private static SharedPreferences f_sP;


    public static ArrayList<MonitoredZone> getMonitoredZones() {
        return monitoredZones;
    }
    public static void setMonitoredZones(ArrayList<MonitoredZone> monitoredZones) {
        Preferences.monitoredZones = monitoredZones;
    }


    /**
     * Initialise les paramètres des préférences lorsque l'application est lancée.
     * Monitored Zones, Filters, etc.
     *
     * @param context Home
     */
    public static void setUpPrefs(Context context, MapDisplay myMap) {

        // Load saved Monitored Zones
        mZ_sP = context.getSharedPreferences(DIR_SP_MONITORED_ZONES, Context.MODE_PRIVATE);
        String masterMZ = mZ_sP.getString(KEY_SP_MZS_LIST, "");

        if(masterMZ.length() != 0) {
            String[] allMZ = masterMZ.split(MZS_SEPARATOR);

            for(String str : allMZ) {
                String temp[] = str.split(MZ_COORD_SEPARATOR);
                double d0 = Double.parseDouble(temp[0]);
                double d1 = Double.parseDouble(temp[1]);
                double d2 = Double.parseDouble(temp[2]);
                double d3 = Double.parseDouble(temp[3]);

                MapDisplay.ajouterPolygon(myMap, d0, d1, d2, d3);
                monitoredZones.add(new MonitoredZone(d0, d1, d2, d3));
            }

            nbrZones = monitoredZones.size();
        }

        // Load filter preferences
        f_sP = context.getSharedPreferences(DIR_SP_FILTERS, Context.MODE_PRIVATE);
        String masterFilters = f_sP.getString(KEY_SP_FILTERS_LIST, "");

        if(masterFilters.length() != 0) {

            MainActivity.mainActivity.findViewById(android.R.id.content);
            MainActivity.mainActivity.findViewById(android.R.id.content);
            MainActivity.mainActivity.findViewById(android.R.id.content);
            MainActivity.mainActivity.findViewById(android.R.id.content);
            MainActivity.mainActivity.findViewById(android.R.id.content);
            MainActivity.mainActivity.findViewById(android.R.id.content);
            MainActivity.mainActivity.findViewById(android.R.id.content);

            /*
            myMap.feuFilter
            myMap.eauFilter
            myMap.terrainFilter
            myMap.meteoFilter
            myMap.showMonitoredZones
            myMap.historiqueFilter
            myMap.historiqueLoaded
            myMap.showUserPins
             */

            for(int i = 0; i < masterFilters.length(); i++) {
                int value = Integer.parseInt(masterFilters.charAt(i) + "");

                if(value == 0) {

                }
                if(value == 1) {

                }
            }
        } else {
            // If first time launching app: initialize default filter string
            SharedPreferences.Editor editor = f_sP.edit();
            editor.putString(KEY_SP_FILTERS_LIST, "");
            editor.apply();
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

        String masterMZ = mZ_sP.getString(KEY_SP_MZS_LIST, "");
        masterMZ += newSP;

        // Removing a possible first entry's erroneous character
        if((masterMZ.charAt(0)+"").equals(MZS_SEPARATOR))
            masterMZ = masterMZ.substring(1);

        // Saving new MonitoredZones list in SP
        SharedPreferences.Editor editor = mZ_sP.edit();
        editor.putString(KEY_SP_MZS_LIST, masterMZ);
        editor.apply();

        nbrZones++;
        monitoredZones.add(newMZ);
    }

    public static void clearPreferences() {
        clearMZSP();
        clearFSP();
    }

    public static void clearMZSP() {
        SharedPreferences.Editor editor = mZ_sP.edit();
        editor.putString(KEY_SP_MZS_LIST, "");
        editor.apply();
    }

    public static void clearFSP() {
        SharedPreferences.Editor editor = f_sP.edit();
        editor.putString(KEY_SP_FILTERS_LIST, "");
        editor.apply();
    }
}