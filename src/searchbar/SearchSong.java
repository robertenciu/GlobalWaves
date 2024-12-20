package searchbar;

import media.music.Song;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import fileio.input.FiltersInput;
import user.User;

import java.util.ArrayList;

public final class SearchSong extends Search {

    public SearchSong() {
    }
    @Override
    public String select(final String name, final User user) {
        for (Song song : super.library.getSongs()) {
            if (name.equals(song.getName())) {
                super.selectedSong = song;
                break;
            }
        }
        super.isSelected = true;

        return "Successfully selected " + name + ".";
    }
    @Override
    public ArrayNode getSearchResultArray(final FiltersInput filter, final User user) {
        ArrayList<Song> result = new ArrayList<>(super.library.getSongs());

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

        resultsCount = Math.min(maxResultSize, result.size());
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
            if (song.getTags().containsAll(tags)) {
                byTags.add(song);
            }
        }
        return byTags;
    }

    private ArrayList<Song> byLyrics(final ArrayList<Song> songs,
                                          final String lyrics) {
        ArrayList<Song> byLyrics = new ArrayList<>();
        for (Song song : songs) {
            if (song.getLyrics().toLowerCase().contains(lyrics.toLowerCase())) {
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
                if (song.getReleaseYear() < Integer.parseInt(releaseYear.substring(1))) {
                    byReleaseyear.add(song);
                }
            } else if (releaseYear.charAt(0) == '>') {
                if (song.getReleaseYear() > Integer.parseInt(releaseYear.substring(1))) {
                    byReleaseyear.add(song);
                }
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
}
