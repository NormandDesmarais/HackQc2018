package hackqc18.Acclimate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public abstract class HistoryCsvParser {
    protected String filename;
    protected ArrayList<Alerte> alertes = new ArrayList<>();
    
    public HistoryCsvParser(String filename) {
        this.filename = filename;
    }
    
    public void openAndParse() {
        String toBeParsed = "";

        try {
            
            File fileDir = new File("src" +
                    File.separator + "main" +
                    File.separator + "java" +
                    File.separator + "hackqc18" +
                    File.separator + "Acclimate" +
                    File.separator + filename);

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
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

    
    public ArrayList<Alerte> getAlertes() {
        return alertes;
    }
}
