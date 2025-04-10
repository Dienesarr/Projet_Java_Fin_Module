package com.example.examen_javal3.Model;

import com.example.examen_javal3.Controller.ProduitVendu;

import java.time.LocalDate;
import java.util.List;

public class Vente {
    private int id;
    private LocalDate dateVente;
    private double total;
    private int clientId;
    private int agentId;
    private String clientNom;
    private String agentNom;
    private String produitsString;


    private List<ProduitVendu> produits;

    // Constructeur
    public Vente(int id, LocalDate dateVente, double total, String clientNom, String agentNom, List<ProduitVendu> produits) {
        this.id = id;
        this.dateVente = dateVente;
        this.total = total;
        this.clientNom = clientNom;
        this.agentNom = agentNom;
        this.produits = produits;
    }


    public Vente(int id, LocalDate dateVente, String produitsString, double total,
                 String clientNom, String agentNom) {
        this.id = id;
        this.dateVente = dateVente;
        this.produitsString = produitsString;
        this.total = total;
        this.clientNom = clientNom;
        this.agentNom = agentNom;

    }

    public Vente(int id, LocalDate dateVente, double total,
                 String clientNom, String agentNom, String produitsString) {
        this.id = id;
        this.dateVente = dateVente;
        this.total = total;
        this.clientNom = clientNom;
        this.agentNom = agentNom;
        this.produitsString = produitsString;
    }




    public Vente(int id, LocalDate dateVente, double total, String clientNom, String agentNom) {}

    public Vente(int id, LocalDate dateVente, double total, int clientId, int agentId, String clientNom, String agentNom) {
        this.id = id;
        this.dateVente = dateVente;
        this.total = total;
        this.clientId = clientId;
        this.agentId = agentId;
        this.clientNom = clientNom;
        this.agentNom = agentNom;
    }

    // Getters et Setters

    public List<ProduitVendu> getProduits() { return produits; }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getDateVente() {
        return dateVente;
    }

    public void setDateVente(LocalDate dateVente) {
        this.dateVente = dateVente;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getProduitsString() {
        return produitsString;
    }

    public void setProduitsString(String produitsString) {
        this.produitsString = produitsString;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public int getAgentId() {
        return agentId;
    }

    public void setAgentId(int agentId) {
        this.agentId = agentId;
    }

    public String getClientNom() {
        return clientNom;
    }

    public void setClientNom(String clientNom) {
        this.clientNom = clientNom;
    }

    public String getAgentNom() {
        return agentNom;
    }

    public void setAgentNom(String agentNom) {
        this.agentNom = agentNom;
    }

    @Override
    public String toString() {
        return "Vente{" +
                "id=" + id +
                ", dateVente=" + dateVente +
                ", total=" + total +
                ", clientId=" + clientId +
                ", agentId=" + agentId +
                ", clientNom='" + clientNom + '\'' +
                ", agentNom='" + agentNom + '\'' +
                ", produitsString='" + produitsString + '\'' +
                '}';
    }



}