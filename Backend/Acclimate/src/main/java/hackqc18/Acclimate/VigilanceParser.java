/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hackqc18.Acclimate;

import java.io.*;
import java.util.ArrayList;

public class VigilanceParser {
    
    private ArrayList<Alerte> alertes = new ArrayList<>();
    
    public VigilanceParser (String filename) {
        String tmp = "";

        try {
            
            File fileDir = new File("src" +
                    File.separator + "main" +
                    File.separator + "java" +
                    File.separator + "hackqc18" +
                    File.separator + "Acclimate" +
                    File.separator + "vigilance.csv");

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(fileDir), "UTF8"));

            String s;
            while ((s = reader.readLine()) != null) {
                if (s.contains("\r")) {
                    s.replace("\r", ",");
                }

                tmp += s;
            }
            reader.close();
        } catch (IOException ex) {
            System.err.println("Erreur à l’ouverture du fichier");
        }
        parseFeed(tmp);
    }
    
    
    public void parseFeed(String toBeParsed){      
        String [] alertePrg = toBeParsed.split(",");        
        
        String nom = "", territoire = "", certitude = "", severite = "", type = "";
        String dateDeMiseAJour = "", urgence = "", description = "", geom = "", IdAlert = "";
        String source = "Ministère de la Sécurité publique du Québec";
        double lng = 0.0, lat = 0.0;
        
        for (int i = 10; i < alertePrg.length; i++){
            int j = i % 11;
            
            switch(j){
                case 0: 
                    nom = alertePrg[i];
                    break;
                case 1:
                    source = alertePrg[i];
                    break;
                case 2:
                    territoire = alertePrg[i];
                    break;
                case 3:
                    certitude = alertePrg[i];
                    break;
                case 4:
                    severite = alertePrg[i]; 
                    break;
                case 5:
                    type = alertePrg[i];
                    break;
                case 6:
                    dateDeMiseAJour = alertePrg[i];
                    break;
                case 7:
                    IdAlert = alertePrg[i];
                    break;
                case 8:
                    urgence = alertePrg[i];
                    break;
                case 9:
                    description = alertePrg[i];
                    break;
                case 10:
                    Alerte theAlert = new Alerte(nom, source, territoire, certitude,
                            severite, type, dateDeMiseAJour, IdAlert, urgence,
                            description, lng, lat);
                    alertes.add(theAlert);
                    break;
                default:
                    break;
            }
        }
    }
    
    public ArrayList<Alerte> getAlertes() {
        return alertes;
    }
}