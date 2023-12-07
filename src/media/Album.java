package media;

import media.music.Song;

import java.util.ArrayList;

public class Album {
    private final String owner;
    private String name;
    private int releaseYear;
    private String description;
    private ArrayList<Song> songs = new ArrayList<>();
    public Album (String owner) {
        this.owner = owner;
    }
    public static boolean exists(final ArrayList<Album> albums, final String name) {
        for (Album album : albums) {
            if (album.getName().equals(name)) {
                return true;
            }
        }

        return false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }

    public String getOwner() {
        return owner;
    }

    public void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
    }
}
