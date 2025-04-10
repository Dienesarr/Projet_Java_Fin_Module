package com.example.examen_javal3.Model;

import com.example.examen_javal3.Model.Produit;
import com.example.examen_javal3.Model.Vente;

public class VenteProduit {
    private int id;
    private Vente vente;
    private Produit produit;
    private int quantite;
    private double prixUnitaire;

    public VenteProduit() {}

    public VenteProduit(int id, Vente vente, Produit produit, int quantite, double prixUnitaire) {
        this.id = id;
        this.vente = vente;
        this.produit = produit;
        this.quantite = quantite;
        this.prixUnitaire = prixUnitaire;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Vente getVente() {
        return vente;
    }

    public void setVente(Vente vente) {
        this.vente = vente;
    }

    public Produit getProduit() {
        return produit;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public double getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(double prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    @Override
    public String toString() {
        return "VenteProduit{" +
                "id=" + id +
                ", vente=" + vente +
                ", produit=" + produit +
                ", quantite=" + quantite +
                ", prixUnitaire=" + prixUnitaire +
                '}';
    }
}