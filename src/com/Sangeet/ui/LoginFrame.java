package com.Sangeet.ui;

import com.Sangeet.dao.AuthDAO;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.SQLException;

public class LoginFrame extends JFrame {

    private final AuthDAO auth = new AuthDAO();

    public LoginFrame() {
        setTitle("Sangeet – Login");
        setSize(920, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);

        // Main rounded panel
        JPanel container = new RoundedPanel(30, new Color(18,18,18));
        container.setLayout(new BorderLayout());
        add(container);

        // LEFT HERO PANEL
        container.add(new HeroPanel(), BorderLayout.WEST);

        // RIGHT LOGIN PANEL
        container.add(new LoginFormPanel(), BorderLayout.CENTER);

        setVisible(true);
    }

    // -------------------------------------------------
    //  Left Image Panel
    // -------------------------------------------------
    class HeroPanel extends JPanel {
        private Image img;

        HeroPanel() {
            try {
                img = new ImageIcon("src/com/Sangeet/assets/login_hero.jpg").getImage();
            } catch (Exception e) {
                e.printStackTrace();
            }
            setPreferredSize(new Dimension(450, 520));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(img, 0, 0, getWidth(), getHeight(), this);

            // Gradient overlay
            Graphics2D g2 = (Graphics2D) g;
            GradientPaint fade = new GradientPaint(
                    0, getHeight(), new Color(0,0,0,130),
                    0, getHeight()-200, new Color(0,0,0,0)
            );
            g2.setPaint(fade);
            g2.fillRect(0, 0, getWidth(), getHeight());

            // Tagline text
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("SansSerif", Font.BOLD, 32));
            g2.drawString("Be a Part of", 30, getHeight()-120);

            g2.setFont(new Font("SansSerif", Font.BOLD, 36));
            g2.drawString("Something Beautiful", 30, getHeight()-70);
        }
    }

    // -------------------------------------------------
    //  RIGHT LOGIN FORM PANEL
    // -------------------------------------------------
    class LoginFormPanel extends JPanel {

        LoginFormPanel() {
            setBackground(new Color(18,18,18));
            setLayout(null);

            JLabel title = new JLabel("Login");
            title.setFont(new Font("SansSerif", Font.BOLD, 30));
            title.setForeground(Color.WHITE);
            title.setBounds(50, 40, 300, 40);
            add(title);

            JLabel sub = new JLabel("Enter your credentials to access your account");
            sub.setForeground(new Color(170,170,170));
            sub.setBounds(50, 80, 450, 25);
            add(sub);

            JLabel userLbl = new JLabel("Email / Username");
            userLbl.setForeground(Color.WHITE);
            userLbl.setBounds(50, 120, 200, 25);
            add(userLbl);

            JTextField userField = new RoundedTextField();
            userField.setBounds(50, 150, 320, 38);
            add(userField);

            JLabel passLbl = new JLabel("Password");
            passLbl.setForeground(Color.WHITE);
            passLbl.setBounds(50, 200, 200, 25);
            add(passLbl);

            JPasswordField passField = new RoundedPasswordField();
            passField.setBounds(50, 230, 320, 38);
            add(passField);

            JComboBox<String> role = new JComboBox<>(
                    new String[]{"Listener", "Artist", "Admin"}
            );
            role.setBounds(50, 280, 320, 40);
            role.setBackground(Color.BLACK);
            role.setForeground(Color.WHITE);
            add(role);

            JButton loginBtn = new RoundedButton("Login");
            loginBtn.setBounds(50, 335, 320, 45);
            add(loginBtn);

            JLabel noAcc = new JLabel("Not a member? ");
            noAcc.setForeground(Color.GRAY);
            noAcc.setBounds(50, 385, 150, 30);
            add(noAcc);

            JButton createAcc = new JButton("Create an account");
            createAcc.setBounds(170, 385, 200, 30);
            createAcc.setBorder(null);
            createAcc.setContentAreaFilled(false);
            createAcc.setForeground(new Color(30, 215, 96));
            createAcc.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            add(createAcc);

            // LOGIN BUTTON ACTION
            loginBtn.addActionListener(e -> {
                String username = userField.getText().trim();
                String password = new String(passField.getPassword());
                String r = (String) role.getSelectedItem();

                int id = -1;
                if ("Artist".equals(r)) id = auth.loginArtist(username, password);
                else if ("Listener".equals(r)) id = auth.loginListener(username, password);
                else id = auth.loginAdmin(username, password);

                if (id != -1) {
                    dispose();
                    MainFrame mf = new MainFrame();
                    try {
                        mf.initForRole(r, username);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                    mf.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid Credentials");
                }
            });

            createAcc.addActionListener(e -> {
                dispose();
                new SignupFrame().setVisible(true);
            });
        }
    }

    // -------------------------------------------------
    //  CUSTOM ROUNDED COMPONENTS
    // -------------------------------------------------

    static class RoundedPanel extends JPanel {
        private final int radius;

        RoundedPanel(int radius, Color bg) {
            this.radius = radius;
            setOpaque(false);
            setBackground(bg);
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

    static class RoundedTextField extends JTextField {
        public RoundedTextField() {
            setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            setBackground(new Color(32,32,32));
            setForeground(Color.WHITE);
        }
    }

    static class RoundedPasswordField extends JPasswordField {
        public RoundedPasswordField() {
            setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            setBackground(new Color(32,32,32));
            setForeground(Color.WHITE);
        }
    }

    static class RoundedButton extends JButton {
        RoundedButton(String text) {
            super(text);
            setBackground(new Color(30, 215, 96));
            setForeground(Color.BLACK);
            setFocusPainted(false);
            setFont(new Font("SansSerif", Font.BOLD, 15));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
    }
}
