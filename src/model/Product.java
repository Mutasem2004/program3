package model;

import java.util.HashMap;
import java.util.Map;

public class Product {

    private int id;
    private String name;
    private Map<Item, Integer> requiredItems;

    public Product(int id, String name) {
        this.id = id;
        this.name = name;
        this.requiredItems = new HashMap<>();
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public Map<Item, Integer> getRequiredItems() { return requiredItems; }

    public void addRequiredItem(Item item, int quantity) {
        requiredItems.put(item, quantity);
    }
}
