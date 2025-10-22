import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class StudentDashboard extends JFrame {
    private int studentId;
    private String studentName;
    private JTextArea requestsArea;
    private JButton  viewApplicationsBtn;

    public StudentDashboard(int studentId, String studentName) {
        this.studentId = studentId;
        this.studentName = studentName;

        setTitle("Student Dashboard - " + studentName);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        requestsArea = new JTextArea();
        requestsArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(requestsArea);
        add(scrollPane, BorderLayout.CENTER);

        JPanel top = new JPanel();
        add(top, BorderLayout.NORTH);


        JButton viewRequestsBtn = new JButton("View Job Requests");
        top.add(viewRequestsBtn);
        viewRequestsBtn.addActionListener(e -> viewJobRequests());

        viewApplicationsBtn = new JButton("My Applications");

        top.add(viewApplicationsBtn);

        viewApplicationsBtn.addActionListener(e -> viewMyApplications());

JButton profileBtn = new JButton("My Profile");
top.add(profileBtn);
profileBtn.addActionListener(e -> viewOrEditProfile());
JButton darkModeBtn = new JButton("🌙 Dark Mode");
top.add(darkModeBtn);

darkModeBtn.addActionListener(e -> toggleDarkMode());

JButton viewInvitesBtn = new JButton("📩 Invitations");
top.add(viewInvitesBtn);

viewInvitesBtn.addActionListener(e -> viewInvitations());

JPanel bottom = new JPanel();
add(bottom, BorderLayout.SOUTH);

JButton notificationsBtn = new JButton("🔔 Notifications");
bottom.add(notificationsBtn);
notificationsBtn.addActionListener(e -> viewNotifications());

JButton logoutBtn = new JButton("🚪 Logout");
bottom.add(logoutBtn);

logoutBtn.addActionListener(e -> {
    int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION
    );

    if (confirm == JOptionPane.YES_OPTION) {
        dispose(); // close current dashboard
        new LoginRegister(); // reopen login page
    }
});

JButton viewPaymentsBtn = new JButton("💰 View Payments");
bottom.add(viewPaymentsBtn);

viewPaymentsBtn.addActionListener(e -> viewPayments());

        setVisible(true);

    }

    private void viewJobRequests() {
        requestsArea.setText("");
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT r.request_id, u.name AS company_name, r.title, r.description, r.category, r.tags, r.budget, r.deadline "
                    +
                    "FROM buyer_requests r JOIN users u ON r.company_id = u.user_id " +
                    "ORDER BY r.deadline ASC";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int reqId = rs.getInt("request_id");
                requestsArea.append("Request ID: " + reqId + "\n");
                requestsArea.append("Company: " + rs.getString("company_name") + "\n");
                requestsArea.append("Title: " + rs.getString("title") + "\n");
                requestsArea.append("Category: " + rs.getString("category") + "\n");
                requestsArea.append("Tags: " + rs.getString("tags") + "\n");
                requestsArea.append("Budget: ₹" + rs.getDouble("budget") + "\n");
                requestsArea.append("Deadline: " + rs.getDate("deadline") + "\n");
                requestsArea.append("Description: " + rs.getString("description") + "\n");
                requestsArea.append("-----------------------------------------------------\n");
            }
            rs.close();
            ps.close();

            // After showing all requests, ask student if they want to apply
            String applyStr = javax.swing.JOptionPane.showInputDialog(this, "Enter Request ID to Apply (or Cancel):");
            if (applyStr != null && !applyStr.trim().isEmpty()) {
                int applyReqId = Integer.parseInt(applyStr);
                applyToRequest(applyReqId);
            }

        } catch (Exception ex) {
            requestsArea.setText("Error fetching job requests: " + ex.getMessage());
        }
    }

    private void applyToRequest(int requestId) {
        String coverLetter = javax.swing.JOptionPane.showInputDialog(this, "Enter a short cover letter:");
        if (coverLetter == null || coverLetter.trim().isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this, "Application cancelled.");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String checkSql = "SELECT * FROM applications WHERE request_id=? AND student_id=?";
            PreparedStatement checkPs = conn.prepareStatement(checkSql);
            checkPs.setInt(1, requestId);
            checkPs.setInt(2, studentId);
            ResultSet rs = checkPs.executeQuery();
            if (rs.next()) {
                javax.swing.JOptionPane.showMessageDialog(this, "You already applied for this request!");
                rs.close();
                checkPs.close();
                return;
            }
            rs.close();
            checkPs.close();

            String insertSql = "INSERT INTO applications(request_id, student_id, cover_letter) VALUES(?,?,?)";
            PreparedStatement ps = conn.prepareStatement(insertSql);
            ps.setInt(1, requestId);
            ps.setInt(2, studentId);
            ps.setString(3, coverLetter);
            ps.executeUpdate();
            ps.close();

            javax.swing.JOptionPane.showMessageDialog(this, "Application submitted successfully!");
        } catch (Exception ex) {
            javax.swing.JOptionPane.showMessageDialog(this, "Error applying: " + ex.getMessage());
        }
    }

    private void viewMyApplications() {
        requestsArea.setText("");
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT a.app_id, r.title AS job_title, u.name AS company_name, a.cover_letter, a.status " +
                    "FROM applications a " +
                    "JOIN buyer_requests r ON a.request_id = r.request_id " +
                    "JOIN users u ON r.company_id = u.user_id " +
                    "WHERE a.student_id = ? " +
                    "ORDER BY a.app_id DESC";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();

            boolean found = false;

            while (rs.next()) {
                found = true;
                requestsArea.append("Application ID: " + rs.getInt("app_id") + "\n");
                requestsArea.append("Job Title: " + rs.getString("job_title") + "\n");
                requestsArea.append("Company: " + rs.getString("company_name") + "\n");
                requestsArea.append("Status: " + rs.getString("status").toUpperCase() + "\n");
                requestsArea.append("Cover Letter: " + rs.getString("cover_letter") + "\n");
                requestsArea.append("-----------------------------------------------------\n");
            }

            if (!found) {
                requestsArea.setText("You have not applied to any jobs yet.");
            }

            rs.close();
            ps.close();
        } catch (Exception ex) {
            requestsArea.setText("❌ Error fetching applications: " + ex.getMessage());
        }
    }


    private void viewOrEditProfile() {
    try (Connection conn = DBConnection.getConnection()) {
        String sql = "SELECT name, email, skills, category, bio, portfolio, profile_pic FROM users WHERE user_id=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, studentId);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            JTextField nameField = new JTextField(rs.getString("name"));
            JTextField emailField = new JTextField(rs.getString("email"));
            JTextField skillsField = new JTextField(rs.getString("skills"));
            JTextField categoryField = new JTextField(rs.getString("category"));
            JTextField portfolioField = new JTextField(rs.getString("portfolio"));
            JTextField profilePicField = new JTextField(rs.getString("profile_pic"));
            JTextArea bioArea = new JTextArea(rs.getString("bio"));
            bioArea.setLineWrap(true);
            bioArea.setWrapStyleWord(true);
            JScrollPane bioScroll = new JScrollPane(bioArea);
            bioScroll.setPreferredSize(new java.awt.Dimension(250,100));

            JPanel panel = new JPanel(new GridLayout(0,2));
            panel.add(new JLabel("Name:"));
            panel.add(nameField);
            panel.add(new JLabel("Email:"));
            panel.add(emailField);
            panel.add(new JLabel("Category:"));
            panel.add(categoryField);
            panel.add(new JLabel("Skills (comma separated):"));
            panel.add(skillsField);
            panel.add(new JLabel("Portfolio Link:"));
            panel.add(portfolioField);
            panel.add(new JLabel("Profile Picture URL:"));
            panel.add(profilePicField);
            panel.add(new JLabel("Bio:"));
            panel.add(bioScroll);

            int result = JOptionPane.showConfirmDialog(this, panel, "My Profile", JOptionPane.OK_CANCEL_OPTION);

            if (result == JOptionPane.OK_OPTION) {
                String updateSql = "UPDATE users SET name=?, email=?, category=?, skills=?, bio=?, portfolio=?, profile_pic=? WHERE user_id=?";
                PreparedStatement updatePs = conn.prepareStatement(updateSql);
                updatePs.setString(1, nameField.getText());
                updatePs.setString(2, emailField.getText());
                updatePs.setString(3, categoryField.getText());
                updatePs.setString(4, skillsField.getText());
                updatePs.setString(5, bioArea.getText());
                updatePs.setString(6, portfolioField.getText());
                updatePs.setString(7, profilePicField.getText());
                updatePs.setInt(8, studentId);
                updatePs.executeUpdate();
                updatePs.close();

                JOptionPane.showMessageDialog(this, "Profile updated successfully!");
            }
        }

        rs.close();
        ps.close();
    } catch(Exception ex) {
        JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
    }
}

