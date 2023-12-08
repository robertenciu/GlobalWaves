package media.music;

import java.util.ArrayList;

public final class Album extends MusicCollection {
    private int releaseYear;
    private String description;
    public Album (String owner) {
        super.owner = owner;
        super.type = MusicCollectionType.ALBUM;
    }
    public static boolean exists(final ArrayList<Album> albums, final String name) {
        for (Album album : albums) {
            if (album.getName().equals(name)) {
                return true;
            }
        }

        return false;
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
