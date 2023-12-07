package user;

import command.Commands;
import media.Library;

public class Host extends User {
    public Host () {
        super();
        super.page = Page.HOST;
    }

    public String printHost() {
        return "Nimic deocamdata";
    }

    public String addAlbum(final Commands command, final Library library) {
        return super.getUsername() + " is not an artist.";
    }
    public String addEvent(final Commands command, final Library library) {
        return super.getUsername() + " is not an artist.";
    }
    public String addMerch(final Commands command, final Library library) {
        return super.getUsername() + " is not an artist.";
    }
}
