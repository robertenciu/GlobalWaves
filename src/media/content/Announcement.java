package media.content;

import java.util.ArrayList;

public final class Announcement {
    private final String owner;
    private String name;
    private String description;
    public Announcement(final String owner) {
        this.owner = owner;
    }

    /**
     * Method that searches an announcement by a given name.
     *
     * @param announcements The list of the announcements.
     * @param name The specific name of the announcement.
     * @return The announcement.
     */
    public static Announcement getAnnouncement(final ArrayList<Announcement> announcements,
                                               final String name) {
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
