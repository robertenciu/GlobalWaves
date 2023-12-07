package media;

import fileio.input.LibraryInput;
import media.music.Song;
import user.Artist;
import user.Host;
import user.User;

import java.util.ArrayList;

public final class Library {
    private final ArrayList<Song> songs;
    private final ArrayList<Podcast> podcasts;
    private final ArrayList<Playlist> playlists;
    private final ArrayList<Album> albums;
    private final ArrayList<User> users;
    private final ArrayList<Host> hosts;
    private final ArrayList<Artist> artists;
    private final ArrayList<Event> events;
    private final ArrayList<Merch> merches;
    public Library(final LibraryInput libraryInput) {
        this.songs = Song.copySongs(libraryInput);
        this.podcasts = Podcast.copyPodcasts(libraryInput);
        this.users = User.copyUsers(libraryInput);
        this.playlists = new ArrayList<>();
        this.hosts = new ArrayList<>();
        this.artists = new ArrayList<>();
        this.albums = new ArrayList<>();
        this.events = new ArrayList<>();
        this.merches = new ArrayList<>();
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }

    public ArrayList<Podcast> getPodcasts() {
        return podcasts;
    }

    public ArrayList<Playlist> getPlaylists() {
        return playlists;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public ArrayList<Host> getHosts() {
        return hosts;
    }

    public ArrayList<Artist> getArtists() {
        return artists;
    }

    public ArrayList<Album> getAlbums() {
        return albums;
    }

    public ArrayList<Merch> getMerches() {
        return merches;
    }

    public ArrayList<Event> getEvents() {
        return events;
    }
}
