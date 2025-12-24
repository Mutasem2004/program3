package io;

import model.Item;
import service.InventoryManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class InventoryCSV {

    private static final String FILE_NAME = "inventory.csv";

    // قراءة المخزون من CSV
    public static void loadItems(InventoryManager inventory) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            br.readLine(); // تخطي العنوان
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                int id = Integer.parseInt(parts[0]);
                String name = parts[1];
                String category = parts[2];
                int available = Integer.parseInt(parts[3]);
                int minimum = Integer.parseInt(parts[4]);

                inventory.addItem(new Item(id, name, category, 0, available, minimum));
            }
        }
    }

    // كتابة المخزون إلى CSV
    public static void saveItems(InventoryManager inventory) throws IOException {
        try (FileWriter fw = new FileWriter(FILE_NAME)) {
            fw.write("id,name,category,availableQuantity,minimumQuantity\n");
            for (Item item : inventory.getAllItems()) {
                fw.write(item.getId() + "," +
                        item.getName() + "," +
                        item.getCategory() + "," +
                        item.getAvailableQuantity() + "," +
                        item.getMinimumQuantity() + "\n");
            }
        }
    }
}

