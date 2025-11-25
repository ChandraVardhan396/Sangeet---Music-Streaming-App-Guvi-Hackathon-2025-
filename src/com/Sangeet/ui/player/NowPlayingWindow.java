package com.Sangeet.ui.player;

import com.Sangeet.audio.PlayerThread;
import com.Sangeet.dao.SongDAO;
import com.Sangeet.models.Song;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Objects;

/**
 * Redesigned Now Playing window — modern dark glass look with rounded corners,
 * circular controls and dynamic cover loading (250x250).
 *
 * Fallback cover (if song-specific cover missing) uses uploaded hero image:
 * /mnt/data/HD-wallpaper-records-phonograph-record-vinyl-record-retro-music-thumbnail.jpg
 */
public class NowPlayingWindow extends JDialog {

    private final PlayerThread playerThread;
    private final Song song;
    private final Thread playThread;

    private final JLabel titleLabel;
    private final JLabel artistLabel;
    private final JProgressBar progressBar;

    private final CircularButton playPauseBtn;
    private final CircularButton stopBtn;

    private boolean isPaused = false;
    private final Timer uiTimer;

    // Fallback hero image path (you uploaded this earlier)
    private static final String FALLBACK_IMAGE = "/mnt/data/HD-wallpaper-records-phonograph-record-vinyl-record-retro-music-thumbnail.jpg";

