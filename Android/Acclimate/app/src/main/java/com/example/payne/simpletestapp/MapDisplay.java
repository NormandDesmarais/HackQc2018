package com.example.payne.simpletestapp;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONObject;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polygon;

import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper pour le map de OSMdroid
 * <p>
 * Created by Utilisateur on 2018-05-03.
 */

public class MapDisplay {

    public MapView map;
    private double[] lastTouch = {0, 0};
    public static boolean currentlyPlacingPin = false;
    public static String last_type_put_down;
    public static final BoundingBox QUEBEC_BOUNDING_BOX = new BoundingBox(63,-58,40,-84);

    // Ensembles de Pins (Markers) pour chaque type de filtre possible
    public ArrayList<Marker> terrainAlerts = new ArrayList<>();
    public ArrayList<Marker> feuAlerts = new ArrayList<>();
    public ArrayList<Marker> eauAlerts = new ArrayList<>();
    public ArrayList<Marker> meteoAlerts = new ArrayList<>();

    public ArrayList<Marker> userPins = new ArrayList<>();
    public ArrayList<Marker> historique = new ArrayList<>(); // TODO: remove?

    // Rectangles colorés (zones surveillées)
    public ArrayList<Polygon> monitoredZones = new ArrayList<>();

    // Filtres (préférences), dans l'ordre du menu
    public static boolean feuFilter = true;
    public static boolean eauFilter = true;
    public static boolean terrainFilter = true;
    public static boolean meteoFilter = true;
    public static boolean showMonitoredZones = true;
    public static boolean showUserPins = true;
    public static boolean historiqueFilter = false;
    public static boolean historiqueLoaded = false;

    // Official pins
    public static Drawable eauIcon;
    public static Drawable feuIcon;
    public static Drawable terrainIcon;
    public static Drawable meteoIcon;
    // User pins
    public static Drawable userEauIcon;
    public static Drawable userFeuIcon;
    public static Drawable userTerrainIcon;
    public static Drawable userMeteoIcon;


    public MapDisplay(MapView map) {
        this.map = map;

        // Setting up the image of the Official Pins
        eauIcon = MainActivity.mainActivity.getResources().getDrawable(R.drawable.pin_goutte);
        feuIcon = MainActivity.mainActivity.getResources().getDrawable(R.drawable.pin_feu);
        terrainIcon = MainActivity.mainActivity.getResources().getDrawable(R.drawable.pin_seisme);
        meteoIcon = MainActivity.mainActivity.getResources().getDrawable(R.drawable.pin_vent);

        // User Pins
        userEauIcon = MainActivity.mainActivity.getResources().getDrawable(R.drawable.pin_user_water);
        userFeuIcon = MainActivity.mainActivity.getResources().getDrawable(R.drawable.pin_user_fire);
        userTerrainIcon = MainActivity.mainActivity.getResources().getDrawable(R.drawable.pin_user_earth);
        userMeteoIcon = MainActivity.mainActivity.getResources().getDrawable(R.drawable.pin_user_wind);
    }


    /**
     * North, south, east, west
     *
     * ex:
     * TOP-LEFT COORD = {result[0], result[3]}
     * BOT-RIGHT COORD = {result[1], result[2]}
     *
     * @return
     */
    public double[] getBoundingBox() {
        BoundingBox current = map.getBoundingBox();
        double[] result = new double[4];

        result[0] = current.getLatNorth();
        result[1] = current.getLatSouth();
        result[2] = current.getLonEast();
        result[3] = current.getLonWest();

        return result;
    }

    public void highlightCurrent(View view) {

        double[] corners = getBoundingBox();

        ajouterPolygon(this, corners[0], corners[1], corners[2], corners[3]);

        MainActivity.manager.addUserNotification(map.getBoundingBox());
    }

