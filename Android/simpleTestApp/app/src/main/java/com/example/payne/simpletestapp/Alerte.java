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

            // TODO : redo this from template

            this.nom = jsonFile.getString("nom");
            this.source = jsonFile.getString("source");
            this.territoire = jsonFile.getString("territoire");
            this.certitude = jsonFile.getString("certitude");
            this.severite = jsonFile.getString("severite");
            this.type = jsonFile.getString("type");
            this.dateDeMiseAJour = jsonFile.getString("dateDeMiseAJour");
            this.idAlerte = jsonFile.getString("idAlerte");
            this.urgence = jsonFile.getString("urgence");
            this.description = jsonFile.getString("description");

            JSONObject geom = jsonFile.getJSONObject("geometry");
            this.longitude = (double) geom.getJSONArray("coordinates").get(0);
            this.longitude = (double) geom.getJSONArray("coordinates").get(0);


            /*
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

        */

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
