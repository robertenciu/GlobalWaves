package main;

import checker.Checker;
import checker.CheckerConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.LibraryInput;
import media.Song;
import media.Playlist;
import media.Podcast;

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
        ObjectMapper objectMapper = new ObjectMapper();

        // Reading library
        LibraryInput library = objectMapper.readValue(new File(LIBRARY_PATH), LibraryInput.class);

        // Reading commands
        Commands[] commands = objectMapper.readValue(new File(CheckerConstants.TESTS_PATH
                                                        + filePath1), Commands[].class);

        // Output array
        ArrayNode outputs = objectMapper.createArrayNode();

        // Media and User copy array creation
        ArrayList<Playlist> playlists = new ArrayList<>();
        ArrayList<Song> songs = Song.copySongs(library);
        ArrayList<Podcast> podcasts = Podcast.copyPodcasts(library);
        ArrayList<User> users = User.copyUsers(library);


        for(Commands command : commands) {
            User user = User.getUserByName(users, command.getUsername());

            // Setting output json node (standard output for all commands)
            ObjectNode objectNode = objectMapper.createObjectNode();
            InputProccesor inputProccesor = new InputProccesor(user, objectNode, command);

            inputProccesor.setPlaylists(playlists);
            inputProccesor.setPodcasts(podcasts);
            inputProccesor.setSongs(songs);

            switch (command.getCommand()) {
                case "search":
                    inputProccesor.search();
                    break;
                case "select":
                    inputProccesor.select();
                    break;
                case "load":
                    inputProccesor.load();
                    break;
                case "playPause":
                    inputProccesor.playPause();
                    break;
                case "repeat":
                    inputProccesor.repeat();
                    break;
                case "shuffle":
                    inputProccesor.shuffle();
                    break;
                case "forward":
                    inputProccesor.forward();
                    break;
                case "backward":
                    inputProccesor.backward();
                    break;
                case "next":
                    inputProccesor.next();
                    break;
                case "prev":
                    inputProccesor.prev();
                    break;
                case "status":
                    inputProccesor.status();
                    break;
                case "like":
                    inputProccesor.like();
                    break;
                case "addRemoveInPlaylist":
                    inputProccesor.addRemoveInPlaylist();
                    break;
                case "createPlaylist":
                    inputProccesor.createPlaylist();
                    break;
                case "showPlaylists":
                    inputProccesor.showPlaylists();
                    break;
                case "showPreferredSongs":
                    inputProccesor.showPreferredSongs();
                    break;
                case "switchVisibility":
                    inputProccesor.switchVisibility();
                    break;
                case "follow":
                    inputProccesor.follow();
                    break;
                case "getTop5Songs":
                    inputProccesor.getTop5Songs();
                    break;
                case "getTop5Playlists":
                    inputProccesor.getTop5Playlists();
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
