package player;

import media.music.Album;

public final class AlbumPlayer extends PlaylistPlayer {
    private final Album loadedAlbum;

    public AlbumPlayer(final Album album) {
        super(album);
        this.loadedAlbum = album;
    }

}
