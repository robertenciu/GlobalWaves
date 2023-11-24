package searchbar;

import com.fasterxml.jackson.databind.node.ArrayNode;
import main.Filters;
import main.User;
import media.Library;
import media.Playlist;
import media.Podcast;
import media.Song;

import java.util.ArrayList;

public abstract class Search {
    public ArrayNode result;
    protected int resultsCount = 0;
    protected final int maxResultSize = 5;
    protected boolean isSelected;
    protected Song selectedSong;
    protected Podcast selectedPodcast;
    protected Playlist selectedPlaylist;
    protected ArrayList<Song> songs;
    protected ArrayList<Podcast> podcasts;
    protected ArrayList<Playlist> playlists;
    private String type;

    /**
     * Creates a specific search(song, playlist, podcast) based on the search type.
     *
     * @param type The search type(song, playlist,etc.)
     * @param library The reference of the library.
     * @return The new created player.
     */
    public static Search newSearch(final String type, final Library library) {
        if (type == null) {
            return null;
        }
        Search search = null;
        switch (type) {
            case "song":
                search = new SearchSong();
                break;
            case "podcast":
                search = new SearchPodcast();
                break;
            case "playlist":
                search = new SearchPlaylist();
                break;
            default:
                break;
        }
        if (search != null) {
            search.setType(type);
            search.setPodcasts(library.getPodcasts());
            search.setPlaylists(library.getPlaylists());
            search.setSongs(library.getSongs());
        }
        return search;
    }

    public abstract void select(final String name);

    public abstract ArrayNode getSearchResultArray(final Filters filter, final User user);

    public final int getResultsCount() {
        return resultsCount;
    }

    public final boolean isSelected() {
        return isSelected;
    }

    public final void setSelected(final boolean selected) {
        isSelected = selected;
    }

    public final void setType(final String type) {
        this.type = type;
    }

    public final String getType() {
        return type;
    }

    public final void setSongs(final ArrayList<Song> songs) {
        this.songs = songs;
    }

    public final void setPodcasts(final ArrayList<Podcast> podcasts) {
        this.podcasts = podcasts;
    }

    public final void setPlaylists(final ArrayList<Playlist> playlists) {
        this.playlists = playlists;
    }

    public final Playlist getSelectedPlaylist() {
        return selectedPlaylist;
    }

    public final Song getSelectedSong() {
        return selectedSong;
    }

    public final Podcast getSelectedPodcast() {
        return selectedPodcast;
    }
}
