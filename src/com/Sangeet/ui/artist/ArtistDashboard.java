package com.Sangeet.ui.artist;

import com.Sangeet.audio.PlayerThread;
import com.Sangeet.dao.SongDAO;
import com.Sangeet.services.FileService;
import com.Sangeet.ui.MainFrame;
import com.Sangeet.models.Song;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * ArtistStudio — modern, glassy Artist dashboard.
 *
 * Constructor signature kept to: ArtistDashboard(MainFrame frame, SongDAO songDAO, int artistId, String artistUsername)
 */
public class ArtistDashboard extends JPanel {

    private final SongDAO dao;
    private final JPanel listPanel = new JPanel();
    private PlayerThread currentPlayer;
    private Thread playerThread;

    private final MainFrame mainFrame;
    private final int artistId;
    private final String artistUsername;

    public ArtistDashboard(MainFrame frame, SongDAO songDAO, int artistId, String artistUsername) {
        this.mainFrame = frame;
        this.dao = songDAO;
        this.artistId = artistId;
        this.artistUsername = artistUsername;

        setLayout(new BorderLayout());
        setBackground(new Color(16, 16, 18));

        // ===== HEADER =====
        JLabel title = new JLabel("Artist Studio", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(Color.WHITE);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(18, 18, 12, 18));
        header.add(title, BorderLayout.CENTER);

        add(header, BorderLayout.NORTH);

        // ===== TOP: Upload area (non-editable artist name + upload button + refresh) =====
        JPanel top = new JPanel(new BorderLayout(12, 0));
        top.setOpaque(false);
        top.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));

        // Left: Artist name (non-editable)
        JLabel artistLabel = new JLabel("Artist: " + artistUsername);
        artistLabel.setForeground(Color.WHITE);
        artistLabel.setFont(new Font("SansSerif", Font.BOLD, 16));

        top.add(artistLabel, BorderLayout.WEST);

        // Right: Upload + Refresh buttons
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        controls.setOpaque(false);

        JButton refresh = new JButton("Refresh");
        styleGreenPill(refresh);
        refresh.addActionListener(e -> refreshList());

        JButton uploadBtn = new JButton("Upload");
        styleGreenPill(uploadBtn);
        uploadBtn.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            int res = fc.showOpenDialog(this);
            if (res == JFileChooser.APPROVE_OPTION) {
                Path source = fc.getSelectedFile().toPath();
                try {
                    // copy file into musics folder and get safe filename
                    String newFilename = FileService.copyToFolder(source, Path.of(dao.getMusicsFolder()));
                    String titleName = source.getFileName().toString();

                    // Upload using artistId (DAO expects artist id based upload - implemented in your SongDAO)
                    boolean ok = dao.addRecord(artistId, titleName, dao.getMusicsFolder() + "/" + newFilename);

                    if (ok) {
                        refreshList();
                        JOptionPane.showMessageDialog(this, "Uploaded: " + titleName);
                    } else {
                        JOptionPane.showMessageDialog(this, "Upload failed");
                    }

                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Upload failed: " + ex.getMessage());
                }
            }
        });

        controls.add(refresh);
        controls.add(uploadBtn);

        top.add(controls, BorderLayout.EAST);
        add(top, BorderLayout.SOUTH);

        // ===== CENTER: Song list =====
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);

        JScrollPane sc = new JScrollPane(listPanel);
        sc.setBorder(null);
        sc.getViewport().setOpaque(false);
        sc.setOpaque(false);

        add(sc, BorderLayout.CENTER);

        // initial load
        refreshList();
    }

    private void styleGreenPill(JButton b) {
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setForeground(Color.BLACK);
        b.setBackground(new Color(30, 215, 96));
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setUI(new RoundedButtonUI());
    }

    private void styleRedPill(JButton b) {
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setForeground(Color.WHITE);
        b.setBackground(new Color(220, 60, 60));
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setUI(new RoundedButtonUI());
    }

    private void refreshList() {
        listPanel.removeAll();

        List<Song> songs = dao.getSongsByArtist(artistId);

        if (songs.isEmpty()) {
            JPanel empty = new JPanel();
            empty.setOpaque(false);
            JLabel lbl = new JLabel("No songs uploaded yet.");
            lbl.setForeground(Color.GRAY);
            empty.add(lbl);
            listPanel.add(empty);
        } else {
            for (Song s : songs) {
                listPanel.add(makeSongCard(s));
                listPanel.add(Box.createVerticalStrut(12));
            }
        }

        revalidate();
        repaint();
    }

    private JPanel makeSongCard(Song s) {
        GlassCard card = new GlassCard(16);
        card.setLayout(new BorderLayout(12, 0));
        card.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 86));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Left: text (title + small path or uploaded date placeholder)
        JLabel titleLbl = new JLabel("<html><b>" + escapeHtml(s.getTitle()) + "</b></html>");
        titleLbl.setForeground(Color.WHITE);
        titleLbl.setFont(new Font("SansSerif", Font.PLAIN, 16));

        JLabel meta = new JLabel("Click to play · " + (s.getPath() != null ? s.getPath().substring(Math.max(0, s.getPath().lastIndexOf('/') + 1)) : ""));
        meta.setForeground(Color.GRAY);
        meta.setFont(new Font("SansSerif", Font.PLAIN, 12));

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.add(titleLbl);
        text.add(meta);

        card.add(text, BorderLayout.CENTER);

        // Right: play (round) and delete (pill) buttons
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        right.setOpaque(false);

        RoundPlayButton play = new RoundPlayButton();
        play.addActionListener(e -> playSong(s));

        JButton del = new JButton("Delete");
        styleRedPill(del);
        del.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Delete song '" + s.getTitle() + "'?",
                    "Confirm",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                if (dao.remove(s.getId())) {
                    refreshList();
                } else {
                    JOptionPane.showMessageDialog(this, "Unable to delete the song.");
                }
            }
        });

        right.add(play);
        right.add(del);
        card.add(right, BorderLayout.EAST);

        // Play if clicking anywhere on the card
        card.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { card.setGlow(true); }
            @Override public void mouseExited(MouseEvent e) { card.setGlow(false); }
            @Override public void mouseClicked(MouseEvent e) { playSong(s); }
        });

        return card;
    }

    private synchronized void playSong(Song s) {
        try {
            if (currentPlayer != null) currentPlayer.stop();

            currentPlayer = new PlayerThread(s);
            playerThread = new Thread(currentPlayer);
            playerThread.start();

            new com.Sangeet.ui.player.NowPlayingWindow(mainFrame, s, currentPlayer).setVisible(true);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Unable to play: " + s.getTitle());
        }
    }

    // minimal HTML escape
    private String escapeHtml(String in) {
        return in == null ? "" : in.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    /* ------------------------
       UI helper components
       ------------------------ */

    static class GlassCard extends JPanel {
        private final int radius;
        private boolean glow = false;

        GlassCard(int radius) {
            this.radius = radius;
            setOpaque(false);
        }

        void setGlow(boolean g) { this.glow = g; repaint(); }

        @Override
        protected void paintComponent(Graphics gg) {
            Graphics2D g = (Graphics2D) gg.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // glass base
            g.setColor(new Color(255,255,255,18));
            g.fillRoundRect(0,0,getWidth(),getHeight(), radius, radius);

            // inner dark
            g.setColor(new Color(30,30,34,170));
            g.fillRoundRect(2,2,getWidth()-4,getHeight()-4, radius-6, radius-6);

            if (glow) {
                g.setColor(new Color(30,215,96,110));
                g.setStroke(new BasicStroke(3));
                g.drawRoundRect(1,1,getWidth()-2,getHeight()-2, radius, radius);
            }

            g.dispose();
            super.paintComponent(gg);
        }
    }

    // Round green play button
    static class RoundPlayButton extends JButton {
        private boolean hover = false;

        RoundPlayButton() {
            super("▶");
            setFocusPainted(false);
            setBorder(null);
            setOpaque(false);
            setForeground(Color.BLACK);
            setFont(new Font("SansSerif", Font.BOLD, 14));
            setPreferredSize(new Dimension(36, 36));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override public void mouseEntered(java.awt.event.MouseEvent e) { hover = true; repaint(); }
                @Override public void mouseExited(java.awt.event.MouseEvent e) { hover = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (hover) {
                g.setColor(new Color(30,215,96,160));
                g.fillOval(0,0,getWidth(),getHeight());
                g.setColor(new Color(30,215,96,220));
                g.fillOval(4,4,getWidth()-8,getHeight()-8);
            } else {
                g.setColor(new Color(30,215,96));
                g.fillOval(0,0,getWidth(),getHeight());
            }

            // draw triangle
            g.setColor(Color.BLACK);
            int pw = getWidth(), ph = getHeight();
            int[] px = { pw/2 - 5, pw/2 - 5, pw/2 + 6 };
            int[] py = { ph/2 - 6, ph/2 + 6, ph/2 };
            g.fillPolygon(px, py, 3);

            g.dispose();
            super.paintComponent(g);
        }

        @Override public boolean isContentAreaFilled() { return false; }
    }

    // Rounded pill-shaped UI
    static class RoundedButtonUI extends javax.swing.plaf.basic.BasicButtonUI {
        @Override
        public void installUI(JComponent c) {
            super.installUI(c);
            JButton b = (JButton) c;
            b.setOpaque(false);
            b.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            b.setFocusPainted(false);
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            b.setFont(new Font("SansSerif", Font.BOLD, 14));
        }

        @Override
        public void paint(Graphics g, JComponent c) {
            JButton b = (JButton) c;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = b.getWidth(), h = b.getHeight();

            g2.setColor(b.getBackground());
            g2.fillRoundRect(0, 0, w, h, h, h);

            g2.setColor(b.getForeground());
            FontMetrics fm = g2.getFontMetrics();
            int x = (w - fm.stringWidth(b.getText())) / 2;
            int y = (h + fm.getAscent()) / 2 - 3;
            g2.drawString(b.getText(), x, y);

            g2.dispose();
        }

        @Override
        public Dimension getPreferredSize(JComponent c) {
            return new Dimension(120, 40);
        }
    }
}
