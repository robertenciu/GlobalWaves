package main;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.EpisodeInput;
import fileio.input.PodcastInput;
import fileio.input.SongInput;
import fileio.input.UserInput;

import java.util.ArrayList;

interface PlayerCommands {
    void load(Integer timestamp, UserInput user);
    void playPause(Integer timestamp, UserInput user);
    void updateStatus(Integer timestamp, UserInput user);
    void like(UserInput user, ObjectNode objectNode);
    void forward(UserInput user, ObjectNode objectNode);
    void addRemoveInPlaylist(ArrayList<Playlist> playlists,
                             Integer playlistId,
                             ObjectNode objectNode);
    void repeat();
}

abstract class AbstractPlayer implements PlayerCommands {
    protected boolean isLoaded = false;
    protected Integer timeLoaded = 0;
    protected int timesRepeated = 0;
    protected Stats status = Stats.getInstance();
    protected Search search = Search.getInstance();

    public static AbstractPlayer createPlayer(final String type) {
        if (type == null) {
            return null;
        }
        if (type.equals("song")) {
            return new SongPlayer();
        }
        if (type.equals("podcast")) {
            return new PodcastPlayer();
        }
        if (type.equals("playlist")) {
            return new PlaylistPlayer();
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
    public void playPause(final Integer timestamp, final UserInput user) {
        if (!status.isPaused()) {
            updateStatus(timestamp, user);
            status.setPaused(true);
        } else {
            status.setPaused(false);
            this.timeLoaded = timestamp;
        }
    }
    public void shuffle(final ObjectNode objectNode, final long seed) {
        objectNode.put("message","The loaded source is not a playlist.");
    }
    public void like(final UserInput user, final ObjectNode objectNode) {
        // Default
        objectNode.put("message", "Loaded source is not a song.");
    }
    public void forward(UserInput user, ObjectNode objectNode) {
        objectNode.put("message", "The loaded source is not a podcast.");
    }
    public void addRemoveInPlaylist(final ArrayList<Playlist> playlists,
                                    final Integer playlistId,
                                    final ObjectNode objectNode) {
        // Default
        objectNode.put("message", "The loaded source is not a song.");
    }
}
class SongPlayer extends AbstractPlayer {
    public SongPlayer() { }
    @Override
    public void addRemoveInPlaylist(final ArrayList<Playlist> playlists,
                                    final Integer playlistId,
                                    final ObjectNode objectNode) {
        Playlist playlist = Playlist.getPlaylistFromId(playlists, playlistId);
        if (playlist == null) {
            objectNode.put("message", "The specified playlist does not exist.");
            return;
        }
        if (playlist.getSongs().contains(search.getSelectedSong())) {
            playlist.getSongs().remove(search.getSelectedSong());
            objectNode.put("message", "Successfully removed from playlist.");
        } else {
            playlist.getSongs().add(search.getSelectedSong());
            objectNode.put("message", "Successfully added to playlist.");
        }
    }

    public void like(final UserInput user, final ObjectNode objectNode) {
        if (user.getLikedSongs().contains(search.getSelectedSong())) {
            objectNode.put("message", "Unlike registered successfully.");
            user.getLikedSongs().remove(search.getSelectedSong());
        } else {
            objectNode.put("message", "Like registered successfully.");
            user.getLikedSongs().add(search.getSelectedSong());
        }
    }

    @Override
    public void load(final Integer timestamp, final UserInput user) {
        status.setRemainedTime(search.getSelectedSong().getDuration());
        status.setName(search.getSelectedSong().getName());
        status.setPaused(false);
        super.timeLoaded = timestamp;
        super.isLoaded = true;
    }
    private void noRepeat() {
        status.setRepeat("No Repeat");
        status.setName("");
        status.setRemainedTime(0);
        status.setPaused(true);
        this.isLoaded = false;
    }
    @Override
    public void updateStatus(final Integer timestamp, final UserInput user) {
        if (status.isPaused()) {
            return;
        }

        status.setName(search.getSelectedSong().getName());
        int timeElapsed = timestamp - timeLoaded;
        if (status.getRemainedTime() <= timeElapsed) {
            switch (status.getRepeat()) {
                case "No Repeat":
                    this.noRepeat();
                    break;
                case "Repeat Once":
                    if (timeElapsed > search.getSelectedSong().getDuration() + status.getRemainedTime()) {
                        this.noRepeat();
                    } else {
                        timeElapsed = timeLoaded + status.getRemainedTime();
                        status.setRemainedTime(search.getSelectedSong().getDuration() - (timestamp - timeElapsed));
                        status.setRepeat("No Repeat");
                    }
                    break;
                case "Repeat Infinite":
                    timeElapsed -= status.getRemainedTime();
                    timeElapsed = timeElapsed % search.getSelectedSong().getDuration();
                    status.setRemainedTime(search.getSelectedSong().getDuration() - timeElapsed);
                    break;
                default:
                    break;
            }
        } else {
            status.setRemainedTime(status.getRemainedTime() - timeElapsed);
        }
        super.timeLoaded = timestamp;
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

class PodcastPlayer extends AbstractPlayer {
    public PodcastPlayer() { }
    private EpisodeInput nextEpisode(final UserInput user) {
        int lastEpisodeIndex = search.getSelectedPodcast().getEpisodes().indexOf(lastEpisode(user));
        return search.getSelectedPodcast().getEpisodes().get(lastEpisodeIndex + 1);
    }
    private EpisodeInput lastEpisode(final UserInput user) {
        return user.getLastEpisodes().get(search.getSelectedPodcast().getName());
    }
    @Override
    public void load(final Integer timestamp, final UserInput user) {
        PodcastInput podcast = search.getSelectedPodcast();
        if (!user.getLastEpisodes().containsKey(podcast.getName())) {
            user.getLastEpisodes().put(podcast.getName(), podcast.getEpisodes().get(0));
        }
        EpisodeInput episode = lastEpisode(user);
        status.setPaused(false);
        status.setRemainedTime(episode.getDuration());
        super.timeLoaded = timestamp;
        super.isLoaded = true;
    }
    public void forward(final UserInput user, final ObjectNode objectNode) {
        EpisodeInput lastEpisode = lastEpisode(user);
        if (lastEpisode.getDuration() >= 90) {
            lastEpisode.setDuration(lastEpisode.getDuration() - 90);
            status.setRemainedTime(status.getRemainedTime() - 90);
        } else {
            EpisodeInput nextEpisode = nextEpisode(user);
            status.setRemainedTime(nextEpisode.getDuration());
            lastEpisode.setDuration(0);
            user.getLastEpisodes().put(search.getSelectedPodcast().getName(), nextEpisode);
        }
        objectNode.put("message", "Skipped forward successfully.");
    }
    public void backward(final UserInput user, final ObjectNode objectNode) {
        EpisodeInput lastEpisode = lastEpisode(user);
        if (lastEpisode.getDuration() - 90 > 0 ) {
            lastEpisode.setDuration(lastEpisode.getDuration() - 90);
            status.setRemainedTime(status.getRemainedTime() - 90);
        } else {
            EpisodeInput nextEpisode = nextEpisode(user);
            status.setRemainedTime(nextEpisode.getDuration());
            lastEpisode.setDuration(0);
            user.getLastEpisodes().put(search.getSelectedPodcast().getName(), nextEpisode);
        }
        objectNode.put("message", "Skipped forward successfully.");
    }
    void handleNoRepeat(Integer timeElapsed, final UserInput user, EpisodeInput episode) {
        timeElapsed = timeElapsed - lastEpisode(user).getDuration();
        episode.setDuration(0);
        episode = nextEpisode(user);
        user.getLastEpisodes().put(search.getSelectedPodcast().getName(), episode);
        episode.setDuration(episode.getDuration() - timeElapsed);
        if (timeElapsed >= episode.getDuration()) {
            handleNoRepeat(timeElapsed, user, episode);
        }
    }
    @Override
    public void updateStatus(final Integer timestamp, final UserInput user) {
        if (status.isPaused()) {
            status.setName(lastEpisode(user).getName());
            status.setRemainedTime(lastEpisode(user).getDuration());
            return;
        }
        EpisodeInput episode = lastEpisode(user);
        if (episode == null) {
            return;
        }
        status.setName(episode.getName());

        int timeElapsed = timestamp - super.timeLoaded;

        if (status.getRemainedTime() <= timeElapsed) {
            switch (status.getRepeat()) {
                case "No Repeat":
                    this.handleNoRepeat(timeElapsed, user, episode);
                    status.setName(lastEpisode(user).getName());
                    status.setRemainedTime(lastEpisode(user).getDuration());
                    break;
                case "Repeat Once":
                    break;
                case "Repeat Infinite":
                    // not implemented yet
                    break;
                default:
                    break;
            }
        } else {
            status.setRemainedTime(status.getRemainedTime() - timeElapsed);
            episode.setDuration(status.getRemainedTime());
        }
        super.timeLoaded = timestamp;
    }

    @Override
    public void repeat() {
        // Repeat is same as in SongPlayer
        new SongPlayer().repeat();
    }
}

class PlaylistPlayer extends AbstractPlayer {
    public PlaylistPlayer() { }
    public int timeElapsed;

    @Override
    public void load(final Integer timestamp, final UserInput user) {
        Playlist playlist = search.getSelectedPlaylist();

        if (playlist != null) {
            if (!playlist.getSongs().isEmpty()) {
                SongInput firstSong = playlist.getSongs().get(0);
                status.setRemainedTime(firstSong.getDuration());
                search.setSelectedSong(firstSong);
            }
            status.setPaused(false);
            super.timeLoaded = timestamp;
            super.isLoaded = true;
        }
    }

    @Override
    public void like(final UserInput user, final ObjectNode objectNode) {
        new SongPlayer().like(user, objectNode);
    }
    private void handleNoRepeat() {
        timeElapsed = timeElapsed - status.getRemainedTime();
        SongInput nextSong = Playlist.nextSong(search.getSelectedSong(), search.getSelectedPlaylist());
        if (nextSong.getName().equals(search.getSelectedPlaylist().getSongs().get(0).getName()) && status.getRepeat().equals("No Repeat")) {
            status.reset();
            search.setSelectedSong(null);
            this.isLoaded = false;
            return;
        }
        status.setRemainedTime(nextSong.getDuration());
        status.setName(nextSong.getName());
        search.setSelectedSong(nextSong);
        if (timeElapsed >= nextSong.getDuration()) {
            handleNoRepeat();
        }
    }
    @Override
    public void updateStatus(final Integer timestamp, final UserInput user) {
        if (status.isPaused()) {
            return;
        }
        if(search.getSelectedSong() == null) {
            return;
        }
        status.setName(search.getSelectedSong().getName());

        timeElapsed = timestamp - timeLoaded;
        if (status.getRemainedTime() > timeElapsed) {
            status.setRemainedTime(status.getRemainedTime() - timeElapsed);
            super.timeLoaded = timestamp;
            return;
        }
        switch (status.getRepeat()) {
            case "No Repeat":
            case "Repeat All":
                handleNoRepeat();
                if(search.getSelectedSong() != null) {
                    status.setRemainedTime(search.getSelectedSong().getDuration() - timeElapsed);
                }
                break;
            case "Repeat Current Song":
                timeElapsed -= status.getRemainedTime();
                timeElapsed = timeElapsed % search.getSelectedSong().getDuration();
                status.setRemainedTime(search.getSelectedSong().getDuration() - timeElapsed);
                break;
            default:
                break;
        }
        super.timeLoaded = timestamp;
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
    public void shuffle(final ObjectNode objectNode, final long seed) {
        if (status.isShuffle()) {
            Playlist.unshuffleSongs(search.getSelectedPlaylist());
            status.setShuffle(false);
            objectNode.put("message", "Shuffle function deactivated successfully.");
        } else {
            Playlist.shuffleSongs(search.getSelectedPlaylist(), seed);
            status.setShuffle(true);
            objectNode.put("message", "Shuffle function activated successfully.");
        }
    }
}
