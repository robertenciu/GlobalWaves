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
import java.util.Objects;
import java.util.ArrayList;

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
        int i = 0;
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
            if (++i == 10)
                break;
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

        // Reading library
        LibraryInput library = objectMapper.readValue(new File(LIBRARY_PATH), LibraryInput.class);

        // Reading commands
        Commands[] commands = objectMapper.readValue(new File(CheckerConstants.TESTS_PATH + filePath1), Commands[].class);

        // Output array
        ArrayNode outputs = objectMapper.createArrayNode();

        // Playlist Array creation
        ArrayList<Playlist> playlists = new ArrayList<>();

        // Initiating search
        Search search = Search.getInstance();
        search.reset(); // reset search history from previous test
        ArrayNode searchResult = null;

        // Initiating player
        AbstractPlayer player = null;

        // Initiating status
        Stats status = Stats.getInstance();
        status.reset(); // reset status history from previous test

        for(Commands command : commands) {
            // Getting user by username from the library
            UserInput user = UserInput.getUserByName(library.getUsers(), command.getUsername());
            if(user == null)
                return;

            // Setting filters
            Filters filter = command.getFilters();

            // Setting output json node (standard output for all commands)
            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("command", command.getCommand());
            objectNode.put("user", command.getUsername());
            objectNode.put("timestamp", command.getTimestamp());

            switch (command.getCommand()) {
                case "search":
                    if(player != null && player.isLoaded()) {
                        player.updateStatus(command.getTimestamp(), user);
                        player.setLoaded(false);
                    }

                    status.reset(); // Resetting status history
                    search.reset(); // Resetting search history
                    search.setType(command.getType());

                    // Updating search result array
                    searchResult = search.getSearchResultArray(library, filter, playlists);

                    // Output for search
                    objectNode.put("message", "Search returned " + search.getResultsCount() + " results");
                    objectNode.put("results", searchResult);
                    break;
                case "select":
                    if (searchResult == null) {
                        System.out.println("No results found");
                        break;
                    }
                    if (command.getItemNumber() <= search.getResultsCount()) {
                        search.select(searchResult.get(command.getItemNumber() - 1).textValue(), library, playlists);
                        objectNode.put("message", "Successfully selected "
                                + searchResult.get(command.getItemNumber() - 1).textValue() + ".");
                    } else
                        objectNode.put("message","The selected ID is too high.");
                    break;
                case "load":
                    if(search.isSelected()) {
                        search.setSelected(false); // No media selected after loading

                        // Creating player depending on media type
                        player = AbstractPlayer.createPlayer(search.getType());
                        if (player == null) {
                            System.out.println("Incorrect type");
                            break;
                        }

                        // Loading media
                        player.load(command.getTimestamp(), user);

                        objectNode.put("message", "Playback loaded successfully.");
                    } else {
                        objectNode.put("message", "Please select a source before attempting to load.");
                    }
                    break;
                case "playPause":
                    if (player == null || !player.isLoaded()) {
                        objectNode.put("message","Please load a source before attempting to pause or resume playback.");
                        break;
                    }

                    if(!status.isPaused()) {
                        objectNode.put("message","Playback paused successfully.");
                    } else {
                        objectNode.put("message","Playback resumed successfully.");
                    }

                    // Pausing or resuming media
                    player.playPause(command.getTimestamp(), user);
                    break;
                case "repeat":
                    if (player == null || !player.isLoaded()) {
                        objectNode.put("message","Please load a source before setting the repeat status.");
                        break;
                    }

                    // Updating status
                    player.updateStatus(command.getTimestamp(), user);

                    if (player.isLoaded()) {
                        player.repeat();
                        objectNode.put("message","Repeat mode changed to " + status.getRepeat().toLowerCase() + ".");
                    } else {
                        objectNode.put("message","Please load a source before setting the repeat status.");
                    }
                    break;
                case "shuffle":
                    if(player == null || !player.isLoaded()) {
                        objectNode.put("message", "Please load a source before using the shuffle function.");
                        break;
                    }

                    // Updating status
                    player.updateStatus(command.getTimestamp(), user);

                    if (player.isLoaded()) {
                        player.shuffle(objectNode, command.getSeed());
                    } else {
                        objectNode.put("message","Please load a source before using the shuffle function.");
                    }
                    break;
                case "forward":
                    if(player == null || !player.isLoaded()) {
                        objectNode.put("message", "Please load a source before skipping forward.");
                        break;
                    }

                    // Updating status
                    player.updateStatus(command.getTimestamp(), user);

                    if (player.isLoaded()) {
                        player.forward(user, objectNode);
                    } else {
                        objectNode.put("message","Please load a source before using the shuffle function.");
                    }
                    break;
                case "status":
                    if (player != null && player.isLoaded())
                        player.updateStatus(command.getTimestamp(), user);
                    ObjectNode statsNode = objectMapper.valueToTree(status);
                    objectNode.set("stats", statsNode);
                    break;
                case "like":
                    if (player != null && player.isLoaded()) {
                        player.like(user, objectNode);
                    } else {
                        objectNode.put("message", "Please load a source before liking or unliking.");
                    }
                    break;
                case "addRemoveInPlaylist":
                    if(player != null && player.isLoaded()) {
                        player.addRemoveInPlaylist(playlists, command.getPlaylistId(), objectNode);
                    } else {
                        objectNode.put("message", "Please load a source before adding to or removing from the playlist.");
                    }
                    break;
                case "createPlaylist":
                    if(Playlist.exists(command.getPlaylistName(), playlists)) {
                        objectNode.put("message","A playlist with the same name already exists.");
                    } else {
                        // Creating new playlist
                        Playlist playlist = new Playlist(command.getPlaylistName());
                        playlist.setCreatedBy(command.getUsername());

                        // Adding playlist to user and playlist array
                        user.getPlaylists().add(playlist);
                        playlists.add(playlist);

                        objectNode.put("message","Playlist created successfully.");
                    }
                    break;
                case "showPlaylists":
                    Playlist.showPlaylists(objectNode, user);
                    break;
                case "showPreferredSongs":
                    user.showPreferredSongs(objectNode);
                    break;
                case "switchVisibility":
                    if (Playlist.instanceCount < command.getPlaylistId()) {
                        objectNode.put("message", "The specified playlist ID is too high.");
                    } else {
                        Playlist.switchVisibility(playlists, command.getPlaylistId());
                    }
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
