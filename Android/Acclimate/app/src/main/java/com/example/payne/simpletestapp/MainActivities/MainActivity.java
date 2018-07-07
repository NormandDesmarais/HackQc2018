package com.example.payne.simpletestapp.MainActivities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.payne.simpletestapp.Authentification.LoginActivity;
import com.example.payne.simpletestapp.DeviceStorage.Preferences;
import com.example.payne.simpletestapp.DeviceStorage.SettingsActivity;
import com.example.payne.simpletestapp.JSONWrapper;
import com.example.payne.simpletestapp.Manager;
import com.example.payne.simpletestapp.Map.MapDisplay;
import com.example.payne.simpletestapp.Objects.Alerte;
import com.example.payne.simpletestapp.R;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

/*
    TODO: (JEREMI ?)
        - FireBase integration
        - Adapt "SettingsActivity"
        - Clicking on Pins -> Center screen with Pin at bottom (for long description InfoWindows)
        - Search precision (centering is off)
        - Notifications for added pins in Monitored Zones
        - Initial center of loaded mapView = Preference ? Final position ?
        - Confirmation email when people are registering
        - Refactor the Redraw in MapDisplay to use "FolderOverlay"
        - Add new icon drawing for HistoPins
 */

/**
 * Classe de la carte.
 */
public class MainActivity extends AppCompatActivity implements MapEventsReceiver {

    // positionnement initial au lancement de la carte
    public static final double[] MONTREAL_COORD = {45.5161, -73.6568};

    MapView mapView = null;
    public static MapDisplay myMapDisplay;
    public static MapEventsOverlay mapEventsOverlay;
    public static MainActivity mainActivity;
    public static Marker lastPlacedPin = null;
    public static Manager manager;
    public static Menu menu;
    public static Marker pin_on_focus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Permissions pour le GPS */
        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);

        // check if enabled and if not send user to the GSP settings
        // Better solution would be to display a dialog and suggesting to
        // go to the settings -- use "AlarmDialog"
        if (!enabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
        }


        //handle permissions first, before mapView is created. not depicted here
        final Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        mainActivity = this;

        // Inflate and create the mapView
        setContentView(R.layout.activity_main);

        // Populating the Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        mapView = findViewById(R.id.map);
        myMapDisplay = new MapDisplay(mapView);
        mapView.setTileSource(TileSourceFactory.MAPNIK);

        // set zoom control and multi-touch gesture
        mapView.setBuiltInZoomControls(false);
        mapView.setMultiTouchControls(true);

        // default initial centered position
        recenterMap(false, MONTREAL_COORD[0], MONTREAL_COORD[1]);

