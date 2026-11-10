import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class OrderForm {

    public static void showOrderForm(JFrame parent, int userId) {

        JFrame frame = new JFrame("Place Order");
        frame.setSize(600, 500);
        frame.setLocationRelativeTo(parent);
        frame.setLayout(null);

        JLabel title = new JLabel("PLACE YOUR ORDER");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setBounds(190, 30, 300, 40);
        frame.add(title);

        JLabel itemLabel = new JLabel("Select Item:");
        itemLabel.setBounds(70, 100, 100, 30);
        frame.add(itemLabel);

        JComboBox<String> itemBox = new JComboBox<>();
        itemBox.setBounds(180, 100, 300, 30);
        frame.add(itemBox);

        JLabel quantityLabel = new JLabel("Quantity:");
        quantityLabel.setBounds(70, 160, 100, 30);
        frame.add(quantityLabel);

        JSpinner quantity = new JSpinner(
                new SpinnerNumberModel(1, 1, 20, 1)
        );
        quantity.setBounds(180, 160, 100, 30);
        frame.add(quantity);

        JLabel priceLabel = new JLabel("Price: ₹0");
        priceLabel.setBounds(180, 220, 200, 30);
        frame.add(priceLabel);

        JButton orderButton = new JButton("PLACE ORDER");
        orderButton.setBounds(180, 290, 200, 45);
        frame.add(orderButton);

        final int[] menuId = {0};
        final double[] price = {0};

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst =
                     con.prepareStatement(
                             "SELECT id, item_name, price FROM menu WHERE availability=true"
                     );
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {

                int id = rs.getInt("id");
                String name = rs.getString("item_name");
                double itemPrice = rs.getDouble("price");

                itemBox.addItem(id + " - " + name + " - ₹" + itemPrice);
            }

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                    frame,
                    "Database Error: " + ex.getMessage()
            );
        }

        itemBox.addActionListener(e -> {

            String selected = (String) itemBox.getSelectedItem();

            if (selected != null) {

                try {

                    String idText = selected.split(" - ")[0];
                    menuId[0] = Integer.parseInt(idText);

                    String sql =
                            "SELECT price FROM menu WHERE id=?";

                    try (Connection con = DBConnection.getConnection();
                         PreparedStatement pst =
                                 con.prepareStatement(sql)) {

                        pst.setInt(1, menuId[0]);

                        ResultSet rs = pst.executeQuery();

                        if (rs.next()) {

                            price[0] = rs.getDouble("price");

                            int qty =
                                    (int) quantity.getValue();

                            priceLabel.setText(
                                    "Total: ₹" + (price[0] * qty)
                            );
                        }
                    }

                } catch (Exception ex) {

                    JOptionPane.showMessageDialog(
                            frame,
                            "Error: " + ex.getMessage()
                    );
                }
            }
        });

        quantity.addChangeListener(e -> {

            int qty = (int) quantity.getValue();

            priceLabel.setText(
                    "Total: ₹" + (price[0] * qty)
            );
        });

        orderButton.addActionListener(e -> {

            try {

                int qty = (int) quantity.getValue();

                double total = price[0] * qty;

                String orderSQL =
                        "INSERT INTO orders (user_id, total_amount, status) VALUES (?, ?, ?)";

                try (Connection con = DBConnection.getConnection();
                     PreparedStatement orderPst =
                             con.prepareStatement(
                                     orderSQL,
                                     Statement.RETURN_GENERATED_KEYS
                             )) {

                    orderPst.setInt(1, userId);
                    orderPst.setDouble(2, total);
                    orderPst.setString(3, "Pending");

                    orderPst.executeUpdate();

                    ResultSet keys =
                            orderPst.getGeneratedKeys();

                    if (keys.next()) {

                        int orderId = keys.getInt(1);

                        String itemSQL =
                                "INSERT INTO order_items (order_id, menu_id, quantity, price) VALUES (?, ?, ?, ?)";

                        try (PreparedStatement itemPst =
                                     con.prepareStatement(itemSQL)) {

                            itemPst.setInt(1, orderId);
                            itemPst.setInt(2, menuId[0]);
                            itemPst.setInt(3, qty);
                            itemPst.setDouble(4, price[0]);

                            itemPst.executeUpdate();
                        }

                        JOptionPane.showMessageDialog(
                                frame,
                                "Order Placed Successfully!\n"
                                + "Order ID: " + orderId
                                + "\nTotal: ₹" + total
                        );

                        frame.dispose();
                    }
                }

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(
                        frame,
                        "Order Error: " + ex.getMessage()
                );
            }
        });

        frame.setVisible(true);
    }
}
