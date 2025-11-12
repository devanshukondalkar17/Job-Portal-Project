import java.awt.*;
import java.sql.*;
import java.util.*;
import javax.swing.*;

abstract class BaseDashboard extends JFrame {
    protected int userId;
    protected String userName;
    protected JTextArea displayArea;
    
    // Constructor
    public BaseDashboard(int userId, String userName, String title) {
        this.userId = userId;
        this.userName = userName;
        
        setTitle(title);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        setupUI();
        setVisible(true);
    }
    protected abstract void setupUI();
    protected abstract void loadData();
    
    protected void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }
    
    protected void showError(String error) {
        JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
    }
}

class JobRequest {
    private int requestId;
    private String companyName;
    private String title;
    private String description;
    private String category;
    private String tags;
    private double budget;
    private java.sql.Date deadline;
    
    public JobRequest(int requestId, String companyName, String title, 
                     String description, String category, String tags, 
                     double budget, java.sql.Date deadline) {
        this.requestId = requestId;
        this.companyName = companyName;
        this.title = title;
        this.description = description;
        this.category = category;
        this.tags = tags;
        this.budget = budget;
        this.deadline = deadline;
    }
    
    public int getRequestId() { return requestId; }
    public String getCompanyName() { return companyName; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public String getTags() { return tags; }
    public double getBudget() { return budget; }
    public java.sql.Date getDeadline() { return deadline; }
    
    public String getFormattedDisplay() {
        return String.format(
            "Request ID: %d\nCompany: %s\nTitle: %s\nCategory: %s\n" +
            "Tags: %s\nBudget: ₹%.2f\nDeadline: %s\nDescription: %s\n%s\n",
            requestId, companyName, title, category, tags, budget, 
            deadline, description, "-----------------------------------------------------"
        );
    }
}

class Application {
    private int appId;
    private String jobTitle;
    private String companyName;
    private String coverLetter;
    private String status;
    
    public Application(int appId, String jobTitle, String companyName, 
                      String coverLetter, String status) {
        this.appId = appId;
        this.jobTitle = jobTitle;
        this.companyName = companyName;
        this.coverLetter = coverLetter;
        this.status = status;
    }
    
    public int getAppId() { return appId; }
    public String getJobTitle() { return jobTitle; }
    public String getCompanyName() { return companyName; }
    public String getCoverLetter() { return coverLetter; }
    public String getStatus() { return status; }
    
    public String getFormattedDisplay() {
        return String.format(
            "Application ID: %d\nJob Title: %s\nCompany: %s\n" +
            "Status: %s\nCover Letter: %s\n%s\n",
            appId, jobTitle, companyName, status.toUpperCase(), 
            coverLetter, "-----------------------------------------------------"
        );
    }
}

class StudentInvitation {
    private int inviteId;
    private String companyName;
    private String title;
    private String description;
    private double budget;
    private String status;
    
    public StudentInvitation(int inviteId, String companyName, String title, 
                     String description, double budget, String status) {
        this.inviteId = inviteId;
        this.companyName = companyName;
        this.title = title;
        this.description = description;
        this.budget = budget;
        this.status = status;
    }
    
    public int getInviteId() { return inviteId; }
    public String getCompanyName() { return companyName; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public double getBudget() { return budget; }
    public String getStatus() { return status; }
    
    public String getFormattedDisplay() {
        return String.format(
            "Invitation ID: %d\nCompany: %s\nJob Title: %s\n" +
            "Budget: ₹%.2f\nStatus: %s\nDescription: %s\n%s\n",
            inviteId, companyName, title, budget, 
            status.toUpperCase(), description, 
            "-----------------------------------------------------"
        );
    }
}

class UserProfile {
    private String name;
    private String email;
    private String skills;
    private String category;
    private String bio;
    private String portfolio;
    private String profilePic;
    
    public UserProfile(String name, String email, String skills, 
                      String category, String bio, String portfolio, 
                      String profilePic) {
        this.name = name;
        this.email = email;
        this.skills = skills;
        this.category = category;
        this.bio = bio;
        this.portfolio = portfolio;
        this.profilePic = profilePic;
    }
    
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getSkills() { return skills; }
    public String getCategory() { return category; }
    public String getBio() { return bio; }
    public String getPortfolio() { return portfolio; }
    public String getProfilePic() { return profilePic; }
    
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setSkills(String skills) { this.skills = skills; }
    public void setCategory(String category) { this.category = category; }
    public void setBio(String bio) { this.bio = bio; }
    public void setPortfolio(String portfolio) { this.portfolio = portfolio; }
    public void setProfilePic(String profilePic) { this.profilePic = profilePic; }
}

class StudentPayment {

