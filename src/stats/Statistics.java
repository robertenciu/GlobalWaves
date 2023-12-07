package stats;

import media.Library;
import media.Song;
import media.Playlist;
import user.User;

import java.util.ArrayList;

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

        ArrayList<Song> songs = library.getSongs();
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

        ArrayList<Playlist> playlists = library.getPlaylists();
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

        ArrayList<User> users = library.getUsers();
        for (User user : users) {
            allUsers.add(user.getUsername());
        }
        // Add artists and hosts

        return allUsers;
    }

    public void setLibrary(final Library library) {
        this.library = library;
    }
}
