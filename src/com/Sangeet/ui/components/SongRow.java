package com.Sangeet.ui.components;

import com.Sangeet.models.Song;
import com.Sangeet.dao.SongDAO;
import javax.swing.*;
import java.awt.*;

/**
 * A simple horizontal row for a song with Play / Delete buttons.
 */
public class SongRow extends JPanel {
    public SongRow(Song song, Runnable onPlay, Runnable onDelete) {
        SongDAO dao = new SongDAO();

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(0, 56));
        setBorder(BorderFactory.createMatteBorder(0,0,1,0, new Color(50,50,50)));
        setBackground(new Color(33,33,35));

        JLabel title = new JLabel(song.getTitle() + " — " + dao.getArtistName(song));
        title.setBorder(BorderFactory.createEmptyBorder(6,12,6,6));
        add(title, BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btns.setOpaque(false);
        JButton play = new JButton("Play");
        play.addActionListener(e -> onPlay.run());
        btns.add(play);

        JButton del = new JButton("Delete");
        del.addActionListener(e -> onDelete.run());
        btns.add(del);

        add(btns, BorderLayout.EAST);
    }
}
