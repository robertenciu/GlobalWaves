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

    public static boolean exists(final ArrayList<Merch> merches, final String name) {
        for (Merch merch : merches) {
            if (merch.getName().equals(name)) {
                return true;
            }
        }

        return false;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
