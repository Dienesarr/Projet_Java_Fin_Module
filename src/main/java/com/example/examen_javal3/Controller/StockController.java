package com.example.examen_javal3.Controller;

import com.example.examen_javal3.Model.DB;
import com.example.examen_javal3.Model.Produit;
import com.example.examen_javal3.Util.Outils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class StockController implements Initializable {
    @FXML
    private TableView<Produit> tblproduit;

    @FXML
    private TableColumn<Produit, Integer> id;

    @FXML
    private TableColumn<Produit, String> nom;

    @FXML
    private TableColumn<Produit, Integer> quantite;

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        nom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        quantite.setCellValueFactory(new PropertyValueFactory<>("stock"));

        try {
            load();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            errorLabel.setText("Erreur lors du chargement des produits.");
        }
    }

    public void load() throws SQLException, ClassNotFoundException {
        List<Produit> listProduit = new ArrayList<>();
        String sql = "SELECT id,nom,stock " +
                "FROM produits ";
        db.initPrepare(sql);
        ResultSet rs = db.executeSelect();

        if (rs != null) {
            while (rs.next()) {
                Produit produit = new Produit(rs.getInt("id"), rs.getString("nom"), rs.getInt("stock"));
                produit.setId(rs.getInt("id"));
                produit.setNom(rs.getString("nom"));
                produit.setQuantite(rs.getInt("stock"));
                listProduit.add(produit);
            }
        }

        // Remplir la Tableau avec les produits
        ObservableList<Produit> listetable = FXCollections.observableArrayList(listProduit);
        tblproduit.setItems(listetable);

        if (rs != null) {
            rs.close();
        }
    }
    @FXML
    private void retour(ActionEvent event) throws IOException {
        // Rediriger vers la page d'accueil
        Outils.load(event, "Page d'accueil", "/com/example/examen_javal3/AccueilAgent.fxml");
    }

    @FXML
    private void Deconnexion(ActionEvent event) throws IOException {
        // Rediriger vers la page de connexion
        Outils.load(event, "Login", "/com/example/examen_javal3/login.fxml");
    }

}
