package searchbar;

import media.Library;

public abstract class SearchFactory {

    /**
     * FACTORY PATTERN
     * <p>
     * Creates a specific search(song, playlist, podcast) based on the search type.
     *
     * @param type The search type(song, playlist,etc.)
     * @param library The reference of the library.
     * @return The new created player.
     */
    public static Search createSearch(final String type, final Library library) {
        Search search;

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
            case "artist":
                search = new SearchArtist();
                break;
            case "album":
                search = new SearchAlbum();
                break;
            case "host":
                search = new SearchHost();
                break;
            default:
                return null;
        }

        search.setType(type);
        search.setLibrary(library);

        return search;
    }
}
