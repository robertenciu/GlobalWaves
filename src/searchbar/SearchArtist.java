package searchbar;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import fileio.input.FiltersInput;
import user.Artist;
import page.PageLocator;
import user.User;

import java.util.ArrayList;

public final class SearchArtist extends Search {

    @Override
    public String select(final String name, final User user) {
        for (Artist artist : super.library.getArtists()) {
            if (name.equals(artist.getUsername())) {
                super.selectedArtist = artist;
                break;
            }
        }
        super.isSelected = true;
        user.setCurrentPageLocator(PageLocator.ARTIST);
        user.setCurrentPage(selectedArtist);

        return "Successfully selected " + name + "'s page.";
    }

    @Override
    public ArrayNode getSearchResultArray(final FiltersInput filter,
                                          final User user) {
        ArrayList<Artist> result = new ArrayList<>(super.library.getArtists());

        if (filter.getName() != null) {
            result = this.byUsername(result, filter.getName());
        }

        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode resultArray = objectMapper.createArrayNode();

        resultsCount = Math.min(maxResultSize, result.size());
        for (Artist artist : result.subList(0, resultsCount)) {
            resultArray.add(artist.getUsername());
        }

        return resultArray;
    }

    private ArrayList<Artist> byUsername(final ArrayList<Artist> artists, final String name) {
        ArrayList<Artist> byUsername = new ArrayList<>();

        for (Artist artist : artists) {
            if (artist.getUsername().startsWith(name)) {
                byUsername.add(artist);
            }
        }

        return byUsername;
    }
}
