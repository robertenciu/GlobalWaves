package Player;

import Media.Playlist;
import Media.Song;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.User;

public final class SongPlayer extends AbstractPlayer {
    public SongPlayer(final Song song) {
        super.loadedSong = song;
    }

    @Override
    public void addRemoveInPlaylist(final User user,
                                    final Integer playlistId,
                                    final ObjectNode obj) {
        Playlist playlist = Playlist.getPlaylistFromId(user, playlistId);
        if (playlist == null) {
            obj.put("message", "The specified playlist does not exist.");
            return;
        }
        if (playlist.getSongs().contains(loadedSong)) {
            playlist.getSongs().remove(loadedSong);
            obj.put("message", "Successfully removed from playlist.");
        } else {
            playlist.getSongs().add(loadedSong);
            obj.put("message", "Successfully added to playlist.");
        }
    }

    public void like(final User user, final ObjectNode obj) {
        if (user.getLikedSongs().contains(loadedSong)) {
            obj.put("message", "Unlike registered successfully.");
            user.getLikedSongs().remove(loadedSong);
            loadedSong.setLikes(loadedSong.getLikes() - 1);
        } else {
            obj.put("message", "Like registered successfully.");
            user.getLikedSongs().add(loadedSong);
            loadedSong.setLikes(loadedSong.getLikes() + 1);
        }
    }

    @Override
    public void load(final Integer timestamp, final User user) {
        status.setRemainedTime(loadedSong.getDuration());
        status.setName(loadedSong.getName());
        status.setPaused(false);
        super.timeUpdated = timestamp;
        super.isLoaded = true;
    }

    private void handleNoRepeat() {
        status.setRepeat("No Repeat");
        status.setName("");
        status.setRemainedTime(0);
        status.setPaused(true);
        this.isLoaded = false;
    }

    @Override
    public void updateStatus(final Integer timestamp, final User user) {
        if (status.isPaused()) {
            return;
        }

        status.setName(loadedSong.getName());

        int timeElapsed = timestamp - timeUpdated;
        if (status.getRemainedTime() > timeElapsed) {
            status.setRemainedTime(status.getRemainedTime() - timeElapsed);
            super.timeUpdated = timestamp;
            return;
        }
        switch (status.getRepeat()) {
            case "No Repeat":
                this.handleNoRepeat();
                break;
            case "Repeat Once":
                if (timeElapsed > loadedSong.getDuration() + status.getRemainedTime()) {
                    this.handleNoRepeat();
                } else {
                    timeElapsed = timestamp - (timeUpdated + status.getRemainedTime());
                    status.setRemainedTime(loadedSong.getDuration() - timeElapsed);
                    status.setRepeat("No Repeat");
                }
                break;
            case "Repeat Infinite":
                timeElapsed -= status.getRemainedTime();
                timeElapsed %= loadedSong.getDuration();
                status.setRemainedTime(loadedSong.getDuration() - timeElapsed);
                break;
            default:
                break;
        }
        super.timeUpdated = timestamp;
    }

    @Override
    public void repeat() {
        switch (status.getRepeat()) {
            case "No Repeat":
                status.setRepeat("Repeat Once");
                break;
            case "Repeat Once":
                status.setRepeat("Repeat Infinite");
                break;
            case "Repeat Infinite":
                status.setRepeat("No Repeat");
                break;
            default:
                break;
        }
    }
}
