package main;

import checker.Checker;
import checker.CheckerConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * The entry point to this homework. It runs the checker that tests your implentation.
 */
public final class Main {
    static final String LIBRARY_PATH = CheckerConstants.TESTS_PATH + "library/library.json";

    /**
     * for coding style
     */
    private Main() {
    }

    /**
     * DO NOT MODIFY MAIN METHOD
     * Call the checker
     * @param args from command line
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void main(final String[] args) throws IOException {
        File directory = new File(CheckerConstants.TESTS_PATH);
        Path path = Paths.get(CheckerConstants.RESULT_PATH);

        if (Files.exists(path)) {
            File resultFile = new File(String.valueOf(path));
            for (File file : Objects.requireNonNull(resultFile.listFiles())) {
                file.delete();
            }
            resultFile.delete();
        }
        Files.createDirectories(path);

        for (File file : Objects.requireNonNull(directory.listFiles())) {
            if (file.getName().startsWith("library")) {
                continue;
            }

            String filepath = CheckerConstants.OUT_PATH + file.getName();
            File out = new File(filepath);
            boolean isCreated = out.createNewFile();
            if (isCreated) {
                action(file.getName(), filepath);
            }
        }

        Checker.calculateScore();
    }

    /**
     * @param filePath1 for input file
     * @param filePath2 for output file
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void action(final String filePath1,
                              final String filePath2) throws IOException {
        Playlist.instanceCount = 0;
        ObjectMapper objectMapper = new ObjectMapper();
        LibraryInput library = objectMapper.readValue(new File(LIBRARY_PATH), LibraryInput.class);
        Commands[] commands = objectMapper.readValue(new File(CheckerConstants.TESTS_PATH + filePath1), Commands[].class);
        ArrayNode outputs = objectMapper.createArrayNode();
        ArrayNode searchResult = null;
        ArrayList<Playlist> playlists = new ArrayList<>();
        ArrayList<User> users = new ArrayList<>();
        for(Commands command : commands) {
            User user = User.getUserByName(users, command.getUsername());
            if(User.getUserByName(users, command.getUsername()) == null) {
                user = new User(command.getUsername());
                users.add(user);
            }
            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("command", command.getCommand());
            objectNode.put("user", command.getUsername());
            objectNode.put("timestamp", command.getTimestamp());
            switch (command.getCommand()) {
                case "search":
                    Filters filter = command.getFilters();
                    searchResult = SearchBar.getSearchResultArray(command.getType(), library, filter, playlists);
                    objectNode.put("message", "Search returned " + SearchBar.index + " results");
                    objectNode.put("results", searchResult);
                    SearchBar.lastSearchType = command.getType();
                    SearchBar.isSelected = false;
                    Player.isLoaded = false;
                    break;
                case "select":
                    if(command.getItemNumber() <= SearchBar.index && !Objects.requireNonNull(searchResult).isEmpty()) {
                        SearchBar.isSelected = true;
                        SearchBar.select(searchResult.get(command.getItemNumber() - 1).textValue(),library, playlists);
                        objectNode.put("message", "Successfully selected " + searchResult.get(command.getItemNumber() - 1).textValue() + ".");
                    } else
                        objectNode.put("message","The selected ID is too high.");
                    break;
                case "load":
                    if(SearchBar.isSelected) {
                        Player.load(command.getTimestamp());
                        objectNode.put("message", "Playback loaded successfully.");
                    } else
                        objectNode.put("message","Please select a source before attempting to load.");
                    break;
                case "playPause":
                    if(Player.isLoaded) {
                        Stats status = Stats.getInstance();
                        Player.status(command.getTimestamp());
                        if(!status.isPaused()) {
                            objectNode.put("message","Playback paused successfully.");
                            status.setPaused(true);
                            Player.timepaused = command.getTimestamp();
                        } else {
                            objectNode.put("message","Playback resumed successfully.");
                            status.setPaused(false);
                            Player.timepaused = 0;
                            Player.timestarted = command.getTimestamp();
                        }
                    } else {
                        objectNode.put("message","Please load a source before attempting to pause or resume playback.");
                    }
                    break;
                case "repeat":
                    if(Player.isLoaded) {
                        Stats status = Stats.getInstance();
                        Player.repeat();
                        objectNode.put("message","Repeat mode changed to " + status.getRepeat() + ".");
                    } else {
                        objectNode.put("message","Please load a source before setting the repeat status.");
                    }
                    break;
                case "status":
                    Stats status = Stats.getInstance();
                    Player.status(command.getTimestamp());
                    ObjectNode statsNode = objectMapper.valueToTree(status);
                    objectNode.set("stats", statsNode);
                    break;
                case "like":
                    if(Player.isLoaded) {
                        Player.like(users, objectNode, command.getUsername());
                    } else {
                        objectNode.put("message", "Please load a source before liking or unliking.");
                    }
                    break;
                case "addRemoveInPlaylist":
                    if(Player.isLoaded) {
                        Player.addRemoveInPlaylist(playlists, command.getPlaylistId(), objectNode);
                    } else {
                        objectNode.put("message", "Please load a source before adding to or removing from the playlist.");
                    }
                    break;
                case "createPlaylist":
                    if(Playlist.exists(command.getPlaylistName(),playlists)) {
                        objectNode.put("message","A playlist with the same name already exists.");
                    } else {
                        Playlist playlist = new Playlist(command.getPlaylistName());
                        playlist.setCreatedBy(command.getUsername());
                        if(user != null)
                            user.getPlaylists().add(playlist);
                        playlists.add(playlist);
                        objectNode.put("message","Playlist created successfully.");
                    }
                    break;
                case "showPlaylists":
                    if(user != null)
                        Playlist.showPlaylists(objectNode, user);
                    break;
                case "showPreferredSongs":
                    if(user != null)
                        User.showPreferedSongs(objectNode, user);
                    break;
                case "switchVisibility":
                    if(Playlist.instanceCount < command.getPlaylistId())
                        objectNode.put("message","The specified playlist ID is too high.");
                    else
                        Playlist.switchVisibility(playlists, command.getPlaylistId());
                    break;
                default:
                    break;
            }
            outputs.add(objectNode);
        }

        ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
        objectWriter.writeValue(new File(filePath2), outputs);
    }
}
