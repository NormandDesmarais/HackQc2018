package com.example.payne.simpletestapp;

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
     * Envoie une requête au serveur de façon périodique
     *
     * @return
     */
    public String ping() throws Exception {

        String uri = this.serverAddress;

        // TODO : add parameter to get request
        uri = uri + "name=toto";

        return this.getRequest(this.serverAddress);

    }

    /**
     *
     * Une requête pour mettre èa jour l'affichage des zones dangereuse
     *
     * @return
     * @throws Exception
     */
    public String request() throws Exception {

        String uri = this.serverAddress;

        // TODO : add parameter to get request
        uri = uri + "name=toto";

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


}