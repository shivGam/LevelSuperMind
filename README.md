# LevelSuperMind Android Assignment: Audio Player App

## Introduction

This is an Android Audio and Video Player App built using **Jetpack Compose**. The app allows users to play, pause, download, and manage audio files for both online and offline usage. The project follows **MVVM** and **Clean Architecture** principles to ensure modularity, readability, and testability of the code.

---

## Features

- **Play/Pause Button**: Controls the playback of audio.
- **Progress Bar**: Shows the current playback position and total duration of the audio.
- **Download Button**: Allows users to download audio files for offline access.
- **Offline Playback**: Enables playing downloaded audio even without an internet connection.
- **Persistent Storage**: Uses Room Database to store downloaded file information and playback progress.
- **Background Downloading**: Utilizes WorkManager to handle downloads in the background.
- **Jetpack Compose UI**: Modern, declarative UI design for seamless user experience.

---

## Architecture

The project follows **MVVM (Model-View-ViewModel)** and **Clean Architecture** to maintain a clean separation of concerns. The architecture ensures that:

- **UI (View)** is separated from business logic (ViewModel).
- **Data** is managed and stored in the Model layer.
- The app is modular and easy to maintain, test, and extend.

---

## Libraries and Dependencies

### Core Libraries

- **Jetpack Compose**: Declarative UI framework for modern Android apps.
  - `androidx.compose.ui`

- **Lifecycle & ViewModel**: For managing UI-related data in a lifecycle-conscious way.
  - `androidx.lifecycle:lifecycle-viewmodel-ktx`
  - `androidx.lifecycle.runtime.ktx`
  - `androidx.compose.runtime:runtime-livedata`

### Data Persistence

- **Room Database**: For storing downloaded files and playback progress.
  - `androidx.room:room-runtime`
  - `androidx.room:room-ktx`
  - `kapt androidx.room:room-compiler`

### Background Tasks

- **WorkManager**: For handling background download tasks.
  - `androidx.work:work-runtime-ktx`

### Networking

- **Retrofit**: For handling network requests (if needed).
  - `com.squareup.retrofit2:retrofit`
  - `com.squareup.retrofit2:converter-gson`

### Image Loading

- **Coil**: For image loading (optional).
  - `io.coil-kt:coil-compose`

---

## Setup and Installation

1. **Clone the Repository**:

   ```bash
   git clone https://github.com/shivGam/LevelSuperMind.git
   ```

2. **Open in Android Studio**.

3. **Sync Gradle Files**.

4. **Build and Run** the app on an emulator or physical device.

---

## How the App Works

1. **Playback Control**:
   - The `PlayPauseButton` component interacts with ExoPlayer to control playback.

2. **Progress Bar**:
   - The progress bar reflects the current playback position and duration fetched from ExoPlayer.

3. **Download Functionality**:
   - Users can tap the `DownloadButton` to download audio files.
   - Downloads are managed using WorkManager to ensure background execution.
   - Downloaded files are saved locally and tracked in the Room Database.

4. **Offline Playback**:
   - The app checks for internet availability.
   - If offline, the app fetches and plays the audio files from local storage.
