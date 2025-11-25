package com.Sangeet.ui.components;

import com.Sangeet.ui.MainFrame;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;

public class Sidebar extends JPanel {

    private final String role;

    public Sidebar(MainFrame main, String role) {
        this.role = role;

        setPreferredSize(new Dimension(240, 0));
        setBackground(new Color(12, 12, 12));
        setLayout(new BorderLayout());

        // ------------ TOP PROFILE SECTION ------------
        add(createProfileSection(), BorderLayout.NORTH);

        // ------------ MIDDLE MENU ------------
        JPanel menuPanel = new JPanel();
        menuPanel.setOpaque(false);
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        add(menuPanel, BorderLayout.CENTER);

        addMenuItems(main, menuPanel);
    }

    // -------------------------------------------------
    // PROFILE SECTION (top)
    // -------------------------------------------------
    private JPanel createProfileSection() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(200, 150));
        panel.setLayout(null);

        // Profile Picture
        JLabel avatar = new JLabel();
        avatar.setBounds(20, 25, 60, 60);
        avatar.setIcon(makeIcon("src/com/Sangeet/assets/icons/profile.png", 60, 60));
        panel.add(avatar);

        JLabel brand = new JLabel("Sangeet");
        brand.setForeground(new Color(30, 215, 96));
        brand.setFont(new Font("SansSerif", Font.BOLD, 26));
        brand.setBounds(95, 25, 200, 60);
        panel.add(brand);

        return panel;
    }

    // -------------------------------------------------
    // ADD ALL MENU ITEMS BASED ON ROLE
    // -------------------------------------------------
    private void addMenuItems(MainFrame main, JPanel menu) {

        if (role.equals("Listener")) {
            menu.add(menuButton(main, "Home", "home", "home.png"));
            menu.add(menuButton(main, "Dashboard", "listener", "dashboard.png"));
            menu.add(menuButton(main, "Artists", "artists", "artists.png"));

        } else if (role.equals("Artist")) {
            menu.add(menuButton(main, "Home", "home", "home.png"));
            menu.add(menuButton(main, "Dashboard", "artist", "dashboard.png"));
            menu.add(menuButton(main, "Followers", "followers", "followers.png"));

        } else if (role.equals("Admin")) {
            menu.add(menuButton(main, "Home", "home", "home.png"));
            menu.add(menuButton(main, "Admin Panel", "admin", "admin.png"));
        }

        menu.add(Box.createVerticalGlue());

        // ---- Logout button at bottom ----
        JButton logout = menuButton(main, "Logout", "home", "logout.png");
        logout.addActionListener(e -> {
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            topFrame.dispose();
            new com.Sangeet.ui.LoginFrame().setVisible(true);
        });
        menu.add(logout);
        menu.add(Box.createRigidArea(new Dimension(0, 10)));
    }

    // -------------------------------------------------
    // MENU BUTTON COMPONENT
    // -------------------------------------------------
    private JButton menuButton(MainFrame main, String text, String screen, String iconName) {

        JButton btn = new RoundButton(text);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 17));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setIcon(makeIcon("src/com/Sangeet/assets/icons/" + iconName, 22, 22));
        btn.setIconTextGap(14);

        btn.setMaximumSize(new Dimension(200, 45));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);

        btn.addActionListener(e -> main.showScreen(screen));

        // HOVER EFFECT
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setForeground(new Color(30, 215, 96));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setForeground(Color.WHITE);
            }
        });

        return btn;
    }

    // -------------------------------------------------
    // ICON HELPER
    // -------------------------------------------------
    public static ImageIcon loadHDIcon(String path, int w, int h) {
        try {
            BufferedImage img = ImageIO.read(new File(path));
            Image scaled = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);

            BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = out.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.drawImage(scaled, 0, 0, null);
            g2.dispose();

            return new ImageIcon(out);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private ImageIcon makeIcon(String path, int w, int h) {
        return loadHDIcon(path, w, h);
    }




    // -------------------------------------------------
    // ROUNDED BUTTON STYLE
    // -------------------------------------------------
    static class RoundButton extends JButton {

        RoundButton(String text) {
            super(text);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (getModel().isRollover()) {
                g2.setColor(new Color(255, 255, 255, 20));
            } else {
                g2.setColor(new Color(0, 0, 0, 0));
            }

            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 18, 18));
            g2.dispose();

            super.paintComponent(g);
        }
    }
}
