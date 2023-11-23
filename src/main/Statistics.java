package main;

import media.Song;
import media.Playlist;

import java.util.ArrayList;
import java.util.Comparator;

public final class Statistics {
    private final ArrayList<Song> songs;
    private final ArrayList<Playlist> playlists;
    private final int topMaxSize = 5;

    public Statistics(final ArrayList<Song> songs,  //TO DO MAKE SEPArate constructors
                      final ArrayList<Playlist> playlists) {
        this.songs = songs;
        this.playlists = playlists;
    }
    public ArrayList<String> getTop5Songs() {
        ArrayList<String> top = new ArrayList<>();

        songs.sort((o1, o2) -> {
            if (o1.getLikes() < o2.getLikes()) {
                return 1;
            } else if (o1.getLikes() > o2.getLikes()) {
                return -1;
            }
            return 0;
        });

        for (Song song : songs.subList(0, topMaxSize)) {
            top.add(song.getName());
        }
        return top;
    }
    public ArrayList<String> getTop5Playlists() {
        ArrayList<String> top = new ArrayList<>();

        this.playlists.sort((o1, o2) -> {
            if (o1.getFollowers() < o2.getFollowers()) {
                return 1;
            } else if (o1.getFollowers() > o2.getFollowers()) {
                return -1;
            }
            return 0;
        });

        int iter = 0;
        for (Playlist playlist : playlists) {
            if (iter == topMaxSize) {
                break;
            }
            if (playlist.getVisibility().equals("public")) {
                top.add(playlist.getName());
                iter++;
            }
        }
        return top;
    }
}
