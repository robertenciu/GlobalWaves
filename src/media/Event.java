package media;

import java.util.ArrayList;

public final class Event {
    private String owner;
    private String name;
    private String date;
    private String description;
    public Event(String owner) {
        this.owner = owner;
    }

    public static boolean exists(final ArrayList<Event> events, final String name) {
        for (Event event : events) {
            if (event.getName().equals(name)) {
                return true;
            }
        }

        return false;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
