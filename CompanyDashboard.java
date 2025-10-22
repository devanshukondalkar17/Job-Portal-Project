import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class CompanyDashboard extends JFrame {

    private int companyId;
    private String companyName;
    private JTextArea displayArea;
    private JComboBox<String> skillDropdown, categoryDropdown;
    private JComboBox<String> ratingDropdown;
    private JButton filterBtn, refreshBtn, postRequestBtn, manageJobsBtn;

    private String[] skillOptions = {"Select Skill","Java","Python","Web Development","Graphic Design",
            "UI/UX","Content Writing","SEO","Video Editing","Animation"};
    private String[] ratingOptions = {"Select Rating","4.5+","4.0+","3.5+","3.0+"};

    public CompanyDashboard(int companyId, String companyName) {
        this.companyId = companyId;
        this.companyName = companyName;

        setTitle("Company Dashboard - "+companyName);
        setSize(1000,600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top Panel with Filters and Buttons
        JPanel top = new JPanel(new FlowLayout());

        top.add(new JLabel("Filter by Skill:"));
        skillDropdown = new JComboBox<>(skillOptions);
        top.add(skillDropdown);


        top.add(new JLabel("Min Rating:"));
        ratingDropdown = new JComboBox<>(ratingOptions);
        top.add(ratingDropdown);

        filterBtn = new JButton("Filter Freelancers");
        top.add(filterBtn);
        filterBtn.addActionListener(e -> filterStudents());

        refreshBtn = new JButton("Show All Students");
        top.add(refreshBtn);
        refreshBtn.addActionListener(e -> loadAllStudents());

        postRequestBtn = new JButton("Post a Job Request");
        top.add(postRequestBtn);
        postRequestBtn.addActionListener(e -> postRequest());

        manageJobsBtn = new JButton("Manage Job Posts");
        top.add(manageJobsBtn);
        manageJobsBtn.addActionListener(e -> manageJobPosts());

        add(top, BorderLayout.NORTH);

        // Display Area
        displayArea = new JTextArea();
        displayArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(displayArea);
        add(scrollPane, BorderLayout.CENTER);



        loadAllStudents();
               JButton viewApplicationsBtn = new JButton("View Applications");
        top.add(viewApplicationsBtn);
        viewApplicationsBtn.addActionListener(e -> viewApplications());

        JButton darkModeBtn = new JButton("🌙 Dark Mode");
top.add(darkModeBtn);

darkModeBtn.addActionListener(e -> toggleDarkMode());

JButton inviteStudentBtn = new JButton("📩 Invite Student");
top.add(inviteStudentBtn);

inviteStudentBtn.addActionListener(e -> inviteStudent());

JPanel bottom = new JPanel();
add(bottom, BorderLayout.SOUTH);

JButton notificationsBtn = new JButton("🔔 Notifications");
bottom.add(notificationsBtn);
notificationsBtn.addActionListener(e -> viewNotifications());

// 🆕 Logout Button
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

JButton paymentBtn = new JButton("💰 Make Payment");
bottom.add(paymentBtn);

paymentBtn.addActionListener(e -> openPaymentDialog());

        setVisible(true);
    }

    // Load all students
    private void loadAllStudents() {
        displayArea.setText("");
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT user_id,name,skills,category,rating FROM users WHERE role='student'";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                displayStudent(rs);
            }
            rs.close(); ps.close();
        } catch (Exception ex) {
            displayArea.setText("Error: " + ex.getMessage());
        }
    }

    // Display single student in text area
    private void displayStudent(ResultSet rs) throws SQLException {
        displayArea.append("ID: "+rs.getInt("user_id")+"\n");
        displayArea.append("Name: "+rs.getString("name")+"\n");
        displayArea.append("Category/Skills: "+rs.getString("category")+" | "+rs.getString("skills")+"\n");
        displayArea.append("Rating: "+rs.getFloat("rating")+"\n");
        displayArea.append("-----------------------------------------------------\n");
    }

    // Advanced filter
    private void filterStudents() {
        String skill = (String) skillDropdown.getSelectedItem();
        String category = (String) categoryDropdown.getSelectedItem();
        String ratingStr = (String) ratingDropdown.getSelectedItem();

        displayArea.setText("");
        try (Connection conn = DBConnection.getConnection()) {
            StringBuilder sql = new StringBuilder("SELECT user_id,name,skills,category,rating FROM users WHERE role='student'");
            if (!skill.equals("Select Skill")) sql.append(" AND (skills LIKE ? OR category LIKE ?)");
            if (!category.equals("Select Category")) sql.append(" AND category=?");
            if (!ratingStr.equals("Select Rating")) sql.append(" AND rating>=?");

            PreparedStatement ps = conn.prepareStatement(sql.toString());

            int index = 1;
            if (!skill.equals("Select Skill")) {
                ps.setString(index++, "%"+skill+"%");
                ps.setString(index++, "%"+skill+"%");
            }
            if (!category.equals("Select Category")) ps.setString(index++, category);
            if (!ratingStr.equals("Select Rating")) ps.setFloat(index++, Float.parseFloat(ratingStr.replace("+","")));

            ResultSet rs = ps.executeQuery();
            boolean found = false;
            while (rs.next()) {
                found = true;
                displayStudent(rs);
            }
            if (!found) displayArea.setText("No students found matching the criteria.");
            rs.close(); ps.close();
        } catch (Exception ex) {
            displayArea.setText("Error: "+ex.getMessage());
        }
    }

    // Job posting
    private void postRequest() {
        String title = JOptionPane.showInputDialog(this, "Enter Title:");
        String description = JOptionPane.showInputDialog(this, "Enter Description:");
        String category = JOptionPane.showInputDialog(this, "Enter Category:");
        String tags = JOptionPane.showInputDialog(this, "Enter Tags:");
        String budgetStr = JOptionPane.showInputDialog(this, "Enter Budget:");
        String deadlineStr = JOptionPane.showInputDialog(this, "Enter Deadline (YYYY-MM-DD):");

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO buyer_requests (company_id, title, description, category, tags, budget, deadline) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, companyId);
            ps.setString(2, title);
            ps.setString(3, description);
            ps.setString(4, category);
            ps.setString(5, tags);
            ps.setDouble(6, Double.parseDouble(budgetStr));
            ps.setDate(7, java.sql.Date.valueOf(deadlineStr));
            ps.executeUpdate();
            ps.close();
            JOptionPane.showMessageDialog(this, "Job Request Posted Successfully!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    // Manage jobs with editing
    private void manageJobPosts() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT request_id, title, description FROM buyer_requests WHERE company_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, companyId);
            ResultSet rs = ps.executeQuery();

            boolean found = false;
            while (rs.next()) {
                found = true;
                int jobId = rs.getInt("request_id");
                String title = rs.getString("title");
                String desc = rs.getString("description");

                JPanel panel = new JPanel(new GridLayout(0,1));
                panel.add(new JLabel("Job ID: "+jobId));
                panel.add(new JLabel("Title: "+title));
                JTextArea descArea = new JTextArea(desc);
                descArea.setLineWrap(true); descArea.setWrapStyleWord(true);
                descArea.setEditable(false);
                panel.add(new JScrollPane(descArea));

                String[] options = {"Edit ✏","Delete ❌","Next ➡"};
                int choice = JOptionPane.showOptionDialog(this, panel, "Manage Job Post",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[2]);

                if (choice == 0) editJobPost(jobId, title, desc);
                if (choice == 1) deleteJob(jobId);
            }

            if (!found) JOptionPane.showMessageDialog(this, "No job posts found.");

            rs.close(); ps.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error managing job posts: "+ex.getMessage());
        }
    }

    private void editJobPost(int jobId, String oldTitle, String oldDesc) {
        JTextField titleField = new JTextField(oldTitle);
        JTextArea descArea = new JTextArea(oldDesc);
        descArea.setLineWrap(true); descArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(descArea); scrollPane.setPreferredSize(new Dimension(250,100));

        JPanel panel = new JPanel(new GridLayout(0,1));
        panel.add(new JLabel("Title:")); panel.add(titleField);
        panel.add(new JLabel("Description:")); panel.add(scrollPane);

        int result = JOptionPane.showConfirmDialog(this, panel, "Edit Job Post", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try (Connection conn = DBConnection.getConnection()) {
                String sql = "UPDATE buyer_requests SET title=?, description=? WHERE request_id=?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, titleField.getText());
                ps.setString(2, descArea.getText());
                ps.setInt(3, jobId);
                ps.executeUpdate(); ps.close();
                JOptionPane.showMessageDialog(this, "Job post updated successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error updating job post: "+ex.getMessage());
            }
        }
    }

    private void deleteJob(int jobId) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "DELETE FROM buyer_requests WHERE request_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, jobId); ps.executeUpdate(); ps.close();
            JOptionPane.showMessageDialog(this, "Job deleted successfully!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error deleting job: "+ex.getMessage());
        }
    }

 private void viewApplications() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT a.app_id, a.request_id, u.name AS student_name, u.email, a.cover_letter, a.status, r.title " +
                         "FROM applications a " +
                         "JOIN buyer_requests r ON a.request_id = r.request_id " +
                         "JOIN users u ON a.student_id = u.user_id " +
                         "WHERE r.company_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, companyId);
            ResultSet rs = ps.executeQuery();

            boolean found = false;

            while (rs.next()) {
                found = true;
                int appId = rs.getInt("app_id");
                String jobTitle = rs.getString("title");
                String studentName = rs.getString("student_name");
                String email = rs.getString("email");
                String coverLetter = rs.getString("cover_letter");
                String status = rs.getString("status");

                JPanel panel = new JPanel(new GridLayout(0, 1));
                panel.add(new JLabel("📄 Application ID: " + appId));
                panel.add(new JLabel("📝 Job Title: " + jobTitle));
                panel.add(new JLabel("👤 Applicant Name: " + studentName));
                panel.add(new JLabel("📧 Email: " + email));
                panel.add(new JLabel("📌 Status: " + status.toUpperCase()));

                JTextArea coverArea = new JTextArea(coverLetter);
                coverArea.setLineWrap(true);
                coverArea.setWrapStyleWord(true);
                coverArea.setEditable(false);
                JScrollPane scrollPane = new JScrollPane(coverArea);
                scrollPane.setBorder(BorderFactory.createTitledBorder("✍ Cover Letter"));
                panel.add(scrollPane);

                String[] options = {"Accept ✅", "Reject ❌", "Next ➡"};
                int choice = JOptionPane.showOptionDialog(
                        this,
                        panel,
                        "Application Details",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        options,
                        options[2]
                );

                if (choice == 0) {
                    updateApplicationStatus(appId, "accepted");
                } else if (choice == 1) {
                    updateApplicationStatus(appId, "rejected");
                }
            }

            if (!found) {
                JOptionPane.showMessageDialog(this, "No applications found for your job posts.");
            }

            rs.close();
            ps.close();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "❌ Error fetching applications: " + ex.getMessage());
        }
    }

    private void updateApplicationStatus(int appId, String newStatus) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE applications SET status=? WHERE app_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, newStatus);
            ps.setInt(2, appId);
            int rows = ps.executeUpdate();
            ps.close();

            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "✅ Application " + newStatus.toUpperCase() + " successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "⚠ No application found with ID: " + appId);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "❌ Error updating status: " + ex.getMessage());
        }
    }

