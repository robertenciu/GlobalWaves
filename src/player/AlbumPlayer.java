package player;

import media.Album;
import media.Playlist;

public final class AlbumPlayer extends PlaylistPlayer {
    private Album loadedAlbum;

    public AlbumPlayer(final Album album) {
        this.loadedAlbum = album;
    }

}
