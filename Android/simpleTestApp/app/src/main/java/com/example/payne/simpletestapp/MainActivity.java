package com.example.payne.simpletestapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.BoundingBox;


public class MainActivity extends AppCompatActivity implements MapEventsReceiver {

    public static final double[] MONTREAL_COORD = {45.5161, -73.6568};

    MapView map = null;
    public MapDisplay myMap = null;
    public MapEventsOverlay mapEventsOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // setup app backend
        try{
            Manager manager = new Manager(this);
        } catch (Exception e){
            e.printStackTrace();
        }

        //handle permissions first, before map is created. not depicted here

/* REMOVED BECAUSE BUGGY AND USELESS! "Configuration" is for resuming saved preferences (or something like that...)

        //load/initialize the osmdroid configuration, this can be done

        final Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        //setting this before the layout is inflated is a good idea
        //it 'should' ensure that the map has a writable location for the map cache, even without permissions
        //if no tiles are displayed, you can try overriding the cache path using Configuration.getInstance().setCachePath
        //see also StorageUtils
        //note, the load method also sets the HTTP User Agent to your application's package name, abusing osm's tile
        //servers will get you banned based on this string
*/

        //inflate and create the map
        setContentView(R.layout.activity_main);

        //creating the Toolbar?
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        /*
        TODO: GPS
        https://developer.android.com/guide/topics/location/strategies
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


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        /* TODO : Add "SEARCH", "FILTERS", and "HISTORIQUE"
        https://developer.android.com/training/appbar/action-views */


        mapEventsOverlay = new MapEventsOverlay(this, this);
        map.getOverlays().add(0, mapEventsOverlay);
    }

    @Override
    public boolean singleTapConfirmedHelper(GeoPoint p) {
        Toast.makeText(this, "tapped :)", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public boolean longPressHelper(GeoPoint p) {
        myMap.addPin(p);
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        //Setting up Search bar
        MenuItem ourSearchItem = menu.findItem(R.id.action_search);
        SearchView sv = (SearchView) ourSearchItem.getActionView();
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                Toast.makeText(MainActivity.this, query, Toast.LENGTH_SHORT).show();

                /* GOOGLE API (GeoCoding):
                https://developers.google.com/maps/documentation/geocoding/intro
                 */

                String tmp = "https://maps.googleapis.com/maps/api/geocode/json?address=";
                /* TODO: Add QUEBEC BOUNDARIES:
                "https://maps.googleapis.com/maps/api/geocode/json?bounds=34.172684,-118.604794|34.236144,-118.500938&address="
                OR
                "https://maps.googleapis.com/maps/api/geocode/json?region=qc&address="
                */
                ServerConnection connection = new ServerConnection(tmp);

                try {
                    String tmpJSON = connection.getRequest(query);

                    /*
                    JSONObject testJSON = JSONWrapper.createJSON(tmpJSON);
                    JSONObject secJSON = (JSONObject) testJSON.getJSONArray("results").get(0);
                    Double testJSON2 = secJSON.getJSONObject("geometry").getJSONObject("viewport").getDouble("lat");
                    */



                    /*
                        "viewport" : {
                           "northeast" : {
                              "lat" : 45.7058381,    NORTH
                              "lng" : -73.47426      EAST
                           },
                           "southwest" : {
                              "lat" : 45.410246,     SOUTH
                              "lng" : -73.986345     WEST
                           }
                        }
                     */


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

                    /* TODO: adjust
                    // deux fois plus grand vers la gauche
                    double horizOffset = (west-east)/2;
                    east = east - horizOffset;
                    west = west - (horizOffset);

                    // deux fois plus grand vers le bas
                    double vertOffset = (north-south)/2;
                    south = south - vertOffset;
                    */

                    BoundingBox boundingBox = new BoundingBox(north, east, south, west);

                    map.zoomToBoundingBox(boundingBox, true);

                    /*
                    Log.w("String", tmpJSON);
                    Log.w("JSON", testJSON.getJSONArray("results").get(0).toString());
                    Log.w("secJSON", testJSON2.toString());
                    */
                } catch (Exception e) {
                    e.printStackTrace();
                }

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
            case (R.id.action_settings):
                Toast.makeText(this, "Who fuckings cares...", Toast.LENGTH_SHORT).show();
                /* TODO: Try a Standard Request here
                https://hackqc.herokuapp.com/api/greeting?name=bob&annee=345&lastname=lolippop
                https://developer.android.com/training/volley/request
                https://stackoverflow.com/questions/3027066/how-to-send-a-json-object-over-request-with-android?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
                 */
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

            case (R.id.highlight):
                myMap.highlightCurrent(findViewById(android.R.id.content));
                break;

            case (R.id.removeAll):
                myMap.removeAll(findViewById(android.R.id.content), mapEventsOverlay);
                break;

            case (R.id.addPin):
                myMap.addPin(myMap.getCenter());
                break;

            case (R.id.circleBtn):
                myMap.drawCircleAtCenter(1000, 5);
                break;

            case (R.id.cB_fire):
                if (item.isChecked()) {
                    item.setChecked(false);
                    Toast.makeText(this, "FIRE unchecked", Toast.LENGTH_SHORT).show();
                } else {
                    item.setChecked(true);
                    Toast.makeText(this, "FIRE is checked", Toast.LENGTH_SHORT).show();
                }
                break;

            case (R.id.cB_water):
                if (item.isChecked()) {
                    item.setChecked(false);
                    Toast.makeText(this, "WATER unchecked", Toast.LENGTH_SHORT).show();
                } else {
                    item.setChecked(true);
                    Toast.makeText(this, "WATER is checked", Toast.LENGTH_SHORT).show();
                }
                break;

            case (R.id.extraBtn):
                Toast.makeText(this, "extraBtn clicked", Toast.LENGTH_SHORT).show();
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
}