    public NowPlayingWindow(JFrame parent, Song song, PlayerThread playerThread) {
        super(parent, "Now Playing", true);

        this.song = song;
        this.playerThread = playerThread;
        this.playThread = new Thread(playerThread);

        setUndecorated(true);
        setSize(520, 640);
        setLocationRelativeTo(parent);
        setLayout(null);
        setBackground(new Color(0,0,0,0));

        // Create rounded content pane
        RoundedPane content = new RoundedPane(20, new Color(18, 18, 18));
        content.setLayout(null);
        content.setBounds(0, 0, getWidth(), getHeight());
        add(content);

        // Close on ESC
        getRootPane().registerKeyboardAction(e -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

        // Top close icon (simple X)
        JButton close = new JButton("✕");
        styleSmallIconButton(close);
        close.setBounds(getWidth() - 46, 12, 32, 32);
        close.addActionListener(e -> {
            safeStop();
            dispose();
        });
        content.add(close);

        // === COVER (250x250) ===
        int coverSize = 250;
        Image coverImg = loadCoverImage(song.getId(), coverSize);
        RoundImagePanel coverPanel = new RoundImagePanel(coverImg, coverSize, coverSize, 18);
        coverPanel.setBounds((getWidth() - coverSize) / 2, 40, coverSize, coverSize);
        content.add(coverPanel);

        // === Title & Artist ===
        titleLabel = new JLabel(song.getTitle(), SwingConstants.CENTER);
        titleLabel.setBounds(30, 320, getWidth() - 60, 36);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        content.add(titleLabel);

        SongDAO sdao = new SongDAO();
        artistLabel = new JLabel(sdao.getArtistName(song), SwingConstants.CENTER);
        artistLabel.setBounds(30, 360, getWidth() - 60, 22);
        artistLabel.setForeground(Color.LIGHT_GRAY);
        artistLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        content.add(artistLabel);

        // === Progress bar ===
        progressBar = new JProgressBar(0, 100);
        progressBar.setBounds(40, 400, getWidth() - 80, 10);
        progressBar.setForeground(new Color(30, 215, 96));
        progressBar.setBackground(new Color(40, 40, 40));
        progressBar.setBorder(BorderFactory.createEmptyBorder());
        content.add(progressBar);

        // === Controls (circular) ===
        playPauseBtn = new CircularButton("⏸"); // default to pause because playback will start
        playPauseBtn.setBounds((getWidth() / 2) - 46, 430, 92, 92);
        styleControl(playPauseBtn);
        playPauseBtn.addActionListener(e -> togglePlayPause());
        content.add(playPauseBtn);

        stopBtn = new CircularButton("⏹");
        stopBtn.setBounds(playPauseBtn.getX() + 110, playPauseBtn.getY() + 20, 52, 52);
        styleControl(stopBtn);
        stopBtn.addActionListener(e -> {
            safeStop();
            dispose();
        });
        content.add(stopBtn);

        // Optional: Back / Next could be added similarly

        // Shadow / border
        content.setBorderColor(new Color(40,40,40,160));

        // Start playback and UI timer (Swing Timer)
        startPlayback();

        uiTimer = new Timer(500, e -> updateProgress());
        uiTimer.start();
    }

    // Load cover image; tries jpg, jpeg, png; fallback to uploaded hero image
    private Image loadCoverImage(int songId, int size) {
        String base = "src/com/Sangeet/assets/covers/" + songId;
        String[] exts = {".jpg", ".jpeg", ".png"};
        BufferedImage img = null;
        try {
            for (String ext : exts) {
                File f = new File(base + ext);
                if (f.exists()) {
                    img = ImageIO.read(f);
                    break;
                }
            }
            if (img == null) {
                // fallback to uploaded hero image
                File fallback = new File(FALLBACK_IMAGE);
                if (fallback.exists()) img = ImageIO.read(fallback);
            }
        } catch (Exception ignored) {}

        if (img == null) {
            // create placeholder image
            img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = img.createGraphics();
            g.setColor(new Color(60,60,64));
            g.fillRect(0,0,size,size);
            g.setColor(Color.LIGHT_GRAY);
            g.setFont(new Font("SansSerif", Font.PLAIN, 14));
            FontMetrics fm = g.getFontMetrics();
            String text = "No Art";
            int tx = (size - fm.stringWidth(text)) / 2;
            int ty = (size - fm.getHeight()) / 2 + fm.getAscent();
            g.drawString(text, tx, ty);
            g.dispose();
        }

        // scale smoothly
        Image scaled = img.getScaledInstance(size, size, Image.SCALE_SMOOTH);
        return scaled;
    }

    private void startPlayback() {
        try {
            if (playThread != null && !playThread.isAlive()) playThread.start();
            isPaused = false;
            playPauseBtn.setText("⏸");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void togglePlayPause() {
        try {
            if (isPaused) {
                playerThread.resumePlayback();
                playPauseBtn.setText("⏸");
                isPaused = false;
            } else {
                playerThread.pause();
                playPauseBtn.setText("▶");
                isPaused = true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void updateProgress() {
        try {
            // PlayerThread.player.status().position() might throw — catch safely
            double pos = 0;
            try {
                pos = PlayerThread.player.status().position();
            } catch (Throwable ignored) {}
            int p = (int) (pos * 100);
            progressBar.setValue(Math.max(0, Math.min(100, p)));
        } catch (Exception ignored) {}
    }

    private void safeStop() {
        try {
            playerThread.stop();
        } catch (Exception ignored) {}
        try { uiTimer.stop(); } catch (Exception ignored) {}
    }

    // style small icon close button
    private void styleSmallIconButton(JButton b) {
        b.setFocusPainted(false);
        b.setBorder(null);
        b.setForeground(Color.LIGHT_GRAY);
        b.setBackground(new Color(0,0,0,0));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void styleControl(JButton b) {
        b.setFocusPainted(false);
        b.setBorder(null);
        b.setForeground(Color.WHITE);
        b.setBackground(new Color(30, 215, 96));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    // -------------------- Custom components --------------------

    // Rounded content pane
    static class RoundedPane extends JPanel {
        private final int radius;
        private final Color bg;
        private Color borderColor = new Color(50,50,50,180);

        RoundedPane(int radius, Color bg) {
            this.radius = radius;
            this.bg = bg;
            setOpaque(false);
            setLayout(null);
        }

        void setBorderColor(Color c) { this.borderColor = c; }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // shadow (subtle)
            g2.setColor(new Color(0,0,0,120));
            g2.fill(new RoundRectangle2D.Double(6, 8, getWidth()-12, getHeight()-16, radius+4, radius+4));

            // background rounded rect
            g2.setColor(bg);
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), radius, radius));

            // border
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(1.5f));
            g2.draw(new RoundRectangle2D.Double(0.75, 0.75, getWidth()-1.5, getHeight()-1.5, radius, radius));

            g2.dispose();
            super.paintComponent(g);
        }
    }

    // Circular control button (text label used for symbol)
    static class CircularButton extends JButton {
        private boolean hover = false;

        CircularButton(String text) {
            super(text);
            setOpaque(false);
            setContentAreaFilled(false);
            setBorder(null);
            setForeground(Color.BLACK);
            setFont(new Font("SansSerif", Font.BOLD, 18));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { hover = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            int w = getWidth(), h = getHeight();
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (hover) {
                g2.setColor(new Color(30,215,96,180));
                g2.fillOval(0,0,w,h);
                g2.setColor(new Color(30,215,96,230));
                g2.fillOval(6,6,w-12,h-12);
            } else {
                g2.setColor(new Color(30,215,96));
                g2.fillOval(0,0,w,h);
            }

            // draw symbol (as black triangle or text overlay)
            g2.setColor(Color.BLACK);
            super.paintComponent(g2);
            g2.dispose();
        }

        @Override public boolean isContentAreaFilled() { return false; }
    }

    // Panel that paints a rounded image (for circular/rounded corners)
    static class RoundImagePanel extends JPanel {
        private final Image image;
        private final int w,h;
        private final int radius;

        RoundImagePanel(Image image, int w, int h, int radius) {
            this.image = image;
            this.w = w;
            this.h = h;
            this.radius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (image == null) return;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // draw rounded image
            BufferedImage rounded = makeRoundedImage(toBufferedImage(image, w, h), radius);
            g2.drawImage(rounded, 0, 0, null);
            g2.dispose();
            super.paintComponent(g);
        }

        // utility: Image -> BufferedImage with correct size
        private static BufferedImage toBufferedImage(Image img, int w, int h) {
            if (img instanceof BufferedImage) {
                return (BufferedImage) img;
            }
            BufferedImage b = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = b.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2.drawImage(img, 0, 0, w, h, null);
            g2.dispose();
            return b;
        }

        // create rounded-corner image
        private static BufferedImage makeRoundedImage(BufferedImage src, int cornerRadius) {
            int w = src.getWidth();
            int h = src.getHeight();
            BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = out.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setClip(new RoundRectangle2D.Float(0, 0, w, h, cornerRadius, cornerRadius));
            g2.drawImage(src, 0, 0, null);
            g2.dispose();
            return out;
        }
    }
}
