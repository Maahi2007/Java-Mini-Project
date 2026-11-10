import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class AdminOrders {

    public static void showOrders(JFrame parent) {

        JFrame frame = new JFrame("Manage Orders");
        frame.setSize(850, 550);
        frame.setLocationRelativeTo(parent);
        frame.setLayout(new BorderLayout());

        String[] columns = {
                "Order ID",
                "Customer",
                "Item",
                "Quantity",
                "Total",
                "Status",
                "Date"
        };

        DefaultTableModel model =
                new DefaultTableModel(columns, 0);

        JTable table = new JTable(model);

        String sql =
                "SELECT o.order_id, u.username, m.item_name, " +
                "oi.quantity, o.total_amount, o.status, o.order_date " +
                "FROM orders o " +
                "JOIN users u ON o.user_id = u.id " +
                "JOIN order_items oi ON o.order_id = oi.order_id " +
                "JOIN menu m ON oi.menu_id = m.id " +
                "ORDER BY o.order_date DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {

                model.addRow(new Object[]{
                        rs.getInt("order_id"),
                        rs.getString("username"),
                        rs.getString("item_name"),
                        rs.getInt("quantity"),
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

        frame.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton updateButton =
                new JButton("UPDATE STATUS");

        frame.add(updateButton, BorderLayout.SOUTH);

        updateButton.addActionListener(e -> {

            int row = table.getSelectedRow();

            if (row == -1) {

                JOptionPane.showMessageDialog(
                        frame,
                        "Please select an order."
                );

                return;
            }

            int orderId =
                    (int) model.getValueAt(row, 0);

            String[] statuses = {
                    "Pending",
                    "Preparing",
                    "Ready",
                    "Completed",
                    "Cancelled"
            };

            String status = (String) JOptionPane.showInputDialog(
                    frame,
                    "Select Order Status:",
                    "Update Status",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    statuses,
                    statuses[0]
            );

            if (status == null) {
                return;
            }

            String updateSQL =
                    "UPDATE orders SET status=? WHERE order_id=?";

            try (Connection con = DBConnection.getConnection();
                 PreparedStatement pst =
                         con.prepareStatement(updateSQL)) {

                pst.setString(1, status);
                pst.setInt(2, orderId);

                pst.executeUpdate();

                model.setValueAt(status, row, 5);

                JOptionPane.showMessageDialog(
                        frame,
                        "Order status updated successfully!"
                );

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(
                        frame,
                        "Update Error: " + ex.getMessage()
                );
            }
        });

        frame.setVisible(true);
    }
}
