package main;

import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fileio.input.SongInput;

import java.util.ArrayList;

public class Playlist {
    private String name;
    private String createdBy;
    private int followers;
    public static Integer instanceCount = 0;
    private final Integer playlistId;
    private ArrayList<SongInput> songs;
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
    public static void showPlaylists(ObjectNode objectNode, User user) {
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

    public static Integer getInstanceCount() {
        return instanceCount;
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
