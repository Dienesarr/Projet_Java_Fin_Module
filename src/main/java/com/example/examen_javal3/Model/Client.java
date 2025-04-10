package com.example.examen_javal3.Model;

public class Client {
    private int id;
    private String nom,telephone,adresse,email;

    public Client(int id, String nom, String telephone, String adresse, String email) {
        this.id = id;
        this.nom = nom;
        this.telephone = telephone;
        this.adresse = adresse;
        this.email = email;
    }

    public Client() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", telephone='" + telephone + '\'' +
                ", adresse='" + adresse + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
