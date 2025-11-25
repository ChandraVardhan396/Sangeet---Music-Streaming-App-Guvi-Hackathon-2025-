package com.Sangeet.ui.components;

import com.Sangeet.audio.PlayerThread;
import com.Sangeet.dao.SongDAO;
import com.Sangeet.models.Song;
import com.Sangeet.ui.MainFrame;
import com.Sangeet.ui.player.NowPlayingWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.Comparator;
import java.util.List;
import java.io.File;


public class HomeScreen extends JPanel {

    private final SongDAO songDAO;
    private PlayerThread currentPlayer;
    private Thread playerThread;
    private final MainFrame mainFrame;

    public HomeScreen(MainFrame mainFrame, SongDAO dao) {
        this.mainFrame = mainFrame;
        this.songDAO = dao;

        setLayout(new BorderLayout());
        setBackground(new Color(16, 16, 18));

        // HEADER
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(25, 35, 5, 35));

        JLabel title = new JLabel("Welcome back to Sangeet 🎵");
        title.setFont(new Font("SansSerif", Font.BOLD, 32));
        title.setForeground(Color.WHITE);

        // HEADER RIGHT — REFRESH BUTTON
        JButton refresh = new JButton("Refresh");
        styleRefresh(refresh);
        refresh.addActionListener(e -> mainFrame.refreshHomeScreen());


        header.add(title, BorderLayout.WEST);
        header.add(refresh, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);


