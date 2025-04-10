package com.example.examen_javal3.Controller;

import com.example.examen_javal3.Model.Categorie;
import com.example.examen_javal3.Model.DB;
import com.example.examen_javal3.Util.Outils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class CategorieController implements Initializable {
    DB db;

    {
        try {
            db = DB.getInstance();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private TableColumn<Categorie, Integer> clid;

    @FXML
    private TableColumn<Categorie, String> clnom;

    @FXML
    private TableView<Categorie> tbluser;

    @FXML
    private TextField txtnom;

    @FXML
    protected void AjoutCategories () throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO categories (id, nom) values (NULL, ?)";
        db.initPrepare(sql);
        db.getPstm().setString(1, txtnom.getText());
        int ok = db.executeMaj();
        if (ok == 1) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Enregistrement");
            alert.setContentText("Categorie ajoutée avec succès");
            alert.showAndWait();
            load();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Enregistrement");
            alert.setContentText("Vérifiez les données");
            alert.showAndWait();
        }
    }



    @Override
    public void initialize(URL url, ResourceBundle resourcesBundle) {
        clid.setCellValueFactory(new PropertyValueFactory<>("id"));
        clnom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        try {
            load();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    public void load() throws SQLException, ClassNotFoundException {
        List<Categorie> listCat = new ArrayList<>();
        String sql = "Select * from categories";
        db.initPrepare(sql);
        ResultSet rs = db.executeSelect();

        while (rs.next()) {
            Categorie categ = new Categorie();
            categ.setId(rs.getInt(1));
            categ.setNom(rs.getString(2));
            listCat.add(categ);
        }
        ObservableList<Categorie> listetable = FXCollections.observableArrayList();
        for (Categorie c : listCat) {
            listetable.add(c);
        }
        tbluser.setItems(listetable);

    }

    @FXML
    protected void modifier() throws SQLException, ClassNotFoundException {
       Categorie selectedCategorie = tbluser.getSelectionModel().getSelectedItem();
        if (selectedCategorie != null) {
            String sql = "UPDATE categories SET nom=? WHERE id=?";
            db.initPrepare(sql);
            db.getPstm().setString(1, txtnom.getText());
            db.getPstm().setInt(2, selectedCategorie.getId());
            int ok = db.executeMaj();
            if (ok == 1) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Modification");
                alert.setContentText("Catergorie modifié avec succès");
                alert.showAndWait();
                load();
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Modification");
                alert.setContentText("Vérifiez les données");
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Modification");
            alert.setContentText("Veuillez sélectionner une categorie à modifier");
            alert.showAndWait();
        }
    }

    @FXML
    protected void supprimer() throws SQLException, ClassNotFoundException {
        Categorie selectedCategorie = tbluser.getSelectionModel().getSelectedItem();
        if (selectedCategorie != null) {
            String sql = "DELETE FROM categories WHERE id=?";
            db.initPrepare(sql);
            db.getPstm().setInt(1, selectedCategorie.getId());
            int ok = db.executeMaj();
            if (ok == 1) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Suppression");
                alert.setContentText("catergorie supprimé avec succès");
                alert.showAndWait();
                load();
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Suppression");
                alert.setContentText("Vérifiez les données");
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Suppression");
            alert.setContentText("Veuillez sélectionner une categorie à supprimer");
            alert.showAndWait();
        }
    }
    public void retour(ActionEvent event) throws IOException {
        Outils.load(event, "Page d'accueil Admin", "/com/example/examen_javal3/AccueilAdmin.fxml");
    }
}
