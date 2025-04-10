package com.example.examen_javal3.Controller;

import com.example.examen_javal3.Model.DB;
import com.example.examen_javal3.Model.Produit;
import com.example.examen_javal3.Model.Vente;
import com.example.examen_javal3.Util.Outils;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import org.jfree.data.json.JSONUtils;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class HistoriqueventeController implements Initializable {
    @FXML private VBox editForm;
    @FXML private VBox editForm2;
    @FXML private TableView<Vente> tableVentes;
    @FXML private TableColumn<Vente, Integer> colIdVente;
    @FXML private TableColumn<Vente, LocalDate> colDate;
    @FXML private TableColumn<Vente, Double> colTotalCommande;
    @FXML private TableColumn<Vente, String> colClient;
    @FXML private TableColumn<Vente, String> colAgent;
    @FXML private TableColumn<Vente, String> colProduits;
    @FXML private ComboBox<String> clientbox;
    @FXML private TableView<Produit> produitsTable;
    @FXML private TableView<Produit> produitselectionner;
    @FXML private TextField txtquantite;
    @FXML private Label totalvente;
    @FXML private Button saveButton;
    @FXML private Button saveproduit;
    @FXML private Button savediminue;
    @FXML private Label labelclient;
    @FXML private Label labelmessage;
    @FXML private Label labelqte;
    @FXML private Label labeltotal;
    @FXML private Label errorLabel;

    private final DB db;

    {
        try {
            db = DB.getInstance();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private final ObservableList<Vente> listevente = FXCollections.observableArrayList();
    private final ObservableList<Produit> listeproduits = FXCollections.observableArrayList();
    private final ObservableList<String> listeclients = FXCollections.observableArrayList();
    private final Map<Integer, Integer> quantiteselectionner = new HashMap<>();
    private final Map<Integer, Integer> originalQuantities = new HashMap<>();
    private final Map<Integer, Integer> originalStock = new HashMap<>();
    private final Map<Integer, Integer> stockactuel = new HashMap<>();
    private Vente selectedVente;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurerColonne();
        masquemodif(false);
        initialisationdonnee();
    }

    private void masquemodif(boolean visible) {
        editForm.setVisible(visible);
        editForm2.setVisible(visible);
        produitsTable.setVisible(visible);
        produitselectionner.setVisible(visible);
        clientbox.setVisible(visible);
        txtquantite.setVisible(visible);
        totalvente.setVisible(visible);
        saveButton.setVisible(visible);
        saveproduit.setVisible(visible);
        savediminue.setVisible(visible);
        labelclient.setVisible(visible);
        //labelmessage.setVisible(visible);
        labelqte.setVisible(visible);
        labeltotal.setVisible(visible);
    }

    // Configuration des colonnes des TableView
    private void configurerColonne() {
        colIdVente.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateVente"));
        colTotalCommande.setCellValueFactory(new PropertyValueFactory<>("total"));
        colProduits.setCellValueFactory(new PropertyValueFactory<>("produitsString"));
        colClient.setCellValueFactory(new PropertyValueFactory<>("clientNom"));
        colAgent.setCellValueFactory(new PropertyValueFactory<>("agentNom"));

        produitsTable.getColumns().forEach(colonne -> {
            if ("produitNom".equals(colonne.getId()))
                colonne.setCellValueFactory(new PropertyValueFactory<>("nom"));
            else if ("produitPrix".equals(colonne.getId()))
                colonne.setCellValueFactory(new PropertyValueFactory<>("prix_vente"));
            else if ("produitQuantite".equals(colonne.getId())) // Ajout de la colonne stock
                colonne.setCellValueFactory(new PropertyValueFactory<>("stock"));
        });

        TableColumn<Produit, Integer> quantiteColonne = (TableColumn<Produit, Integer>) produitselectionner.getColumns().get(1);
        quantiteColonne.setCellValueFactory(cellData -> new SimpleIntegerProperty(quantiteselectionner.getOrDefault(cellData.getValue().getId(), 0)).asObject());
    }

    private void initialisationdonnee() {
        try {
            loadVentes();
            loadClients();
            loadProduits();
        } catch (SQLException e) {
            errorLabel.setStyle("-fx-text-fill: red;");
            errorLabel.setText("Erreur d'initialisation: " + e.getMessage());
        }
    }

    private void loadVentes() {
        String query = "SELECT v.id, v.date_vente, v.total, "
                + "c.nom AS client, u.nom AS agent, "
                + "GROUP_CONCAT(CONCAT(p.nom, ' (', vp.quantite, ')')) AS produits "
                + "FROM ventes v "
                + "JOIN clients c ON v.client_id = c.id "
                + "JOIN utilisateurs u ON v.agent_id = u.id "
                + "JOIN ventes_produits vp ON v.id = vp.vente_id "
                + "JOIN produits p ON vp.produit_id = p.id "
                + "GROUP BY v.id";

        try {
            db.initPrepare(query);
            ResultSet rs = db.executeSelect();
            listevente.clear();

            while (rs.next()) {
                Vente vente = new Vente(
                        rs.getInt("id"),
                        rs.getDate("date_vente").toLocalDate(),
                        rs.getString("produits"),
                        rs.getDouble("total"),
                        rs.getString("client"),
                        rs.getString("agent")
                );
                listevente.add(vente);

            }
            tableVentes.setItems(listevente);
        } catch (SQLException e) {
            errorLabel.setStyle("-fx-text-fill: red;");
            errorLabel.setText("Erreur de chargement: " + e.getMessage());
        }
    }

    private void loadClients() throws SQLException {
        db.initPrepare("SELECT nom FROM clients");
        ResultSet rs = db.executeSelect();
        listeclients.clear();
        while (rs.next()) listeclients.add(rs.getString("nom"));
        clientbox.setItems(listeclients);
    }

    private void loadProduits() throws SQLException {
        db.initPrepare("SELECT id, nom, prix_vente, stock FROM produits");
        ResultSet rs = db.executeSelect();
        listeproduits.clear();
        originalStock.clear();
        stockactuel.clear();

        while (rs.next()) {
            int id = rs.getInt("id");
            String nom = rs.getString("nom");
            double prixVente = rs.getDouble("prix_vente");
            int quantite = rs.getInt("stock");

            listeproduits.add(new Produit(id, nom, prixVente, quantite));
            originalStock.put(id, quantite);
            stockactuel.put(id, quantite);


        }
        produitsTable.setItems(listeproduits);

    }

    @FXML
    private void deletevente() {
        Vente selected = tableVentes.getSelectionModel().getSelectedItem();
        if (selected == null) {
            errorLabel.setStyle("-fx-text-fill: red;");
            errorLabel.setText("Aucune vente selectionnee ");
            return;
        }

        try {
            db.getConnection().setAutoCommit(false);

            List<Map.Entry<Integer, Integer>> produits = new ArrayList<>();
            db.initPrepare("SELECT produit_id, quantite FROM ventes_produits WHERE vente_id = ?");
            db.getPstm().setInt(1, selected.getId());
            ResultSet rs = db.executeSelect();
            while (rs.next()) {
                produits.add(Map.entry(rs.getInt("produit_id"), rs.getInt("quantite")));
            }
            rs.close();

            db.initPrepare("DELETE FROM ventes_produits WHERE vente_id = ?");
            db.getPstm().setInt(1, selected.getId());
            db.executeMaj();

            db.initPrepare("DELETE FROM ventes WHERE id = ?");
            db.getPstm().setInt(1, selected.getId());
            db.executeMaj();

            for (Map.Entry<Integer, Integer> entry : produits) {
                db.initPrepare("UPDATE produits SET stock = stock + ? WHERE id = ?");
                db.getPstm().setInt(1, entry.getValue());
                db.getPstm().setInt(2, entry.getKey());
                db.executeMaj();
            }

            db.getConnection().commit();
            listevente.remove(selected);
            errorLabel.setStyle("-fx-text-fill: green;");
            errorLabel.setText("Vente supprimee avec succes !");
        } catch (SQLException e) {
            try {
                db.getConnection().rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            errorLabel.setStyle("-fx-text-fill: red;");
            errorLabel.setText("Erreur de suppression: " + e.getMessage());
        } finally {
            try {
                db.getConnection().setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void editevente() {
        selectedVente = tableVentes.getSelectionModel().getSelectedItem();
        if (selectedVente == null) {
            errorLabel.setStyle("-fx-text-fill: red;");
            errorLabel.setText("Veuillez selectionner une vente !");
            return;
        }

        quantiteselectionner.clear();
        originalQuantities.clear();
        masquemodif(true);
        clientbox.setValue(selectedVente.getClientNom());
        loadproduitselectionner();
        updateProductSelectionner();
        updateTotal();
    }

    private void loadproduitselectionner() {
        try {
            db.initPrepare("SELECT produit_id, quantite FROM ventes_produits WHERE vente_id = ?");
            db.getPstm().setInt(1, selectedVente.getId());
            ResultSet rs = db.executeSelect();
            while (rs.next()) {
                int produitId = rs.getInt("produit_id");
                int quantite = rs.getInt("quantite");
                originalQuantities.put(produitId, quantite);
                quantiteselectionner.put(produitId, quantite);
                updateStock(produitId, -quantite);
            }
        } catch (SQLException e) {
            errorLabel.setStyle("-fx-text-fill: red;");
            errorLabel.setText("Erreur de chargement: " + e.getMessage());
        }
    }

    @FXML
    private void ajouterProduit() {
        Produit selected = produitsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            errorLabel.setStyle("-fx-text-fill: red;");
            errorLabel.setText("Aucun produit selectionne !");
            return;
        }

        try {
            int quantite = Integer.parseInt(txtquantite.getText());
            if (quantite <= 0) throw new NumberFormatException();

            int produitId = selected.getId();
            int disponible = stockactuel.get(produitId);

            if (quantite > disponible) {
                errorLabel.setStyle("-fx-text-fill: red;");
                errorLabel.setText("Stock insuffisant Disponible: " + disponible);
                return;
            }

            quantiteselectionner.merge(produitId, quantite, Integer::sum);
            updateStock(produitId, -quantite);
            updateProductSelectionner();
            updateTotal();
            txtquantite.clear();
            refreshProduitsTable();
        } catch (NumberFormatException e) {
            errorLabel.setStyle("-fx-text-fill: red;");
            errorLabel.setText("Quantite invalide ");
        }
    }

    @FXML
    private void dimunuer() {
        Produit selectionner = produitselectionner.getSelectionModel().getSelectedItem();
        if (selectionner == null) {
            errorLabel.setStyle("-fx-text-fill: red;");
            errorLabel.setText("Veuillez selectionner un produit dans la liste !");
            return;
        }

        try {
            int quantite = Integer.parseInt(txtquantite.getText());
            if (quantite <= 0) throw new NumberFormatException();

            int produitId = selectionner.getId();
            int currentQty = quantiteselectionner.getOrDefault(produitId, 0);

            if (quantite > currentQty) {
                errorLabel.setStyle("-fx-text-fill: red;");
                errorLabel.setText("Quantité a diminuer trop elevee !");
                return;
            }

            int newQty = currentQty - quantite;

            if (newQty > 0) {
                quantiteselectionner.put(produitId, newQty);
                updateStock(produitId, quantite);
            } else {
                quantiteselectionner.remove(produitId);
                updateStock(produitId, currentQty);
            }

            txtquantite.clear();
            updateProductSelectionner();
            updateTotal();
            refreshProduitsTable();

        } catch (NumberFormatException e) {
            errorLabel.setStyle("-fx-text-fill: red;");
            errorLabel.setText("Quantite invalide !");
        }
    }

    private void updateStock(int produitId, int delta) {
        stockactuel.put(produitId, stockactuel.get(produitId) + delta);
    }

    private void refreshProduitsTable() {
        ObservableList<Produit> updatedList = FXCollections.observableArrayList();
        for (Produit p : listeproduits) {
            updatedList.add(new Produit(
                    p.getId(),
                    p.getNom(),
                    p.getPrix_vente(),
                    stockactuel.get(p.getId())
            ));
        }
        listeproduits.setAll(updatedList);
    }

    @FXML
    private void enregistreedit() {
        if (clientbox.getValue() == null || quantiteselectionner.isEmpty()) {
            errorLabel.setStyle("-fx-text-fill: red;");
            errorLabel.setText("Veuillez remplir tous les champs !");
            return;
        }

        try {
            db.getConnection().setAutoCommit(false);

            int clientId = getClientId(clientbox.getValue());
            if (clientId == -1) {
                throw new SQLException("Client non trouve");
            }

            Map<Integer, Integer> stockDifferences = new HashMap<>();
            originalQuantities.forEach((id, qty) ->
                    stockDifferences.put(id, qty - quantiteselectionner.getOrDefault(id, 0))
            );
            quantiteselectionner.forEach((id, newQty) -> {
                if (!originalQuantities.containsKey(id)) {
                    stockDifferences.put(id, -newQty);
                }
            });

            db.initPrepare("UPDATE produits SET stock = stock+ ? WHERE id = ?");
            for (Map.Entry<Integer, Integer> entry : stockDifferences.entrySet()) {
                db.getPstm().setInt(1, entry.getValue());
                db.getPstm().setInt(2, entry.getKey());
                db.getPstm().addBatch();
            }
            db.getPstm().executeBatch();

            String updateVenteQuery = "UPDATE ventes SET date_vente=?, total=?, client_id=? WHERE id=?";
            db.initPrepare(updateVenteQuery);
            db.getPstm().setDate(1, Date.valueOf(LocalDate.now()));
            db.getPstm().setDouble(2, Double.parseDouble(totalvente.getText().replace(",", ".")));
            db.getPstm().setInt(3, clientId);
            db.getPstm().setInt(4, selectedVente.getId());
            int rowsUpdated = db.executeMaj();
            if (rowsUpdated == 0) {
                throw new SQLException("Echec de la modification de la vente");
            }

            db.initPrepare("DELETE FROM ventes_produits WHERE vente_id = ?");
            db.getPstm().setInt(1, selectedVente.getId());
            db.executeMaj();

            String insertQuery = "INSERT INTO ventes_produits (vente_id, produit_id, quantite, prix_unitaire) VALUES (?, ?, ?, ?)";
            db.initPrepare(insertQuery);
            for (Map.Entry<Integer, Integer> entry : quantiteselectionner.entrySet()) {
                Produit p = getProduitId(entry.getKey());
                if (p == null) continue;

                db.getPstm().setInt(1, selectedVente.getId());
                db.getPstm().setInt(2, p.getId());
                db.getPstm().setInt(3, entry.getValue());
                db.getPstm().setDouble(4, p.getPrix_vente());
                db.getPstm().addBatch();
            }
            db.getPstm().executeBatch();

            db.getConnection().commit();
            errorLabel.setStyle("-fx-text-fill: green;");
            errorLabel.setText("Vente modifiee avec succes !");
            masquemodif(false);
            initialisationdonnee();

        } catch (SQLException | NumberFormatException e) {
            try {
                db.getConnection().rollback();
                errorLabel.setStyle("-fx-text-fill: red;");
                errorLabel.setText("Erreur lors de la modification : " + e.getMessage());
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                db.getConnection().setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private Produit getProduitId(int id) {
        return listeproduits.stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElse(null);
    }

    private int getClientId(String nom) throws SQLException {
        db.initPrepare("SELECT id FROM clients WHERE nom = ?");
        db.getPstm().setString(1, nom);
        ResultSet rs = db.executeSelect();
        return rs.next() ? rs.getInt("id") : -1;
    }

    private void updateProductSelectionner() {
        produitselectionner.setItems(listeproduits.filtered(p ->
                quantiteselectionner.containsKey(p.getId())
        ));
    }

    private void updateTotal() {
        double total = quantiteselectionner.entrySet().stream()
                .mapToDouble(entry -> {
                    Produit p = getProduitId(entry.getKey());
                    return p != null ? p.getPrix_vente() * entry.getValue() : 0;
                })
                .sum();
        totalvente.setText(String.format("%.2f", total));
    }

    @FXML
    private void retour(ActionEvent event) throws IOException {
        // Rediriger vers la page d'accueil
        Outils.load(event, "Page d'accueil", "/com/example/examen_javal3/AccueilAdmin.fxml");
    }

    @FXML
    private void Deconnexion(ActionEvent event) throws IOException {
        // Rediriger vers la page de connexion
        Outils.load(event, "Login", "/com/example/examen_javal3/login.fxml");
    }
}