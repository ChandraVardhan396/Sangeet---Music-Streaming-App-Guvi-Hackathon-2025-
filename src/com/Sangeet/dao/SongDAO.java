package com.Sangeet.dao;

import com.Sangeet.db.DBConnection;
import com.Sangeet.models.Song;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SongDAO {

    private static final String MUSIC_FOLDER = "src/com/Sangeet/musics";

    private final ArtistDAO artistDAO = new ArtistDAO();

    public SongDAO() {
        File folder = new File(MUSIC_FOLDER);
        if (!folder.exists()) folder.mkdirs();
    }

    public List<Song> getAll() {
        List<Song> out = new ArrayList<>();

        String sql = """
        SELECT s.id, s.title, s.path, s.artist_id, a.display_name
        FROM songs s 
        LEFT JOIN artists a ON s.artist_id = a.id
        ORDER BY s.uploaded_at DESC
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String path = rs.getString("path");
                int artistId = rs.getInt("artist_id");

                out.add(new Song(id, title, path, artistId));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return out;
    }


    /**
     * Copy file to musics folder and store DB record.
     * artistUsername used to find/create artist row.
     */
    public boolean addRecord(int artistId, String title, String srcPath) {
        try {

            File src = new File(srcPath);
            if (!src.exists()) return false;

            File dest = new File(MUSIC_FOLDER + "/" + src.getName());
            Files.copy(src.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);

            String sql = "INSERT INTO songs (artist_id, title, path) VALUES (?, ?, ?)";
            try (Connection c = DBConnection.getConnection();
                 PreparedStatement ps = c.prepareStatement(sql)) {

                ps.setInt(1, artistId);
                ps.setString(2, title);
                ps.setString(3, dest.getAbsolutePath());
                ps.executeUpdate();
            }

            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }


    public boolean remove(int songId) {
        try (Connection c = DBConnection.getConnection()) {
            String path = null;
            try (PreparedStatement ps = c.prepareStatement("SELECT path FROM songs WHERE id = ?")) {
                ps.setInt(1, songId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) path = rs.getString(1);
                }
            }
            if (path != null) {
                File f = new File(path);
                if (f.exists()) f.delete();
            }
            try (PreparedStatement ps = c.prepareStatement("DELETE FROM songs WHERE id = ?")) {
                ps.setInt(1, songId);
                int r = ps.executeUpdate();
                return r > 0;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public String getMusicsFolder() {
        return MUSIC_FOLDER;
    }

    public boolean deleteSongsByArtist(int artistId) {
        String sql = "DELETE FROM songs WHERE artist_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, artistId);
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Song> getSongsByArtist(int artistId) {

        List<Song> list = new ArrayList<>();

        String sql = "SELECT id, title, path, artist_id FROM songs WHERE artist_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, artistId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Song(
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getString("path"),
                            rs.getInt("artist_id")
                    ));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public String getArtistName(Song song) {
        String sql = "SELECT display_name FROM artists WHERE id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, song.getArtistId());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("display_name");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Unknown";
    }





}
