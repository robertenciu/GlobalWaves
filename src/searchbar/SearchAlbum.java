package searchbar;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import command.Filters;
import media.music.Album;
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

        return "Successfully selected " + name + ".";
    }

    @Override
    public ArrayNode getSearchResultArray(final Filters filter, final User user) {
        ArrayList<Album> result = new ArrayList<>(super.library.getAlbums());

        if (filter.getName() != null) {
            result = this.byName(result, filter.getName());
        }
        if (filter.getOwner() != null) {
            result = this.byOwner(result, filter.getOwner());
        }
        if (filter.getDescription() != null) {
            result = this.byDescription(result, filter.getDescription());
        }

        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode resultArray = objectMapper.createArrayNode();

        resultsCount = Math.min(maxResultSize, result.size());
        for (Album album : result.subList(0, resultsCount)) {
            resultArray.add(album.getName());
        }

        return resultArray;
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
