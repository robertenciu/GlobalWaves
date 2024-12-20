package player;

public final class Status {
    private String name;
    private Integer remainedTime;
    private String repeat;
    private boolean shuffle;
    private boolean paused;

    public Status() {
        name = "";
        remainedTime = 0;
        repeat = "No Repeat";
        shuffle = false;
        paused = true;
    }

    /**
     * Resets status to the implicit values.
     */
    public void reset() {
        name = "";
        remainedTime = 0;
        repeat = "No Repeat";
        shuffle = false;
        paused = true;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Integer getRemainedTime() {
        return remainedTime;
    }

    public void setRemainedTime(final Integer remainedTime) {
        this.remainedTime = remainedTime;
    }

    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(final String repeat) {
        this.repeat = repeat;
    }

    public boolean isShuffle() {
        return shuffle;
    }

    public void setShuffle(final boolean shuffle) {
        this.shuffle = shuffle;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(final boolean paused) {
        this.paused = paused;
    }
}
