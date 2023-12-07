package player;

import com.fasterxml.jackson.databind.node.ObjectNode;
import media.Playlist;
import media.Song;

public final class PlaylistPlayer extends Player {
    private final Playlist loadedPlaylist;
    private Song loadedSong;
    private boolean reachedPlaylistEnd;
    public PlaylistPlayer(final Playlist playlist) {
        this.loadedPlaylist = playlist;
    }

    @Override
    public void load(final Integer timestamp) {
        loadedSong = loadedPlaylist.firstSong();
        super.isLoaded = true;

        status.setRemainedTime(loadedSong.getDuration());
        status.setName(loadedSong.getName());
        status.setPaused(false);

        super.timeUpdated = timestamp;
    }

    @Override
    public void like(final ObjectNode obj) {
        SongPlayer temp = new SongPlayer(loadedSong);
        temp.setUser(user);
        temp.like(obj);
    }
    private void getCurrentSong(final int timeElapsed) {
        int timeRemaining = timeElapsed;
        Song nextSong;
        do {
            timeRemaining = timeRemaining - status.getRemainedTime();
            nextSong = loadedPlaylist.nextSong(loadedSong);

            assert nextSong != null;
            if (nextSong.equals(loadedPlaylist.firstSong())
                    && status.getRepeat().equals("No Repeat")) {
                handleNoRepeat();
                return;
            }

            loadedSong = nextSong;
            status.setRemainedTime(nextSong.getDuration());
        } while (timeRemaining >= nextSong.getDuration());

        status.setRemainedTime(loadedSong.getDuration() - timeRemaining);
        status.setName(loadedSong.getName());
    }

    private void handleNoRepeat() {
        status.reset();
        loadedSong = null;
        super.isLoaded = false;
    }

    @Override
    public void updateStatus(final Integer timestamp) {
        if (status.isPaused() || loadedSong == null
                || user.getConnectionStatus().equals("Offline")) {
            return;
        }

        int timeElapsed = timestamp - timeUpdated;
        if (status.getRemainedTime() > timeElapsed) {
            status.setRemainedTime(status.getRemainedTime() - timeElapsed);
            super.timeUpdated = timestamp;
            return;
        }
        switch (status.getRepeat()) {
            case "No Repeat":
            case "Repeat All":
                this.getCurrentSong(timeElapsed);
                break;
            case "Repeat Current Song":
                timeElapsed -= status.getRemainedTime();
                timeElapsed %= loadedSong.getDuration();
                status.setRemainedTime(loadedSong.getDuration() - timeElapsed);
                break;
            default:
                break;
        }
        checkReachedPlaylistEnd();
        // Setting timeUpdated to last call of updateStatus timestamp
        super.timeUpdated = timestamp;
    }

    private void checkReachedPlaylistEnd() {
        if (loadedSong == loadedPlaylist.lastSong()) {
            reachedPlaylistEnd = true;
        }
    }

    @Override
    public void repeat() {
        switch (status.getRepeat()) {
            case "No Repeat":
                status.setRepeat("Repeat All");
                break;
            case "Repeat All":
                status.setRepeat("Repeat Current Song");
                break;
            case "Repeat Current Song":
                status.setRepeat("No Repeat");
                break;
            default:
                break;
        }
    }

    @Override
    public void shuffle(final ObjectNode obj, final long seed) {
        if (status.isShuffle()) {
            loadedPlaylist.unshuffleSongs();
            status.setShuffle(false);
            obj.put("message", "Shuffle function deactivated successfully.");
        } else {
            loadedPlaylist.shuffleSongs(seed);
            status.setShuffle(true);
            obj.put("message", "Shuffle function activated successfully.");
        }
    }

    @Override
    public void next(final ObjectNode obj, final Integer timestamp) {
        Song nextSong = loadedPlaylist.nextSong(loadedSong);
        assert nextSong != null;

        switch (status.getRepeat()) {
            case "No Repeat":
                if (nextSong.equals(loadedPlaylist.firstSong())) {
                    status.reset();
                    loadedSong = null;
                    super.isLoaded = false;
                    break;
                }
            case "Repeat All":
                loadedSong = nextSong;
            case "Repeat Current Song":
                status.setRemainedTime(loadedSong.getDuration());
                status.setName(loadedSong.getName());
            default:
                break;
        }

        super.timeUpdated = timestamp;
        if (status.getName().isEmpty()) {
            obj.put("message",
                    "Please load a source before skipping to the next track.");
        } else {
            status.setPaused(false);
            obj.put("message",
                    "Skipped to next track successfully. The current track is "
                            + status.getName() + ".");
        }

    }

    @Override
    public void prev(final ObjectNode obj, final Integer timestamp) {
        if (status.getRemainedTime() < loadedSong.getDuration()) {
            status.setRemainedTime(loadedSong.getDuration());
        } else {
            Song prev = loadedPlaylist.prevSong(loadedSong);

            if (!status.getRepeat().equals("Repeat All") && prev == loadedPlaylist.lastSong()) {
                prev = loadedPlaylist.firstSong();
            } else if (prev == loadedPlaylist.lastSong()) {
                if (!reachedPlaylistEnd) {
                    prev = loadedPlaylist.firstSong();
                }
            }

            status.setRemainedTime(prev.getDuration());
            status.setName(prev.getName());
            loadedSong = prev;
        }

        super.timeUpdated = timestamp;
        status.setPaused(false);

        obj.put("message",
                "Returned to previous track successfully. The current track is "
                        + status.getName() + ".");
    }
}
