/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mytunes;

/**
 *
 * @author Robinhood
 */
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    private static final String URL = "jdbc:sqlite:mytunes.db";

    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public static void createNewDatabase() {
        try (Connection conn = connect()) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void createTables() {
        String sql = "CREATE TABLE IF NOT EXISTS songs (\n"
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + " title TEXT NOT NULL,\n"
                + " artist TEXT NOT NULL,\n"
                + " album TEXT NOT NULL,\n"
                + " year INTEGER,\n"
                + " genre TEXT,\n"
                + " comment TEXT\n"
                + ");";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Tables have been created.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
     public static void insertSong(String title, String artist, String album, int year, String genre, String comment) {
        String sql = "INSERT INTO songs(title, artist, album, year, genre, comment) VALUES(?,?,?,?,?,?)";

        try (Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.setString(2, artist);
            pstmt.setString(3, album);
            pstmt.setInt(4, year);
            pstmt.setString(5, genre);
            pstmt.setString(6, comment);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public static void selectAllSongs() {
        String sql = "SELECT id, title, artist, album, year, genre, comment FROM songs";

        try (Connection conn = connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)) {

            while (rs.next()) {
                System.out.println(rs.getInt("id") +  "\t" +
                                   rs.getString("title") + "\t" +
                                   rs.getString("artist") + "\t" +
                                   rs.getString("album") + "\t" +
                                   rs.getInt("year") + "\t" +
                                   rs.getString("genre") + "\t" +
                                   rs.getString("comment"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    } 
    public static void updateSong(int id, String title, String artist, String album, int year, String genre, String comment) {
        String sql = "UPDATE songs SET title = ? , "
                + "artist = ? , "
                + "album = ? , "
                + "year = ? , "
                + "genre = ? , "
                + "comment = ? "
                + "WHERE id = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, title);
            pstmt.setString(2, artist);
            pstmt.setString(3, album);
            pstmt.setInt(4, year);
            pstmt.setString(5, genre);
            pstmt.setString(6, comment);
            pstmt.setInt(7, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public static void deleteSong(int id) {
        String sql = "DELETE FROM songs WHERE id = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    
     

    public static void main(String[] args) {
        createNewDatabase();
        createTables();
    }
}
