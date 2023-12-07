package main;

import checker.Checker;
import checker.CheckerConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import command.Commands;
import fileio.input.LibraryInput;
import media.Library;
import stats.Statistics;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

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

        int i = 0;
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
            if (++i == 4)
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
        ObjectMapper objectMapper = new ObjectMapper();

        // Reading library
        LibraryInput libraryInput = objectMapper.readValue(new File(LIBRARY_PATH),
                                                                    LibraryInput.class);

        // Reading commands
        Commands[] commands = objectMapper.readValue(new File(CheckerConstants.TESTS_PATH
                                                        + filePath1), Commands[].class);

        // Setting output array
        ArrayNode outputs = objectMapper.createArrayNode();

        // Media and User copy array creation into library (ready to be processed)
        Library library = new Library(libraryInput);

        // Initializing statistics
        Statistics statistics = Statistics.getInstance();
        statistics.setLibrary(library);


        for (Commands command : commands) {
            // Setting output json objectNode for each command
            ObjectNode objectNode = objectMapper.createObjectNode();

            // Setting input processor
            InputProccesor inputProccesor = new InputProccesor(library, objectNode, command);

            switch (command.getCommand()) {
                case "search":
                    inputProccesor.search();
                    break;
                case "select":
                    inputProccesor.select();
                    break;
                case "switchConnectionStatus":
                    inputProccesor.switchConnectionStatus();
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
                case "getOnlineUsers":
                    inputProccesor.getOnlineUsers();
                    break;
                case "getAllUsers":
                    inputProccesor.getAllUsers();
                    break;
                case "addUser":
                    inputProccesor.addUser();
                    break;
                case "addAlbum":
                    inputProccesor.addAlbum();
                    break;
                case "addEvent":
                    inputProccesor.addEvent();
                    break;
                case "addMerch":
                    inputProccesor.addMerch();
                    break;
                case "showAlbums":
                    inputProccesor.showAlbums();
                    break;
                case "printCurrentPage":
                    inputProccesor.printCurrentPage();
                    break;
                default:
                    break;
            }

            // Adding current command output to the outputs array
            outputs.add(objectNode);
        }

        ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
        objectWriter.writeValue(new File(filePath2), outputs);
    }
}