        // MAIN CONTENT
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(10, 35, 35, 35));

        // === LATEST COLLECTION ===
        content.add(makeSectionLabel("Latest Collection"));
        content.add(Box.createVerticalStrut(15));
        content.add(makeLatestCollectionCardsResponsive());

        content.add(Box.createVerticalStrut(35));

        // === ALL SONGS ===
        content.add(makeSectionLabel("All Songs"));
        content.add(Box.createVerticalStrut(15));
        content.add(makeAllSongsList());

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);

        add(scroll, BorderLayout.CENTER);
    }

    // SECTION LABEL
    private JPanel makeSectionLabel(String text) {

        JLabel lbl = new JLabel(text);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 24));
        lbl.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        wrapper.setOpaque(false);
        wrapper.add(lbl);

        return wrapper;
    }


    // -------------------------------
    // LATEST COLLECTION CARDS — responsive flow
    // -------------------------------
    private JPanel makeLatestCollectionCardsResponsive() {
        // Use FlowLayout so cards wrap responsively when window resizes
        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        wrapper.setOpaque(false);

        List<Song> songs = songDAO.getAll();
        songs.sort(Comparator.comparingInt(Song::getId).reversed());

        int limit = Math.min(6, songs.size());
        for (int i = 0; i < limit; i++) {
            Song s = songs.get(i);
            JPanel card = makeAlbumCard(s);
            wrapper.add(card);
        }

        // placeholders (if fewer than 6)
        for (int i = limit; i < 6; i++) {
            wrapper.add(makePlaceholderCard());
        }

        return wrapper;
    }

    private JPanel makeAlbumCard(Song s) {

        JPanel card = new GlassCard(18);
        card.setPreferredSize(new Dimension(180, 220));
        card.setLayout(new BorderLayout());
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // === Load cover image ===
        JLabel img = new JLabel();
        img.setHorizontalAlignment(SwingConstants.CENTER);

        String coverPathJPG = "src/com/Sangeet/assets/covers/" + s.getId() + ".jpg";
        String coverPathPNG = "src/com/Sangeet/assets/covers/" + s.getId() + ".png";

        Image cover = null;

        try {
            File f = new File(coverPathJPG);
            if (!f.exists()) f = new File(coverPathPNG);
            if (f.exists()) {
                cover = new ImageIcon(f.getAbsolutePath()).getImage()
                        .getScaledInstance(180, 150, Image.SCALE_SMOOTH);
            }
        } catch (Exception ignored) {}

        if (cover != null) {
            img.setIcon(new ImageIcon(cover));
            img.setOpaque(false);
        } else {
            // fallback placeholder
            img.setOpaque(true);
            img.setBackground(new Color(55, 55, 60));
            img.setForeground(Color.LIGHT_GRAY);
            img.setText("No Art");
            img.setFont(new Font("SansSerif", Font.PLAIN, 13));
        }

        img.setPreferredSize(new Dimension(180, 150));

        JLabel label = new JLabel(
                "<html><div style='padding:6px;'><b>" + escapeHtml(s.getTitle()) +
                        "</b><br/><span style='color:#a8a8a8'>Artist " +
                        songDAO.getArtistName(s) + "</span></div></html>"
        );
        label.setForeground(Color.WHITE);

        // Floating round play button
        RoundPlayButton floatingPlay = new RoundPlayButton();
        floatingPlay.setToolTipText("Play");
        floatingPlay.addActionListener(e -> playSongFromCard(s));

        // Create layered cover section with play overlay
        JLayeredPane layer = new JLayeredPane();
        layer.setPreferredSize(new Dimension(180, 150));

        img.setBounds(0, 0, 180, 150);
        floatingPlay.setBounds(180 - 46, 8, 36, 36);

        layer.add(img, Integer.valueOf(0));
        layer.add(floatingPlay, Integer.valueOf(1));

        // Hover effect → glow
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                ((GlassCard) card).setGlow(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                ((GlassCard) card).setGlow(false);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                playSongFromCard(s);
            }
        });

        card.add(layer, BorderLayout.NORTH);
        card.add(label, BorderLayout.CENTER);

        return card;
    }


    // Placeholder if fewer songs
    private JPanel makePlaceholderCard() {
        JPanel card = new GlassCard(18);
        card.setPreferredSize(new Dimension(180, 220));
        card.setLayout(new BorderLayout());

        JLabel img = new JLabel("No Art", SwingConstants.CENTER);
        img.setOpaque(true);
        img.setBackground(new Color(55, 55, 58));
        img.setForeground(Color.GRAY);
        img.setPreferredSize(new Dimension(180, 150));

        JLabel label = new JLabel("Unknown Album");
        label.setForeground(Color.WHITE);
        label.setHorizontalAlignment(SwingConstants.CENTER);

        card.add(img, BorderLayout.NORTH);
        card.add(label, BorderLayout.CENTER);

        return card;
    }

    // -------------------------------
    // ALL SONGS LIST (each row has round play button)
    // -------------------------------
    private JPanel makeAllSongsList() {
        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setOpaque(false);

        List<Song> songs = songDAO.getAll();
        for (Song s : songs) {
            list.add(makeSongRow(s));
            list.add(Box.createVerticalStrut(10));
        }

        return list;
    }

    private JPanel makeSongRow(Song s) {
        JPanel row = new RoundedPanel(20);
        row.setLayout(new BorderLayout(12, 0));
        row.setBackground(new Color(35, 35, 38, 200));
        row.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));

        // left: text
        JLabel title = new JLabel("<html><b>" + escapeHtml(s.getTitle()) + "</b></html>");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.PLAIN, 16));

        JLabel artist = new JLabel("Artist ID: " + songDAO.getArtistName(s));
        artist.setForeground(Color.GRAY);

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.add(title);
        text.add(artist);

        // right: round play button
        RoundPlayButton playBtn = new RoundPlayButton();
        playBtn.addActionListener(e -> playSongFromRow(s));

        // hover highlight for row
        row.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                row.setBackground(new Color(45, 45, 48, 220));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                row.setBackground(new Color(35, 35, 38, 200));
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                // if clicked anywhere on row (not just button), play
                playSongFromRow(s);
            }
        });

        row.add(text, BorderLayout.CENTER);

        JPanel right = new JPanel(new BorderLayout());
        right.setOpaque(false);
        // align button center-right
        right.add(playBtn, BorderLayout.CENTER);
        row.add(right, BorderLayout.EAST);

        return row;
    }

    // Play handlers (shared logic)
    private synchronized void playSongFromCard(Song s) {
        playInternal(s);
    }

    private synchronized void playSongFromRow(Song s) {
        playInternal(s);
    }

    private void playInternal(Song s) {
        try {
            if (currentPlayer != null) currentPlayer.stop();

            PlayerThread currentPlayer = new PlayerThread(s);
            playerThread = new Thread(currentPlayer);
            playerThread.start();

            NowPlayingWindow npw = new NowPlayingWindow(mainFrame, s, currentPlayer);
            npw.setVisible(true);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Unable to play the song: " + s.getTitle());
        }
    }

    // small HTML escape (very minimal)
    private String escapeHtml(String in) {
        return in == null ? "" : in.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    // ------------------------------
    // Custom UI components
    // ------------------------------

    // Round green play button (Spotify style)
    static class RoundPlayButton extends JButton {
        private boolean hover = false;

        RoundPlayButton() {
            super("▶");
            setFocusPainted(false);
            setBorder(null);
            setOpaque(false);
            setForeground(Color.BLACK);
            setFont(new Font("SansSerif", Font.BOLD, 14));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(36, 36));
            setToolTipText("Play");

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                @Override
                public void mouseExited(MouseEvent e) { hover = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // glow when hovered
            if (hover) {
                g2.setColor(new Color(30, 215, 96, 160));
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(30, 215, 96, 200));
                g2.fillOval(4, 4, getWidth()-8, getHeight()-8);
            } else {
                g2.setColor(new Color(30, 215, 96));
                g2.fillOval(0, 0, getWidth(), getHeight());
            }

            // draw triangle play symbol in center (text will overlay but we draw for crispness)
            g2.setColor(Color.BLACK);
            int pw = getWidth(), ph = getHeight();
            int[] px = { pw/2 - 5, pw/2 - 5, pw/2 + 6 };
            int[] py = { ph/2 - 6, ph/2 + 6, ph/2 };
            g2.fillPolygon(px, py, 3);

            g2.dispose();
            super.paintComponent(g);
        }

        @Override
        public boolean isContentAreaFilled() {
            return false;
        }
    }

    // Glass card with subtle glow
    static class GlassCard extends JPanel {
        private final int radius;
        private boolean glow = false;

        GlassCard(int radius) {
            this.radius = radius;
            setOpaque(false);
        }

        void setGlow(boolean g) {
            glow = g;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // base glass
            g2.setColor(new Color(255,255,255,20));
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), radius, radius));

            // subtle inner fill
            g2.setColor(new Color(30,30,34, (glow ? 200 : 150)));
            g2.fill(new RoundRectangle2D.Double(2, 2, getWidth()-4, getHeight()-4, radius-6, radius-6));

            // glow border when hovered
            if (glow) {
                g2.setColor(new Color(30,215,96,90));
                g2.setStroke(new BasicStroke(3));
                g2.draw(new RoundRectangle2D.Double(1.5, 1.5, getWidth()-3, getHeight()-3, radius, radius));
            }

            g2.dispose();
            super.paintComponent(g);
        }


    }

    static class RoundedPanel extends JPanel {
        private final int radius;
        RoundedPanel(int r) {
            this.radius = r;
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




    private void styleRefresh(JButton btn) {

        btn.setPreferredSize(new Dimension(110, 40));   // wide, pill shape
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setForeground(Color.BLACK);
        btn.setBackground(new Color(30, 215, 96));
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 22, 10, 22));

        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {

            @Override
            public void paint(Graphics g, JComponent c) {

                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = c.getWidth();
                int h = c.getHeight();
                int arc = h;   // makes perfect pill shape

                // Main pill background
                g2.setColor(btn.getBackground());
                g2.fillRoundRect(0, 0, w, h, arc, arc);

                // Draw centered text
                g2.setColor(btn.getForeground());
                FontMetrics fm = g2.getFontMetrics();
                String txt = btn.getText();

                int x = (w - fm.stringWidth(txt)) / 2;
                int y = (h + fm.getAscent()) / 2 - 3;

                g2.drawString(txt, x, y);

                g2.dispose();
            }

            @Override
            public Dimension getPreferredSize(JComponent c) {
                return new Dimension(110, 40);
            }
        });

        // Hover glow
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(30, 215, 96, 220));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(30, 215, 96));
            }
        });
    }



}
