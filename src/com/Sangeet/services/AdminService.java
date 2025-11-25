package com.Sangeet.services;

import com.Sangeet.models.Song;
import java.util.List;

public class AdminService {

    private final SongService songService = new SongService();

    public List<Song> getAll() {
        return songService.getAll();
    }

    public boolean remove(int index) {
        return songService.remove(index);
    }
}
