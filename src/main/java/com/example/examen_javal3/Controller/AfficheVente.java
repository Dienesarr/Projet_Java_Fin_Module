package com.example.examen_javal3.Controller;

import com.example.examen_javal3.Model.DB;
import com.example.examen_javal3.Model.Produit;
import com.example.examen_javal3.Util.Outils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AfficheVente implements Initializable {

    @FXML
    private ComboBox<String> clientbox;

    @FXML
    private TableView<Produit> produitsTable;

    @FXML
    private TableColumn<Produit, String> produitNom;

    @FXML
    private TableColumn<Produit, Double> produitPrix;

    @FXML
    private TableColumn<Produit, Integer> produitQuantite;

    @FXML
    private TextField txtquantite;

    @FXML
    private TableView<Produit> produitselectionner;

    @FXML
    private Label totalvente;

    @FXML
    private Label errorLabel;

    private DB db;

    {
        try {
            db = DB.getInstance();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private ObservableList<Produit> produitselectionne = FXCollections.observableArrayList();

    public void initialize(URL url, ResourceBundle resourceBundle) {
        produitsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        produitNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        produitPrix.setCellValueFactory(new PropertyValueFactory<>("prix_vente"));
        produitQuantite.setCellValueFactory(new PropertyValueFactory<>("stock"));

        // Correction ici : utilisez "quantite" au lieu de "stock"
        produitselectionner.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("nom"));
        produitselectionner.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("prix_vente"));
        produitselectionner.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("quantite"));
        loadclient();
        loadproduit();
    }

    @FXML
    public void Affichevente(ActionEvent event) {
        loadPage("/com/example/examen_javal3/test1.fxml", event);
    }

    private void loadPage(String fxmlPath, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Remplir les ComboBox et Tableau
    private void loadclient() {
        List<String> clients = getClients();
        ObservableList<String> observableClients = FXCollections.observableArrayList(clients);
        clientbox.setItems(observableClients);
    }

    private List<String> getClients() {
        List<String> clients = new ArrayList<>();
        String query = "SELECT nom FROM clients";

        try {
            db.initPrepare(query);
            ResultSet rs = db.executeSelect();
            while (rs.next()) {
                String nomClient = rs.getString("nom");
                clients.add(nomClient);
            }
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return clients;
    }

    private void loadproduit() {
        List<Produit> produits = getProduits();
        ObservableList<Produit> observableProduits = FXCollections.observableArrayList(produits);
        produitsTable.setItems(observableProduits);
    }

    private List<Produit> getProduits() {
        List<Produit> produits = new ArrayList<>();
        String query = "SELECT id, nom, prix_vente, stock FROM produits";

        try {
            db.initPrepare(query);
            ResultSet rs = db.executeSelect();
            while (rs.next()) {
                Produit produit = new Produit(rs.getInt("id"), rs.getString("nom"), rs.getInt("stock"));
                produit.setId(rs.getInt("id"));
                produit.setNom(rs.getString("nom"));
                produit.setPrix_vente(rs.getDouble("prix_vente"));
                produit.setQuantite(rs.getInt("stock"));
                produits.add(produit);
            }
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return produits;
    }


    @FXML
    private void ajouterProduit() {
        ObservableList<Produit> produitselectionnerList = produitsTable.getSelectionModel().getSelectedItems();

        if (produitselectionnerList.isEmpty()) {
            errorLabel.setText("Veuillez selectionner au moins un produit.");
            return;
        }

        int quantiteDemandee;
        try {
            quantiteDemandee = Integer.parseInt(txtquantite.getText());
            if (quantiteDemandee <= 0) {
                errorLabel.setText("La quantite doit etre superieure a zero.");
                return;
            }
        } catch (NumberFormatException e) {
            errorLabel.setText("Veuillez entrer une quantite valide.");
            return;
        }

        for (Produit produitselectionner : produitselectionnerList) {
            if (quantiteDemandee > produitselectionner.getQuantite()) {
                errorLabel.setText("Quantite demander superieure au stock disponible pour le produit : " + produitselectionner.getNom());
                return;
            }

            Produit produitVente = new Produit(
                    produitselectionner.getId(),
                    produitselectionner.getNom(),
                    produitselectionner.getPrix_vente(),
                    0, // Prix non utiliserrrr
                    "", // Référence non utiliseeeeeeee
                    quantiteDemandee,
                    "" // Catégorie non utiliseeeeee
            );

            produitselectionne.add(produitVente);


        }


        produitselectionner.setItems(produitselectionne);
        calculerTotal();
        txtquantite.clear();
    }

    private void calculerTotal() {
        double total = 0;
        for (Produit produit : produitselectionne) {
            total += produit.getPrix_vente() * produit.getQuantite();
        }
        totalvente.setText(String.format("%.2f", total));
    }

    @FXML
    private void enregistrerVente() {
        String clientNom = clientbox.getValue();
        if (clientNom == null || produitselectionne.isEmpty()) {
            errorLabel.setText("Veuillez selectionner un client et ajouter des produits.");
            return;
        }

        int clientId = getClientId(clientNom);
        if (clientId == -1) {
            errorLabel.setText("Client invalide.");
            return;
        }

        // Remplacez cette valeur par l'ID de l'agent si nécessaire
        int agentId = 8; // Exemple : ID de l'agent par défaut

        // Convertir le total en double
        String totalText = totalvente.getText().replace(",", ".");
        double total;
        try {
            total = Double.parseDouble(totalText);
        } catch (NumberFormatException e) {
            errorLabel.setText("Erreur de format du total.");
            return;
        }

        try {
            String queryVente = "INSERT INTO ventes (date_vente, total, client_id, agent_id) VALUES (?, ?, ?, ?)";
            db.initPrepare(queryVente);
            db.getPstm().setDate(1, java.sql.Date.valueOf(LocalDate.now()));
            db.getPstm().setDouble(2, total);
            db.getPstm().setInt(3, clientId);
            db.getPstm().setInt(4, agentId); // ID de l'agent
            db.executeMaj();

            String queryLastId = "SELECT LAST_INSERT_ID() AS dernier_id";
            db.initPrepare(queryLastId);
            ResultSet rs = db.executeSelect();
            int venteId = -1;
            if (rs.next()) {
                venteId = rs.getInt("dernier_id");
            }
            rs.close();

            // Enregistrer les produits de la vente
            for (Produit produit : produitselectionne) {
                String queryVenteProduit = "INSERT INTO ventes_produits (vente_id, produit_id, quantite, prix_unitaire) VALUES (?, ?, ?, ?)";
                db.initPrepare(queryVenteProduit);
                db.getPstm().setInt(1, venteId);
                db.getPstm().setInt(2, produit.getId());
                db.getPstm().setInt(3, produit.getQuantite());
                db.getPstm().setDouble(4, produit.getPrix_vente());
                db.executeMaj();

                // Mettre à jour le stock
                String queryUpdateStock = "UPDATE produits SET stock = stock - ? WHERE id = ?";
                db.initPrepare(queryUpdateStock);
                db.getPstm().setInt(1, produit.getQuantite());
                db.getPstm().setInt(2, produit.getId());
                db.executeMaj();
            }

            errorLabel.setText("Vente enregistree avec succes.");
            produitselectionne.clear();
            totalvente.setText("00");
            loadproduit();
            loadclient();
        } catch (SQLException e) {
            errorLabel.setText("Erreur lors de l'enregistrement de la vente : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private int getClientId(String clientNom) {
        String query = "SELECT id FROM clients WHERE nom = ?";
        int clientId = -1;

        try {
            db.initPrepare(query);
            db.getPstm().setString(1, clientNom);
            ResultSet rs = db.executeSelect();

            if (rs != null && rs.next()) {
                clientId = rs.getInt("id");
            }

            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return clientId;
    }

    public void effacer(ActionEvent event) {
        clientbox.getSelectionModel().clearSelection();
        txtquantite.clear();
        produitQuantite.getColumns().clear();
    }

    @FXML
    private void retour(ActionEvent event) throws IOException {
        // Rediriger vers la page d'accueil par défaut
        Outils.load(event, "Page d'accueil", "/com/example/examen_javal3/AccueilAgent.fxml");
    }
}