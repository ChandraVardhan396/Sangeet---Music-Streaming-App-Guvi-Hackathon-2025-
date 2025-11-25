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
private static final String HOST = "localhost";
    private static final String PORT = "3306";
    private static final String NAME = "sangeet";
    private static final String USER = "root";
    private static final String PASS = "password";


```

### Create Database Schema (Run Before Starting App)
```sql
CREATE DATABASE sangeet;
USE sangeet;

CREATE TABLE listeners (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    display_name VARCHAR(255) NOT NULL
);


CREATE TABLE artists (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    display_name VARCHAR(255) NOT NULL,
    followers INT DEFAULT 0
);


CREATE TABLE admins (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    display_name VARCHAR(255) NOT NULL
);


CREATE TABLE songs (
    id INT PRIMARY KEY AUTO_INCREMENT,
    artist_id INT,
    title VARCHAR(255) NOT NULL,
    path VARCHAR(1024) NOT NULL,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (artist_id) REFERENCES artists(id) ON DELETE CASCADE
);


CREATE TABLE followers (
    listener_id INT,
    artist_id INT,
    PRIMARY KEY (listener_id, artist_id),
    FOREIGN KEY (listener_id) REFERENCES listeners(id) ON DELETE CASCADE,
    FOREIGN KEY (artist_id) REFERENCES artists(id) ON DELETE CASCADE
);


```
### Place cover images inside:

```
src/com/Sangeet/assets/covers/

Naming must match the song ID:

1.jpg
2.jpg
3.jpg
...

```

### Run the Application

- Import the project into IntelliJ IDEA
- Configure MySQL credentials inside:
```
src/com/Sangeet/dao/DBConnection.java
```
- Run the SQL schema provided above
- Build & run Main.java
- Sign up and Log in as Listener, Artist, or Admin.

### Sangeet is a complete Java-based music streaming platform featuring:

- Clean, modern, Spotify-inspired UI
- Role-based navigation (Listener / Artist / Admin)
- Song upload, playback, and follow system
- Full MySQL database backend
- Smooth VLCJ audio playback with multithreading



