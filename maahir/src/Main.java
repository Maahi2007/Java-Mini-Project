import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            JFrame frame = new JFrame("Canteen Order Management");

            frame.setSize(500, 400);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setLayout(null);

            JLabel title = new JLabel("CANTEEN ORDER MANAGEMENT");
            title.setFont(new Font("Arial", Font.BOLD, 22));
            title.setBounds(90, 40, 350, 40);
            frame.add(title);

            JLabel userLabel = new JLabel("Username");
            userLabel.setBounds(90, 120, 100, 30);
            frame.add(userLabel);

            JTextField username = new JTextField();
            username.setBounds(190, 120, 200, 30);
            frame.add(username);

            JLabel passLabel = new JLabel("Password");
            passLabel.setBounds(90, 170, 100, 30);
            frame.add(passLabel);

            JPasswordField password = new JPasswordField();
            password.setBounds(190, 170, 200, 30);
            frame.add(password);

            JButton loginButton = new JButton("LOGIN");
            loginButton.setBounds(180, 230, 140, 40);
            frame.add(loginButton);

            loginButton.addActionListener(e -> {

                String user = username.getText();
                String pass = new String(password.getPassword());

                String sql =
                        "SELECT * FROM users WHERE username=? AND password=?";

                try (Connection con = DBConnection.getConnection();
                     PreparedStatement pst = con.prepareStatement(sql)) {

                    pst.setString(1, user);
                    pst.setString(2, pass);

                    ResultSet rs = pst.executeQuery();

                    if (rs.next()) {

                        String role = rs.getString("role");
                        int userId = rs.getInt("id");


                        JOptionPane.showMessageDialog(
                                frame,
                                "Login Successful!"
                        );

                        frame.dispose();

if (role.equalsIgnoreCase("admin")) {

    AdminDashboard.showAdminDashboard(frame, userId);

} else {

    Dashboard.showDashboard(role, userId);
}

                    } else {

                        JOptionPane.showMessageDialog(
                                frame,
                                "Invalid Username or Password!"
                        );
                    }

                } catch (Exception ex) {

                    JOptionPane.showMessageDialog(
                            frame,
                            "Database Error: " + ex.getMessage()
                    );
                }
            });

            frame.setVisible(true);
        });
    }
}
