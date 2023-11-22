package player;

import searchbar.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import media.*;
import main.Stats;
import main.User;

public abstract class AbstractPlayer implements PlayerCommands {
    protected boolean isLoaded;
    protected Integer timeUpdated;
    protected Stats status = Stats.getInstance();
    protected Song loadedSong;
    protected Podcast loadedPodcast;
    protected Playlist loadedPlaylist;
    protected Episode loadedEpisode;
    public static AbstractPlayer createPlayer(final Search search) {
        if (search.getType() == null) {
            return null;
        }
        if (search.getType().equals("song")) {
            return new SongPlayer(search.getSelectedSong());
        }
        if (search.getType().equals("podcast")) {
            return new PodcastPlayer(search.getSelectedPodcast());
        }
        if (search.getType().equals("playlist")) {
            return new PlaylistPlayer(search.getSelectedPlaylist());
        }
        return null;
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public void setLoaded(final boolean loaded) {
        isLoaded = loaded;
    }

    @Override
    public void playPause(final Integer timestamp, final User user) {
        if (!status.isPaused()) {
            updateStatus(timestamp, user);
            status.setPaused(true);
        } else {
            status.setPaused(false);
            this.timeUpdated = timestamp;
        }
    }

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
}
