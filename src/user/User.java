package user;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import command.Commands;
import media.Library;
import player.Status;
import media.music.Song;
import media.Playlist;
import media.Episode;
import fileio.input.UserInput;
import fileio.input.LibraryInput;
import player.Player;
import searchbar.Search;

import java.util.ArrayList;
import java.util.HashMap;

public class User {
    private String username;
    private int age;
    private String city;
    private final ArrayList<Song> likedSongs;
    private final ArrayList<Playlist> playlists;
    private final HashMap<String, Episode> lastEpisodes;
    private final ArrayList<Playlist> followedPlaylists;
    private Search search;
    private Player player;
    private Status status;
    private final int maxResults = 5;
    private String connectionStatus;
    protected Page page;

    public User() {
        this.likedSongs = new ArrayList<>();
        this.playlists = new ArrayList<>();
        this.lastEpisodes = new HashMap<>();
        this.followedPlaylists = new ArrayList<>();
        this.page = Page.HOME;
    }

    public User(final UserInput user) {
        this.likedSongs = new ArrayList<>();
        this.playlists = new ArrayList<>();
        this.lastEpisodes = new HashMap<>();
        this.followedPlaylists = new ArrayList<>();
        this.username = user.getUsername();
        this.age = user.getAge();
        this.city = user.getCity();
        this.page = Page.HOME;
        this.connectionStatus = "Online";
    }

    /**
     * This is a method that returns a copy of the input users,
     * in order to perform additional processing.
     *
     * @param library The input library.
     * @return A new ArrayList containing copies of the original users.
     */
    public static ArrayList<User> copyUsers(final LibraryInput library) {
        ArrayList<User> users = new ArrayList<>();
        for (UserInput userInput : library.getUsers()) {
            User user = new User(userInput);
            users.add(user);
        }
        return users;
    }

    /**
     * This is a method that returns a user based on the provided username from a list of users.
     *
     * @param library   The list of users.
     * @param command The username.
     * @return The user object matching the given username.
     */
    public static User getUser(final Library library, final Commands command) {
        User currentUser = null;
        for (User user : library.getUsers()) {
            if (user.getUsername().equals(command.getUsername())) {
                currentUser = user;
                break;
            }
        }

        if (currentUser == null) {
            currentUser = Artist.getArtist(library.getArtists(), command);
        }

//        if (currentUser == null) {
//           currentUser = Host.getHost(library.getHosts(), command);
//        }

        return currentUser;
    }

    /**
     * Method that returns a playlist based on a specific id.
     *
     * @param playlistId The id.
     * @return The playlist corresponding to id.
     */
    public Playlist getPlaylist(final Integer playlistId) {
        for (Playlist playlist : playlists) {
            if (playlist.getPlaylistId().equals(playlistId)) {
                return playlist;
            }
        }
        return null;
    }

    /**
     * Method that returns the last played episode by the user on a specific podcast.
     *
     * @param podcastName The name of the podcast.
     * @return The last episode played.
     */
    public Episode lastEpisode(final String podcastName) {
        return this.getLastEpisodes().get(podcastName);
    }

    /**
     * Method that lists the playlists of the user in an objectNode.
     *
     * @param objectNode The objectNode.
     */
    public void showPlaylists(final ObjectNode objectNode) {
        ArrayNode result = objectNode.putArray("result");

        for (Playlist playlist : this.getPlaylists()) {
            ObjectNode playlistObject = result.addObject();
            playlistObject.put("name", playlist.getName());
            ArrayNode songsArray = playlistObject.putArray("songs");
            for (Song song : playlist.getOriginalOrder()) {
                songsArray.add(song.getName());
            }
            playlistObject.put("visibility", playlist.getVisibility());
            playlistObject.put("followers", playlist.getFollowers());
        }
    }

    /**
     * This method switches the visibility of the playlist gotten by an id.
     * Puts the specific messages in the objectNode.
     *
     * @param playlistId The id.
     * @param objectNode The objectNode that needs to hold the message.
     */
    public void switchVisibility(final Integer playlistId, final ObjectNode objectNode) {
        Playlist playlist = getPlaylist(playlistId);
        assert playlist != null;

        if (playlist.getVisibility().equals("public")) {
            playlist.setVisibility("private");
            objectNode.put("message", "Visibility status updated successfully to private.");
        } else {
            playlist.setVisibility("public");
            objectNode.put("message", "Visibility status updated successfully to public.");
        }
    }

