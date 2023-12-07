package searchbar;

import com.fasterxml.jackson.databind.node.ArrayNode;
import command.Filters;
import media.Album;
import user.Artist;
import user.Host;
import user.Page;
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
        user.setPage(Page.HOST);

        return "not implemented";
    }

    @Override
    public ArrayNode getSearchResultArray(Filters filter, User user) {
        return null;
    }

    public ArrayList<Host> byUsername(final ArrayList<Host> hosts, final String name) {
        ArrayList<Host> byUsername = new ArrayList<>();

        for (Host host : hosts) {
            if (host.getUsername().startsWith(name)) {
                byUsername.add(host);
            }
        }

        return byUsername;
    }
}
