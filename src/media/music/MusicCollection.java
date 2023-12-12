package media.music;

import media.Library;
import user.Artist;
import user.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public abstract class MusicCollection {
    protected String owner;
    protected String name;
    protected ArrayList<Song> songs;
    protected ArrayList<Song> originalOrder;
    protected MusicCollectionType type;

    public MusicCollection() { }

    public MusicCollection(MusicCollection other) {
        this.owner = other.owner;
        this.name = other.name;
        this.songs = new ArrayList<>(other.songs);
        this.originalOrder = other.originalOrder;
        this.type = other.type;
    }

    /**
     * Shuffling the playlist's songs based on a seed.
     * Retaining a copy of the original order in order to reverse.
     *
     * @param seed The seed.
     */
    public void shuffleSongs(final long seed) {
        Random random = new Random(seed);
        this.originalOrder = new ArrayList<>(this.getSongs());
        Collections.shuffle(this.getSongs(), random);
    }

    /**
     * Restoring the playlist's songs to the original order.
     */
    public void unshuffleSongs() {
        Collections.copy(this.getSongs(), this.originalOrder);
    }

    /**
     * This method returns the next song in the playlist.
     *
     * @param song The current song playing.
     * @return The next song.
     */
    public Song nextSong(final Song song) {
        for (int i = 0; i < this.getSongs().size() - 1; i++) {
            if (this.getSongs().get(i).getName().equals(song.getName())) {
                return this.getSongs().get(i + 1);
            }
        }
        return firstSong();
    }

    /**
     * This method returns the previous song in the playlist.
     * Returns the last song if the current song is the first song.
     *
     * @param song The current song.
     * @return The previous song.
     */
    public Song prevSong(final Song song) {
        for (int i = 1; i < this.getSongs().size(); i++) {
            if (this.getSongs().get(i).getName().equals(song.getName())) {
                return this.getSongs().get(i - 1);
            }
        }
        return lastSong();
    }

    /**
     * This method returns the first song of the playlist.
     *
     * @return The first song.
     */
    public Song firstSong() {
        return this.getSongs().get(0);
    }

    /**
     * This method returns the last song of the playlist.
     *
     * @return The last song.
     */
    public Song lastSong() {
        return this.getSongs().get(this.getSongs().size() - 1);
    }

    public int totalLikes() {
        int likes = 0;
        for (Song song : songs) {
            likes += song.getLikes();
        }
        return likes;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }

    public void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
    }

    public ArrayList<Song> getOriginalOrder() {
        return originalOrder;
    }

    public MusicCollectionType getType() {
        return type;
    }

    public void setType(MusicCollectionType type) {
        this.type = type;
    }

}
