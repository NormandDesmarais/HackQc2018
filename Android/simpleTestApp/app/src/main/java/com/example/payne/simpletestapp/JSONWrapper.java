package com.example.payne.simpletestapp;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

public class JSONWrapper {

    public JSONObject jsonFile;

    public JSONWrapper(String fileUrl) throws Exception {

        jsonFile = new JSONObject(JSONWrapper.getStringFromFile(fileUrl));
        Log.w("jsonFile : ", jsonFile.toString());

    }


    /* https://stackoverflow.com/
    questions/12910503/read-file-as-string
    ?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
     */
    private static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }


    private static String getStringFromFile (String filePath) throws Exception {
        File fl = new File(filePath);
        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);
        //Make sure you close all streams.
        fin.close();
        return ret;
    }


}
