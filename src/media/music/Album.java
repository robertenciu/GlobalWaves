package media.music;

import java.util.ArrayList;

public final class Album extends MusicCollection {
    private int releaseYear;
    private String description;
    public Album (String owner) {
        super.owner = owner;
        super.type = MusicCollectionType.ALBUM;
    }

    public Album (final Album album) {
        super(album);
        this.description = album.description;
        this.releaseYear = album.releaseYear;
    }

    public static Album getAlbum(final ArrayList<Album> albums, final String name) {
        for (Album album : albums) {
            if (album.getName().equals(name)) {
                return album;
            }
        }

        return null;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
