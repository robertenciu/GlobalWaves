package main;

import admin.Admin;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import command.Commands;
import media.Library;
import media.Playlist;
import player.Player;
import searchbar.Search;
import stats.Statistics;
import player.Status;
import user.User;

import java.util.ArrayList;

public final class InputProccesor {
    private Search search;
    private Status status;
    private Player player;
    private final Library library;
    private final User user;
    private final ObjectNode objectNode;
    private final Commands command;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ArrayNode empty = objectMapper.createArrayNode();
    public InputProccesor(final Library library,
                          final ObjectNode objectNode,
                          final Commands command) {
        this.library = library;

        this.objectNode = objectNode;

        this.command = command;
        objectNode.put("command", command.getCommand());

        if (command.getUsername() != null) {
            objectNode.put("user", command.getUsername());
        }

        // Getting current user
        this.user = User.getUserByName(library.getUsers(), command.getUsername());
        if (user != null) {
            this.search = user.getSearch();
            if (user.getStatus() == null) {
                user.setStatus(new Status());
            }
            this.status = user.getStatus();
            this.player = user.getPlayer();
        }

        objectNode.put("timestamp", command.getTimestamp());
    }

    /**
     * Handle search command.
     */
    public void search() {
        if (user.getConnectionStatus().equals("Offline")) {
            objectNode.put("message", user.getUsername() + " is offline.");
            objectNode.put("results", empty);
            return;
        }

        if (player != null && player.isLoaded()) {
            player.updateStatus(command.getTimestamp());
            player.setLoaded(false);
        }

        // Resetting status history
        status.reset();

        // New search
        user.setSearch(Search.newSearch(command.getType(), library));
        search = user.getSearch();

        // Updating search result array
        search.setResult(search.getSearchResultArray(command.getFilters(), user));

        // Output for search
        objectNode.put("message", "Search returned " + search.getResultsCount() + " results");
        objectNode.put("results", search.getResult());
    }

    /**
     * Handle select command.
     */
    public void select() {
        if (user.getConnectionStatus().equals("Offline")) {
            objectNode.put("message", user.getUsername() + " is offline.");
            return;
        }

        if (search == null || search.getResult() == null) {
            objectNode.put("message", "Please conduct a search before making a selection.");
            return;
        }
        if (command.getItemNumber() <= search.getResultsCount()) {
            String selected = search.getResult().get(command.getItemNumber() - 1).textValue();
            String message = search.select(selected, user);
            objectNode.put("message", message);
        } else {
            objectNode.put("message", "The selected ID is too high.");
        }
        search.setResult(null);
    }

    /**
     * Handle load command.
     */
    public void load() {
        if (user.getConnectionStatus().equals("Offline")) {
            objectNode.put("message", user.getUsername() + " is offline.");
            return;
        }

        if (search == null) {
            objectNode.put("message", "Please select a source before attempting to load.");
            return;
        }
        if (search.isSelected()) {
            search.setSelected(false); // No media selected after loading

            // Creating player depending on media type
            user.setPlayer(Player.createPlayer(search, status, user));
            player = user.getPlayer();

            // Loading media
            assert player != null;
            player.load(command.getTimestamp());

            objectNode.put("message",
                    "Playback loaded successfully.");
        } else {
            objectNode.put("message",
                    "Please select a source before attempting to load.");
        }
    }

    /**
     * Handle playPause command.
     */
    public void playPause() {
        if (user.getConnectionStatus().equals("Offline")) {
            objectNode.put("message", user.getUsername() + " is offline.");
            return;
        }

        if (player == null || !player.isLoaded()) {
            objectNode.put("message",
                    "Please load a source before attempting to pause or resume playback.");
            return;
        }

        if (!status.isPaused()) {
            objectNode.put("message", "Playback paused successfully.");
        } else {
            objectNode.put("message", "Playback resumed successfully.");
        }

        // Pausing or resuming media
        player.playPause(command.getTimestamp());
    }

    /**
     * Handle repeat command.
     */
    public void repeat() {
        if (user.getConnectionStatus().equals("Offline")) {
            objectNode.put("message", user.getUsername() + " is offline.");
            return;
        }

        if (player == null || !player.isLoaded()) {
            objectNode.put("message",
                    "Please load a source before setting the repeat status.");
            return;
        }

        // Updating status
        player.updateStatus(command.getTimestamp());

        if (player.isLoaded()) {
            player.repeat();
            objectNode.put("message",
                    "Repeat mode changed to " + status.getRepeat().toLowerCase() + ".");
        } else {
            objectNode.put("message",
                    "Please load a source before setting the repeat status.");
        }
    }

