# 🔧 Quick Fix: "Unable to Connect to Server"

## ✅ **What I Just Fixed**

1. ✅ Updated `LoginActivity.java` - Changed IP from `192.168.4.101` to `143.110.252.32`
2. ✅ Removed trailing slash from `ApiService.java` 
3. ✅ Created `network_security_config.xml` - Explicitly allows HTTP to your server
4. ✅ Updated `AndroidManifest.xml` - Added network security config

---

## 🚀 **Now Do This:**

### **Step 1: Rebuild the App**

In Android Studio:
1. **Build** → **Clean Project**
2. **Build** → **Rebuild Project**
3. Wait for build to finish

### **Step 2: Uninstall Old App**

On your phone:
- Settings → Apps → Election Survey → Uninstall

(This ensures old cached data is cleared)

### **Step 3: Run Fresh Install**

1. Click **Run** ▶️ in Android Studio
2. Wait for installation
3. Open the app

### **Step 4: Test Login**

1. Enter credentials
2. Click Login
3. Should work now! ✅

---

## 🔍 **If Still Not Working:**

### **Test 1: Check Server Endpoint**

On your phone browser, test the actual login endpoint:
```
http://143.110.252.32:4000/api/login
```

**Expected:** Should return an error (because no body), but NOT a connection error.

### **Test 2: Check Android Logcat**

In Android Studio:
1. View → Tool Windows → Logcat
2. Filter by: `com.example.electionsurvey2`
3. Try login
4. Look for error messages

**Common errors:**
- `java.net.UnknownHostException` → DNS/network issue
- `java.net.ConnectException` → Server not reachable
- `javax.net.ssl.SSLException` → HTTPS/SSL issue

### **Test 3: Verify Server is Running**

On the server (143.110.252.32):
```bash
# Check if Node.js is running
ps aux | grep node

# Check if port 4000 is listening
netstat -tuln | grep 4000

# Restart server if needed
cd backend
npm run dev
```

---

## 📋 **Current Configuration**

**ApiService.java:**
```java
public static final String BASE_URL = "http://143.110.252.32:4000";
```

**LoginActivity.java:**
```java
private static final String BASE_URL = "http://143.110.252.32:4000";
```

**Both are now correct!** ✅

---

## 🎯 **Most Likely Issue**

If it still doesn't work after rebuild, the server might not be running on that IP. 

**Check:**
1. Is the server actually running on `143.110.252.32`?
2. Is port 4000 open in the server's firewall?
3. Can you access `http://143.110.252.32:4000` from your phone's browser?

---

**Rebuild and try again!** 🚀



