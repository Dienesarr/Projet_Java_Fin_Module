package com.example.examen_javal3.Controller;

import com.example.examen_javal3.Model.DB;
import com.example.examen_javal3.Model.Utilisateur;
import com.example.examen_javal3.Util.Outils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class UtilisateurController implements Initializable {
    DB db;

    {
        try {
            db = DB.getInstance();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private TableColumn<Utilisateur, Integer> clid;

    @FXML
    private TableColumn<Utilisateur, String> clnom;

    @FXML
    private TableColumn<Utilisateur, String> clpassword;

    @FXML
    private TableColumn<Utilisateur, String> clrole;

    @FXML
    private TableColumn<Utilisateur, String> clusername;

    @FXML
    private TableView<Utilisateur> tbluser;

    @FXML
    private TextField txtnom;

    @FXML
    private TextField txtpassword;

    @FXML
    private ComboBox<String> txtrole;

    @FXML
    private TextField txtusername;
    @FXML
    public void Annuler(ActionEvent event){
        txtnom.setText("");
        txtusername.setText("");
        txtpassword.setText("");
        txtrole.setValue("");
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erreur lors du hachage du mot de passe", e);
        }
    }


    @FXML
    protected void AjoutUtilisateur () throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO utilisateurs(id, nom,username,password, role) values (NULL, ?, ?, ?, ?)";
        db.initPrepare(sql);
        db.getPstm().setString(1, txtnom.getText());
        db.getPstm().setString(2, txtusername.getText());
        db.getPstm().setString(3, hashPassword(txtpassword.getText()));
        db.getPstm().setString(4, txtrole.getValue());
        int ok = db.executeMaj();
        if (ok == 1) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Enregistrement");
            alert.setContentText("Utilisateur ajoutée avec succès");
            alert.showAndWait();
            load();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Enregistrement");
            alert.setContentText("Vérifiez les données");
            alert.showAndWait();
        }
    }


    public String[] roles = {"Admin","Agent"};

    public void listerole() throws ClassNotFoundException {
        List<String> listR = new ArrayList<>();
        for (String role : roles) {
            listR.add(role);
        }
        ObservableList listeRole = FXCollections.observableArrayList();
    }
    @Override
    public void initialize(URL url, ResourceBundle resourcesBundle) {
        clid.setCellValueFactory(new PropertyValueFactory<>("id"));
        clnom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        clusername.setCellValueFactory(new PropertyValueFactory<>("username"));
        clpassword.setCellValueFactory(new PropertyValueFactory<>("password"));
        clrole.setCellValueFactory(new PropertyValueFactory<>("role"));
        txtrole.getItems().addAll(roles);

        try {
            load();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    public void load() throws SQLException, ClassNotFoundException {
        List<Utilisateur> listUser = new ArrayList<>();
        String sql = "Select * from Utilisateurs";
        db.initPrepare(sql);
        ResultSet rs = db.executeSelect();

        while (rs.next()) {
            Utilisateur user = new Utilisateur();
            user.setId(rs.getInt(1));
            user.setNom(rs.getString(2));
            user.setUsername(rs.getString(3));
            user.setPassword(rs.getString(4));
            user.setRole(rs.getString(5));
            listUser.add(user);
        }
        ObservableList<Utilisateur> listetable = FXCollections.observableArrayList();
        for (Utilisateur p : listUser) {
            listetable.add(p);
        }
        tbluser.setItems(listetable);

    }

    @FXML
    protected void modifier() throws SQLException, ClassNotFoundException {
        Utilisateur selectedUtilisateur = tbluser.getSelectionModel().getSelectedItem();
        if (selectedUtilisateur != null) {
            String sql = "UPDATE utilisateurs SET nom=?,username=?,password=?, role=? WHERE id=?";
            db.initPrepare(sql);
            db.getPstm().setString(1, txtnom.getText());
            db.getPstm().setString(2, txtusername.getText());
            db.getPstm().setString(3, hashPassword(txtpassword.getText()));
            db.getPstm().setString(4, txtrole.getValue());
            db.getPstm().setInt(5, selectedUtilisateur.getId());
            int ok = db.executeMaj();
            if (ok == 1) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Modification");
                alert.setContentText("Utilisateur modifié avec succès");
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
            alert.setContentText("Veuillez sélectionner un utilisateur à modifier");
            alert.showAndWait();
        }
    }

    @FXML
    protected void supprimer() throws SQLException, ClassNotFoundException {
        Utilisateur selectedUtilisateur = tbluser.getSelectionModel().getSelectedItem();
        if (selectedUtilisateur != null) {
            String sql = "DELETE FROM utilisateurs WHERE id=?";
            db.initPrepare(sql);
            db.getPstm().setInt(1, selectedUtilisateur.getId());
            int ok = db.executeMaj();
            if (ok == 1) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Suppression");
                alert.setContentText("Utilisateur supprimé avec succès");
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
            alert.setContentText("Veuillez sélectionner un utilisateur à supprimer");
            alert.showAndWait();
        }
    }
    public void retour(ActionEvent event) throws IOException {
        Outils.load(event, "Page d'accueil Admin", "/com/example/examen_javal3/AccueilAdmin.fxml");
    }
}
