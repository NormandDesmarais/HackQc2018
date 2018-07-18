package com.example.payne.simpletestapp.DeviceStorage;


import android.content.Context;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;

import com.example.payne.simpletestapp.MainActivities.MainActivity;
import com.example.payne.simpletestapp.Map.MapDisplay;
import com.example.payne.simpletestapp.Objects.MonitoredZone;
import com.example.payne.simpletestapp.R;

import java.util.ArrayList;

/*
    TODO:
        - Add LastKnownGPS Coord for centering immediately on the second loading of app
 */

/**
 * https://developer.android.com/training/data-storage/shared-preferences#java
 * https://stackoverflow.com/questions/2614719/how-do-i-get-the-sharedpreferences-from-a-preferenceactivity-in-android?rq=1
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

    private static int nbrZones = 0;
    private static ArrayList<MonitoredZone> monitoredZones = new ArrayList<>();

    public static final String DIR_SP_MONITORED_ZONES = "monitored";
    public static final String DIR_SP_FILTERS = "filters";

    public static final String KEY_SP_MZS_LIST = "spmzlist";
    public static final String MZS_SEPARATOR = "#";
    public static final String MZ_COORD_SEPARATOR = "&";

    public static final String KEY_SP_FILTERS_LIST = "flist";
    public static final String DEFAULT_FILTERS = "1111110";

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

        // Uniquement s'il existe déjà des données
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
        Menu menu = MainActivity.menu;

        if(masterFilters.length() != 0) {

            for(int i = 0; i < masterFilters.length(); i++) {
                int value = Integer.parseInt(masterFilters.charAt(i) + "");

                // Dans l'ordre du menu!
                switch(i) {
                    case 0: // Feu
                        MapDisplay.feuFilter = filterTypeBtn(value, menu.findItem(R.id.cB_fire));
                    case 1: // Eau
                        MapDisplay.eauFilter = filterTypeBtn(value, menu.findItem(R.id.cB_water));
                    case 2: // Terrain
                        MapDisplay.terrainFilter = filterTypeBtn(value, menu.findItem(R.id.cB_terrain));
                    case 3: // Météo
                        MapDisplay.meteoFilter = filterTypeBtn(value, menu.findItem(R.id.cB_meteo));
                    case 4: // MZ
                        MapDisplay.showMonitoredZones = filterTypeBtn(value, menu.findItem(R.id.cB_zones));
                    case 5: // UserPins
                        MapDisplay.showUserPins = filterTypeBtn(value, menu.findItem(R.id.cB_users));
                    case 6: // Histo
                        MapDisplay.historiqueFilter = filterTypeBtn(value, menu.findItem(R.id.cB_histo));
                }
            }
        } else {
            // If first time launching app: initialize default filter string
            SharedPreferences.Editor editor = f_sP.edit();
            editor.putString(KEY_SP_FILTERS_LIST, DEFAULT_FILTERS);
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

    /**
     * Pour changer la valeur en mémoire de la préférence de l'usager pour une CB-filtre.
     *
     * @param item se doit d'être une CB.
     * @param pos 1 = feu, 2 = eau, etc. ... en ordre du menu d'options.
     * @return TRUE si le CB est rendu CHECKED, FALSE sinon.
     */
    public static boolean toggleFilterPref(MenuItem item, int pos) {

        boolean bool = toggleFilterCB(item);

        SharedPreferences.Editor editor = f_sP.edit();
        String toUpdate = f_sP.getString(KEY_SP_FILTERS_LIST, "");
        String tmp = "";

        if(pos != 0)
            tmp = toUpdate.substring(0,pos);
        else // si position 0
            tmp = toUpdate.substring(0,pos);

        if(bool)
            tmp += "1";
        else
            tmp += "0";

        if(pos != toUpdate.length())
            tmp += toUpdate.substring(pos+1,toUpdate.length());

        editor.putString(KEY_SP_FILTERS_LIST, tmp);
        editor.apply();

        return bool;
    }

    public static void clearPreferences() {
        clearMZSP();
        resetFSP();
    }

    public static void clearMZSP() {
        SharedPreferences.Editor editor = mZ_sP.edit();
        editor.putString(KEY_SP_MZS_LIST, "");
        editor.apply();
        MainActivity.myMapDisplay.monitoredZones.clear();
    }

    public static void resetFSP() {
        SharedPreferences.Editor editor = f_sP.edit();
        editor.putString(KEY_SP_FILTERS_LIST, DEFAULT_FILTERS);
        editor.apply();
    }


    /*
    Section des méthodes privées
     */

    private static boolean filterTypeBtn(int bool, MenuItem item) {
        if(bool == 0) { // false
            item.setChecked(false);
            return false;
        } else { // true
            item.setChecked(true);
            return true;
        }
    }

    /**
     * Pour toggle le CheckBox Item.
     *
     * @param item se doit d'être un CB.
     * @return TRUE si l'état final du CB est "checked".
     */
    private static boolean toggleFilterCB(MenuItem item) {
        item.setChecked(!item.isChecked());
        return item.isChecked();
    }
}