private boolean isDarkMode = false;

private void toggleDarkMode() {
    isDarkMode = !isDarkMode; // Toggle mode

    if(isDarkMode) {
        // Dark background & light text
        getContentPane().setBackground(java.awt.Color.DARK_GRAY);
        requestsArea.setBackground(java.awt.Color.BLACK);
        requestsArea.setForeground(java.awt.Color.WHITE);
    } else {
        // Light mode
        getContentPane().setBackground(java.awt.Color.LIGHT_GRAY);
        requestsArea.setBackground(java.awt.Color.WHITE);
        requestsArea.setForeground(java.awt.Color.BLACK);
    }

    // Update all buttons in top panel
    for(java.awt.Component comp : ((JPanel)getContentPane().getComponent(0)).getComponents()) {
        if(comp instanceof JButton) {
            comp.setBackground(isDarkMode ? java.awt.Color.GRAY : java.awt.Color.LIGHT_GRAY);
            comp.setForeground(isDarkMode ? java.awt.Color.WHITE : java.awt.Color.BLACK);
        }
    }
}


private void viewInvitations() {
    requestsArea.setText(""); // Clear previous content

    try (Connection conn = DBConnection.getConnection()) {
        String sql = "SELECT i.invite_id, u.name AS company_name, i.title, i.description, i.budget, i.status " +
                     "FROM company_invites i " +
                     "JOIN users u ON i.company_id = u.user_id " +
                     "WHERE i.student_id = ? " +
                     "ORDER BY i.invite_id DESC";

        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, studentId);
        ResultSet rs = ps.executeQuery();

        boolean found = false;

        while(rs.next()) {
            found = true;
            int inviteId = rs.getInt("invite_id");
            String companyName = rs.getString("company_name");
            String title = rs.getString("title");
            String desc = rs.getString("description");
            double budget = rs.getDouble("budget");
            String status = rs.getString("status");

            requestsArea.append("Invitation ID: " + inviteId + "\n");
            requestsArea.append("Company: " + companyName + "\n");
            requestsArea.append("Job Title: " + title + "\n");
            requestsArea.append("Budget: ₹" + budget + "\n");
            requestsArea.append("Status: " + status.toUpperCase() + "\n");
            requestsArea.append("Description: " + desc + "\n");
            requestsArea.append("-----------------------------------------------------\n");
        }

        if(!found) {
            requestsArea.setText("You have no invitations at the moment.");
        }

        rs.close(); ps.close();

        // Ask student to accept/reject invitation
        String inviteIdStr = javax.swing.JOptionPane.showInputDialog(this, "Enter Invitation ID to respond (or Cancel):");
        if(inviteIdStr != null && !inviteIdStr.trim().isEmpty()) {
            int inviteId = Integer.parseInt(inviteIdStr);

            String[] options = {"Accept ✅", "Reject ❌"};
            int choice = javax.swing.JOptionPane.showOptionDialog(
                    this,
                    "Do you want to accept this invitation?",
                    "Respond to Invitation",
                    javax.swing.JOptionPane.DEFAULT_OPTION,
                    javax.swing.JOptionPane.PLAIN_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            if(choice == 0) updateInvitationStatus(inviteId, "accepted");
            else if(choice == 1) updateInvitationStatus(inviteId, "rejected");
        }

    } catch(Exception ex) {
        requestsArea.setText("Error fetching invitations: " + ex.getMessage());
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

        if(rows > 0) {
            javax.swing.JOptionPane.showMessageDialog(this, "Invitation " + newStatus.toUpperCase() + " successfully!");
        } else {
            javax.swing.JOptionPane.showMessageDialog(this, "No invitation found with ID: " + inviteId);
        }

    } catch(Exception ex) {
        javax.swing.JOptionPane.showMessageDialog(this, "Error updating invitation: " + ex.getMessage());
    }
}

