package com.Sangeet.ui.player;

import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.base.MediaPlayer;

import javax.swing.*;

public class MusicPlayer {

    private static MediaPlayerFactory factory;
    private static MediaPlayer player;
    private static boolean initialized = false;

    public static void init() {
        if (initialized) return;

        factory = new MediaPlayerFactory();
        player = factory.mediaPlayers().newMediaPlayer();
        initialized = true;

        // Optional: volume defaults
        player.audio().setVolume(80);
    }

    public static void playSong(String filePath) {
        init();
        player.media().play(filePath);
    }

    public static void pause() {
        if (!initialized) return;
        player.controls().pause();
    }

    public static void stop() {
        if (!initialized) return;
        player.controls().stop();
    }

    public static long getTime() {
        if (!initialized) return 0;
        return player.status().time();
    }

    public static long getLength() {
        if (!initialized) return 0;
        return player.status().length();
    }

    public static void seek(long ms) {
        if (!initialized) return;
        player.controls().setTime(ms);
    }

    public static boolean isPlaying() {
        if (!initialized) return false;
        return player.status().isPlaying();
    }
}
