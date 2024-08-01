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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.sql.PreparedStatement;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;
import javax.swing.tree.TreePath;
/**
 *
 * @author Robinhood
 */
public class MyTunesGUI extends javax.swing.JFrame {
    
    private JTable songTable;
    private CustomTableModel tableModel;
    //private DefaultTableModel tableModel;
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
    private JMenuItem deletePlaylistMenuItem;
    private String currentPlaylist;
    private JSlider volumeSlider;
    private Map<String, TableColumn> hiddenCols;
    private TableRowSorter<DefaultTableModel> sorter;
    private JLabel leftTimerLabel;
    private JLabel rightTimerLabel;
    private JProgressBar progressBar;
    private Timer songTimer;
    private int songLengthInSeconds;
    private int elapsedTimeInSeconds;

    
    // Columns to be toggled
    private JCheckBoxMenuItem albumMenuItem;
    private JCheckBoxMenuItem artistMenuItem;
    private JCheckBoxMenuItem yearMenuItem;
    private JCheckBoxMenuItem genreMenuItem;
    private JCheckBoxMenuItem commentMenuItem;
     
    /**
     * Creates new form MyTunesGUI
     */
    public MyTunesGUI() {
        setTitle("MyTunes");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null); 
        
        
         
        hiddenCols = new HashMap<>();
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
        leftPanel.add(playlistScrollPane, BorderLayout.CENTER);
        // Resize listener to adjust component sizes
        leftPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int height = leftPanel.getHeight();
                int paneHeight = height / 22; 