    /**
     * Handle shuffle command.
     */
    public void shuffle() {
        if (user.getConnectionStatus().equals("Offline")) {
            objectNode.put("message", user.getUsername() + " is offline.");
            return;
        }

        if (player == null || !player.isLoaded()) {
            objectNode.put("message", "Please load a source before using the shuffle function.");
            return;
        }

        // Updating status
        player.updateStatus(command.getTimestamp());

        if (player.isLoaded()) {
            player.shuffle(objectNode, command.getSeed());
        } else {
            objectNode.put("message", "Please load a source before using the shuffle function.");
        }
    }

    /**
     * Handle forward command.
     */
    public void forward() {
        if (user.getConnectionStatus().equals("Offline")) {
            objectNode.put("message", user.getUsername() + " is offline.");
            return;
        }

        if (player == null || !player.isLoaded()) {
            objectNode.put("message", "Please load a source before attempting to forward.");
            return;
        }

        // Updating status
        player.updateStatus(command.getTimestamp());

        if (player.isLoaded()) {
            player.forward(objectNode);
        } else {
            objectNode.put("message", "Please load a source before attempting to forward.");
        }
    }

    /**
     * Handle backward command.
     */
    public void backward() {
        if (user.getConnectionStatus().equals("Offline")) {
            objectNode.put("message", user.getUsername() + " is offline.");
            return;
        }

        if (player == null || !player.isLoaded()) {
            objectNode.put("message", "Please select a source before rewinding.");
            return;
        }

        // Updating status
        player.updateStatus(command.getTimestamp());

        if (player.isLoaded()) {
            player.backward(objectNode);
        } else {
            objectNode.put("message", "Please select a source before rewinding.");
        }
    }

    /**
     * Handle next command.
     */
    public void next() {
        if (user.getConnectionStatus().equals("Offline")) {
            objectNode.put("message", user.getUsername() + " is offline.");
            return;
        }

        if (player == null || !player.isLoaded()) {
            objectNode.put("message", "Please load a source before skipping to the next track.");
            return;
        }

        // Updating status
        player.updateStatus(command.getTimestamp());

        if (player.isLoaded()) {
            player.next(objectNode, command.getTimestamp());
        } else {
            objectNode.put("message", "Please load a source before skipping to the next track.");
        }
    }

    /**
     * Handle prev command.
     */
    public void prev() {
        if (user.getConnectionStatus().equals("Offline")) {
            objectNode.put("message", user.getUsername() + " is offline.");
            return;
        }

        if (player == null || !player.isLoaded()) {
            objectNode.put("message",
                    "Please load a source before returning to the previous track.");
            return;
        }

        // Updating status
        player.updateStatus(command.getTimestamp());

        if (player.isLoaded()) {
            player.prev(objectNode, command.getTimestamp());
        } else {
            objectNode.put("message",
                    "Please load a source before returning to the previous track.");
        }
    }

    /**
     * Handle status command.
     */
    public void status() {
        if (player != null && player.isLoaded()) {
            player.updateStatus(command.getTimestamp());
        }
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode statsNode = objectMapper.valueToTree(status);
        objectNode.set("stats", statsNode);
    }

    /**
     * Handle like command.
     */
    public void like() {
        if (user.getConnectionStatus().equals("Offline")) {
            objectNode.put("message", user.getUsername() + " is offline.");
            return;
        }

        if (player != null && player.isLoaded()) {
            player.updateStatus(command.getTimestamp());
            player.like(objectNode);
        } else {
            objectNode.put("message", "Please load a source before liking or unliking.");
        }
    }

    /**
     * Handle addRemoveInPlaylist command.
     */
    public void addRemoveInPlaylist() {
        if (user.getConnectionStatus().equals("Offline")) {
            objectNode.put("message", user.getUsername() + " is offline.");
            return;
        }

        if (player != null && player.isLoaded()) {
            player.addRemoveInPlaylist(command.getPlaylistId(), objectNode);
        } else {
            objectNode.put("message",
                    "Please load a source before adding to or removing from the playlist.");
        }
    }

    /**
     * Handle createPlaylist command.
     */
    public void createPlaylist() {
        if (user.getConnectionStatus().equals("Offline")) {
            objectNode.put("message", user.getUsername() + " is offline.");
            return;
        }

        if (Playlist.exists(command.getPlaylistName(), library.getPlaylists())) {
            objectNode.put("message", "A playlist with the same name already exists.");
        } else {

            // Creating new playlist
            int id = user.getPlaylists().size() + 1;
            Playlist playlist = new Playlist(command.getPlaylistName(), id, user.getUsername());

            // Adding playlist to user and playlist array
            user.getPlaylists().add(playlist);
            library.getPlaylists().add(playlist);

            objectNode.put("message", "Playlist created successfully.");
        }
    }

