package com.Sangeet.ui.listener;

import com.Sangeet.audio.PlayerThread;
import com.Sangeet.dao.SongDAO;
import com.Sangeet.models.Song;
import com.Sangeet.ui.MainFrame;
import com.Sangeet.ui.components.SongRow;
import com.Sangeet.ui.player.NowPlayingWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

public class ListenerDashboard extends JPanel {

    private final SongDAO dao;
    private PlayerThread currentPlayer;
    private Thread playerThread;

    private final JPanel songListPanel = new JPanel();
    private final JPanel centerPanel = new GlassPanel();
    private final JTextField searchField = new JTextField();

    private MainFrame mainFrame;

    public ListenerDashboard(MainFrame mainFrame, SongDAO dao) {
        this.mainFrame = mainFrame;
        this.dao = dao;

        setLayout(new BorderLayout());
        setBackground(new Color(18, 18, 18));

        // ------------ TOP BAR ------------
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        topBar.setBackground(new Color(18, 18, 18));

        JLabel title = new JLabel("Explore Music");
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        topBar.add(title);

        add(topBar, BorderLayout.NORTH);

        // ------------ CENTER: Album Highlight ------------
        centerPanel.setPreferredSize(new Dimension(450, 450));
        centerPanel.setLayout(new BorderLayout());

        JLabel welcomeText = new JLabel("Select a song to start playing", SwingConstants.CENTER);
        welcomeText.setFont(new Font("SansSerif", Font.BOLD, 20));
        welcomeText.setForeground(Color.WHITE);

        centerPanel.add(welcomeText, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // ------------ RIGHT: Song List ------------
        JPanel rightPanel = new GlassPanel();
        rightPanel.setPreferredSize(new Dimension(380, 0));
        rightPanel.setLayout(new BorderLayout());
        rightPanel.setBackground(new Color(0, 0, 0, 60));

        JLabel listTitle = new JLabel("All Songs", SwingConstants.CENTER);
        listTitle.setForeground(Color.WHITE);
        listTitle.setFont(new Font("SansSerif", Font.BOLD, 20));
        listTitle.setBorder(BorderFactory.createEmptyBorder(15, 5, 10, 5));

        rightPanel.add(listTitle, BorderLayout.NORTH);

        songListPanel.setLayout(new BoxLayout(songListPanel, BoxLayout.Y_AXIS));
        songListPanel.setOpaque(false);

        JScrollPane scroll = new JScrollPane(songListPanel);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);

        rightPanel.add(scroll);

        add(rightPanel, BorderLayout.EAST);

        loadSongs();
    }

    // LOAD SONGS INTO RIGHT PANEL
    private void loadSongs() {
        songListPanel.removeAll();
        List<Song> songs = dao.getAll();

        for (Song s : songs) {
            JPanel item = createSongCard(s);
            songListPanel.add(item);
            songListPanel.add(Box.createVerticalStrut(6));
        }

        revalidate();
        repaint();
    }

    // MODERN SONG CARD
    private JPanel createSongCard(Song s) {

        JPanel card = new RoundedPanel(15);
        card.setLayout(new BorderLayout());
        card.setBackground(new Color(30, 30, 30, 180));
        card.setMaximumSize(new Dimension(320, 65));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel label = new JLabel("<html><b>" + s.getTitle() + "</b><br><span style='color:gray;'>Artist ID: "
                + s.getArtistId() + "</span></html>");
        label.setForeground(Color.WHITE);
        label.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        card.add(label, BorderLayout.CENTER);

        // Hover effect
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                card.setBackground(new Color(40, 40, 40, 220));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                card.setBackground(new Color(30, 30, 30, 180));
            }

            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                playSong(s);
            }
        });

        return card;
    }

    // PLAY SONG
    private void playSong(Song s) {
        if (currentPlayer != null) currentPlayer.stop();

        PlayerThread currentPlayer = new PlayerThread(s);
        playerThread = new Thread(currentPlayer);
        playerThread.start();

        // Update center panel UI with album preview
        centerPanel.removeAll();

        JLabel preview = new JLabel("Playing: " + s.getTitle(), SwingConstants.CENTER);
        preview.setFont(new Font("SansSerif", Font.BOLD, 22));
        preview.setForeground(Color.WHITE);

        centerPanel.add(preview, BorderLayout.CENTER);
        centerPanel.revalidate();
        centerPanel.repaint();

        NowPlayingWindow np = new NowPlayingWindow(mainFrame, s, currentPlayer);
        np.setVisible(true);
    }

    // ----------- Utility Panels ------------

    static class GlassPanel extends JPanel {
        GlassPanel() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(new Color(255, 255, 255, 20));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            super.paintComponent(g);
        }
    }

    static class RoundedPanel extends JPanel {
        private final int radius;

        RoundedPanel(int radius) {
            this.radius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), radius, radius));
            g2.dispose();
        }
    }
}
