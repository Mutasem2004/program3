package main;

import ui.ManagerFrame;
import model.Item;
import model.Product;
import model.Task;
import model.ProductLine;
import service.InventoryManager;
import thread.ProductLineThread;

import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {

        // ===== إنشاء المخزون =====
        InventoryManager inventory = new InventoryManager();

        Item flour = new Item(1, "طحين", "أغذية", 3.5, 200, 20);
        Item sugar = new Item(2, "سكر", "أغذية", 4.0, 150, 10);
        Item cocoa = new Item(3, "كاكاو", "أغذية", 5.0, 100, 5);
        Item milk = new Item(4, "حليب", "أغذية", 2.5, 120, 10);
        Item butter = new Item(5, "زبدة", "أغذية", 6.0, 80, 5);

        inventory.addItem(flour);
        inventory.addItem(sugar);
        inventory.addItem(cocoa);
        inventory.addItem(milk);
        inventory.addItem(butter);

        // ===== إنشاء المنتجات =====
        Product cake = new Product(1, "كيك");
        cake.addRequiredItem(flour, 2);
        cake.addRequiredItem(sugar, 1);

        Product chocolateCake = new Product(2, "كيك شوكولاتة");
        chocolateCake.addRequiredItem(flour, 2);
        chocolateCake.addRequiredItem(sugar, 1);
        chocolateCake.addRequiredItem(cocoa, 1);

        Product milkCake = new Product(3, "كيك بالحليب");
        milkCake.addRequiredItem(flour, 2);
        milkCake.addRequiredItem(sugar, 1);
        milkCake.addRequiredItem(milk, 1);

        Product butterCake = new Product(4, "كيك بالزبدة");
        butterCake.addRequiredItem(flour, 2);
        butterCake.addRequiredItem(sugar, 1);
        butterCake.addRequiredItem(butter, 1);

        inventory.addProduct(cake);
        inventory.addProduct(chocolateCake);
        inventory.addProduct(milkCake);
        inventory.addProduct(butterCake);

        // ===== إنشاء المهمات =====
        Task t1 = new Task(1, cake, 30, "زبون A", LocalDate.now(), LocalDate.now().plusDays(2));
        Task t2 = new Task(2, chocolateCake, 20, "زبون B", LocalDate.now(), LocalDate.now().plusDays(3));
        Task t3 = new Task(3, milkCake, 25, "زبون C", LocalDate.now(), LocalDate.now().plusDays(2));
        Task t4 = new Task(4, cake, 15, "زبون D", LocalDate.now(), LocalDate.now().plusDays(1));
        Task t5 = new Task(5, butterCake, 18, "زبون E", LocalDate.now(), LocalDate.now().plusDays(2));
        Task t6 = new Task(6, chocolateCake, 22, "زبون F", LocalDate.now(), LocalDate.now().plusDays(3));

        inventory.addTask(t1);
        inventory.addTask(t2);
        inventory.addTask(t3);
        inventory.addTask(t4);
        inventory.addTask(t5);
        inventory.addTask(t6);

        // ===== فتح واجهة المدير =====
        ManagerFrame managerFrame = new ManagerFrame(inventory);

        // ===== إنشاء خطوط الإنتاج =====
        ProductLine line1 = new ProductLine(1, "خط الإنتاج 1");
        line1.addTask(t1);
        line1.addTask(t2);

        ProductLine line2 = new ProductLine(2, "خط الإنتاج 2");
        line2.addTask(t3);
        line2.addTask(t4);

        ProductLine line3 = new ProductLine(3, "خط الإنتاج 3");
        line3.addTask(t5);
        line3.addTask(t6);

        // ===== تشغيل خطوط الإنتاج Threads مع التنبيهات =====
        ProductLineThread thread1 = new ProductLineThread(line1, inventory, managerFrame);
        ProductLineThread thread2 = new ProductLineThread(line2, inventory, managerFrame);
        ProductLineThread thread3 = new ProductLineThread(line3, inventory, managerFrame);

        thread1.start();
        thread2.start();
        thread3.start();
    }
}
