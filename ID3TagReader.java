/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mytunes;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import java.io.File;
import java.io.FilenameFilter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
/**
 *
 * @author Robinhood
 */
public class ID3TagReader {
    
    
    public static void main(String[] args) {
        String directoryPath = "C:\\Library\\";
        File directory = new File(directoryPath);
        
        // Filter to list only MP3 files
        FilenameFilter mp3Filter = (dir, name) -> name.toLowerCase().endsWith(".mp3");
        File[] mp3Files = directory.listFiles(mp3Filter);

        if (mp3Files != null) {
            for (File mp3File : mp3Files) {
                try {
                    // Read ID3 tags
                    AudioFile audioFile = AudioFileIO.read(mp3File);
                    Tag tag = audioFile.getTag();

                    // Extract ID3 tag information
                    String title = tag.getFirst(FieldKey.TITLE);
                    String artist = tag.getFirst(FieldKey.ARTIST);
                    String album = tag.getFirst(FieldKey.ALBUM);
                    String year = tag.getFirst(FieldKey.YEAR);
                    String genre = tag.getFirst(FieldKey.GENRE);
                    String comment = tag.getFirst(FieldKey.COMMENT);

                    // Insert metadata into the database
                    insertSongIntoDatabase(title, artist, album, year, genre, comment);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("No MP3 files found in the specified directory.");
        }
    
    }
    private static void insertSongIntoDatabase(String title, String artist, String album, String year, String genre, String comment) {
        String sql = "INSERT INTO songs (title, artist, album, year, genre, comment) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.setString(2, artist);
            pstmt.setString(3, album);
            pstmt.setInt(4, year != null ? Integer.parseInt(year) : 0);
            pstmt.setString(5, genre);
            pstmt.setString(6, comment);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
