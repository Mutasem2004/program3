package service;

import model.Item;
import model.Product;
import model.Task;
import model.ProductLine;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InventoryManager {

    private static final String DATA_DIR = "data";
    private static final String ITEMS_FILE = DATA_DIR + "/items.txt";
    private static final String PRODUCTS_FILE = DATA_DIR + "/products.txt";
    private static final String TASKS_FILE = DATA_DIR + "/tasks.txt";
    private static final String LINES_FILE = DATA_DIR + "/productLines.txt";

    private List<Item> items = new ArrayList<>();
    private List<Product> products = new ArrayList<>();
    private List<Task> tasks = new ArrayList<>();
    private List<ProductLine> productLines = new ArrayList<>();

    public InventoryManager() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) dir.mkdir();

        loadItemsFromFile();
        loadProductsFromFile();
        loadTasksFromFile();
        loadProductLinesFromFile();
    }

    /* ================= خطوط الإنتاج ================= */
    public void addProductLine(ProductLine line) {
        if (line.getId() <= 0)
            throw new IllegalArgumentException("❌ ID خط الإنتاج يجب أن يكون موجب");
        if (productLines.stream().anyMatch(l -> l.getId() == line.getId()))
            throw new IllegalArgumentException("❌ ID خط الإنتاج مكرر");
        productLines.add(line);
        saveProductLinesToFile();
    }

    public List<ProductLine> getAllProductLines() {
        return productLines;
    }

    /* ================= المهمات ================= */
    public void addTask(Task task) {
        if (task.getId() <= 0)
            throw new IllegalArgumentException("❌ ID المهمة يجب أن يكون موجب");
        if (tasks.stream().anyMatch(t -> t.getId() == task.getId()))
            throw new IllegalArgumentException("❌ ID المهمة مكرر");
        tasks.add(task);
        saveTasksToFile();
    }

    public Task getTask(int id) {
        return tasks.stream().filter(t -> t.getId() == id).findFirst().orElse(null);
    }

    public void removeTask(Task task) {
        tasks.remove(task);
        saveTasksToFile();
    }

    public List<Task> getAllTasks() {
        return tasks;
    }

    /* ================= المواد الخام ================= */
    public void addItem(Item item) {
        if (item.getId() <= 0)
            throw new IllegalArgumentException("❌ ID المادة يجب أن يكون موجب");
        if (items.stream().anyMatch(i -> i.getId() == item.getId()))
            throw new IllegalArgumentException("❌ ID المادة مكرر");
        items.add(item);
        saveItemsToFile();
    }

    public void removeItem(Item item) {
        items.remove(item);
        saveItemsToFile();
    }

    public Item getItem(int id) {
        return items.stream().filter(i -> i.getId() == id).findFirst().orElse(null);
    }

    public List<Item> getAllItems() {
        return items;
    }

    public void updateItemQuantity(int itemId, int newQty) {
        Item item = getItem(itemId);
        if (item != null) {
            item.setAvailableQuantity(newQty);
            saveItemsToFile();
        }
    }

    /* ================= المنتجات ================= */
    public void addProduct(Product p) {
        if (p.getId() <= 0)
            throw new IllegalArgumentException("❌ ID المنتج يجب أن يكون موجب");
        if (products.stream().anyMatch(pr -> pr.getId() == p.getId()))
            throw new IllegalArgumentException("❌ ID المنتج مكرر");
        products.add(p);
        saveProductsToFile();
    }

    public Product getProduct(int id) {
        return products.stream().filter(p -> p.getId() == id).findFirst().orElse(null);
    }

    public List<Product> getAllProducts() {
        return products;
    }

    /* ================= الإنتاج ================= */
    public boolean canProduce(Product product, int quantity) {
        for (Map.Entry<Item, Integer> entry : product.getRequiredItems().entrySet()) {
            Item item = entry.getKey();
            int requiredQty = entry.getValue() * quantity;
            if (item.getAvailableQuantity() < requiredQty)
                return false;
        }
        return true;
    }

    public void consumeItems(Product product, int quantity) throws Exception {
        if (!canProduce(product, quantity))
            throw new Exception("❌ المواد غير كافية لإنتاج " + product.getName());

        for (Map.Entry<Item, Integer> entry : product.getRequiredItems().entrySet()) {
            Item item = entry.getKey();
            int requiredQty = entry.getValue() * quantity;
            item.consumeQuantity(requiredQty);
        }
        saveItemsToFile();
    }

    /* ================= التخزين ================= */
    private void saveItemsToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ITEMS_FILE))) {
            for (Item item : items) pw.println(item.toFileString());
        } catch (IOException e) {
            System.out.println("❌ خطأ أثناء حفظ items.txt");
        }
    }

    private void loadItemsFromFile() {
        File file = new File(ITEMS_FILE);
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) items.add(Item.fromFileString(line));
        } catch (IOException e) {
            System.out.println("❌ خطأ أثناء قراءة items.txt");
        }
    }

    private void saveProductsToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(PRODUCTS_FILE))) {
            for (Product p : products) pw.println(p.toFileString());
        } catch (IOException e) {
            System.out.println("❌ خطأ أثناء حفظ products.txt");
        }
    }

    private void loadProductsFromFile() {
        File file = new File(PRODUCTS_FILE);
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null)
                products.add(Product.fromFileString(line, items));
        } catch (IOException e) {
            System.out.println("❌ خطأ أثناء قراءة products.txt");
        }
    }

    private void saveTasksToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(TASKS_FILE))) {
            for (Task t : tasks) pw.println(t.toFileString());
        } catch (IOException e) {
            System.out.println("❌ خطأ أثناء حفظ tasks.txt");
        }
    }

    private void loadTasksFromFile() {
        File file = new File(TASKS_FILE);
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null)
                tasks.add(Task.fromFileString(line, products));
        } catch (IOException e) {
            System.out.println("❌ خطأ أثناء قراءة tasks.txt");
        }
    }

    private void saveProductLinesToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(LINES_FILE))) {
            for (ProductLine line : productLines) pw.println(line.toFileString());
        } catch (IOException e) {
            System.out.println("❌ خطأ أثناء حفظ productLines.txt");
        }
    }

    private void loadProductLinesFromFile() {
        File file = new File(LINES_FILE);
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null)
                productLines.add(ProductLine.fromFileString(line, tasks));
        } catch (IOException e) {
            System.out.println("❌ خطأ أثناء قراءة productLines.txt");
        }
    }
}
