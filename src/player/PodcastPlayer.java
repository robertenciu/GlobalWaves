package player;

import media.Episode;
import media.Podcast;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.User;

public final class PodcastPlayer extends AbstractPlayer {
    public PodcastPlayer(final Podcast podcast) {
        super.loadedPodcast = podcast;
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
    public void load(final Integer timestamp, final User user) {
        Podcast podcast = loadedPodcast;
        if (!user.getLastEpisodes().containsKey(podcast.getName())) {
            user.getLastEpisodes().put(podcast.getName(), podcast.getEpisodes().get(0));
        }

        super.loadedEpisode = lastEpisode(user);
        super.isLoaded = true;

        status.setPaused(false);
        status.setRemainedTime(loadedEpisode.getDuration());
        status.setName(loadedEpisode.getName());

        super.timeUpdated = timestamp;
    }

    public void forward(final User user, final ObjectNode obj) {
        if (loadedEpisode.getDuration() >= 90) {
            loadedEpisode.setDuration(loadedEpisode.getDuration() - 90);
            status.setRemainedTime(status.getRemainedTime() - 90);
        } else {
            loadedEpisode.setDuration(0);
            Episode nextEpisode = nextEpisode(user);
            loadedEpisode = nextEpisode;
            status.setRemainedTime(nextEpisode.getDuration());
            user.getLastEpisodes().put(loadedPodcast.getName(), nextEpisode);
        }
        obj.put("message", "Skipped forward successfully.");
    }

    public void backward(final User user, final ObjectNode obj) {
        if (loadedEpisode.getDuration() + 90 <= loadedEpisode.getInitialDuration()) {
            loadedEpisode.setDuration(loadedEpisode.getDuration() + 90);
            status.setRemainedTime(status.getRemainedTime() + 90);
        } else {
            status.setRemainedTime(loadedEpisode.getInitialDuration());
            loadedEpisode.setDuration(loadedEpisode.getInitialDuration());
        }
        obj.put("message", "Rewound successfully.");
    }

    private void getCurrentEpisode(Integer timeElapsed, final User user) {
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
        super.loadedEpisode = null;
        super.isLoaded = false;
    }
    @Override
    public void updateStatus(final Integer timestamp, final User user) {
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
                this.getCurrentEpisode(timeElapsed, user);
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

    public void next(final User user, final ObjectNode obj, final Integer timestamp) {
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
        obj.put("message", "Skipped to next track successfully. The current track is " +
                status.getName() + ".");
    }
}
