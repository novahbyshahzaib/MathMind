# NovahMathMind 🧠

A brain-training Android app built with **Kotlin** and **Jetpack Compose**. NovahMathMind features three native games and a hidden Developer Mode for injecting custom HTML games.

---

## 🎮 Games

### 1. Math Challenge
Test your arithmetic skills under pressure! Choose from three difficulty levels:
- **Easy** — Single-digit operations (+, −, ×, ÷)
- **Medium** — Double-digit operations
- **Hard** — Mixed multi-digit operations with complex division/multiplication

Race against a **60-second timer** to answer as many questions as possible. Division questions always produce clean integer results. View your final score, accuracy, and play again.

### 2. Sudoku
A full **9×9 Sudoku** puzzle with:
- Puzzles generated using a **backtracking algorithm**
- **Real-time conflict highlighting** — conflicting cells in the same row, column, or 3×3 subgrid glow red
- **Check Solution** to verify your answers
- **Auto-Solve** to see the complete solution
- **New Game** to generate a fresh puzzle

### 3. Reaction Time
Test your reflexes:
1. The screen starts **Red** with "Wait for Green..."
2. After a random delay (1.5–5 seconds), it turns **Green** with "TAP NOW!"
3. Tap as fast as you can — your reaction time is measured in milliseconds
4. If you tap too early (while red), you'll see "Too early!" and must restart
5. Your **best time** is saved locally and displayed on screen

---

## ⚙️ Settings & Easter Egg

The Settings page displays app information. At the bottom, you'll see the text **"Coded by novah"**.

### 🥚 Unlocking Developer Mode
1. Tap **"Coded by novah"** **5 times within 2 seconds**
2. A passcode dialog will appear
3. Enter exactly: `NOVAH HOST`
4. Developer Options will be unlocked and persisted across app restarts

### 🛠️ Developer Tools: Custom HTML Games
Once Developer Mode is unlocked:
1. Go to **Settings → Add Custom Game**
2. Enter a **Game Title** and paste raw **HTML**, **CSS**, and **JavaScript**
3. Save the game — it appears on the **Homepage** alongside the native games
4. Tapping the custom game opens a **full-screen WebView** that renders your HTML/CSS/JS

---

## 📦 Installation

### Option A: Download the Pre-Built APK (Recommended)

1. Go to the [**Actions** tab](../../actions) of this repository
2. Click on the latest successful **"Build NovahMathMind APK"** workflow run
3. Scroll to the **Artifacts** section at the bottom
4. Download **NovahMathMind-APK**
5. Extract the ZIP file to get the `.apk`

#### Installing the APK on Your Android Device

1. **Transfer the APK** to your Android device (via USB, email, cloud drive, etc.)
2. On your device, go to **Settings → Security** (or **Settings → Apps & notifications → Special app access**)
3. Enable **"Install unknown apps"** or **"Unknown sources"** for your file manager or browser
4. Open the `.apk` file on your device
5. Tap **Install** when prompted
6. Once installed, open **NovahMathMind** from your app drawer

> **Note:** On Android 8.0+, you grant install permission per-app. You may need to allow your file manager to install apps.

### Option B: Build from Source

#### Prerequisites
- **Android Studio** Ladybug (2024.2) or newer
- **JDK 17** or higher
- **Android SDK** with API level 35

#### Steps

1. **Clone the repository:**
   ```bash
   git clone https://github.com/novahbyshahzaib/MathMind.git
   cd MathMind
   ```

2. **Open in Android Studio:**
   - Open Android Studio
   - Select **File → Open** and navigate to the cloned directory
   - Wait for Gradle sync to complete

3. **Build the debug APK:**
   ```bash
   chmod +x gradlew
   ./gradlew assembleDebug
   ```

4. **Find the APK at:**
   ```
   app/build/outputs/apk/debug/app-debug.apk
   ```

5. **Install on a connected device:**
   ```bash
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```

---

## 🏗️ Project Structure

```
NovahMathMind/
├── .github/workflows/build.yml      # CI/CD: builds APK on push/PR
├── app/
│   ├── build.gradle.kts              # App dependencies & config
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── res/                      # Resources (icons, themes, strings)
│       └── java/com/novah/mathmind/
│           ├── MainActivity.kt       # App entry point
│           ├── data/
│           │   ├── AppDatabase.kt    # Room database singleton
│           │   ├── CustomGame.kt     # Entity for custom HTML games
│           │   └── CustomGameDao.kt  # DAO for CRUD operations
│           └── ui/
│               ├── theme/Theme.kt    # Material3 theme
│               ├── navigation/NavGraph.kt  # Navigation routes
│               └── screens/
│                   ├── HomeScreen.kt           # Dashboard with game grid
│                   ├── MathChallengeScreen.kt  # Difficulty selector
│                   ├── MathGameScreen.kt       # Math game with timer
│                   ├── SudokuScreen.kt         # Sudoku with solver
│                   ├── ReactionTimeScreen.kt   # Reflex tester
│                   ├── SettingsScreen.kt       # Settings + Easter Egg
│                   ├── AddCustomGameScreen.kt  # HTML game editor
│                   └── CustomGamePlayerScreen.kt # WebView player
├── build.gradle.kts                  # Root Gradle config
├── settings.gradle.kts               # Project settings
└── gradle/wrapper/                   # Gradle wrapper
```

---

## 🔧 Tech Stack

| Technology | Purpose |
|---|---|
| **Kotlin** | Primary language |
| **Jetpack Compose** | Declarative UI framework |
| **Material 3** | Modern design system |
| **Navigation Compose** | Screen navigation |
| **Room Database** | Local storage for custom games |
| **SharedPreferences** | Reaction time best score & dev mode state |
| **WebView** | Rendering custom HTML/CSS/JS games |
| **GitHub Actions** | CI/CD for automated APK builds |

---

## 🚀 CI/CD

Every push or pull request to `main` triggers the GitHub Actions workflow:

1. Checks out the code
2. Sets up JDK 17 (Temurin)
3. Grants Gradle wrapper permissions
4. Runs `./gradlew assembleDebug`
5. Uploads the APK as artifact **"NovahMathMind-APK"**

---

## 📄 License

This project is provided as-is for educational and personal use.