package com.Sangeet.dao;

import com.Sangeet.db.DBConnection;
import com.Sangeet.models.Artist;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ArtistDAO {

    // Find artist ID by username
    public int findByUsername(String username) {
        String sql = "SELECT id FROM artists WHERE username = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("id");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    // Create a new artist
    public int create(String username, String password, String displayName) {
        String sql = "INSERT INTO artists (username, password, display_name) VALUES (?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, displayName);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    // Fetch all artists (for Listener follow/unfollow list)
    public List<Artist> getAllArtists() {
        List<Artist> list = new ArrayList<>();
        String sql = "SELECT id, username, display_name FROM artists";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String username = rs.getString("username");
                String displayName = rs.getString("display_name");

                list.add(new Artist(id, username, displayName));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // Get follower count
    public int getFollowersCount(int artistId) {
        String sql = "SELECT followers FROM artists WHERE id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, artistId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("followers");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    // Increase followers
    public void incrementFollowers(int artistId) {
        String sql = "UPDATE artists SET followers = followers + 1 WHERE id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, artistId);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Decrease followers
    public void decrementFollowers(int artistId) {
        String sql = "UPDATE artists SET followers = followers - 1 WHERE id=? AND followers > 0";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, artistId);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Admin removes an artist entirely
    public void deleteArtist(int artistId) {
        String sql = "DELETE FROM artists WHERE id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, artistId);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
