package Media;

import fileio.input.*;

public final class Episode {
    private String name;
    private Integer duration;
    private Integer initialDuration;
    private String description;

    public Episode(EpisodeInput episode) {
        this.description = episode.getDescription();
        this.name = episode.getName();
        this.duration = episode.getDuration();
        this.initialDuration = episode.getDuration();
    }

    public void setInitialDuration(Integer initialDuration) {
        this.initialDuration = initialDuration;
    }

    public Integer getInitialDuration() {
        return initialDuration;
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

