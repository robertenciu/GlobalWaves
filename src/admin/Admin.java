package admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import command.Commands;
import media.Album;
import media.Library;
import media.music.Song;
import user.Artist;
import user.Host;
import user.User;

public class Admin {
    private Admin() { }
    public static String addUser(final Commands command, final Library library) {
        User newUser = null;
        if (command.getType().equals("user")) {
            newUser = new User();
            newUser.setConnectionStatus("Online");
        } else if (command.getType().equals("artist")) {
            newUser = new Artist();
        } else if (command.getType().equals("host")){
            newUser = new Host();
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

    public static ArrayNode showAlbums(final Commands command, final Library library) {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode result = objectMapper.createArrayNode();

        Artist artist = Artist.getArtist(library.getArtists(), command);
        assert artist != null;
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
}
