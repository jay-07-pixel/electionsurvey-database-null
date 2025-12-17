# 🚨 FIX DATABASE ISSUE - Step by Step

## ⚠️ **Your Database is Empty - Need to Setup**

Since you deleted all data, you need to recreate everything.

---

## 🚀 **QUICK FIX (5 Minutes)**

### **Step 1: Run Database Verification**

In your backend terminal:

```bash
cd backend
node verify_database.js
```

**This will show you:**
- ✅ Which tables exist
- ❌ Which tables are missing
- 👥 How many users/areas/questions you have

---

### **Step 2: Open phpMyAdmin**

1. Go to your phpMyAdmin (usually `http://localhost/phpmyadmin` or your hosting panel)
2. Click on your database: `election_survey`
3. Click **SQL** tab at the top

---

### **Step 3: Run the Setup Script**

1. Open file: `backend/database_setup.sql`
2. **Copy ALL the SQL code** (Ctrl+A, Ctrl+C)
3. **Paste** into phpMyAdmin SQL tab
4. Click **Go** button

**This will:**
- ✅ Create all 6 tables
- ✅ Insert sample data
- ✅ Set up relationships

---

### **Step 4: Verify It Worked**

Run verification again:
```bash
node verify_database.js
```

**Should show:**
- ✅ All 6 tables exist
- ✅ 3 users
- ✅ 5 areas
- ✅ 9 wards
- ✅ 6 questions

---

## 📋 **Manual Setup (If SQL Script Fails)**

### **Create Tables One by One:**

#### **1. Users Table**
```sql
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(15) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### **2. Areas Table**
```sql
CREATE TABLE areas (
    id INT PRIMARY KEY AUTO_INCREMENT,
    area_name VARCHAR(100) NOT NULL
);
```

#### **3. Wards Table**
```sql
CREATE TABLE wards (
    id INT PRIMARY KEY AUTO_INCREMENT,
    area_id INT NOT NULL,
    ward_name VARCHAR(100) NOT NULL,
    FOREIGN KEY (area_id) REFERENCES areas(id) ON DELETE CASCADE
);
```

#### **4. Questions Table**
```sql
CREATE TABLE questions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    survey_id INT NOT NULL DEFAULT 1,
    question_text TEXT NOT NULL,
    type ENUM('text', 'single', 'multiple') NOT NULL
);
```

#### **5. Options Table**
```sql
CREATE TABLE options (
    id INT PRIMARY KEY AUTO_INCREMENT,
    question_id INT NOT NULL,
    option_text VARCHAR(255) NOT NULL,
    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE
);
```

#### **6. Responses Table**
```sql
CREATE TABLE responses (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    survey_id INT NOT NULL,
    question_id INT NOT NULL,
    area_id INT NOT NULL,
    ward_id INT NOT NULL,
    selected_option_id INT NULL,
    answer_text TEXT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (question_id) REFERENCES questions(id)
);
```

---

### **Insert Sample Data:**

#### **Insert Users:**
```sql
INSERT INTO users (name, phone, password_hash) VALUES
('Test User', '1234567890', 'password123'),
('John Doe', '9876543210', 'password123');
```

#### **Insert Areas:**
```sql
INSERT INTO areas (area_name) VALUES
('Indira Nagar'),
('Deopur'),
('Rajendra Nagar');
```

#### **Insert Wards:**
```sql
INSERT INTO wards (area_id, ward_name) VALUES
(1, 'Indira Nagar Ward 1'),
(1, 'Indira Nagar Ward 2'),
(2, 'Deopur Ward 1');
```

#### **Insert Questions:**
```sql
INSERT INTO questions (survey_id, question_text, type) VALUES
(1, 'Which party will win?', 'single'),
(1, 'Major issues in your ward', 'multiple'),
(1, 'Additional comments', 'text');
```

#### **Insert Options:**
```sql
INSERT INTO options (question_id, option_text) VALUES
(1, 'BJP'),
(1, 'NCP'),
(1, 'Congress'),
(2, 'Water'),
(2, 'Roads'),
(2, 'Electricity');
```

---

## ✅ **After Setup**

1. **Restart backend:**
   ```bash
   cd backend
   npm run dev
   ```

2. **Test endpoints:**
   - `http://143.110.252.32:4000/api/test-db`
   - `http://143.110.252.32:4000/api/test-tables`
   - `http://143.110.252.32:4000/api/test-users`

3. **Try login in app:**
   - Phone: `1234567890`
   - Password: `password123`

---

## 🎯 **Quick Checklist**

- [ ] Database `election_survey` exists
- [ ] All 6 tables created
- [ ] At least 1 user inserted
- [ ] At least 1 area inserted
- [ ] At least 1 ward inserted
- [ ] At least 1 question inserted
- [ ] Backend restarted
- [ ] Test endpoints work

---

**Run the SQL script in phpMyAdmin and your database will be fixed!** 🚀



