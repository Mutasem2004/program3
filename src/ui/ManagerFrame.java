package ui;

import model.*;
import service.InventoryManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;

public class ManagerFrame extends JFrame {

    private InventoryManager inventory;
    public final DefaultTableModel itemTableModel;
    public final DefaultTableModel taskTableModel;

    public ManagerFrame(InventoryManager inventory) {
        this.inventory = inventory;

        setTitle("واجهة المدير - نظام المخزون");
        setSize(950, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        /* ================= المواد الخام ================= */
        JPanel itemsPanel = new JPanel(new BorderLayout());
        itemTableModel = new DefaultTableModel(
                new Object[]{"ID","الاسم","الفئة","الكمية المتوفرة","الحد الأدنى"},0
        );
        JTable itemTable = new JTable(itemTableModel);
        itemsPanel.add(new JScrollPane(itemTable), BorderLayout.CENTER);

        inventory.getAllItems().forEach(item ->
                itemTableModel.addRow(new Object[]{
                        item.getId(), item.getName(), item.getCategory(),
                        item.getAvailableQuantity(), item.getMinimumQuantity()
                })
        );

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
            int row = itemTable.getSelectedRow();
            if(row == -1) return;
            String input = JOptionPane.showInputDialog("ادخل الكمية الجديدة:");
            try{
                int newQty = Integer.parseInt(input);
                int itemId = (int)itemTableModel.getValueAt(row,0);
                inventory.updateItemQuantity(itemId,newQty);
                updateItemQuantity(itemId,newQty);
            } catch(Exception ex){ JOptionPane.showMessageDialog(this,"قيمة غير صحيحة"); }
        });

