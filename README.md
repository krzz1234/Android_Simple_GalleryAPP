
# 📸 Android Simple Gallery App

A lightweight Android gallery application built with **Java** and **Android SDK**, designed to demonstrate how to load and display images from local storage **without using any third-party libraries** like Glide, Picasso, or Fresco.

---

## ✨ Features

- 📂 Loads and displays images from the device's local storage
- 🔍 Completely dependency-free (no Glide, Picasso, or Fresco)
- 📱 Clean and intuitive UI using only native Android components
- 🔄 Supports basic image gestures like rotation and scaling
- 🧠 Great for learning how gallery apps work under the hood

---

## 🚀 Getting Started

### Prerequisites

- Android Studio (Chipmunk or newer recommended)
- Android SDK version 21 or higher
- Java 8+

### Running the App

1. Clone the repository:
   ```bash
   git clone https://github.com/krzz1234/Android_Simple_GalleryAPP.git

2. Open the project in **Android Studio**

3. Connect a physical device or start an emulator

4. Build and run the app

---

## 📁 Project Structure

```
app/
├── java/
│   └── com.example.simplegallery/
│       ├── MainActivity.java
│       ├── ImageAdapter.java
│       └── utils/
│           └── ImageLoader.java
├── res/
│   ├── layout/
│   └── drawable/
└── AndroidManifest.xml
```

---

## 🛠️ Tech Stack

* **Language:** Java
* **Framework:** Android SDK
* **Architecture:** Activity-based with RecyclerView and custom adapters

---

## 📚 Learning Goals

This project was created to:

* Understand Android’s native image loading and decoding mechanisms
* Learn how to access and display images from external/local storage
* Build a fully functional gallery app without relying on external libraries
* Practice using `RecyclerView`, `Adapter`, and custom `ViewHolder` implementations

```

