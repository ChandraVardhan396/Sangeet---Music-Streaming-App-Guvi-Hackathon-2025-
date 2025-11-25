package com.Sangeet.models;

public class Song {
    private int id;
    private String title;
    private String path;
    private int artistId;

    public Song(int id, String title, String path, int artistId) {
        this.id = id;
        this.title = title;
        this.path = path;
        this.artistId = artistId;
    }

    public Song(String title, String path, int artistId) {
        this(0, title, path, artistId);
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getPath() { return path; }
    public int getArtistId() { return artistId; }

    @Override
    public String toString() {
        return title + " (Artist ID: " + artistId + ")";
    }
}