    /**
     * Handle showPlaylists command.
     */
    public void showPlaylists() {
        user.showPlaylists(objectNode);
        search = null;
    }

    /**
     * Handle showPreferredSongs command.
     */
    public void showPreferredSongs() {
        user.showPreferredSongs(objectNode);
        search = null;
    }

    /**
     * Handle switchVisibility command.
     */
    public void switchVisibility() {
        if (user.getConnectionStatus().equals("Offline")) {
            objectNode.put("message", user.getUsername() + " is offline.");
            return;
        }

        if (user.getPlaylists().size() < command.getPlaylistId()) {
            objectNode.put("message", "The specified playlist ID is too high.");
        } else {
            user.switchVisibility(command.getPlaylistId(), objectNode);
        }
        search = null;
    }

    /**
     * Handle follow command.
     */
    public void follow() {
        if (user.getConnectionStatus().equals("Offline")) {
            objectNode.put("message", user.getUsername() + " is offline.");
            return;
        }

        if (search == null || !search.isSelected()) {
            objectNode.put("message", "Please select a source before following or unfollowing.");
            return;
        }
        if (search.getSelectedPlaylist() != null) {
            Playlist.follow(user, search.getSelectedPlaylist(), objectNode);
        } else {
            objectNode.put("message", "The selected source is not a playlist.");
        }
    }

    /**
     * Handle getTop5songs command.
     */
    public void getTop5Songs() {
        Statistics statistic = Statistics.getInstance();
        ArrayNode result = objectNode.putArray("result");
        ArrayList<String> top5 = statistic.getTop5Songs();
        for (String song : top5) {
            result.add(song);
        }
    }

    /**
     * Handle getTop5Playlists command.
     */
    public void getTop5Playlists() {
        Statistics statistic = Statistics.getInstance();
        ArrayNode result = objectNode.putArray("result");
        ArrayList<String> top5Playlists = statistic.getTop5Playlists();
        for (String playlist : top5Playlists) {
            result.add(playlist);
        }
    }

    public void getOnlineUsers() {
        Statistics statistic = Statistics.getInstance();
        ArrayNode result = objectNode.putArray("result");
        ArrayList<String> onlineUsers = statistic.getOnlineUsers();
        for (String onlineUser : onlineUsers) {
            result.add(onlineUser);
        }
    }

    public void getAllUsers() {
        Statistics statistic = Statistics.getInstance();
        ArrayNode result = objectNode.putArray("result");
        ArrayList<String> allUsers = statistic.getAllUsers();
        for (String abstractUser : allUsers) {
            result.add(abstractUser);
        }
    }

    public void switchConnectionStatus() {
        if (user == null) {
            objectNode.put("message", "The username " + command.getUsername() + " doesn't exist.");
            return;
        }

        String message = user.switchConnectionStatus(command.getTimestamp());
        objectNode.put("message", message);
    }

    public void addUser() {
        if (user != null) {
            objectNode.put("message", "The username " + command.getUsername()
                    + " is already taken.");
            return;
        }

        String message = Admin.addUser(command, library);
        objectNode.put("message", message);
    }

    public void showAlbums() {
        ArrayNode result = Admin.showAlbums(command, library);
        objectNode.put("result", result);
    }

    public void addAlbum() {
        if (user == null) {
            objectNode.put("message", "The username " + command.getUsername()
                    + " doesn't exist.");
            return;
        }

        String message = user.addAlbum(command, library);
        objectNode.put("message", message);
    }

    public void addEvent() {
        if (user == null) {
            objectNode.put("message", "The username " + command.getUsername()
                    + " doesn't exist.");
            return;
        }

        String message = user.addEvent(command, library);
        objectNode.put("message", message);
    }

    public void addMerch() {
        if (user == null) {
            objectNode.put("message", "The username " + command.getUsername()
                    + " doesn't exist.");
            return;
        }

        String message = user.addMerch(command, library);
        objectNode.put("message", message);
    }
    public void printCurrentPage() {
        if (user.getConnectionStatus().equals("Offline")) {
            objectNode.put("message", user.getUsername() + " is offline.");
            return;
        }

        String message = user.printCurrentPage();
        objectNode.put("message", message);
    }
}
