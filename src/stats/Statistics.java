package stats;

import media.Library;
import media.music.Album;
import media.music.Song;
import media.music.Playlist;
import user.Artist;
import user.Host;
import user.User;

import java.util.ArrayList;
import java.util.Collections;

public final class Statistics {
    private Library library;
    private final int topMaxSize = 5;
    private static Statistics instance = null;

    private Statistics() { }

    /**
     * SINGLETON PATTERN CLASS.
     * This method returns the instance of the class and creates one if it doesn't exist.
     *
     * @return The instance of the class.
     */
    public static Statistics getInstance() {
        if (instance == null) {
            instance = new Statistics();
        }
        return instance;
    }

    /**
     * @return The first 5 most liked songs.
     */
    public ArrayList<String> getTop5Songs() {
        ArrayList<String> top = new ArrayList<>();

        ArrayList<Song> songs = new ArrayList<>(library.getSongs());
        songs.sort((o1, o2) -> {
            if (o1.getLikes() < o2.getLikes()) {
                return 1;
            } else if (o1.getLikes() > o2.getLikes()) {
                return -1;
            }
            return 0;
        });

        for (Song song : songs.subList(0, topMaxSize)) {
            top.add(song.getName());
        }

        return top;
    }

    /**
     * @return The first 5 most followed playlists.
     */
    public ArrayList<String> getTop5Playlists() {
        ArrayList<String> top = new ArrayList<>();

        ArrayList<Playlist> playlists = new ArrayList<>(library.getPlaylists());
        playlists.sort((o1, o2) -> {
            if (o1.getFollowers() < o2.getFollowers()) {
                return 1;
            } else if (o1.getFollowers() > o2.getFollowers()) {
                return -1;
            }
            return 0;
        });

        int iter = 0;
        for (Playlist playlist : playlists) {
            if (iter == topMaxSize) {
                break;
            }
            if (playlist.getVisibility().equals("public")) {
                top.add(playlist.getName());
                iter++;
            }
        }

        return top;
    }

    public ArrayList<String> getTop5Albums() {
        ArrayList<String> top5Albums = new ArrayList<>();

        ArrayList<Album> AlbumsCopy = new ArrayList<>(library.getAlbums());
        AlbumsCopy.sort(((o1, o2) -> {
            if (o1.totalLikes() == o2.totalLikes()) {
                return o1.getName().compareTo(o2.getName());
            }
            return o2.totalLikes() - o1.totalLikes();
        }));

        int iter = 0;
        for (Album album : AlbumsCopy) {
            if (iter++ == topMaxSize) {
                break;
            }
            top5Albums.add(album.getName());
        }

        return top5Albums;
    }

    public ArrayList<String> getTop5Artists() {
        ArrayList<String> top5Artists = new ArrayList<>();

        ArrayList<Artist> ArtistCopy = new ArrayList<>(library.getArtists());
        ArtistCopy.sort(((o1, o2) -> {
            int o1TotalLikes = 0;
            for (Album album : o1.getAlbums()) {
                o1TotalLikes += album.totalLikes();
            }

            int o2TotalLikes = 0;
            for (Album album : o2.getAlbums()) {
                o2TotalLikes += album.totalLikes();
            }

            return o2TotalLikes - o1TotalLikes;
        }));

        int iter = 0;
        for (Artist artist : ArtistCopy) {
            if (iter++ == topMaxSize) {
                break;
            }
            top5Artists.add(artist.getUsername());
        }

        return top5Artists;
    }

    public ArrayList<String> getOnlineUsers() {
        ArrayList<String> onlineUsers = new ArrayList<>();

        ArrayList<User> users = library.getUsers();
        for (User user : users) {
            if (user.getConnectionStatus().equals("Online")) {
                onlineUsers.add(user.getUsername());
            }
        }

        return onlineUsers;
    }

    public ArrayList<String> getAllUsers() {
        ArrayList<String> allUsers = new ArrayList<>();

        for (User user : library.getUsers()) {
            allUsers.add(user.getUsername());
        }

        for (Artist artist : library.getArtists()) {
            allUsers.add(artist.getUsername());
        }

        for (Host host : library.getHosts()) {
            allUsers.add((host.getUsername()));
        }

        return allUsers;
    }

    public void setLibrary(final Library library) {
        this.library = library;
    }
}
