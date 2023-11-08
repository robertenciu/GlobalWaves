package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import fileio.input.EpisodeInput;
import fileio.input.LibraryInput;
import fileio.input.PodcastInput;
import fileio.input.SongInput;

import java.util.ArrayList;

public class SearchBar {
    public static int index = 0;
    public static boolean isSelected = false;
    public static String lastSearchType = null;
    public static SongInput selectedSong;
    public static PodcastInput selectedPodcast;
    public static EpisodeInput selectedEpisode;
    public static Playlist selectedPlaylist;
    public static void select(String name, LibraryInput library, ArrayList<Playlist> playlists) {
        if(lastSearchType.equals("song")) {
            for(SongInput song : library.getSongs())
                if(name.equals(song.getName())) {
                    selectedSong = song;
                    selectedPodcast = null;
                    selectedEpisode = null;
                    selectedPlaylist = null;
                    break;
                }
        } else if(lastSearchType.equals("podcast")) {
            for (PodcastInput podcast : library.getPodcasts())
                if (name.equals(podcast.getName())) {
                    selectedPodcast = podcast;
                    selectedSong = null;
                    selectedEpisode = null;
                    selectedPlaylist = null;
                    break;
                }
        } else if(lastSearchType.equals("playlist")) {
            for(Playlist playlist : playlists)
                if(name.equals(playlist.getName())) {
                    selectedPodcast = null;
                    selectedSong = null;
                    selectedEpisode = null;
                    selectedPlaylist = playlist;
                    break;
                }
        }
    }
    public static ArrayNode getSearchResultArray(String type, LibraryInput library, Filters filter, ArrayList<Playlist> playlists) {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode resultArray = objectMapper.createArrayNode();
        if(type.equals("song")) {
            ArrayList<SongInput> results = (ArrayList<SongInput>) library.getSongs().clone();
            if (filter.getTags() != null)
                results = SearchSong.byTags(results, filter.getTags());
            if(filter.getArtist() != null)
                results = SearchSong.byArtist(results, filter.getArtist());
            if(filter.getLyrics() != null)
                results = SearchSong.byLyrics(results, filter.getLyrics());
            if(filter.getName() != null)
                results = SearchSong.byName(results, filter.getName());
            if(filter.getReleaseYear() != null)
                results = SearchSong.byReleaseyear(results, filter.getReleaseYear());
            if(filter.getGenre() != null)
                results = SearchSong.byGenre(results, filter.getGenre());
            if(filter.getAlbum() != null)
                results = SearchSong.byAlbum(results, filter.getAlbum());
            index = Math.min(5, results.size());
            for (SongInput song : results.subList(0, index)) {
                resultArray.add(((SongInput) song).getName());
            }

        } else if(type.equals("podcast")) {
            ArrayList<PodcastInput> results = (ArrayList<PodcastInput>) library.getPodcasts().clone();
            if (filter.getName() != null)
                results = SearchPodcast.byName(results, filter.getName());
            if (filter.getOwner() != null)
                results = SearchPodcast.byOwner(results, filter.getOwner());
            index = Math.min(5, results.size());
            for (PodcastInput podcast : results.subList(0, index)) {
                resultArray.add(((PodcastInput) podcast).getName());
            }
        } else if(type.equals("playlist")) {
            ArrayList<Playlist> results = (ArrayList<Playlist>) playlists.clone();
            if(filter.getName() != null)
                results = SearchPlaylist.byName(results, filter.getName());
            if(filter.getOwner() != null)
                results = SearchPlaylist.byOwner(results, filter.getOwner());
            index = Math.min(5, results.size());
            for (Playlist playlist : results.subList(0, index)) {
                resultArray.add(((Playlist) playlist).getName());
            }
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
        ArrayList<SongInput> byTags = new ArrayList<SongInput>();
        for(SongInput song : songs) {
             if(song.getTags().containsAll(tags))
                 byTags.add(song);
        }
        return byTags;
    }
    public static ArrayList<SongInput> byLyrics(ArrayList<SongInput> songs, String lyrics) {
        ArrayList<SongInput> byLyrycs = new ArrayList<SongInput>();
        for(SongInput song : songs) {
            if(song.getLyrics().contains(lyrics)) {
                byLyrycs.add(song);
            }
        }
        return byLyrycs;
    }
    public static ArrayList<SongInput> byGenre(ArrayList<SongInput> songs, String genre) {
        ArrayList<SongInput> byGenre = new ArrayList<SongInput>();
        for(SongInput song : songs) {
            if(song.getGenre().equals(genre)) {
                byGenre.add(song);
            }
        }
        return byGenre;
    }
    public static ArrayList<SongInput> byReleaseyear(ArrayList<SongInput> songs, String releaseYear) {
        ArrayList<SongInput> byReleaseyear = new ArrayList<SongInput>();
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
        ArrayList<SongInput> byArtist = new ArrayList<SongInput>();
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
        ArrayList<PodcastInput> byName = new ArrayList<PodcastInput>();
        for(PodcastInput podcast : podcasts) {
            if(podcast.getName().startsWith(name)) {
                byName.add(podcast);
            }
        }
        return byName;
    }
    public static ArrayList<PodcastInput> byOwner(ArrayList<PodcastInput> podcasts, String Owner) {
        ArrayList<PodcastInput> byName = new ArrayList<PodcastInput>();
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
        ArrayList<Playlist> byOwner = new ArrayList<Playlist>();
        for(Playlist playlist : playlists) {
            if(playlist.getCreatedBy().equals(name))
                byOwner.add(playlist);
        }
        return byOwner;
    }
    public static ArrayList<Playlist> byName(ArrayList<Playlist> playlists, String name) {
        ArrayList<Playlist> byName = new ArrayList<Playlist>();
        for(Playlist playlist : playlists) {
            if(playlist.getName().startsWith(name))
                byName.add(playlist);
        }
        return byName;
    }
}