package com.Sangeet.ui;

import com.Sangeet.dao.AuthDAO;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class SignupFrame extends JFrame {

    private final AuthDAO auth = new AuthDAO();

    public SignupFrame() {
        setTitle("Sangeet – Signup");
        setSize(830, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());

        // Outer rounded container
        JPanel container = new RoundedPanel(30, new Color(18,18,18));
        container.setLayout(new BorderLayout());
        add(container);

        // Left hero panel
        container.add(new HeroPanel(), BorderLayout.WEST);

        // Right signup panel
        container.add(new SignupFormPanel(), BorderLayout.CENTER);

        setVisible(true);
    }

    // ------------------------------------------
    // HERO PANEL (Left Side)
    // ------------------------------------------
    class HeroPanel extends JPanel {

        private Image img;

        HeroPanel() {
            try {
                img = new ImageIcon("src/com/Sangeet/assets/login_hero.jpg").getImage();
            } catch (Exception e) {
                e.printStackTrace();
            }
            setPreferredSize(new Dimension(450, 560));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(img, 0, 0, getWidth(), getHeight(), this);

            Graphics2D g2 = (Graphics2D) g;

            // dark gradient overlay
            GradientPaint fade = new GradientPaint(
                    0, getHeight(), new Color(0,0,0,140),
                    0, getHeight()-300, new Color(0,0,0,0)
            );

            g2.setPaint(fade);
            g2.fillRect(0, 0, getWidth(), getHeight());

            // bottom tagline
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("SansSerif", Font.BOLD, 32));
            g2.drawString("Join the", 30, getHeight()-120);

            g2.setFont(new Font("SansSerif", Font.BOLD, 36));
            g2.drawString("Sangeet Community", 30, getHeight()-70);
        }
    }

    // ------------------------------------------
    // SIGNUP PANEL (Right Side)
    // ------------------------------------------
    class SignupFormPanel extends JPanel {

        SignupFormPanel() {
            setLayout(null);
            setBackground(new Color(18,18,18));

            JLabel title = new JLabel("Create Account");
            title.setFont(new Font("SansSerif", Font.BOLD, 28));
            title.setForeground(Color.WHITE);
            title.setBounds(50, 40, 350, 40);
            add(title);

            JLabel subt = new JLabel("Fill in your details to get started");
            subt.setForeground(new Color(160,160,160));
            subt.setBounds(50, 78, 400, 25);
            add(subt);

            JLabel userLbl = new JLabel("Username");
            userLbl.setForeground(Color.WHITE);
            userLbl.setBounds(50, 130, 200, 25);
            add(userLbl);

            RoundedTextField userField = new RoundedTextField();
            userField.setBounds(50, 155, 330, 40);
            add(userField);

            JLabel passLbl = new JLabel("Password");
            passLbl.setForeground(Color.WHITE);
            passLbl.setBounds(50, 205, 200, 25);
            add(passLbl);

            RoundedPasswordField passField = new RoundedPasswordField();
            passField.setBounds(50, 230, 330, 40);
            add(passField);

            JLabel nameLbl = new JLabel("Display Name");
            nameLbl.setForeground(Color.WHITE);
            nameLbl.setBounds(50, 280, 200, 25);
            add(nameLbl);

            RoundedTextField displayField = new RoundedTextField();
            displayField.setBounds(50, 305, 330, 40);
            add(displayField);

            JComboBox<String> roleBox = new JComboBox<>(
                    new String[]{"Listener", "Artist", "Admin"}
            );
            roleBox.setBounds(50, 360, 330, 40);
            roleBox.setBackground(Color.BLACK);
            roleBox.setForeground(Color.WHITE);
            add(roleBox);

            RoundedButton signupBtn = new RoundedButton("Create Account");
            signupBtn.setBounds(50, 420, 330, 45);
            add(signupBtn);

            JLabel loginText = new JLabel("Already have an account?");
            loginText.setForeground(Color.GRAY);
            loginText.setBounds(50, 470, 200, 30);
            add(loginText);

            JButton loginBtn = new JButton("Login");
            loginBtn.setBounds(240, 470, 120, 30);
            loginBtn.setBorder(null);
            loginBtn.setContentAreaFilled(false);
            loginBtn.setForeground(new Color(30,215,96));
            loginBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            add(loginBtn);

            // ───────────────────────── LOGIN link ─────────────────────────
            loginBtn.addActionListener(e -> {
                dispose();
                new LoginFrame().setVisible(true);
            });

            // ───────────────────────── SIGNUP ACTION ─────────────────────────
            signupBtn.addActionListener(e -> {
                String u = userField.getText().trim();
                String pw = new String(passField.getPassword());
                String d = displayField.getText().trim();
                String r = (String) roleBox.getSelectedItem();

                boolean ok = false;

                if ("Artist".equals(r)) ok = auth.insertArtist(u, pw, d);
                else if ("Listener".equals(r)) ok = auth.insertListener(u, pw, d);
                else ok = auth.insertAdmin(u, pw, d);

                if (ok) {
                    JOptionPane.showMessageDialog(this, "Account Created! You can now login.");
                    dispose();
                    new LoginFrame().setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "Signup failed (Username might exist)");
                }
            });
        }
    }

    // ------------------------------------------
    // Custom Rounded Components
    // ------------------------------------------
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
        RoundedTextField() {
            setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
            setBackground(new Color(32,32,32));
            setForeground(Color.WHITE);
            setFont(new Font("SansSerif", Font.PLAIN, 15));
        }
    }

    static class RoundedPasswordField extends JPasswordField {
        RoundedPasswordField() {
            setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
            setBackground(new Color(32,32,32));
            setForeground(Color.WHITE);
            setFont(new Font("SansSerif", Font.PLAIN, 15));
        }
    }

    static class RoundedButton extends JButton {
        RoundedButton(String text) {
            super(text);
            setBackground(new Color(30, 215, 96)); // Spotify Green
            setForeground(Color.BLACK);
            setFocusPainted(false);
            setFont(new Font("SansSerif", Font.BOLD, 15));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
    }
}