private boolean isDarkMode = false;

private void toggleDarkMode() {
    isDarkMode = !isDarkMode; // Toggle mode

    if(isDarkMode) {
        // Dark background & light text
        getContentPane().setBackground(java.awt.Color.DARK_GRAY);
        displayArea.setBackground(java.awt.Color.BLACK);
        displayArea.setForeground(java.awt.Color.WHITE);
        skillDropdown.setBackground(java.awt.Color.GRAY);
        skillDropdown.setForeground(java.awt.Color.WHITE);
    } else {
        // Light mode
        getContentPane().setBackground(java.awt.Color.LIGHT_GRAY);
        displayArea.setBackground(java.awt.Color.WHITE);
        displayArea.setForeground(java.awt.Color.BLACK);
        skillDropdown.setBackground(java.awt.Color.WHITE);
        skillDropdown.setForeground(java.awt.Color.BLACK);
    }

    // Update all buttons in top panel
    for(java.awt.Component comp : ((JPanel)getContentPane().getComponent(0)).getComponents()) {
        if(comp instanceof JButton) {
            comp.setBackground(isDarkMode ? java.awt.Color.GRAY : java.awt.Color.LIGHT_GRAY);
            comp.setForeground(isDarkMode ? java.awt.Color.WHITE : java.awt.Color.BLACK);
        }
    }
}

