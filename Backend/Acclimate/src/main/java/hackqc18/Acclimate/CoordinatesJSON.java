package hackqc18.Acclimate;

import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;

public class CoordinatesJSON {
    private JSONObject jsonObject = new JSONObject();

    public CoordinatesJSON(double x, double y) {
        JSONArray jArray = new JSONArray();
        jArray.add(x);
        jArray.add(y);
        jsonObject.put("coordinates", jArray);
    }
    
    public add(double x, double y) {
        JSONArray jArray = new JSONArray();
        jArray.add(x);
        jArray.add(y);
        jsonObject.
    }
    
}
