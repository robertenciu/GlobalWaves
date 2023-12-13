package searchbar;

import com.fasterxml.jackson.databind.node.ArrayNode;
import fileio.input.FiltersInput;
import media.Library;
import media.music.Album;
import media.music.Playlist;
import media.music.Song;
import media.podcast.Podcast;
import user.Artist;
import user.Host;
import user.User;

public abstract class Search {
    protected ArrayNode result;
    protected int resultsCount = 0;
    protected final int maxResultSize = 5;
    protected boolean isSelected;
    protected Song selectedSong;
    protected Podcast selectedPodcast;
    protected Playlist selectedPlaylist;
    protected Album selectedAlbum;
    protected Artist selectedArtist;
    protected Host selectedHost;
    protected Library library;
    private String type;

    /**
     * This method selects a specific file based on the search.
     * The method searches for the media name in the specific library(song, podcast),
     *      and sets the proper attribute to the selected file.
     *
     * @param name The name of the chosen file from the search bar.
     */
    public abstract String select(String name, User user);

    /**
     * Method for listing an array of results based on the search filters.
     *
     * @param filter The search filter.
     * @param user The user that makes the search (since each user has individual search result).
     * @return The array containing the search result (ArrayNode for output purpose).
     */
    public abstract ArrayNode getSearchResultArray(FiltersInput filter, User user);

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

    public final Album getSelectedAlbum() {
        return selectedAlbum;
    }

    public final void setLibrary(final Library library) {
        this.library = library;
    }
}
