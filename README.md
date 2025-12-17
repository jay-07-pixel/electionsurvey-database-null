# Election Survey Application

A comprehensive full-stack election survey system with a Node.js backend API and a modern Android mobile application.

## 📱 Project Overview

This application is designed for conducting election surveys with location-based data collection. Users can log in, select their area and ward, answer survey questions, and submit responses that are stored in a centralized database.

### Key Features

✅ **User Authentication** - Secure login system  
✅ **Location Selection** - Area and Ward based navigation  
✅ **Dynamic Surveys** - Support for multiple question types  
✅ **Response Collection** - Batch submission with transactions  
✅ **Modern UI** - Material Design 3 with government theme  
✅ **Real-time Sync** - Backend API integration  

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────┐
│         Android Mobile App              │
│    (Java + Material Design 3)           │
└────────────────┬────────────────────────┘
                 │ HTTP/REST API
                 │
┌────────────────▼────────────────────────┐
│         Node.js Backend API             │
│    (Express + OkHttp + MySQL)           │
└────────────────┬────────────────────────┘
                 │
┌────────────────▼────────────────────────┐
│         MySQL Database                  │
│    (Users, Areas, Wards, Questions)     │
└─────────────────────────────────────────┘
```

---

## 🚀 Tech Stack

### Backend
- **Runtime**: Node.js v16+
- **Framework**: Express.js
- **Database**: MySQL 8.0+
- **Dependencies**:
  - `express` - Web framework
  - `mysql2` - MySQL client with promises
  - `cors` - Cross-origin resource sharing
  - `dotenv` - Environment configuration
  - `bcryptjs` - Password hashing
  - `nodemon` - Development auto-reload

### Frontend (Android)
- **Language**: Java
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)
- **UI Framework**: Material Design 3
- **Dependencies**:
  - Material Components 1.11.0
  - OkHttp 4.12.0 (HTTP client)
  - RecyclerView 1.3.2
  - CardView 1.0.0

---

## 📦 Installation & Setup

### ⚡ Quick Start

```bash
# 1. Clone the repository
git clone https://github.com/jay-07-pixel/electionsurvey-database-null.git
cd ELECTIONSURVEY2

# 2. Backend Setup
cd backend
npm install
cp .env.example .env
# Edit .env with your database credentials
mysql -u root -p < database_setup.sql
npm run dev

# 3. Android Setup
# Open project in Android Studio
# Update IP address in ApiService.java and LoginActivity.java
# Sync Gradle and Run
```

### Prerequisites

- Node.js (v16 or higher)
- MySQL Server (v8.0 or higher)
- Android Studio (Latest version)
- JDK 8 or higher

### Backend Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd ELECTIONSURVEY2
   ```

2. **Navigate to backend directory**
   ```bash
   cd backend
   ```

3. **Install dependencies**
   ```bash
   npm install
   ```

4. **Configure environment variables**
   
   Copy `.env.example` to `.env`:
   ```bash
   cp .env.example .env
   ```
   
   Edit `.env` file with your database credentials:
   ```env
   DB_HOST=localhost
   DB_USER=root
   DB_PASS=your_password
   DB_NAME=election_survey
   PORT=4000
   ```

5. **Setup MySQL Database**
   
   Run the database setup script:
   ```bash
   mysql -u root -p < database_setup.sql
   ```
   
   Or manually in MySQL/phpMyAdmin:
   ```sql
   CREATE DATABASE election_survey;
   USE election_survey;
   SOURCE database_setup.sql;
   ```
   
   The `database_setup.sql` file includes:
   - All table creation
   - Sample data (users, areas, wards, questions, options)

6. **Start the server**
   
   Development mode (with auto-reload):
   ```bash
   npm run dev
   ```
   
   Production mode:
   ```bash
   npm start
   ```
   
   Server will run on `http://localhost:4000`
   
   **Verify it's working:**
   ```bash
   curl http://localhost:4000
   ```
   Should return: `{"message":"Election Survey API is running"}`

### Android App Setup

1. **Open Android Studio**
   
   File → Open → Select the project directory

2. **⚠️ IMPORTANT: Update IP Address**
   
   **You MUST update the server IP address** in the following files before running:
   
   - `app/src/main/java/com/example/electionsurvey2/ApiService.java`
     - Line 23: Change `BASE_URL` to your backend server IP
     - Example: `public static final String BASE_URL = "http://YOUR_IP:4000";`
   
   - `app/src/main/java/com/example/electionsurvey2/LoginActivity.java`
     - Line 46: Change `BASE_URL` to your backend server IP
     - Example: `private static final String BASE_URL = "http://YOUR_IP:4000";`
   
   **To find your IP address:**
   - Windows: Run `ipconfig` in CMD and look for IPv4 Address
   - Mac/Linux: Run `ifconfig` or `ip addr` in terminal
   - Make sure your phone and computer are on the same WiFi network