                // Set bounds for the library and playlist scroll panes
                libraryScrollPane.setBounds(0, 0, leftPanel.getWidth(), paneHeight);
                playlistScrollPane.setBounds(0, paneHeight, leftPanel.getWidth(), height - paneHeight); // Fill remaining height
            }
        });

        // Set up the table
        String[] columns = {"Title", "Artist", "Album", "Year", "Genre", "Comment","File"};
        //tableModel = new DefaultTableModel(columns, 0);
        int[] hiddenColumns = {6}; 
        tableModel = new CustomTableModel(new Object[0][columns.length], columns, hiddenColumns);
        /*
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Make all cells editable except for the first four columns (Title, Artist, Album, Year)
                return column == 5; // "Comment" column is editable
            }
        };*/
        songTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(songTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // Set up sorting(Bonus Marks)
        sorter = new TableRowSorter<>(tableModel);
        songTable.setRowSorter(sorter);
        sorter.setSortKeys(Collections.singletonList(new RowSorter.SortKey(0, SortOrder.ASCENDING)));
        
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
        volumeSlider = new JSlider(0, 100, 50);

        buttonPanel.add(playButton);
        buttonPanel.add(stopButton);
        buttonPanel.add(pauseButton);
        buttonPanel.add(unpauseButton);
        buttonPanel.add(nextButton);
        buttonPanel.add(previousButton);
        buttonPanel.add(new JLabel("Volume"));
        buttonPanel.add(volumeSlider);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Create header popup menu
        JPopupMenu headerPopupMenu = new JPopupMenu();
        albumMenuItem = new JCheckBoxMenuItem("Album", true);
        artistMenuItem = new JCheckBoxMenuItem("Artist", true);
        yearMenuItem = new JCheckBoxMenuItem("Year", true);
        genreMenuItem = new JCheckBoxMenuItem("Genre", true);
        commentMenuItem = new JCheckBoxMenuItem("Comment", true);

        headerPopupMenu.add(albumMenuItem);
        headerPopupMenu.add(artistMenuItem);
        headerPopupMenu.add(yearMenuItem);
        headerPopupMenu.add(genreMenuItem);
        headerPopupMenu.add(commentMenuItem);
        
        albumMenuItem.addActionListener(e -> toggleColumnVisibility("Album", albumMenuItem.isSelected()));
        artistMenuItem.addActionListener(e -> toggleColumnVisibility("Artist", artistMenuItem.isSelected()));
        yearMenuItem.addActionListener(e -> toggleColumnVisibility("Year", yearMenuItem.isSelected()));
        genreMenuItem.addActionListener(e -> toggleColumnVisibility("Genre", genreMenuItem.isSelected()));
        commentMenuItem.addActionListener(e -> toggleColumnVisibility("Comment", commentMenuItem.isSelected()));
        
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
        deletePlaylistMenuItem = new JMenuItem("Delete Playlist");
        playlistPopupMenu.add(deletePlaylistMenuItem);
        
        
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
    songTable.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    headerPopupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
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
        deletePlaylistMenuItem.addActionListener(e -> deletePlaylist());

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
        // Add Timers and Progress Bar
        JPanel statusPanel = new JPanel(new BorderLayout());
        leftTimerLabel = new JLabel("00:00:00");
        rightTimerLabel = new JLabel("00:00:00");
        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);

        JPanel timerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        timerPanel.add(leftTimerLabel);
        timerPanel.add(progressBar);
        timerPanel.add(rightTimerLabel);

        statusPanel.add(timerPanel, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.NORTH);
        // Load column configuration on startup
        //loadColumnConfiguration();

        // Save column configuration on window close
        /*
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveColumnConfiguration();
            }
        });*/
        setVisible(true);
    }
    private void saveColumnConfiguration() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("columnConfig.txt"))) {
            for (Enumeration<TableColumn> e = songTable.getColumnModel().getColumns(); e.hasMoreElements();) {
                TableColumn column = e.nextElement();
                writer.println(column.getHeaderValue() + "," + (column.getModelIndex() - 1));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void loadColumnConfiguration() {
        File file = new File("columnConfig.txt");
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    String columnName = parts[0];
                    boolean isVisible = Boolean.parseBoolean(parts[1]);
                    JCheckBoxMenuItem menuItem = getMenuItemByName(columnName);
                    if (menuItem != null) {
                        menuItem.setSelected(isVisible);
                        toggleColumnVisibility(columnName, isVisible);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private JCheckBoxMenuItem getMenuItemByName(String columnName) {
        switch(columnName) {
            case "Album": return albumMenuItem;
            case "Artist": return artistMenuItem;
            case "Year": return yearMenuItem;
            case "Genre": return genreMenuItem;
            case "Comment": return commentMenuItem;
            default: return null;
        }
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
    private void deletePlaylist() {
    DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) playlistTree.getLastSelectedPathComponent();
    if (selectedNode != null && selectedNode.isLeaf() && selectedNode != playlistRootNode) {
        String playlistName = selectedNode.toString();
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete the playlist \"" + playlistName + "\"?", "Delete Playlist", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            Database.deletePlaylist(playlistName);
            populateDeletePlaylistsFromDatabase();
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
    
    private void populateDeletePlaylistsFromDatabase() {
        playlistRootNode.removeAllChildren(); // Clear existing nodes
        List<String> playlists = Database.getAllPlaylists();
        for (String playlist : playlists) {
            playlistRootNode.add(new DefaultMutableTreeNode(playlist));
        }
        playlistTreeModel.reload(); // Reload the tree model to refresh the view
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
    /*private void toggleColumnVisibility(String columnName, boolean isVisible) {
        TableColumnModel columnModel = songTable.getColumnModel();
        int columnIndex = -1;
        for (int i = 0; i < columnModel.getColumnCount(); i++) {
            if (columnModel.getColumn(i).getHeaderValue().equals(columnName)) {
                columnIndex = i;
                break;
            }
        }

        if (columnIndex != -1) {
            if (isVisible) {
                songTable.addColumn(columnModel.getColumn(columnIndex));
            } else {
                songTable.removeColumn(columnModel.getColumn(columnIndex));
            }
        }
    } */
    private void toggleColumnVisibility(String columnName, boolean isVisible) {
        TableColumnModel columnModel = songTable.getColumnModel();
        if (isVisible) {
            TableColumn column = hiddenCols.remove(columnName);
            if (column != null) {
                columnModel.addColumn(column);
                int columnCount = columnModel.getColumnCount();
                columnModel.moveColumn(columnModel.getColumnCount() - 1, getColumnIndex(columnName));
            }
        } else {
            int index = getColumnIndex(columnName);
            if (index != -1) {
                TableColumn column = columnModel.getColumn(index);
                hiddenCols.put(columnName, column);
                columnModel.removeColumn(column);
            }
        }
        //saveColumnConfiguration();// Save the configuration whenever a column is toggled
    }

    private int getColumnIndex(String columnName) {
        for (int i = 0; i < songTable.getColumnCount(); i++) {
            if (songTable.getColumnName(i).equals(columnName)) {
                return i;
            }
        }
        return -1;
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
