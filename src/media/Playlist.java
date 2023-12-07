package media;

import com.fasterxml.jackson.databind.node.ObjectNode;
import user.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public final class Playlist {
    private String name;
    private final String owner;
    private int followers;
    private final Integer playlistId;
    private ArrayList<Song> songs;
    private ArrayList<Song> originalOrder;
    private String visibility;
    public Playlist(final String name, final int id, final String owner) {
        this.visibility = "public";
        this.name = name;
        this.songs = new ArrayList<>();
        this.originalOrder = new ArrayList<>();
        this.playlistId = id;
        this.owner = owner;
    }

    /**
     * This method checks if the playlist exists in the global array of the playlists.
     *
     * @param name The playlist name.
     * @param playlists The global playlist array.
     * @return Returns true if it exists, false otherwise.
     */
    public static boolean exists(final String name, final ArrayList<Playlist> playlists) {
        for (Playlist playlist : playlists) {
            if (playlist.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Shuffling the playlist's songs based on a seed.
     * Retaining a copy of the original order in order to reverse.
     *
     * @param seed The seed.
     */
    public void shuffleSongs(final long seed) {
        Random random = new Random(seed);
        this.originalOrder = new ArrayList<>(this.getSongs());
        Collections.shuffle(this.getSongs(), random);
    }

    /**
     * Restoring the playlist's songs to the original order.
     */
    public void unshuffleSongs() {
        Collections.copy(this.getSongs(), this.originalOrder);
    }

    /**
     * Method that adds a playlist to the list of followed playlists of the user.
     * Increases/Decreases the followers count of the playlist.
     * Adds a specific message to the objectNode in order to show as output.
     *
     * @param user The current user.
     * @param playlist The followed playlist.
     * @param objectNode The objectNode that holds the specific messages.
     */
    public static void follow(final User user,
                              final Playlist playlist,
                              final ObjectNode objectNode) {
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

    /**
     * This method returns the next song in the playlist.
     *
     * @param song The current song playing.
     * @return The next song.
     */
    public Song nextSong(final Song song) {
        for (int i = 0; i < this.getSongs().size() - 1; i++) {
            if (this.getSongs().get(i).getName().equals(song.getName())) {
                return this.getSongs().get(i + 1);
            }
        }
        return firstSong();
    }

    /**
     * This method returns the previous song in the playlist.
     * Returns the last song if the current song is the first song.
     *
     * @param song The current song.
     * @return The previous song.
     */
    public Song prevSong(final Song song) {
        for (int i = 1; i < this.getSongs().size(); i++) {
            if (this.getSongs().get(i).getName().equals(song.getName())) {
                return this.getSongs().get(i - 1);
            }
        }
        return lastSong();
    }

    /**
     * This method returns the first song of the playlist.
     *
     * @return The first song.
     */
    public Song firstSong() {
        return this.getSongs().get(0);
    }

    /**
     * This method returns the last song of the playlist.
     *
     * @return The last song.
     */
    public Song lastSong() {
        return this.getSongs().get(this.getSongs().size() - 1);
    }

    public int totalLikes() {
        int likes = 0;
        for (Song song : songs) {
            likes += song.getLikes();
        }
        return likes;
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

    public void setName(final String name) {
        this.name = name;
    }

    public Integer getPlaylistId() {
        return this.playlistId;
    }

    public ArrayList<Song> getOriginalOrder() {
        return originalOrder;
    }

    public ArrayList<Song> getSongs() {
        return this.songs;
    }

    public void setSongs(final ArrayList<Song> songs) {
        this.songs = songs;
    }

    public String getOwner() {
        return owner;
    }

}