    /**
     * This method lists user's preferred songs in the objectNode.
     *
     * @param objectNode The objectNode.
     */
    public void showPreferredSongs(final ObjectNode objectNode) {
        ArrayNode result = objectNode.putArray("result");
        if (likedSongs.isEmpty()) {
            return;
        }
        for (Song song : likedSongs) {
            if (song != null) {
                result.add(song.getName());
            }
        }
    }

    public final String switchConnectionStatus(Integer timestamp) {
        if (this.connectionStatus.equals("Online")) {
            if (player != null && player.isLoaded()) {
                player.updateStatus(timestamp);
            }
            this.connectionStatus = "Offline";
        } else {
            this.connectionStatus = "Online";
        }

        return this.username + " has changed status successfully.";
    }
    private String printHome() {
        StringBuilder result = new StringBuilder("Liked songs:\n\t[");

        ArrayList<Song> topLikedsongs = new ArrayList<>(likedSongs);
        topLikedsongs.sort((o1, o2) -> {
            if (o1.getLikes() < o2.getLikes()) {
                return 1;
            } else if (o1.getLikes() > o2.getLikes()) {
                return -1;
            }
            return 0;
        });

        int resultCount = Math.min(topLikedsongs.size(), maxResults);
        for (int i = 0; i < resultCount; i++) {
            result.append(topLikedsongs.get(i).getName()).append(", ");
        }
        if (resultCount > 0) {
            result.deleteCharAt(result.length() - 1);
            result.deleteCharAt(result.length() - 1);
        }

        result.append("]\n\nFollowed playlists:\n\t[");

        ArrayList<Playlist> topFollowedPlaylists = new ArrayList<>(followedPlaylists);
        topFollowedPlaylists.sort((o1, o2) -> {
            if (o1.totalLikes() < o2.totalLikes()) {
                return 1;
            } else if (o1.totalLikes() > o2.totalLikes()) {
                return -1;
            }
            return 0;
        });

        resultCount = Math.min(topFollowedPlaylists.size(), maxResults);
        for (int i = 0; i < resultCount; i++) {
            result.append(topFollowedPlaylists.get(i).getName()).append(", ");
        }
        if (resultCount > 0) {
            result.deleteCharAt(result.length() - 1);
            result.deleteCharAt(result.length() - 1);
        }
        result.append("]");

        return result.toString();
    }

    private String printLikedContent() {
        return null;
    }
    public final String printCurrentPage() {
        if (page == Page.HOME) {
            return printHome();
        }

        if (page == Page.LIKED_CONTENT) {
            return printLikedContent();
        }

        if (page == Page.ARTIST) {
            return search.getSelectedArtist().printArtist();
        }

        if (page == Page.HOST) {
            return search.getSelectedHost().printHost();
        }

        return null;
    }

    public String addAlbum(final Commands command, final Library library) {
        return this.username + " is not an artist.";
    }
    public String addEvent(final Commands command, final Library library) {
        return this.username + " is not an artist.";
    }
    public String addMerch(final Commands command, final Library library) {
        return this.username + " is not an artist.";
    }
    public final ArrayList<Playlist> getFollowedPlaylists() {
        return followedPlaylists;
    }

    public final ArrayList<Song> getLikedSongs() {
        return likedSongs;
    }

    public final ArrayList<Playlist> getPlaylists() {
        return playlists;
    }

    public final HashMap<String, Episode> getLastEpisodes() {
        return lastEpisodes;
    }

    public final String getUsername() {
        return username;
    }

    public final void setUsername(final String username) {
        this.username = username;
    }

    public final int getAge() {
        return age;
    }

    public final void setAge(final int age) {
        this.age = age;
    }

    public final String getCity() {
        return city;
    }

    public final void setCity(final String city) {
        this.city = city;
    }

    public final Search getSearch() {
        return search;
    }

    public final void setSearch(final Search search) {
        this.search = search;
    }

    public final Player getPlayer() {
        return player;
    }

    public final void setPlayer(final Player player) {
        this.player = player;
    }

    public Status getStatus() {
        return status;
    }

    public final void setStatus(final Status status) {
        this.status = status;
    }

    public final String getConnectionStatus() {
        return connectionStatus;
    }

    public final void setConnectionStatus(final String connectionStatus) {
        this.connectionStatus = connectionStatus;
    }

    public final void setPage(final Page page) {
        this.page = page;
    }
}
