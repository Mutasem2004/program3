package ui;

import model.Item;
import model.Product;
import model.Task;
import service.InventoryManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ManagerFrame extends JFrame {

    private InventoryManager inventory;
    public final DefaultTableModel itemTableModel;
    public final DefaultTableModel taskTableModel;

    public ManagerFrame(InventoryManager inventory) {
        this.inventory = inventory;

        setTitle("واجهة المدير - نظام المخزون");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        // ===== المواد الخام =====
        JPanel itemsPanel = new JPanel(new BorderLayout());
        itemTableModel = new DefaultTableModel(
                new Object[]{"ID", "الاسم", "الفئة", "الكمية المتوفرة", "الحد الأدنى"}, 0
        );
        JTable itemTable = new JTable(itemTableModel);
        itemTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        itemsPanel.add(new JScrollPane(itemTable), BorderLayout.CENTER);

        // تعبئة الجدول بالمواد
        inventory.getAllItems().forEach(item ->
                itemTableModel.addRow(new Object[]{
                        item.getId(), item.getName(), item.getCategory(),
                        item.getAvailableQuantity(), item.getMinimumQuantity()
                })
        );

        // ===== أزرار المواد الخام =====
        JPanel itemButtonPanel = new JPanel();
        JButton updateQtyButton = new JButton("تحديث الكمية");
        JButton addItemButton = new JButton("إضافة مادة جديدة");
        JButton deleteItemButton = new JButton("حذف المادة");

        itemButtonPanel.add(updateQtyButton);
        itemButtonPanel.add(addItemButton);
        itemButtonPanel.add(deleteItemButton);
        itemsPanel.add(itemButtonPanel, BorderLayout.SOUTH);

        // ===== أحداث أزرار المواد الخام =====
        updateQtyButton.addActionListener(e -> {
            int selectedRow = itemTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "اختر مادة لتحديث الكمية");
                return;
            }
            String input = JOptionPane.showInputDialog("ادخل الكمية الجديدة:");
            if (input != null && !input.isEmpty()) {
                try {
                    int newQty = Integer.parseInt(input);
                    int itemId = (int) itemTableModel.getValueAt(selectedRow, 0);
                    Item item = inventory.getItem(itemId);
                    if (item != null) {
                        item.setAvailableQuantity(newQty); // تحديث المخزون
                        updateItemQuantity(itemId, item.getAvailableQuantity()); // تحديث الجدول
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "ادخل رقم صحيح");
                }
            }
        });

        addItemButton.addActionListener(e -> {
            JTextField idField = new JTextField();
            JTextField nameField = new JTextField();
            JTextField categoryField = new JTextField();
            JTextField qtyField = new JTextField();
            JTextField minField = new JTextField();
            Object[] message = {"ID:", idField, "الاسم:", nameField, "الفئة:", categoryField,
                    "الكمية المتوفرة:", qtyField, "الحد الأدنى:", minField};
            int option = JOptionPane.showConfirmDialog(null, message, "إضافة مادة جديدة", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                try {
                    int id = Integer.parseInt(idField.getText());
                    String name = nameField.getText();
                    String category = categoryField.getText();
                    int qty = Integer.parseInt(qtyField.getText());
                    int min = Integer.parseInt(minField.getText());
                    Item newItem = new Item(id, name, category, 0, qty, min);
                    inventory.addItem(newItem);
                    itemTableModel.addRow(new Object[]{id, name, category, qty, min});
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "ادخل قيم صحيحة");
                }
            }
        });

        deleteItemButton.addActionListener(e -> {
            int selectedRow = itemTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "اختر مادة للحذف");
                return;
            }
            int itemId = (int) itemTableModel.getValueAt(selectedRow, 0);
            Item item = inventory.getItem(itemId);
            if (item != null) {
                inventory.removeItem(item); // إزالة من المخزون
                itemTableModel.removeRow(selectedRow); // إزالة من الجدول
            }
        });

        tabbedPane.add("المواد الخام", itemsPanel);

        // ===== المهمات =====
        JPanel tasksPanel = new JPanel(new BorderLayout());
        taskTableModel = new DefaultTableModel(
                new Object[]{"ID", "المنتج", "الكمية", "الحالة", "التقدم (%)"}, 0
        );
        JTable taskTable = new JTable(taskTableModel);
        taskTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tasksPanel.add(new JScrollPane(taskTable), BorderLayout.CENTER);

        inventory.getAllTasks().forEach(task ->
                taskTableModel.addRow(new Object[]{
                        task.getId(), task.getProduct().getName(),
                        task.getRequiredQuantity(), task.getStatus(), task.getProgress()
                })
        );

        // ===== أزرار المهمات =====
        JPanel taskButtonPanel = new JPanel();
        JButton updateStatusButton = new JButton("تغيير حالة المهمة");
        JButton deleteTaskButton = new JButton("حذف المهمة");

        taskButtonPanel.add(updateStatusButton);
        taskButtonPanel.add(deleteTaskButton);
        tasksPanel.add(taskButtonPanel, BorderLayout.SOUTH);

        // ===== أحداث أزرار المهمات =====
        updateStatusButton.addActionListener(e -> {
            int selectedRow = taskTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "اختر مهمة لتغيير الحالة");
                return;
            }
            Object[] options = {"PENDING", "COMPLETED", "CANCELLED"};
            String status = (String) JOptionPane.showInputDialog(null, "اختر الحالة الجديدة:", "تغيير الحالة",
                    JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            if (status != null) {
                int taskId = (int) taskTableModel.getValueAt(selectedRow, 0);
                Task task = inventory.getTask(taskId);
                if (task != null) {
                    task.setStatus(Task.TaskStatus.valueOf(status));
                    taskTableModel.setValueAt(task.getStatus(), selectedRow, 3);
                }
            }
        });

        deleteTaskButton.addActionListener(e -> {
            int selectedRow = taskTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "اختر مهمة للحذف");
                return;
            }
            int taskId = (int) taskTableModel.getValueAt(selectedRow, 0);
            Task task = inventory.getTask(taskId);
            if (task != null) {
                inventory.getAllTasks().remove(task);
                taskTableModel.removeRow(selectedRow);
            }
        });

        tabbedPane.add("المهمات", tasksPanel);

        add(tabbedPane);
        setVisible(true);
    }

    // ===== دوال لتحديث الجدول بشكل آمن من Threads =====
    public void updateTaskProgress(int taskId, int progress, Task.TaskStatus status) {
        SwingUtilities.invokeLater(() -> {
            for (int row = 0; row < taskTableModel.getRowCount(); row++) {
                if ((int) taskTableModel.getValueAt(row, 0) == taskId) {
                    taskTableModel.setValueAt(progress, row, 4);
                    taskTableModel.setValueAt(status, row, 3);
                    break;
                }
            }
        });
    }

    public void updateItemQuantity(int itemId, int quantity) {
        SwingUtilities.invokeLater(() -> {
            for (int row = 0; row < itemTableModel.getRowCount(); row++) {
                if ((int) itemTableModel.getValueAt(row, 0) == itemId) {
                    itemTableModel.setValueAt(quantity, row, 3);
                    break;
                }
            }
        });
    }
}
