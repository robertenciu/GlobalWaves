package SearchBar;

import Media.Playlist;
import Media.Podcast;
import Media.Song;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import main.Filters;
import main.User;

import java.util.ArrayList;

public class SearchPlaylist extends Search {
    public SearchPlaylist() {
    }

    public void select(final String name) {
        for (Playlist playlist : super.playlists)
            if (name.equals(playlist.getName())) {
                super.selectedPlaylist = playlist;
            }
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

        super.resultsCount = Math.min(5, result.size());
        for (Playlist playlist : result.subList(0, resultsCount)) {
            resultArray.add(playlist.getName());
        }
        return resultArray;
    }

    private ArrayList<Playlist> byOwner(final ArrayList<Playlist> playlists,
                                        final String name) {
        ArrayList<Playlist> byOwner = new ArrayList<>();
        for (Playlist playlist : playlists) {
            if (playlist.getCreatedBy().equals(name))
                byOwner.add(playlist);
        }
        return byOwner;
    }

    private ArrayList<Playlist> byName(final ArrayList<Playlist> playlists,
                                       final String name) {
        ArrayList<Playlist> byName = new ArrayList<>();
        for (Playlist playlist : playlists) {
            if (playlist.getName().startsWith(name))
                byName.add(playlist);
        }
        return byName;
    }

    public Playlist getSelectedPlaylist() {
        return selectedPlaylist;
    }

    public void setSelectedPlaylist(Playlist selectedPlaylist) {
        this.selectedPlaylist = selectedPlaylist;
    }
}
