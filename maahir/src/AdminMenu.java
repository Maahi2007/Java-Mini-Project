import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class AdminMenu {

    public static void showMenu(JFrame parent) {

        JFrame frame = new JFrame("Manage Menu");
        frame.setSize(800, 550);
        frame.setLocationRelativeTo(parent);
        frame.setLayout(new BorderLayout());

        String[] columns = {
                "ID", "Item Name", "Category", "Price", "Available"
        };

        DefaultTableModel model =
                new DefaultTableModel(columns, 0);

        JTable table = new JTable(model);

        loadMenu(model);

        frame.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel panel = new JPanel();

        JButton addButton = new JButton("ADD");
        JButton editButton = new JButton("EDIT");
        JButton deleteButton = new JButton("DELETE");

        panel.add(addButton);
        panel.add(editButton);
        panel.add(deleteButton);

        frame.add(panel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> {

            JTextField nameField = new JTextField();
            JTextField categoryField = new JTextField();
            JTextField priceField = new JTextField();

            Object[] fields = {
                    "Item Name:", nameField,
                    "Category:", categoryField,
                    "Price:", priceField
            };

            int result = JOptionPane.showConfirmDialog(
                    frame,
                    fields,
                    "Add Menu Item",
                    JOptionPane.OK_CANCEL_OPTION
            );

            if (result == JOptionPane.OK_OPTION) {

                try {

                    double price =
                            Double.parseDouble(priceField.getText());

                    String sql =
                            "INSERT INTO menu " +
                            "(item_name, category, price, availability) " +
                            "VALUES (?, ?, ?, true)";

                    try (Connection con = DBConnection.getConnection();
                         PreparedStatement pst =
                                 con.prepareStatement(sql)) {

                        pst.setString(1, nameField.getText());
                        pst.setString(2, categoryField.getText());
                        pst.setDouble(3, price);

                        pst.executeUpdate();
                    }

                    model.setRowCount(0);
                    loadMenu(model);

                    JOptionPane.showMessageDialog(
                            frame,
                            "Menu item added successfully!"
                    );

                } catch (Exception ex) {

                    JOptionPane.showMessageDialog(
                            frame,
                            "Error: " + ex.getMessage()
                    );
                }
            }
        });

        editButton.addActionListener(e -> {

            int row = table.getSelectedRow();

            if (row == -1) {

                JOptionPane.showMessageDialog(
                        frame,
                        "Please select an item."
                );

                return;
            }

            int id = (int) model.getValueAt(row, 0);

            String oldName =
                    model.getValueAt(row, 1).toString();

            String oldCategory =
                    model.getValueAt(row, 2).toString();

            String oldPrice =
                    model.getValueAt(row, 3).toString();

            JTextField nameField =
                    new JTextField(oldName);

            JTextField categoryField =
                    new JTextField(oldCategory);

            JTextField priceField =
                    new JTextField(oldPrice);

            Object[] fields = {
                    "Item Name:", nameField,
                    "Category:", categoryField,
                    "Price:", priceField
            };

            int result = JOptionPane.showConfirmDialog(
                    frame,
                    fields,
                    "Edit Menu Item",
                    JOptionPane.OK_CANCEL_OPTION
            );

            if (result == JOptionPane.OK_OPTION) {

                try {

                    double price =
                            Double.parseDouble(priceField.getText());

                    String sql =
                            "UPDATE menu SET item_name=?, " +
                            "category=?, price=? WHERE id=?";

                    try (Connection con = DBConnection.getConnection();
                         PreparedStatement pst =
                                 con.prepareStatement(sql)) {

                        pst.setString(1, nameField.getText());
                        pst.setString(2, categoryField.getText());
                        pst.setDouble(3, price);
                        pst.setInt(4, id);

                        pst.executeUpdate();
                    }

                    model.setRowCount(0);
                    loadMenu(model);

                    JOptionPane.showMessageDialog(
                            frame,
                            "Menu item updated successfully!"
                    );

                } catch (Exception ex) {

                    JOptionPane.showMessageDialog(
                            frame,
                            "Error: " + ex.getMessage()
                    );
                }
            }
        });

        deleteButton.addActionListener(e -> {

            int row = table.getSelectedRow();

            if (row == -1) {

                JOptionPane.showMessageDialog(
                        frame,
                        "Please select an item."
                );

                return;
            }

            int id = (int) model.getValueAt(row, 0);

            int confirm = JOptionPane.showConfirmDialog(
                    frame,
                    "Delete this menu item?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {

                try {

                    String sql =
                            "DELETE FROM menu WHERE id=?";

                    try (Connection con = DBConnection.getConnection();
                         PreparedStatement pst =
                                 con.prepareStatement(sql)) {

                        pst.setInt(1, id);
                        pst.executeUpdate();
                    }

                    model.setRowCount(0);
                    loadMenu(model);

                    JOptionPane.showMessageDialog(
                            frame,
                            "Menu item deleted successfully!"
                    );

                } catch (Exception ex) {

                    JOptionPane.showMessageDialog(
                            frame,
                            "Delete Error: " + ex.getMessage()
                    );
                }
            }
        });

        frame.setVisible(true);
    }

    private static void loadMenu(DefaultTableModel model) {

        String sql = "SELECT * FROM menu ORDER BY id";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst =
                     con.prepareStatement(sql);
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
                    null,
                    "Database Error: " + ex.getMessage()
            );
        }
    }
}
