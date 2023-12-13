package media.podcast;

import fileio.input.EpisodeInput;

public final class Episode {
    private String name;
    private Integer duration;
    private Integer initialDuration;
    private String description;
    public Episode() {
    }

    public Episode(final EpisodeInput episode) {
        this.description = episode.getDescription();
        this.name = episode.getName();
        this.duration = episode.getDuration();
        this.initialDuration = episode.getDuration();
    }

    public Episode(final Episode episode) {
        this.name = episode.name;
        this.duration = episode.duration;
        this.initialDuration = episode.initialDuration;
        this.description = episode.description;
    }
    public Integer getInitialDuration() {
        return initialDuration;
    }

    public void setInitialDuration(final Integer initialDuration) {
        this.initialDuration = initialDuration;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(final Integer duration) {
        this.duration = duration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }
}

