package hackqc18.Acclimate.alert;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class CsvAlertParser {

    protected String filename;

    protected HashMap<String, Alert> alerts = new HashMap<>();


    public CsvAlertParser(String filename) {
        this.filename = filename;
    }


    public void openAndParse() {
        String toBeParsed = "";

        try {

            File fileDir = new File("src" + File.separator + "main"
                    + File.separator + "resources" + File.separator + filename);

            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(fileDir), "UTF8"));

            String s;
            while ((s = reader.readLine()) != null) {
                if (s.contains("\r")) {
                    s.replace("\r", ",");
                }

                toBeParsed += s;
            }
            reader.close();
        } catch (IOException ex) {
            System.err.println("Erreur à l’ouverture du fichier");
        }
        parseContent(toBeParsed);
    }


    public abstract void parseContent(String toBeParsed);


    public ArrayList<Alert> getAlertes() {
        return new ArrayList<>(alerts.values());
    }


    /**
     * Utility method that creates a unique id from the alert type, date,
     * longitude and latitude.
     *
     * @return the id
     */
    public String createId(String type, String date, double lng, double lat) {
        return (type.hashCode() + "-" + date.hashCode() + "-" + lng + "-" + lat)
                .replaceAll("\\.", "");
    }
}
