package fileio.input;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.Playlist;

import java.util.ArrayList;
import java.util.HashMap;

public final class UserInput {
    private String username;
    private int age;
    private String city;
    private ArrayList<SongInput> likedSongs;
    private ArrayList<Playlist> playlists;
    private HashMap<String, EpisodeInput> lastEpisodes;

    public UserInput() {
        likedSongs = new ArrayList<>();
        playlists = new ArrayList<>();
        lastEpisodes = new HashMap<>();
    }
    /**
     * This is a sample method that adds two integers.
     *
     * @param users The first integer.
     * @param name The second integer.
     * @return The sum of the two integers.
     */
    public static UserInput getUserByName(final ArrayList<UserInput> users,final String name) {
        if (users.isEmpty()) {
            return null;
        }
        for (UserInput user : users) {
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
        for (SongInput song : likedSongs) {
            if (song != null) {
                result.add(song.getName());
            }
        }
    }
    public ArrayList<SongInput> getLikedSongs() {
        return likedSongs;
    }

    public void setLikedSongs(final ArrayList<SongInput> likedSongs) {
        this.likedSongs = likedSongs;
    }

    public ArrayList<Playlist> getPlaylists() {
        return playlists;
    }

    public HashMap<String, EpisodeInput> getLastEpisodes() {
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
