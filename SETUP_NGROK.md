# 🌐 Setup ngrok for Mobile Data Access

## What is ngrok?

ngrok creates a secure public URL that tunnels to your localhost, making your local backend accessible from anywhere on the internet (including mobile data).

---

## 📥 **Installation Steps**

### **Step 1: Download ngrok**

1. Visit: https://ngrok.com/download
2. Download **Windows (64-bit)**
3. Extract the ZIP file
4. Move `ngrok.exe` to a convenient location (e.g., `C:\ngrok\`)

### **Step 2: Sign Up (Optional but Recommended)**

1. Create free account at https://ngrok.com/signup
2. Get your auth token from dashboard
3. Run in PowerShell:
   ```bash
   ngrok config add-authtoken YOUR_TOKEN_HERE
   ```

---

## 🚀 **Usage**

### **Start Backend Server**

Terminal 1:
```bash
cd backend
npm run dev
```

Expected output:
```
Server is running on port 4000
Database connected successfully
```

### **Start ngrok Tunnel**

Terminal 2:
```bash
cd C:\ngrok
ngrok http 4000
```

**You'll see:**
```
ngrok

Session Status    online
Account           your@email.com (Plan: Free)
Version           3.x.x
Region            United States (us)
Latency           -
Web Interface     http://127.0.0.1:4040
Forwarding        https://abc123-xyz.ngrok-free.app -> http://localhost:4000

Connections       ttl     opn     rt1     rt5     p50     p90
                  0       0       0.00    0.00    0.00    0.00
```

### **Copy Your Public URL**

Copy the **Forwarding** URL (https://abc123-xyz.ngrok-free.app)

---

## 📱 **Update Android App**

### **Step 1: Update ApiService.java**

File: `app/src/main/java/com/example/electionsurvey2/ApiService.java`

**Change Line 23:**

**Before:**
```java
public static final String BASE_URL = "http://192.168.4.101:4000";
```

**After:**
```java
public static final String BASE_URL = "https://abc123-xyz.ngrok-free.app";
```

⚠️ **Important**: Use YOUR actual ngrok URL (it's different each time)

### **Step 2: Rebuild App**

1. In Android Studio:
   - Build → Clean Project
   - Build → Rebuild Project
2. Run the app

---

## ✅ **Testing**

### **Test 1: Browser Test**

On your phone (using mobile data):
```
https://abc123-xyz.ngrok-free.app
```

Should see:
```json
{"message": "Election Survey API is running"}
```

### **Test 2: Login Test**

Open your app and try to login. Should work now! ✅

---

## 🎯 **ngrok Web Interface**

ngrok provides a web dashboard at `http://127.0.0.1:4040`

Open this in your browser to see:
- All HTTP requests
- Request/response details
- Real-time traffic monitoring

Very useful for debugging!

---

## 🔄 **Important Notes**

### **Free Plan Limitations:**
- URL changes every time you restart ngrok
- Must update Android app each time with new URL
- Limited connections per minute

### **Solution for Consistent URL:**
- Sign up for ngrok paid plan ($8/month)
- Get static domain (e.g., `myapp.ngrok.io`)
- URL never changes

---

## 📊 **Comparison**

| Method | Cost | Setup Time | Permanent URL | Production Ready |
|--------|------|------------|---------------|------------------|
| ngrok (free) | Free | 2 minutes | ❌ Changes | ❌ Testing only |
| ngrok (paid) | $8/mo | 5 minutes | ✅ Static | ⚠️ OK |
| Railway | Free | 15 minutes | ✅ Static | ✅ Yes |
| DigitalOcean | $5/mo | 30 minutes | ✅ Static | ✅ Yes |

---

## 🚀 **Quick Start Commands**

### **PowerShell 1: Backend**
```bash
cd C:\Users\jayjo\AndroidStudioProjects\ELECTIONSURVEY2\backend
npm run dev
```

### **PowerShell 2: ngrok**
```bash
cd C:\ngrok
ngrok http 4000
```

### **Update Android App**
```java
// ApiService.java line 23
public static final String BASE_URL = "https://YOUR-NGROK-URL";
```

### **Rebuild & Run**
```
Build → Rebuild Project
Run ▶️
```

---

## ✅ **Expected Result**

After setup:
- ✅ App works on **WiFi**
- ✅ App works on **Mobile Data**
- ✅ App works **anywhere with internet**

---

## 🎉 **Next Steps**

1. **Download ngrok** from https://ngrok.com/download
2. **Extract and run** `ngrok http 4000`
3. **Copy the HTTPS URL** from ngrok
4. **Update** `ApiService.java` with the URL
5. **Rebuild** the app
6. **Test** on mobile data

Your app will now work on mobile data! 🚀



