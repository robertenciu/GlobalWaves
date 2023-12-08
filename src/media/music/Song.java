package media.music;

import fileio.input.LibraryInput;
import fileio.input.SongInput;
import media.Library;
import user.Artist;
import user.User;

import java.util.ArrayList;

public final class Song {
    private String name;
    private Integer duration;
    private String album;
    private ArrayList<String> tags;
    private String lyrics;
    private String genre;
    private Integer releaseYear;
    private String artist;
    private int likes;
    private ArrayList<User> likedBy = new ArrayList<>();

    public Song() { }

    public Song(final SongInput song) {
        this.name = song.getName();
        this.duration = song.getDuration();
        this.album = song.getAlbum();
        this.tags = song.getTags();
        this.lyrics = song.getLyrics();
        this.genre = song.getGenre();
        this.releaseYear = song.getReleaseYear();
        this.artist = song.getArtist();
        this.likedBy = new ArrayList<>();
    }

    /**
     * This is a method that returns a copy of the input songs
     * in order to perform additional processing.
     *
     * @param library The input library.
     * @return A new ArrayList containing copies of the original songs.
     */
    public static ArrayList<Song> copySongs(final LibraryInput library) {
        ArrayList<Song> songs = new ArrayList<>();
        for (SongInput songInput : library.getSongs()) {
            Song song = new Song(songInput);
            songs.add(song);
        }
        return songs;
    }

    public void updateInteracting(final User user,
                                  final boolean isInteracting) {
        Library library = Library.getInstance();
        Artist songArtist = Artist.getArtist(library.getArtists(), this.artist);

        if (songArtist != null) {
            if (isInteracting) {
                songArtist.setInteracting(true);
                songArtist.getUsersInteracting().add(user);
            } else {
                songArtist.getUsersInteracting().remove(user);
                if (songArtist.getUsersInteracting().isEmpty()) {
                    songArtist.setInteracting(false);
                }
            }
        }
    }
    public ArrayList<User> getLikedBy() {
        return likedBy;
    }

    public void setReleaseYear(final Integer releaseYear) {
        this.releaseYear = releaseYear;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(final int likes) {
        this.likes = likes;
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

    public String getAlbum() {
        return album;
    }

    public void setAlbum(final String album) {
        this.album = album;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(final ArrayList<String> tags) {
        this.tags = tags;
    }

    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(final String lyrics) {
        this.lyrics = lyrics;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(final String genre) {
        this.genre = genre;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(final int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(final String artist) {
        this.artist = artist;
    }
}
