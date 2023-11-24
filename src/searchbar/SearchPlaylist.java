package searchbar;

import media.Playlist;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import command.Filters;
import user.User;

import java.util.ArrayList;

public final class SearchPlaylist extends Search {
    public SearchPlaylist() {
    }

    @Override
    public void select(final String name) {
        for (Playlist playlist : super.playlists) {
            if (name.equals(playlist.getName())) {
                super.selectedPlaylist = playlist;
                break;
            }
        }
        super.isSelected = true;
    }

    @Override
    public ArrayNode getSearchResultArray(final Filters filter, final User user) {
        ArrayList<Playlist> result = new ArrayList<>(super.playlists);

        if (filter.getName() != null) {
            result = this.byName(result, filter.getName());
        }
        if (filter.getOwner() != null) {
            result = this.byOwner(result, filter.getOwner());
        }
        result.removeIf(playlist -> playlist.getVisibility().equals("private")
                && !user.getPlaylists().contains(playlist));

        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode resultArray = objectMapper.createArrayNode();

        super.resultsCount = Math.min(maxResultSize, result.size());
        for (Playlist playlist : result.subList(0, resultsCount)) {
            resultArray.add(playlist.getName());
        }
        return resultArray;
    }

    private ArrayList<Playlist> byOwner(final ArrayList<Playlist> playlists,
                                        final String name) {
        ArrayList<Playlist> byOwner = new ArrayList<>();
        for (Playlist playlist : playlists) {
            if (playlist.getOwner().equals(name)) {
                byOwner.add(playlist);
            }
        }
        return byOwner;
    }

    private ArrayList<Playlist> byName(final ArrayList<Playlist> playlists,
                                       final String name) {
        ArrayList<Playlist> byName = new ArrayList<>();
        for (Playlist playlist : playlists) {
            if (playlist.getName().startsWith(name)) {
                byName.add(playlist);
            }
        }
        return byName;
    }
}
