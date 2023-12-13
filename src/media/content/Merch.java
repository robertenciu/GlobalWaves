package media.content;

import java.util.ArrayList;

public final class Merch {
    private final String owner;
    private String name;
    private String description;
    private int price;

    public Merch(final String owner) {
        this.owner = owner;
    }

    /**
     * Method that searches a merch by a given name.
     *
     * @param merches The list of the merches.
     * @param name The specific name of the merch.
     * @return The merch.
     */
    public static Merch getMerch(final ArrayList<Merch> merches, final String name) {
        for (Merch merch : merches) {
            if (merch.getName().equals(name)) {
                return merch;
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

    public int getPrice() {
        return price;
    }

    public void setPrice(final int price) {
        this.price = price;
    }
}