private void inviteStudent() {
    try (Connection conn = DBConnection.getConnection()) {
        // Step 1: Choose a student
        String sql = "SELECT user_id, name, skills, category FROM users WHERE role='student'";
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        StringBuilder studentsList = new StringBuilder();
        while(rs.next()) {
            studentsList.append(rs.getInt("user_id"))
                        .append(": ")
                        .append(rs.getString("name"))
                        .append(" (")
                        .append(rs.getString("category"))
                        .append(" | ")
                        .append(rs.getString("skills"))
                        .append(")\n");
        }

        if(studentsList.length() == 0) {
            JOptionPane.showMessageDialog(this, "No students available to invite.");
            rs.close(); ps.close();
            return;
        }

        String studentIdStr = JOptionPane.showInputDialog(this, 
            "Available Students:\n" + studentsList + "\nEnter Student ID to invite:");
        if(studentIdStr == null || studentIdStr.trim().isEmpty()) return;

        int studentId = Integer.parseInt(studentIdStr);

        // Step 2: Enter job title and description for invitation
        String title = JOptionPane.showInputDialog(this, "Enter Job Title:");
        String description = JOptionPane.showInputDialog(this, "Enter Job Description:");
        String budgetStr = JOptionPane.showInputDialog(this, "Enter Budget:");

        double budget = Double.parseDouble(budgetStr);

        // Step 3: Insert as a special invitation in a new table: company_invites
        String insertSql = "INSERT INTO company_invites(company_id, student_id, title, description, budget, status) " +
                           "VALUES (?, ?, ?, ?, ?, 'pending')";
        PreparedStatement insertPs = conn.prepareStatement(insertSql);
        insertPs.setInt(1, companyId);
        insertPs.setInt(2, studentId);
        insertPs.setString(3, title);
        insertPs.setString(4, description);
        insertPs.setDouble(5, budget);

        insertPs.executeUpdate();
        insertPs.close();

        JOptionPane.showMessageDialog(this, "✅ Invitation sent to student successfully!");
        rs.close(); ps.close();

    } catch(Exception ex) {
        JOptionPane.showMessageDialog(this, "Error sending invitation: " + ex.getMessage());
    }
}

