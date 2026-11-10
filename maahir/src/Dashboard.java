import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class Dashboard {

    public static void showDashboard(String role, int userId) {

        JFrame frame = new JFrame("Canteen Order Management - Dashboard");

        frame.setSize(700, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(null);

        JLabel title = new JLabel("CANTEEN DASHBOARD");
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setBounds(210, 40, 350, 40);
        frame.add(title);

        JLabel welcome = new JLabel("Welcome! Role: " + role);
        welcome.setFont(new Font("Arial", Font.PLAIN, 18));
        welcome.setBounds(250, 90, 250, 30);
        frame.add(welcome);

        JButton menuButton = new JButton("VIEW MENU");
        menuButton.setBounds(100, 160, 200, 60);
        frame.add(menuButton);

        JButton orderButton = new JButton("PLACE ORDER");
        orderButton.setBounds(350, 160, 200, 60);
        frame.add(orderButton);

        JButton ordersButton = new JButton("MY ORDERS");
        ordersButton.setBounds(100, 250, 200, 60);
        frame.add(ordersButton);

        JButton logoutButton = new JButton("LOGOUT");
        logoutButton.setBounds(350, 250, 200, 60);
        frame.add(logoutButton);

        // VIEW MENU
        menuButton.addActionListener(e -> showMenu(frame));

        // PLACE ORDER
        orderButton.addActionListener(e ->
                OrderForm.showOrderForm(frame, userId)
        );

        // MY ORDERS
        ordersButton.addActionListener(e ->
        MyOrders.showOrders(frame, userId)
);

        // LOGOUT
        logoutButton.addActionListener(e -> {
            frame.dispose();
        });

        frame.setVisible(true);
    }

    private static void showMenu(JFrame parent) {

        JFrame menuFrame = new JFrame("Canteen Menu");

        menuFrame.setSize(600, 450);
        menuFrame.setLocationRelativeTo(parent);

        String[] columns = {
                "ID",
                "Item Name",
                "Category",
                "Price",
                "Available"
        };

        DefaultTableModel model =
                new DefaultTableModel(columns, 0);

        JTable table = new JTable(model);

        String sql = "SELECT * FROM menu";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {

                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("item_name"),
                        rs.getString("category"),
                        rs.getDouble("price"),
                        rs.getBoolean("availability")
                                ? "Yes"
                                : "No"
                });
            }

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                    menuFrame,
                    "Database Error: " + ex.getMessage()
            );
        }

        menuFrame.add(new JScrollPane(table));
        menuFrame.setVisible(true);
    }
}