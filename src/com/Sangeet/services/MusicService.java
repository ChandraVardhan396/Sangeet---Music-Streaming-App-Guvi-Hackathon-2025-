package com.Sangeet.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class MusicService {

    private static final String MUSIC_DIR = "src/com/Sangeet/musics/";

    static {
        File dir = new File(MUSIC_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    // Upload music
    public static boolean uploadSong(File sourceFile) {
        try {
            File dest = new File(MUSIC_DIR + sourceFile.getName());
            Files.copy(sourceFile.toPath(), dest.toPath());
            return true;
        } catch (IOException e) {
            System.out.println("Upload error: " + e.getMessage());
            return false;
        }
    }

    // Get all uploaded songs
    public static List<String> getAllSongs() {
        List<String> songs = new ArrayList<>();

        File dir = new File(MUSIC_DIR);
        File[] files = dir.listFiles();

        if (files == null) return songs;

        for (File f : files) {
            if (f.isFile()) songs.add(f.getName());
        }

        return songs;
    }
}