    private int paymentId;
    private String companyName;
    private String jobTitle;
    private double amount;
    private Timestamp paymentDate;
    
    public StudentPayment(int paymentId, String companyName, String jobTitle, 
                  double amount, Timestamp paymentDate) {
        this.paymentId = paymentId;
        this.companyName = companyName;
        this.jobTitle = jobTitle;
        this.amount = amount;
        this.paymentDate = paymentDate;
    }
    
    public int getPaymentId() { return paymentId; }
    public String getCompanyName() { return companyName; }
    public String getJobTitle() { return jobTitle; }
    public double getAmount() { return amount; }
    public Timestamp getPaymentDate() { return paymentDate; }
    
    public String getFormattedDisplay() {
        return String.format(
            "Company: %s\nJob Title: %s\nAmount: ₹%.2f\nDate: %s\n%s\n",
            companyName, jobTitle, amount, paymentDate,
            "-----------------------------------------------------"
        );
    }
}

public class StudentDashboard extends BaseDashboard {
   
    private JButton viewRequestsBtn;
    private JButton viewApplicationsBtn;
    private JButton profileBtn;
    private JButton darkModeBtn;
    private JButton viewInvitesBtn;
    private JButton notificationsBtn;
    private JButton logoutBtn;
    private JButton viewPaymentsBtn;
    private boolean isDarkMode = false;
    private JPanel topPanel;
    private JPanel bottomPanel;
    
    public StudentDashboard(int studentId, String studentName) {
        super(studentId, studentName, "Student Dashboard - " + studentName);
    }
    
    
    @Override
    protected void setupUI() {
        displayArea = new JTextArea();
        displayArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(displayArea);
        add(scrollPane, BorderLayout.CENTER);
        
        topPanel = new JPanel();
        add(topPanel, BorderLayout.NORTH);
        
        viewRequestsBtn = new JButton("View Job Requests");
        topPanel.add(viewRequestsBtn);
        viewRequestsBtn.addActionListener(e -> viewJobRequests());
        
        viewApplicationsBtn = new JButton("My Applications");
        topPanel.add(viewApplicationsBtn);
        viewApplicationsBtn.addActionListener(e -> viewMyApplications());
        
        profileBtn = new JButton("My Profile");
        topPanel.add(profileBtn);
        profileBtn.addActionListener(e -> viewOrEditProfile());
        
        darkModeBtn = new JButton("🌙 Dark Mode");
        topPanel.add(darkModeBtn);
        darkModeBtn.addActionListener(e -> toggleDarkMode());
        
        viewInvitesBtn = new JButton("📩 Invitations");
        topPanel.add(viewInvitesBtn);
        viewInvitesBtn.addActionListener(e -> viewInvitations());
        
        bottomPanel = new JPanel();
        add(bottomPanel, BorderLayout.SOUTH);
        
        notificationsBtn = new JButton("🔔 Notifications");
        bottomPanel.add(notificationsBtn);
        notificationsBtn.addActionListener(e -> viewNotifications());
        
        viewPaymentsBtn = new JButton("💰 View Payments");
        bottomPanel.add(viewPaymentsBtn);
        viewPaymentsBtn.addActionListener(e -> viewPayments());
        
        logoutBtn = new JButton("🚪 Logout");
        bottomPanel.add(logoutBtn);
        logoutBtn.addActionListener(e -> logout());
    }
    
    @Override
    protected void loadData() {
        displayArea.setText("Welcome to Student Dashboard!\n\n" +
                          "Use the buttons above to navigate through different sections.");
    }
    
    private void displayData(java.util.List<JobRequest> requests) {
        displayArea.setText("");
        for (JobRequest request : requests) {
            displayArea.append(request.getFormattedDisplay());
        }
    }
    
