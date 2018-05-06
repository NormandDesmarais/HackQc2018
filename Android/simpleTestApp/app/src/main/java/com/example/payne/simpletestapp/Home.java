package com.example.payne.simpletestapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;


public class Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.home);

        ServerConnection tmpServer = new ServerConnection(Manager.SERVER_ADDR, Manager.PORT);

        String quebec, userPins;

        // valeurs :)
        int feu = 0;
        int eau = 0;
        int meteo = 0;
        int terrain = 0;


        try {
            quebec = tmpServer.ping(MapDisplay.QUEBEC_BOUNDING_BOX);
            userPins = tmpServer.getRequest("/getUserAlerts", "");

            JSONArray alertesQuebec = (new JSONObject(quebec).getJSONArray("alertes"));
            JSONArray alertesuser = (new JSONObject(userPins).getJSONArray("alertes"));

            for (int i = 0; i < alertesQuebec.length(); i++){
                switch (((JSONObject) alertesQuebec.get(i)).getJSONObject("alerte").getString("type")) {
                    case "Feu" : feu++; break;
                    case "Eau" : eau++; break;
                    case "Inondation" : eau++; break;
                    case "Suivi des cours d'eau" : eau++; break;
                    case "pluie" : meteo++; break;
                    case "vent" : meteo++;
                    case "Meteo" : meteo++; break;
                    case "Terrain" : terrain++; break;
                    default: meteo++; break;
                }
            }

            for (int i = 0; i < alertesuser.length(); i++){
                switch (((JSONObject) alertesuser.get(i)).getJSONObject("alerte").getString("type")) {
                    case "Feu" : feu++; break;
                    case "Eau" : eau++; break;
                    case "Meteo" : meteo++; break;
                    case "Terrain" : terrain++; break;
                    default: meteo++; break;
                }
            }


        } catch (Exception e){
            e.printStackTrace();
        }

        Log.w("VALUES",feu + " " + eau + " " + terrain + " " + meteo);

        findViewById(R.id.proceed).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
