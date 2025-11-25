package com.Sangeet.db;

import java.sql.Connection;
import java.sql.Statement;

public class DatabaseUtils {

    public static void createTablesIfNotExist() {
        String createUsers = """
            CREATE TABLE IF NOT EXISTS users (
              id INT AUTO_INCREMENT PRIMARY KEY,
              username VARCHAR(100) NOT NULL UNIQUE,
              role ENUM('LISTENER','ARTIST','ADMIN') NOT NULL
            );
            """;

        String createArtists = """
            CREATE TABLE IF NOT EXISTS artists (
              id INT AUTO_INCREMENT PRIMARY KEY,
              user_id INT NOT NULL,
              display_name VARCHAR(150),
              FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
            );
            """;

        String createSongs = """
            CREATE TABLE IF NOT EXISTS songs (
              id INT AUTO_INCREMENT PRIMARY KEY,
              artist_id INT NOT NULL,
              title VARCHAR(255) NOT NULL,
              path VARCHAR(1024) NOT NULL,
              uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
              FOREIGN KEY (artist_id) REFERENCES artists(id) ON DELETE CASCADE
            );
            """;

        try (Connection c = DBConnection.getConnection();
             Statement s = c.createStatement()) {
            s.execute(createUsers);
            s.execute(createArtists);
            s.execute(createSongs);
            System.out.println("[DB] Tables ensured");
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Failed to create tables", ex);
        }
    }
}
