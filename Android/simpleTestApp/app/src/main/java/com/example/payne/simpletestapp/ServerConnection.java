package com.example.payne.simpletestapp;

import android.util.Log;
import android.widget.Toast;

import org.osmdroid.util.BoundingBox;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class ServerConnection {

    public String serverAddress;
    public int port;

    public ServerConnection(String addr, int port){
        this.serverAddress = addr;
        this.port = port;

    }

    public ServerConnection(String addr){

        this.port = 80;
        this.serverAddress = addr;

    }

    /**
     * Envoie une requête au serveur de façon périodique.
     * <p></p>
     * Reçoit toutes les alertes sur le territoire
     *
     * @return
     */
    public String ping(String path, BoundingBox boundingBox) throws Exception {

        String param =
            "?north=" + boundingBox.getLatNorth() +
            "&south=" + boundingBox.getLatSouth() +
            "&east=" + boundingBox.getLonEast() +
            "&west=" + boundingBox.getLonWest();

        String uri = this.serverAddress + path + param;
        return this.getRequest(uri);

    }


    /**
     *
     * Une requête pour mettre à jour l'affichage des zones dangereuse
     *
     * @return
     * @throws Exception
     */
    public String request(String path, BoundingBox boundingBox) throws Exception {

        String param =
                "?north=" + boundingBox.getLatNorth() +
                "&south=" + boundingBox.getLatSouth() +
                "&east=" + boundingBox.getLonEast() +
                "&west=" + boundingBox.getLonWest();

        String uri = this.serverAddress + path + param;
        return this.getRequest(uri);

    }

    /**
     *
     * Une requête pour mettre èa jour l'affichage des zones dangereuse
     *
     * @return
     * @throws Exception
     */
    public String request(String param) throws Exception {

        String uri = this.serverAddress;

        // TODO : add parameter to get request
        uri = uri + param;

        return this.getRequest(uri);

    }


    /**
     * Une requpête GET générale
     *
     * @return  String  le résultat de la reuête
     * @throws Exception
     */
    public String getRequest() throws Exception {

        final Response result = new Response();

        Thread connection = new Thread(new Runnable() {

            @Override
            public void run() {

                try{
                    URL obj = new URL(serverAddress);
                    HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                    // optional default is GET
                    con.setRequestMethod("GET");
                    int responseCode = con.getResponseCode();
                    // Log.w("response code : ", responseCode + "");

                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String inputLine;
                    StringBuffer response = new StringBuffer();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }

                    in.close();

                    result.response = response.toString();

                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        connection.start();
        connection.join();


        return result.response;

    }

    public String getRequest(final String param) throws Exception {

        final Response result = new Response();

        Thread connection = new Thread(new Runnable() {

            @Override
            public void run() {

                try{
                    URL obj = new URL(serverAddress + param);
                    Log.w("URLTEST : ", serverAddress + param);
                    HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                    // optional default is GET
                    con.setRequestMethod("GET");
                    int responseCode = con.getResponseCode();
                    // Log.w("response code : ", responseCode + "");

                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String inputLine;
                    StringBuffer response = new StringBuffer();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }

                    in.close();

                    result.response = response.toString();
                    Log.w("RESULTAT REQUEST : ", result.response);

                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        connection.start();
        connection.join();


        return result.response;

    }

    public Boolean postAlert(Alerte alerte) {

        boolean success = false;
        alerte.log();

        String param =  "?type=" + alerte.type +
                        "&lat=" + alerte.getLatitude() +
                        "&lng=" + alerte.getLongitude();

        try {
            this.getRequest(param);
            success = true;
        } catch (Exception e) {
            e.printStackTrace();
        }



        return success;


    }


}
