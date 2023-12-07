package user;

import command.Commands;
import media.*;

import java.util.ArrayList;

public class Artist extends User {

    private final ArrayList<Album> albums = new ArrayList<>();
    private final ArrayList<Event> events = new ArrayList<>();
    private final ArrayList<Merch> merches = new ArrayList<>();
    public Artist () {
        super();
        super.page = Page.ARTIST;
    }

    public String printArtist() {
        return "Nimic deocamdata";
    }
    public static Artist getArtistByName(final ArrayList<Artist> artists, final String name) {
        if (artists.isEmpty() || name == null) {
            return null;
        }
        for (Artist artist : artists) {
            if (artist.getUsername().equals(name)) {
                return artist;
            }
        }
        return null;
    }
    private boolean hasDuplicateSongs(ArrayList<Song> songs) {
        for (int i = 0; i < songs.size() - 1; i++) {
            for (int j = i + 1; j < songs.size(); j++) {
                if (songs.get(i).getName().equals(songs.get(j).getName())) {
                    return true;
                }
            }
        }
        return false;
    }
    @Override
    public String addAlbum(final Commands command, final Library library) {
        if (Album.exists(albums, command.getName())) {
            return super.getUsername() + " has another album with the same name.";
        }

        if (hasDuplicateSongs(command.getSongs())) {
            return super.getUsername() + " has the same song at least twice in this album.";
        }

        Album newAlbum = new Album(command.getUsername());
        newAlbum.setName(command.getName());
        newAlbum.setReleaseYear(command.getReleaseYear());
        newAlbum.setDescription(command.getDescription());
        newAlbum.setSongs(command.getSongs());

        library.getAlbums().add(newAlbum);
        library.getSongs().addAll(newAlbum.getSongs());
        this.albums.add(newAlbum);
        return super.getUsername() + " has added new album successfully.";
    }

    private boolean isDateValid(final Commands command) {
        final String yearAsString = command.getDate().substring(6);
        final int year = Integer.parseInt(yearAsString);

        final String monthAsString = command.getDate().substring(3, 5);
        final int month = Integer.parseInt(monthAsString);

        final String dayAsString = command.getDate().substring(0, 2);
        final int day = Integer.parseInt(dayAsString);


        if (month == 2 && day > 28) {
            return false;
        }

        if (day > 31) {
            return false;
        }

        if (month > 12) {
            return false;
        }

        return year <= 2023 && year >= 1900;
    }
    public String addEvent(final Commands command, final Library library) {
        if (Event.exists(events, command.getName())) {
            return super.getUsername() + " has another event with the same name.";
        }

        if (!isDateValid(command)) {
            return "Event for " + super.getUsername() + " does not have a valid date.";
        }

        Event newEvent = new Event(command.getUsername());
        newEvent.setDate(command.getDate());
        newEvent.setDescription(command.getDescription());
        newEvent.setName(command.getName());

        library.getEvents().add(newEvent);
        this.events.add(newEvent);
        return super.getUsername() + " has added new event successfully.";
    }

    private boolean isPriceValid(final Commands command) {
        return command.getPrice() >= 0;
    }
    public String addMerch(final Commands command, final Library library) {
        if (Merch.exists(merches, command.getName())) {
            return super.getUsername() + " has merchandise with the same name.";
        }

        if (!isPriceValid(command)) {
            return "Price for merchandise can not be negative.";
        }

        Merch newMerch = new Merch(command.getUsername());
        newMerch.setName(command.getName());
        newMerch.setPrice(command.getPrice());
        newMerch.setDescription(command.getDescription());

        library.getMerches().add(newMerch);
        this.merches.add(newMerch);
        return super.getUsername() + " has added new merchandise successfully.";
    }

    public ArrayList<Album> getAlbums() {
        return albums;
    }
}