/* TODO: GPS recentering after having found position
        new Thread(new Runnable() {
            @Override
            public void run() {
                GeoPoint location = myMapDisplay.locationOverlay.getMyLocation();
                while(location == null) location = myMapDisplay.locationOverlay.getMyLocation();
                // TODO: Ajoutée un temps maximal (pour ne pas avoir un Thread qui roule à l'infini)

                // Une fois une position trouvée
                double lat = location.getLatitude();
                double lon = location.getLongitude();
                Log.w("Location GPS", "lat: " + lat + " long: " + lon);
                recenterMap(true, lat, lon);
            }
        }).start();
*/


        // setup app backend
        manager = new Manager(this, myMapDisplay);


        // Logo button
        findViewById(R.id.logo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Merci d'utiliser Acclimate :) ", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        /*
        Setting up Events for "Confirm Alert Dialog"
         */
        findViewById(R.id.confirm_no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.mainActivity.findViewById(R.id.confirm_dialog).setVisibility(View.GONE);
            }
        });
        findViewById(R.id.confirm_yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*
                TODO: GÉRER LE "YES" DU DIALOG
                Which "temporary pin" are we removing?
                Do we need to set "currentlyPlacingPin to false?
                 */
                // Remove temporary Pin
                mapView.getOverlays().remove(lastPlacedPin);
                String type="";
                if (pin_on_focus.getTitle().contains("Feu")) type = "Feu";
                if (pin_on_focus.getTitle().contains("Eau")) type = "Eau";
                if (pin_on_focus.getTitle().contains("Meteo")) type = "Meteo";
                if (pin_on_focus.getTitle().contains("Terrain")) type = "Terrain";

                String param =  "?type=" + type.toLowerCase() +
                        "&lat=" + pin_on_focus.getPosition().getLatitude() +
                        "&lng=" + pin_on_focus.getPosition().getLongitude();
                try{
                    manager.mainServer.getRequest("/putAlert", param);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Hide PopUp
                MainActivity.mainActivity.findViewById(R.id.confirm_dialog).setVisibility(View.GONE);
                MapDisplay.currentlyPlacingPin = false;

                // Pour ne pas pouvoir confirmer plusieurs fois le même Marker
                pin_on_focus.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker, MapView mapView) {
                        marker.showInfoWindow();
                        mapView.getController().animateTo(marker.getPosition());
                        return true;
                    }
                });
            }
        });

        /*
        Setting up Events for "New Alert Type" prompt dialog
         */
        final int[] views = new int[]{R.id.wind_btn, R.id.water_btn, R.id.fire_btn, R.id.earth_btn};
        final String[] types = new String[]{"Meteo", "Eau", "Feu", "Terrain"};
        final Drawable[] drawables = new Drawable[]{MapDisplay.userMeteoIcon, MapDisplay.userEauIcon, MapDisplay.userFeuIcon, MapDisplay.userTerrainIcon};
        for(int i = 0; i < views.length; i++) {
            final int tmp = i;
            findViewById(views[tmp]).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    placingPin(mapView, myMapDisplay, types[tmp], drawables[tmp]);
                }
            });
        }


        // Cancel button
        findViewById(R.id.cancel_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Remove canceled Pin
                mapView.getOverlays().remove(lastPlacedPin);
                mapView.invalidate();

                // Hide PopUp
                MainActivity.mainActivity.findViewById(R.id.pop_up).setVisibility(View.GONE);
                MapDisplay.currentlyPlacingPin = false;
            }
        });


        mapEventsOverlay = new MapEventsOverlay(this, this);
        mapView.getOverlays().add(0, mapEventsOverlay);
    }

    @Override
    public boolean singleTapConfirmedHelper(GeoPoint p) {
        InfoWindow.closeAllInfoWindowsOn(mapView);
        return false;
    }

    @Override
    public boolean longPressHelper(GeoPoint p) {
        // TODO: Test what happens if we long-press in other components than the mapView? (dialogs, menu, ...)
        myMapDisplay.addUserPin(p);
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MainActivity.menu = menu;

        // Loading Preferences stored in the phone (or initializing them)
        Preferences.setUpPrefs(this, myMapDisplay);
        myMapDisplay.refresh();

        //Setting up Search bar
        MenuItem ourSearchItem = menu.findItem(R.id.action_search);
        SearchView sv = (SearchView) ourSearchItem.getActionView();

        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // To get the Bounding Box that surrounds the researched query text
                BoundingBox boundingBox = JSONWrapper.googleBoundingBox(query);

                if (boundingBox != null)
                    mapView.zoomToBoundingBox(boundingBox, false);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            /*
            https://github.com/sophiesavoie/hackathon/commit/1a4632f48d31911c1c1e4062f57e756593e1c3b7

            case (R.id.action_settings):
                Toast.makeText(this, "Who cares...", Toast.LENGTH_SHORT).show();

                break;

            case (R.id.posBtn):
                BoundingBox current = mapView.getBoundingBox();

                double top = current.getLatNorth();
                double bottom = current.getLatSouth();
                double east = current.getLonEast();
                double west = current.getLonWest();

                String descr = "north : " + top + " \n" +
                        "south : " + bottom + " \n" +
                        "east :" + east + " \n" +
                        "west : " + west;

                Toast.makeText(this, descr, Toast.LENGTH_SHORT).show();
                break;

            case (R.id.centerBtn):
                IGeoPoint center = mapView.getMapCenter();
                Toast.makeText(this,
                        "Center\n Lat : " + center.getLatitude() +
                                "\nLong : " + center.getLongitude(),
                        Toast.LENGTH_SHORT).show();
            */

            case (R.id.highlight):
                manager.queryNewPins();
                break;

            case (R.id.maj):
                // TODO: Server Request for all Monitored Zones
                break;

            case (R.id.add):
                Preferences.addCurrentBoundingBoxToMZSP(myMapDisplay);
                myMapDisplay.refresh();
                break;

            case (R.id.clear_MZ):
                Preferences.clearMZSP();
                myMapDisplay.refresh();
                break;

            case (R.id.settings):
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
                break;

            /*
            case (R.id.removeAll):
                myMapDisplay.removeAll(findViewById(android.R.id.content), mapEventsOverlay);
                break;

            case (R.id.addUserPin):
                myMapDisplay.addUserPin(myMapDisplay.getCenter(), "seisme");
                break;

            case (R.id.circleBtn):
                myMapDisplay.drawCircleAtCenter(1000, 5);
                break;
            */

            case (R.id.cB_histo):
                // Does not save the filter selection as a Preference! (too laggy)
                if (!MapDisplay.historiqueLoaded){

                    Snackbar.make(mapView, "Cette opération peut prendre un certain temps.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    item.setEnabled(false);
                    final MenuItem tmp = item;

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            manager.getHistorique();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    myMapDisplay.refresh();
                                    tmp.setEnabled(true);
                                }
                            });
                        }
                    }).start();

                    MapDisplay.historiqueFilter = toggleFilterCB(tmp);
                    MapDisplay.historiqueLoaded = true;
                } else {
                    MapDisplay.historiqueFilter = toggleFilterCB(item);
                    myMapDisplay.refresh();
                }
                break;

            // Setting up the Filter Check-Boxes (cb)
            case (R.id.cB_fire):
                MapDisplay.feuFilter = Preferences.toggleFilterPref(item, 0);
                myMapDisplay.refresh(); break;
            case (R.id.cB_water):
                MapDisplay.eauFilter = Preferences.toggleFilterPref(item, 1);
                myMapDisplay.refresh(); break;
            case (R.id.cB_terrain):
                MapDisplay.terrainFilter = Preferences.toggleFilterPref(item, 2);
                myMapDisplay.refresh(); break;
            case (R.id.cB_meteo):
                MapDisplay.meteoFilter = Preferences.toggleFilterPref(item, 3);
                myMapDisplay.refresh(); break;
            case (R.id.cB_zones):
                MapDisplay.showMonitoredZones = Preferences.toggleFilterPref(item, 4);
                myMapDisplay.refresh(); break;
            case (R.id.cB_users):
                MapDisplay.showUserPins = Preferences.toggleFilterPref(item, 5);
                myMapDisplay.refresh(); break;

            case (R.id.profileBtn):
                // TODO: Launch FIREBASE Activity here
                Intent intent2 = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent2);
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onResume() {
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        mapView.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    public void onPause() {
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        mapView.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }




    /*
    private static section : pour le Refactor et autres...
     */

    private static void placingPin(MapView map, MapDisplay myMap, String type, Drawable icon) {
        // Remove temporary Pin
        map.getOverlays().remove(lastPlacedPin);

        // Hide PopUp
        MainActivity.mainActivity.findViewById(R.id.pop_up).setVisibility(View.GONE);
        MapDisplay.currentlyPlacingPin = false;

        // locally register alert
        Alerte alert = new Alerte(lastPlacedPin.getPosition().getLongitude(),
                lastPlacedPin.getPosition().getLatitude(),
                type);

        myMap.addUserAlertPin(alert, icon);
        manager.postAlert(alert);
    }

    /**
     * Pour toggle un CheckBox Item.
     *
     * @param item se doit d'être un CB.
     * @return TRUE si l'état final du CB est "checked".
     */
    private static boolean toggleFilterCB(MenuItem item) {
        item.setChecked(!item.isChecked());
        return item.isChecked();
    }

    /**
     * Pour centrer la map sur une coordonnée AVANT que le GPS ne sache où l'utilisateur est.
     * Lorsque le GPS sait où il est, on peut appeler cette méthode avec "foundGPS = true".
     * TODO: (si bool=true) Devrait alors centrer l'écran sur la position communiquée.
     *
     * @param foundGPS 'true' seulement si la requête vient avec les coordonnées GPS de localisation
     * @param lat
     * @param lon
     */
    private void recenterMap(boolean foundGPS, double lat, double lon) {

        final IMapController mapController = mapView.getController();
        final GeoPoint startPoint = new GeoPoint(lat, lon);

        Log.w("onUiThread", "bool: "+ foundGPS + " lat: " + lat + " lon: " + lon);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mapController.setCenter(startPoint);
            }
        });

        double zoomLvl;
        if(foundGPS) zoomLvl = 9;
        else zoomLvl = 7;
        final double tmp = zoomLvl;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mapController.setZoom(tmp);
            }
        });
    }
}
