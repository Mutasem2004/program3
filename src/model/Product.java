package model;

import java.util.HashMap;
import java.util.List;
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
    public void addRequiredItem(Item item, int quantity) { requiredItems.put(item, quantity); }

    @Override
    public String toString() { return name; }

    // ===== التخزين =====
    public String toFileString() {
        StringBuilder sb = new StringBuilder();
        sb.append(id).append(",").append(name);
        for (Map.Entry<Item, Integer> entry : requiredItems.entrySet()) {
            sb.append(",").append(entry.getKey().getId()).append(":").append(entry.getValue());
        }
        return sb.toString();
    }

    public static Product fromFileString(String line, List<Item> allItems) {
        String[] parts = line.split(",");
        Product p = new Product(Integer.parseInt(parts[0]), parts[1]);
        for (int i = 2; i < parts.length; i++) {
            String[] kv = parts[i].split(":");
            int itemId = Integer.parseInt(kv[0]);
            int qty = Integer.parseInt(kv[1]);
            Item item = allItems.stream().filter(it -> it.getId() == itemId).findFirst().orElse(null);
            if (item != null) p.addRequiredItem(item, qty);
        }
        return p;
    }
}
