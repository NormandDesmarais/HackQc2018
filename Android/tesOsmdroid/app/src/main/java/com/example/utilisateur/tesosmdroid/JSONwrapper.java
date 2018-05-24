package com.example.utilisateur.tesosmdroid;

import android.util.JsonReader;
import android.util.Log;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * wrapper autour de l'objet JSON
 * <p>
 * Created by Utilisateur on 2018-05-04.
 */

public class JSONwrapper {

    public JSONObject jsonFile;

    public JSONwrapper(String fileUrl) throws Exception {

        jsonFile = new JSONObject(JSONwrapper.getStringFromFile(fileUrl));
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
