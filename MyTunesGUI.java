/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.mytunes;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.dnd.*;
import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.sql.PreparedStatement;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
/**
 *
 * @author Robinhood
 */
public class MyTunesGUI extends javax.swing.JFrame {
    
    private JTable songTable;
    private DefaultTableModel tableModel;
    private JButton playButton, stopButton, pauseButton, unpauseButton, nextButton, previousButton;
    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenuItem openMenuItem, exitMenuItem, addMenuItem, deleteMenuItem;
    private JPopupMenu popupMenu;
    private JMenuItem popupAdd, popupDelete;
    /**
     * Creates new form MyTunesGUI
     */
    public MyTunesGUI() {
        setTitle("MyTunes");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Set up the tabledexr
        String[] columns = {"Title", "Artist", "Album", "Year", "Genre", "Comment"};
        //tableModel = new DefaultTableModel(columns, 0);
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Make all cells editable except for the first four columns (Title, Artist, Album, Year)
                return column == 5; // "Comment" column is editable
            }
        };
        songTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(songTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // Enable drag and drop on the table
        new DropTarget(songTable, new SongDropTargetListener());

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

        // Set up the menu bar
        menuBar = new JMenuBar();
        fileMenu = new JMenu("File");
        openMenuItem = new JMenuItem("Open");
        exitMenuItem = new JMenuItem("Exit");
        addMenuItem = new JMenuItem("Add");
        deleteMenuItem = new JMenuItem("Delete");

        fileMenu.add(openMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(addMenuItem);
        fileMenu.add(deleteMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        // Set up the popup menu
        popupMenu = new JPopupMenu();
        popupAdd = new JMenuItem("Add");
        popupDelete = new JMenuItem("Delete");

        popupMenu.add(popupAdd);
        popupMenu.add(popupDelete);

        songTable.setComponentPopupMenu(popupMenu);

        // Add Action Listeners (example for Play button)
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Code to play song
            }
        });
        
        
        
    popupAdd.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        String title = JOptionPane.showInputDialog("Enter song title:");
        String artist = JOptionPane.showInputDialog("Enter artist name:");
        String album = JOptionPane.showInputDialog("Enter album name:");
        int year = Integer.parseInt(JOptionPane.showInputDialog("Enter year:"));
        String genre = JOptionPane.showInputDialog("Enter genre:");
        String comment = JOptionPane.showInputDialog("Enter comment:");

        Database.insertSong(title, artist, album, year, genre, comment);
        populateTableFromDatabase();
    }
});
    popupDelete.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        int selectedRow = songTable.getSelectedRow();
        if (selectedRow != -1) {
            String title = (String) tableModel.getValueAt(selectedRow, 0);
            String artist = (String) tableModel.getValueAt(selectedRow, 1);
            String album = (String) tableModel.getValueAt(selectedRow, 2);
            Database.deleteSong(title, artist, album);
            tableModel.removeRow(selectedRow);
        } else {
            JOptionPane.showMessageDialog(null, "Please select a song to delete.");
        }
    }
});

    
    addMenuItem.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        // Example: Collect song details from user input (e.g., using a dialog)
        // Collect song details from user input
                String title = JOptionPane.showInputDialog("Enter song title:");
                String artist = JOptionPane.showInputDialog("Enter artist name:");
                String album = JOptionPane.showInputDialog("Enter album name:");
                int year = Integer.parseInt(JOptionPane.showInputDialog("Enter year:"));
                String genre = JOptionPane.showInputDialog("Enter genre:");
                String comment = JOptionPane.showInputDialog("Enter comment:");


        Database.insertSong(title, artist, album, year, genre, comment);

        // Update table model to reflect new data
        //tableModel.addRow(new Object[]{title, artist, album, year, genre, comment});
        populateTableFromDatabase();
        
    }
});
    /*deleteMenuItem.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        int selectedRow = songTable.getSelectedRow();
        if (selectedRow != -1) {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            Database.deleteSong(id);
            tableModel.removeRow(selectedRow);
        } else {
            JOptionPane.showMessageDialog(null, "Please select a song to delete.");
        }
    }
});*/
    deleteMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = songTable.getSelectedRow();
                if (selectedRow != -1) {
                    String title = (String) tableModel.getValueAt(selectedRow, 0);
                    String artist = (String) tableModel.getValueAt(selectedRow, 1);
                    String album = (String) tableModel.getValueAt(selectedRow, 2);
                    Database.deleteSong(title, artist, album);
                    tableModel.removeRow(selectedRow);
                } else {
                    JOptionPane.showMessageDialog(null, "Please select a song to delete.");
                }
            }
        });
     // Add a TableModelListener to detect changes in the table (for Comment updates)
        tableModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if (e.getType() == TableModelEvent.UPDATE) {
                    int row = e.getFirstRow();
                    int column = e.getColumn();
                    if (column == tableModel.findColumn("Comment")) {
                        String updatedComment = (String) tableModel.getValueAt(row, column);
                        String title = (String) tableModel.getValueAt(row, tableModel.findColumn("Title"));
                        String artist = (String) tableModel.getValueAt(row, tableModel.findColumn("Artist"));
                        String album = (String) tableModel.getValueAt(row, tableModel.findColumn("Album"));
                        Database.updateSongComment(title, artist, album, updatedComment);
                    }
                }
            }
        });



        // Similarly, add action listeners for other buttons and menu items
        // Populate table with data from the database
        populateTableFromDatabase();
        
        // Set up a timer to refresh the table every 5 seconds
        Timer refreshTimer = new Timer(5000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                populateTableFromDatabase();
            }
        });
        refreshTimer.start();
        setVisible(true);
    }
    private void populateTableFromDatabase() {
        tableModel.setRowCount(0); // Clear existing data
        List<Object[]> songs = Database.getAllSongs();
        for (Object[] song : songs) {
            tableModel.addRow(song);
        }
        /**try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM songs")) {

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("artist"),
                    rs.getString("album"),
                    rs.getInt("year"),
                    rs.getString("genre"),
                    rs.getString("comment")
                });
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }**/
        
    }
    private class SongDropTargetListener extends DropTargetAdapter {
        @Override
        public void drop(DropTargetDropEvent dtde) {
            try {
                dtde.acceptDrop(DnDConstants.ACTION_COPY);
                List<File> droppedFiles = (List<File>) dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                for (File file : droppedFiles) {
                    if (file.getName().toLowerCase().endsWith(".mp3")) {
                        // Read ID3 tags
                        AudioFile audioFile = AudioFileIO.read(file);
                        Tag tag = audioFile.getTag();

                        // Extract ID3 tag information
                        String title = tag.getFirst(FieldKey.TITLE);
                        String artist = tag.getFirst(FieldKey.ARTIST);
                        String album = tag.getFirst(FieldKey.ALBUM);
                        String year = tag.getFirst(FieldKey.YEAR);
                        String genre = tag.getFirst(FieldKey.GENRE);
                        String comment = tag.getFirst(FieldKey.COMMENT);

                        // Insert metadata into the database
                        insertSongDragnDrop(title, artist, album, year, genre, comment);

                        // Update table model to reflect new data
                        populateTableFromDatabase();
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    
     public static void insertSongDragnDrop(String title, String artist, String album, String year, String genre, String comment) {
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

    private int getLastInsertedId() {
        int lastId = -1;
        String sql = "SELECT last_insert_rowid() AS last_id";
        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                lastId = rs.getInt("last_id");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return lastId;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>                        

    /**
     * @param args the command line arguments
     */
     public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MyTunesGUI();
            }
        });
    }
   
    

    // Variables declaration - do not modify                     
    // End of variables declaration                   
}
