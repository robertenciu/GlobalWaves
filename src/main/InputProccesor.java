package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import media.Playlist;
import media.Podcast;
import media.Song;
import player.AbstractPlayer;
import searchbar.Search;

import java.util.ArrayList;

public final class InputProccesor {
    private Search search;
    private Stats status;
    private AbstractPlayer player;
    private ArrayList<Playlist> playlists;
    private  ArrayList<Song> songs;
    private  ArrayList<Podcast> podcasts;
    private final User user;
    private final ObjectNode objectNode;
    private final Commands command;

    public void setPlaylists(ArrayList<Playlist> playlists) {
        this.playlists = playlists;
    }

    public void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
    }

    public void setPodcasts(ArrayList<Podcast> podcasts) {
        this.podcasts = podcasts;
    }

    public InputProccesor(final User user,
                          final ObjectNode objectNode,
                          final Commands command) {

        this.objectNode = objectNode;

        this.command = command;
        objectNode.put("command", command.getCommand());

        this.user = user;
        if (user != null) {
            this.search = user.search;
            if (user.status == null) {
                user.status = new Stats();
            }
            this.status = user.status;
            this.player = user.player;
            objectNode.put("user", command.getUsername());
        }

        objectNode.put("timestamp", command.getTimestamp());
    }
    public void search() {
        if(player != null && player.isLoaded()) {
            player.updateStatus(command.getTimestamp(), user);
            player.setLoaded(false);
        }

        // Resetting status history
        status.reset();

        // New search
        assert user != null;
        user.search = Search.newSearch(command.getType(), songs, podcasts, playlists);
        search = user.search;

        // Updating search result array
        search.result = search.getSearchResultArray(command.getFilters(), user);

        // Output for search
        objectNode.put("message", "Search returned " + search.getResultsCount() + " results");
        objectNode.put("results", search.result);
    }

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

    public void load() {
        if (search == null) {
            objectNode.put("message", "Please select a source before attempting to load.");
            return;
        }
        if (search.isSelected()) {
            search.setSelected(false); // No media selected after loading

            // Creating player depending on media type
            assert user != null;
            user.player = AbstractPlayer.createPlayer(search, status);
            player = user.player;

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

    public void playPause() {
        if (player == null || !player.isLoaded()) {
            objectNode.put("message",
                    "Please load a source before attempting to pause or resume playback.");
            return;
        }

        if(!status.isPaused()) {
            objectNode.put("message","Playback paused successfully.");
        } else {
            objectNode.put("message","Playback resumed successfully.");
        }

        // Pausing or resuming media
        player.playPause(command.getTimestamp(), user);
    }

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

    public void shuffle() {
        if(player == null || !player.isLoaded()) {
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

    public void forward() {
        if(player == null || !player.isLoaded()) {
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

    public void backward() {
        if(player == null || !player.isLoaded()) {
            objectNode.put("message", "Please select a source before rewinding.");
            return;
        }

        // Updating status
        player.updateStatus(command.getTimestamp(), user);

        if (player.isLoaded()) {
            player.backward(user, objectNode);
        } else {
            objectNode.put("message","Please select a source before rewinding.");
        }
    }

    public void next() {
        if(player == null || !player.isLoaded()) {
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

    public void prev() {
        if(player == null || !player.isLoaded()) {
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

    public void status() {
        if (player != null && player.isLoaded()) {
            player.updateStatus(command.getTimestamp(), user);
        }
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode statsNode = objectMapper.valueToTree(status);
        objectNode.set("stats", statsNode);
    }

    public void like() {
        if (player != null && player.isLoaded()) {
            player.updateStatus(command.getTimestamp(), user);
            player.like(user, objectNode);
        } else {
            objectNode.put("message", "Please load a source before liking or unliking.");
        }
    }

    public void addRemoveInPlaylist() {
        if(player != null && player.isLoaded()) {
            player.addRemoveInPlaylist(user, command.getPlaylistId(), objectNode);
        } else {
            objectNode.put("message",
                    "Please load a source before adding to or removing from the playlist.");
        }
    }

    public void createPlaylist() {
        if(Playlist.exists(command.getPlaylistName(), playlists)) {
            objectNode.put("message", "A playlist with the same name already exists.");
        } else if (user != null){
            // Creating new playlist
            Playlist playlist = new Playlist(command.getPlaylistName(),
                    user.getPlaylists().size() + 1);
            playlist.setCreatedBy(command.getUsername());

            // Adding playlist to user and playlist array
            user.getPlaylists().add(playlist);
            playlists.add(playlist);

            objectNode.put("message","Playlist created successfully.");
        }
    }

    public void showPlaylists() {
        Playlist.showPlaylists(objectNode, user); //TO DO:make showplaylists on user
        search = null;
    }

    public void showPreferredSongs() {
        user.showPreferredSongs(objectNode);
        search = null;
    }

    public void switchVisibility() {
        if (user.getPlaylists().size()  < command.getPlaylistId()) {
            objectNode.put("message", "The specified playlist ID is too high.");
        } else {
            Playlist.switchVisibility(command.getPlaylistId(), user, objectNode);  // Make switch on user
        }
        search = null;
    }

    public void follow() {
        if(search == null || !search.isSelected()) {
            objectNode.put("message", "Please select a source before following or unfollowing.");
            return;
        }
        if (search.getSelectedPlaylist() != null && user != null) {
            Playlist.follow(user, search.getSelectedPlaylist(), objectNode);
        } else {
            objectNode.put("message", "The selected source is not a playlist.");
        }
    }

    public void getTop5Songs() {
        Statistics statistic = new Statistics(songs, playlists);
        ArrayNode result = objectNode.putArray("result");
        ArrayList<String> top5 = statistic.getTop5Songs();
        for (String song : top5) {
            result.add(song);
        }
    }

    public void getTop5Playlists() {
        Statistics statistic = new Statistics(songs, playlists);
        ArrayNode resultPlaylist = objectNode.putArray("result");
        ArrayList<String> top5Playlists = statistic.getTop5Playlists();
        for (String playlist : top5Playlists) {
            resultPlaylist.add(playlist);
        }
    }
}
