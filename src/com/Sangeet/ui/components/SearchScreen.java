package com.Sangeet.ui.components;


import com.Sangeet.dao.SongDAO;

import javax.swing.*;
import java.awt.*;


public class SearchScreen extends JPanel {
    public SearchScreen(SongDAO songDAO) {
        setLayout(new BorderLayout());


        JTextField searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(0, 40));
        add(searchField, BorderLayout.NORTH);


        add(new JLabel("Search Results will appear here"), BorderLayout.CENTER);
    }
}