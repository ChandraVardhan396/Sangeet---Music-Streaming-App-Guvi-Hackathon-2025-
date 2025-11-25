package com.Sangeet.ui.components;

import com.Sangeet.dao.ArtistDAO;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class FollowersPanel extends JPanel {

    private final ArtistDAO artistDAO = new ArtistDAO();

    public FollowersPanel(int artistId) {

        setLayout(new BorderLayout());
        setBackground(new Color(16,16,18));  // consistent dark background

        // ===== HEADER =====
        JLabel title = new JLabel("Followers", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 32));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(25, 10, 15, 10));

        add(title, BorderLayout.NORTH);

        // ===== FOLLOWER COUNT CARD =====
        int count = artistDAO.getFollowersCount(artistId);

        FollowersCard card = new FollowersCard(count);
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.add(card);

        add(centerWrapper, BorderLayout.CENTER);
    }

    // ============================
    //   Glassmorphic Followers Card
    // ============================
    static class FollowersCard extends JPanel {

        private final int count;
        private boolean glow = false;

        FollowersCard(int count) {
            this.count = count;
            setPreferredSize(new Dimension(320, 220));
            setOpaque(false);

            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    glow = true;
                    repaint();
                }
                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    glow = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();

            // Base glass rectangle
            g.setColor(new Color(255,255,255,18));
            g.fill(new RoundRectangle2D.Double(0, 0, w, h, 26, 26));

            // inner dark fill
            g.setColor(new Color(30,30,34,170));
            g.fill(new RoundRectangle2D.Double(3, 3, w-6, h-6, 20, 20));

            // Glow border
            if (glow) {
                g.setColor(new Color(30,215,96,140));
                g.setStroke(new BasicStroke(4));
                g.draw(new RoundRectangle2D.Double(2, 2, w-4, h-4, 22, 22));
            }

            // followers count
            g.setColor(Color.WHITE);
            g.setFont(new Font("SansSerif", Font.BOLD, 70));

            String text = String.valueOf(count);
            FontMetrics fm = g.getFontMetrics();
            int tw = fm.stringWidth(text);
            int th = fm.getAscent();

            g.drawString(
                    text,
                    (w - tw) / 2,
                    (h + th) / 2 - 10
            );

            // small label
            g.setFont(new Font("SansSerif", Font.PLAIN, 18));
            g.setColor(new Color(180,180,180));

            String sub = "Total Followers";
            int sw = g.getFontMetrics().stringWidth(sub);

            g.drawString(sub, (w - sw) / 2, (h + th) / 2 + 20);

            g.dispose();
        }
    }
}
