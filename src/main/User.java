package main;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import Media.*;
import fileio.input.*;

import java.util.ArrayList;
import java.util.HashMap;

public final class User {
    private String username;
    private int age;
    private String city;
    private ArrayList<Song> likedSongs;
    private final ArrayList<Playlist> playlists;
    private int numberOfPlaylists;
    private final HashMap<String, Episode> lastEpisodes;
    private final ArrayList<Playlist> followedPlaylists;

    public User(UserInput user) {
        likedSongs = new ArrayList<>();
        playlists = new ArrayList<>();
        lastEpisodes = new HashMap<>();
        followedPlaylists = new ArrayList<>();
        this.username = user.getUsername();
        this.age = user.getAge();
        this.city = user.getCity();
    }
    public static ArrayList<User> copyUsers(LibraryInput library) {
        ArrayList<User> users = new ArrayList<>();
        for (UserInput userInput : library.getUsers()) {
            User user = new User(userInput);
            users.add(user);
        }
        return users;
    }
    /**
     * This is a sample method that adds two integers.
     *
     * @param users The first integer.
     * @param name The second integer.
     * @return The sum of the two integers.
     */
    public static User getUserByName(final ArrayList<User> users, final String name) {
        if (users.isEmpty() || name == null) {
            return null;
        }
        for (User user : users) {
            if (user.getUsername().equals(name)) {
                return user;
            }
        }
        return null;
    }
    public void showPreferredSongs(final ObjectNode objectNode) {
        ArrayNode result = objectNode.putArray("result");
        if (likedSongs.isEmpty()) {
            return;
        }
        for (Song song : likedSongs) {
            if (song != null) {
                result.add(song.getName());
            }
        }
    }

    public int getNumberOfPlaylists() {
        return numberOfPlaylists;
    }

    public void setNumberOfPlaylists(int numberOfPlaylists) {
        this.numberOfPlaylists = numberOfPlaylists;
    }

    public ArrayList<Playlist> getFollowedPlaylists() {
        return followedPlaylists;
    }

    public ArrayList<Song> getLikedSongs() {
        return likedSongs;
    }

    public void setLikedSongs(final ArrayList<Song> likedSongs) {
        this.likedSongs = likedSongs;
    }

    public ArrayList<Playlist> getPlaylists() {
        return playlists;
    }

    public HashMap<String, Episode> getLastEpisodes() {
        return lastEpisodes;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public int getAge() {
        return age;
    }

    public void setAge(final int age) {
        this.age = age;
    }

    public String getCity() {
        return city;
    }

    public void setCity(final String city) {
        this.city = city;
    }
}