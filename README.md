# 📚 LearnKeep – Smart Knowledge Tracking Android App

LearnKeep is a modern Android application built using **Java** and **Room Database** that helps users capture, organize, and manage their daily learning efficiently.

The app works completely offline and allows users to store topics, attach files, track confidence levels, and manage learning resources in a clean and structured way.

---

## 🚀 Features

### 📌 Topic Management
- Add, edit, and delete topics
- Real-time search functionality
- Clean and minimal UI

### 📎 Attachments Support
- 📷 Capture images using Camera
- 🖼 Select images from Gallery
- 📄 Attach PDF and other files
- Auto-delete files when removed
- Fullscreen image preview
- Physical file cleanup when topic is deleted

### 🎯 Confidence Tracking
- Select confidence level from 1–10
- Color-coded circular selector (Red → Green)
- Stored per topic

### 🏷 Tag System
- Single tag per topic
- Tag-based automatic icon selection
- Editable tag support

### 📺 Multiple YouTube Links
- Add multiple YouTube links per topic
- Open all saved videos easily
- Clickable links inside topic details

### 🔎 Smart Search
- Real-time filtering
- Search by title or tag

### 🌞 UI Behavior
- App always runs in Light Mode
- Rounded search bar
- Modern Material UI design

### 💾 Offline Storage
- Room Database (SQLite)
- Internal File Storage
- No internet required

---

## 🛠 Tech Stack

- **Language:** Java
- **Database:** Room (SQLite)
- **UI:** XML + Material Components
- **Storage:** Internal File System
- **Architecture:** Activity-based structure

---

## 📂 Project Structure

```
com.example.learnkeep
│
├── MainActivity.java
├── AddKnowledgeActivity.java
├── TopicDetailsActivity.java
├── TopicAdapter.java
├── AttachmentAdapter.java
├── TopicIconHelper.java
├── AppDatabase.java
├── KnowledgeEntity.java
└── KnowledgeDao.java
```

---

# 📥 How To Clone & Run This Project

## 🔹 1️⃣ Install Requirements

Make sure you have:

- Android Studio (Latest Version)
- Git installed
- Android SDK installed via Android Studio

---

## 🔹 2️⃣ Clone the Repository

Open terminal / command prompt:

```bash
git clone https://github.com/YOUR_USERNAME/LearnKeep.git
```

Replace `YOUR_USERNAME` with your GitHub username.

---

## 🔹 3️⃣ Open Project in Android Studio

1. Open Android Studio
2. Click **Open**
3. Select the cloned `LearnKeep` folder
4. Wait for Gradle Sync to complete

---

## 🔹 4️⃣ Install Missing SDK (If Prompted)

If Android Studio asks to install SDK components:
- Click **Install**
- Wait until setup completes

---

## 🔹 5️⃣ Run the App

- Connect a physical Android device  
  OR  
- Create and start an Emulator  

Then click:

▶️ **Run App**

---

# 🧪 Database Information

This app uses **Room Database** for local storage.

If you modify the database schema:
- Increase the database version number
- OR clear app data

---

# 📸 Screenshots (Optional)

You can add screenshots like this:

```
screenshots/
├── main_screen.png
├── add_topic.png
└── edit_topic.png
```

Add inside README:

```markdown
![Main Screen](screenshots/main_screen.png)
```

---

# 🔮 Future Improvements

- Dark mode toggle
- Cloud backup (Firebase)
- User authentication
- Analytics dashboard
- MVVM architecture refactor
- Play Store release

---

# 👨‍💻 Author

**Jitesh Choudhary**  
Android Developer | Java | Room | UI/UX Enthusiast  

---

# ⭐ Support

If you like this project, consider giving it a ⭐ on GitHub.
