package media.content;

import java.util.ArrayList;

public final class Announcement {
    private final String owner;
    private String name;
    private String description;
    public Announcement(final String owner) {
        this.owner = owner;
    }

    public static Announcement getAnnouncement(final ArrayList<Announcement> announcements, final String name) {
        for (Announcement announcement : announcements) {
            if (announcement.getName().equals(name)) {
                return announcement;
            }
        }

        return null;
    }

    public String getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }
}
