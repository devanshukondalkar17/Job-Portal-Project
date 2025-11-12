import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

abstract class Dashboard extends JFrame {
    protected int userId;
    protected String userName;
    protected JTextArea displayArea;
    
    public Dashboard(int userId, String userName, String title) {
        this.userId = userId;
        this.userName = userName;
        
        setTitle(title);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        try {
            setupUI();
            setVisible(true);
        } catch (Exception e) {
            showError("Error initializing dashboard: " + e.getMessage());
            e.printStackTrace();
        }
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

class Student {
    private int userId;
    private String name;
    private String skills;
    private String category;
    private float rating;
    
    public Student(int userId, String name, String skills, String category, float rating) {
        this.userId = userId;
        this.name = name;
        this.skills = skills != null ? skills : "N/A";
        this.category = category != null ? category : "N/A";
        this.rating = rating;
    }
    
    public int getUserId() { return userId; }
    public String getName() { return name; }
    public String getSkills() { return skills; }
    public String getCategory() { return category; }
    public float getRating() { return rating; }
    
    public String getFormattedDisplay() {
        return String.format(
            "ID: %d\nName: %s\nCategory/Skills: %s | %s\nRating: %.1f\n%s\n",
            userId, name, category, skills, rating,
            "-----------------------------------------------------"
        );
    }
}

class JobPost {
    private int requestId;
    private String title;
    private String description;
    private String category;
    private String tags;
    private double budget;
    private java.sql.Date deadline;
    
    public JobPost(int requestId, String title, String description, 
                   String category, String tags, double budget, java.sql.Date deadline) {
        this.requestId = requestId;
        this.title = title;
        this.description = description;
        this.category = category;
        this.tags = tags;
        this.budget = budget;
        this.deadline = deadline;
    }
    
    public JobPost(int requestId, String title, String description) {
        this.requestId = requestId;
        this.title = title != null ? title : "Untitled";
        this.description = description != null ? description : "No description";
    }
    
    public int getRequestId() { return requestId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public String getTags() { return tags; }
    public double getBudget() { return budget; }
    public java.sql.Date getDeadline() { return deadline; }
    
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    
    public String getFormattedDisplay() {
        return String.format(
            "Job ID: %d\nTitle: %s\nDescription: %s\n%s\n",
            requestId, title, description,
            "-----------------------------------------------------"
        );
    }
}

class JobApplication {
    private int appId;
    private int requestId;
    private String studentName;
    private String email;
    private String coverLetter;
    private String status;
    private String jobTitle;
    
    public JobApplication(int appId, int requestId, String studentName, 
                         String email, String coverLetter, String status, String jobTitle) {
        this.appId = appId;
        this.requestId = requestId;
        this.studentName = studentName != null ? studentName : "Unknown";
        this.email = email != null ? email : "N/A";
        this.coverLetter = coverLetter != null ? coverLetter : "No cover letter provided";
        this.status = status != null ? status : "pending";
        this.jobTitle = jobTitle != null ? jobTitle : "Untitled Job";
    }
    
    public int getAppId() { return appId; }
    public int getRequestId() { return requestId; }
    public String getStudentName() { return studentName; }
    public String getEmail() { return email; }
    public String getCoverLetter() { return coverLetter; }
    public String getStatus() { return status; }
    public String getJobTitle() { return jobTitle; }
    
    public void setStatus(String status) { this.status = status; }
    
    public String getFormattedDisplay() {
        return String.format(
            "📄 Application ID: %d\n📝 Job Title: %s\n👤 Applicant: %s\n" +
            "📧 Email: %s\n📌 Status: %s\n✍ Cover Letter: %s\n%s\n",
            appId, jobTitle, studentName, email, status.toUpperCase(), 
            coverLetter, "-----------------------------------------------------"
        );
    }
}

class Invitation {
    private int inviteId;
    private int studentId;
    private String title;
    private String description;
    private double budget;
    private String status;
    
    public Invitation(int inviteId, int studentId, String title, 
                     String description, double budget, String status) {
        this.inviteId = inviteId;
        this.studentId = studentId;
        this.title = title;
        this.description = description;
        this.budget = budget;
        this.status = status;
    }
    
    public int getInviteId() { return inviteId; }
    public int getStudentId() { return studentId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public double getBudget() { return budget; }
    public String getStatus() { return status; }
    
    public void setStatus(String status) { this.status = status; }
}

class Payment {
    private int studentId;
    private String jobTitle;
    private double amount;
    private String status;
    
    public Payment(int studentId, String jobTitle, double amount, String status) {
        this.studentId = studentId;
        this.jobTitle = jobTitle;
        this.amount = amount;
        this.status = status;
    }
    
    public int getStudentId() { return studentId; }
    public String getJobTitle() { return jobTitle; }
    public double getAmount() { return amount; }
    public String getStatus() { return status; }
    
    public boolean isValid() {
        return studentId > 0 && amount > 0 && jobTitle != null && !jobTitle.trim().isEmpty();
    }
}

public class CompanyDashboard extends Dashboard {
    private JComboBox<String> skillDropdown, ratingDropdown;
    private JButton filterBtn, refreshBtn, postRequestBtn, manageJobsBtn;
    private JButton viewApplicationsBtn, darkModeBtn, inviteStudentBtn;
    private JButton notificationsBtn, logoutBtn, paymentBtn;
    private boolean isDarkMode = false;
    
    public CompanyDashboard(int companyId, String companyName) {
        super(companyId, companyName, "Company Dashboard - " + companyName);
    }
    
    @Override
    protected void setupUI() {
        try {
            String[] skillOptions = {"Select Skill", "Java", "Python", "Web Development", 
                                    "Graphic Design", "UI/UX", "Content Writing", 
                                    "SEO", "Video Editing", "Animation"};
            String[] ratingOptions = {"Select Rating", "4.5+", "4.0+", "3.5+", "3.0+"};
            
            displayArea = new JTextArea();
            displayArea.setEditable(false);
            displayArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            JScrollPane scrollPane = new JScrollPane(displayArea);
            add(scrollPane, BorderLayout.CENTER);
            
            JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
            
            topPanel.add(new JLabel("Filter by Skill:"));
            skillDropdown = new JComboBox<>(skillOptions);
            topPanel.add(skillDropdown);
            
            topPanel.add(new JLabel("Min Rating:"));
            ratingDropdown = new JComboBox<>(ratingOptions);
            topPanel.add(ratingDropdown);
            
            filterBtn = new JButton("Filter Freelancers");
            filterBtn.addActionListener(e -> filterStudents());
            topPanel.add(filterBtn);
            
            refreshBtn = new JButton("Show All Students");
            refreshBtn.addActionListener(e -> loadData());
            topPanel.add(refreshBtn);
            
            postRequestBtn = new JButton("Post a Job Request");
            postRequestBtn.addActionListener(e -> postRequest());
            topPanel.add(postRequestBtn);
            
            manageJobsBtn = new JButton("Manage Job Posts");
            manageJobsBtn.addActionListener(e -> manageJobPosts());
            topPanel.add(manageJobsBtn);
            
            viewApplicationsBtn = new JButton("View Applications");
            viewApplicationsBtn.addActionListener(e -> viewApplications());
            topPanel.add(viewApplicationsBtn);
            
            darkModeBtn = new JButton("🌙 Dark Mode");
            darkModeBtn.addActionListener(e -> toggleDarkMode());
            topPanel.add(darkModeBtn);
            
            inviteStudentBtn = new JButton("📩 Invite Student");
            inviteStudentBtn.addActionListener(e -> inviteStudent());
            topPanel.add(inviteStudentBtn);
            
            add(topPanel, BorderLayout.NORTH);
            
            JPanel bottomPanel = new JPanel(new FlowLayout());
            
            notificationsBtn = new JButton("🔔 Notifications");
            notificationsBtn.addActionListener(e -> viewNotifications());
            bottomPanel.add(notificationsBtn);
            
            paymentBtn = new JButton("💰 Make Payment");
            paymentBtn.addActionListener(e -> openPaymentDialog());
            bottomPanel.add(paymentBtn);
            
            logoutBtn = new JButton("🚪 Logout");
            logoutBtn.addActionListener(e -> logout());
            bottomPanel.add(logoutBtn);
            
            add(bottomPanel, BorderLayout.SOUTH);
            
            loadData();
            
        } catch (Exception e) {
            showError("Error setting up UI: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Override
    protected void loadData() {
        loadAllStudents();
    }
    
    private void displayData(List<Student> students) {
        if (students == null || students.isEmpty()) {
            displayArea.setText("No students found.");
            return;
        }
        
        displayArea.setText("");
        for (Student student : students) {
            displayArea.append(student.getFormattedDisplay());
        }
    }
    
    private void displayData(List<JobPost> jobs, boolean isJobPost) {
        if (jobs == null || jobs.isEmpty()) {
            displayArea.setText("No job posts found.");
            return;
        }
        
        displayArea.setText("");
        for (JobPost job : jobs) {
            displayArea.append(job.getFormattedDisplay());
        }
    }
    
    private void loadAllStudents() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT user_id, name, skills, category, rating " +
                        "FROM users WHERE role='student'";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            
            List<Student> students = new ArrayList<>();
            while (rs.next()) {
                students.add(new Student(
                    rs.getInt("user_id"),
                    rs.getString("name"),
                    rs.getString("skills"),
                    rs.getString("category"),
                    rs.getFloat("rating")
                ));
            }
            
            if (students.isEmpty()) {
                displayArea.setText("No students found in the database.\n\n" +
                    "Tip: Make sure students are registered in the system.");
            } else {
                displayData(students);
            }
            
        } catch (SQLException ex) {
            showError("Database Error: " + ex.getMessage() + 
                "\n\nPlease check:\n1. Database connection settings\n2. Table 'users' exists\n3. MySQL server is running");
            displayArea.setText("Error connecting to database.\nCheck console for details.");
            ex.printStackTrace();
        } catch (Exception ex) {
            showError("Error loading students: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void filterStudents() {
        String skill = (String) skillDropdown.getSelectedItem();
        String ratingStr = (String) ratingDropdown.getSelectedItem();
        
        if (skill == null) skill = "Select Skill";
        if (ratingStr == null) ratingStr = "Select Rating";
        
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            StringBuilder sql = new StringBuilder(
                "SELECT user_id, name, skills, category, rating " +
                "FROM users WHERE role='student'"
            );
            
            boolean hasSkill = !skill.equals("Select Skill");
            boolean hasRating = !ratingStr.equals("Select Rating");
            
            if (hasSkill) {
                sql.append(" AND (skills LIKE ? OR category LIKE ?)");
            }
            if (hasRating) {
                sql.append(" AND rating >= ?");
            }
            
            ps = conn.prepareStatement(sql.toString());
            
            int index = 1;
            if (hasSkill) {
                ps.setString(index++, "%" + skill + "%");
                ps.setString(index++, "%" + skill + "%");
            }
            if (hasRating) {
                ps.setFloat(index++, Float.parseFloat(ratingStr.replace("+", "")));
            }
            
            rs = ps.executeQuery();
            
            List<Student> students = new ArrayList<>();
            while (rs.next()) {
                students.add(new Student(
                    rs.getInt("user_id"),
                    rs.getString("name"),
                    rs.getString("skills"),
                    rs.getString("category"),
                    rs.getFloat("rating")
                ));
            }
            
            if (students.isEmpty()) {
                displayArea.setText("No students found matching the criteria:\n" +
                    (hasSkill ? "Skill: " + skill + "\n" : "") +
                    (hasRating ? "Rating: " + ratingStr + "\n" : ""));
            } else {
                displayData(students);
            }
            
        } catch (Exception ex) {
            showError("Error filtering students: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void postRequest() {
        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        
        JTextField titleField = new JTextField();
        JTextArea descArea = new JTextArea(3, 20);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(descArea);
        
        JTextField categoryField = new JTextField();
        JTextField tagsField = new JTextField();
        JTextField budgetField = new JTextField();
        JTextField deadlineField = new JTextField("2025-12-31");
        
        panel.add(new JLabel("Title:"));
        panel.add(titleField);
        panel.add(new JLabel("Description:"));
        panel.add(descScroll);
        panel.add(new JLabel("Category:"));
        panel.add(categoryField);
        panel.add(new JLabel("Tags (comma-separated):"));
        panel.add(tagsField);
        panel.add(new JLabel("Budget (₹):"));
        panel.add(budgetField);
        panel.add(new JLabel("Deadline (YYYY-MM-DD):"));
        panel.add(deadlineField);
        
        int result = JOptionPane.showConfirmDialog(this, panel,
            "Post New Job Request", JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            Connection conn = null;
            PreparedStatement ps = null;
            
            try {
                String title = titleField.getText().trim();
                String description = descArea.getText().trim();
                String category = categoryField.getText().trim();
                String tags = tagsField.getText().trim();
                String budgetStr = budgetField.getText().trim();
                String deadlineStr = deadlineField.getText().trim();
                
                if (title.isEmpty() || description.isEmpty()) {
                    showError("Title and Description are required!");
                    return;
                }
                
                conn = DBConnection.getConnection();
                String sql = "INSERT INTO buyer_requests (company_id, title, description, " +
                            "category, tags, budget, deadline) VALUES (?, ?, ?, ?, ?, ?, ?)";
                ps = conn.prepareStatement(sql);
                ps.setInt(1, userId);
                ps.setString(2, title);
                ps.setString(3, description);
                ps.setString(4, category.isEmpty() ? "General" : category);
                ps.setString(5, tags.isEmpty() ? "" : tags);
                ps.setDouble(6, budgetStr.isEmpty() ? 0.0 : Double.parseDouble(budgetStr));
                ps.setDate(7, java.sql.Date.valueOf(deadlineStr));
                ps.executeUpdate();
                
                showMessage("✅ Job Request Posted Successfully!");
                
            } catch (NumberFormatException ex) {
                showError("Invalid budget format! Please enter a valid number.");
            } catch (IllegalArgumentException ex) {
                showError("Invalid deadline format! Use YYYY-MM-DD (e.g., 2025-12-31)");
            } catch (Exception ex) {
                showError("Error posting job: " + ex.getMessage());
                ex.printStackTrace();
            } finally {
                try {
                    if (ps != null) ps.close();
                    if (conn != null) conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    private void manageJobPosts() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT request_id, title, description " +
                        "FROM buyer_requests WHERE company_id = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            rs = ps.executeQuery();
            
            java.util.List<JobPost> jobs = new ArrayList<>();
            while (rs.next()) {
                jobs.add(new JobPost(
                    rs.getInt("request_id"),
                    rs.getString("title"),
                    rs.getString("description")
                ));
            }
            
            if (jobs.isEmpty()) {
                showMessage("No job posts found.\n\nCreate a new job post using 'Post a Job Request' button.");
                return;
            }
            
            for (JobPost job : jobs) {
                JPanel panel = new JPanel(new GridLayout(0, 1));
                panel.add(new JLabel("Job ID: " + job.getRequestId()));
                panel.add(new JLabel("Title: " + job.getTitle()));
                
                JTextArea descArea = new JTextArea(job.getDescription());
                descArea.setLineWrap(true);
                descArea.setWrapStyleWord(true);
                descArea.setEditable(false);
                descArea.setRows(4);
                panel.add(new JScrollPane(descArea));
                
                String[] options = {"Edit ✏", "Delete ❌", "Next ➡"};
                int choice = JOptionPane.showOptionDialog(this, panel, 
                    "Manage Job Post", JOptionPane.DEFAULT_OPTION, 
                    JOptionPane.PLAIN_MESSAGE, null, options, options[2]);
                
                if (choice == 0) editJobPost(job);
                else if (choice == 1) deleteJob(job.getRequestId());
            }
            
        } catch (Exception ex) {
            showError("Error managing job posts: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void editJobPost(JobPost job) {
        JTextField titleField = new JTextField(job.getTitle());
        JTextArea descArea = new JTextArea(job.getDescription());
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setRows(5);
        JScrollPane scrollPane = new JScrollPane(descArea);
        scrollPane.setPreferredSize(new Dimension(300, 120));
        
        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.add(new JLabel("Title:"));
        panel.add(titleField);
        panel.add(new JLabel("Description:"));
        panel.add(scrollPane);
        
        int result = JOptionPane.showConfirmDialog(this, panel, 
            "Edit Job Post", JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            Connection conn = null;
            PreparedStatement ps = null;
            
            try {
                conn = DBConnection.getConnection();
                String sql = "UPDATE buyer_requests SET title=?, description=? " +
                            "WHERE request_id=?";
                ps = conn.prepareStatement(sql);
                ps.setString(1, titleField.getText());
                ps.setString(2, descArea.getText());
                ps.setInt(3, job.getRequestId());
                ps.executeUpdate();
                
                job.setTitle(titleField.getText());
                job.setDescription(descArea.getText());
                
                showMessage("✅ Job post updated successfully!");
                
            } catch (Exception ex) {
                showError("Error updating job post: " + ex.getMessage());
                ex.printStackTrace();
            } finally {
                try {
                    if (ps != null) ps.close();
                    if (conn != null) conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    private void deleteJob(int jobId) {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this job post?\nThis action cannot be undone.",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm != JOptionPane.YES_OPTION) return;
        
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DBConnection.getConnection();
            String sql = "DELETE FROM buyer_requests WHERE request_id=?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, jobId);
            int rows = ps.executeUpdate();
            
            if (rows > 0) {
                showMessage("✅ Job deleted successfully!");
            } else {
                showMessage("⚠ Job not found.");
            }
            
        } catch (Exception ex) {
            showError("Error deleting job: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            try {
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void viewApplications() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT a.app_id, a.request_id, u.name AS student_name, " +
                        "u.email, a.cover_letter, a.status, r.title " +
                        "FROM applications a " +
                        "JOIN buyer_requests r ON a.request_id = r.request_id " +
                        "JOIN users u ON a.student_id = u.user_id " +
                        "WHERE r.company_id = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            rs = ps.executeQuery();
            
            List<JobApplication> applications = new ArrayList<>();
            while (rs.next()) {
                applications.add(new JobApplication(
                    rs.getInt("app_id"),
                    rs.getInt("request_id"),
                    rs.getString("student_name"),
                    rs.getString("email"),
                    rs.getString("cover_letter"),
                    rs.getString("status"),
                    rs.getString("title")
                ));
            }
            
            if (applications.isEmpty()) {
                showMessage("📭 No applications found for your job posts.");
                return;
            }
            
            for (JobApplication app : applications) {
                JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
                panel.add(new JLabel("📄 Application ID: " + app.getAppId()));
                panel.add(new JLabel("📝 Job Title: " + app.getJobTitle()));
                panel.add(new JLabel("👤 Applicant Name: " + app.getStudentName()));
                panel.add(new JLabel("📧 Email: " + app.getEmail()));
                panel.add(new JLabel("📌 Status: " + app.getStatus().toUpperCase()));
                
                JTextArea coverArea = new JTextArea(app.getCoverLetter());
                coverArea.setLineWrap(true);
                coverArea.setWrapStyleWord(true);
                coverArea.setEditable(false);
                coverArea.setRows(4);
                JScrollPane scrollPane = new JScrollPane(coverArea);
                scrollPane.setBorder(BorderFactory.createTitledBorder("✍ Cover Letter"));
                panel.add(scrollPane);
                
                String[] options = {"Accept ✅", "Reject ❌", "Next ➡"};
                int choice = JOptionPane.showOptionDialog(this, panel,
                    "Application Details", JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE, null, options, options[2]);
                
                if (choice == 0) {
                    updateApplicationStatus(app.getAppId(), "accepted");
                    app.setStatus("accepted");
                } else if (choice == 1) {
                    updateApplicationStatus(app.getAppId(), "rejected");
                    app.setStatus("rejected");
                }
            }
            
        } catch (Exception ex) {
            showError("Error fetching applications: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void updateApplicationStatus(int appId, String newStatus) {
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DBConnection.getConnection();
            String sql = "UPDATE applications SET status=? WHERE app_id=?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, newStatus);
            ps.setInt(2, appId);
            int rows = ps.executeUpdate();
            
            if (rows > 0) {
                showMessage("✅ Application " + newStatus.toUpperCase() + " successfully!");
            } else {
                showMessage("⚠ No application found with ID: " + appId);
            }
            
        } catch (Exception ex) {
            showError("Error updating status: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            try {
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void toggleDarkMode() {
        isDarkMode = !isDarkMode;
        
        Color bgColor = isDarkMode ? new Color(45, 45, 45) : Color.LIGHT_GRAY;
        Color textBg = isDarkMode ? new Color(30, 30, 30) : Color.WHITE;
        Color textFg = isDarkMode ? Color.WHITE : Color.BLACK;
        Color btnBg = isDarkMode ? new Color(70, 70, 70) : new Color(240, 240, 240);
        
        getContentPane().setBackground(bgColor);
        displayArea.setBackground(textBg);
        displayArea.setForeground(textFg);
        skillDropdown.setBackground(isDarkMode ? new Color(70, 70, 70) : Color.WHITE);
        skillDropdown.setForeground(textFg);
        ratingDropdown.setBackground(isDarkMode ? new Color(70, 70, 70) : Color.WHITE);
        ratingDropdown.setForeground(textFg);
        
        darkModeBtn.setText(isDarkMode ? "☀ Light Mode" : "🌙 Dark Mode");
        
        Component[] topComps = ((JPanel) getContentPane().getComponent(0)).getComponents();
        for (Component comp : topComps) {
            if (comp instanceof JButton) {
                comp.setBackground(btnBg);
                comp.setForeground(textFg);
            } else if (comp instanceof JLabel) {
                comp.setForeground(textFg);
            }
        }
        
        Component[] bottomComps = ((JPanel) getContentPane().getComponent(2)).getComponents();
        for (Component comp : bottomComps) {
            if (comp instanceof JButton) {
                comp.setBackground(btnBg);
                comp.setForeground(textFg);
            }
        }
    }
    
    private void inviteStudent() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT user_id, name, skills, category FROM users WHERE role='student'";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            
            StringBuilder studentsList = new StringBuilder("Available Students:\n\n");
            boolean hasStudents = false;
            
            while (rs.next()) {
                hasStudents = true;
                studentsList.append(String.format("ID: %d | %s | %s | %s\n",
                    rs.getInt("user_id"),
                    rs.getString("name"),
                    rs.getString("category") != null ? rs.getString("category") : "N/A",
                    rs.getString("skills") != null ? rs.getString("skills") : "N/A"));
            }
            
            if (!hasStudents) {
                showMessage("No students available to invite.");
                return;
            }
            
            String studentIdStr = JOptionPane.showInputDialog(this,
                studentsList + "\nEnter Student ID to invite:");
            
            if (studentIdStr == null || studentIdStr.trim().isEmpty()) return;
            
            int studentId = Integer.parseInt(studentIdStr.trim());
            
            JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
            JTextField titleField = new JTextField();
            JTextArea descArea = new JTextArea(3, 20);
            descArea.setLineWrap(true);
            descArea.setWrapStyleWord(true);
            JScrollPane descScroll = new JScrollPane(descArea);
            JTextField budgetField = new JTextField();
            
            panel.add(new JLabel("Job Title:"));
            panel.add(titleField);
            panel.add(new JLabel("Job Description:"));
            panel.add(descScroll);
            panel.add(new JLabel("Budget (₹):"));
            panel.add(budgetField);
            
            int result = JOptionPane.showConfirmDialog(this, panel,
                "Create Invitation", JOptionPane.OK_CANCEL_OPTION);
            
            if (result == JOptionPane.OK_OPTION) {
                String title = titleField.getText().trim();
                String description = descArea.getText().trim();
                double budget = Double.parseDouble(budgetField.getText().trim());
                
                Invitation invite = new Invitation(0, studentId, title, description, budget, "pending");
                
                String insertSql = "INSERT INTO company_invites(company_id, student_id, " +
                                  "title, description, budget, status) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement insertPs = conn.prepareStatement(insertSql);
                insertPs.setInt(1, userId);
                insertPs.setInt(2, invite.getStudentId());
                insertPs.setString(3, invite.getTitle());
                insertPs.setString(4, invite.getDescription());
                insertPs.setDouble(5, invite.getBudget());
                insertPs.setString(6, invite.getStatus());
                insertPs.executeUpdate();
                insertPs.close();
                
                showMessage("✅ Invitation sent to student successfully!");
            }
            
        } catch (NumberFormatException ex) {
            showError("Invalid number format! Please enter valid numbers.");
        } catch (Exception ex) {
            showError("Error sending invitation: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void viewNotifications() {
        StringBuilder notifText = new StringBuilder();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT i.invite_id, u.name AS student_name, i.title, i.status " +
                        "FROM company_invites i " +
                        "JOIN users u ON i.student_id = u.user_id " +
                        "WHERE i.company_id = ? AND i.status != 'pending' " +
                        "ORDER BY i.invite_id DESC LIMIT 10";
            
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            rs = ps.executeQuery();
            
            boolean found = false;
            while (rs.next()) {
                found = true;
                notifText.append("🔔 Invite ID: ").append(rs.getInt("invite_id"))
                        .append("\n👤 Student: ").append(rs.getString("student_name"))
                        .append("\n📝 Job Title: ").append(rs.getString("title"))
                        .append("\n📌 Status: ").append(rs.getString("status").toUpperCase())
                        .append("\n").append("-".repeat(50)).append("\n\n");
            }
            
            if (!found) {
                notifText.append("📭 No new notifications.");
            }
            
            JTextArea textArea = new JTextArea(notifText.toString());
            textArea.setEditable(false);
            textArea.setRows(15);
            textArea.setColumns(40);
            JScrollPane scrollPane = new JScrollPane(textArea);
            
            JOptionPane.showMessageDialog(this, scrollPane,
                "🔔 Notifications", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (Exception ex) {
            showError("Error fetching notifications: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
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
        
        int result = JOptionPane.showConfirmDialog(this, panel,
            "💰 Enter Payment Details", JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            Connection conn = null;
            PreparedStatement ps = null;
            
            try {
                int studentId = Integer.parseInt(studentIdField.getText().trim());
                String jobTitle = jobTitleField.getText().trim();
                double amount = Double.parseDouble(amountField.getText().trim());
                
                Payment payment = new Payment(studentId, jobTitle, amount, "completed");
                
                if (!payment.isValid()) {
                    showError("Invalid payment details!\n\nPlease ensure:\n" +
                        "- Student ID is valid\n- Job title is not empty\n- Amount is greater than 0");
                    return;
                }
                
                conn = DBConnection.getConnection();
                String sql = "INSERT INTO payments(company_id, student_id, job_title, amount, status) " +
                            "VALUES (?, ?, ?, ?, ?)";
                ps = conn.prepareStatement(sql);
                ps.setInt(1, userId);
                ps.setInt(2, studentId);
                ps.setString(3, jobTitle);
                ps.setDouble(4, amount);
                ps.setString(5, "completed");
                ps.executeUpdate();
                
                showMessage(String.format("✅ Payment Successful!\n\n" +
                    "Amount: ₹%.2f\n" +
                    "Student ID: %d\n" +
                    "Job: %s\n\n" +
                    "Payment has been processed successfully!",
                    amount, studentId, jobTitle));
                
            } catch (NumberFormatException ex) {
                showError("Invalid input!\n\nPlease enter:\n" +
                    "- Valid numeric Student ID\n- Valid numeric Amount");
            } catch (Exception ex) {
                showError("Error processing payment: " + ex.getMessage() +
                    "\n\nPlease check:\n" +
                    "- Database connection\n" +
                    "- Student ID exists\n" +
                    "- 'payments' table exists");
                ex.printStackTrace();
            } finally {
                try {
                    if (ps != null) ps.close();
                    if (conn != null) conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            showMessage("👋 Logged out successfully!\n\nThank you for using our platform.");
            
            System.exit(0);
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new CompanyDashboard(1, "Test Company");
            } catch (Exception e) {
                System.err.println("Error starting CompanyDashboard: " + e.getMessage());
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                    "Failed to start Company Dashboard!\n\n" +
                    "Error: " + e.getMessage() + "\n\n" +
                    "Please check:\n" +
                    "1. MySQL server is running\n" +
                    "2. Database 'freelance_platform' exists\n" +
                    "3. Required tables are created\n" +
                    "4. Database credentials are correct in DBConnection class",
                    "Startup Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}