private void viewNotifications() {
    StringBuilder notifText = new StringBuilder();

    try (Connection conn = DBConnection.getConnection()) {

        // 1️⃣ Invitations updates
        String inviteSql = "SELECT i.invite_id, u.name AS company_name, i.title, i.status " +
                           "FROM company_invites i " +
                           "JOIN users u ON i.company_id = u.user_id " +
                           "WHERE i.student_id = ? AND i.status != 'pending' " +
                           "ORDER BY i.invite_id DESC";

        PreparedStatement ps1 = conn.prepareStatement(inviteSql);
        ps1.setInt(1, studentId);
        ResultSet rs1 = ps1.executeQuery();

        boolean found = false;

        while(rs1.next()) {
            found = true;
            notifText.append("Invitation from: ").append(rs1.getString("company_name")).append("\n");
            notifText.append("Job Title: ").append(rs1.getString("title")).append("\n");
            notifText.append("Status: ").append(rs1.getString("status").toUpperCase()).append("\n");
            notifText.append("-----------------------------------------------------\n");
        }

        rs1.close(); ps1.close();

        // 2️⃣ Application status updates
        String appSql = "SELECT a.app_id, r.title AS job_title, u.name AS company_name, a.status " +
                        "FROM applications a " +
                        "JOIN buyer_requests r ON a.request_id = r.request_id " +
                        "JOIN users u ON r.company_id = u.user_id " +
                        "WHERE a.student_id = ? AND a.status != 'applied' " +
                        "ORDER BY a.app_id DESC";

        PreparedStatement ps2 = conn.prepareStatement(appSql);
        ps2.setInt(1, studentId);
        ResultSet rs2 = ps2.executeQuery();

        while(rs2.next()) {
            found = true;
            notifText.append("Application Update for: ").append(rs2.getString("company_name")).append("\n");
            notifText.append("Job Title: ").append(rs2.getString("job_title")).append("\n");
            notifText.append("Status: ").append(rs2.getString("status").toUpperCase()).append("\n");
            notifText.append("-----------------------------------------------------\n");
        }

        rs2.close(); ps2.close();

        if(!found) notifText.append("No new notifications.");

        JOptionPane.showMessageDialog(this, notifText.toString(), "Notifications", JOptionPane.INFORMATION_MESSAGE);

    } catch(Exception ex) {
        JOptionPane.showMessageDialog(this, "Error fetching notifications: " + ex.getMessage());
    }
}


private void viewPayments() {
    StringBuilder sb = new StringBuilder();

    try (Connection conn = DBConnection.getConnection()) {
        String sql = "SELECT p.payment_id, u.name AS company_name, p.job_title, p.amount, p.payment_date " +
                     "FROM payments p " +
                     "JOIN users u ON p.company_id = u.user_id " +
                     "WHERE p.student_id = ? ORDER BY p.payment_date DESC";

        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, studentId);
        ResultSet rs = ps.executeQuery();

        boolean found = false;
        while (rs.next()) {
            found = true;
            sb.append("Company: ").append(rs.getString("company_name")).append("\n");
            sb.append("Job Title: ").append(rs.getString("job_title")).append("\n");
            sb.append("Amount: ₹").append(rs.getDouble("amount")).append("\n");
            sb.append("Date: ").append(rs.getTimestamp("payment_date")).append("\n");
            sb.append("-----------------------------------------------------\n");
        }

        rs.close(); ps.close();

        if (!found) sb.append("No payments received yet.");

        JOptionPane.showMessageDialog(this, sb.toString(), "Payment History", JOptionPane.INFORMATION_MESSAGE);

    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Error fetching payments: " + ex.getMessage());
    }
}

}




