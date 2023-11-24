package player;

import com.fasterxml.jackson.databind.node.ObjectNode;
import main.Stats;
import main.User;
import media.Episode;
import media.Playlist;
import media.Podcast;
import media.Song;
import searchbar.Search;

public abstract class Player implements PlayerCommands {
    protected boolean isLoaded;
    protected Integer timeUpdated;
    protected Stats status;
    //make user here
    protected User user;
    // make this into each class
    protected Song loadedSong;
    protected Podcast loadedPodcast;
    protected Playlist loadedPlaylist;
    protected Episode loadedEpisode;

    /**
     * Creates a specific player(song, playlist, podcast) based on the search type.
     * Sets the player status.
     *
     * @param search The search made by user.
     * @param status The reference of the user status.
     * @return The new created player.
     */
    public static Player createPlayer(final Search search, final Stats status, final User user) {
        if (search.getType() == null) {
            return null;
        }
        Player player = null;
        if (search.getType().equals("song")) {
            player = new SongPlayer(search.getSelectedSong());
        }
        if (search.getType().equals("podcast")) {
            player = new PodcastPlayer(search.getSelectedPodcast());
        }
        if (search.getType().equals("playlist")) {
            player = new PlaylistPlayer(search.getSelectedPlaylist());
        }
        if (player != null) {
            player.setStatus(status);
        }
        return player;
    }

    /**
     * This method updates the status by putting it on pause or unpause.
     * Default method for all media types.
     *
     * @param timestamp The current timestamp
     * @param user The current user.
     */
    @Override
    public final void playPause(final Integer timestamp, final User user) {
        if (!status.isPaused()) {
            updateStatus(timestamp, user);
            status.setPaused(true);
        } else {
            status.setPaused(false);
            this.timeUpdated = timestamp;
        }
    }

    /**
     * This method sets the player to the next song.
     *
     * @param user The current user.
     * @param obj The objectNode for output message.
     * @param timestamp The current timestamp of the command.
     */
    public void next(final User user, final ObjectNode obj, final Integer timestamp) {
    }

    public void prev(final User user, final ObjectNode obj, final Integer timestamp) {
    }
    public void shuffle(final ObjectNode obj, final long seed) {
        // Default
        obj.put("message", "The loaded source is not a playlist.");
    }

    public void like(final User user, final ObjectNode obj) {
        // Default
        obj.put("message", "Loaded source is not a song.");
    }

    public void forward(final User user, final ObjectNode obj) {
        // Default
        obj.put("message", "The loaded source is not a podcast.");
    }

    public void backward(final User user, final ObjectNode obj) {
        // Default
        obj.put("message", "The loaded source is not a podcast.");
    }

    public void addRemoveInPlaylist(final User user,
                                    final Integer playlistId,
                                    final ObjectNode obj) {
        // Default
        obj.put("message", "The loaded source is not a song.");
    }

    public final void setStatus(Stats status) {
        this.status = status;
    }

    public final boolean isLoaded() {
        return isLoaded;
    }

    public final void setLoaded(final boolean loaded) {
        isLoaded = loaded;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
