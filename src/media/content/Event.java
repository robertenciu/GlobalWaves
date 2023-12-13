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

    /**
     * Method that searches an event by a given name.
     *
     * @param events The list of the events.
     * @param name The specific name of the event.
     * @return The event.
     */
    public static Event getEvent(final ArrayList<Event> events,
                                 final String name) {
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

    public void setName(final String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(final String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }
}
