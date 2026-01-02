package ui;

import service.InventoryManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    private InventoryManager inventory;

    public LoginFrame(InventoryManager inventory) {
        this.inventory = inventory;

        setTitle("نظام إدارة المخزون - تسجيل الدخول");
        setSize(350, 220);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // اسم المستخدم
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("اسم المستخدم:"), gbc);
        gbc.gridx = 1;
        usernameField = new JTextField(15);
        panel.add(usernameField, gbc);

        // كلمة المرور
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("كلمة المرور:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        panel.add(passwordField, gbc);

        // زر الدخول
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        loginButton = new JButton("دخول");
        panel.add(loginButton, gbc);

        add(panel);

        // حدث تسجيل الدخول
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword()).trim();

                if(username.equals("admin") && password.equals("123")) {
                    JOptionPane.showMessageDialog(null, "تم تسجيل الدخول كمدير");
                    openDashboard();
                } else if(username.equals("supervisor") && password.equals("123")) {
                    JOptionPane.showMessageDialog(null, "تم تسجيل الدخول كمشرف");
                    openDashboard(); // لاحقًا يمكن تقييد بعض الأزرار للمشرف
                } else {
                    JOptionPane.showMessageDialog(null, "اسم المستخدم أو كلمة المرور غير صحيحة");
                }
            }
        });

        setVisible(true);
    }

    private void openDashboard() {
        SwingUtilities.invokeLater(() -> {
            new ManagerFrame(inventory); // فتح واجهة المدير
            this.dispose(); // إغلاق نافذة تسجيل الدخول
        });
    }

    public static void main(String[] args) {
        // إنشاء InventoryManager واحد لتمريره
        InventoryManager inventory = new InventoryManager();
        SwingUtilities.invokeLater(() -> new LoginFrame(inventory));
    }
}
