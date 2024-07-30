/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mytunes;

/**
 *
 * @author Robinhood
 */

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PlaylistWindow extends JFrame {
    private JTable songTable;
    private DefaultTableModel tableModel;
    private JButton playButton, stopButton, pauseButton, unpauseButton, nextButton, previousButton;

    public PlaylistWindow(String playlistName) {
        setTitle("Playlist: " + playlistName);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Set up the table
        String[] columns = {"Title", "Artist", "Album", "Year", "Genre", "Comment"};
        tableModel = new DefaultTableModel(columns, 0);
        songTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(songTable);
        add(scrollPane, BorderLayout.CENTER);

        // Set up the buttons
        JPanel buttonPanel = new JPanel();
        playButton = new JButton("Play");
        stopButton = new JButton("Stop");
        pauseButton = new JButton("Pause");
        unpauseButton = new JButton("Unpause");
        nextButton = new JButton("Next");
        previousButton = new JButton("Previous");

        buttonPanel.add(playButton);
        buttonPanel.add(stopButton);
        buttonPanel.add(pauseButton);
        buttonPanel.add(unpauseButton);
        buttonPanel.add(nextButton);
        buttonPanel.add(previousButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Populate table with data from the selected playlist
        populateTableFromPlaylist(playlistName);
    }

    private void populateTableFromPlaylist(String playlistName) {
        tableModel.setRowCount(0);
        List<Object[]> songs = Database.getSongsFromPlaylist(playlistName);
        for (Object[] song : songs) {
            tableModel.addRow(song);
        }
    }
}