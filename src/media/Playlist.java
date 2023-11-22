package media;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public final class Playlist {
    private String name;
    private String createdBy;
    private int followers;
    private final Integer playlistId;
    private ArrayList<Song> songs;
    private ArrayList<Song> originalOrder;
    private String visibility;
    public Playlist(final String name, final int id) {
        this.visibility = "public";
        this.name = name;
        this.songs = new ArrayList<>();
        this.playlistId = id;
    }

    public static boolean exists(final String name, final ArrayList<Playlist> playlists) {
        for (Playlist playlist : playlists) {
            if (playlist.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }
    public static void shuffleSongs(final Playlist playlist, final long seed) {
        Random random = new Random(seed);
        if (playlist.getSongs() == null) {
            return;
        }
        playlist.originalOrder = new ArrayList<>(playlist.getSongs());
        Collections.shuffle(playlist.getSongs(), random);
    }
    public static void unshuffleSongs(final Playlist playlist) {
        playlist.setSongs(playlist.originalOrder);
    }
    public static void switchVisibility(final Integer playlistId,
                                        final User user,
                                        final ObjectNode objectNode) {
        Playlist playlist = getPlaylistFromId(user, playlistId);
        assert playlist != null;

        if (!playlist.getCreatedBy().equals(user.getUsername())) {
            return;
        }

        if (playlist.getVisibility().equals("public")) {
            playlist.setVisibility("private");
            objectNode.put("message", "Visibility status updated successfully to private.");
        } else {
            playlist.setVisibility("public");
            objectNode.put("message", "Visibility status updated successfully to public.");
        }
    }
    public static void follow(final User user, Playlist playlist, ObjectNode objectNode) {
        if (user.getPlaylists().contains(playlist)) {
            objectNode.put("message", "You cannot follow or unfollow your own playlist.");
            return;
        }
        if (playlist.getVisibility().equals("private")) {
            return;
        }
        if (user.getFollowedPlaylists().contains(playlist)) {
            user.getFollowedPlaylists().remove(playlist);
            playlist.setFollowers(playlist.getFollowers() - 1);
            objectNode.put("message", "Playlist unfollowed successfully.");
        } else {
            user.getFollowedPlaylists().add(playlist);
            playlist.setFollowers(playlist.getFollowers() + 1);
            objectNode.put("message", "Playlist followed successfully.");
        }
    }
    public static Playlist getPlaylistFromId(final User user, final Integer playlistId) {
        ArrayList<Playlist> playlists = user.getPlaylists();
        for(Playlist playlist : playlists)
            if(playlist.getPlaylistId().equals(playlistId))
                return playlist;
        return null;
    }
    public  Song nextSong(Song song, Playlist playlist) {
        for (int i = 0; i < playlist.getSongs().size() - 1; i++)
            if (playlist.getSongs().get(i).getName().equals(song.getName()))
                return playlist.getSongs().get(i + 1);
        if (lastSong(playlist).getName().equals(song.getName()))
            return firstSong(playlist);
        return null;
    }
    public Song prevSong(Song song, Playlist playlist) {
        for (int i = 1; i < playlist.getSongs().size(); i++)
            if (playlist.getSongs().get(i).getName().equals(song.getName()))
                return playlist.getSongs().get(i - 1);
        return Playlist.firstSong(playlist);
    }
    public Song firstSong(Playlist playlist, User user) {
        return user.getPlaylist(playlist.getName()).getSongs().get(0);
    }
    public Song lastSong(Playlist playlist) {
        return playlist.getSongs().get(playlist.getSongs().size() - 1);
    }
    public static void showPlaylists(final ObjectNode objectNode, final User user) {
        ArrayNode result = objectNode.putArray("result");

        for (Playlist playlist : user.getPlaylists()) {
            ObjectNode playlistObject = result.addObject();
            playlistObject.put("name", playlist.getName());
            ArrayNode songsArray = playlistObject.putArray("songs");
            for (Song song : playlist.getSongs()) {
                songsArray.add(song.getName());
            }
            playlistObject.put("visibility", playlist.getVisibility());
            playlistObject.put("followers", playlist.getFollowers());
        }
    }
    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(final String visibility) {
        this.visibility = visibility;
    }

    public Integer getFollowers() {
        return followers;
    }

    public void setFollowers(final Integer followers) {
        this.followers = followers;
    }

    public String getName() {
        return this.name;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(final String createdBy) {
        this.createdBy = createdBy;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Integer getPlaylistId() {
        return this.playlistId;
    }

    public ArrayList<Song> getSongs() {
        return this.songs;
    }

    public void setSongs(final ArrayList<Song> songs) {
        this.songs = songs;
    }
}
