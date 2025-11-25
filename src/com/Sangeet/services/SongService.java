package com.Sangeet.services;

import com.Sangeet.models.Song;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SongService {

    private static final String MUSIC_FOLDER = "src/com/Sangeet/musics";

    // UI uses this method
    public List<Song> getAll() {
        List<Song> list = new ArrayList<>();

        File folder = new File(MUSIC_FOLDER);
        if (!folder.exists()) folder.mkdirs();

        File[] files = folder.listFiles();
        if (files == null) return list;

        for (File f : files) {
            if (f.getName().endsWith(".mp3")) {
                list.add(new Song(f.getName(), f.getAbsolutePath(), 0));
            }
        }
        return list;
    }

    // Needed by UI
    public String getMusicsFolder() {
        return MUSIC_FOLDER;
    }

    // For artist upload
    public boolean addRecord(String title, String artist, String filePath) {
        try {
            File src = new File(filePath);
            File dest = new File(MUSIC_FOLDER + "/" + src.getName());

            return src.renameTo(dest);
        } catch (Exception e) {
            return false;
        }
    }

    // Needed by Admin to remove songs
    public boolean remove(int index) {
        List<Song> songs = getAll();
        if (index < 0 || index >= songs.size()) return false;

        File f = new File(songs.get(index).getPath());
        return f.delete();
    }
}
