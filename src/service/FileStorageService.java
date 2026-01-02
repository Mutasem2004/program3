package service;

import model.Item;
import model.Product;
import model.ProductLine;
import model.Task;

import java.io.*;
import java.util.*;

public class FileStorageService {

    private static final String DATA_DIR = "data";
    private static final String ITEMS_FILE = DATA_DIR + "/items.txt";
    private static final String PRODUCTS_FILE = DATA_DIR + "/products.txt.txt";
    private static final String TASKS_FILE = DATA_DIR + "/tasks.txt";
    private static final String LINES_FILE = DATA_DIR + "/product_lines.txt";

    public static void saveItems(List<Item> items) throws IOException {
        ensureDataDir();
        try (PrintWriter pw = new PrintWriter(new FileWriter(ITEMS_FILE))) {
            for (Item item : items) {
                pw.println(item.getId() + "," + item.getName() + "," + item.getCategory() + "," +
                        item.getPrice() + "," + item.getAvailableQuantity() + "," + item.getMinimumQuantity());
            }
        }
    }

    public static List<Item> loadItems() throws IOException {
        List<Item> items = new ArrayList<>();
        File file = new File(ITEMS_FILE);
        if (!file.exists()) return items;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                items.add(new Item(Integer.parseInt(p[0]), p[1], p[2],
                        Double.parseDouble(p[3]), Integer.parseInt(p[4]), Integer.parseInt(p[5])));
            }
        }
        return items;
    }

    public static void saveProducts(List<Product> products) throws IOException {
        ensureDataDir();
        try (PrintWriter pw = new PrintWriter(new FileWriter(PRODUCTS_FILE))) {
            for (Product p : products) {
                StringBuilder sb = new StringBuilder();
                sb.append(p.getId()).append("|").append(p.getName()).append("|");
                p.getRequiredItems().forEach((item, qty) -> sb.append(item.getId()).append(":").append(qty).append(","));
                pw.println(sb.toString());
            }
        }
    }

    public static List<Product> loadProducts(List<Item> items) throws IOException {
        List<Product> products = new ArrayList<>();
        Map<Integer, Item> itemMap = new HashMap<>();
        for (Item i : items) itemMap.put(i.getId(), i);

        File file = new File(PRODUCTS_FILE);
        if (!file.exists()) return products;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                products.add(Product.fromFileString(line, (List<Item>) itemMap));
            }
        }
        return products;
    }

    public static void saveTasks(List<Task> tasks) throws IOException {
        ensureDataDir();
        try (PrintWriter pw = new PrintWriter(new FileWriter(TASKS_FILE))) {
            for (Task t : tasks) {
                pw.println(t.toFileString());
            }
        }
    }

    public static List<Task> loadTasks(List<Product> products) throws IOException {
        List<Task> tasks = new ArrayList<>();
        Map<Integer, Product> productMap = new HashMap<>();
        for (Product p : products) productMap.put(p.getId(), p);

        File file = new File(TASKS_FILE);
        if (!file.exists()) return tasks;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                tasks.add(Task.fromFileString(line, (List<Product>) productMap));
            }
        }
        return tasks;
    }

    public static void saveProductLines(List<ProductLine> lines) throws IOException {
        ensureDataDir();
        try (PrintWriter pw = new PrintWriter(new FileWriter(LINES_FILE))) {
            for (ProductLine line : lines) {
                StringBuilder sb = new StringBuilder();
                sb.append(line.getId()).append("|").append(line.getName()).append("|");
                for (Task t : line.getTasks()) sb.append(t.getId()).append(",");
                pw.println(sb.toString());
            }
        }
    }

    public static List<ProductLine> loadProductLines(List<Task> tasks) throws IOException {
        List<ProductLine> lines = new ArrayList<>();
        Map<Integer, Task> taskMap = new HashMap<>();
        for (Task t : tasks) taskMap.put(t.getId(), t);

        File file = new File(LINES_FILE);
        if (!file.exists()) return lines;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String lineStr;
            while ((lineStr = br.readLine()) != null) {
                String[] parts = lineStr.split("\\|");
                ProductLine line = new ProductLine(Integer.parseInt(parts[0]), parts[1]);
                if (parts.length > 2 && !parts[2].isEmpty()) {
                    String[] taskIds = parts[2].split(",");
                    for (String tid : taskIds) {
                        if (tid.isEmpty()) continue;
                        Task t = taskMap.get(Integer.parseInt(tid));
                        if (t != null) line.addTask(t);
                    }
                }
                lines.add(line);
            }
        }
        return lines;
    }

    private static void ensureDataDir() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) dir.mkdirs();
    }
}