    private void displayData(java.util.List<Application> applications, boolean isApplication) {
        displayArea.setText("");
        if (applications.isEmpty()) {
            displayArea.setText("You have not applied to any jobs yet.");
        } else {
            for (Application app : applications) {
                displayArea.append(app.getFormattedDisplay());
            }
        }
    }
    
    private void viewJobRequests() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT r.request_id, u.name AS company_name, r.title, " +
                        "r.description, r.category, r.tags, r.budget, r.deadline " +
                        "FROM buyer_requests r JOIN users u ON r.company_id = u.user_id " +
                        "ORDER BY r.deadline ASC";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            java.util.List<JobRequest> requests = new ArrayList<>();
            while (rs.next()) {
                requests.add(new JobRequest(
                    rs.getInt("request_id"),
                    rs.getString("company_name"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getString("category"),
                    rs.getString("tags"),
                    rs.getDouble("budget"),
                    rs.getDate("deadline")
                ));
            }
            
            displayData(requests);
            
            rs.close();
            ps.close();
            
            String applyStr = JOptionPane.showInputDialog(this, 
                "Enter Request ID to Apply (or Cancel):");
            if (applyStr != null && !applyStr.trim().isEmpty()) {
                applyToRequest(Integer.parseInt(applyStr));
            }
            
        } catch (Exception ex) {
            showError("Error fetching job requests: " + ex.getMessage());
        }
    }
    
    private void applyToRequest(int requestId) {
        String coverLetter = JOptionPane.showInputDialog(this, 
            "Enter a short cover letter:");
        if (coverLetter == null || coverLetter.trim().isEmpty()) {
            showMessage("Application cancelled.");
            return;
        }
        
        try (Connection conn = DBConnection.getConnection()) {
            String checkSql = "SELECT * FROM applications WHERE request_id=? AND student_id=?";
            PreparedStatement checkPs = conn.prepareStatement(checkSql);
            checkPs.setInt(1, requestId);
            checkPs.setInt(2, userId);
            ResultSet rs = checkPs.executeQuery();
            
            if (rs.next()) {
                showMessage("You already applied for this request!");
                rs.close();
                checkPs.close();
                return;
            }
            rs.close();
            checkPs.close();
            
            String insertSql = "INSERT INTO applications(request_id, student_id, cover_letter) VALUES(?,?,?)";
            PreparedStatement ps = conn.prepareStatement(insertSql);
            ps.setInt(1, requestId);
            ps.setInt(2, userId);
            ps.setString(3, coverLetter);
            ps.executeUpdate();
            ps.close();
            
            showMessage("Application submitted successfully!");
            
        } catch (Exception ex) {
            showError("Error applying: " + ex.getMessage());
        }
    }
    
    private void viewMyApplications() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT a.app_id, r.title AS job_title, u.name AS company_name, " +
                        "a.cover_letter, a.status FROM applications a " +
                        "JOIN buyer_requests r ON a.request_id = r.request_id " +
                        "JOIN users u ON r.company_id = u.user_id " +
                        "WHERE a.student_id = ? ORDER BY a.app_id DESC";
            
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            
            java.util.List<Application> applications = new ArrayList<>();
            while (rs.next()) {
                applications.add(new Application(
                    rs.getInt("app_id"),
                    rs.getString("job_title"),
                    rs.getString("company_name"),
                    rs.getString("cover_letter"),
                    rs.getString("status")
                ));
            }
            
            displayData(applications, true);
            
