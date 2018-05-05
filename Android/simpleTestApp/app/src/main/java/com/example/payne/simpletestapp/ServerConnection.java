package com.example.payne.simpletestapp;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;


public class ServerConnection {

    public String test = "https://hackqc.herokuapp.com/api/testPoint";
    public String serverAdress;
    public int port;
    public String serverUri;

    public ServerConnection(String addr, int port){
        this.serverAdress = addr;
        this.port = port;
        serverUri = "http://" + Manager.SERVER_ADDR + ":" + Manager.PORT + "/greeting?" ;

    }

    /**
     * Envoie une requête au serveur de façon périodique
     *
     * @return
     */
    public String ping() throws Exception {

        final Response result = new Response();

        Thread conn = new Thread( new Runnable() {

            @Override
            public void run() {

                try {

                    // Server URI
                    String param = "name=toto";
                    String url = serverUri + param;
                    Log.w("URLtest : ", test);

                    URL obj = new URL(test);
                    HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                    // optional default is GET
                    con.setRequestMethod("GET");
                    int responseCode = con.getResponseCode();
                    Log.w("response code : ", responseCode + "");

                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String inputLine;
                    StringBuffer response = new StringBuffer();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }

                    in.close();

                    Log.w("response", response.toString());

                    result.response = response.toString();

                } catch (Exception e){
                    // TODO: manage pin error
                    e.printStackTrace();
                }
            }
        });

        conn.start();
        conn.join();

        return result.response;

    }

    public String getRequest(String url){

        final Response result = new Response();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    URL obj = new URL(test);
                    HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                    // optional default is GET
                    con.setRequestMethod("GET");
                    int responseCode = con.getResponseCode();
                    Log.w("response code : ", responseCode + "");

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
        }).start();

        return result.response;

    }


}
