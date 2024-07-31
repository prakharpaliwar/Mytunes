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
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.sql.PreparedStatement;
import java.util.Enumeration;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
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
    private JMenuItem openMenuItem, exitMenuItem, addMenuItem, deleteMenuItem, createPlaylistMenuItem;
    private JPopupMenu popupMenu;
    private JMenuItem popupAdd, popupDelete,addToPlaylistMenuItem;;
    private JPanel leftPanel;
    private JTree libraryTree;
    private JTree playlistTree;
    private JTree tree;
    private DefaultTreeModel treemodel;
    private DefaultTreeModel treemodel1;
    private DefaultMutableTreeNode playlistRootNode;
    private DefaultTreeModel playlistTreeModel;
    private JPopupMenu playlistPopupMenu;
    private JMenuItem openInNewWindowMenuItem;
    private String currentPlaylist;
     
    /**
     * Creates new form MyTunesGUI
     */
    public MyTunesGUI() {
        setTitle("MyTunes");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null); 
        
        
        setLayout(new BorderLayout());

        // Create and add the left panel
        leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setPreferredSize(new Dimension(getWidth() / 8, getHeight()));
        leftPanel.setBackground(Color.LIGHT_GRAY);
        add(leftPanel, BorderLayout.WEST);

        // Create the "Library" tree
        DefaultMutableTreeNode libraryNode = new DefaultMutableTreeNode("Library") {
            @Override
            public boolean isLeaf() {
                return true; // Make this node non-expandable and non-collapsible
            }
        };
        libraryTree = new JTree(libraryNode);
        libraryTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        libraryTree.setRootVisible(true);
        libraryTree.addMouseListener(new java.awt.event.MouseAdapter() {
        @Override
        public void mousePressed(java.awt.event.MouseEvent evt) {
            int selRow = libraryTree.getRowForLocation(evt.getX(), evt.getY());
            TreePath selPath = libraryTree.getPathForLocation(evt.getX(), evt.getY());
            if(selRow != -1) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) selPath.getLastPathComponent();
                if (selectedNode.toString().equals("Library")) {
                    System.out.println("Library Node Selected");
                    // Handle library selection
                    currentPlaylist = null; // Reset current playlist
                    populateTableFromDatabase();
                }
            }
        }
        });


      
        JScrollPane libraryScrollPane = new JScrollPane(libraryTree);
        libraryScrollPane.setBorder(BorderFactory.createEmptyBorder());
        //libraryScrollPane.setPreferredSize(new Dimension(getWidth() / 8, getHeight() / 2));
        leftPanel.add(libraryScrollPane);

        // Create the "Playlist" tree
        playlistRootNode = new DefaultMutableTreeNode("Playlists") {
            @Override
            public boolean isLeaf() {
                return false; // Allow this node to be expandable and collapsible
            }
        };
        playlistTreeModel = new DefaultTreeModel(playlistRootNode);
        playlistTree = new JTree(playlistTreeModel);
        playlistTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        playlistTree.setRootVisible(true);
        playlistTree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                System.out.println("Paliwar");
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) playlistTree.getLastSelectedPathComponent();
                if (selectedNode != null && selectedNode.isLeaf() && selectedNode != playlistRootNode) {
                    // Handle playlist selection
                   currentPlaylist = selectedNode.toString();
                   populateTableFromPlaylist(currentPlaylist);
                    //populateTableFromDatabase();
                }
            }
        });
        
        JScrollPane playlistScrollPane = new JScrollPane(playlistTree);
        playlistScrollPane.setBorder(BorderFactory.createEmptyBorder());
        //playlistScrollPane.setPreferredSize(new Dimension(getWidth()/8, getHeight()/2));
        leftPanel.add(playlistScrollPane);
        
      leftPanel.addComponentListener(new ComponentAdapter() {
        @Override
        public void componentResized(ComponentEvent e) {
            int height = leftPanel.getHeight();
            int paneHeight = (height - 10) / 2; // Subtract total gap (10) from height
            libraryScrollPane.setBounds(0, 0, leftPanel.getWidth(), paneHeight);
            playlistScrollPane.setBounds(0, paneHeight + 5, leftPanel.getWidth(), paneHeight); // +5 for gap
        }
    });


        
        
        
        

        

        // Set up the table
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
        createPlaylistMenuItem = new JMenuItem("Create Playlist");

        fileMenu.add(openMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(createPlaylistMenuItem);
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
        JMenu addToPlaylistMenuItem = new JMenu("Add to Playlist");

        popupMenu.add(popupAdd);
        popupMenu.add(popupDelete);
        popupMenu.add(addToPlaylistMenuItem);

        songTable.setComponentPopupMenu(popupMenu);

        // Add Action Listeners (example for Play button)
        createPlaylistMenuItem.addActionListener(e -> createPlaylist());
        
        // Set up the popup menu for the playlist tree
        playlistPopupMenu = new JPopupMenu();
        openInNewWindowMenuItem = new JMenuItem("Open in New Window");
        playlistPopupMenu.add(openInNewWindowMenuItem);
        
         // Set up the action listener to update the "Add to Playlist" sub-menu
        songTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
                if (SwingUtilities.isRightMouseButton(evt)) {
                    updateAddToPlaylistMenu(addToPlaylistMenuItem);
                    popupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
                }
            }
        });
        
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Code to play song
            }
        });
        
    // Add right-click listener to playlist tree
    playlistTree.addMouseListener(new java.awt.event.MouseAdapter() {
       @Override
       public void mousePressed(java.awt.event.MouseEvent evt) {
           if (SwingUtilities.isRightMouseButton(evt)) {
               int row = playlistTree.getClosestRowForLocation(evt.getX(), evt.getY());
               playlistTree.setSelectionRow(row);
               playlistPopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
           }
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


         // Add Action Listener for "Open in New Window" menu item
        openInNewWindowMenuItem.addActionListener(e -> openPlaylistInNewWindow());
        // Similarly, add action listeners for other buttons and menu items
        // Populate table with data from the database
        populateTableFromDatabase();
        
        // Populate playlist tree with data from the database
        populatePlaylistsFromDatabase();
        
        // Set up a timer to refresh the table every 5 seconds
        Timer refreshTimer = new Timer(5000, e -> {
            if (currentPlaylist == null) {
                populateTableFromDatabase();
            } else {
                populateTableFromPlaylist(currentPlaylist);
            }
        });
        refreshTimer.start();
        setVisible(true);
    }
        // Method to update the "Add to Playlist" sub-menu
    private void updateAddToPlaylistMenu(JMenu addToPlaylistMenuItem) {
        addToPlaylistMenuItem.removeAll();
        List<String> playlists = Database.getAllPlaylists();
        for (String playlist : playlists) {
            JMenuItem playlistItem = new JMenuItem(playlist);
            playlistItem.addActionListener(e -> addToPlaylist(playlist));
            addToPlaylistMenuItem.add(playlistItem);
        }
    }
    
    // Method to add the selected song to the chosen playlist
    private void addToPlaylist(String playlistName) {
        int selectedRow = songTable.getSelectedRow();
        if (selectedRow != -1) {
            String title = (String) tableModel.getValueAt(selectedRow, 0);
            String artist = (String) tableModel.getValueAt(selectedRow, 1);
            String album = (String) tableModel.getValueAt(selectedRow, 2);
            Database.addSongToPlaylist(title, artist, album, playlistName);
            if (playlistName.equals(currentPlaylist)) {
                populateTableFromPlaylist(playlistName);
            }
        }
    }
    
    // Method to add the selected song to the chosen playlist
     private void openPlaylistInNewWindow() {
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) playlistTree.getLastSelectedPathComponent();
        if (selectedNode != null && selectedNode.isLeaf() && selectedNode != playlistRootNode) {
            String playlistName = selectedNode.toString();
            PlaylistWindow playlistWindow = new PlaylistWindow(playlistName);
            playlistWindow.setVisible(true);

            // Refill the main library table with the song library
            populateTableFromDatabase();
        }
    }
    private void createPlaylist() {
        String playlistName = JOptionPane.showInputDialog("Enter playlist name:");
        if (playlistName != null && !playlistName.trim().isEmpty()) {
            Database.createPlaylist(playlistName);
            //populatePlaylistsFromDatabase();
            DefaultMutableTreeNode newPlaylistNode = new DefaultMutableTreeNode(playlistName);
            playlistTreeModel.insertNodeInto(newPlaylistNode, playlistRootNode, playlistRootNode.getChildCount());
            TreePath path = new TreePath(newPlaylistNode.getPath());
            playlistTree.setSelectionPath(path);
        }
    }
 
    private void populateTableFromDatabase() {
        tableModel.setRowCount(0); // Clear existing data
        List<Object[]> songs = Database.getAllSongs();
        for (Object[] song : songs) {
            tableModel.addRow(song);
        }
    }
    
    private void populatePlaylistsFromDatabase() {
        List<String> playlists = Database.getAllPlaylists();
        for (String playlist : playlists) {
            DefaultMutableTreeNode newPlaylistNode = new DefaultMutableTreeNode(playlist);
            playlistTreeModel.insertNodeInto(newPlaylistNode, playlistRootNode, playlistRootNode.getChildCount());
        }
        
    }
    
    private void populateTableFromPlaylist(String playlistName) {
        tableModel.setRowCount(0); // Clear existing data
        List<Object[]> songs = Database.getSongsFromPlaylist(playlistName);
        for (Object[] song : songs) {
            tableModel.addRow(song);
        }
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
                        
                        // Update table model to reflect new data
                        if (currentPlaylist == null) {
                            populateTableFromDatabase();
                        } else {
                            populateTableFromPlaylist(currentPlaylist);
                        }
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
