-- 1️⃣ Create Database
CREATE DATABASE FreelanceDB;
USE FreelanceDB;

-- 2️⃣ Users Table
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    email VARCHAR(100) UNIQUE,
    password VARCHAR(100),
    role ENUM('student','company'),
    skills VARCHAR(255),
    category VARCHAR(100),
    rating FLOAT DEFAULT 0
);

-- 3️⃣ Gigs Table (Students Post Gigs)
CREATE TABLE gigs (
    gig_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    title VARCHAR(100),
    description TEXT,
    price DOUBLE,
    category VARCHAR(50),
    tags VARCHAR(255),
    delivery_time INT,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 4️⃣ Buyer Requests (Companies Post Jobs)
CREATE TABLE buyer_requests (
    request_id INT AUTO_INCREMENT PRIMARY KEY,
    company_id INT,
    title VARCHAR(100),
    description TEXT,
    category VARCHAR(50),
    tags VARCHAR(255),
    budget DOUBLE,
    deadline DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (company_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 5️⃣ Orders Table (Student Accepts Gig/Request)
CREATE TABLE orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    gig_id INT,
    student_id INT,
    company_id INT,
    status ENUM('pending','in-progress','completed') DEFAULT 'pending',
    start_date DATE,
    end_date DATE,
    FOREIGN KEY (gig_id) REFERENCES gigs(gig_id),
    FOREIGN KEY (student_id) REFERENCES users(user_id),
    FOREIGN KEY (company_id) REFERENCES users(user_id)
);

-- 6️⃣ Sample Users: 5 Students + 5 Companies
INSERT INTO users(name,email,password,role,skills,category,rating) VALUES
('Alice','alice@gmail.com','pass','student','Java,Python','Programming',4.5),
('Bob','bob@gmail.com','pass','student','Graphic Design,UI/UX','Design',4.7),
('Charlie','charlie@gmail.com','pass','student','Content Writing,SEO','Writing',4.3),
('David','david@gmail.com','pass','student','Video Editing,Animation','Multimedia',4.6),
('Eve','eve@gmail.com','pass','student','Web Development,JavaScript','Programming',4.8),
('TechCorp','techcorp@gmail.com','pass','company',NULL,NULL,0),
('DesignPro','designpro@gmail.com','pass','company',NULL,NULL,0),
('WriteIt','writeit@gmail.com','pass','company',NULL,NULL,0),
('MediaHouse','mediahouse@gmail.com','pass','company',NULL,NULL,0),
('WebSolutions','websolutions@gmail.com','pass','company',NULL,NULL,0);

-- 7️⃣ Sample Gigs by Students
INSERT INTO gigs(user_id,title,description,price,category,tags,delivery_time) VALUES
(1,'Java Web App','I will build a web app using Java',2000,'Programming','Java,Web',5),
(1,'Python Script','I will write Python automation scripts',1500,'Programming','Python,Automation',3),
(2,'Logo Design','Professional logo for your brand',1000,'Design','Graphic Design,Logo',2),
(2,'UI Mockups','Create UI/UX mockups for apps',1200,'Design','UI/UX,Design',3),
(3,'SEO Content','High-quality content with SEO',800,'Writing','Content Writing,SEO',2),
(3,'Blog Articles','Well-researched blog articles',900,'Writing','Writing,Blogging',3),
(4,'Video Editing','Edit your videos professionally',1500,'Multimedia','Video Editing,Editing',4),
(4,'Animation Video','2D animation for your project',2500,'Multimedia','Animation,Video',5),
(5,'Website Development','Build responsive websites',3000,'Programming','Web Development,HTML,CSS',7),
(5,'JS Interactive App','Interactive web apps with JS',2000,'Programming','JavaScript,Web',5);

-- 8️⃣ Sample Buyer Requests by Companies
INSERT INTO buyer_requests(company_id,title,description,category,tags,budget,deadline) VALUES
(6,'Java Backend','Need Java backend for app','Programming','Java,Backend',2500,'2025-10-25'),
(6,'Python Automation','Automate Excel tasks using Python','Programming','Python,Automation',1500,'2025-10-20'),
(7,'Logo Design','Brand new logo needed','Design','Graphic Design,Logo',1200,'2025-10-18'),
(7,'UI Redesign','Redesign UI for website','Design','UI/UX,Web Design',2000,'2025-10-22'),
(8,'SEO Articles','SEO blog content required','Writing','Content Writing,SEO',1000,'2025-10-19'),
(8,'Blog Writing','Write technical blog articles','Writing','Writing,Blogging',900,'2025-10-21'),
(9,'Promo Video','Create promotional video','Multimedia','Video Editing,Editing',1800,'2025-10-23'),
(9,'Animation Explainer','2D explainer animation','Multimedia','Animation,Video',2200,'2025-10-24'),
(10,'Responsive Website','Build responsive site with JS','Programming','Web Development,JavaScript',3200,'2025-10-26'),
(10,'Interactive Web App','Interactive app with JS','Programming','JavaScript,Web',2800,'2025-10-27');

-- 9️⃣ Sample Orders
INSERT INTO orders(gig_id,student_id,company_id,status,start_date,end_date) VALUES
(1,1,6,'completed','2025-09-01','2025-09-07'),
(2,1,6,'in-progress','2025-10-01','2025-10-03'),
(3,2,7,'pending','2025-10-05','2025-10-07'),
(4,2,7,'completed','2025-09-15','2025-09-18'),
(5,5,10,'in-progress','2025-10-03','2025-10-10');


CREATE TABLE applications (
    app_id INT AUTO_INCREMENT PRIMARY KEY,
    request_id INT,
    student_id INT,
    cover_letter TEXT,
    status ENUM('applied','accepted','rejected') DEFAULT 'applied',
    FOREIGN KEY (request_id) REFERENCES buyer_requests(request_id),
    FOREIGN KEY (student_id) REFERENCES users(user_id)
);

SELECT * FROM applications;

SELECT * FROM Companies;

ALTER TABLE users
ADD COLUMN bio TEXT,
ADD COLUMN portfolio VARCHAR(255),
ADD COLUMN profile_pic VARCHAR(255);

CREATE TABLE notifications (
    notification_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    message VARCHAR(255),
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);
ALTER TABLE orders
ADD COLUMN rating FLOAT DEFAULT 0,
ADD COLUMN review TEXT;

CREATE TABLE bookmarks (
    bookmark_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    target_id INT,  -- job_id for students, student_id for companies
    type ENUM('job','student')
);

CREATE TABLE company_invites (
    invite_id INT AUTO_INCREMENT PRIMARY KEY,
    company_id INT,
    student_id INT,
    title VARCHAR(100),
    description TEXT,
    budget DOUBLE,
    status ENUM('pending','accepted','rejected') DEFAULT 'pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (company_id) REFERENCES users(user_id),
    FOREIGN KEY (student_id) REFERENCES users(user_id)
);

CREATE TABLE payments (
    payment_id INT AUTO_INCREMENT PRIMARY KEY,
    company_id INT,
    student_id INT,
    job_title VARCHAR(255),
    amount DOUBLE,
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) DEFAULT 'completed'
);
SELECT * FROM users;
DELETE FROM users
WHERE user_id = 27

ALTER TABLE users ADD COLUMN face_data TEXT;
ALTER TABLE users 
MODIFY face_data MEDIUMTEXT;

