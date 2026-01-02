package main;

import service.InventoryManager;
import ui.LoginFrame;

public class Main {
    public static void main(String[] args) {

        // ===== إنشاء المخزون وتحميل البيانات من الملفات =====
        InventoryManager inventory = new InventoryManager();

        // ===== تمرير InventoryManager إلى LoginFrame =====
        javax.swing.SwingUtilities.invokeLater(() -> new LoginFrame(inventory));
    }
}