    /**
     * Pour ajouter une zone colorée là où mentionné.
     * Utilisé pour signifier qu'une zone est surveillée.
     */
    public static void ajouterPolygon(MapDisplay mapD, double north, double south, double east, double west) {

        List<GeoPoint> geoPoints = new ArrayList<>();

        //add your points here
        geoPoints.add(new GeoPoint(north, east));
        geoPoints.add(new GeoPoint(north, west));
        geoPoints.add(new GeoPoint(south, west));
        geoPoints.add(new GeoPoint(south, east));

        Polygon polygon = new Polygon(mapD.map);    //see note below
        polygon.setFillColor(Color.argb(75, 255, 0, 0));
        geoPoints.add(geoPoints.get(0));    //forces the loop to close
        polygon.setPoints(geoPoints);

        // style
        polygon.setStrokeColor(Color.argb(75, 255, 100, 0));
        polygon.setStrokeWidth(0);

        // infos
        polygon.setTitle("Zone d'alerte");
        polygon.setSnippet("Vous recevrez des notifications lorsqu'une nouvelle alerte " +
                "sera détectée à l'intérieur de cette zone.");
        polygon.setSubDescription("Pour vous désabonner à cette alerte, aller dans votre compte client.");

        mapD.monitoredZones.add(polygon);
    }

    public void removeAll(View view, MapEventsOverlay mapEventsOverlay) {

        this.map.getOverlayManager().removeAll(this.map.getOverlays());

        map.getOverlays().add(0, mapEventsOverlay);

        this.map.invalidate();

    }

    /**
     * Create a single temporary default pin and puts it on the map.
     * Used for User input on the phone.
     *
     * @param   pos     position fr the defaul pin
     */
    public void addUserPin(GeoPoint pos) {

        if (!currentlyPlacingPin) {
            currentlyPlacingPin = false;

            Marker pin = new Marker(map);
            pin.setPosition(pos);
            pin.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

            pin.setTitle("TITLE : A pin");
            pin.setSubDescription("A subdescripton");
            pin.setSnippet("A snippet");

            map.getOverlays().add(pin);
            this.map.invalidate();

            MainActivity.lastPlacedPin = pin;
            showPopUp();
        }
    }

    /**
     * create a pin
     *
     * @param alerte
     * @param icon
     */
    public void addUserAlertPin(Alerte alerte, Drawable icon) {

        if (!showUserPins){
            showUserPins = !showUserPins;

            MainActivity.menu.findItem(R.id.cB_users).setChecked(true);
        }

        GeoPoint pos = new GeoPoint(alerte.getLatitude(), alerte.getLongitude());
        Marker pin = new Marker(map);
        pin.setPosition(pos);
        pin.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

        pin.setIcon(icon);

        pin.setTitle("Type : " + alerte.type + "\n" + "Catégorie : " + alerte.nom);

        String description = alerte.dateDeMiseAJour + " ";
        pin.setSubDescription(description);

        String snippet = alerte.description + " " + alerte.certitude;
        pin.setSnippet(snippet);

        this.userPins.add(pin);
        this.refresh();
    }

    /**
     *
     *
     * @param alerte
     * @param icon
     * @return
     */
    public Marker createAlertPin(Alerte alerte, Drawable icon) {

        GeoPoint pos = new GeoPoint(alerte.getLatitude(), alerte.getLongitude());
        Marker pin = new Marker(map);
        pin.setPosition(pos);
        pin.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        pin.setIcon(icon);


        if (alerte.description.equals("alerte usager")) {

            pin.setTitle("Type : " + alerte.type);

            String snippet = "confiance : " + alerte.certitude;
            pin.setSnippet(snippet);

            String description =  "mis à jour : " + alerte.dateDeMiseAJour;
            pin.setSubDescription(description);

        } else {

            pin.setTitle("Type : " + alerte.type + "\nCatégorie : " + alerte.nom);

            String snippet = alerte.description;
            pin.setSnippet(snippet);

            String description = alerte.source + " " + alerte.dateDeMiseAJour;
            pin.setSubDescription(description);
        }

        return pin;
    }


    public void drawAlertPins(ArrayList<Marker> markers, Drawable icon){

        for (Marker m : markers) {
            map.getOverlayManager().add(m);
        }
    }

    public GeoPoint getCenter() {
        return (GeoPoint) this.map.getMapCenter();
    }

