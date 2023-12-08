package media.podcast;

import java.util.ArrayList;

import fileio.input.PodcastInput;
import fileio.input.EpisodeInput;
import fileio.input.LibraryInput;

public final class Podcast {
    private String name;
    private String owner;
    private ArrayList<Episode> episodes;
    public Podcast(final String owner) {
        this.owner = owner;
    }

    public Podcast(final PodcastInput podcast) {
        this.name = podcast.getName();
        this.owner = podcast.getOwner();
        this.episodes = new ArrayList<>();
        for (EpisodeInput episodeInput : podcast.getEpisodes()) {
            Episode episode = new Episode(episodeInput);
            this.episodes.add(episode);
        }
    }

    /**
     * This is a method that returns a copy of the input podcasts,
     * in order to perform additional processing.
     *
     * @param library The input library.
     * @return A new ArrayList containing copies of the original podcasts.
     */
    public static ArrayList<Podcast> copyPodcasts(final LibraryInput library) {
        ArrayList<Podcast> podcasts = new ArrayList<>();
        for (PodcastInput podcastInput : library.getPodcasts()) {
            Podcast podcast = new Podcast(podcastInput);
            podcasts.add(podcast);
        }
        return podcasts;
    }

    public static boolean exists(final ArrayList<Podcast> podcasts, final String name) {
        for (Podcast podcast : podcasts) {
            if (podcast.getName().equals(name)) {
                return true;
            }
        }

        return false;
    }

    /**
     * This method returns the next episode of the podcast.
     *
     * @param episode The current episode.
     * @return The next episode.
     */
    public Episode nextEpisode(final Episode episode) {
        for (int i = 0; i < this.getEpisodes().size() - 1; i++) {
            if (this.getEpisodes().get(i).getName().equals(episode.getName())) {
                return this.getEpisodes().get(i + 1);
            }
        }
        return firstEpisode();
    }

    /**
     * This method returns the first episode of the current podcast.
     *
     * @return The first episode.
     */
    public Episode firstEpisode() {
        return this.getEpisodes().get(0);
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(final String owner) {
        this.owner = owner;
    }

    public ArrayList<Episode> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(final ArrayList<Episode> episodes) {
        this.episodes = episodes;
    }
}
