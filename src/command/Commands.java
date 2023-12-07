package command;

import media.music.Song;

import java.util.ArrayList;

public final class Commands {
    private String command;
    private String username;
    private String name;
    private Integer playlistId;
    private long seed;
    private String playlistName;
    private int releaseYear;
    private int age;
    private String city;
    private String description;
    private Integer timestamp;
    private Integer itemNumber;
    private String type;
    private Filters filters;
    private int price;
    private String date;
    private ArrayList<Song> songs;
    public Commands() { }

    public int getPrice() {
        return price;
    }

    public void setPrice(final int price) {
        this.price = price;
    }

    public String getDate() {
        return date;
    }

    public void setDate(final String date) {
        this.date = date;
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }

    public void setSongs(final ArrayList<Song> songs) {
        this.songs = songs;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public long getSeed() {
        return seed;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(final int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public int getAge() {
        return age;
    }

    public void setAge(final int age) {
        this.age = age;
    }

    public String getCity() {
        return city;
    }

    public void setCity(final String city) {
        this.city = city;
    }

    public void setSeed(final long seed) {
        this.seed = seed;
    }

    public String getCommand() {
        return command;
    }

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
