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
    private JButton loginBtn, registerBtn, faceLoginBtn, faceRegisterBtn;
    private JComboBox<String> roleBox;
    private FaceRecognitionService faceService;

    public LoginRegister() {
        faceService = new FaceRecognitionService();
        
        setTitle("Login / Register with Face Recognition");
        setSize(450, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(8, 2, 10, 10));

        add(new JLabel("Email:"));
        emailField = new JTextField();
        add(emailField);

        add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        add(passwordField);

        add(new JLabel("Role (for registration):"));
        roleBox = new JComboBox<>(new String[]{"student", "company"});
        add(roleBox);

        loginBtn = new JButton("Login");
        registerBtn = new JButton("Register");
        add(loginBtn);
        add(registerBtn);

        faceLoginBtn = new JButton("Face Login");
        faceRegisterBtn = new JButton("Register with Face");
        add(faceLoginBtn);
        add(faceRegisterBtn);

        add(new JLabel(""));
        add(new JLabel(""));

        loginBtn.addActionListener(e -> login());
        registerBtn.addActionListener(e -> register());
        faceLoginBtn.addActionListener(e -> faceLogin());
        faceRegisterBtn.addActionListener(e -> faceRegister());

        setVisible(true);
    }

    private void login() {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());
        String role = (String) roleBox.getSelectedItem();

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM users WHERE email=? AND password=? AND role=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, email);
            ps.setString(2, password);
            ps.setString(3, role);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Login Successful!");
                int userId = rs.getInt("user_id");
                String name = rs.getString("name");
                dispose();
                if (role.equals("student")) {
                    new StudentDashboard(userId, name);
                } else {
                    new CompanyDashboard(userId, name);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials!");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void register() {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());
        String role = (String) roleBox.getSelectedItem();
        String name = JOptionPane.showInputDialog(this, "Enter Name:");

        String skills = "", category = "";
        if (role.equals("student")) {
            category = JOptionPane.showInputDialog(this, "Enter category (e.g. Programming, Design):");
            skills = JOptionPane.showInputDialog(this, "Enter skills comma separated (e.g. Java,Python):");
        }

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO users(name,email,password,role,skills,category) VALUES(?,?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, password);
            ps.setString(4, role);
            ps.setString(5, skills);
            ps.setString(6, category);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Registration Successful!");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void faceLogin() {
        String role = (String) roleBox.getSelectedItem();
        
        JOptionPane.showMessageDialog(this, "Position your face in front of the camera");
        
        String faceData = faceService.captureFace();
        if (faceData == null) {
            JOptionPane.showMessageDialog(this, "Failed to capture face!");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT user_id, name, role, face_data FROM users WHERE role=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, role);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String storedFaceData = rs.getString("face_data");
                if (storedFaceData != null && faceService.matchFaces(faceData, storedFaceData)) {
                    int userId = rs.getInt("user_id");
                    String name = rs.getString("name");
                    String userRole = rs.getString("role");
                    
                    JOptionPane.showMessageDialog(this, "Face Login Successful! Welcome " + name);
                    dispose();
                    if (userRole.equals("student")) {
                        new StudentDashboard(userId, name);
                    } else {
                        new CompanyDashboard(userId, name);
                    }
                    return;
                }
            }
            
            JOptionPane.showMessageDialog(this, "Face not recognized! Please register first.");
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void faceRegister() {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());
        String role = (String) roleBox.getSelectedItem();
        
        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter email and password first!");
            return;
        }

        String name = JOptionPane.showInputDialog(this, "Enter Name:");
        if (name == null || name.isEmpty()) return;

        String skills = "", category = "";
        if (role.equals("student")) {
            category = JOptionPane.showInputDialog(this, "Enter category (e.g. Programming, Design):");
            skills = JOptionPane.showInputDialog(this, "Enter skills comma separated (e.g. Java,Python):");
        }

        JOptionPane.showMessageDialog(this, "Position your face in front of the camera.\nMultiple photos will be captured.");
        
        String faceData = faceService.captureFace();
        if (faceData == null) {
            JOptionPane.showMessageDialog(this, "Failed to capture face!");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO users(name,email,password,role,skills,category,face_data) VALUES(?,?,?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, password);
            ps.setString(4, role);
            ps.setString(5, skills);
            ps.setString(6, category);
            ps.setString(7, faceData);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Registration with Face Successful!");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        new LoginRegister();
    }
}