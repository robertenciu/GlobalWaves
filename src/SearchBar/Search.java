package SearchBar;

import com.fasterxml.jackson.databind.node.ArrayNode;
import main.*;
import Media.*;
import java.util.ArrayList;

public abstract class Search {
    protected int resultsCount = 0;
    protected boolean isSelected = false;
    protected Song selectedSong;
    protected Podcast selectedPodcast;
    protected Playlist selectedPlaylist;
    protected ArrayList<Song> songs;
    protected ArrayList<Podcast> podcasts;
    protected ArrayList<Playlist> playlists;
    private String type;
    public static Search newSearch(final String type,
                                   final ArrayList<Song> songs,
                                   final ArrayList<Podcast> podcasts,
                                   final ArrayList<Playlist> playlists) {
        if (type == null) {
            return null;
        }
        Search search = null;
        switch(type) {
            case "song":
                search = new SearchSong();
            case "podcast":
                search = new SearchPodcast();
            case "playlist":
                search = new SearchPlaylist();
            default:
                break;
        }
        if (search != null) {
            search.setType(type);
            search.setPodcasts(podcasts);
            search.setPlaylists(playlists);
            search.setSongs(songs);
        }
        return search;
    }

    public abstract void select(final String name);

    public abstract ArrayNode getSearchResultArray(final Filters filter, final User user);

    public void reset() {
        resultsCount = 0;
        isSelected = false;
        type = null;
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

    public void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
    }

    public void setPodcasts(ArrayList<Podcast> podcasts) {
        this.podcasts = podcasts;
    }

    public void setPlaylists(ArrayList<Playlist> playlists) {
        this.playlists = playlists;
    }

    public Song getSelectedSong() {
        return selectedSong;
    }

    public Podcast getSelectedPodcast() {
        return selectedPodcast;
    }

    public Playlist getSelectedPlaylist() {
        return selectedPlaylist;
    }
}
