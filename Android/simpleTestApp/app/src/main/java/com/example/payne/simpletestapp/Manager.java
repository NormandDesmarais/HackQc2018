package com.example.payne.simpletestapp;

import android.util.Log;

import org.json.JSONObject;

public class Manager {

    public static final String SERVER_ADDR = "10.240.201.81";
    public static final int PORT = 8080;
    public ServerConnection mainServer;
    public static final String testURL = "https://hackqc.herokuapp.com/api/testPoint";
    public MainActivity mainActivity;

    public Manager(MainActivity act) throws Exception {

        this.mainActivity = act;

        // test server setup
        mainServer = new ServerConnection(Manager.SERVER_ADDR, Manager.PORT);
        ServerConnection testServer = new ServerConnection(testURL);

        String response = testServer.getRequest();
        // test
        Log.w("test server : ", response) ;
        JSONObject JSONtest = JSONWrapper.createJSON(response);
        //this.drawPolygon(JSONtest);

    }

    public void drawPolygon(JSONObject polyPoints){

        this.mainActivity.myMap.drawPolygon(polyPoints);


    }


}
