package com.Sangeet.ui.admin;

import com.Sangeet.dao.ArtistDAO;
import com.Sangeet.dao.FollowDAO;
import com.Sangeet.dao.SongDAO;
import com.Sangeet.models.Artist;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

public class AdminDashboard extends JPanel {

    private final ArtistDAO artistDAO = new ArtistDAO();
    private final SongDAO songDAO = new SongDAO();
    private final FollowDAO followDAO = new FollowDAO();

    private final JPanel center = new JPanel();

    public AdminDashboard(SongDAO ignored) {

        setLayout(new BorderLayout());
        setBackground(new Color(16, 16, 18));

        // HEADER
        JLabel title = new JLabel("Admin Panel — Manage Artists", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(Color.WHITE);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        header.add(title, BorderLayout.CENTER);

        add(header, BorderLayout.NORTH);

        // CENTER LIST
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        JScrollPane scroll = new JScrollPane(center);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);

        add(scroll, BorderLayout.CENTER);

        // REFRESH BUTTON
        JButton refresh = new JButton("Refresh");
        styleRefresh(refresh);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottom.setOpaque(false);
        bottom.add(refresh);
        add(bottom, BorderLayout.SOUTH);

        refresh.addActionListener(e -> loadArtists());

        loadArtists();
    }

    private void styleRefresh(JButton btn) {
        btn.setBackground(new Color(30, 215, 96));
        btn.setForeground(Color.BLACK);
        btn.setUI(new RoundedButtonUI());  // make rounded
    }

    private void loadArtists() {

        center.removeAll();
        List<Artist> all = artistDAO.getAllArtists();

        for (Artist a : all) {
            center.add(makeArtistCard(a));
            center.add(Box.createVerticalStrut(16));
        }

        revalidate();
        repaint();
    }

    private JPanel makeArtistCard(Artist a) {

        JPanel card = new GlassCard(24);
        card.setLayout(new BorderLayout(15, 0));
        card.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel lbl = new JLabel(
                "<html><b>" + a.getDisplayName() + "</b><br><span style='color:#999;'>@" + a.getUsername() + "</span></html>"
        );

        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 18));
        card.add(lbl, BorderLayout.CENTER);

        JButton deleteBtn = new JButton("Delete Artist");
        styleDelete(deleteBtn);

        deleteBtn.addActionListener(ev -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Delete artist '" + a.getDisplayName() + "' and ALL their songs?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                songDAO.deleteSongsByArtist(a.getId());
                followDAO.deleteAllFollowersOfArtist(a.getId());
                artistDAO.deleteArtist(a.getId());
                loadArtists();
            }
        });

        JPanel right = new JPanel();
        right.setOpaque(false);
        right.add(deleteBtn);

        card.add(right, BorderLayout.EAST);

        // Hover Glow
        card.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                ((GlassCard) card).setGlow(true);
            }
            @Override public void mouseExited(MouseEvent e) {
                ((GlassCard) card).setGlow(false);
            }
        });

        return card;
    }

    private void styleDelete(JButton btn) {
        btn.setBackground(new Color(220, 60, 60));  // red pill
        btn.setForeground(Color.WHITE);
        btn.setUI(new RoundedButtonUI()); // rounded pill
    }

    // ============================================================
    //   GLASS CARD (same aesthetic as HomeScreen / ArtistList)
    // ============================================================
    static class GlassCard extends JPanel {
        private boolean glow = false;
        private final int radius;

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

            // Glass base
            g2.setColor(new Color(255, 255, 255, 25));
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), radius, radius));

            // Inner dark
            g2.setColor(new Color(30, 30, 34, 150));
            g2.fill(new RoundRectangle2D.Double(2, 2, getWidth() - 4, getHeight() - 4, radius - 6, radius - 6));

            // Glow border
            if (glow) {
                g2.setColor(new Color(30, 215, 96, 120));
                g2.setStroke(new BasicStroke(3));
                g2.draw(new RoundRectangle2D.Double(1.5, 1.5, getWidth() - 3, getHeight() - 3, radius, radius));
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ============================================================
    //       Rounded Pill-Shaped Button UI (Spotify Style)
    // ============================================================
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

            int w = b.getWidth();
            int h = b.getHeight();

            // pill background
            g2.setColor(b.getBackground());
            g2.fillRoundRect(0, 0, w, h, h, h);

            // text
            g2.setColor(b.getForeground());
            FontMetrics fm = g2.getFontMetrics();
            int x = (w - fm.stringWidth(b.getText())) / 2;
            int y = (h + fm.getAscent()) / 2 - 3;

            g2.drawString(b.getText(), x, y);
            g2.dispose();
        }

        @Override
        public Dimension getPreferredSize(JComponent c) {
            return new Dimension(130, 40);
        }
    }
}
