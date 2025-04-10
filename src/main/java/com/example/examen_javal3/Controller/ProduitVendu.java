package com.example.examen_javal3.Controller;

public class ProduitVendu {
    private String nom;
    private int quantite;
    private double prixUnitaire;

    // Constructeur
    public ProduitVendu(String nom, int quantite, double prixUnitaire) {
        this.nom = nom;
        this.quantite = quantite;
        this.prixUnitaire = prixUnitaire;
    }

    // Getters et Setters
    public String getNom() { return nom; }
    public int getQuantite() { return quantite; }
    public double getPrixUnitaire() { return prixUnitaire; }
}