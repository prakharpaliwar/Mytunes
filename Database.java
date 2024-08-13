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
import java.util.ArrayList;
import java.util.List;

public class Database {
    private static final String URL = "jdbc:sqlite:testtune.db";

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
        
        String sqlPlaylists = "CREATE TABLE IF NOT EXISTS playlists (\n"
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + " name TEXT NOT NULL\n"
                + ");";

        String sqlPlaylistSongs = "CREATE TABLE IF NOT EXISTS playlist_songs (\n"
                + " playlist_id INTEGER NOT NULL,\n"
                + " song_id INTEGER NOT NULL,\n"
                + " FOREIGN KEY (playlist_id) REFERENCES playlists(id),\n"
                + " FOREIGN KEY (song_id) REFERENCES songs(id)\n"
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
    public static void updateSongComment(String title, String artist, String album, String updatedComment) {
    String sql = "UPDATE songs SET comment = ? WHERE title = ? AND artist = ? AND album = ?";
    
    try (Connection conn = connect();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.setString(1, updatedComment);
        pstmt.setString(2, title);
        pstmt.setString(3, artist);
        pstmt.setString(4, album);
        
        pstmt.executeUpdate();
        
    } catch (SQLException e) {
        System.out.println(e.getMessage());
    }
}

    public static void deleteSong(String title, String artist, String album) {
        String sql = "DELETE FROM songs WHERE title = ? AND artist = ? AND album = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, title);
            pstmt.setString(2, artist);
            pstmt.setString(3, album);
            pstmt.executeUpdate();


        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public static void insertSongWithQuery(String query) {
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(query);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    
    public static List<Object[]> getAllSongs() {
        List<Object[]> songs = new ArrayList<>();
        String sql = "SELECT * FROM songs";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Object[] song = {
                    rs.getString("title"),
                    rs.getString("artist"),
                    rs.getString("album"),
                    rs.getInt("year"),
                    rs.getString("genre"),
                    rs.getString("comment")
                };
                songs.add(song);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return songs;
    }
    public static boolean songExists(String title, String artist, String album) {
    String sql = "SELECT COUNT(*) FROM songs WHERE title = ? AND artist = ? AND album = ?";
    try (Connection conn = connect();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, title);
        pstmt.setString(2, artist);
        pstmt.setString(3, album);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            return rs.getInt(1) > 0;
        }
    } catch (SQLException e) {
        System.out.println(e.getMessage());
    }
    return false;
}

    public static void createPlaylist(String playlistName) {
        String sql = "INSERT INTO playlists(name) VALUES(?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, playlistName);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public static List<Object[]> getSongsFromPlaylist(String playlistName) {
        List<Object[]> songs = new ArrayList<>();
        String sql = "SELECT s.title, s.artist, s.album, s.year, s.genre, s.comment " +
                     "FROM songs s " +
                     "JOIN playlist_songs ps ON s.id = ps.song_id " +
                     "JOIN playlists p ON ps.playlist_id = p.id " +
                     "WHERE p.name = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, playlistName);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Object[] song = {
                    rs.getString("title"),
                    rs.getString("artist"),
                    rs.getString("album"),
                    rs.getInt("year"),
                    rs.getString("genre"),
                    rs.getString("comment")
                };
                songs.add(song);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return songs;
    }
    
     public static List<String> getAllPlaylists() {
        List<String> playlists = new ArrayList<>();
        String sql = "SELECT name FROM playlists";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                playlists.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return playlists;
    }
    public static void addSongToPlaylist(String title, String artist, String album, String playlistName) {
        String getSongIdSql = "SELECT id FROM songs WHERE title = ? AND artist = ? AND album = ?";
        String getPlaylistIdSql = "SELECT id FROM playlists WHERE name = ?";
        String insertPlaylistSongSql = "INSERT INTO playlist_songs(playlist_id, song_id) VALUES(?, ?)";

        try (Connection conn = connect();
             PreparedStatement getSongIdStmt = conn.prepareStatement(getSongIdSql);
             PreparedStatement getPlaylistIdStmt = conn.prepareStatement(getPlaylistIdSql);
             PreparedStatement insertPlaylistSongStmt = conn.prepareStatement(insertPlaylistSongSql)) {

            // Get song ID
            getSongIdStmt.setString(1, title);
            getSongIdStmt.setString(2, artist);
            getSongIdStmt.setString(3, album);
            ResultSet songIdRs = getSongIdStmt.executeQuery();
            int songId = -1;
            if (songIdRs.next()) {
                songId = songIdRs.getInt("id");
            }

            // Get playlist ID
            getPlaylistIdStmt.setString(1, playlistName);
            ResultSet playlistIdRs = getPlaylistIdStmt.executeQuery();
            int playlistId = -1;
            if (playlistIdRs.next()) {
                playlistId = playlistIdRs.getInt("id");
            }

            // Insert into playlist_songs table
            if (songId != -1 && playlistId != -1) {
                insertPlaylistSongStmt.setInt(1, playlistId);
                insertPlaylistSongStmt.setInt(2, songId);
                insertPlaylistSongStmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public static String getFilePathFromDatabase(String title, String artist, String album) {
        // Implementing the logic to retrieve the file path from the database based on title, artist, and album
        return "C://Library//" + title + ".mp3";
    }
    
    public static void deletePlaylist(String playlistName) {
    String getPlaylistIdSql = "SELECT id FROM playlists WHERE name = ?";
    String deletePlaylistSongsSql = "DELETE FROM playlist_songs WHERE playlist_id = ?";
    String deletePlaylistSql = "DELETE FROM playlists WHERE id = ?";

    try (Connection conn = connect();
         PreparedStatement getPlaylistIdStmt = conn.prepareStatement(getPlaylistIdSql);
         PreparedStatement deletePlaylistSongsStmt = conn.prepareStatement(deletePlaylistSongsSql);
         PreparedStatement deletePlaylistStmt = conn.prepareStatement(deletePlaylistSql)) {

        // Get playlist ID
        getPlaylistIdStmt.setString(1, playlistName);
        ResultSet playlistIdRs = getPlaylistIdStmt.executeQuery();
        int playlistId = -1;
        if (playlistIdRs.next()) {
            playlistId = playlistIdRs.getInt("id");
        }

        // Delete from playlist_songs and playlists table
        if (playlistId != -1) {
            deletePlaylistSongsStmt.setInt(1, playlistId);
            deletePlaylistSongsStmt.executeUpdate();

            deletePlaylistStmt.setInt(1, playlistId);
            deletePlaylistStmt.executeUpdate();
        }
    } catch (SQLException e) {
        System.out.println(e.getMessage());
    }
}

    
     

    public static void main(String[] args) {
        //createNewDatabase();
        //createTables();
       
    }
}
