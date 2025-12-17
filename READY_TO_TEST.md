# 🚀 Ready to Test - Quick Start Guide

## ✅ IP Configuration Complete

Your Android app is now configured with your Wi-Fi IP address: **192.168.4.101**

---

## 📝 Pre-Testing Checklist

### 1. **Start Backend Server**
```bash
cd backend
npm run dev
```

You should see:
```
Server is running on port 4000
Database connected successfully
```

### 2. **Verify Backend is Accessible**
Open browser and visit:
```
http://192.168.4.101:4000
```

You should see:
```json
{
  "message": "Election Survey API is running"
}
```

### 3. **Test Login API (Optional)**
Using PowerShell or Command Prompt:
```powershell
curl -X POST http://192.168.4.101:4000/api/login -H "Content-Type: application/json" -d "{\"phone\":\"YOUR_TEST_PHONE\",\"password\":\"YOUR_TEST_PASSWORD\"}"
```

---

## 📱 Android App Testing

### **Step 1: Open in Android Studio**
- Open the project folder in Android Studio
- Wait for Gradle sync to complete
- Check for any errors in Build tab

### **Step 2: Connect Device/Emulator**
- **Physical Device (Recommended):**
  - Enable Developer Options
  - Enable USB Debugging
  - Connect via USB
  - Make sure phone is on same Wi-Fi: **192.168.4.x**
  
- **Emulator:**
  - May require additional network configuration
  - Physical device testing is more reliable

### **Step 3: Run the App**
1. Click the green Run button ▶️
2. Select your device
3. Wait for installation
4. App should open to login screen

### **Step 4: Test Login**
- Enter phone number from your database
- Enter password
- Click "Login" button
- Should see:
  - Loading spinner
  - Navigation to "Welcome, [Your Name]" screen

---

## 🐛 Troubleshooting

### **"Unable to connect to server"**
✅ **Solutions:**
- Verify backend is running (`npm run dev`)
- Check phone is on Wi-Fi (not mobile data)
- Confirm phone and laptop on same network
- Try opening `http://192.168.4.101:4000` in phone's browser
- Check Windows Firewall isn't blocking port 4000

### **"Invalid phone or password"**
✅ **Solutions:**
- Verify test user exists in database
- Check password matches exactly (case-sensitive)
- Review backend console for errors

### **App crashes on login**
✅ **Solutions:**
- Check Android Logcat for error messages
- Verify internet permission in AndroidManifest.xml
- Ensure OkHttp dependency is installed

### **Network timeout**
✅ **Solutions:**
- Check backend is responding (browser test)
- Verify IP address is correct in code
- Increase timeout in ApiService.java if needed

---

## 🔥 Windows Firewall Configuration

If connection fails, allow Node.js through firewall:

1. Open Windows Defender Firewall
2. Click "Allow an app or feature through Windows Defender Firewall"
3. Click "Change settings"
4. Find "Node.js" or click "Allow another app"
5. Browse to Node.js executable
6. Check both "Private" and "Public" networks
7. Click OK

**Or run this in PowerShell (as Administrator):**
```powershell
New-NetFirewallRule -DisplayName "Node.js Server" -Direction Inbound -Protocol TCP -LocalPort 4000 -Action Allow
```

---

## 📊 Test User Data

Make sure you have a test user in your database:

```sql
-- Check if user exists
SELECT * FROM users WHERE phone = 'YOUR_PHONE';

-- If not, create one
INSERT INTO users (name, phone, password_hash) 
VALUES ('Test User', '1234567890', 'password123');
```

**Note:** Password is plain text for testing (as per your controller logic)

---

## 🎯 Expected Behavior

### **Successful Login Flow:**
```
1. User enters credentials
2. "Logging in..." button shows
3. Progress spinner appears
4. Brief network delay
5. Navigation to Area Selection
6. Welcome message displays user's name
```

### **Failed Login Flow:**
```
1. User enters wrong credentials
2. "Logging in..." button shows
3. Progress spinner appears
4. Red error message appears:
   "Invalid phone or password"
5. User can try again
```

---

## 📞 Your Network Configuration

```
Wi-Fi Adapter: 192.168.4.101
Subnet: 255.255.255.0
Gateway: 192.168.4.8

Backend Server: http://192.168.4.101:4000
API Endpoint: http://192.168.4.101:4000/api/login
```

**Both devices MUST be connected to the same Wi-Fi network!**

---

## 🧪 Quick API Test Commands

### **Test Server Health:**
```bash
curl http://192.168.4.101:4000
```

### **Test Login Endpoint:**
```bash
curl -X POST http://192.168.4.101:4000/api/login ^
  -H "Content-Type: application/json" ^
  -d "{\"phone\":\"1234567890\",\"password\":\"password123\"}"
```

### **Test Areas Endpoint:**
```bash
curl http://192.168.4.101:4000/api/areas
```

---

## 📱 Android Logcat Commands

To view app logs in terminal:
```bash
# View all app logs
adb logcat | findstr "com.example.electionsurvey"

# View only errors
adb logcat *:E

# Clear and view fresh logs
adb logcat -c && adb logcat
```

---

## ✨ Success Indicators

✅ Backend shows: `Server is running on port 4000`  
✅ Backend shows: `Database connected successfully`  
✅ Browser loads: `http://192.168.4.101:4000`  
✅ Phone is on Wi-Fi: `192.168.4.x`  
✅ App installs without errors  
✅ Login screen displays correctly  
✅ Login succeeds and navigates to next screen  

---

## 🎉 Ready to Test!

Everything is configured. Now:

1. **Start backend:** `cd backend && npm run dev`
2. **Open Android Studio**
3. **Run app** ▶️
4. **Test login** with your credentials

Good luck! 🚀





