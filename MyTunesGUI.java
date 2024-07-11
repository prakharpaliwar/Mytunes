/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.mytunes;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
        String[] columns = {"ID","Title", "Artist", "Album", "Year", "Genre", "Comment"};
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
    deleteMenuItem.addActionListener(new ActionListener() {
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
});
    



        // Similarly, add action listeners for other buttons and menu items
        // Populate table with data from the database
        populateTableFromDatabase();
        setVisible(true);
    }
    private void populateTableFromDatabase() {
        tableModel.setRowCount(0); // Clear existing data
        try (Connection conn = Database.connect();
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
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
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
    }// </editor-fold>//GEN-END:initComponents

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
   
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
