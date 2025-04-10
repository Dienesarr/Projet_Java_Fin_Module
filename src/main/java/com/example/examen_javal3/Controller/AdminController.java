package com.example.examen_javal3.Controller;

import com.example.examen_javal3.Model.DB;
import com.example.examen_javal3.Model.Produit;
import com.example.examen_javal3.Util.Outils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Font;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AdminController {
    @FXML
    private CategoryAxis axeX;
    @FXML
    private BarChart<String, Number> graphvente;
    @FXML
    private Label stockTotal;
    @FXML
    private TableView<Produit> tblstockfaible;
    @FXML
    private TableColumn<Produit, String> produit;
    @FXML
    private TableColumn<Produit, String> reference;
    @FXML
    private TableColumn<Produit, Integer> quantite;
    @FXML private Label ventedujour;
    DB db;

    {
        try {
            db = DB.getInstance();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }



    @FXML
    public void initialize() {

        produit.setCellValueFactory(new PropertyValueFactory<>("nom"));
        reference.setCellValueFactory(new PropertyValueFactory<>("reference"));
        quantite.setCellValueFactory(new PropertyValueFactory<>("stock"));
        loadStock();
        loadVente();
        loadgraphique();
        // Rotation des étiquettes de l'axe X à 45 degrés
        axeX.setTickLabelRotation(-45);
        axeX.setTickLabelFont(Font.font("System", 12));
    }


    private void loadStock() {
        try {
            // Calcul le stock total
            String queryStock = "SELECT SUM(stock) AS total FROM produits";
            db.initPrepare(queryStock);
            ResultSet rss = db.executeSelect();
            updateStockTotal(stockTotal, rss, "total");

            // le stock faible
            String queryLowStock = "SELECT id, nom,reference, stock FROM produits WHERE stock < 10";
            db.initPrepare(queryLowStock);
            ResultSet rs = db.executeSelect();
            updateStockfaible(rs);

        } catch (SQLException e) {
        }
    }

    private void updateStockfaible(ResultSet rs) throws SQLException {
        ObservableList<Produit> lowStockData = FXCollections.observableArrayList();

        while (rs.next()) {
            Produit p = new Produit(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("reference"),
                    rs.getInt("stock")
            );
            lowStockData.add(p);
        }

        Platform.runLater(() -> {
            tblstockfaible.setItems(lowStockData);
            tblstockfaible.refresh();
        });
    }
    private void loadVente() {
        try {

            String today = LocalDate.now().toString();
            String querySales = "SELECT SUM(vp.quantite * vp.prix_unitaire) AS total " +
                    "FROM ventes v " +
                    "JOIN ventes_produits vp ON v.id = vp.vente_id " +
                    "WHERE v.date_vente = CURRENT_DATE";
            db.initPrepare(querySales);
            ResultSet rs = db.executeSelect();
            updateVentejour(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void loadgraphique() {
        try {

            String queryChart = "SELECT p.nom, SUM(vp.quantite) AS total " +
                    "FROM ventes_produits vp " +
                    "JOIN produits p ON vp.produit_id = p.id " +
                    "GROUP BY p.nom ORDER BY total DESC LIMIT 5";
            db.initPrepare(queryChart);
            ResultSet rs = db.executeSelect();


            graphvente.getData().clear(); // Effacer les anciennes données

            try {
                while (rs.next()) {
                    String productName = rs.getString("nom");
                    int productTotal = rs.getInt("total");

                    XYChart.Series<String, Number> series = new XYChart.Series<>();
                    series.setName(productName); // Nom pour identifier la série
                    series.getData().add(new XYChart.Data<>(productName, productTotal));

                    graphvente.getData().add(series);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Méthodes utilitaires pour mettre à jour les labels avec les résultats des requêtes SQL

    private void updateStockTotal(Label label, ResultSet rs, String column) throws SQLException {
        if (rs.next()) {
            int value = rs.getInt(column);
            label.setText(String.valueOf(value));
        }
    }


    private void updateVentejour(ResultSet rs) throws SQLException {
        if (rs.next()) {
            double total = rs.getDouble("total");
            Platform.runLater(() -> ventedujour.setText(String.format("%,.2f fcfa", total)));
        }
    }

    public void utilisateurs(ActionEvent event) throws IOException {
        Outils.load(event, "Page Produit", "/com/example/examen_javal3/Utilisateurs.fxml");
    }

    public void categories(ActionEvent event) throws IOException {
        Outils.load(event, "Page Produit", "/com/example/examen_javal3/Categorie.fxml");
    }

    public void produits(ActionEvent event) throws IOException {
        Outils.load(event, "Page Produit", "/com/example/examen_javal3/Produit.fxml");
    }

    public void clients(ActionEvent event) throws IOException {
        Outils.load(event, "Page Produit", "/com/example/examen_javal3/Client.fxml");
    }

    public void ventes(ActionEvent event) throws IOException {
        Outils.load(event, "Page vente", "/com/example/examen_javal3/test1.fxml");
    }

    public void rapport(ActionEvent event) throws IOException {
        Outils.load(event, "Page Produit", "/com/example/examen_javal3/Rapport.fxml");
    }

    public void Deconnexion(ActionEvent event) throws IOException {
        Outils.load(event, "Page d'accueil Admin", "/com/example/examen_javal3/login.fxml");
    }


}