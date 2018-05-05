package com.example.payne.simpletestapp;

import android.util.Log;

public class Manager {

    public static final String SERVER_ADDR = "10.240.201.81";
    public static final int PORT = 8080;
    public ServerConnection server;
    public static final String test = "https://hackqc.herokuapp.com/api/testPoint";
    public MainActivity mainActivity;

    public Manager(MainActivity act){

        // test server setup
        ServerConnection mainServer = new ServerConnection(Manager.SERVER_ADDR, Manager.PORT);

        try {
            server.ping();
        } catch (Exception e){
            e.printStackTrace();
        }

    }

}
