import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class MyOrders {

    public static void showOrders(JFrame parent, int userId) {

        JFrame frame = new JFrame("My Orders");
        frame.setSize(700, 500);
        frame.setLocationRelativeTo(parent);

        String[] columns = {
                "Order ID",
                "Item",
                "Quantity",
                "Price",
                "Total",
                "Status",
                "Date"
        };

        DefaultTableModel model =
                new DefaultTableModel(columns, 0);

        JTable table = new JTable(model);

        String sql =
                "SELECT o.order_id, m.item_name, oi.quantity, " +
                "oi.price, o.total_amount, o.status, o.order_date " +
                "FROM orders o " +
                "JOIN order_items oi ON o.order_id = oi.order_id " +
                "JOIN menu m ON oi.menu_id = m.id " +
                "WHERE o.user_id = ? " +
                "ORDER BY o.order_date DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, userId);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {

                model.addRow(new Object[]{
                        rs.getInt("order_id"),
                        rs.getString("item_name"),
                        rs.getInt("quantity"),
                        "₹" + rs.getDouble("price"),
                        "₹" + rs.getDouble("total_amount"),
                        rs.getString("status"),
                        rs.getTimestamp("order_date")
                });
            }

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                    frame,
                    "Database Error: " + ex.getMessage()
            );
        }

        frame.add(new JScrollPane(table));
        frame.setVisible(true);
    }
}