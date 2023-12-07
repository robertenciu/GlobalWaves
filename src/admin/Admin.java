package admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import command.Commands;
import media.Album;
import media.Library;
import media.Song;
import user.Artist;
import user.Host;
import user.User;

public class Admin {
    public static String addUser(final Commands command, final Library library) {
        User newUser;
        if (command.getType().equals("user")) {
            newUser = new User();
        } else if (command.getType().equals("artist")) {
            newUser = new Artist();
        } else {
            newUser = new Host();
        }

        newUser.setUsername(command.getUsername());
        newUser.setAge(command.getAge());
        newUser.setCity(command.getCity());

        library.getUsers().add(newUser);

        switch (command.getType()) {
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

        Artist artist = Artist.getArtistByName(library.getArtists(), command.getUsername());
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
