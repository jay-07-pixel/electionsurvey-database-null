# 🔍 Diagnostic Steps for "Internal Server Error"

## ⚠️ **Quick Tests to Identify the Problem**

### **Step 1: Test Database Connection**

Open your browser and visit:
```
http://143.110.252.32:4000/api/test-db
```

**Expected:**
```json
{
  "success": true,
  "message": "Database connection successful"
}
```

**If you see error:**
- Database credentials wrong in `.env`
- MySQL not running
- Database doesn't exist

---

### **Step 2: Test if Tables Exist**

Visit:
```
http://143.110.252.32:4000/api/test-tables
```

**Expected:**
```json
{
  "success": true,
  "tables": ["users", "areas", "wards", "questions", "options", "responses"],
  "missingTables": [],
  "allTablesExist": true
}
```

**If missingTables is not empty:**
- Tables not created
- Run the SQL setup script again

---

### **Step 3: Test if Users Exist**

Visit:
```
http://143.110.252.32:4000/api/test-users
```

**Expected:**
```json
{
  "success": true,
  "count": 3,
  "users": [...]
}
```

**If count is 0:**
- No users in database
- Need to insert sample data

---

## 🔧 **Common Issues & Fixes**

### **Issue 1: Database Connection Failed**

**Symptoms:**
- `/api/test-db` returns error
- Backend console shows: "Database connection failed"

**Fix:**
1. Check `backend/.env` file:
   ```env
   DB_HOST=localhost
   DB_USER=root
   DB_PASS=your_password
   DB_NAME=election_survey
   ```

2. Verify MySQL is running:
   ```bash
   # Windows
   services.msc → Find MySQL → Start
   ```

3. Test connection manually:
   ```bash
   mysql -u root -p
   # Enter password
   USE election_survey;
   SHOW TABLES;
   ```

---

### **Issue 2: Tables Don't Exist**

**Symptoms:**
- `/api/test-tables` shows missing tables

**Fix:**
1. Open phpMyAdmin
2. Select database: `election_survey`
3. Go to SQL tab
4. Copy entire content from `backend/database_setup.sql`
5. Paste and click Go
6. Verify tables created

---

### **Issue 3: No Users in Database**

**Symptoms:**
- `/api/test-users` shows count: 0

**Fix:**
Run this in phpMyAdmin SQL tab:
```sql
INSERT INTO users (name, phone, password_hash) VALUES
('Test User', '1234567890', 'password123'),
('John Doe', '9876543210', 'password123');
```

---

### **Issue 4: Backend Code Error**

**Symptoms:**
- Server crashes
- Console shows error stack trace

**Fix:**
1. Check backend console for error messages
2. Look for line numbers in error
3. Common issues:
   - Wrong database method (query vs execute)
   - Missing try-catch blocks
   - Undefined variables

---

## 📋 **Checklist**

Run through these tests:

- [ ] Backend server is running (`npm run dev`)
- [ ] Server shows: "Server is running on port 4000"
- [ ] Server shows: "Database connected successfully"
- [ ] `/api/test-db` returns success
- [ ] `/api/test-tables` shows all 6 tables exist
- [ ] `/api/test-users` shows at least 1 user
- [ ] `.env` file has correct credentials
- [ ] MySQL is running
- [ ] Database `election_survey` exists

---

## 🚀 **Quick Fix Commands**

### **Restart Backend:**
```bash
cd backend
npm run dev
```

### **Check Backend Logs:**
Look at the terminal where `npm run dev` is running. You should see:
- "Server is running on port 4000"
- "Database connected successfully"
- Any error messages

---

## 🎯 **Most Likely Causes**

1. **Tables don't exist** (70%)
   - Solution: Run `database_setup.sql` in phpMyAdmin

2. **No users in database** (20%)
   - Solution: Insert test users

3. **Database connection failed** (10%)
   - Solution: Check `.env` credentials

---

**Run the 3 test endpoints and tell me what you see!** 🔍



