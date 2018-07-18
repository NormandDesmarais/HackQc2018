package com.example.payne.simpletestapp.Server;


import android.util.Log;

import com.example.payne.simpletestapp.Objects.Alerte;
import com.example.payne.simpletestapp.Objects.AlerteSpring;

import org.osmdroid.util.BoundingBox;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;


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
    public String ping(BoundingBox boundingBox) throws Exception {

        String param =
            "?nord=" + boundingBox.getLatNorth() +
            "&sud=" + boundingBox.getLatSouth() +
            "&est=" + boundingBox.getLonEast() +
            "&ouest=" + boundingBox.getLonWest();

        return this.getRequest("/alertes", param);

    }


    public String getRequest(final String path, final String param) throws Exception {

        final Response result = new Response();

        Thread connection = new Thread(new Runnable() {

            @Override
            public void run() {

                try{
                    URL obj = new URL(serverAddress + path + param);
                    /* TODO: Boolean for Secured-cast connection:
                    HttpsURLConnection con = (HttpsURLConnection) obj.openConnection(); */
                    HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                    // optional default is GET
                    con.setRequestMethod("GET");
                    // int responseCode = con.getResponseCode();

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

    public Boolean postAlert(Alerte alerte) {

        boolean success = false;
        alerte.log();

        String param =  "?type=" + alerte.type +
                        "&lat=" + alerte.getLatitude() +
                        "&lng=" + alerte.getLongitude();

        try {
            this.getRequest("/putAlert", param);
            success = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return success;

    }

    public AlerteSpring[] testSpringRequest(){

        String testUrl =
                "https://acclimate-api.herokuapp.com/alertes/api/historical/alerts?north=45.5&south=44.5&west=-75.5&east=-74.5";

        // Set the Accept header
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setAccept(Collections.singletonList(new MediaType("application","json")));
        HttpEntity<?> requestEntity = new HttpEntity<Object>(requestHeaders);

        // Create a new RestTemplate instance
        RestTemplate restTemplate = new RestTemplate();

        // Make the HTTP GET request, marshaling the response from JSON to an array of Events
        ResponseEntity<AlerteSpring[]> responseEntity =
                restTemplate.exchange(testUrl, HttpMethod.GET, requestEntity, AlerteSpring[].class);

        return responseEntity.getBody();

    }


}
