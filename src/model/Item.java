package model;

public class Item {
    private int id;
    private String name;
    private String category;
    private double price;
    private int availableQuantity;
    private int minimumQuantity;

    public Item(int id, String name, String category, double price, int availableQuantity, int minimumQuantity) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.availableQuantity = availableQuantity;
        this.minimumQuantity = minimumQuantity;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public double getPrice() { return price; }

    public int getAvailableQuantity() { return availableQuantity; }
    public int getMinimumQuantity() { return minimumQuantity; }

    public void setAvailableQuantity(int qty) { this.availableQuantity = qty; }

    public void addQuantity(int qty) { this.availableQuantity += qty; }

    public boolean consumeQuantity(int qty) {
        if (availableQuantity >= qty) {
            availableQuantity -= qty;
            return true;
        }
        return false;
    }
}
