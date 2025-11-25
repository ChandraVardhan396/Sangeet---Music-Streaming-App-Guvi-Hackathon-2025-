package com.Sangeet.dao;

import com.Sangeet.db.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FollowDAO {

    // Check if listener follows an artist
    public boolean isFollowing(int listenerId, int artistId) {
        String sql = "SELECT 1 FROM followers WHERE listener_id = ? AND artist_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, listenerId);
            ps.setInt(2, artistId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // true if any row exists
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // Insert follow row
    public void follow(int listenerId, int artistId) {
        String sql = "INSERT INTO followers(listener_id, artist_id) VALUES (?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, listenerId);
            ps.setInt(2, artistId);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Remove follow
    public void unfollow(int listenerId, int artistId) {
        String sql = "DELETE FROM followers WHERE listener_id=? AND artist_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, listenerId);
            ps.setInt(2, artistId);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // When admin deletes an artist
    public void deleteAllFollowersOfArtist(int artistId) {
        String sql = "DELETE FROM followers WHERE artist_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, artistId);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
