package media;

import fileio.input.LibraryInput;
import user.User;

import java.util.ArrayList;

public final class Library {
    private final ArrayList<Song> songs;
    private final ArrayList<Podcast> podcasts;
    private final ArrayList<Playlist> playlists;

    private final ArrayList<User> users;
    public Library(final LibraryInput libraryInput) {
        this.songs = Song.copySongs(libraryInput);
        this.podcasts = Podcast.copyPodcasts(libraryInput);
        this.users = User.copyUsers(libraryInput);
        this.playlists = new ArrayList<>();
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
}
