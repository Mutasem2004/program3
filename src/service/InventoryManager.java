package service;

import model.Item;
import model.Product;
import model.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InventoryManager {
    private List<Item> items = new ArrayList<>();
    private List<Product> products = new ArrayList<>();
    private List<Task> tasks = new ArrayList<>();

    // ===== المواد الخام =====
    public void addItem(Item item) { items.add(item); }
    public void removeItem(Item item) { items.remove(item); }
    public Item getItem(int id) {
        return items.stream().filter(i -> i.getId() == id).findFirst().orElse(null);
    }
    public List<Item> getAllItems() { return items; }

    // ===== المنتجات =====
    public void addProduct(Product p) { products.add(p); }
    public Product getProduct(int id) {
        return products.stream().filter(p -> p.getId() == id).findFirst().orElse(null);
    }
    public List<Product> getAllProducts() { return products; }

    // ===== المهمات =====
    public void addTask(Task task) { tasks.add(task); }
    public Task getTask(int id) {
        return tasks.stream().filter(t -> t.getId() == id).findFirst().orElse(null);
    }
    public List<Task> getAllTasks() { return tasks; }

    // ===== دوال لإنتاج المنتجات =====
    public boolean canProduce(Product product, int quantity) {
        // تتحقق إذا كل المواد المطلوبة متوفرة
        for (Map.Entry<Item, Integer> entry : product.getRequiredItems().entrySet()) {
            Item item = entry.getKey();
            int requiredQty = entry.getValue() * quantity;
            if (item.getAvailableQuantity() < requiredQty) {
                return false;
            }
        }
        return true;
    }

    public void consumeItems(Product product, int quantity) throws Exception {
        if (!canProduce(product, quantity)) {
            throw new Exception("❌ المواد غير كافية لإنتاج " + product.getName());
        }
        // استهلاك المواد
        for (Map.Entry<Item, Integer> entry : product.getRequiredItems().entrySet()) {
            Item item = entry.getKey();
            int requiredQty = entry.getValue() * quantity;
            item.consumeQuantity(requiredQty);
        }
    }
}
