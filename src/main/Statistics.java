package main;

import fileio.input.SongInput;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

public class Statistics {
    private final ArrayList<SongInput> songs;
    private final ArrayList<Playlist> playlists;

    public Statistics(ArrayList<SongInput> songs, ArrayList<Playlist> playlists) {
        this.songs = songs;
        this.playlists = playlists;
    }
    public ArrayList<String> getTop5Songs() {
        ArrayList<String> top5 = new ArrayList<>();
        songs.sort(new Comparator<SongInput>() {
            @Override
            public int compare(SongInput o1, SongInput o2) {
                if (o1.getLikes() < o2.getLikes()) {
                    return 1;
                } else if (o1.getLikes() > o2.getLikes()) {
                    return -1;
                }
                return 0;
            }
        });
        for (SongInput song : songs.subList(0, 5)) {
            top5.add(song.getName());
        }
        return top5;
    }
    public ArrayList<String> getTop5Playlists() {
        ArrayList<String> top5 = new ArrayList<>();
        this.playlists.sort(new Comparator<Playlist>() {
            @Override
            public int compare(Playlist o1, Playlist o2) {
                if (o1.getFollowers() < o2.getFollowers()) {
                    return 1;
                } else if (o1.getFollowers() > o2.getFollowers()) {
                    return -1;
                }
                return 0;
            }
        });
        int iter = 0;
        for (Playlist playlist : playlists) {
            if (iter == 5) {
                break;
            }
            if (playlist.getVisibility().equals("public")) {
                top5.add(playlist.getName());
                iter++;
            }
        }
        return top5;
    }
}
