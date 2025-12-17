# Android Election Survey App - Setup Instructions

## 📱 Project Overview

Complete professional Login Screen for Election Survey Android App with backend integration.

---

## 🔧 Setup Steps

### 1. **Configure Backend IP Address**

Update the IP address in both files to match your laptop's IP:

**File: `LoginActivity.java`** (Line 38)
```java
private static final String BASE_URL = "http://YOUR_LAPTOP_IP:4000";
```

**File: `ApiService.java`** (Line 20)
```java
public static final String BASE_URL = "http://YOUR_LAPTOP_IP:4000";
```

**To find your IP:**
- Windows: Open CMD → type `ipconfig` → look for IPv4 Address
- Mac/Linux: Open Terminal → type `ifconfig` → look for inet address

Example: `http://192.168.1.100:4000`

---

### 2. **Install Dependencies**

Open Android Studio and sync Gradle. The following dependencies will be installed:

- **AppCompat**: Android compatibility library
- **Material Design**: Modern UI components
- **OkHttp**: HTTP client for API calls
- **ConstraintLayout**: Flexible layouts

---

### 3. **Internet Permission**

Already added in `AndroidManifest.xml`:
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

---

### 4. **Backend Server**

Make sure your Node.js backend is running:

```bash
cd backend
npm run dev
```

Server should be running on `http://localhost:4000`

---

## 📂 Project Structure

```
app/
├── src/main/
│   ├── java/com/example/electionsurvey/
│   │   ├── LoginActivity.java           # Login screen logic
│   │   ├── AreaSelectionActivity.java   # Next screen after login
│   │   └── ApiService.java              # HTTP utility class
│   │
│   ├── res/
│   │   ├── layout/
│   │   │   ├── activity_login.xml       # Login UI
│   │   │   └── activity_area_selection.xml  # Area selection UI
│   │   └── values/
│   │       └── strings.xml              # String resources
│   │
│   └── AndroidManifest.xml              # App configuration
│
└── build.gradle                         # Dependencies
```

---

## 🎨 UI Features

### Login Screen (`activity_login.xml`)
- ✅ Logo placeholder at top
- ✅ Title: "Election Survey System"
- ✅ Phone number input field
- ✅ Password input field (with show/hide toggle)
- ✅ Login button (Government blue theme)
- ✅ Progress bar for loading state
- ✅ Error message display
- ✅ Material Design components
- ✅ Rounded corners and modern styling

### Colors
- Primary: `#0066CC` (Government Blue)
- Background: `#F5F5F5` (Light Gray)
- Error: `#D32F2F` (Red)
- Text: `#666666` (Dark Gray)

---

## 🔐 Login Flow

1. User enters phone and password
2. App validates inputs
3. Makes POST request to `/api/login`
4. On success:
   - Saves user data to SharedPreferences
   - Navigates to AreaSelectionActivity
5. On failure:
   - Shows error message below inputs
6. On network error:
   - Shows toast message

---

## 💾 Data Storage

**SharedPreferences Keys:**
- `user_id` - User's database ID
- `user_name` - User's full name
- `user_phone` - User's phone number
- `is_logged_in` - Login status (boolean)

**Preferences Name:** `ElectionSurveyPrefs`

---

## 🧪 Testing

### Test Login Credentials
Use the test data from your backend database.

Example:
- Phone: `1234567890`
- Password: `password123`

### Testing Steps:
1. Make sure backend is running
2. Update IP address in code
3. Run app on device/emulator
4. Enter credentials
5. Click Login
6. Should navigate to Area Selection screen

### Troubleshooting:
- **"Unable to connect to server"**: Check backend is running and IP is correct
- **"Invalid phone or password"**: Check credentials exist in database
- **Network error**: Check phone and laptop are on same WiFi network

---

## 📡 API Integration

### Login Endpoint
```
POST http://YOUR_IP:4000/api/login

Request Body:
{
  "phone": "1234567890",
  "password": "password123"
}

Success Response (200):
{
  "success": true,
  "data": {
    "id": 1,
    "name": "John Doe",
    "phone": "1234567890"
  }
}

Error Response (401):
{
  "success": false,
  "message": "Invalid credentials"
}
```

---

## 🔄 Session Management

- Login state is persisted using SharedPreferences
- User stays logged in even after app restart
- Logout button clears session and returns to login
- Back button disabled on Area Selection to prevent accidental logout

---

## 🚀 Next Steps

After successful login implementation, the next features to add:

1. **Area Selection Screen**
   - Fetch areas from `/api/areas`
   - Display in Spinner/RecyclerView
   
2. **Ward Selection Screen**
   - Fetch wards from `/api/wards/:areaId`
   - Dynamic loading based on selected area

3. **Survey Questions Screen**
   - Fetch questions from `/api/surveys/:surveyId/questions`
   - Display different question types (radio, checkbox, text)

4. **Submit Responses**
   - POST to `/api/responses`
   - Save all answers in batch

---

## 📝 Important Notes

1. **cleartext traffic** is enabled for local development (HTTP)
2. For production, use HTTPS and remove `usesCleartextTraffic="true"`
3. Backend must be running before testing the app
4. Phone and laptop must be on the same network
5. Test on physical device for best results

---

## 🛠️ Build Configuration

**Minimum SDK:** 24 (Android 7.0)  
**Target SDK:** 34 (Android 14)  
**Java Version:** 8  
**Gradle Plugin:** Latest stable

---

## ✅ Checklist

- [ ] Backend server running on port 4000
- [ ] IP address updated in LoginActivity.java and ApiService.java
- [ ] Gradle sync completed successfully
- [ ] Internet permission verified
- [ ] Test user exists in database
- [ ] Phone and laptop on same network
- [ ] App builds and runs without errors

---

## 📞 Support

If you encounter issues:
1. Check backend logs for API errors
2. Use Android Logcat to see app errors
3. Verify network connectivity
4. Ensure proper IP configuration

---

**Ready to proceed with Area Selection API Integration!** 🎉





