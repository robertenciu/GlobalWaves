package admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.CommandInput;
import media.music.Album;
import media.Library;
import media.music.Song;
import media.podcast.Episode;
import media.podcast.Podcast;
import user.Artist;
import user.Host;
import user.User;

public final class Admin {
    private Admin() { }

    /**
     * Adds a user(standard, artist, host) in the system.
     *
     * @param command The input of the command containing user's attributes.
     * @param library The global library used for saving the users added.
     * @return The output specific message.
     */
    public static String addUser(final CommandInput command, final Library library) {
        User newUser;

        switch (command.getType()) {
            case "user":
                newUser = new User();
                break;
            case "artist" :
                newUser = new Artist();
                break;
            case "host":
                newUser = new  Host();
                break;
            default:
                newUser = null;
        }

        assert newUser != null;
        newUser.setUsername(command.getUsername());
        newUser.setAge(command.getAge());
        newUser.setCity(command.getCity());

        switch (command.getType()) {
            case "user":
                library.getUsers().add(newUser);
                break;
            case "host":
                library.getHosts().add((Host) newUser);
                break;
            case "artist":
                library.getArtists().add((Artist) newUser);
                break;
            default:
                break;
        }

        return "The username " + command.getUsername() + " has been added successfully.";
    }

    /**
     * Method for updating all the running players at a specific timestamp.
     *
     * @param command The commandInput which contains the timestamp.
     * @param library The library used for iterating through the users.
     */
    public static void updatePlayers(final CommandInput command, final Library library) {
        for (User user : library.getUsers()) {
            if (user.getPlayer() != null) {
                user.getPlayer().updateStatus(command.getTimestamp());
            }
        }
    }

    /**
     * This method deletes a specific user from the system;
     *
     * @param user The user that needs to be deleted.
     * @param library The library containing all the users.
     * @return A specific message to confirm whether the task has been done or not.
     */
    public static String deleteUser(final User user, final Library library) {
        if (user.removeCurrentUser(library)) {
            return user.getUsername() + " was successfully deleted.";
        }

        return user.getUsername() + " can't be deleted.";
    }

    /**
     * Adds all the albums from a given artist to an ArrayNode in order
     * to be displayed in the JsonOutput.
     *
     * @param artist The artist.
     * @return The ArrayNode.
     */
    public static ArrayNode showAlbums(final Artist artist) {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode result = objectMapper.createArrayNode();

        for (Album album : artist.getAlbums()) {
            ObjectNode obj = objectMapper.createObjectNode();
            obj.put("name", album.getName());

            ArrayNode songResult = objectMapper.createArrayNode();
            for (Song song : album.getSongs()) {
                songResult.add(song.getName());
            }
            obj.put("songs", songResult);

            result.add(obj);
        }

        return result;
    }

    /**
     * Adds all the podcasts from a given host to an ArrayNode in order
     * to be displayed in the JsonOutput.
     *
     * @param host The host.
     * @return The ArrayNode.
     */
    public static ArrayNode showPodcasts(final Host host) {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode result = objectMapper.createArrayNode();

        for (Podcast podcast : host.getPodcasts()) {
            ObjectNode obj = objectMapper.createObjectNode();
            obj.put("name", podcast.getName());

            ArrayNode episodeResult = objectMapper.createArrayNode();
            for (Episode episode : podcast.getEpisodes()) {
                episodeResult.add(episode.getName());
            }
            obj.put("episodes", episodeResult);

            result.add(obj);
        }

        return result;
    }

}
