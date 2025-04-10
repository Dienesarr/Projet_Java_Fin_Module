package com.example.examen_javal3.Controller;

import com.example.examen_javal3.Model.DB;
import com.example.examen_javal3.Util.Outils;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.event.ActionEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RapportController {

    public void generateReportPdf(ActionEvent actionEvent) {
        // Créer un FileChooser pour permettre à l'utilisateur de choisir l'emplacement du fichier
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le rapport PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf"));
        fileChooser.setInitialFileName("RapportVentes.pdf"); // Nom par défaut du fichier

        // Ouvrir la boîte de dialogue pour enregistrer le fichier
        Stage stage = (Stage) ((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        // Vérifier si l'utilisateur a choisi un fichier
        if (file != null) {
            Document document = new Document();

            try {
                // Création du fichier PDF à l'emplacement choisi
                String outputPath = file.getAbsolutePath();
                PdfWriter.getInstance(document, new FileOutputStream(outputPath));
                document.open();
                document.add(new Paragraph("Rapport des Ventes\n\n"));

                // Utilisation de la classe DB pour récupérer les données
                DB db = DB.getInstance();
                db.initPrepare("SELECT v.id, v.date_vente, v.total, c.nom AS client, u.nom AS agent " +
                        "FROM ventes v " +
                        "JOIN clients c ON v.client_id = c.id " +
                        "JOIN utilisateurs u ON v.agent_id = u.id");
                ResultSet rs = db.executeSelect();

                // Ajout des données récupérées dans le PDF
                while (rs.next()) {
                    int idVente = rs.getInt("id");
                    String dateVente = rs.getString("date_vente");
                    double totalVente = rs.getDouble("total");
                    String nomClient = rs.getString("client");
                    String nomAgent = rs.getString("agent");

                    // Formatage des données pour le PDF
                    String ligne = String.format(
                            "Vente ID : %d, Date : %s, Total : %.2f CFA, Client : %s, Agent : %s",
                            idVente, dateVente, totalVente, nomClient, nomAgent
                    );
                    document.add(new Paragraph(ligne));
                }

                // Fermeture du ResultSet
                rs.close();

                System.out.println("Le fichier PDF a été généré avec succès : " + outputPath);

            } catch (DocumentException | IOException | SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                document.close();
            }
        } else {
            System.out.println("Aucun fichier sélectionné.");
        }
    }

    public void generateReportExcel(ActionEvent actionEvent) {
        // Créer un FileChooser pour permettre à l'utilisateur de choisir l'emplacement du fichier
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le rapport Excel");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers Excel", "*.xlsx"));
        fileChooser.setInitialFileName("RapportVentes.xlsx"); // Nom par défaut du fichier

        // Ouvrir la boîte de dialogue pour enregistrer le fichier
        Stage stage = (Stage) ((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        // Vérifier si l'utilisateur a choisi un fichier
        if (file != null) {
            try (Workbook workbook = new XSSFWorkbook()) {
                // Créer une feuille Excel
                Sheet sheet = workbook.createSheet("Rapport des Ventes");

                // Créer l'en-tête de la feuille
                Row headerRow = sheet.createRow(0);
                String[] columns = {"ID Vente", "Date Vente", "Total", "Client", "Agent"};
                for (int i = 0; i < columns.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(columns[i]);
                }

                // Utilisation de la classe DB pour récupérer les données
                DB db = DB.getInstance();
                db.initPrepare("SELECT v.id, v.date_vente, v.total, c.nom AS client, u.nom AS agent " +
                        "FROM ventes v " +
                        "JOIN clients c ON v.client_id = c.id " +
                        "JOIN utilisateurs u ON v.agent_id = u.id");
                ResultSet rs = db.executeSelect();

                // Ajout des données récupérées dans la feuille Excel
                int rowNum = 1;
                while (rs.next()) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(rs.getInt("id"));
                    row.createCell(1).setCellValue(rs.getString("date_vente"));
                    row.createCell(2).setCellValue(rs.getDouble("total"));
                    row.createCell(3).setCellValue(rs.getString("client"));
                    row.createCell(4).setCellValue(rs.getString("agent"));
                }

                // Fermeture du ResultSet
                rs.close();

                // Ajuster la largeur des colonnes automatiquement
                for (int i = 0; i < columns.length; i++) {
                    sheet.autoSizeColumn(i);
                }

                // Enregistrer le fichier Excel
                try (FileOutputStream fileOut = new FileOutputStream(file)) {
                    workbook.write(fileOut);
                }

                System.out.println("Le fichier Excel a été généré avec succès : " + file.getAbsolutePath());

            } catch (IOException | SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Aucun fichier sélectionné.");
        }
    }
    public void retour(ActionEvent event) throws IOException {
        Outils.load(event, "Page d'accueil Admin", "/com/example/examen_javal3/AccueilAdmin.fxml");
    }
}