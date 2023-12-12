package media.music;

import com.fasterxml.jackson.databind.node.ObjectNode;
import user.User;

import java.util.ArrayList;
public class Playlist extends MusicCollection {
    private int followers;
    private final Integer playlistId;
    private String visibility;
    private ArrayList<User> followedBy = new ArrayList<>();
    public Playlist(final String name, final int id, final String owner) {
        this.visibility = "public";
        this.playlistId = id;
        super.songs = new ArrayList<>();
        super.originalOrder = new ArrayList<>();
        super.name = name;
        super.owner = owner;
        super.type = MusicCollectionType.PLAYLIST;
    }
    public Playlist(final Playlist playlist) {
        super(playlist);
        this.followedBy = playlist.followedBy;
        this.playlistId = playlist.playlistId;
        this.followers = playlist.followers;
        this.visibility = playlist.visibility;
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
            playlist.getFollowedBy().remove(user);
            objectNode.put("message", "Playlist unfollowed successfully.");
        } else {
            user.getFollowedPlaylists().add(playlist);
            playlist.setFollowers(playlist.getFollowers() + 1);
            playlist.getFollowedBy().add(user);
            objectNode.put("message", "Playlist followed successfully.");
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

    public Integer getPlaylistId() {
        return this.playlistId;
    }

    public ArrayList<User> getFollowedBy() {
        return followedBy;
    }
}
