package com.Sangeet.ui.components;

import com.Sangeet.dao.SongDAO;
import com.Sangeet.models.Song;

import javax.swing.*;
import java.awt.*;

public class LibraryScreen extends JPanel {
    private final SongDAO songDAO;

    public LibraryScreen(SongDAO dao) {
        this.songDAO = dao;
        setLayout(new BorderLayout());
        JLabel title = new JLabel("Your Library", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        add(title, BorderLayout.NORTH);

        refreshList();
    }

    private void refreshList() {
        removeAll();
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBackground(new Color(24,24,26));

        for (Song s : songDAO.getAll()) {
            center.add(new JLabel(s.toString()));
        }
        add(new JScrollPane(center), BorderLayout.CENTER);
        revalidate();
        repaint();
    }
}
