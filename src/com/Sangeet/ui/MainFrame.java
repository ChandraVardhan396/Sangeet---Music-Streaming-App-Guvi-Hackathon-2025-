package com.Sangeet.ui;

import com.Sangeet.dao.ArtistDAO;
import com.Sangeet.dao.ListenerDAO;
import com.Sangeet.dao.SongDAO;
import com.Sangeet.ui.admin.AdminDashboard;
import com.Sangeet.ui.artist.ArtistDashboard;
import com.Sangeet.ui.components.*;
import com.Sangeet.ui.listener.ListenerDashboard;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class MainFrame extends JFrame {
    private final CardLayout cards = new CardLayout();
    private final JPanel mainPanel = new JPanel(cards);
    private final SongDAO songDAO = new SongDAO();

    public MainFrame() {
        setTitle("Sangeet");
        setSize(1200, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // IMPORTANT: do NOT add Sidebar here.
        // Sidebar only gets added inside initForRole()

        add(mainPanel, BorderLayout.CENTER);
    }


    public void showScreen(String key) {
        cards.show(mainPanel, key);
    }

    /**
     * Call after login. Makes UI role-tailored: shows only permitted menu items in Sidebar (Sidebar uses MainFrame.showScreen).
     */
    public void initForRole(String role, String username) throws SQLException {

        mainPanel.removeAll();

        if (role.equals("Listener")) {

            ListenerDAO listenerDAO = new ListenerDAO();
            int listenerId = listenerDAO.findByUsername(username);

            mainPanel.add(new HomeScreen(this,songDAO), "home");

            mainPanel.add(new ListenerDashboard(this, songDAO), "listener");
            mainPanel.add(new ArtistListPanel(this, listenerId), "artists");
        }
        else if (role.equals("Artist")) {

            ArtistDAO artistDAO = new ArtistDAO();
            int artistId = artistDAO.findByUsername(username);

            mainPanel.add(new HomeScreen(this,songDAO), "home");

            mainPanel.add(new ArtistDashboard(this, songDAO, artistId, username), "artist");

            mainPanel.add(new FollowersPanel(artistId), "followers");
        }
        else {
            mainPanel.add(new HomeScreen(this,songDAO), "home");

            mainPanel.add(new AdminDashboard(songDAO), "admin");
        }

        // Remove previous sidebar SAFELY
        BorderLayout layout = (BorderLayout) getLayout();
        Component oldSidebar = layout.getLayoutComponent(BorderLayout.WEST);
        if (oldSidebar != null) {
            remove(oldSidebar);
        }

        // Add the new sidebar
        add(new Sidebar(this, role), BorderLayout.WEST);

        revalidate();
        repaint();

        if (role.equals("Artist")) showScreen("artist");
        else if (role.equals("Listener")) showScreen("listener");
        else showScreen("admin");
    }

    public void refreshHomeScreen() {
        // Remove the old home screen
        mainPanel.remove(0);

        // Create a new home screen
        HomeScreen newScreen = new HomeScreen(this, songDAO);

        // Add it again with same key
        mainPanel.add(newScreen, "home");

        // Show the new refreshed screen
        showScreen("home");

        mainPanel.revalidate();
        mainPanel.repaint();
    }





}
