package media.content;

import java.util.ArrayList;

public final class Event {
    private final String owner;
    private String name;
    private String date;
    private String description;
    public Event(final String owner) {
        this.owner = owner;
    }

    public static Event getEvent(final ArrayList<Event> events, final String name) {
        for (Event event : events) {
            if (event.getName().equals(name)) {
                return event;
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
