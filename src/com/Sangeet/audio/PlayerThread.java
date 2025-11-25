package com.Sangeet.audio;

import com.Sangeet.models.Song;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.base.MediaPlayer;

import java.io.File;

public class PlayerThread implements Runnable {

    private static MediaPlayerFactory factory;
    public static MediaPlayer player;

    private final Song song;

    private volatile boolean playing = true;
    private volatile boolean paused = false;
    private final Object pauseLock = new Object();

    public PlayerThread(Song song) {
        this.song = song;

        if (factory == null) {
            factory = new MediaPlayerFactory();
            player = factory.mediaPlayers().newMediaPlayer();
            player.audio().setVolume(85);
        }
    }

    private String cleanPath(String raw) {
        raw = raw.replace("file:\\", "")
                .replace("file:/", "")
                .replace("file:///", "")
                .replace("file://", "");

        return new File(raw).getAbsolutePath();
    }

    @Override
    public void run() {
        try {
            System.out.println("[Player] Starting: " + song.getTitle());

            String finalPath = cleanPath(song.getPath());
            System.out.println("[PATH] " + finalPath);

            player.media().play(finalPath);

            while (playing) {

                synchronized (pauseLock) {
                    while (paused) {
                        player.controls().pause();
                        pauseLock.wait();
                    }
                }

                Thread.sleep(200);
            }

            if (!playing) {
                player.controls().stop();
            }

            System.out.println("[Player] Ended: " + song.getTitle());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void pause() {
        paused = true;
    }

    public void resumePlayback() {
        synchronized (pauseLock) {
            paused = false;
            player.controls().play();
            pauseLock.notifyAll();
        }
    }

    public void stop() {
        playing = false;
        resumePlayback();
    }
}
