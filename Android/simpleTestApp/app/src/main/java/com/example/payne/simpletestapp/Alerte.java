package com.example.payne.simpletestapp;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Alerte {

    private double lattitude;
    private double longitude;
    public String nom;
    public String source;
    public String territoire;
    public String certitude;
    public String severite;
    public String type;
    public String dateDeMiseAJour;
    public String idAlerte;
    public String urgence;
    public String description;

    /**
     * Create an Alert object from the a JSON file (GEOJSON format)
     *
     * @param jsonFile
     */
    public Alerte(JSONObject jsonFile) {

        try {

            JSONObject point = jsonFile.getJSONObject("PointTest");
            JSONObject geometry =  point.getJSONObject("geometry");
            JSONObject properties = point.getJSONObject("properties");
            // get ccord
            if (geometry.getString("type").equals("Point")){
                JSONArray coordinates = geometry.getJSONArray("coordinates");
                this.lattitude = (double) coordinates.get(0);
                this.longitude = (double) coordinates.get(1);
            }

            this.type = point.getString("type");
            this.dateDeMiseAJour = properties.getString("date_observation");



        } catch (JSONException j){
            j.printStackTrace();
        }

    }

    public Alerte(double longitude, double lattitude){

        this.longitude = longitude;
        this.lattitude = lattitude;

    }

    public void log(){
        Log.w("Alerte : ", this.toString());
    }

    @Override
    public String toString(){

        String result = "";

        result += "nom : " + this.nom + "\n";
        result += "position : lat = " + this.getLattitude() + " - longitude = " + this.getLongitude();
        result += "type : " + this.type;

        return result;
    }

    public double getLattitude() {
        return lattitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
