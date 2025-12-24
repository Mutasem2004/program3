package thread;

import model.ProductLine;
import model.Task;
import model.Task.TaskStatus;
import service.InventoryManager;
import ui.ManagerFrame;

import javax.swing.*;

public class ProductLineThread extends Thread {

    private ProductLine productLine;
    private InventoryManager inventory;
    private ManagerFrame managerFrame;

    public ProductLineThread(ProductLine productLine, InventoryManager inventory, ManagerFrame managerFrame) {
        this.productLine = productLine;
        this.inventory = inventory;
        this.managerFrame = managerFrame;
    }

    @Override
    public void run() {
        productLine.setStatus(ProductLine.Status.ACTIVE);

        for (Task task : productLine.getTasks()) {
            if (task.getStatus() != TaskStatus.PENDING) continue;

            synchronized (inventory) {
                try {
                    if (!inventory.canProduce(task.getProduct(), task.getRequiredQuantity())) {
                        task.setStatus(TaskStatus.CANCELLED);
                        managerFrame.updateTaskProgress(task.getId(), task.getProgress(), task.getStatus());
                        showAlert("المهمة " + task.getId() + " تم إلغاؤها: مواد غير كافية");
                        continue;
                    }
                    inventory.consumeItems(task.getProduct(), task.getRequiredQuantity());

                    // تحديث جدول المواد الخام
                    task.getProduct().getRequiredItems().forEach((item, qty) -> {
                        managerFrame.updateItemQuantity(item.getId(), item.getAvailableQuantity());
                    });

                } catch (Exception e) {
                    task.setStatus(TaskStatus.CANCELLED);
                    managerFrame.updateTaskProgress(task.getId(), task.getProgress(), task.getStatus());
                    showAlert("المهمة " + task.getId() + " تم إلغاؤها: " + e.getMessage());
                    continue;
                }
            }

            // تنفيذ المهمة وتحديث التقدم
            try {
                for (int i = 0; i < 10; i++) {
                    Thread.sleep(500);
                    task.updateProgress(10);
                    managerFrame.updateTaskProgress(task.getId(), task.getProgress(), task.getStatus());
                }
            } catch (InterruptedException e) { e.printStackTrace(); }

            task.setStatus(TaskStatus.COMPLETED);
            managerFrame.updateTaskProgress(task.getId(), task.getProgress(), task.getStatus());
            showAlert("✅ انتهت المهمة " + task.getId());
        }

        productLine.setStatus(ProductLine.Status.STOPPED);
    }

    // ===== دالة عرض التنبيه الصوتي والرسالة =====
    private void showAlert(String message) {
        // رسالة منبثقة
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, message));

        // تنبيه صوتي
        java.awt.Toolkit.getDefaultToolkit().beep();
    }
}
