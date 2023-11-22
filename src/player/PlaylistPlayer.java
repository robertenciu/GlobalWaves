package player;

import media.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.User;

public final class PlaylistPlayer extends AbstractPlayer {
    public PlaylistPlayer(final Playlist playlist) {
        super.loadedPlaylist = playlist;
    }

    @Override
    public void load(final Integer timestamp, final User user) {
        super.loadedSong = Playlist.firstSong(loadedPlaylist, user);
        super.isLoaded = true;

        status.setRemainedTime(loadedSong.getDuration());
        status.setName(loadedSong.getName());
        status.setPaused(false);

        super.timeUpdated = timestamp;
    }

    @Override
    public void like(final User user, final ObjectNode obj) {
        new SongPlayer(loadedSong).like(user, obj);
    }
    private void getCurrentSong(final int timeElapsed, final User user) {
        int timeRemaining = timeElapsed;
        Song nextSong;
        do {
            timeRemaining = timeRemaining - status.getRemainedTime();
            nextSong = Playlist.nextSong(loadedSong, loadedPlaylist);
            assert nextSong != null;

            if (nextSong.equals(Playlist.firstSong(loadedPlaylist, user)) &&
                                                    status.getRepeat().equals("No Repeat")) {
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
        super.loadedSong = null;
        super.isLoaded = false;
    }

    @Override
    public void updateStatus(final Integer timestamp, final User user) {
        if (status.isPaused() || loadedSong == null) {
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
                this.getCurrentSong(timeElapsed, user);
                break;
            case "Repeat Current Song":
                timeElapsed -= status.getRemainedTime();
                timeElapsed %= loadedSong.getDuration();
                status.setRemainedTime(loadedSong.getDuration() - timeElapsed);
                break;
            default:
                break;
        }

        // Setting timeUpdated to last call of updateStatus timestamp
        super.timeUpdated = timestamp;
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

    public void shuffle(final ObjectNode obj, final long seed) {
        if (status.isShuffle()) {
            Playlist.unshuffleSongs(loadedPlaylist);
            status.setShuffle(false);
            obj.put("message", "Shuffle function deactivated successfully.");
        } else {
            Playlist.shuffleSongs(loadedPlaylist, seed);
            status.setShuffle(true);
            obj.put("message", "Shuffle function activated successfully.");
        }
    }

    public void next(final User user, final ObjectNode obj, final Integer timestamp) {
        Song nextSong = Playlist.nextSong(loadedSong, loadedPlaylist);
        assert nextSong != null;

        switch (status.getRepeat()) {
            case "No Repeat":
                if (nextSong.equals(Playlist.firstSong(loadedPlaylist, user))) {
                    status.reset();
                    super.loadedSong = null;
                    super.isLoaded = false;
                    break;
                }
            case "Repeat All":
                super.loadedSong = nextSong;
                status.setRemainedTime(loadedSong.getDuration());
                status.setName(loadedSong.getName());
            default:
                break;
        }

        super.timeUpdated = timestamp;

        obj.put("message",
                "Skipped to next track successfully. The current track is "
                        + status.getName() + ".");

    }

    public void prev(final User user, final ObjectNode obj, final Integer timestamp) {
        if (status.getRemainedTime() < loadedSong.getDuration()) {
            status.setRemainedTime(loadedSong.getDuration());
        } else {
            Song prev = Playlist.prevSong(loadedSong, loadedPlaylist);
            status.setRemainedTime(prev.getDuration());
            status.setName(prev.getName());
            super.loadedSong = prev;
        }
        super.timeUpdated = timestamp;
        obj.put("message",
                "Returned to previous track successfully. The current track is " +
                        status.getName() + ".");
    }
}
