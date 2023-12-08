package player;

import com.fasterxml.jackson.databind.node.ObjectNode;
import user.User;
import searchbar.Search;

public abstract class Player implements PlayerCommands {
    protected boolean isLoaded;
    protected Integer timeUpdated;
    protected Status status;
    protected User user;

    /**
     * Creates a specific player(song, playlist, podcast) based on the search type.
     * Sets the player status.
     *
     * @param search The search made by user.
     * @param status The reference of the user status.
     * @return The new created player.
     */
    public static Player createPlayer(final Search search, final Status status, final User user) {
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
        if (search.getType().equals("album")) {
            player = new AlbumPlayer(search.getSelectedAlbum());
        }
        if (player != null) {
            player.setStatus(status);
            player.setUser(user);
        }
        return player;
    }

    /**
     * This method updates the status by putting it on pause or unpause.
     * Default method for all media types.
     *
     * @param timestamp The current timestamp
     */
    @Override
    public final void playPause(final Integer timestamp) {
        if (!status.isPaused()) {
            updateStatus(timestamp);
            status.setPaused(true);
        } else {
            status.setPaused(false);
            this.timeUpdated = timestamp;
        }
    }

    /**
     * This method sets the player to the next file(song, episode).
     *
     * @param obj The objectNode for output message.
     * @param timestamp The current timestamp of the command.
     */
    public void next(final ObjectNode obj, final Integer timestamp) {
    }

    /**
     * This method sets the player to the previous file(song, episode).
     * If the file already started it goes back to the beginning of the file.
     *
     * @param obj The objectNode for output message.
     * @param timestamp The current timestamp of the command.
     */
    public void prev(final ObjectNode obj, final Integer timestamp) {
    }

    /**
     * Method for shuffling feature.
     *
     * @param obj The objectNode for output message.
     * @param seed The seed for the random object.
     */
    public void shuffle(final ObjectNode obj, final long seed) {
        // Default
        obj.put("message", "The loaded source is not a playlist.");
    }

    /**
     * Method for liking a loaded song.
     *
     * @param obj The objectNode for specific output message.
     */
    public void like(final ObjectNode obj) {
        // Default
        obj.put("message", "Loaded source is not a song.");
    }

    /**
     * Method for forwarding a podcast (skipping an amount from the loaded episode).
     *
     * @param obj The objectNode for the output message.
     */
    public void forward(final ObjectNode obj) {
        // Default
        obj.put("message", "The loaded source is not a podcast.");
    }

    /**
     * This method sets the player to go back an amount of seconds from the playing episode.
     * If the amount is bigger than the elapsed time, it goes back to the beginning of the episode.
     *
     * @param obj The objectNode for the output message.
     */
    public void backward(final ObjectNode obj) {
        // Default
        obj.put("message", "The loaded source is not a podcast.");
    }

    /**
     * This method adds playing song to a specific playlist given by an id.
     *
     * @param playlistId The playlist id.
     * @param obj The objectNode which contains a specific output message.
     */
    public void addRemoveInPlaylist(final Integer playlistId, final ObjectNode obj) {
        // Default
        obj.put("message", "The loaded source is not a song.");
    }

    public final void setStatus(final Status status) {
        this.status = status;
    }

    public final boolean isLoaded() {
        return isLoaded;
    }

    public final void setLoaded(final boolean loaded) {
        isLoaded = loaded;
    }

    public final void setUser(final User user) {
        this.user = user;
    }

}
