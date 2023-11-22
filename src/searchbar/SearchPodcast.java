package searchbar;

import media.Podcast;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import main.Filters;
import main.User;

import java.util.ArrayList;

public final class SearchPodcast extends Search {

    public SearchPodcast() {
    }

    public void select(final String name) {
        for (Podcast podcast : super.podcasts) {
            if (name.equals(podcast.getName())) {
                super.selectedPodcast = podcast;
                break;
            }
        }

        super.isSelected = true;
    }

    public ArrayNode getSearchResultArray(final Filters filter, final User user) {
        ArrayList<Podcast> result = new ArrayList<>(super.podcasts);

        if (filter.getName() != null) {
            result = this.byName(result, filter.getName());
        }
        if (filter.getOwner() != null) {
            result = this.byOwner(result, filter.getOwner());
        }

        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode resultArray = objectMapper.createArrayNode();

        resultsCount = Math.min(maxResultSize, result.size());
        for (Podcast podcast : result.subList(0, resultsCount)) {
            resultArray.add(podcast.getName());
        }
        return resultArray;
    }
    private ArrayList<Podcast> byName(final ArrayList<Podcast> podcasts,
                                      final String name) {
        ArrayList<Podcast> byName = new ArrayList<>();
        for (Podcast podcast : podcasts) {
            if (podcast.getName().startsWith(name)) {
                byName.add(podcast);
            }
        }
        return byName;
    }

    private ArrayList<Podcast> byOwner(final ArrayList<Podcast> podcasts,
                                            final String owner) {
        ArrayList<Podcast> byName = new ArrayList<>();
        for (Podcast podcast : podcasts) {
            if (podcast.getOwner().equals(owner)) {
                byName.add(podcast);
            }
        }
        return byName;
    }
}
