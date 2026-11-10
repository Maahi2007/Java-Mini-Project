import javax.swing.*;
import java.awt.*;

public class AdminDashboard {

    public static void showAdminDashboard(JFrame parent, int userId) {

        JFrame frame = new JFrame("Admin Dashboard");
        frame.setSize(700, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(parent);
        frame.setLayout(null);

        JLabel title = new JLabel("ADMIN DASHBOARD");
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setBounds(230, 40, 300, 40);
        frame.add(title);

        JButton ordersButton = new JButton("MANAGE ORDERS");
        ordersButton.setBounds(100, 140, 200, 60);
        frame.add(ordersButton);

        JButton menuButton = new JButton("MANAGE MENU");
        menuButton.setBounds(350, 140, 200, 60);
        frame.add(menuButton);

        JButton logoutButton = new JButton("LOGOUT");
        logoutButton.setBounds(225, 250, 200, 60);
        frame.add(logoutButton);

        ordersButton.addActionListener(e ->
                JOptionPane.showMessageDialog(
                        frame,
                        "Manage Orders feature coming next!"
                )
        );

        menuButton.addActionListener(e ->
        AdminMenu.showMenu(frame)
);

        logoutButton.addActionListener(e -> {
            frame.dispose();
            parent.setVisible(true);
        });

        frame.setVisible(true);
    }
}
