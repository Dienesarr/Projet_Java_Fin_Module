package com.example.examen_javal3.Controller;

import com.example.examen_javal3.Model.Client;
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

public class ClientController implements Initializable {
    DB db;


    {
        try {
            db = DB.getInstance();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    @FXML
    private TableColumn<Client, String> cladresse;

    @FXML
    private TableColumn<Client, String> clemail;

    @FXML
    private TableColumn<Client, Integer> clid;

    @FXML
    private TableColumn<Client, String> clnom;

    @FXML
    private TableColumn<Client, String> cltelephone;
    @FXML
    private TableView<Client> tblclient;
    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtadresse;

    @FXML
    private TextField txtnom;

    @FXML
    private TextField txttelephone;

    @FXML
    protected void Ajoutclient() throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO clients(id, nom, telephone, adresse,email) values (NULL, ?, ?, ?, ?)";
        db.initPrepare(sql);
        db.getPstm().setString(1, txtnom.getText());
        db.getPstm().setString(2, txttelephone.getText());
        db.getPstm().setString(3, txtadresse.getText());
        db.getPstm().setString(4, txtEmail.getText());
        int ok = db.executeMaj();
        if (ok == 1) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Enregistrement");
            alert.setContentText("Client ajoutée avec succès");
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
        cltelephone.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        cladresse.setCellValueFactory(new PropertyValueFactory<>("adresse"));
        clemail.setCellValueFactory(new PropertyValueFactory<>("email"));


        try {
            load();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }


    }

    public void load() throws SQLException, ClassNotFoundException {
        List<Client> listCli = new ArrayList<>();

        String sql = "Select * from clients";
        db.initPrepare(sql);
        ResultSet rs = db.executeSelect();

        while (rs.next()) {
            Client client = new Client();
            client.setId(rs.getInt(1));
            client.setNom(rs.getString(2));
            client.setTelephone(rs.getString(3));
            client.setAdresse(rs.getString(4));
            client.setEmail(rs.getString(5));
            listCli.add(client);
        }
        ObservableList<Client> listetable = FXCollections.observableArrayList();
        for (Client c : listCli) {
            listetable.add(c);
        }
        tblclient.setItems(listetable);

    }

    @FXML
    protected void modifier() throws SQLException, ClassNotFoundException {
        Client selectedClient = tblclient.getSelectionModel().getSelectedItem();
        if (selectedClient != null) {
            String sql = "UPDATE clients SET nom=?, telephone=?, adresse=?, email=? WHERE id=?";
            db.initPrepare(sql);
            db.getPstm().setString(1, txtnom.getText());
            db.getPstm().setString(2, txttelephone.getText());
            db.getPstm().setString(3, txtadresse.getText());
            db.getPstm().setString(4, txtEmail.getText());
            db.getPstm().setInt(5, selectedClient.getId());

            int ok = db.executeMaj();
            if (ok == 1) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Modification");
                alert.setContentText("Client modifié avec succès");
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
            alert.setContentText("Veuillez sélectionner un client à modifier");
            alert.showAndWait();
        }
    }

    @FXML
    protected void supprimer() throws SQLException, ClassNotFoundException {
        Client selectedClient = tblclient.getSelectionModel().getSelectedItem();
        if (selectedClient != null) {
            String sql = "DELETE FROM clients WHERE id=?";
            db.initPrepare(sql);
            db.getPstm().setInt(1, selectedClient.getId());

            int ok = db.executeMaj();
            if (ok == 1) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Suppression");
                alert.setContentText("Client supprimé avec succès");
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
            alert.setContentText("Veuillez sélectionner un client à supprimer");
            alert.showAndWait();
        }
    }

    @FXML
    public void retour(ActionEvent event) throws IOException {
        Outils.load(event, "Page d'acueil", "/com/example/examen_javal3/AccueilAdmin.fxml");
    }

}