    public void setLastTouch(double x, double y) {
        this.lastTouch[0] = x;
        this.lastTouch[1] = y;
    }

    public double[] getLastTouch() {
        return this.lastTouch;
    }

    /**
     * Pour montrer le PopUp pour confirmer le type d'alerte.
     */
    public void showPopUp() {
        MainActivity.mainActivity.findViewById(R.id.pop_up).setVisibility(View.VISIBLE);
    }

    /**
     * Met-à jour les listes
     *
     * @param serverPins
     * @param userPins
     * @throws Exception
     */
    public void updateLists(JSONObject serverPins, JSONObject userPins) throws Exception {

        JSONArray allServerAlerts = serverPins.getJSONArray("alertes");
        JSONArray allUserAlerts = userPins.getJSONArray("alertes");

        for (int i = 0; i < allServerAlerts.length(); i++) {

            JSONObject serverAlert = allServerAlerts.getJSONObject(i).getJSONObject("alerte");

            switch (serverAlert.getString("type")) {

                case "Suivi des cours d'eau":
                case "Inondation":
                    Alerte tmp1 = new Alerte(serverAlert);
                    tmp1.type = "Eau";
                    this.eauAlerts.add(createAlertPin(tmp1, eauIcon));
                    break;

                case "Vent":
                    Alerte tmp2 = new Alerte(serverAlert);
                    tmp2.type = "Meteo";
                    this.meteoAlerts.add(createAlertPin(tmp2, meteoIcon));
                    break;

                case "Pluie":
                    Alerte tmp3 = new Alerte(serverAlert);
                    tmp3.type = "Meteo";
                    this.meteoAlerts.add(createAlertPin(tmp3, meteoIcon));
                    break;

                case "feu":
                case "Feu de forêt":
                case "Feu":
                    this.feuAlerts.add(createAlertPin(new Alerte(serverAlert), feuIcon));
                    break;

                case "eau":
                case "Eau":
                    this.eauAlerts.add(createAlertPin(new Alerte(serverAlert), eauIcon));
                    break;

                case "Tornade": // Parce que le "terrainIcon" ressemble à une tornade.. ?
                case "terrain":
                case "Terrain":
                    this.terrainAlerts.add(createAlertPin(new Alerte(serverAlert), terrainIcon));
                    break;

                default:
                case "meteo":
                case "Meteo":
                    this.meteoAlerts.add(createAlertPin(new Alerte(serverAlert), meteoIcon));
                    break;
            }
        }

        for (int i = 0; i < allUserAlerts.length(); i++){

            JSONObject userAlert = allUserAlerts.getJSONObject(i).getJSONObject("alerte");

            Drawable currentIcon;

            switch (userAlert.getString("type")){
                case "Eau" : currentIcon = userEauIcon; break;
                case "Feu" : currentIcon = userFeuIcon; break;
                case "Meteo" : currentIcon = userMeteoIcon; break;
                case "Terrain" : currentIcon = userTerrainIcon; break;
                default: currentIcon = userMeteoIcon;
            }

            this.userPins.add(createAlertPin(new Alerte(userAlert), currentIcon));
        }

        this.map.invalidate();
    }

    public void redrawScreen() {

        if(feuFilter) this.drawAlertPins(feuAlerts, feuIcon);
        if(eauFilter) this.drawAlertPins(eauAlerts, eauIcon);
        if(terrainFilter) this.drawAlertPins(terrainAlerts, terrainIcon);
        if(meteoFilter) this.drawAlertPins(meteoAlerts, meteoIcon);


        if (showMonitoredZones){
            for (Polygon p : monitoredZones){
                this.map.getOverlayManager().add(p);
            }
        }

        if (showUserPins) {
            for (Marker m : userPins) {
                map.getOverlayManager().add(m);
            }
        }

        if (historiqueFilter) {
            for (Marker h : userPins) {
                map.getOverlayManager().add(h);
            }
        }

        this.map.invalidate();
    }

    public void refresh(){
        this.removeAll(MainActivity.mainActivity.findViewById(android.R.id.content),
                MainActivity.mapEventsOverlay);
        this.redrawScreen();
    }
}
