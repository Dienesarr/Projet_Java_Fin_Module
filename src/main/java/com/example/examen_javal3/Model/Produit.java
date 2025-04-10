package com.example.examen_javal3.Model;
import java.sql.Blob;
public class Produit {

    private int id, stock, categorie_id;
    private double prix_achat, prix_vente;
    private String nom, reference, categorieNom;
    private Blob image;
    private int quantite;

    // Constructeurs
    public Produit(int id, String nom, String reference, int stock) {
        this.id = id;
        this.nom = nom;
        this.stock = stock;
        this.reference = reference;
    }


    public Produit(int id, String nom, double prix_vente, int stock) {
        this.id = id;
        this.nom = nom;
        this.prix_vente = prix_vente;
        this.stock = stock;

    }
//pour produitselectionner
    public Produit(int id, String nom, int stock) {
        this.id = id;
        this.nom = nom;
        this.stock = stock;

    }

    public Produit(int id, String nom, double prixVente, double prixAchat, String reference, int quantite, String categorieNom) {
        this.id = id;
        this.nom = nom;
        this.prix_vente = prixVente;
        this.prix_achat = prixAchat;
        this.reference = reference;
        this.quantite = quantite;
        this.categorieNom = categorieNom;
    }


    // Getters et Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getCategorie_id() {
        return categorie_id;
    }

    public void setCategorie_id(int categorie_id) {
        this.categorie_id = categorie_id;
    }

    public double getPrix_achat() {
        return prix_achat;
    }

    public void setPrix_achat(double prix_achat) {
        this.prix_achat = prix_achat;
    }

    public double getPrix_vente() {
        return prix_vente;
    }

    public void setPrix_vente(double prix_vente) {
        this.prix_vente = prix_vente;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getCategorieNom() {
        return categorieNom;
    }

    public void setCategorieNom(String categorieNom) {
        this.categorieNom = categorieNom;
    }

    public Blob getImage() {
        return image;
    }

    public void setImage(Blob image) {
        this.image = image;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    @Override
    public String toString() {
        return "Produit{" +
                "id=" + id +
                ", stock=" + stock +
                ", categorie_id=" + categorie_id +
                ", prix_achat=" + prix_achat +
                ", prix_vente=" + prix_vente +
                ", nom='" + nom + '\'' +
                ", reference='" + reference + '\'' +
                ", categorieNom='" + categorieNom + '\'' +
                ", image=" + image +
                ", quantite=" + quantite +
                '}';
    }
}