private void viewNotifications() {
    StringBuilder notifText = new StringBuilder();
    try (Connection conn = DBConnection.getConnection()) {
        String sql = "SELECT i.invite_id, u.name AS student_name, i.title, i.status " +
                     "FROM company_invites i " +
                     "JOIN users u ON i.student_id = u.user_id " +
                     "WHERE i.company_id = ? AND i.status != 'pending' " +
                     "ORDER BY i.invite_id DESC";

        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, companyId);
        ResultSet rs = ps.executeQuery();

        boolean found = false;
        while(rs.next()) {
            found = true;
            notifText.append("Student: ").append(rs.getString("student_name")).append("\n");
            notifText.append("Job Title: ").append(rs.getString("title")).append("\n");
            notifText.append("Status: ").append(rs.getString("status").toUpperCase()).append("\n");
            notifText.append("-----------------------------------------------------\n");
        }

        rs.close(); ps.close();

        if(!found) notifText.append("No new notifications.");

        JOptionPane.showMessageDialog(this, notifText.toString(), "Notifications", JOptionPane.INFORMATION_MESSAGE);

    } catch(Exception ex) {
        JOptionPane.showMessageDialog(this, "Error fetching notifications: " + ex.getMessage());
    }
}


private void openPaymentDialog() {
    JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));

    JTextField studentIdField = new JTextField();
    JTextField jobTitleField = new JTextField();
    JTextField amountField = new JTextField();

    panel.add(new JLabel("Student ID:"));
    panel.add(studentIdField);
    panel.add(new JLabel("Job Title:"));
    panel.add(jobTitleField);
    panel.add(new JLabel("Amount (₹):"));
    panel.add(amountField);

    int result = JOptionPane.showConfirmDialog(
            this,
            panel,
            "Enter Payment Details",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
    );

    if (result == JOptionPane.OK_OPTION) {
        try {
            int studentId = Integer.parseInt(studentIdField.getText().trim());
            String jobTitle = jobTitleField.getText().trim();
            double amount = Double.parseDouble(amountField.getText().trim());

            if (checkAcceptedStatus(studentId, jobTitle)) {
                openMockPaymentScreen(studentId, jobTitle, amount);
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "❌ Payment not allowed. The student has not accepted this job yet.",
                        "Payment Blocked",
                        JOptionPane.WARNING_MESSAGE
                );
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid input: " + ex.getMessage());
        }
    }
}

private boolean checkAcceptedStatus(int studentId, String jobTitle) {
    boolean accepted = false;

    try (Connection conn = DBConnection.getConnection()) {
        String sql = "SELECT status FROM company_invites " +
                     "WHERE company_id = ? AND student_id = ? AND title = ? LIMIT 1";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, companyId);
        ps.setInt(2, studentId);
        ps.setString(3, jobTitle);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            String status = rs.getString("status");
            if ("accepted".equalsIgnoreCase(status)) {
                accepted = true;
            }
        }

        rs.close();
        ps.close();
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Error checking status: " + ex.getMessage());
    }

    return accepted;
}

private void openMockPaymentScreen(int studentId, String jobTitle, double amount) {
    JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
    panel.add(new JLabel("💳 Simulated Payment Gateway"));
    panel.add(new JLabel("Job: " + jobTitle));
    panel.add(new JLabel("Amount: ₹" + amount));
    panel.add(new JLabel("Paying to Student ID: " + studentId));

    int result = JOptionPane.showConfirmDialog(
            this,
            panel,
            "Confirm Payment",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.INFORMATION_MESSAGE
    );

    if (result == JOptionPane.OK_OPTION) {
        recordPayment(studentId, jobTitle, amount);
    }
}

private void recordPayment(int studentId, String jobTitle, double amount) {
    try (Connection conn = DBConnection.getConnection()) {
        String sql = "INSERT INTO payments (company_id, student_id, job_title, amount, status) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, companyId);
        ps.setInt(2, studentId);
        ps.setString(3, jobTitle);
        ps.setDouble(4, amount);
        ps.setString(5, "completed");
        ps.executeUpdate();
        ps.close();

        JOptionPane.showMessageDialog(this, "✅ Payment of ₹" + amount + " successful!");
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Payment error: " + ex.getMessage());
    }
}

}
