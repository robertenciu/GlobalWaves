package user;

import command.Commands;
import media.*;
import media.music.Song;

import java.util.ArrayList;

public final class Artist extends User {

    private final ArrayList<Album> albums = new ArrayList<>();
    private final ArrayList<Event> events = new ArrayList<>();
    private final ArrayList<Merch> merches = new ArrayList<>();
    public Artist() {
        super();
        super.page = Page.ARTIST;
    }

    public String printArtist() {
        StringBuilder result = new StringBuilder("Albums:\n\t[");

        for (Album album : albums) {
            result.append(album.getName()).append(", ");
        }
        if (!albums.isEmpty()) {
            result.deleteCharAt(result.length() - 1);
            result.deleteCharAt(result.length() - 1);
        }

        result.append("]\n\nMerch:\n\t[");

        for (Merch merch : merches) {
            result.append(merch.getName()).append(" - ");
            result.append(merch.getPrice()).append(":\n\t");
            result.append(merch.getDescription()).append(", ");
        }
        if (!merches.isEmpty()) {
            result.deleteCharAt(result.length() - 1);
            result.deleteCharAt(result.length() - 1);
        }

        result.append("]\n\nEvents:\n\t[");

        for (Event event : events) {
            result.append(event.getName()).append(" - ");
            result.append(event.getDate()).append(":\n\t");
            result.append(event.getDescription()).append(", ");
        }
        if (!events.isEmpty()) {
            result.deleteCharAt(result.length() - 1);
            result.deleteCharAt(result.length() - 1);
        }

        result.append("]");

        return result.toString();
    }

    /**
     * @param artists
     * @param command
     * @return
     */
    public static Artist getArtist(final ArrayList<Artist> artists, final Commands command) {
        if (artists.isEmpty() || command == null) {
            return null;
        }
        for (Artist artist : artists) {
            if (artist.getUsername().equals(command.getUsername())) {
                return artist;
            }
        }
        return null;
    }
    private boolean hasDuplicateSongs(final ArrayList<Song> songs) {
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
        final int[] yearGap = new int[] {1900, 2023};
        final int maxMonth = 12;
        final int maxDaysInMonth = 31;
        final int maxDaysInFebruary = 28;
        final int february = 2;

        final String yearAsString = command.getDate().substring(6);
        final int year = Integer.parseInt(yearAsString);

        final String monthAsString = command.getDate().substring(3, 5);
        final int month = Integer.parseInt(monthAsString);

        final String dayAsString = command.getDate().substring(0, 2);
        final int day = Integer.parseInt(dayAsString);


        if (month == february && day > maxDaysInFebruary ) {
            return false;
        }

        if (month > maxMonth) {
            return false;
        }

        if (day > maxDaysInMonth) {
            return false;
        }

        return year <= yearGap[1] && year >= yearGap[0];
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
