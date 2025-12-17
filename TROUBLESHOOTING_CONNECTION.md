# 🔧 Troubleshooting "Unable to Connect to Server" Error

## ⚠️ Issue
Android app shows: **"Unable to connect to server"** when trying to login.

---

## ✅ **STEP-BY-STEP FIX**

### **Step 1: Start the Backend Server**

Open a new terminal/PowerShell and run:

```bash
cd backend
npm run dev
```

**Expected Output:**
```
Server is running on port 4000
Database connected successfully
```

**If you see errors:**
- Check MySQL is running
- Verify `.env` file exists with correct DB credentials
- Run `npm install` if packages are missing

---

### **Step 2: Verify Server is Accessible on Laptop**

Open your browser and visit:
```
http://localhost:4000
```

**Should see:**
```json
{"message": "Election Survey API is running"}
```

**If you don't see this:**
- Server is not running
- Port 4000 is being used by another app
- Check terminal for error messages

---

### **Step 3: Test Server from Network IP**

Your WiFi IP is: **192.168.4.101**

Open browser and visit:
```
http://192.168.4.101:4000
```

**Should see:**
```json
{"message": "Election Survey API is running"}
```

**If browser cannot connect:**
- Windows Firewall is blocking port 4000
- Need to allow Node.js through firewall (see Step 4)

---

### **Step 4: Configure Windows Firewall**

#### Option A: PowerShell (Recommended)

Open PowerShell **as Administrator** and run:

```powershell
New-NetFirewallRule -DisplayName "Node.js Backend" -Direction Inbound -Protocol TCP -LocalPort 4000 -Action Allow
```

#### Option B: GUI Method

1. Open **Windows Defender Firewall**
2. Click **"Allow an app or feature through Windows Defender Firewall"**
3. Click **"Change settings"** button
4. Click **"Allow another app"**
5. Browse to Node.js executable (usually in `C:\Program Files\nodejs\node.exe`)
6. Click **Add**
7. Check **both** "Private" and "Public" checkboxes
8. Click **OK**

---

### **Step 5: Verify IP in Android Code**

Check that the IP address in your Android app matches your laptop's IP.

**File**: `app/src/main/java/com/example/electionsurvey2/ApiService.java`

**Line 23** should be:
```java
public static final String BASE_URL = "http://192.168.4.101:4000";
```

**If IP is wrong**, update it to your laptop's WiFi IP and rebuild the app.

To find your IP again:
```bash
ipconfig
```
Look for **"Wireless LAN adapter Wi-Fi"** → **IPv4 Address**

---

### **Step 6: Check Phone Network**

**CRITICAL**: Phone and laptop MUST be on the **same WiFi network**

On your phone:
1. Go to **Settings** → **WiFi**
2. Check connected network
3. Verify IP address starts with **192.168.4.xxx**

**Common Issues:**
- Phone on mobile data instead of WiFi ❌
- Phone on different WiFi network ❌
- Phone on same WiFi but different subnet ❌

**Solution**: Connect phone to the same WiFi as laptop ✅

---

### **Step 7: Test API from Phone Browser**

On your Android phone, open Chrome/Browser and visit:
```
http://192.168.4.101:4000
```

**Should see:**
```json
{"message": "Election Survey API is running"}
```

**If you can see this:**
- Backend is accessible from phone ✅
- Problem is in Android app code

**If you cannot see this:**
- Network issue between phone and laptop ❌
- Go back to Steps 3-6

---

### **Step 8: Test Login API from Phone**

On phone browser, use a REST API testing app or visit:
```
http://192.168.4.101:4000/api/login
```

Or test with curl from laptop:
```bash
curl -X POST http://192.168.4.101:4000/api/login -H "Content-Type: application/json" -d "{\"phone\":\"1234567890\",\"password\":\"password123\"}"
```

---

### **Step 9: Rebuild Android App**

If all above steps work, rebuild the app:

1. In Android Studio:
   - Build → Clean Project
   - Build → Rebuild Project
2. Uninstall app from phone
3. Run app again

---

## 🐛 **Common Error Scenarios**

### **Scenario 1: Backend Not Running**
```
Error: Unable to connect to server
```
**Fix**: Start backend with `npm run dev`

### **Scenario 2: Wrong IP Address**
```
Error: Unable to connect to server
```
**Fix**: Update `BASE_URL` in `ApiService.java` with correct IP

### **Scenario 3: Firewall Blocking**
```
Browser on laptop works: ✅
Browser on phone fails: ❌
```
**Fix**: Configure Windows Firewall (Step 4)

### **Scenario 4: Different Network**
```
Laptop WiFi: 192.168.4.101
Phone WiFi: 192.168.1.xxx (different!)
```
**Fix**: Connect both to same WiFi network

### **Scenario 5: Database Not Connected**
```
Server starts but crashes on API calls
```
**Fix**: Check MySQL is running and `.env` has correct credentials

---

## 📋 **Quick Checklist**

Run through this checklist:

- [ ] Backend server is running (`npm run dev`)
- [ ] Server shows: "Server is running on port 4000"
- [ ] Server shows: "Database connected successfully"
- [ ] Browser on laptop can access `http://localhost:4000` ✅
- [ ] Browser on laptop can access `http://192.168.4.101:4000` ✅
- [ ] Windows Firewall allows Node.js/Port 4000
- [ ] Phone is connected to WiFi (not mobile data)
- [ ] Phone WiFi IP starts with `192.168.4.xxx`
- [ ] Browser on phone can access `http://192.168.4.101:4000` ✅
- [ ] IP in `ApiService.java` is `192.168.4.101`
- [ ] App is rebuilt after IP change
- [ ] Test user exists in database

---

## 🎯 **Most Likely Causes**

1. **Backend not running** (70% of cases)
   - Solution: `cd backend && npm run dev`

2. **Firewall blocking** (20% of cases)
   - Solution: Allow port 4000 in Windows Firewall

3. **Different WiFi networks** (5% of cases)
   - Solution: Connect both devices to same WiFi

4. **Wrong IP address** (5% of cases)
   - Solution: Update `ApiService.java` with correct IP

---

## 💡 **Quick Test**

Open phone browser and try:
```
http://192.168.4.101:4000
```

- **Works?** → Problem is in app, rebuild it
- **Doesn't work?** → Network/firewall issue, fix Steps 3-6

---

## 📞 **Need More Help?**

If still not working, check:
1. Android Logcat for detailed error messages
2. Backend console for incoming requests
3. Both devices on same network segment

---

**Follow these steps and the connection should work!** 🚀