3. **Sync Gradle**
   
   File → Sync Project with Gradle Files

4. **Build the project**
   
   Build → Clean Project  
   Build → Rebuild Project

5. **Run on device/emulator**
   
   Click Run ▶️ button

---

## 🗄️ Database Schema

### Users Table
```sql
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(15) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Areas Table
```sql
CREATE TABLE areas (
    id INT PRIMARY KEY AUTO_INCREMENT,
    area_name VARCHAR(100) NOT NULL
);
```

### Wards Table
```sql
CREATE TABLE wards (
    id INT PRIMARY KEY AUTO_INCREMENT,
    area_id INT NOT NULL,
    ward_name VARCHAR(100) NOT NULL,
    FOREIGN KEY (area_id) REFERENCES areas(id)
);
```

### Questions Table
```sql
CREATE TABLE questions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    survey_id INT NOT NULL,
    question_text TEXT NOT NULL,
    type ENUM('text', 'single', 'multiple') NOT NULL
);
```

### Options Table
```sql
CREATE TABLE options (
    id INT PRIMARY KEY AUTO_INCREMENT,
    question_id INT NOT NULL,
    option_text VARCHAR(255) NOT NULL,
    FOREIGN KEY (question_id) REFERENCES questions(id)
);
```

### Responses Table
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

## 🔌 API Endpoints

### Authentication

#### Login
```http
POST /api/login
Content-Type: application/json

Request:
{
  "phone": "1234567890",
  "password": "password123"
}

Response (200):
{
  "success": true,
  "data": {
    "id": 1,
    "name": "John Doe",
    "phone": "1234567890"
  }
}
```

### Areas

#### Get All Areas
```http
GET /api/areas

Response (200):
{
  "success": true,
  "data": [
    {"id": 1, "area_name": "Indira Nagar"},
    {"id": 2, "area_name": "Deopur"}
  ]
}
```

### Wards

#### Get Wards by Area
```http
GET /api/wards/:areaId

Response (200):
{
  "success": true,
  "data": [
    {"id": 1, "ward_name": "Indira Nagar Ward 1"},
    {"id": 2, "ward_name": "Indira Nagar Ward 2"}
  ]
}
```

### Survey

#### Get Survey Questions
```http
GET /api/surveys/:surveyId/questions

Response (200):
{
  "success": true,
  "data": [
    {
      "id": 1,
      "question_text": "Which party will win?",
      "type": "single",
      "options": [
        {"id": 10, "option_text": "BJP"},
        {"id": 11, "option_text": "NCP"}
      ]
    }
  ]
}
```

#### Submit Survey Responses
```http
POST /api/responses
Content-Type: application/json

Request:
{
  "user_id": 1,
  "survey_id": 1,
  "area_id": 2,
  "ward_id": 5,
  "answers": [
    {"question_id": 1, "selected_option_id": 10},
    {"question_id": 2, "selected_option_ids": [21, 22]},
    {"question_id": 3, "answer_text": "Good infrastructure"}
  ]
}

