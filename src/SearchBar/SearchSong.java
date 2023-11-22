package SearchBar;

import Media.Song;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import main.Filters;
import main.User;

import java.util.ArrayList;

public class SearchSong extends Search {

    public SearchSong() {
    }

    public void select(final String name) {
        for (Song song : super.songs)
            if (name.equals(song.getName())) {
                this.selectedSong = song;
            }
    }
    
    public ArrayNode getSearchResultArray(final Filters filter, final User user) {
        ArrayList<Song> result = new ArrayList<>(super.songs);

        if (filter.getTags() != null) {
            result = this.byTags(result, filter.getTags());
        }
        if (filter.getArtist() != null) {
            result = this.byArtist(result, filter.getArtist());
        }
        if (filter.getLyrics() != null) {
            result = this.byLyrics(result, filter.getLyrics());
        }
        if (filter.getName() != null) {
            result = this.byName(result, filter.getName());
        }
        if (filter.getReleaseYear() != null) {
            result = this.byReleaseyear(result, filter.getReleaseYear());
        }
        if (filter.getGenre() != null) {
            result = this.byGenre(result, filter.getGenre());
        }
        if (filter.getAlbum() != null) {
            result = this.byAlbum(result, filter.getAlbum());
        }

        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode resultArray = objectMapper.createArrayNode();
        
        resultsCount = Math.min(5, result.size());
        for (Song song : result.subList(0, resultsCount)) {
            resultArray.add(song.getName());
        }
        return resultArray;
    }

    private ArrayList<Song> byName(final ArrayList<Song> songs,
                                        final String name) {
        ArrayList<Song> byName = new ArrayList<>();
        for (Song song : songs) {
            if (song.getName().startsWith(name)) {
                byName.add(song);
            }
        }
        return byName;
    }

    private ArrayList<Song> byAlbum(final ArrayList<Song> songs,
                                         final String album) {
        ArrayList<Song> byAlbum = new ArrayList<>();
        for (Song song : songs) {
            if (song.getAlbum().equals(album)) {
                byAlbum.add(song);
            }
        }
        return byAlbum;
    }

    private ArrayList<Song> byTags(final ArrayList<Song> songs,
                                        final ArrayList<String> tags) {
        ArrayList<Song> byTags = new ArrayList<>();
        for (Song song : songs) {
            if (song.getTags().containsAll(tags))
                byTags.add(song);
        }
        return byTags;
    }

    private ArrayList<Song> byLyrics(final ArrayList<Song> songs,
                                          final String Lyrics) {
        ArrayList<Song> byLyrics = new ArrayList<>();
        for (Song song : songs) {
            if (song.getLyrics().toLowerCase().contains(Lyrics.toLowerCase())) {
                byLyrics.add(song);
            }
        }
        return byLyrics;
    }

    private ArrayList<Song> byGenre(final ArrayList<Song> songs,
                                         final String genre) {
        ArrayList<Song> byGenre = new ArrayList<>();
        for (Song song : songs) {
            if (song.getGenre().equalsIgnoreCase(genre)) {
                byGenre.add(song);
            }
        }
        return byGenre;
    }

    private ArrayList<Song> byReleaseyear(final ArrayList<Song> songs,
                                               final String releaseYear) {
        ArrayList<Song> byReleaseyear = new ArrayList<>();
        for (Song song : songs) {
            if (releaseYear.charAt(0) == '<') {
                if (song.getReleaseYear() < Integer.parseInt(releaseYear.substring(1)))
                    byReleaseyear.add(song);
            } else {
                if (song.getReleaseYear() > Integer.parseInt(releaseYear.substring(1)))
                    byReleaseyear.add(song);
            }
        }
        return byReleaseyear;
    }

    private ArrayList<Song> byArtist(final ArrayList<Song> songs,
                                          final String artist) {
        ArrayList<Song> byArtist = new ArrayList<>();
        for (Song song : songs) {
            if (song.getArtist().equals(artist)) {
                byArtist.add(song);
            }
        }
        return byArtist;
    }

    public Song getSelectedSong() {
        return selectedSong;
    }

    public void setSelectedSong(Song selectedSong) {
        this.selectedSong = selectedSong;
    }
}
