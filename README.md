# Sangeet — Music Streaming App (Guvi Hackathon 2025)

Sangeet is a Java-based music streaming app with a modern, Spotify-inspired UI.  
Listeners can play songs and follow artists, artists can upload and manage their music,  
and admins can oversee all content. Built using JDBC + MySQL, the app includes  
role-based dashboards, smooth multithreaded audio playback, and a clean dark theme.

---

## 🚀 Features

### 🎧 Listener
- Browse and play all songs  
- Follow / Unfollow artists  
- View complete artist list  
- See latest song collection  

### 🎤 Artist
- Upload new songs  
- Delete previously uploaded songs  
- View total follower count  

### 🛠 Admin
- View all artists  
- Delete any artist  
- Automatically remove songs & followers of deleted artist  

---

## 🧱 Tech Stack
- Java  
- Swing (FlatLaf Theme)  
- JDBC  
- MySQL  
- VLCJ (Audio Playback)  
- Multithreading (PlayerThread)

---

## ⚙️ Setup Instructions

### 1️⃣ Configure MySQL (Required)
Sangeet uses a **local MySQL server**.

Update your database credentials in:

`src/com/Sangeet/dao/DBConnection.java`

```java
private static final String URL = "jdbc:mysql://localhost:3306/sangeet";
private static final String USER = "root";
private static final String PASS = "your_password";
