package com.example.payne.simpletestapp;

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
     * @param JsonFile
     */
    public Alerte(JSONObject JsonFile){



    }

    public Alerte(double longitude, double lattitude){

        this.longitude = longitude;
        this.lattitude = lattitude;

    }

}
