package main;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.SongInput;

import java.util.ArrayList;

public class User {
    private String name;
    private ArrayList<SongInput> likedSongs;
    public ArrayList<Playlist> playlists;
    public User() {likedSongs = new ArrayList<>();playlists = new ArrayList<>();}
    public User(String name) {likedSongs = new ArrayList<>();this.name = name; playlists = new ArrayList<>();}
    public static User getUserByName(ArrayList<User> users, String name) {
        if(users.isEmpty())
            return null;
        for(User user : users) {
            if(user.getName() != null) {
                if (user.getName().equals(name))
                    return user;
            }
        }
        return null;
    }

    public static void showPreferedSongs(ObjectNode objectNode, User user) {
        ArrayNode result = objectNode.putArray("result");
        for(SongInput song : user.getLikedSongs())
            result.add(song.getName());
    }
    public ArrayList<Playlist> getPlaylists() {
        return playlists;
    }

    public void setPlaylists(ArrayList<Playlist> playlists) {
        this.playlists = playlists;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<SongInput> getLikedSongs() {
        return likedSongs;
    }

    public void setLikedSongs(ArrayList<SongInput> likedSongs) {
        this.likedSongs = likedSongs;
    }
}
