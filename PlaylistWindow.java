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
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
public class PlaylistWindow extends JFrame {
    private String playlistName;
    private JTable songTable;
    private DefaultTableModel tableModel;
    private JButton playButton, stopButton, pauseButton, unpauseButton, nextButton, previousButton;
    private AdvancedPlayer mp3Player;
    private boolean isPaused;
    private int currentSongIndex;
    private File[] songs;
    private CustomAudioDevice audioDevice;
    private JSlider volumeSlider;
    private JMenu fileMenu,controlsMenu;
    private JMenuBar menuBar;

    public PlaylistWindow(String playlistName) {
        this.playlistName = playlistName;
        setTitle("Playlist: " + playlistName);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        
        // Set up the menu bar
        menuBar = new JMenuBar();
        fileMenu = new JMenu("File");
        controlsMenu = new JMenu("Controls");
        menuBar.add(fileMenu);
        menuBar.add(controlsMenu);
        setJMenuBar(menuBar);
        // Set up the table
        String[] columns = {"Title", "Artist", "Album", "Year", "Genre", "Comment"};
        tableModel = new DefaultTableModel(columns, 0);
        songTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(songTable);
        add(scrollPane, BorderLayout.CENTER);
        
        
        // Enable drag and drop on the playlist table
        songTable.setDragEnabled(true);
        songTable.setTransferHandler(new TransferHandler() {
            @Override
            public boolean canImport(TransferSupport support) {
                return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor) || support.isDataFlavorSupported(DataFlavor.stringFlavor);
            }

            @Override
            public boolean importData(TransferSupport support) {
                if (!canImport(support)) {
                    return false;
                }

                try {
                    if (support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                        // Handle file drop
                        List<File> droppedFiles = (List<File>) support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                        for (File file : droppedFiles) {
                            if (file.getName().toLowerCase().endsWith(".mp3")) {
                                addFileToPlaylist(file);
                            }
                        }
                    } else if (support.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                        // Handle internal row drop
                        String data = (String) support.getTransferable().getTransferData(DataFlavor.stringFlavor);
                        String[] songs = data.split("\n");
                        for (String song : songs) {
                            String[] songDetails = song.split("-");
                            //print("hello"+Arrays.toString(songDetails));
                            String title = songDetails[0];
                            String artist = songDetails[1];
                            String album = songDetails[2];
                            int year = Integer.parseInt(songDetails[3]);
                            String genre = songDetails[4];
                            String comment = songDetails[5];

                            // Check if the song is in the library; if not, add it
                            if (!Database.songExists(title, artist, album)) {
                                File file = new File("C://Library//" + title + ".mp3");
                                Database.insertSong(title, artist, album, year, genre, comment);
                            }

                            // Add the song to the playlist
                            Database.addSongToPlaylist(title, artist, album, playlistName);
                        }
                    }

                    // Update the table model
                    populateTableFromPlaylist(playlistName);
                    return true;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return false;
                }
            }
        });
        /*new DropTarget(songTable, new PlaylistDropTargetListener());
        songTable.setTransferHandler(new TransferHandler() {
            @Override
            public boolean canImport(TransferSupport support) {
                return support.isDataFlavorSupported(DataFlavor.stringFlavor);
            }

            @Override
            public boolean importData(TransferSupport support) {
                if (!canImport(support)) {
                    return false;
                }

                try {
                    Transferable transferable = support.getTransferable();
                    String data = (String) transferable.getTransferData(DataFlavor.stringFlavor);
                    String[] songs = data.split("\n");

                    for (String song : songs) {
                        String[] songDetails = song.split(" - ");
                        String title = songDetails[0];
                        String artist = songDetails[1];
                        String album = songDetails[2];

                        // Check if the song is in the library; if not, add it
                        if (!Database.songExists(title, artist, album)) {
                            // Assume you have a method to retrieve a file path for the song
                            File file = new File("C://Library//" + title + ".mp3");
                            // Add the song to the library (this method needs to be implemented)
                            Database.insertSong(title, artist, album, 0, "Unknown", "");
                        }

                        // Add the song to the playlist
                        Database.addSongToPlaylist(title, artist, album, playlistName);
                    }

                    // Update the table model
                    populateTableFromPlaylist(playlistName);

                    return true;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return false;
                }
            }
        });
        */
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
        
        
        // Add window listener to stop song and close window
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                stopSong(); // Stop the song
                dispose();  // Close the window
            }
        });
         // Add action listeners
        playButton.addActionListener(e -> playSong());
        stopButton.addActionListener(e -> stopSong());
        pauseButton.addActionListener(e -> pauseSong());
        unpauseButton.addActionListener(e -> unpauseSong());
        nextButton.addActionListener(e -> playNextSong());
        previousButton.addActionListener(e -> playPreviousSong());
           // Add volume slider
        volumeSlider = new JSlider(0, 100, 100); // Range 0-100, starting at 100 (full volume)
        volumeSlider.setMajorTickSpacing(10);
        volumeSlider.setPaintTicks(true);
        volumeSlider.setPaintLabels(true);
        volumeSlider.addChangeListener(e -> {
                int sliderValue = volumeSlider.getValue();
                float volume = sliderValue / 100.0f; // Convert 0-100 to 0.0-1.0
                setVolume(volume);
        });
        buttonPanel.add(volumeSlider); // Add slider to your button panel or wherever appropriate
        

        // Populate table with data from the selected playlist
        populateTableFromPlaylist(playlistName);
    }
     private void playSong() {
        if (mp3Player != null && isPaused) {
            playSongFromIndex(currentSongIndex);
            isPaused = false;
        } else {
            int row = songTable.getSelectedRow();
            if (row >= 0) {
                currentSongIndex = row;
                playSongFromIndex(currentSongIndex);
            }
        }
    }

    private void stopSong() {
        if (mp3Player != null) {
            mp3Player.close();
            mp3Player = null;
        }
    }

    private void pauseSong() {
        stopSong();
        isPaused = true;
    }

    private void unpauseSong() {
        playSong();
    }

    private void playNextSong() {
       int selectedRow = songTable.getSelectedRow();
        if (selectedRow != -1 && selectedRow < tableModel.getRowCount() - 1) {
            selectedRow++;
        } else {
            selectedRow = 0;
        }
        songTable.setRowSelectionInterval(selectedRow, selectedRow);
        playSong();
    }

    private void playPreviousSong() {
        int selectedRow = songTable.getSelectedRow();
        if (selectedRow > 0) {
            selectedRow--;
        } else {
            selectedRow = tableModel.getRowCount() - 1;
        }
        songTable.setRowSelectionInterval(selectedRow, selectedRow);
        playSong();
    }

    private void playSongFromIndex(int index) {
        if (index >= 0 && index < songs.length) {
            File songFile = songs[index];
            try {
                if (mp3Player != null) {
                    mp3Player.close();
                }
                FileInputStream fileInputStream = new FileInputStream(songFile);
                mp3Player = new AdvancedPlayer(fileInputStream);
                new Thread(() -> {
                    try {
                        mp3Player.play();
                    } catch (JavaLayerException e) {
                        e.printStackTrace();
                    }
                }).start();
                isPaused = false;
            } catch (FileNotFoundException | JavaLayerException ex) {
                ex.printStackTrace();
            }
        }
    }
    private void setVolume(float volume) {
    if (audioDevice != null) {
        try {
            audioDevice.setVolume(volume); // Ensure your `audioDevice` supports volume changes
        } catch (Exception e) {
            e.printStackTrace();
        }
     }
    }

     private void increaseVolume() {
        int currentValue = volumeSlider.getValue();
        if (currentValue < 100) {
            volumeSlider.setValue(currentValue + 5); // Increase by 5
        }
    }

    private void decreaseVolume() {
        int currentValue = volumeSlider.getValue();
        if (currentValue > 0) {
            volumeSlider.setValue(currentValue - 5); // Decrease by 5
        }
    }
    /*
    private class PlaylistDropTargetListener extends DropTargetAdapter {
        @Override
        public void drop(DropTargetDropEvent dtde) {
            try {
                dtde.acceptDrop(DnDConstants.ACTION_COPY);
                List<File> droppedFiles = (List<File>) dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                for (File file : droppedFiles) {
                    if (file.getName().toLowerCase().endsWith(".mp3")) {
                        AudioFile audioFile = AudioFileIO.read(file);
                        Tag tag = audioFile.getTag();

                        // Extract ID3 tag information
                        String title = tag.getFirst(FieldKey.TITLE);
                        String artist = tag.getFirst(FieldKey.ARTIST);
                        String album = tag.getFirst(FieldKey.ALBUM);
                        String year = tag.getFirst(FieldKey.YEAR);
                        String genre = tag.getFirst(FieldKey.GENRE);
                        String comment = tag.getFirst(FieldKey.COMMENT);

                        if (!Database.songExists(title, artist, album)) {
                            Database.insertSong(title, artist, album, Integer.parseInt(year), genre, comment);
                        }
                        Database.addSongToPlaylist(title, artist, album, playlistName);
                    }
                }
                // Refresh the playlist table
                populateTableFromPlaylist(playlistName);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }*/

    /*private void populateTableFromPlaylist(String playlistName) {
        tableModel.setRowCount(0);
        List<Object[]> songs = Database.getSongsFromPlaylist(playlistName);
        for (Object[] song : songs) {
            tableModel.addRow(song);
        }
    }*/
    private File getFileFromTitle(String title) {
        return new File("C://Library//" + title + ".mp3"); // Replace with actual file path
    }
    private void addFileToPlaylist(File file) {
        try {
            AudioFile audioFile = AudioFileIO.read(file);
            Tag tag = audioFile.getTag();

            // Extract ID3 tag information
            String title = tag.getFirst(FieldKey.TITLE);
            String artist = tag.getFirst(FieldKey.ARTIST);
            String album = tag.getFirst(FieldKey.ALBUM);
            String year = tag.getFirst(FieldKey.YEAR);
            String genre = tag.getFirst(FieldKey.GENRE);
            String comment = tag.getFirst(FieldKey.COMMENT);

            if (!Database.songExists(title, artist, album)) {
                Database.insertSong(title, artist, album, Integer.parseInt(year), genre, comment);
            }
            Database.addSongToPlaylist(title, artist, album, playlistName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /*
    private class PlaylistDropTargetListener extends DropTargetAdapter {
        @Override
        public void drop(DropTargetDropEvent dtde) {
            try {
                dtde.acceptDrop(DnDConstants.ACTION_COPY);
                
                // Handle file drop
                if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                    List<File> droppedFiles = (List<File>) dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    
                    for (File file : droppedFiles) {
                        if (file.getName().endsWith(".mp3")) {
                            // Extract song details from the file
                            String title = file.getName().replace(".mp3", "");
                            String artist = "Unknown Artist"; // You might want to extract this from ID3 tags
                            String album = "Unknown Album";   // You might want to extract this from ID3 tags
                            int year = 0;                    // You might want to extract this from ID3 tags
                            String genre = "Unknown Genre";  // You might want to extract this from ID3 tags
                            String comment = "";             // You might want to extract this from ID3 tags
                            
                            // Insert the song into the database
                            if (!Database.songExists(title, artist, album)) {
                                Database.insertSong(title, artist, album, year, genre, comment);
                            }
                            Database.addSongToPlaylist(title, artist, album, playlistName);
                            
                            // Refresh the table
                            populateTableFromPlaylist(playlistName);
                        }
                    }
                } else {
                    dtde.rejectDrop();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }*/
     
    private void populateTableFromPlaylist(String playlistName) {
        tableModel.setRowCount(0);
        List<Object[]> songsList = Database.getSongsFromPlaylist(playlistName);
        songs = new File[songsList.size()];
        for (int i = 0; i < songsList.size(); i++) {
            Object[] song = songsList.get(i);
            tableModel.addRow(song);
            songs[i] = getFileFromTitle((String) song[0]);
        }
        currentSongIndex = 0;
    }
    
}
