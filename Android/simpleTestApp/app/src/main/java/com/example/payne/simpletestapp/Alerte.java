package com.example.payne.simpletestapp;

import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Alerte {

    private double lattitude;
    private double longitude;
    private String nom;
    private String source;
    private String territoire;
    private String certitude;
    private String severite;
    private String type;
    private String dateDeMiseAJour;
    private String idAlerte;
    private String urgence;
    private String description;

    /**
     * Create an Alert object from the a JSON file (GEOJSON format)
     *
     * @param jsonFile
     */
    public Alerte(JSONObject jsonFile) {

        try {

            JSONObject point = jsonFile.getJSONObject("testPoint");
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
        // test


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
        result += "position : lat = " + this.lattitude + " - longitude = " + this.longitude;
        result += "type : " + this.type;

        return result;
    }
}
