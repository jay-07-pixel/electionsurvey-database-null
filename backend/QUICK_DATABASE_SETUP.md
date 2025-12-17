# 🗄️ Quick Database Setup Guide

## ⚠️ **You Deleted Database - Need to Recreate Tables**

Since you deleted all data in phpMyAdmin, you need to recreate the database structure.

---

## 🚀 **Step-by-Step Setup**

### **Step 1: Open phpMyAdmin**

1. Go to your phpMyAdmin (usually `http://localhost/phpmyadmin`)
2. Click on **SQL** tab at the top

### **Step 2: Select Database**

Make sure you select the correct database:
- Database name: `election_survey`
- If it doesn't exist, create it first:
  ```sql
  CREATE DATABASE election_survey;
  ```

### **Step 3: Run Setup Script**

1. Open the file: `backend/database_setup.sql`
2. **Copy ALL the SQL code** from that file
3. **Paste it** into phpMyAdmin SQL tab
4. Click **Go** or press **Ctrl+Enter**

### **Step 4: Verify Tables Created**

After running, you should see:
- ✅ 6 tables created: `users`, `areas`, `wards`, `questions`, `options`, `responses`
- ✅ Sample data inserted

---

## 📋 **What Gets Created**

### **Tables:**
1. ✅ `users` - User accounts
2. ✅ `areas` - Geographic areas
3. ✅ `wards` - Wards within areas
4. ✅ `questions` - Survey questions
5. ✅ `options` - Answer options
6. ✅ `responses` - User responses

### **Sample Data:**
- ✅ 3 test users (phone: 1234567890, password: password123)
- ✅ 5 areas
- ✅ 9 wards
- ✅ 6 survey questions
- ✅ Multiple options for questions

---

## 🧪 **Test Login Credentials**

After setup, you can login with:

**Phone:** `1234567890`  
**Password:** `password123`

Or:

**Phone:** `9876543210`  
**Password:** `password123`

---

## 🔍 **Verify Setup Worked**

Run this in phpMyAdmin SQL tab:

```sql
SELECT 'Users' as TableName, COUNT(*) as Count FROM users
UNION ALL
SELECT 'Areas', COUNT(*) FROM areas
UNION ALL
SELECT 'Wards', COUNT(*) FROM wards
UNION ALL
SELECT 'Questions', COUNT(*) FROM questions
UNION ALL
SELECT 'Options', COUNT(*) FROM options;
```

**Expected Results:**
- Users: 3
- Areas: 5
- Wards: 9
- Questions: 6
- Options: 20+

---

## ⚠️ **If You Get Errors**

### **Error: "Table already exists"**
- Tables already exist, that's OK
- You can skip the CREATE TABLE statements
- Just run the INSERT statements

### **Error: "Database doesn't exist"**
- Create it first:
  ```sql
  CREATE DATABASE election_survey;
  USE election_survey;
  ```

### **Error: "Foreign key constraint fails"**
- Make sure you run the script in order
- Tables must be created before inserting data

---

## 🔧 **Check Backend .env File**

Make sure your `backend/.env` file has correct database credentials:

```env
DB_HOST=localhost
DB_USER=root
DB_PASS=your_password_here
DB_NAME=election_survey
PORT=4000
```

---

## ✅ **After Setup**

1. ✅ Database tables created
2. ✅ Sample data inserted
3. ✅ Backend can connect
4. ✅ App can login
5. ✅ Areas and wards load
6. ✅ Survey questions display

---

## 🚀 **Quick Commands**

### **If using MySQL command line:**

```bash
mysql -u root -p
```

Then:
```sql
source backend/database_setup.sql
```

Or copy-paste the entire SQL file content.

---

**Run the SQL script and your database will be ready!** 🎉



