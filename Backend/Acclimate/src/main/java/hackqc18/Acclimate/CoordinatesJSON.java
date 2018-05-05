/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hackqc18.Acclimate;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author incognito
 */
public class CoordinatesJSON {
    private JSONObject jsonObject = new JSONObject();

    public CoordinatesJSON(double x, double y) {
        JSONArray jArray = new JSONArray();
        jArray.add(x);
        jArray.add(y);
        jsonObject.put("coordinates", jArray);
    }
    
    
}
