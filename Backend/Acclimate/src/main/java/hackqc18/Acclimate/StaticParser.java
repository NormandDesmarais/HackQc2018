package hackqc18.Acclimate;

import static hackqc18.Acclimate.AlertesFluxRss.getInfosStr;
import java.io.*;
import java.util.ArrayList;

public class StaticParser {

    String tmp = "";
    String[] champs = {};
    String[] constrAlerte = {};
    String donnee = "";
    
    private ArrayList<Double> coordX = new ArrayList<>();
    private ArrayList<Double> coordY = new ArrayList<>();
    private ArrayList<Alerte> alertes = new ArrayList<>();

    public StaticParser () {

        try {
            FileReader fr = new FileReader("src" +
                    File.separator + "main" +
                    File.separator + "java" +
                    File.separator + "hackqc18" +
                    File.separator + "Acclimate" +
                    File.separator + "histo_alert.json");
            BufferedReader reader = new BufferedReader(fr);
            String s;
            while ((s = reader.readLine()) != null) {
                tmp += s;
            }
            reader.close();
        } catch (IOException ex) {
            System.out.println("Erreur à l’ouverture du fichier");
        }
        
        tmp.substring(76);
        //createFile();
        parseFeed();
    }

    public void createFile() {
        //System.out.println(tmp);
    }
    
    public void parseFeed(){
        String nom, source, territoire, certitude, severite, type;
        String dateDeMiseAJour, urgence, description, geom;
        
        ArrayList<String> alertePrg = getInfos("{", 0, "} }", tmp);
        
        for (int i = 0; i < alertePrg.size(); i++){
            System.out.println(alertePrg.get(i));
            nom = "";
            //String coords = getInfosStr("<b>Urgence</b>", 0, "amp;zoom", alertePrg.get(i));
            //geom = getInfosStr("center=", 4, "&", coords);
            //String auteur = getInfosStr("Auteur", 150, "br/>", alertePrg.get(i));
            source = "Ministère de la Sécurité publique du Québec";
            //type = getInfosStr("<b>Type</b> :", 1, "<br/>", alertePrg.get(i));
            //dateDeMiseAJour = getInfosStr("<b>Date de mise à jour</b> :", 1,
                    //"<br/>", alertePrg.get(i));
            //description = getInfosStr("<b>Description</b> :", 1, "<br/>", alertePrg.get(i));
            //severite = getInfosStr("<b>Sévérite</b> :", 1, "<br/>", alertePrg.get(i));
            //territoire = getInfosStr("<b>Secteur</b> :", 1, "<br/>", alertePrg.get(i));
            certitude = getInfosStr("\"Inconnue\"", 0, ",", alertePrg.get(i));
            //System.out.println(certitude);
            //urgence = getInfosStr("<b>Urgence</b> :", 1, "<br/>", alertePrg.get(i));
            //String coordos = geom + "<>";
            //double x = Double.parseDouble(("-" + getInfosStr("-", 0, ",", coordos)));
            //double y = Double.parseDouble((getInfosStr(",", (x + "").length(),
                    //"<>", coordos)));
            //this.coordX.add(x);
            //this.coordY.add(y);
        }
    }
    
    
    public static String getInfosStr(String balise, int offset, String fin, String rss) {
        String liste = "";
        String rssText = rss;
        int ix;
        for (String word : rssText.split(fin)) {
            if (word.contains(balise)) {
                ix = rssText.indexOf(word) + balise.length();
                liste += (rssText.substring(ix + offset, rssText.indexOf(fin, ix + 1)));
            }
        }
        return liste;
    }
    
    
    public static ArrayList<String> getInfos(String balise, int offset, String fin, String rss) {
        ArrayList<String> liste = new ArrayList<>();
        String rssText = rss;
        int ix;
        for (String word : rssText.split(fin)) {
            if (word.contains(balise)) {
                ix = rssText.indexOf(word) + balise.length();
                liste.add(rssText.substring(ix + offset, rssText.indexOf(fin, ix + 1)));
                //System.out.println(rssText.substring(ix + offset, rssText.indexOf(fin, ix + 1)));
            }
        }
        return liste;
    }
}
