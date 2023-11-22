package Player;

import Media.Episode;
import Media.Podcast;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.User;

public final class PodcastPlayer extends AbstractPlayer {
    public PodcastPlayer(final Podcast podcast) {
        super.loadedPodcast = podcast;
    }

    private Episode nextEpisode(final User user) {
        int lastEpisodeIndex = search.getSelectedPodcast().getEpisodes().indexOf(lastEpisode(user));
        return search.getSelectedPodcast().getEpisodes().get(lastEpisodeIndex + 1);
    }

    private Episode lastEpisode(final User user) {
        return user.getLastEpisodes().get(search.getSelectedPodcast().getName());
    }

    private Episode firstEpisode(final User user) {
        return search.getSelectedPodcast().getEpisodes().get(0);
    }

    @Override
    public void load(final Integer timestamp, final User user) {
        Podcast podcast = search.getSelectedPodcast();
        if (!user.getLastEpisodes().containsKey(podcast.getName())) {
            user.getLastEpisodes().put(podcast.getName(), podcast.getEpisodes().get(0));
        }
        Episode episode = lastEpisode(user);
        episode.setInitialDuration(episode.getDuration());
        status.setPaused(false);
        status.setRemainedTime(episode.getDuration());
        super.timeLoaded = timestamp;
        super.isLoaded = true;
    }

    public void forward(final User user, final ObjectNode obj) {
        Episode lastEpisode = lastEpisode(user);
        if (lastEpisode.getDuration() >= 90) {
            lastEpisode.setDuration(lastEpisode.getDuration() - 90);
            status.setRemainedTime(status.getRemainedTime() - 90);
        } else {
            Episode nextEpisode = nextEpisode(user);
            status.setRemainedTime(nextEpisode.getDuration());
            lastEpisode.setDuration(0);
            user.getLastEpisodes().put(search.getSelectedPodcast().getName(), nextEpisode);
        }
        obj.put("message", "Skipped forward successfully.");
    }

    public void backward(final User user, final ObjectNode obj) {
        Episode lastEpisode = lastEpisode(user);
        if (lastEpisode.getDuration() + 90 <= lastEpisode.getInitialDuration()) {
            lastEpisode.setDuration(lastEpisode.getDuration() + 90);
            status.setRemainedTime(status.getRemainedTime() + 90);
        } else {
            status.setRemainedTime(lastEpisode.getInitialDuration());
            lastEpisode.setDuration(lastEpisode.getInitialDuration());
        }
        obj.put("message", "Rewound successfully.");
    }

    void handleNoRepeat(Integer timeElapsed, final User user, Episode episode) {
        timeElapsed = timeElapsed - lastEpisode(user).getDuration();
        episode.setDuration(0);
        episode = nextEpisode(user);
        episode.setInitialDuration(episode.getDuration());
        user.getLastEpisodes().put(search.getSelectedPodcast().getName(), episode);
        episode.setDuration(episode.getDuration() - timeElapsed);
        if (timeElapsed >= episode.getDuration()) {
            handleNoRepeat(timeElapsed, user, episode);
        }
    }

    @Override
    public void updateStatus(final Integer timestamp, final User user) {
        if (status.isPaused()) {
            status.setName(lastEpisode(user).getName());
            status.setRemainedTime(lastEpisode(user).getDuration());
            return;
        }
        Episode episode = lastEpisode(user);
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
    }

    public void next(final User user, final ObjectNode obj, final Integer timestamp) {
        Episode nextEpisode = nextEpisode(user);
        switch (status.getRepeat()) {
            case "No Repeat":
                if (nextEpisode.equals(firstEpisode(user))) {
                    status.reset();
                    isLoaded = false;
                } else {
                    user.getLastEpisodes().put(search.getSelectedPodcast().getName(), nextEpisode);
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
        timeLoaded = timestamp;
        obj.put("message", "Skipped to next track successfully. The current track is " +
                status.getName() + ".");
    }
}
