package com.example.examen_javal3.Controller;
import com.example.examen_javal3.Model.DB;
import com.example.examen_javal3.Model.Produit;
import com.example.examen_javal3.Util.Outils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ProduitController implements Initializable {

    DB db;


    {
        try {
            db = DB.getInstance();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private File fichierSelectionne;
    @FXML
    private TableColumn<Produit, String> clcategorie;

    @FXML
    private TableColumn<Produit, Integer> clid;

    @FXML
    private TableColumn<Produit, Blob> climage;

    @FXML
    private TableColumn<Produit, String> clnom;

    @FXML
    private TableColumn<Produit, Double> clprix_achat;

    @FXML
    private TableColumn<Produit, Double> clprix_vente;

    @FXML
    private TableColumn<Produit, String> clreference;

    @FXML
    private TableColumn<Produit, Integer> clstock;

    @FXML
    private ImageView img;

    @FXML
    private TableView<Produit> tblprod;

    @FXML
    private ComboBox<Integer> txtid_categorie;

    @FXML
    private TextField txtnom;

    @FXML
    private TextField txtprix_achat;

    @FXML
    private TextField txtprix_vente;

    @FXML
    private TextField txtreference;

    @FXML
    private TextField txtstock;


    @FXML
    protected void importerImage() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Fichiers d'image", "*.png", "*.jpg", "*.jpeg")
        );

        Stage stage = (Stage) img.getScene().getWindow();
        File fichierSelectionne = fileChooser.showOpenDialog(stage);

        if (fichierSelectionne != null) {
            Image image = new Image(fichierSelectionne.toURI().toString());
            img.setImage(image);
            this.fichierSelectionne = fichierSelectionne;
        } else {
            showAlert(Alert.AlertType.WARNING, "Fichier non sélectionné", "Aucun fichier sélectionné.");
        }
    }

    @FXML
    protected void Ajoutprod() throws SQLException, ClassNotFoundException, IOException {

            if (txtnom.getText().isEmpty() || txtreference.getText().isEmpty() || txtprix_achat.getText().isEmpty() || txtprix_vente.getText().isEmpty() || txtstock.getText().isEmpty() || txtid_categorie.getValue() == null) {
                showAlert(Alert.AlertType.WARNING, "Champs manquants", "Veuillez remplir tous les champs obligatoires.");
                return;
            }

            String sql = "INSERT INTO produits(id, nom, reference, image, prix_achat, prix_vente, stock, categorie_id) VALUES (NULL, ?, ?, ?, ?, ?, ?, ?)";
            db.initPrepare(sql);
            db.getPstm().setString(1, txtnom.getText());
            db.getPstm().setString(2, txtreference.getText());

            try (FileInputStream fis = new FileInputStream(this.fichierSelectionne)) {
                db.getPstm().setBinaryStream(3, fis, (int) this.fichierSelectionne.length());
                db.getPstm().setDouble(4, Double.parseDouble(txtprix_achat.getText()));
                db.getPstm().setDouble(5, Double.parseDouble(txtprix_vente.getText()));
                db.getPstm().setInt(6, Integer.parseInt(txtstock.getText()));
                Integer categorieSelectionnee = txtid_categorie.getValue();
                db.getPstm().setInt(7, categorieSelectionnee);

                int ok = db.executeMaj();

                if (ok == 1) {
                    showAlert(Alert.AlertType.INFORMATION, "Enregistrement", "Produit ajouté avec succès");
                    load();
                } else {
                    showAlert(Alert.AlertType.WARNING, "Enregistrement", "Erreur inconnue lors de l'ajout du produit. Vérifiez les données et réessayez.");
                }
            } catch (FileNotFoundException e) {
                showAlert(Alert.AlertType.ERROR, "Fichier non trouvé", "Le fichier sélectionné est introuvable.");
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur SQL", "Erreur lors de l'exécution de la requête SQL : " + e.getMessage());
            }
     }


    @Override
    public void initialize(URL url, ResourceBundle resourcesBundle) {
        clid.setCellValueFactory(new PropertyValueFactory<>("id"));
        clnom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        clreference.setCellValueFactory(new PropertyValueFactory<>("reference"));
        climage.setCellValueFactory(new PropertyValueFactory<>("image"));
        clprix_achat.setCellValueFactory(new PropertyValueFactory<>("prix_achat"));
        clprix_vente.setCellValueFactory(new PropertyValueFactory<>("prix_vente"));
        clstock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        clcategorie.setCellValueFactory(new PropertyValueFactory<>("categorieNom"));

        try {
            load();
            loadCategories();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadCategories() throws SQLException {
        db.initPrepare("SELECT id FROM categories");
        ResultSet rs = db.executeSelect();
        ObservableList<Integer> list_cat = FXCollections.observableArrayList();

        try {
            while (rs.next()) {
                list_cat.add(rs.getInt("id"));
            }
            txtid_categorie.setItems(list_cat);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (db.getPstm() != null) {
                try {
                    db.getPstm().close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void load() throws SQLException, ClassNotFoundException {
        List<Produit> listprod = new ArrayList<>();

        String sql = "SELECT p.id, p.nom, p.reference, p.image, p.prix_achat, p.prix_vente, p.stock, c.nom AS categorieNom " +
                "FROM produits p " +
                "JOIN categories c ON p.categorie_id = c.id";
        db.initPrepare(sql);
        ResultSet rs = db.executeSelect();

        while (rs.next()) {
            Produit prod = new Produit(rs.getInt("id"), rs.getString("nom"), rs.getDouble("prix_vente"), rs.getInt("stock"));
            prod.setId(rs.getInt("id"));
            prod.setNom(rs.getString("nom"));
            prod.setReference(rs.getString("reference"));
            prod.setImage(rs.getBlob("image"));
            prod.setPrix_achat(rs.getDouble("prix_achat"));
            prod.setPrix_vente(rs.getDouble("prix_vente"));
            prod.setStock(rs.getInt("stock"));
            prod.setCategorieNom(rs.getString("categorieNom"));
            listprod.add(prod);
        }

        ObservableList<Produit> listetable = FXCollections.observableArrayList(listprod);
        tblprod.setItems(listetable);
    }

    @FXML
    protected void supprimerProduit() throws SQLException, ClassNotFoundException {
        Produit selectedProduit = tblprod.getSelectionModel().getSelectedItem();
        if (selectedProduit != null) {
            String sql = "DELETE FROM produits WHERE id = ?";
            db.initPrepare(sql);
            db.getPstm().setInt(1, selectedProduit.getId());
            int ok = db.executeMaj();
            if (ok == 1) {
                showAlert(Alert.AlertType.INFORMATION, "Suppression", "Produit supprimé avec succès");
                load(); // Recharge les données
            } else {
                showAlert(Alert.AlertType.WARNING, "Suppression", "Échec de la suppression du produit");
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Aucun produit sélectionné", "Veuillez sélectionner un produit à supprimer");
        }
    }

    @FXML
    protected void modifierProduit() throws SQLException, ClassNotFoundException, IOException {
        Produit selectedProduit = tblprod.getSelectionModel().getSelectedItem();
        if (selectedProduit != null) {
            if (txtnom.getText().isEmpty() || txtreference.getText().isEmpty() || txtprix_achat.getText().isEmpty() ||
                    txtprix_vente.getText().isEmpty() || txtstock.getText().isEmpty() || txtid_categorie.getValue() == null ||
                    fichierSelectionne == null) {
                showAlert(Alert.AlertType.WARNING, "Champs manquants", "Veuillez remplir tous les champs obligatoires et sélectionner une image.");
                return;
            }

            String sql = "UPDATE produits SET nom = ?, reference = ?, image = ?, prix_achat = ?, prix_vente = ?, stock = ?, categorie_id = ? WHERE id = ?";
            db.initPrepare(sql);
            db.getPstm().setString(1, txtnom.getText());
            db.getPstm().setString(2, txtreference.getText());

            try (FileInputStream fis = new FileInputStream(this.fichierSelectionne)) {
                db.getPstm().setBinaryStream(3, fis, (int) this.fichierSelectionne.length());
                db.getPstm().setDouble(4, Double.parseDouble(txtprix_achat.getText()));
                db.getPstm().setDouble(5, Double.parseDouble(txtprix_vente.getText()));
                db.getPstm().setInt(6, Integer.parseInt(txtstock.getText()));

                Integer selectedCategorie = txtid_categorie.getValue();
                db.getPstm().setInt(7, selectedCategorie);
                db.getPstm().setInt(8, selectedProduit.getId());

                int ok = db.executeMaj();

                if (ok == 1) {
                    showAlert(Alert.AlertType.INFORMATION, "Modification", "Produit modifié avec succès");
                    load(); // Reload the data
                } else {
                    showAlert(Alert.AlertType.WARNING, "Modification", "Vérifiez les données");
                }
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Aucun produit sélectionné", "Veuillez sélectionner un produit à modifier");
        }
    }



    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }






    public void retour(ActionEvent event) throws IOException {
    Outils.load(event, "Page d'accueil Admin", "/com/example/examen_javal3/AccueilAdmin.fxml");
}

}
