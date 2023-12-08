package user;

import command.Commands;
import media.*;
import media.content.Event;
import media.content.Merch;
import media.music.Album;
import media.music.MusicCollection;
import media.music.Playlist;
import media.music.Song;
import player.PlaylistPlayer;
import player.SongPlayer;

import java.util.ArrayList;

public final class Artist extends User implements Page {

    private final ArrayList<Album> albums = new ArrayList<>();
    private final ArrayList<Event> events = new ArrayList<>();
    private final ArrayList<Merch> merches = new ArrayList<>();
    public Artist() {
        super();
    }

    @Override
    public String printPage() {
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
     * @param artists a
     * @param name a
     * @return a
     */
    public static Artist getArtist(final ArrayList<Artist> artists, final String name) {
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

    public boolean removeCurrentUser(Library library) {
        if (isInteracting) {
            return false;
        }

        library.getArtists().remove(this);
        for (MusicCollection musicCollection : albums) {
            for (Song song : musicCollection.getSongs()) {
                for (User user : song.getLikedBy()) {
                    user.getLikedSongs().remove(song);
                }
            }
            for (Playlist playlist : library.getPlaylists()) {
                playlist.getSongs().removeAll(musicCollection.getSongs());
            }
            library.getSongs().removeAll(musicCollection.getSongs());
        }
        library.getAlbums().removeAll(this.albums);
        library.getMerches().removeAll(this.merches);
        library.getEvents().removeAll(this.events);
        this.events.clear();
        this.merches.clear();
        this.albums.clear();

        return true;
    }

    public String removeAlbum(Commands command) {
        Album album = Album.getAlbum(albums, command.getName());

        if (album == null) {
            return this.username + " doesn't have an album with the given name.";
        }

        Library library = Library.getInstance();
        for (User user : library.getUsers()) {
            if (user.getPlayer() != null) {
                user.getPlayer().updateStatus(command.getTimestamp());
                Song loadedSong = user.getPlayer().getLoadedSong();
                if (loadedSong != null && album.getSongs().contains(loadedSong)) {
                    return this.username + " can't delete this album.";
                }
            }
        }

        return this.username + " deleted the album successfully.";
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
        if (Album.getAlbum(albums, command.getName()) != null) {
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

    @Override
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

    @Override
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
