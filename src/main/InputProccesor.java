package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import media.Library;
import media.Playlist;
import player.Player;
import searchbar.Search;

import java.util.ArrayList;

public final class InputProccesor {
    private Search search;
    private Stats status;
    private Player player;
    private final Library library;
    private final User user;
    private final ObjectNode objectNode;
    private final Commands command;

    public InputProccesor(final Library library,
                          final User user,
                          final ObjectNode objectNode,
                          final Commands command) {
        this.library = library;

        this.objectNode = objectNode;

        this.command = command;
        objectNode.put("command", command.getCommand());

        this.user = user;
        if (user != null) {
            this.search = user.getSearch();
            if (user.getStatus() == null) {
                user.setStatus(new Stats());
            }
            this.status = user.getStatus();
            this.player = user.getPlayer();
            objectNode.put("user", command.getUsername());
        }

        objectNode.put("timestamp", command.getTimestamp());
    }

    /**
     * Handle search command.
     */
    public void search() {
        if (player != null && player.isLoaded()) {
            player.updateStatus(command.getTimestamp(), user);
            player.setLoaded(false);
        }

        // Resetting status history
        status.reset();

        // New search
        assert user != null;
        user.setSearch(Search.newSearch(command.getType(), library));
        search = user.getSearch();

        // Updating search result array
        search.result = search.getSearchResultArray(command.getFilters(), user);

        // Output for search
        objectNode.put("message", "Search returned " + search.getResultsCount() + " results");
        objectNode.put("results", search.result);
    }

    /**
     * Handle select command.
     */
    public void select() {
        if (search == null || search.result == null) {
            objectNode.put("message", "Please conduct a search before making a selection.");
            return;
        }
        if (command.getItemNumber() <= search.getResultsCount()) {
            String selected = search.result.get(command.getItemNumber() - 1).textValue();
            search.select(selected);
            objectNode.put("message", "Successfully selected " + selected + ".");
        } else {
            objectNode.put("message", "The selected ID is too high.");
        }
        search.result = null;
    }

    /**
     * Handle load command.
     */
    public void load() {
        if (search == null) {
            objectNode.put("message", "Please select a source before attempting to load.");
            return;
        }
        if (search.isSelected()) {
            search.setSelected(false); // No media selected after loading

            // Creating player depending on media type
            assert user != null;
            user.setPlayer(Player.createPlayer(search, status));
            player = user.getPlayer();

            // Loading media
            assert player != null;
            player.load(command.getTimestamp(), user);

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
        player.playPause(command.getTimestamp(), user);
    }

    /**
     * Handle repeat command.
     */
    public void repeat() {
        if (player == null || !player.isLoaded()) {
            objectNode.put("message",
                    "Please load a source before setting the repeat status.");
            return;
        }

        // Updating status
        player.updateStatus(command.getTimestamp(), user);

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
        if (player == null || !player.isLoaded()) {
            objectNode.put("message", "Please load a source before using the shuffle function.");
            return;
        }

        // Updating status
        player.updateStatus(command.getTimestamp(), user);

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
        if (player == null || !player.isLoaded()) {
            objectNode.put("message", "Please load a source before attempting to forward.");
            return;
        }

        // Updating status
        player.updateStatus(command.getTimestamp(), user);

        if (player.isLoaded()) {
            player.forward(user, objectNode);
        } else {
            objectNode.put("message", "Please load a source before attempting to forward.");
        }
    }

    /**
     * Handle backward command.
     */
    public void backward() {
        if (player == null || !player.isLoaded()) {
            objectNode.put("message", "Please select a source before rewinding.");
            return;
        }

        // Updating status
        player.updateStatus(command.getTimestamp(), user);

        if (player.isLoaded()) {
            player.backward(user, objectNode);
        } else {
            objectNode.put("message", "Please select a source before rewinding.");
        }
    }

    /**
     * Handle next command.
     */
    public void next() {
        if (player == null || !player.isLoaded()) {
            objectNode.put("message", "Please load a source before skipping to the next track.");
            return;
        }

        // Updating status
        player.updateStatus(command.getTimestamp(), user);

        if (player.isLoaded()) {
            player.next(user, objectNode, command.getTimestamp());
        } else {
            objectNode.put("message", "Please load a source before skipping to the next track.");
        }
    }

    /**
     * Handle prev command.
     */
    public void prev() {
        if (player == null || !player.isLoaded()) {
            objectNode.put("message",
                    "Please load a source before returning to the previous track.");
            return;
        }

        // Updating status
        player.updateStatus(command.getTimestamp(), user);

        if (player.isLoaded()) {
            player.prev(user, objectNode, command.getTimestamp());
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
            player.updateStatus(command.getTimestamp(), user);
        }
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode statsNode = objectMapper.valueToTree(status);
        objectNode.set("stats", statsNode);
    }

    /**
     * Handle like command.
     */
    public void like() {
        if (player != null && player.isLoaded()) {
            player.updateStatus(command.getTimestamp(), user);
            player.like(user, objectNode);
        } else {
            objectNode.put("message", "Please load a source before liking or unliking.");
        }
    }

    /**
     * Handle addRemoveInPlaylist command.
     */
    public void addRemoveInPlaylist() {
        if (player != null && player.isLoaded()) {
            player.addRemoveInPlaylist(user, command.getPlaylistId(), objectNode);
        } else {
            objectNode.put("message",
                    "Please load a source before adding to or removing from the playlist.");
        }
    }

    /**
     * Handle createPlaylist command.
     */
    public void createPlaylist() {
        if (Playlist.exists(command.getPlaylistName(), library.getPlaylists())) {
            objectNode.put("message", "A playlist with the same name already exists.");
        } else if (user != null) {
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
        if (search == null || !search.isSelected()) {
            objectNode.put("message", "Please select a source before following or unfollowing.");
            return;
        }
        if (search.getSelectedPlaylist() != null && user != null) {
            Playlist.follow(user, search.getSelectedPlaylist(), objectNode);
        } else {
            objectNode.put("message", "The selected source is not a playlist.");
        }
    }

    /**
     * Handle getTop5songs command.
     */
    public void getTop5Songs() {
        Statistics statistic = new Statistics(library.getSongs(), library.getPlaylists());
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
        Statistics statistic = new Statistics(library.getSongs(), library.getPlaylists());
        ArrayNode resultPlaylist = objectNode.putArray("result");
        ArrayList<String> top5Playlists = statistic.getTop5Playlists();
        for (String playlist : top5Playlists) {
            resultPlaylist.add(playlist);
        }
    }
}
