package com.example.examen_javal3.Model;

import java.sql.*;

public class DB {
    // pour la connexion
    Connection conn = null;
    // pour les resultats des requetes de type Select
    private ResultSet rs;
    //pour les requetes preparees
    private PreparedStatement pstm;
    // pour les resultats de type mise a jour(INSERT,UPDATE,DELETE)
    private int ok;

    // Instance unique de la classe com.example.gestion_voiture.DB (Singleton)
    private static DB instance;



    public DB() throws ClassNotFoundException {
        String url="jdbc:mysql://127.0.0.1:3306/gestionstocksventes";
        String user="root";
        String password="";
        try {
        Class.forName("com.mysql.cj.jdbc.Driver");

            conn=DriverManager.getConnection(url,user,password);


        if(conn!=null)
        {
            System.out.println("connexion reussi");
        }


    }catch (Exception ex)
    {
        System.out.println(ex);
    }
    }

    /**
     * Méthode pour obtenir l'instance unique de la classe com.example.gestion_voiture.DB.
     *
     * @return instance unique de com.example.gestion_voiture.DB
     */
    public static DB getInstance() throws ClassNotFoundException {
        if (instance == null) {
            instance = new DB(); // Créer une nouvelle instance si elle n'existe pas
        }
        return instance;
    }

    public  void Getconnection()
    {
        try

        {



            String url="jdbc:mysql://127.0.0.1:3306/gestionstocksventes";
            String user="root";
            String password="";

            Class.forName("com.mysql.cj.jdbc.Driver");
            conn=DriverManager.getConnection(url,user,password);

            if(conn!=null)
            {
                System.out.println("connexion reussi");
            }


        }catch (Exception ex)
        {
            System.out.println(ex);
        }
    }

    public void initPrepare(String sql)
    {
        try
        {
            //Getconnection();
            pstm=conn.prepareStatement(sql);

        }
        catch (Exception ex)
        {

        }
    }

    public ResultSet executeSelect()
    {
        rs=null;

        try
        {
            rs=pstm.executeQuery();

        }
        catch (Exception ex)
        {

        }
        return rs;
    }

    public int executeMaj()
    {
        try
        {

            ok=pstm.executeUpdate();

        }
        catch (Exception ex)
        {

        }
        return ok;
    }


    public void closeConnection()
    {
        try
        {
            if(conn!=null)
            {
                conn.close();
            }

        }
        catch (Exception ex)
        {

        }
    }


    public PreparedStatement getPstm() {

        return pstm;
    }


    public void setPstm(PreparedStatement pstm) {

        this.pstm = pstm;
    }


    public int getLastInsertId() {
        return 0;
    }

    public ResultSet executeSelect(String query, String clientNom) {
        return null;
    }
    public Connection getConnection() {
        return conn;
    }
}
