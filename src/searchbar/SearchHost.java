package searchbar;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import fileio.input.FiltersInput;
import user.Host;
import page.PageLocator;
import user.User;

import java.util.ArrayList;

public final class SearchHost extends Search {

    @Override
    public String select(final String name, final User user) {
        for (Host host : super.library.getHosts()) {
            if (name.equals(host.getUsername())) {
                super.selectedHost = host;
                break;
            }
        }
        super.isSelected = true;
        user.setCurrentPageLocator(PageLocator.HOST);
        user.setCurrentPage(selectedHost);

        return "Successfully selected " + name + "'s page.";
    }

    @Override
    public ArrayNode getSearchResultArray(final FiltersInput filter,
                                          final User user2) {
        ArrayList<Host> result = new ArrayList<>(super.library.getHosts());

        if (filter.getName() != null) {
            result = this.byUsername(result, filter.getName());
        }

        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode resultArray = objectMapper.createArrayNode();

        resultsCount = Math.min(maxResultSize, result.size());
        for (Host host : result.subList(0, resultsCount)) {
            resultArray.add(host.getUsername());
        }

        return resultArray;
    }

    private ArrayList<Host> byUsername(final ArrayList<Host> hosts, final String name) {
        ArrayList<Host> byUsername = new ArrayList<>();

        for (Host host : hosts) {
            if (host.getUsername().startsWith(name)) {
                byUsername.add(host);
            }
        }

        return byUsername;
    }
}
