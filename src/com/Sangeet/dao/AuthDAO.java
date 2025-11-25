package com.Sangeet.dao;

import com.Sangeet.db.DBConnection;

import java.sql.*;

public class AuthDAO {

    public boolean insertArtist(String username, String password, String displayName) {
        String sql = "INSERT INTO artists (username, password, display_name) VALUES (?, ?, ?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, displayName);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean insertListener(String username, String password, String displayName) {
        String sql = "INSERT INTO listeners (username, password, display_name) VALUES (?, ?, ?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, displayName);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean insertAdmin(String username, String password, String displayName) {
        String sql = "INSERT INTO admins (username, password, display_name) VALUES (?, ?, ?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, displayName);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // simple auth check (returns id or -1)
    public int loginArtist(String username, String password) {
        return findCredential("artists", username, password);
    }
    public int loginListener(String username, String password) {
        return findCredential("listeners", username, password);
    }
    public int loginAdmin(String username, String password) {
        return findCredential("admins", username, password);
    }

    private int findCredential(String table, String username, String password) {
        String sql = "SELECT id FROM " + table + " WHERE username = ? AND password = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
