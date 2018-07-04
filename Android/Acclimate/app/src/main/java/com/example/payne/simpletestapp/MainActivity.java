package com.example.payne.simpletestapp;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

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


/**
 * Classe de la carte.
 */
public class MainActivity extends AppCompatActivity implements MapEventsReceiver {

    // positionnement initial au lancement de la carte
    public static final double[] MONTREAL_COORD = {45.5161, -73.6568};

    MapView map = null;
    public MapDisplay myMap;
    public static MapEventsOverlay mapEventsOverlay;
    public static MainActivity mainActivity;
    public static Marker lastPlacedPin = null;
    public static Manager manager;
    public static Menu menu;
    public static Marker pin_on_focus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //handle permissions first, before map is created. not depicted here
        final Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        mainActivity = this;

        // Inflate and create the map
        setContentView(R.layout.activity_main);

        // Populating the Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        /*
        TODO: GPS
        https://developer.android.com/guide/topics/location/strategies
        https://github.com/miskoajkula/Gps_location
         */

        map = findViewById(R.id.map);
        myMap = new MapDisplay(map);
        map.setTileSource(TileSourceFactory.MAPNIK);

        // set zoom control and multi-touch gesture
        map.setBuiltInZoomControls(false);
        map.setMultiTouchControls(true);

        // default initial value
        IMapController mapController = map.getController();
        mapController.setZoom(12);
        GeoPoint startPoint = new GeoPoint(MONTREAL_COORD[0], MONTREAL_COORD[1]);
        mapController.setCenter(startPoint);

        // setup app backend
        manager = new Manager(this, myMap);


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

                /* TODO: GÉRER LE "YES" DU DIALOG
                Which "temporary pin" are we removing?
                Do we need to set "currentlyPlacingPin to false?
                 */
                // Remove temporary Pin
                map.getOverlays().remove(lastPlacedPin);
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
        findViewById(R.id.wind_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                placingPin(map, myMap, "Meteo", MapDisplay.meteoIcon);
            }
        });
        findViewById(R.id.water_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                placingPin(map, myMap, "Eau", MapDisplay.eauIcon);
            }
        });
        findViewById(R.id.fire_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                placingPin(map, myMap, "Feu", MapDisplay.feuIcon);
            }
        });
        findViewById(R.id.earth_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                placingPin(map, myMap, "Terrain", MapDisplay.terrainIcon);
            }
        });


        // Cancel button
        findViewById(R.id.cancel_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Remove canceled Pin
                map.getOverlays().remove(lastPlacedPin);
                map.invalidate();

                // Hide PopUp
                MainActivity.mainActivity.findViewById(R.id.pop_up).setVisibility(View.GONE);
                MapDisplay.currentlyPlacingPin = false;
            }
        });


        mapEventsOverlay = new MapEventsOverlay(this, this);
        map.getOverlays().add(0, mapEventsOverlay);
    }

    @Override
    public boolean singleTapConfirmedHelper(GeoPoint p) {
        InfoWindow.closeAllInfoWindowsOn(map);
        return false;
    }

    @Override
    public boolean longPressHelper(GeoPoint p) {
        // TODO: Test what happens if we long-press in other components than the map? (dialogs, menu, ...)
        myMap.addUserPin(p);
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MainActivity.menu = menu;

        // Loading Preferences stored in the phone (or initializing them)
        Preferences.setUpPrefs(this, myMap);
        myMap.refresh();

        //Setting up Search bar
        MenuItem ourSearchItem = menu.findItem(R.id.action_search);
        SearchView sv = (SearchView) ourSearchItem.getActionView();

        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // To get the Bounding Box that surrounds the researched query text
                BoundingBox boundingBox = JSONWrapper.googleBoundingBox(query);

                if (boundingBox != null)
                    map.zoomToBoundingBox(boundingBox, false);

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

        //noinspection SimplifiableIfStatement
        switch (id) {
            /*
            https://github.com/sophiesavoie/hackathon/commit/1a4632f48d31911c1c1e4062f57e756593e1c3b7

            case (R.id.action_settings):
                Toast.makeText(this, "Who cares...", Toast.LENGTH_SHORT).show();

                break;

            case (R.id.posBtn):
                BoundingBox current = map.getBoundingBox();

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
                IGeoPoint center = map.getMapCenter();
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
                Preferences.addCurrentBoundingBoxToMZSP(myMap);
                //myMap.highlightCurrent(findViewById(android.R.id.content));
                myMap.refresh();
                break;

            /*
            case (R.id.removeAll):
                myMap.removeAll(findViewById(android.R.id.content), mapEventsOverlay);
                break;

            case (R.id.addUserPin):
                myMap.addUserPin(myMap.getCenter(), "seisme");
                break;

            case (R.id.circleBtn):
                myMap.drawCircleAtCenter(1000, 5);
                break;
            */

            case (R.id.cB_histo):
                // TODO: Remove for phones? Or make algo better.

                if (!MapDisplay.historiqueLoaded){

                    Toast.makeText(mainActivity, "Chargement de l'historique. Cette action peut prendre un certain temps", Toast.LENGTH_SHORT).show();

                    manager.getHistorique();
                    MapDisplay.historiqueFilter = true;
                    MapDisplay.historiqueLoaded = true;

                } else {
                    MapDisplay.historiqueFilter = toggleFilterCB(item);
                    myMap.refresh();
                }
                break;

            // Setting up the Filter Check-Boxes (cb)
            case (R.id.cB_fire): // TODO: FINISH
                MapDisplay.feuFilter = Preferences.toggleFilterPref(item, 0);
                myMap.refresh(); break;
            case (R.id.cB_water):
                MapDisplay.eauFilter = Preferences.toggleFilterPref(item, 1);
                myMap.refresh(); break;
            case (R.id.cB_terrain):
                MapDisplay.terrainFilter = Preferences.toggleFilterPref(item, 2);
                myMap.refresh(); break;
            case (R.id.cB_meteo):
                MapDisplay.meteoFilter = Preferences.toggleFilterPref(item, 3);
                myMap.refresh(); break;
            case (R.id.cB_zones):
                MapDisplay.showMonitoredZones = Preferences.toggleFilterPref(item, 4);
                myMap.refresh(); break;
            case (R.id.cB_users):
                MapDisplay.showUserPins = Preferences.toggleFilterPref(item, 5);
                myMap.refresh(); break;

            case (R.id.profileBtn):
                // TODO: Launch FIREBASE Activity here
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
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    public void onPause() {
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }

    /*
    private static section : pour le Refactor
     */

    // Pour du Refactor (reducing code duplication)
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