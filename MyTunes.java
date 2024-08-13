/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.mytunes;

import javax.swing.SwingUtilities;

/**
 *
 * @author Robinhood
 */
public class MyTunes {

    public static void main(String[] args) {
        //System.out.println("Hello World!");
        // Run the GUI in the Event Dispatch Thread for thread safety
        // Initialize the database and create tables if they do not exist
        //Database.createNewDatabase();
        //Database.createTables();
        //ID3TagReader.main(args);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MyTunesGUI(); // Create and show the MyTunes GUI
            }
        });
    }
}
