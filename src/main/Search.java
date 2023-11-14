package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import fileio.input.EpisodeInput;
import fileio.input.LibraryInput;
import fileio.input.PodcastInput;
import fileio.input.SongInput;

import java.util.ArrayList;

public class Search {
    private int resultsCount = 0;
    private boolean isSelected = false;
    private String type;
    private SongInput selectedSong;
    private PodcastInput selectedPodcast;
    private Playlist selectedPlaylist;
    private static Search instance = null;
    public Search () {}
    public void reset() {
        resultsCount = 0;
        isSelected = false;
        type = null;
        selectedSong = null;
        selectedPodcast = null;
        selectedPlaylist = null;
    }
    public static Search getInstance() {
        if (instance == null) {
            instance = new Search();
        }
        return instance;
    }

    public int getResultsCount() {
        return resultsCount;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public SongInput getSelectedSong() {
        return selectedSong;
    }

    public void setSelectedSong(SongInput selectedSong) {
        this.selectedSong = selectedSong;
    }

    public PodcastInput getSelectedPodcast() {
        return selectedPodcast;
    }

    public Playlist getSelectedPlaylist() {
        return selectedPlaylist;
    }
    public void select(String name, LibraryInput library, ArrayList<Playlist> playlists) {
        this.isSelected = true;

        switch (this.type) {
            case "song":
                for(SongInput song : library.getSongs())
                    if (name.equals(song.getName())) {
                        this.selectedSong = song;
                        break;
                    }
                break;
            case "podcast":
                for (PodcastInput podcast : library.getPodcasts())
                    if (name.equals(podcast.getName())) {
                        this.selectedPodcast = podcast;
                        break;
                    }
                break;
            case "playlist":
                for(Playlist playlist : playlists)
                    if (name.equals(playlist.getName())) {
                        this.selectedPlaylist = playlist;
                        break;
                    }
                break;
            default:
                break;
        }
    }
    public ArrayNode getSearchResultArray(LibraryInput library, Filters filter, ArrayList<Playlist> playlists) {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode resultArray = objectMapper.createArrayNode();
        switch (type) {
            case "song":
                ArrayList<SongInput> songSearchResult = (ArrayList<SongInput>) library.getSongs().clone();
                if (filter.getTags() != null)
                    songSearchResult = SearchSong.byTags(songSearchResult, filter.getTags());
                if (filter.getArtist() != null)
                    songSearchResult = SearchSong.byArtist(songSearchResult, filter.getArtist());
                if (filter.getLyrics() != null)
                    songSearchResult = SearchSong.byLyrics(songSearchResult, filter.getLyrics());
                if (filter.getName() != null)
                    songSearchResult = SearchSong.byName(songSearchResult, filter.getName());
                if (filter.getReleaseYear() != null)
                    songSearchResult = SearchSong.byReleaseyear(songSearchResult, filter.getReleaseYear());
                if (filter.getGenre() != null)
                    songSearchResult = SearchSong.byGenre(songSearchResult, filter.getGenre());
                if (filter.getAlbum() != null)
                    songSearchResult = SearchSong.byAlbum(songSearchResult, filter.getAlbum());
                resultsCount = Math.min(5, songSearchResult.size());
                for (SongInput song : songSearchResult.subList(0, resultsCount)) {
                    resultArray.add(((SongInput) song).getName());
                }
                break;
            case "podcast":
                ArrayList<PodcastInput> podcastSearchResult = (ArrayList<PodcastInput>) library.getPodcasts().clone();
                if (filter.getName() != null)
                    podcastSearchResult = SearchPodcast.byName(podcastSearchResult, filter.getName());
                if (filter.getOwner() != null)
                    podcastSearchResult = SearchPodcast.byOwner(podcastSearchResult, filter.getOwner());
                resultsCount = Math.min(5, podcastSearchResult.size());
                for (PodcastInput podcast : podcastSearchResult.subList(0, resultsCount)) {
                    resultArray.add(((PodcastInput) podcast).getName());
                }
                break;
            case "playlist":
                ArrayList<Playlist> playlistSearchResult = (ArrayList<Playlist>) playlists.clone();
                if (filter.getName() != null)
                    playlistSearchResult = SearchPlaylist.byName(playlistSearchResult, filter.getName());
                if (filter.getOwner() != null)
                    playlistSearchResult = SearchPlaylist.byOwner(playlistSearchResult, filter.getOwner());
                resultsCount = Math.min(5, playlistSearchResult.size());
                for (Playlist playlist : playlistSearchResult.subList(0, resultsCount)) {
                    resultArray.add(((Playlist) playlist).getName());
                }
                break;
            default:
                break;
        }
        return resultArray;
    }

}
class SearchSong {
    public static ArrayList<SongInput> byName(ArrayList<SongInput> songs, String name) {
        ArrayList<SongInput> byName = new ArrayList<>();
        for(SongInput song : songs) {
            if(song.getName().startsWith(name)) {
                byName.add(song);
            }
        }
        return byName;
    }
    public static ArrayList<SongInput> byAlbum(ArrayList<SongInput> songs, String album) {
        ArrayList<SongInput> byAlbum = new ArrayList<>();
        for(SongInput song : songs) {
            if(song.getAlbum().equals(album)) {
                byAlbum.add(song);
            }
        }
        return byAlbum;
    }
    public static ArrayList<SongInput> byTags(ArrayList<SongInput> songs, ArrayList<String> tags) {
        ArrayList<SongInput> byTags = new ArrayList<>();
        for(SongInput song : songs) {
             if(song.getTags().containsAll(tags))
                 byTags.add(song);
        }
        return byTags;
    }
    public static ArrayList<SongInput> byLyrics(ArrayList<SongInput> songs, String Lyrics) {
        ArrayList<SongInput> byLyrics = new ArrayList<>();
        for(SongInput song : songs) {
            if(song.getLyrics().toLowerCase().contains(Lyrics)) {
                byLyrics.add(song);
            }
        }
        return byLyrics;
    }
    public static ArrayList<SongInput> byGenre(ArrayList<SongInput> songs, String genre) {
        ArrayList<SongInput> byGenre = new ArrayList<>();
        for(SongInput song : songs) {
            if(song.getGenre().equals(genre)) {
                byGenre.add(song);
            }
        }
        return byGenre;
    }
    public static ArrayList<SongInput> byReleaseyear(ArrayList<SongInput> songs, String releaseYear) {
        ArrayList<SongInput> byReleaseyear = new ArrayList<>();
        for(SongInput song : songs) {
            if(releaseYear.charAt(0) == '<') {
                if(song.getReleaseYear() < Integer.parseInt(releaseYear.substring(1)))
                    byReleaseyear.add(song);
            } else {
                if(song.getReleaseYear() > Integer.parseInt(releaseYear.substring(1)))
                    byReleaseyear.add(song);
            }
        }
        return byReleaseyear;
    }
    public static ArrayList<SongInput> byArtist(ArrayList<SongInput> songs, String artist) {
        ArrayList<SongInput> byArtist = new ArrayList<>();
        for(SongInput song : songs) {
            if(song.getArtist().equals(artist)) {
                byArtist.add(song);
            }
        }
        return byArtist;
    }
}
class SearchPodcast {
    public static ArrayList<PodcastInput> byName(ArrayList<PodcastInput> podcasts, String name) {
        ArrayList<PodcastInput> byName = new ArrayList<>();
        for(PodcastInput podcast : podcasts) {
            if(podcast.getName().startsWith(name)) {
                byName.add(podcast);
            }
        }
        return byName;
    }
    public static ArrayList<PodcastInput> byOwner(ArrayList<PodcastInput> podcasts, String Owner) {
        ArrayList<PodcastInput> byName = new ArrayList<>();
        for(PodcastInput podcast : podcasts) {
            if(podcast.getOwner().equals(Owner)) {
                byName.add(podcast);
            }
        }
        return byName;
    }
}
class SearchPlaylist {
    public static ArrayList<Playlist> byOwner(ArrayList<Playlist> playlists, String name) {
        ArrayList<Playlist> byOwner = new ArrayList<>();
        for(Playlist playlist : playlists) {
            if(playlist.getCreatedBy().equals(name))
                byOwner.add(playlist);
        }
        return byOwner;
    }
    public static ArrayList<Playlist> byName(ArrayList<Playlist> playlists, String name) {
        ArrayList<Playlist> byName = new ArrayList<>();
        for(Playlist playlist : playlists) {
            if(playlist.getName().startsWith(name))
                byName.add(playlist);
        }
        return byName;
    }
}