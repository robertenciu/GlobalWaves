package player;

import media.Episode;
import media.Podcast;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.User;

public final class PodcastPlayer extends Player {
    private final Podcast loadedPodcast;
    private Episode loadedEpisode;
    public PodcastPlayer(final Podcast podcast) {
        this.loadedPodcast = podcast;
    }

    private Episode nextEpisode(final User user) {
        int lastEpisodeIndex = loadedPodcast.getEpisodes().indexOf(lastEpisode(user));
        return loadedPodcast.getEpisodes().get(lastEpisodeIndex + 1);
    }

    private Episode lastEpisode(final User user) {
        return user.getLastEpisodes().get(loadedPodcast.getName());
    }

    private Episode firstEpisode() {
        return loadedPodcast.getEpisodes().get(0);
    }

    @Override
    public void load(final Integer timestamp) {
        Podcast podcast = loadedPodcast;
        if (!user.getLastEpisodes().containsKey(podcast.getName())) {
            user.getLastEpisodes().put(podcast.getName(), podcast.getEpisodes().get(0));
        }

        loadedEpisode = lastEpisode(user);
        super.isLoaded = true;

        status.setPaused(false);
        status.setRemainedTime(loadedEpisode.getDuration());
        status.setName(loadedEpisode.getName());

        super.timeUpdated = timestamp;
    }

    @Override
    public void forward(final ObjectNode obj) {
        final int amount = 90;
        if (loadedEpisode.getDuration() >= amount) {
            loadedEpisode.setDuration(loadedEpisode.getDuration() - amount);
            status.setRemainedTime(status.getRemainedTime() - amount);
        } else {
            loadedEpisode.setDuration(loadedEpisode.getInitialDuration());
            Episode nextEpisode = nextEpisode(user);
            loadedEpisode = nextEpisode;
            status.setRemainedTime(nextEpisode.getDuration());
            user.getLastEpisodes().put(loadedPodcast.getName(), nextEpisode);
        }
        obj.put("message", "Skipped forward successfully.");
    }

    @Override
    public void backward(final ObjectNode obj) {
        final int amount = 90;
        if (loadedEpisode.getDuration() + amount <= loadedEpisode.getInitialDuration()) {
            loadedEpisode.setDuration(loadedEpisode.getDuration() + amount);
            status.setRemainedTime(status.getRemainedTime() + amount);
        } else {
            status.setRemainedTime(loadedEpisode.getInitialDuration());
            loadedEpisode.setDuration(loadedEpisode.getInitialDuration());
        }
        obj.put("message", "Rewound successfully.");
    }

    @Override
    public void next(final ObjectNode obj, final Integer timestamp) {
        Episode nextEpisode = nextEpisode(user);
        switch (status.getRepeat()) {
            case "No Repeat":
                if (nextEpisode.equals(firstEpisode())) {
                    status.reset();
                    isLoaded = false;
                } else {
                    user.getLastEpisodes().put(loadedPodcast.getName(), nextEpisode);
                    status.setRemainedTime(nextEpisode.getDuration());
                    status.setName(nextEpisode.getName());
                }
                break;
            case "Repeat Once":
            case "Repeat Infinite":
                status.setRemainedTime(nextEpisode.getDuration());
                status.setName(nextEpisode.getName());
                break;
            default:
                break;
        }

        super.timeUpdated = timestamp;
        obj.put("message", "Skipped to next track successfully. The current track is "
                + status.getName() + ".");
    }

    private void getCurrentEpisode(final Integer timeElapsed) {
        int timeRemaining = timeElapsed;
        Episode nextEpisode;
        do {
            timeRemaining = timeRemaining - loadedEpisode.getDuration();
            loadedEpisode.setDuration(loadedEpisode.getInitialDuration());
            nextEpisode = nextEpisode(user);
            assert nextEpisode != null;

            if (nextEpisode.equals(firstEpisode())) {
                handleNoRepeat();
                return;
            }

            loadedEpisode = nextEpisode;
            user.getLastEpisodes().put(loadedPodcast.getName(), loadedEpisode);
        } while (timeRemaining >= nextEpisode.getDuration());

        status.setName(loadedEpisode.getName());
        status.setRemainedTime(loadedEpisode.getDuration() - timeRemaining);
        loadedEpisode.setDuration(status.getRemainedTime());
    }
    private void handleNoRepeat() {
        status.reset();
        loadedEpisode.setDuration(loadedEpisode.getInitialDuration());
        loadedEpisode = null;
        super.isLoaded = false;
    }
    @Override
    public void updateStatus(final Integer timestamp) {
        if (status.isPaused() || loadedEpisode == null) {
            return;
        }

        int timeElapsed = timestamp - super.timeUpdated;

        if (status.getRemainedTime() > timeElapsed) {
            status.setRemainedTime(status.getRemainedTime() - timeElapsed);
            loadedEpisode.setDuration(status.getRemainedTime());
            super.timeUpdated = timestamp;
            return;
        }
        switch (status.getRepeat()) {
            case "No Repeat":
                this.getCurrentEpisode(timeElapsed);
                break;
            case "Repeat Once":
                if (timeElapsed > loadedEpisode.getDuration() + status.getRemainedTime()) {
                    this.handleNoRepeat();
                } else {
                    timeElapsed = timestamp - (timeUpdated + status.getRemainedTime());
                    status.setRemainedTime(loadedEpisode.getInitialDuration() - timeElapsed);
                    status.setRepeat("No Repeat");
                    loadedEpisode.setDuration(status.getRemainedTime());
                }
                break;
            case "Repeat Infinite":
                timeElapsed -= status.getRemainedTime();
                timeElapsed %= loadedEpisode.getInitialDuration();
                status.setRemainedTime(loadedEpisode.getInitialDuration() - timeElapsed);
                loadedEpisode.setDuration(status.getRemainedTime());
                break;
            default:
                break;
        }
        super.timeUpdated = timestamp;
    }

    @Override
    public void repeat() {
        new SongPlayer().repeat();
    }

}
