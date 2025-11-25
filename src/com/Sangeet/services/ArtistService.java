package com.Sangeet.services;

import com.Sangeet.models.Song;
import java.util.List;

public class ArtistService {

    private final SongService songService = new SongService();

    public boolean addRecord(String title, String artist, String path) {
        return songService.addRecord(title, artist, path);
    }

    public List<Song> getAll() {
        return songService.getAll();
    }

    public boolean remove(int index) {
        return songService.remove(index);
    }

    public String getMusicsFolder() {
        return songService.getMusicsFolder();
    }
}
