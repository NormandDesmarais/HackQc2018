package hackqc18.Acclimate;

import java.io.*;

public class StaticParser {

    String tmp = "";
    String[] champs = {};
    String[] constrAlerte = {};
    String donnee = "";

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

        createFile();
    }

    public void createFile() {
        System.out.println(tmp);



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
}
