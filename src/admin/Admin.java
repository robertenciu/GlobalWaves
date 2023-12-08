package admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import command.Commands;
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
    public static String addUser(final Commands command, final Library library) {
        User newUser = null;
        switch (command.getType()) {
            case "user" -> {
                newUser = new User();
                newUser.setConnectionStatus("Online");
            }
            case "artist" -> newUser = new Artist();
            case "host" -> newUser = new Host();
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

    public static String deleteUser(final Commands command, final Library library) {
        User user = User.getUser(library, command.getUsername());
        for (User interact : user.getUsersInteracting()) {
            if (interact.getPlayer() != null) {
                interact.getPlayer().updateStatus(command.getTimestamp());
            }
        }

        if (user.removeCurrentUser(library)) {
            return command.getUsername() + " was successfully deleted.";
        }

        return command.getUsername() + " can't be deleted.";
    }

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
