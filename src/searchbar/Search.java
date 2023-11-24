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
    protected ArrayNode result;
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

    /**
     * This method selects a specific file based on the search.
     * The method searches for the media name in the specific library(song, podcast),
     *      and sets the proper attribute to the selected file.
     *
     * @param name The name of the chosen file from the search bar.
     */
    public abstract void select(String name);

    /**
     * Method for listing an array of results based on the search filters.
     *
     * @param filter The search filter.
     * @param user The user that makes the search (since each user has individual search result).
     * @return The array containing the search result (ArrayNode for output purpose).
     */
    public abstract ArrayNode getSearchResultArray(Filters filter, User user);

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

    public final ArrayNode getResult() {
        return result;
    }

    public final void setResult(final ArrayNode result) {
        this.result = result;
    }
}
