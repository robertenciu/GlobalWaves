package main;

public final class Commands {
    private String command;
    private String username;
    private Integer playlistId;
    private long seed;
    private String playlistName;
    private Integer timestamp;
    private Integer itemNumber;
    private String type;
    private Filters filters;
    public Commands() { }

    public long getSeed() {
        return seed;
    }

    public void setSeed(final long seed) { this.seed = seed; }

    public String getCommand() { return command; }

    public void setCommand(final String command) {
        this.command = command;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public Integer getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(final Integer playlistId) {
        this.playlistId = playlistId;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public void setPlaylistName(final String playlistName) {
        this.playlistName = playlistName;
    }

    public Integer getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(final Integer timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getItemNumber() {
        return itemNumber;
    }

    public void setItemNumber(final Integer itemNumber) {
        this.itemNumber = itemNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public Filters getFilters() {
        return filters;
    }

    public void setFilters(final Filters filters) {
        this.filters = filters;
    }
}
