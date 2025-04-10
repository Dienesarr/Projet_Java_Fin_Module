package com.example.examen_javal3.Controller;

import com.example.examen_javal3.Model.DB;
import com.example.examen_javal3.Util.Outils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {
    DB db;

    {
        try {
            db = DB.getInstance();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    @FXML
    private Button btnconnexion;

    @FXML
    private Label echec;

    @FXML
    private PasswordField txtpassword;

    @FXML
    private TextField txtusername;

    @FXML
    void seConnecter(ActionEvent event) {
        String username = txtusername.getText();
        String password = txtpassword.getText();
        String hashedPassword = hashPassword(password); // Hacher le mot de passe saisi par l'utilisateur
        String sql = "SELECT * FROM utilisateurs WHERE username = ? AND password = ?";
        db.initPrepare(sql);

        try {
            db.getPstm().setString(1, username);
            db.getPstm().setString(2, hashedPassword); // Utiliser le mot de passe haché pour la comparaison
            ResultSet rs = db.executeSelect();

            if (rs.next()) {
                String role = rs.getString(5);
                if (role.equals("Admin")) {
                    Outils.load(event, "Page d'accueil Admin", "/com/example/examen_javal3/AccueilAdmin.fxml");
                } else if (role.equals("Agent")) {
                    Outils.load(event, "Page d'accueil Agent", "/com/example/examen_javal3/AccueilAgent.fxml");
                } else {
                    showAlert(Alert.AlertType.WARNING, "INFO CONNEXION", "Login ou Password Incorrecte");
                }
            } else {
                showAlert(Alert.AlertType.WARNING, "INFO CONNEXION", "Login ou Password Incorrecte");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
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


    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    public void Annuler(ActionEvent event){
        txtusername.setText("");
        txtpassword.setText("");
    }
    public void motdepasseoublier(ActionEvent event) throws IOException {
        Outils.load(event,"Page d'acueil Admin","/com/example/examen_javal3/ModifPassword.fxml");
    }

}
