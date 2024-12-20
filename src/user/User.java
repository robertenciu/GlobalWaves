package user;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.CommandInput;
import media.Library;
import media.music.MusicCollection;
import page.Page;
import page.PageLocator;
import player.Status;
import media.music.Song;
import media.music.Playlist;
import media.podcast.Episode;
import fileio.input.UserInput;
import fileio.input.LibraryInput;
import player.Player;
import searchbar.Search;

import java.util.ArrayList;
import java.util.HashMap;

public class User implements Page {
    protected String username;
    protected int age;
    protected String city;
    private final ArrayList<Song> likedSongs;
    private final ArrayList<Playlist> playlists;
    private final HashMap<String, Episode> lastEpisodes;
    private final ArrayList<Playlist> followedPlaylists;
    private PageLocator currentPageLocator;
    private Page currentPage;
    private Search search;
    private Player player;
    private Status status;
    private String connectionStatus;


    public User() {
        this.likedSongs = new ArrayList<>();
        this.playlists = new ArrayList<>();
        this.lastEpisodes = new HashMap<>();
        this.followedPlaylists = new ArrayList<>();
        this.currentPageLocator = PageLocator.HOME;
        this.currentPage = this;
        this.connectionStatus = "Online";
    }

    public User(final UserInput user) {
        this.likedSongs = new ArrayList<>();
        this.playlists = new ArrayList<>();
        this.lastEpisodes = new HashMap<>();
        this.followedPlaylists = new ArrayList<>();
        this.username = user.getUsername();
        this.age = user.getAge();
        this.city = user.getCity();
        this.currentPageLocator = PageLocator.HOME;
        this.connectionStatus = "Online";
        this.currentPage = this;
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
     * @param name The username.
     * @return The user object matching the given username.
     */
    public static User getUser(final Library library, final String name) {
        User currentUser = null;
        for (User user : library.getUsers()) {
            if (user.getUsername().equals(name)) {
                currentUser = user;
                break;
            }
        }

        if (currentUser == null) {
            currentUser = Artist.getArtist(library.getArtists(), name);
        }

        if (currentUser == null) {
           currentUser = Host.getHost(library.getHosts(), name);
        }

        return currentUser;
    }

    /**
     * Removes the user from the system and all of its interactions
     *
     * @param library The library that contains the users.
     * @return true if operation is completed, false if the user is interacting
     *          with another user.
     */
    public boolean removeCurrentUser(final Library library) {
        for (User user : library.getUsers()) {
            if (user.getPlayer() != null) {
                MusicCollection musicCollection = user.getPlayer().getLoadedPlaylist();
                if (musicCollection == null) {
                    continue;
                }
                if (musicCollection.getOwner().equals(this.username)) {
                    return false;
                }
            }
        }

        for (User user : library.getUsers()) {
            user.getFollowedPlaylists().removeIf(playlist ->
                                                    playlist.getOwner().equals(this.username));
        }

        for (Playlist playlist : library.getPlaylists()) {
            for (int i = 0; i < playlist.getFollowedBy().size(); i++) {
                if (playlist.getFollowedBy().get(i).equals(this)) {
                    playlist.setFollowers(playlist.getFollowers() - 1);
                    playlist.getFollowedBy().remove(this);
                }
            }
        }

        for (Playlist playlist : playlists) {
            library.getSongs().removeAll(playlist.getSongs());
        }

        library.getUsers().remove(this);
        library.getPlaylists().removeAll(this.playlists);
        this.search = null;
        this.player = null;
        this.status = null;
        this.likedSongs.clear();
        this.playlists.clear();
        this.lastEpisodes.clear();
        this.followedPlaylists.clear();

        return true;
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

    /**
     * Switches the connection status of a user.
     *
     * @param timestamp The timestamp needed to update the player.
     * @return The specific message for connection switch.
     */
    public String switchConnectionStatus(final Integer timestamp) {
        if (this.connectionStatus.equals("Online")) {
            if (player != null && player.isLoaded()) {
                player.updateStatus(timestamp);
            }
            this.connectionStatus = "Offline";
        } else {
            if (player != null && player.isLoaded()) {
                player.setTimeUpdated(timestamp);
            }
            this.connectionStatus = "Online";
        }

        return this.username + " has changed status successfully.";
    }

    /**
     * This method changes the current page of the user.
     *
     * @param command The command that holds the next page.
     * @return The message for validating the operation.
     */
    public final String changePage(final CommandInput command) {
        if (command.getNextPage().equals("Home")) {
            currentPageLocator = PageLocator.HOME;
        } else if (command.getNextPage().equals("LikedContent")) {
            currentPageLocator = PageLocator.LIKED_CONTENT;
        } else {
            return this.username + " is trying to access a non-existent page.";
        }

        currentPage = this;
        return this.username + " accessed " + command.getNextPage() + " successfully.";
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

        final int maxResults = 5;
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
        StringBuilder result = new StringBuilder("Liked songs:\n\t[");

        for (Song song : likedSongs) {
            result.append(song.getName()).append(" - ");
            result.append(song.getArtist()).append(", ");
        }
        if (!likedSongs.isEmpty()) {
            result.deleteCharAt(result.length() - 1);
            result.deleteCharAt(result.length() - 1);
        }

        result.append("]\n\nFollowed playlists:\n\t[");

        for (Playlist playlist : followedPlaylists) {
            result.append(playlist.getName()).append(" - ");
            result.append(playlist.getOwner()).append(", ");
        }
        if (!followedPlaylists.isEmpty()) {
            result.deleteCharAt(result.length() - 1);
            result.deleteCharAt(result.length() - 1);
        }

        result.append("]");

        return result.toString();
    }

    /**
     * Prints current page of the user.
     *
     * @return The page specific output message.
     */
    public String printPage() {
        if (currentPageLocator == PageLocator.HOME) {
            return printHome();
        }
        if (currentPageLocator == PageLocator.LIKED_CONTENT) {
            return printLikedContent();
        }

        return currentPage.printPage();
    }

    /**
     * Method that adds an album to the artist and the library.
     *
     * @param command The command input holding the album characteristics.
     * @param library The library.
     * @return The specific message for validation.
     */
    public String addAlbum(final CommandInput command, final Library library) {
        return this.username + " is not an artist.";
    }

    /**
     * Method that removes an album from an artist.
     *
     * @param command The command input holding the album name.
     * @param library The library.
     * @return The specific message for validation.
     */
    public String removeAlbum(final CommandInput command, final Library library) {
        return this.username + " is not an artist.";
    }

    /**
     * Method that adds an event to the artist and the library.
     *
     * @param command The command input holding the event characteristics.
     * @param library The library.
     * @return The specific message for validation.
     */
    public String addEvent(final CommandInput command, final Library library) {
        return this.username + " is not an artist.";
    }

    /**
     * Method that removes an event from an artist.
     *
     * @param command The command input holding the event name.
     * @param library The library.
     * @return The specific message for validation.
     */
    public String removeEvent(final CommandInput command, final Library library) {
        return this.username + " is not an artist.";
    }

    /**
     * Method that adds a merch to the artist and the library.
     *
     * @param command The command input holding the merch characteristics.
     * @param library The library.
     * @return The specific message for validation.
     */
    public String addMerch(final CommandInput command, final Library library) {
        return this.username + " is not an artist.";
    }

    /**
     * Method that adds a podcast to the host and the library.
     *
     * @param command The command input holding the podcast characteristics.
     * @param library The library.
     * @return The specific message for validation.
     */
    public String addPodcast(final CommandInput command, final Library library) {
        return this.username + " is not a host.";
    }

    /**
     * Method that removes a podcast from a host.
     *
     * @param command The command input holding the podcast name.
     * @param library The library.
     * @return The specific message for validation.
     */
    public String removePodcast(final CommandInput command, final Library library) {
        return this.username + " is not a host.";
    }

    /**
     * Method that adds an announcement to the host and the library.
     *
     * @param command The command input holding the announcement characteristics.
     * @param library The library.
     * @return The specific message for validation.
     */
    public String addAnnouncement(final CommandInput command, final Library library) {
        return this.username + " is not a host.";
    }

    /**
     * Method that removes an announcement from an artist.
     *
     * @param command The command input holding the announcement name.
     * @param library The library.
     * @return The specific message for validation.
     */
    public String removeAnnouncement(final CommandInput command, final Library library) {
        return this.username + " is not a host.";
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

    public final Status getStatus() {
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

    public final void setCurrentPageLocator(final PageLocator currentPageLocator) {
        this.currentPageLocator = currentPageLocator;
    }
    public final void setCurrentPage(final Page currentPage) {
        this.currentPage = currentPage;
    }

    public final Page getCurrentPage() {
        return currentPage;
    }
}
