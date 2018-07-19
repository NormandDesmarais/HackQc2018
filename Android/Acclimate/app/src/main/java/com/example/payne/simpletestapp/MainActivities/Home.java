package com.example.payne.simpletestapp.MainActivities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.payne.simpletestapp.Authentification.AuthUIActivity;
import com.example.payne.simpletestapp.Manager;
import com.example.payne.simpletestapp.Map.MapDisplay;
import com.example.payne.simpletestapp.R;
import com.example.payne.simpletestapp.Server.ServerConnection;
import com.example.payne.simpletestapp.Map.ShowLocationActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import org.json.JSONArray;
import org.json.JSONObject;

// TODO: Thread on URL server request

/**
 * Classe de la Page d'Acceuil. C'est la fenêtre qui s'ouvre au lancement de l'application.
 */
public class Home extends AppCompatActivity {

    private int feu, eau, meteo, terrain; // valeurs totales
    private int feuU, eauU, meteoU, terrainU; // valeurs d'usagers


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Vibrator
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(300);

        setContentView(R.layout.home);

        // ['proceed' est le "map button"] et ['textView' est le texte "Entrer"]: chacun mène à la carte
        findViewById(R.id.proceed).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.textView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        // Lancer la vérification d'authentification conservée et utiliser l'usager retourné
        if(AuthUIActivity.setUpFirebaseAuth() != null) { // si déjà connecté
            Snackbar.make(findViewById(R.id.proceed),
                    "You are logged in!", Snackbar.LENGTH_SHORT).show();
        } else { // pas connecté
            Snackbar.make(findViewById(R.id.proceed),
                    "You are not logged in!", Snackbar.LENGTH_SHORT).show();
        }
    }

    // À chaque retour sur le Home Page, on recalcule le nombre d'alertes.
    public void onResume() {
        super.onResume();

        showAmountAlerts(false);

        new Thread(new Runnable() {
            @Override
            public void run() {
                showAmountAlerts(true);
            }
        }).start();
    }

    private void showAmountAlerts(boolean online) {

        // initialisation
        eau = 0; feu = 0; meteo = 0; terrain = 0; eauU = 0; feuU = 0; meteoU = 0; terrainU = 0;

        if(online) {
            ServerConnection tmpServer = new ServerConnection(Manager.SERVER_ADDR, Manager.PORT);

            String quebec, userPins;

            try {
                quebec = tmpServer.ping(MapDisplay.QUEBEC_BOUNDING_BOX);
                userPins = tmpServer.getRequest("/getUserAlerts", "");

                JSONArray alertesQuebec = (new JSONObject(quebec).getJSONArray("alertes"));
                JSONArray alertesUser = (new JSONObject(userPins).getJSONArray("alertes"));

                // TODO: Refactor this thing all over the app (also @MapDisplay, around line 300)
                for (int i = 0; i < alertesQuebec.length(); i++) {
                    switch (((JSONObject) alertesQuebec.get(i)).getJSONObject("alerte").getString("type")) {
                        case "Feu":
                        case "feu":
                        case "Feu de forêt":
                            feu++;
                            break;
                        case "Suivi des cours d'eau":
                        case "Inondation":
                        case "Eau":
                        case "eau":
                            eau++;
                            break;
                        case "Terrain":
                            terrain++;
                            break;
                        case "vent":
                        case "Vent":
                        case "Meteo":
                        case "pluie":
                        case "Orage violent":
                        default:
                            meteo++;
                            break;
                    }
                }

                for (int i = 0; i < alertesUser.length(); i++) {
                    switch (((JSONObject) alertesUser.get(i)).getJSONObject("alerte").getString("type")) {
                        case "Feu": feuU++; break;
                        case "Eau": eauU++; break;
                        case "Meteo": meteoU++; break;
                        case "Terrain": terrainU++; break;
                        default: meteo++; break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Texte lié aux alertes OFFICIELLES
                ((TextView) findViewById(R.id.dlb_eauMain)).setText(String.valueOf(eau) + " alertes actives");
                ((TextView) findViewById(R.id.dlb_feuMain)).setText(String.valueOf(feu) + " alertes actives");
                ((TextView) findViewById(R.id.dlb_meteoMain)).setText(String.valueOf(meteo) + " alertes actives");
                ((TextView) findViewById(R.id.dlb_terrainMain)).setText(String.valueOf(terrain) + " alertes actives");

                // Texte lié aux alertes d'USAGERS
                ((TextView) findViewById(R.id.dlb_eauSec)).setText("+" + String.valueOf(eauU) + " saisies d'USAGERS");
                ((TextView) findViewById(R.id.dlb_feuSec)).setText("+" + String.valueOf(feuU) + " saisies d'USAGERS");
                ((TextView) findViewById(R.id.dlb_meteoSec)).setText("+" + String.valueOf(meteoU) + " saisies d'USAGERS");
                ((TextView) findViewById(R.id.dlb_terrainSec)).setText("+" + String.valueOf(terrainU) + " saisies d'USAGERS");
            }
        });
    }
}