            rs.close();
            ps.close();
            
        } catch (Exception ex) {
            showError("Error fetching applications: " + ex.getMessage());
        }
    }
    
    private void viewOrEditProfile() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT name, email, skills, category, bio, portfolio, " +
                        "profile_pic FROM users WHERE user_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                UserProfile profile = new UserProfile(
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("skills"),
                    rs.getString("category"),
                    rs.getString("bio"),
                    rs.getString("portfolio"),
                    rs.getString("profile_pic")
                );
                
                JTextField nameField = new JTextField(profile.getName());
                JTextField emailField = new JTextField(profile.getEmail());
                JTextField skillsField = new JTextField(profile.getSkills());
                JTextField categoryField = new JTextField(profile.getCategory());
                JTextField portfolioField = new JTextField(profile.getPortfolio());
                JTextField profilePicField = new JTextField(profile.getProfilePic());
                
                JTextArea bioArea = new JTextArea(profile.getBio());
                bioArea.setLineWrap(true);
                bioArea.setWrapStyleWord(true);
                JScrollPane bioScroll = new JScrollPane(bioArea);
                bioScroll.setPreferredSize(new Dimension(250, 100));
                
                JPanel panel = new JPanel(new GridLayout(0, 2));
                panel.add(new JLabel("Name:"));
                panel.add(nameField);
                panel.add(new JLabel("Email:"));
                panel.add(emailField);
                panel.add(new JLabel("Category:"));
                panel.add(categoryField);
                panel.add(new JLabel("Skills:"));
                panel.add(skillsField);
                panel.add(new JLabel("Portfolio:"));
                panel.add(portfolioField);
                panel.add(new JLabel("Profile Pic:"));
                panel.add(profilePicField);
                panel.add(new JLabel("Bio:"));
                panel.add(bioScroll);
                
                int result = JOptionPane.showConfirmDialog(this, panel, 
                    "My Profile", JOptionPane.OK_CANCEL_OPTION);
                
                if (result == JOptionPane.OK_OPTION) {
                    String updateSql = "UPDATE users SET name=?, email=?, category=?, " +
                                      "skills=?, bio=?, portfolio=?, profile_pic=? WHERE user_id=?";
                    PreparedStatement updatePs = conn.prepareStatement(updateSql);
                    updatePs.setString(1, nameField.getText());
                    updatePs.setString(2, emailField.getText());
                    updatePs.setString(3, categoryField.getText());
                    updatePs.setString(4, skillsField.getText());
                    updatePs.setString(5, bioArea.getText());
                    updatePs.setString(6, portfolioField.getText());
                    updatePs.setString(7, profilePicField.getText());
                    updatePs.setInt(8, userId);
                    updatePs.executeUpdate();
                    updatePs.close();
                    
                    showMessage("Profile updated successfully!");
                }
            }
            
            rs.close();
            ps.close();
            
        } catch (Exception ex) {
            showError("Error: " + ex.getMessage());
        }
    }
    
    private void toggleDarkMode() {
        isDarkMode = !isDarkMode;
        
        Color bgColor = isDarkMode ? Color.DARK_GRAY : Color.LIGHT_GRAY;
        Color textBg = isDarkMode ? Color.BLACK : Color.WHITE;
        Color textFg = isDarkMode ? Color.WHITE : Color.BLACK;
        Color btnBg = isDarkMode ? Color.GRAY : Color.LIGHT_GRAY;
        
        getContentPane().setBackground(bgColor);
        displayArea.setBackground(textBg);
        displayArea.setForeground(textFg);
        
        for (Component comp : topPanel.getComponents()) {
            if (comp instanceof JButton) {
                comp.setBackground(btnBg);
                comp.setForeground(textFg);
            }
        }
        
        for (Component comp : bottomPanel.getComponents()) {
            if (comp instanceof JButton) {
                comp.setBackground(btnBg);
                comp.setForeground(textFg);
            }
        }
    }
    
    private void viewInvitations() {
        displayArea.setText("");
        
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT i.invite_id, u.name AS company_name, i.title, " +
                        "i.description, i.budget, i.status FROM company_invites i " +
                        "JOIN users u ON i.company_id = u.user_id " +
                        "WHERE i.student_id = ? ORDER BY i.invite_id DESC";
            
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            
            java.util.List<StudentInvitation> invitations = new ArrayList<>();
            while (rs.next()) {
                invitations.add(new StudentInvitation(
                    rs.getInt("invite_id"),
                    rs.getString("company_name"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getDouble("budget"),
                    rs.getString("status")
                ));
            }
            
            if (invitations.isEmpty()) {
                displayArea.setText("You have no invitations at the moment.");
            } else {
                for (StudentInvitation invite : invitations) {
                    displayArea.append(invite.getFormattedDisplay());
                }
            }
            
            rs.close();
            ps.close();
            
            String inviteIdStr = JOptionPane.showInputDialog(this, 
                "Enter Invitation ID to respond (or Cancel):");
            if (inviteIdStr != null && !inviteIdStr.trim().isEmpty()) {
                int inviteId = Integer.parseInt(inviteIdStr);
                
                String[] options = {"Accept ✅", "Reject ❌"};
                int choice = JOptionPane.showOptionDialog(this,
                    "Do you want to accept this invitation?",
                    "Respond to Invitation",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null, options, options[0]);
                
                if (choice == 0) updateInvitationStatus(inviteId, "accepted");
                else if (choice == 1) updateInvitationStatus(inviteId, "rejected");
            }
            
        } catch (Exception ex) {
            showError("Error fetching invitations: " + ex.getMessage());
        }
    }
    
    private void updateInvitationStatus(int inviteId, String newStatus) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE company_invites SET status=? WHERE invite_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, newStatus);
            ps.setInt(2, inviteId);
            int rows = ps.executeUpdate();
            ps.close();
            
            if (rows > 0) {
                showMessage("Invitation " + newStatus.toUpperCase() + " successfully!");
            } else {
                showMessage("No invitation found with ID: " + inviteId);
            }
            
        } catch (Exception ex) {
            showError("Error updating invitation: " + ex.getMessage());
        }
    }
    
    private void viewNotifications() {
        StringBuilder notifText = new StringBuilder();
        
        try (Connection conn = DBConnection.getConnection()) {
            String inviteSql = "SELECT i.invite_id, u.name AS company_name, i.title, i.status " +
                              "FROM company_invites i JOIN users u ON i.company_id = u.user_id " +
                              "WHERE i.student_id = ? AND i.status != 'pending' " +
                              "ORDER BY i.invite_id DESC";
            
            PreparedStatement ps1 = conn.prepareStatement(inviteSql);
            ps1.setInt(1, userId);
            ResultSet rs1 = ps1.executeQuery();
            
            boolean found = false;
            while (rs1.next()) {
                found = true;
                notifText.append("Invitation from: ").append(rs1.getString("company_name"))
                        .append("\nJob Title: ").append(rs1.getString("title"))
                        .append("\nStatus: ").append(rs1.getString("status").toUpperCase())
                        .append("\n-----------------------------------------------------\n");
            }
            rs1.close();
            ps1.close();
            
            String appSql = "SELECT a.app_id, r.title AS job_title, u.name AS company_name, a.status " +
                           "FROM applications a JOIN buyer_requests r ON a.request_id = r.request_id " +
                           "JOIN users u ON r.company_id = u.user_id " +
                           "WHERE a.student_id = ? AND a.status != 'applied' " +
                           "ORDER BY a.app_id DESC";
            
            PreparedStatement ps2 = conn.prepareStatement(appSql);
            ps2.setInt(1, userId);
            ResultSet rs2 = ps2.executeQuery();
            
            while (rs2.next()) {
                found = true;
                notifText.append("Application Update for: ").append(rs2.getString("company_name"))
                        .append("\nJob Title: ").append(rs2.getString("job_title"))
                        .append("\nStatus: ").append(rs2.getString("status").toUpperCase())
                        .append("\n-----------------------------------------------------\n");
            }
            rs2.close();
            ps2.close();
            
            if (!found) notifText.append("No new notifications.");
            
            JOptionPane.showMessageDialog(this, notifText.toString(), 
                "Notifications", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (Exception ex) {
            showError("Error fetching notifications: " + ex.getMessage());
        }
    }
    
    private void viewPayments() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT p.payment_id, u.name AS company_name, p.job_title, " +
                        "p.amount, p.payment_date FROM payments p " +
                        "JOIN users u ON p.company_id = u.user_id " +
                        "WHERE p.student_id = ? ORDER BY p.payment_date DESC";
            
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            
            java.util.List<StudentPayment> payments = new ArrayList<>();
            while (rs.next()) {
                payments.add(new StudentPayment(
                    rs.getInt("payment_id"),
                    rs.getString("company_name"),
                    rs.getString("job_title"),
                    rs.getDouble("amount"),
                    rs.getTimestamp("payment_date")
                ));
            }
            
            StringBuilder sb = new StringBuilder();
            if (payments.isEmpty()) {
                sb.append("No payments received yet.");
            } else {
                for (StudentPayment payment : payments) {
                    sb.append(payment.getFormattedDisplay());
                }
            }
            
            rs.close();
            ps.close();
            
            JOptionPane.showMessageDialog(this, sb.toString(), 
                "Payment History", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (Exception ex) {
            showError("Error fetching payments: " + ex.getMessage());
        }
    }
    
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginRegister();
        }
    }
}

