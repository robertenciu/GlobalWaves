package searchbar;

import com.fasterxml.jackson.databind.node.ArrayNode;
import command.Filters;
import media.Album;
import user.User;

import java.util.ArrayList;

public final class SearchAlbum extends Search {
    @Override
    public String select(final String name, final User user) {
        for (Album album : super.library.getAlbums()) {
            if (name.equals(album.getName())) {
                super.selectedAlbum = album;
                break;
            }
        }
        super.isSelected = true;

        return "not implemented";
    }

    @Override
    public ArrayNode getSearchResultArray(final Filters filter, final User user) {
        return null;
    }

    public ArrayList<Album> byName(final ArrayList<Album> albums, final String name) {
        ArrayList<Album> byName = new ArrayList<>();

        for (Album album : albums) {
            if (album.getName().startsWith(name)) {
                byName.add(album);
            }
        }

        return byName;
    }

    public ArrayList<Album> byOwner(final ArrayList<Album> albums, final String owner) {
        ArrayList<Album> byOwner = new ArrayList<>();

        for (Album album : albums) {
            if (album.getOwner().startsWith(owner)) {
                byOwner.add(album);
            }
        }

        return byOwner;
    }

    public ArrayList<Album> byDescription(final ArrayList<Album> albums, final String description) {
        ArrayList<Album> byDescription = new ArrayList<>();

        for (Album album : albums) {
            if (album.getDescription().toLowerCase().startsWith(description)) {
                byDescription.add(album);
            }
        }

        return byDescription;
    }

}
