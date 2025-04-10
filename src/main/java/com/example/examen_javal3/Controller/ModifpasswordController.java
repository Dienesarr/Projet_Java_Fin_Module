package com.example.examen_javal3.Controller;

import com.example.examen_javal3.Model.DB;
import com.example.examen_javal3.Util.Outils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ModifpasswordController {

    @FXML
    private Label echec;

    @FXML
    private PasswordField txtnewpassword;

    @FXML
    private PasswordField txtpassword;

    @FXML
    private TextField txtusername;

    private DB db;

    {
        try {
            db = DB.getInstance();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void modifpassword(ActionEvent event) {
        String username = txtusername.getText();
        String oldPassword = txtpassword.getText();
        String newPassword = txtnewpassword.getText();

        if (username.isEmpty() || oldPassword.isEmpty() || newPassword.isEmpty()) {
            echec.setText("Veuillez remplir tous les champs.");
            return;
        }

        try {
            // Vérifier si l'utilisateur existe
            String query = "SELECT password FROM utilisateurs WHERE username = ?";
            db.initPrepare(query);
            db.getPstm().setString(1, username);
            ResultSet rs = db.executeSelect();

            if (rs.next()) {
                String storedHashedPassword = rs.getString("password");
                String hashedOldPassword = hashPassword(oldPassword);

                // Vérifier si l'ancien mot de passe est correct
                if (storedHashedPassword.equals(hashedOldPassword)) {
                    // Mettre à jour le mot de passe
                    String hashedNewPassword = hashPassword(newPassword);
                    String updateQuery = "UPDATE utilisateurs SET password = ? WHERE username = ?";
                    db.initPrepare(updateQuery);
                    db.getPstm().setString(1, hashedNewPassword);
                    db.getPstm().setString(2, username);
                    db.executeMaj();

                    echec.setStyle("-fx-text-fill: green;");
                    echec.setText("Mot de passe modifié avec succès !");
                    Outils.load(event, "Login","/com/example/examen_javal3/login.fxml");
                } else {
                    echec.setStyle("-fx-text-fill: red;");
                    echec.setText("Ancien mot de passe incorrect.");
                }
            } else {
                echec.setStyle("-fx-text-fill: red;");
                echec.setText("Utilisateur non trouvé.");
            }
        } catch (SQLException e) {
            echec.setStyle("-fx-text-fill: red;");
            echec.setText("Erreur de base de données : " + e.getMessage());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
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
    public void Annuler(ActionEvent event) throws IOException {
        Outils.load(event, "Page d'accueil Admin", "/com/example/examen_javal3/login.fxml");
    }
}