Response (201):
{
  "success": true,
  "message": "Responses saved"
}
```

---

## 📱 Android App Features

### 1. Login Screen
- Modern Material Design 3 UI
- Blue gradient header with logo
- Rounded input fields
- Form validation
- Error handling

### 2. Area Selection
- Dynamic list from API
- Modern card-based UI
- Icon circles with colored backgrounds
- Smooth animations
- Empty state handling

### 3. Ward Selection
- Filtered by selected area
- Similar modern design
- Back navigation enabled
- Loading states

### 4. Survey Questions
- Three question types supported:
  - **Text**: EditText input
  - **Single Choice**: Radio buttons
  - **Multiple Choice**: Checkboxes
- Card-based layout
- Question counter (X of Y)
- Input validation
- Next/Submit buttons

### 5. Success Screen
- Animated success icon
- Professional completion message
- Back to home button
- Smooth transitions

---

## 🎨 UI Design

### Color Scheme
- **Primary**: #0057B8 (Government Blue)
- **Primary Dark**: #003E87
- **Accent**: #FFC107 (Amber)
- **Background**: #F5F7FA
- **Success**: #4CAF50

### Design Features
- Material Design 3 components
- 16-20dp corner radius on cards
- 4-8dp elevation
- 60dp button heights
- 24dp padding
- Sans-serif-medium font

---

## 🧪 Testing

### Backend Testing

1. **Test Server Health**
   ```bash
   curl http://localhost:4000
   ```

2. **Test Login**
   ```bash
   curl -X POST http://localhost:4000/api/login \
     -H "Content-Type: application/json" \
     -d '{"phone":"1234567890","password":"password123"}'
   ```

3. **Test Areas**
   ```bash
   curl http://localhost:4000/api/areas
   ```

### Android Testing

1. **Login Flow**
   - Enter valid credentials
   - Verify navigation to Area Selection

2. **Area/Ward Selection**
   - Select an area
   - Verify wards load for that area
   - Select a ward

3. **Survey Flow**
   - Answer all questions
   - Submit survey
   - Verify success screen appears

4. **Error Handling**
   - Test with invalid credentials
   - Test with network disconnected
   - Test empty field validation

---

## 🐛 Troubleshooting

### Backend Issues

**Problem**: Cannot connect to database  
**Solution**: Check MySQL is running and credentials in `.env` are correct

**Problem**: Port 4000 already in use  
**Solution**: Change PORT in `.env` or stop the process using port 4000

### Android Issues

**Problem**: "Unable to connect to server"  
**Solution**: 
- Verify backend is running
- Check IP address in ApiService.java
- Ensure phone and laptop are on same WiFi

**Problem**: Build errors  
**Solution**:
- Sync Gradle
- Clean & Rebuild project
- Check all dependencies are installed

---

## 📂 Project Structure

```
ELECTIONSURVEY2/
├── backend/
│   ├── config/
│   │   └── db.js                 # Database configuration
│   ├── controllers/
│   │   ├── authController.js     # Login logic
│   │   ├── areaController.js     # Area logic
│   │   ├── wardController.js     # Ward logic
│   │   ├── surveyController.js   # Survey logic
│   │   └── responseController.js # Response logic
│   ├── models/
│   │   ├── userModel.js          # User queries
│   │   ├── areaModel.js          # Area queries
│   │   ├── wardModel.js          # Ward queries
│   │   └── surveyModel.js        # Survey queries
│   ├── routes/
│   │   ├── authRoutes.js         # Auth endpoints
│   │   ├── areaRoutes.js         # Area endpoints
│   │   ├── wardRoutes.js         # Ward endpoints
│   │   ├── surveyRoutes.js       # Survey endpoints
│   │   └── responseRoutes.js     # Response endpoints
│   ├── server.js                 # Main server file
│   ├── .env                      # Environment variables
│   └── package.json              # Dependencies
│
├── app/
│   ├── src/main/
│   │   ├── java/com/example/electionsurvey2/
│   │   │   ├── LoginActivity.java
│   │   │   ├── AreaSelectionActivity.java
│   │   │   ├── WardSelectionActivity.java
│   │   │   ├── SurveyActivity.java
│   │   │   ├── SuccessActivity.java
│   │   │   ├── ApiService.java
│   │   │   └── Models (Area, Ward, Question, Option, Answer)
│   │   │
│   │   ├── res/
│   │   │   ├── layout/           # XML layouts
│   │   │   ├── values/           # Colors, themes, strings
│   │   │   ├── drawable/         # Drawables and backgrounds
│   │   │   └── anim/             # Animations
│   │   │
│   │   └── AndroidManifest.xml
│   │
│   └── build.gradle              # App dependencies
│
└── README.md                     # This file
```

---

## 🔐 Security Considerations

⚠️ **Important**: This is a development version. For production:

1. **Backend**:
   - Use bcrypt for password hashing (already implemented)
   - Add JWT authentication
   - Enable HTTPS
   - Add rate limiting
   - Validate all inputs
   - Use prepared statements (already implemented)

2. **Android**:
   - Store sensitive data securely
   - Use HTTPS only
   - Implement certificate pinning
   - Obfuscate code with ProGuard

---

## 📝 Sample Data

### Create Test User
```sql
INSERT INTO users (name, phone, password_hash) 
VALUES ('Test User', '1234567890', 'password123');
```

### Add Areas
```sql
INSERT INTO areas (area_name) VALUES 
('Indira Nagar'), ('Deopur'), ('Rajendra Nagar');
```

### Add Wards
```sql
INSERT INTO wards (area_id, ward_name) VALUES 
(1, 'Indira Nagar Ward 1'),
(1, 'Indira Nagar Ward 2'),
(2, 'Deopur Ward 1');
```

### Add Survey Questions
```sql
INSERT INTO questions (survey_id, question_text, type) VALUES
(1, 'Which party will win?', 'single'),
(1, 'Major issues in your area', 'multiple'),
(1, 'Additional comments', 'text');
```

### Add Options
```sql
INSERT INTO options (question_id, option_text) VALUES
(1, 'BJP'), (1, 'NCP'), (1, 'Congress'),
(2, 'Water'), (2, 'Roads'), (2, 'Electricity');
```

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

---

## 📄 License

This project is licensed under the MIT License.

---

## 👥 Contact

For questions or support, please contact the development team.

---

## 🎉 Acknowledgments

- Material Design 3 by Google
- Express.js framework
- OkHttp library
- MySQL database

---

**Version**: 1.0.0  
**Last Updated**: 2025  
**Status**: ✅ Production Ready

---

## 📋 Quick Start Commands

```bash
# Backend
cd backend
npm install
npm run dev

# Android
# Open in Android Studio and click Run
```

---

Made with ❤️ for Election Survey System

