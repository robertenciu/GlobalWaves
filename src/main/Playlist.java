package main;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.SongInput;
import fileio.input.UserInput;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

public final class Playlist {
    private String name;
    private String createdBy;
    private int followers;
    public static Integer instanceCount = 0;
    private final Integer playlistId;
    private ArrayList<SongInput> songs;
    private ArrayList<SongInput> originalOrder;
    private String visibility;
    public Playlist () {this.visibility = "public";instanceCount++;this.playlistId = instanceCount;}
    public Playlist(String name) {
        this.visibility = "public";
        this.name = name;
        this.songs = new ArrayList<>();
        instanceCount++;
        this.playlistId = instanceCount;
    }

    public static boolean exists(String name, ArrayList<Playlist> playlists) {
        for(Playlist playlist : playlists) {
            if(playlist.getName().equals(name))
                return true;
        }
        return false;
    }
    public static void shuffleSongs(final Playlist playlist, final long seed) {
        Random random = new Random(seed);
        playlist.originalOrder = new ArrayList<>(playlist.getSongs());
        Collections.shuffle(playlist.getSongs(), random);
    }
    public static void unshuffleSongs(final Playlist playlist) {
        Collections.copy(playlist.getSongs(), playlist.originalOrder);
    }
    public static void switchVisibility(ArrayList<Playlist> playlists, Integer playlistId) {
        Playlist playlist = getPlaylistFromId(playlists, playlistId);
        if(playlist != null) {
            if(playlist.getVisibility().equals("public"))
                playlist.setVisibility("private");
            else
                playlist.setVisibility("public");
        }
    }
    public static Playlist getPlaylistFromId(ArrayList<Playlist> playlists, Integer playlistId) {
        if(playlistId > Playlist.instanceCount)
            return null;
        for(Playlist playlist : playlists)
            if(playlist.getPlaylistId().equals(playlistId))
                return playlist;
        return null;
    }
    public static SongInput nextSong(SongInput currentSong, Playlist playlist) {
        for(int i = 0; i < playlist.getSongs().size() - 1; i++)
            if(playlist.getSongs().get(i).getName().equals(currentSong.getName()))
                return playlist.getSongs().get(i + 1);
        if(playlist.getSongs().get(playlist.getSongs().size() - 1).getName().equals(currentSong.getName()))
            return playlist.getSongs().get(0);
        return null;
    }
    public static void showPlaylists(ObjectNode objectNode, UserInput user) {
        ArrayNode result = objectNode.putArray("result");

        for (Playlist playlist : user.getPlaylists()) {
            ObjectNode playlistObject = result.addObject();
            playlistObject.put("name", playlist.getName());
            ArrayNode songsArray = playlistObject.putArray("songs");
            for (SongInput song : playlist.getSongs()) {
                songsArray.add(song.getName());
            }
            playlistObject.put("visibility", playlist.getVisibility());
            playlistObject.put("followers", playlist.getFollowers());
        }
    }
    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public Integer getFollowers() {
        return followers;
    }

    public void setFollowers(Integer followers) {
        this.followers = followers;
    }

    public String getName() {
        return this.name;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPlaylistId() {
        return this.playlistId;
    }

    public ArrayList<SongInput> getSongs() {
        return this.songs;
    }

    public void setSongs(ArrayList<SongInput> songs) {
        this.songs = songs;
    }
}
