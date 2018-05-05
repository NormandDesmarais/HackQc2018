package com.example.payne.simpletestapp;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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
    static private Context ctx;
    public static boolean currentlyPlacingPin = false;
    public static final BoundingBox MONTREAL_BOUNDING_BOX = new BoundingBox(63,40,58,84);

    public ArrayList<Alerte> terrainAlerts = new ArrayList<>();
    public ArrayList<Alerte> feuAlerts = new ArrayList<>();
    public ArrayList<Alerte> eauAlerts = new ArrayList<>();
    public ArrayList<Alerte> meteoAlerts = new ArrayList<>();

    public static boolean terrainFilter = true;
    public static boolean feuFilter = true;
    public static boolean eauFilter = true;
    public static boolean meteoFilter = true;

    public static Drawable eauIcon = MainActivity.mainActivity.getResources().getDrawable(R.drawable.pin_goutte);
    public static Drawable feuIcon = MainActivity.mainActivity.getResources().getDrawable(R.drawable.pin_feu);
    public static Drawable terrainIcon = MainActivity.mainActivity.getResources().getDrawable(R.drawable.pin_seisme);
    public static Drawable meteoIcon = MainActivity.mainActivity.getResources().getDrawable(R.drawable.pin_vent);


    public MapDisplay(MapView map, Context ctx) {
        this.map = map;
        this.ctx = ctx;
    }


    /**
     * North, south, east, west
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
        List<GeoPoint> geoPoints = new ArrayList<>();

        //add your points here
        geoPoints.add(new GeoPoint(corners[0], corners[2])); // North East
        geoPoints.add(new GeoPoint(corners[0], corners[3])); // North West
        geoPoints.add(new GeoPoint(corners[1], corners[3])); // South West
        geoPoints.add(new GeoPoint(corners[1], corners[2])); // South East

        Polygon polygon = new Polygon(this.map);    //see note below
        polygon.setFillColor(Color.argb(75, 255, 0, 0));
        geoPoints.add(geoPoints.get(0));    //forces the loop to close
        polygon.setPoints(geoPoints);

        // style
        polygon.setStrokeColor(Color.argb(75, 255, 0, 0));
        polygon.setStrokeWidth(0);

        // infos
        polygon.setTitle("TITLE : A sample polygon");
        polygon.setSnippet("A Snippet");
        polygon.setSubDescription("A Subdescription");

        this.map.getOverlayManager().add(polygon);
        this.map.invalidate();
    }

    public void removeAll(View view, MapEventsOverlay mapEventsOverlay) {

        Toast.makeText(view.getContext(), this.map.getOverlayManager().overlays().size() - 1 + " items removed", Toast.LENGTH_SHORT).show();
        this.map.getOverlayManager().removeAll(this.map.getOverlays());

        map.getOverlays().add(0, mapEventsOverlay);

        this.map.invalidate();

    }

    public void addPin(GeoPoint pos) {

        if (!currentlyPlacingPin) {
            currentlyPlacingPin = !currentlyPlacingPin;

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


    public void addPin(GeoPoint pos, String type) {

        if (!currentlyPlacingPin) {
            currentlyPlacingPin = !currentlyPlacingPin;

            Marker pin = new Marker(map);
            pin.setPosition(pos);
            pin.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

            pin.setTitle("TITLE : A pin");
            pin.setSubDescription("A subdescripton");
            pin.setSnippet("A snippet");

            switch (type) {
                case "eau":
                    pin.setIcon(eauIcon);
                    break;
                case "seisme":
                    pin.setIcon(terrainIcon);
                    break;
                case "vent":
                    pin.setIcon(meteoIcon);
                    break;
                case "feu":
                    pin.setIcon(feuIcon);
                    break;
                default:
                    break;
            }

            pin.setTitle("TITLE : A pin");
            pin.setSubDescription("A subdescripton");
            pin.setSnippet("A snippet");

            map.getOverlays().add(pin);
            this.map.invalidate();

            MainActivity.lastPlacedPin = pin;
            showPopUp();
        }

    }

    public void drawCircleAtCenter(int radius, int shade) {

        for (int i = 1; i <= shade + 1; i++) {

            ArrayList<GeoPoint> circlePoints = new ArrayList<>();

            for (float f = 0; f < 360; f += 1) {
                circlePoints.add(new GeoPoint(
                        this.getCenter().getLatitude(), this.getCenter().getLongitude())
                        .destinationPoint(i * (radius / shade), f));
            }

            Polygon circle = new Polygon(this.map);    //see note below
            circlePoints.add(circlePoints.get(0));    //forces the loop to close
            circle.setPoints(circlePoints);

            // define style
            circle.setStrokeWidth(0);
            circle.setStrokeColor(Color.argb(25, 10, 255, 10));
            circle.setFillColor(Color.argb(75, 10, 255, 10));


            map.getOverlayManager().add(circle);

        }

        map.invalidate();

    }


    public void drawPolygon(JSONObject JsonPoints) {

        Log.w("method :", "drawPolygon");

    }

    public void addAlertPin(Alerte alerte, Drawable icon) {

        GeoPoint pos = new GeoPoint(alerte.getLatitude(), alerte.getLongitude());
        Marker pin = new Marker(map);
        pin.setPosition(pos);
        pin.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

        pin.setTitle(alerte.nom);
        pin.setIcon(icon);

        String description = alerte.description + " " + alerte.type;
        pin.setSubDescription(description);

        String snippet = alerte.dateDeMiseAJour + " " + alerte.urgence;
        pin.setSnippet(snippet);

        map.getOverlays().add(pin);
        Log.w("NEW_PIN", pin.getPosition().toString());
        this.map.invalidate();
    }

    public void drawAlertPins(ArrayList<Alerte> alertes, Drawable icon){

        for (Alerte alerte : alertes) {
            this.addAlertPin(alerte, icon);
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

    public void updateLists(JSONObject allPins) throws Exception {

        JSONArray alertes = allPins.getJSONArray("alertes");

        for (int i = 0; i < alertes.length(); i++) {

            JSONObject alerte = alertes.getJSONObject(i);

            switch (alerte.getString("type")) {
                case "feu":
                    this.feuAlerts.add(new Alerte(alerte));
                    break;
                case "eau":
                    this.eauAlerts.add(new Alerte(alerte));
                    break;
                case "meteo":
                    this.meteoAlerts.add(new Alerte(alerte));
                    break;
                case "terrain":
                    this.terrainAlerts.add(new Alerte(alerte));
                    break;
                default:
                    this.meteoAlerts.add(new Alerte(alerte));
                    break;
            }
        }

    }

    public void displayLists() {

        if(feuFilter) this.drawAlertPins(feuAlerts, feuIcon);
        if(eauFilter) this.drawAlertPins(eauAlerts, eauIcon);
        if(terrainFilter) this.drawAlertPins(terrainAlerts, terrainIcon);
        if(meteoFilter) this.drawAlertPins(meteoAlerts, meteoIcon);

    }

    public void refreshPins(){

        this.removeAll(MainActivity.mainActivity.findViewById(android.R.id.content),
                       MainActivity.mapEventsOverlay);
        this.displayLists();
        this.map.invalidate();
    }

}
