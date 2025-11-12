# 🚀 FreelanceDB Platform

A comprehensive desktop-based freelance marketplace platform built with Java Swing and MySQL, connecting students (freelancers) with companies for project opportunities. Features include advanced face recognition authentication using OpenCV.


## 📋 Table of Contents
- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Prerequisites](#-prerequisites)
- [Installation](#-installation)
- [Database Setup](#-database-setup)
- [Usage](#-usage)
- [Project Structure](#-project-structure)
- [Contributing](#-contributing)


## ✨ Features

### 👨‍💼 For Companies
- 🔍 **Search & Filter Freelancers** - Find students by skills, categories, and ratings
- 📝 **Post Job Requests** - Create detailed job postings with budgets and deadlines
- 📊 **Manage Job Posts** - Edit or delete existing job postings
- 📄 **Review Applications** - Accept or reject student applications
- 📧 **Direct Invitations** - Invite specific students for projects
- 💰 **Payment Management** - Process and track payments to freelancers
- 🔔 **Notifications** - Stay updated on invitation responses
- 🌓 **Dark Mode** - Toggle between light and dark themes

### 👨‍🎓 For Students (Freelancers)
- 🔎 **Browse Job Requests** - View all available opportunities
- ✍️ **Apply with Cover Letters** - Submit applications with personalized messages
- 📬 **Receive Invitations** - Get direct job invitations from companies
- 👤 **Profile Management** - Update skills, portfolio, bio, and profile picture
- 📱 **Application Tracking** - Monitor application status (applied/accepted/rejected)
- 💳 **Payment History** - View all received payments
- 🔔 **Notifications** - Get updates on applications and invitations
- 🌓 **Dark Mode** - Comfortable viewing experience

### 🔐 Security Features
- 🎭 **Face Recognition Login** - Biometric authentication using OpenCV
- 🔒 **Traditional Authentication** - Email and password-based login
- 👥 **Role-Based Access** - Separate dashboards for students and companies
- 🛡️ **Secure Registration** - Face data encryption and storage

## 🛠️ Tech Stack

- **Frontend:** Java Swing (GUI Framework)
- **Backend:** Java (JDBC)
- **Database:** MySQL 8.0+
- **Face Recognition:** OpenCV 4.x
- **Authentication:** Custom implementation with biometric support
- **Architecture:** MVC Pattern

## 📦 Prerequisites

Before running this project, ensure you have the following installed:

- **Java Development Kit (JDK) 8 or higher**
- **MySQL Server 8.0+**
- **OpenCV 4.x** (with Java bindings)
- **MySQL Connector/J** (JDBC Driver)
- **Webcam** (for face recognition features)

## 🔧 Installation

### 1. Clone the Repository
```bash
git clone https://github.com/devanshukondalkar17/Job-Portal-Project.git
cd [your_foldername]
```

### 2. Install OpenCV for Java

**Windows:**
```bash
# Download OpenCV from https://opencv.org/releases/
# Extract and add opencv_java4xx.dll to your system PATH
# Add opencv.jar to your project classpath
```

**Linux/Mac:**
```bash
# Install via package manager
sudo apt-get install libopencv-dev python3-opencv  # Ubuntu/Debian
brew install opencv                                  # macOS

# Or build from source
# Follow instructions at: https://docs.opencv.org/master/d7/d9f/tutorial_linux_install.html
```

### 3. Add Required Libraries

Add the following JAR files to your project's classpath:
- `opencv-4xx.jar` (OpenCV Java bindings)
- `mysql-connector-java-8.x.x.jar` (MySQL JDBC Driver)

### 4. Download Haar Cascade File

Download the face detection model:
```bash
# Download haarcascade_frontalface_default.xml
wget https://raw.githubusercontent.com/opencv/opencv/master/data/haarcascades/haarcascade_frontalface_default.xml

# Place it in your project root directory
```

## 🗄️ Database Setup

### 1. Create Database and Tables

```bash
# Login to MySQL
mysql -u root -p

# Run the SQL script
mysql -u root -p < freelanceDB.sql
```

Or manually execute:

```sql
CREATE DATABASE FreelanceDB;
USE FreelanceDB;

-- Run all table creation queries from freelanceDB.sql
```

### 2. Update Database Credentials

Edit `DBConnection.java` with your MySQL credentials:

```java
private static final String URL = "jdbc:mysql://localhost:3306/FreelanceDB";
private static final String USER = "your_mysql_username";
private static final String PASS = "your_mysql_password";
```

### 3. Verify Sample Data

The SQL script includes sample data:
- 5 Student accounts
- 5 Company accounts
- 10 Sample gigs
- 10 Sample job requests

**Default Test Credentials:**
- **Student:** `alice@gmail.com` / Password: `pass`
- **Company:** `techcorp@gmail.com` / Password: `pass`

## 🚀 Usage

### Running the Application

#### Option 1: Using IDE (Eclipse/IntelliJ)
1. Import the project into your IDE
2. Add OpenCV and MySQL libraries to build path
3. Run `LoginRegister.java` as the main class

#### Option 2: Using Command Line
```bash
# Compile
javac -cp ".:opencv-4xx.jar:mysql-connector-java-8.x.x.jar" *.java

# Run
java -cp ".:opencv-4xx.jar:mysql-connector-java-8.x.x.jar" -Djava.library.path=/path/to/opencv/lib LoginRegister
```

### First Time Setup

1. **Launch Application** - Run `LoginRegister.java`
2. **Choose Registration Method:**
   - Traditional: Enter email, password, and role
   - Face Recognition: Enter details + capture face
3. **Login:**
   - Traditional: Enter credentials
   - Face Recognition: Click "Face Login" and look at camera
4. **Explore Features** based on your role

## 📁 Project Structure

```
FreelanceDB-Platform/
│
├── src/
│   ├── LoginRegister.java          # Authentication & Registration
│   ├── StudentDashboard.java       # Student UI & Logic
│   ├── CompanyDashboard.java       # Company UI & Logic
│   ├── DBConnection.java           # Database Connection Manager
│   └── FaceRecognitionService.java # Face Recognition Logic
│
├── database/
│   └── freelanceDB.sql             # Database Schema & Sample Data
│
├── opencv/
│   └── data/
│       └── haarcascade_frontalface_default.xml
│
├── lib/
│   ├── opencv-4xx.jar
│   └── mysql-connector-java-8.x.x.jar
│
├── README.md
```

## 📊 Database Schema

### Core Tables
- **users** - User accounts (students & companies)
- **gigs** - Student service offerings
- **buyer_requests** - Company job postings
- **applications** - Job applications
- **orders** - Active projects
- **company_invites** - Direct invitations
- **payments** - Payment transactions
- **notifications** - User notifications
- **bookmarks** - Saved items


## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 🐛 Known Issues

- Face recognition accuracy depends on lighting conditions
- Requires webcam access for biometric features
- Dark mode styling may need adjustments on different OS themes

## 🔮 Future Enhancements

- [ ] Web-based interface (Spring Boot + React)
- [ ] Real-time chat between students and companies
- [ ] Payment gateway integration (Stripe/PayPal)
- [ ] Advanced search with AI-based matching
- [ ] Mobile application (Android/iOS)
- [ ] Email notifications
- [ ] File upload for portfolios
- [ ] Rating and review system enhancements


## 👤 DEVANSHU KONDALKAR

**DEVANSHU KONDALKAR**
- GitHub: https://github.com/devanshukondalkar17
- LinkedIn: https://www.linkedin.com/in/devanshu-kondalkar-b6ab00343?utm_source=share&utm_campaign=share_via&utm_content=profile&utm_medium=android_app

## 🙏 Acknowledgments

- OpenCV for face recognition capabilities
- MySQL for robust database management
- Java Swing for GUI components

---

⭐ **If you found this project helpful, please give it a star!** ⭐

Made with ❤️ DEVANSHU KONDALKAR
