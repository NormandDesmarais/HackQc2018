package com.example.payne.simpletestapp;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.Polygon;
import org.osmdroid.views.overlay.infowindow.BasicInfoWindow;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

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
    private Context ctx;
    public static boolean currentlyPlacingPin = false;
    public static final BoundingBox MONTREAL_BOUNDING_BOX = new BoundingBox(63,40,58,84);

    public ArrayList<Alerte> terrainAlerts = new ArrayList<>();
    public ArrayList<Alerte> feuAlerts = new ArrayList<>();
    public ArrayList<Alerte> eauAlerts = new ArrayList<>();
    public ArrayList<Alerte> meteoAlerts = new ArrayList<>();

    public Boolean terrainFilter = false;
    public Boolean feuFilter = false;
    public Boolean eauFilter = false;
    public Boolean meteoFilter = false;


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
                    pin.setIcon(ctx.getResources().getDrawable(R.drawable.pin_goutte));
                    break;
                case "seisme":
                    pin.setIcon(ctx.getResources().getDrawable(R.drawable.pin_seisme));
                    break;
                case "vent":
                    pin.setIcon(ctx.getResources().getDrawable(R.drawable.pin_vent));
                    break;
                case "feu":
                    pin.setIcon(ctx.getResources().getDrawable(R.drawable.pin_feu));
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

        GeoPoint pos = new GeoPoint(alerte.getLattitude(), alerte.getLongitude());
        Marker pin = new Marker(map);
        pin.setPosition(pos);
        pin.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

        pin.setTitle(alerte.nom);

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

        // View layout = MainActivity.mainActivity.findViewById(R.id.pop_up);
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
                default:
                    this.meteoAlerts.add(new Alerte(alerte));
            }
        }

    }

    public void displayLists() {

        if (feuFilter) this.drawAlertPins(feuAlerts, ctx.getResources().getDrawable(R.drawable.pin_feu));
        if (eauFilter) this.drawAlertPins(eauAlerts, ctx.getResources().getDrawable(R.drawable.pin_goutte));
        if (terrainFilter) this.drawAlertPins(terrainAlerts, ctx.getResources().getDrawable(R.drawable.pin_seisme));
        if (meteoFilter) this.drawAlertPins(meteoAlerts, ctx.getResources().getDrawable(R.drawable.pin_vent));

    }

    public void refreshPins(){

        this.removeAll(MainActivity.mainActivity.findViewById(android.R.id.content),
                       MainActivity.mapEventsOverlay);
        this.displayLists();
        this.map.invalidate();

    }


}
