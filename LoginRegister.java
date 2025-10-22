import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class LoginRegister extends JFrame {

    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginBtn, registerBtn, faceLoginBtn;
    private JComboBox<String> roleBox;
    private FaceRecognitionUtil faceUtil;

    public LoginRegister() {
        setTitle("Login / Register");
        setSize(400,300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(6,2,10,10));

        faceUtil = new FaceRecognitionUtil();

        add(new JLabel("Email:"));
        emailField = new JTextField();
        add(emailField);

        add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        add(passwordField);

        add(new JLabel("Role (for registration):"));
        roleBox = new JComboBox<>(new String[]{"student","company"});
        add(roleBox);

        loginBtn = new JButton("Login");
        registerBtn = new JButton("Register");
        faceLoginBtn = new JButton("Login with Face");

        add(loginBtn);
        add(registerBtn);
        add(faceLoginBtn);

        loginBtn.addActionListener(e -> login());
        registerBtn.addActionListener(e -> register());
        faceLoginBtn.addActionListener(e -> loginWithFace());

        setVisible(true);
    }

    // Normal login with email + password
     private void login() {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());
        String role = (String) roleBox.getSelectedItem();

        try(Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM users WHERE email=? AND password=? AND role=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1,email);
            ps.setString(2,password);
            ps.setString(3,role);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                JOptionPane.showMessageDialog(this,"Login Successful!");
                int userId = rs.getInt("user_id");
                String name = rs.getString("name");
                dispose(); // close login window
                if(role.equals("student")) {
                    new StudentDashboard(userId,name);
                } else {
                    new CompanyDashboard(userId,name);
                }
            } else {
                JOptionPane.showMessageDialog(this,"Invalid credentials!");
            }
        } catch(Exception ex) {
            JOptionPane.showMessageDialog(this,"Error: "+ex.getMessage());
        }
    }

    // Registration with face capture
    private void register() {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());
        String role = (String) roleBox.getSelectedItem();
        String name = JOptionPane.showInputDialog(this,"Enter Name:");

        String skills = "", category = "";
        if(role.equals("student")) {
            category = JOptionPane.showInputDialog(this,"Enter category (e.g. Programming, Design):");
            skills = JOptionPane.showInputDialog(this,"Enter skills comma separated (e.g. Java,Python):");
        }

        try(Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO users(name,email,password,role,skills,category) VALUES(?,?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1,name);
            ps.setString(2,email);
            ps.setString(3,password);
            ps.setString(4,role);
            ps.setString(5,skills);
            ps.setString(6,category);
            ps.executeUpdate();

            // Get generated user_id
            ResultSet keys = ps.getGeneratedKeys();
            int userId = 0;
            if(keys.next()) userId = keys.getInt(1);

            JOptionPane.showMessageDialog(this,"Registration Successful! Now capture your face.");
            faceUtil.registerFace(userId); // capture face images

            ps.close();
            keys.close();

        } catch(Exception ex) {
            JOptionPane.showMessageDialog(this,"Error: "+ex.getMessage());
        }
    }

private void loginWithFace() {
    JOptionPane.showMessageDialog(this,"Face recognition will start. Look at the camera.");

    int recognizedUserId = -1;
    long startTime = System.currentTimeMillis();
    long timeout = 10000; // 10 seconds

    while(System.currentTimeMillis() - startTime < timeout) {
        recognizedUserId = faceUtil.recognizeFace(); // try to recognize
        if(recognizedUserId != -1) break; // face recognized
        try {
            Thread.sleep(500); // check every 0.5 second
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    if(recognizedUserId == -1) {
        JOptionPane.showMessageDialog(this,"Face not recognized. Please try again.");
        return;
    }

    // Fetch user info from DB
    try(Connection conn = DBConnection.getConnection()) {
        String sql = "SELECT name, role FROM users WHERE user_id=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, recognizedUserId);
        ResultSet rs = ps.executeQuery();
        if(rs.next()) {
            String name = rs.getString("name");
            String role = rs.getString("role");
            JOptionPane.showMessageDialog(this,"Face recognized! Logging in as " + name);
            dispose();
            if(role.equals("student")) {
                new StudentDashboard(recognizedUserId,name);
            } else {
                new CompanyDashboard(recognizedUserId,name);
            }
        }
        rs.close();
        ps.close();
    } catch(Exception ex) {
        JOptionPane.showMessageDialog(this,"Error: "+ex.getMessage());
    }
}

    public static void main(String[] args) {
        new LoginRegister();
    }
}
