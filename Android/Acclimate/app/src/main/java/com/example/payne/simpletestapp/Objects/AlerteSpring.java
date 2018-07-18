package com.example.payne.simpletestapp.Objects;


public class AlerteSpring {

        private String id;
        private String nom;
        private String source;
        private String territoire;
        private String certitude;
        private String severite;
        private String dateDeMiseAJour;
        private String urgence;
        private String description;
        private String type;
        private Geometry geometry;
        private int count;


        // GETTERS AND SETTERS
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTerritoire() {
        return territoire;
    }

    public void setTerritoire(String territoire) {
        this.territoire = territoire;
    }

    public String getCertitude() {
        return certitude;
    }

    public void setCertitude(String certitude) {
        this.certitude = certitude;
    }

    public String getSeverite() {
        return severite;
    }

    public void setSeverite(String severite) {
        this.severite = severite;
    }

    public String getDateDeMiseAJour() {
        return dateDeMiseAJour;
    }

    public void setDateDeMiseAJour(String dateDeMiseAJour) {
        this.dateDeMiseAJour = dateDeMiseAJour;
    }

    public String getUrgence() {
        return urgence;
    }

    public void setUrgence(String urgence) {
        this.urgence = urgence;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
