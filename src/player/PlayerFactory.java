package player;

import media.music.Album;
import media.music.Playlist;
import searchbar.Search;
import user.User;

public abstract class PlayerFactory {

    /**
     * FACTORY PATTERN
     * <p>
     * Creates a specific player(song, playlist, podcast) based on the search type.
     * Sets the player status.
     *
     * @param search The search made by user.
     * @param status The reference of the user status.
     * @return The new created player.
     */
    public static Player createPlayer(final Search search, final Status status, final User user) {
        Player player;

        switch (search.getType()) {
            case "song":
                player = new SongPlayer(search.getSelectedSong());
                break;
            case "playlist":
                player = new PlaylistPlayer(new Playlist(search.getSelectedPlaylist()));
                break;
            case "album":
                player = new AlbumPlayer(new Album(search.getSelectedAlbum()));
                break;
            case "podcast":
                player = new PodcastPlayer(search.getSelectedPodcast());
                break;
            default:
                return null;
        }

        player.setStatus(status);
        player.setUser(user);

        return player;
    }
}
