package thread;

import model.ProductLine;
import model.Task;
import model.Task.TaskStatus;
import service.InventoryManager;
import ui.ManagerFrame;

public class ProductLineThread extends Thread {

    private final ProductLine productLine;
    private final InventoryManager inventory;
    private final ManagerFrame managerFrame;

    public ProductLineThread(ProductLine productLine,
                             InventoryManager inventory,
                             ManagerFrame managerFrame) {
        this.productLine = productLine;
        this.inventory = inventory;
        this.managerFrame = managerFrame;
    }

    @Override
    public void run() {

        productLine.setStatus(ProductLine.Status.ACTIVE);

        while (true) {
            boolean hasPending = false;

            for (Task task : productLine.getTasks()) {
                if (task.getStatus() != TaskStatus.PENDING) continue;

                hasPending = true;
                executeTask(task);
            }

            if (!hasPending) {
                try {
                    Thread.sleep(1000); // انتظار مهمة جديدة
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }

    private void executeTask(Task task) {

        synchronized (inventory) {
            try {
                if (!inventory.canProduce(task.getProduct(), task.getRequiredQuantity())) {
                    task.setStatus(TaskStatus.CANCELLED);
                    managerFrame.updateTaskProgress(task.getId(), task.getProgress(), task.getStatus());
                    return;
                }

                inventory.consumeItems(task.getProduct(), task.getRequiredQuantity());

                // تحديث المواد الخام بالواجهة
                task.getProduct().getRequiredItems().forEach((item, qty) ->
                        managerFrame.updateItemQuantity(item.getId(), item.getAvailableQuantity())
                );

            } catch (Exception e) {
                task.setStatus(TaskStatus.CANCELLED);
                managerFrame.updateTaskProgress(task.getId(), task.getProgress(), task.getStatus());
                return;
            }
        }

        // تنفيذ المهمة مع تحديث التقدم
        try {
            for (int i = 0; i < 10; i++) {
                Thread.sleep(500);
                task.updateProgress(10);
                managerFrame.updateTaskProgress(task.getId(), task.getProgress(), task.getStatus());
            }
        } catch (InterruptedException e) {
            return;
        }

        task.setStatus(TaskStatus.COMPLETED);
        managerFrame.updateTaskProgress(task.getId(), task.getProgress(), task.getStatus());
    }
}