        addItemButton.addActionListener(e -> {
            JTextField idField = new JTextField();
            JTextField nameField = new JTextField();
            JTextField catField = new JTextField();
            JTextField qtyField = new JTextField();
            JTextField minField = new JTextField();
            Object[] message = {"ID:",idField,"الاسم:",nameField,"الفئة:",catField,
                    "الكمية:",qtyField,"الحد الأدنى:",minField};
            if(JOptionPane.showConfirmDialog(this,message,"إضافة مادة",JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION){
                try{
                    Item item = new Item(Integer.parseInt(idField.getText()),nameField.getText(),
                            catField.getText(),0,Integer.parseInt(qtyField.getText()),Integer.parseInt(minField.getText()));
                    inventory.addItem(item);
                    itemTableModel.addRow(new Object[]{
                            item.getId(),item.getName(),item.getCategory(),
                            item.getAvailableQuantity(),item.getMinimumQuantity()
                    });
                } catch(Exception ex){ JOptionPane.showMessageDialog(this,"بيانات غير صحيحة"); }
            }
        });

        deleteItemButton.addActionListener(e -> {
            int row = itemTable.getSelectedRow();
            if(row==-1) return;
            int itemId = (int)itemTableModel.getValueAt(row,0);
            Item item = inventory.getItem(itemId);
            if(item!=null){
                inventory.removeItem(item);
                itemTableModel.removeRow(row);
            }
        });

        tabbedPane.add("المواد الخام",itemsPanel);

        /* ================= المهمات ================= */
        JPanel tasksPanel = new JPanel(new BorderLayout());
        taskTableModel = new DefaultTableModel(
                new Object[]{"ID","المنتج","الكمية","الحالة","التقدم (%)"},0
        );
        JTable taskTable = new JTable(taskTableModel);
        tasksPanel.add(new JScrollPane(taskTable),BorderLayout.CENTER);

        inventory.getAllTasks().forEach(task ->
                taskTableModel.addRow(new Object[]{
                        task.getId(),task.getProduct().getName(),task.getRequiredQuantity(),
                        task.getStatus(),task.getProgress()
                })
        );

        JPanel taskButtonPanel = new JPanel();
        JButton addTaskButton = new JButton("إضافة مهمة جديدة");
        JButton updateStatusButton = new JButton("تغيير حالة المهمة");
        JButton deleteTaskButton = new JButton("حذف المهمة");
        JButton startLineButton = new JButton("تشغيل خط الإنتاج");
        JButton stopLineButton = new JButton("إيقاف خط الإنتاج");

        taskButtonPanel.add(addTaskButton);
        taskButtonPanel.add(updateStatusButton);
        taskButtonPanel.add(deleteTaskButton);
        taskButtonPanel.add(startLineButton);
        taskButtonPanel.add(stopLineButton);
        tasksPanel.add(taskButtonPanel,BorderLayout.SOUTH);

        // ===== أحداث أزرار المهمات =====
        // إضافة مهمة جديدة
        addTaskButton.addActionListener(e -> {
            JTextField idField = new JTextField();
            JComboBox<Product> productBox = new JComboBox<>(inventory.getAllProducts().toArray(new Product[0]));
            JTextField qtyField = new JTextField();
            JComboBox<ProductLine> lineBox = new JComboBox<>(inventory.getAllProductLines().toArray(new ProductLine[0]));

            Object[] msg = {"ID المهمة:",idField,"المنتج:",productBox,"الكمية:",qtyField,"خط الإنتاج:",lineBox};
            if(JOptionPane.showConfirmDialog(this,msg,"إضافة مهمة",JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION){
                try{
                    Task task = new Task(Integer.parseInt(idField.getText()),
                            (Product)productBox.getSelectedItem(),
                            Integer.parseInt(qtyField.getText()),
                            "زبون افتراضي",LocalDate.now(),LocalDate.now().plusDays(2));
                    inventory.addTask(task);
                    taskTableModel.addRow(new Object[]{
                            task.getId(),task.getProduct().getName(),task.getRequiredQuantity(),
                            task.getStatus(),task.getProgress()
                    });
                    ProductLine line = (ProductLine)lineBox.getSelectedItem();
                    if(line!=null) line.addTask(task);
                }catch(Exception ex){ JOptionPane.showMessageDialog(this,"خطأ في إدخال البيانات"); }
            }
        });

        // تغيير حالة المهمة
        updateStatusButton.addActionListener(e -> {
            int row = taskTable.getSelectedRow();
            if(row==-1) return;
            int taskId = (int)taskTableModel.getValueAt(row,0);
            Task task = inventory.getTask(taskId);
            Object[] options = {"PENDING","COMPLETED","CANCELLED"};
            String status = (String)JOptionPane.showInputDialog(this,"اختر الحالة الجديدة:","تغيير الحالة",
                    JOptionPane.PLAIN_MESSAGE,null,options,options[0]);
            if(status!=null && task!=null){
                task.setStatus(Task.TaskStatus.valueOf(status));
                taskTableModel.setValueAt(task.getStatus(),row,3);
            }
        });

        // حذف مهمة
        deleteTaskButton.addActionListener(e -> {
            int row = taskTable.getSelectedRow();
            if(row==-1) return;
            int taskId = (int)taskTableModel.getValueAt(row,0);
            Task task = inventory.getTask(taskId);
            if(task!=null){
                inventory.removeTask(task);
                taskTableModel.removeRow(row);
            }
        });

        // تشغيل/إيقاف خطوط الإنتاج
        startLineButton.addActionListener(e -> {
            ProductLine line = (ProductLine)JOptionPane.showInputDialog(this,"اختر خط الإنتاج لتشغيله",
                    "تشغيل خط الإنتاج",JOptionPane.PLAIN_MESSAGE,null,inventory.getAllProductLines().toArray(),null);
            if(line!=null){
                line.setStatus(ProductLine.Status.ACTIVE);
                JOptionPane.showMessageDialog(this,"تم تشغيل خط الإنتاج: "+line.getName());
            }
        });

        stopLineButton.addActionListener(e -> {
            ProductLine line = (ProductLine)JOptionPane.showInputDialog(this,"اختر خط الإنتاج لإيقافه",
                    "إيقاف خط الإنتاج",JOptionPane.PLAIN_MESSAGE,null,inventory.getAllProductLines().toArray(),null);
            if(line!=null){
                line.setStatus(ProductLine.Status.STOPPED);
                JOptionPane.showMessageDialog(this,"تم إيقاف خط الإنتاج: "+line.getName());
            }
        });

        tabbedPane.add("المهمات",tasksPanel);
        add(tabbedPane);
        setVisible(true);
    }

    // ===== دوال تحديث الجدول من Threads =====
    public void updateTaskProgress(int taskId,int progress,Task.TaskStatus status){
        SwingUtilities.invokeLater(() -> {
            for(int r=0;r<taskTableModel.getRowCount();r++){
                if((int)taskTableModel.getValueAt(r,0)==taskId){
                    taskTableModel.setValueAt(progress,r,4);
                    taskTableModel.setValueAt(status,r,3);
                }
            }
        });
    }

    public void updateItemQuantity(int itemId,int quantity){
        SwingUtilities.invokeLater(() -> {
            for(int r=0;r<itemTableModel.getRowCount();r++){
                if((int)itemTableModel.getValueAt(r,0)==itemId){
                    itemTableModel.setValueAt(quantity,r,3);
                }
            }
        });
    }
}
