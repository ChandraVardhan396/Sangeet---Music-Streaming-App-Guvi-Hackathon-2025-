package com.Sangeet.ui.components;

import com.Sangeet.dao.ArtistDAO;
import com.Sangeet.dao.FollowDAO;
import com.Sangeet.models.Artist;
import com.Sangeet.ui.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.List;

public class ArtistListPanel extends JPanel {

    private final MainFrame frame;
    private final ArtistDAO artistDAO = new ArtistDAO();
    private final FollowDAO followDAO = new FollowDAO();
    private final int listenerId;

    public ArtistListPanel(MainFrame frame, int listenerId) throws SQLException {
        this.frame = frame;
        this.listenerId = listenerId;

        setLayout(new BorderLayout());
        setBackground(new Color(16,16,18));

        // ===== HEADER =====
        JLabel title = new JLabel("Discover Artists");
        title.setFont(new Font("SansSerif", Font.BOLD, 30));
        title.setForeground(Color.WHITE);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        header.add(title, BorderLayout.WEST);

        add(header, BorderLayout.NORTH);

        // ===== MAIN LIST =====
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);

        List<Artist> artists = artistDAO.getAllArtists();

        for (Artist artist : artists) {
            listPanel.add(makeArtistCard(artist));
            listPanel.add(Box.createVerticalStrut(15));
        }

        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);

        add(scroll, BorderLayout.CENTER);
    }

    // =======================================================
    // Artist Card – Glassmorphic + Rounded UI
    // =======================================================
    private JPanel makeArtistCard(Artist artist) {

        boolean isFollowing = followDAO.isFollowing(listenerId, artist.getId());

        JPanel card = new GlassCard(25);
        card.setLayout(new BorderLayout(15, 0));
        card.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // ===== Artist Name =====
        JLabel name = new JLabel("<html><b>" + artist.getDisplayName() + "</b></html>");
        name.setForeground(Color.WHITE);
        name.setFont(new Font("SansSerif", Font.PLAIN, 20));

        // ===== Follow / Unfollow Button =====
        JButton followBtn = new JButton(isFollowing ? "Unfollow" : "Follow");
        followBtn.setForeground(Color.BLACK);
        followBtn.setBackground(new Color(30, 215, 96));
        followBtn.setFocusPainted(false);
        followBtn.setBorder(BorderFactory.createEmptyBorder(8, 22, 8, 22));
        followBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        followBtn.setFont(new Font("SansSerif", Font.BOLD, 14));

        // Rounded button
        followBtn.setUI(new RoundedButtonUI());

        followBtn.addActionListener(e -> {
            if (followBtn.getText().equals("Follow")) {
                followDAO.follow(listenerId, artist.getId());
                artistDAO.incrementFollowers(artist.getId());
                followBtn.setText("Unfollow");
            } else {
                followDAO.unfollow(listenerId, artist.getId());
                artistDAO.decrementFollowers(artist.getId());
                followBtn.setText("Follow");
            }
        });

        // Hover effect for card
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                ((GlassCard) card).setGlow(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                ((GlassCard) card).setGlow(false);
            }
        });

        card.add(name, BorderLayout.CENTER);

        JPanel right = new JPanel();
        right.setOpaque(false);
        right.add(followBtn);

        card.add(right, BorderLayout.EAST);

        return card;
    }

    // =======================================================
    // Glass card UI
    // =======================================================
    static class GlassCard extends JPanel {
        private final int radius;
        private boolean glow = false;

        GlassCard(int r) {
            this.radius = r;
            setOpaque(false);
        }

        public void setGlow(boolean g) {
            glow = g;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Base glass panel
            g2.setColor(new Color(255,255,255,25));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);

            // Dark overlay
            g2.setColor(new Color(30,30,34,160));
            g2.fillRoundRect(2, 2, getWidth()-4, getHeight()-4, radius-6, radius-6);

            if (glow) {
                g2.setColor(new Color(30,215,96,120));
                g2.setStroke(new BasicStroke(3));
                g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, radius, radius);
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }

    // =======================================================
    // Rounded Button UI (Spotify-like)
    // =======================================================
    static class RoundedButtonUI extends javax.swing.plaf.basic.BasicButtonUI {

        @Override
        public void installUI(JComponent c) {
            super.installUI(c);
            JButton b = (JButton) c;
            b.setOpaque(false);
            b.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            b.setForeground(Color.BLACK);
            b.setBackground(new Color(30, 215, 96));
            b.setFocusPainted(false);
        }

        @Override
        public void paint(Graphics g, JComponent c) {
            JButton b = (JButton) c;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = b.getWidth();
            int h = b.getHeight();

            // Background (green pill)
            g2.setColor(b.getBackground());
            g2.fillRoundRect(0, 0, w, h, h, h); // h,h makes it a pill shape

            // Text
            FontMetrics fm = g2.getFontMetrics();
            int x = (w - fm.stringWidth(b.getText())) / 2;
            int y = (h + fm.getAscent()) / 2 - 3;
            g2.setColor(b.getForeground());
            g2.drawString(b.getText(), x, y);

            g2.dispose();
        }

        @Override
        public Dimension getPreferredSize(JComponent c) {
            return new Dimension(120, 40);
        }
    }
}
