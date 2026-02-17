# Kanaz Gallery

[![Android Build](https://github.com/GoKanaz/KanazGallery/actions/workflows/android_build.yml/badge.svg)](https://github.com/GoKanaz/KanazGallery/actions/workflows/android_build.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![API](https://img.shields.io/badge/API-21%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=21)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.20-blue.svg)](https://kotlinlang.org)
[![Material Design](https://img.shields.io/badge/Material-3-6200EE.svg)](https://material.io)

A modern, feature-rich gallery application for Android built with Kotlin and Material Design 3.

## Features

- 📸 View all photos and videos from device storage
- 📁 Automatic album organization by folders
- ⭐ Favorite your best memories
- 🔍 Search and filter media
- 🎯 Multi-select for batch operations
- 📤 Share media to other apps
- 🗑️ Delete unwanted media
- 🖼️ Fullscreen view with pinch-to-zoom
- 📽️ Video player with controls
- 🎪 Slideshow mode with adjustable speed
- 🌓 Dark/Light theme support
- 🚀 Smooth animations and transitions

## Screenshots

| Gallery View | Album View | Fullscreen |
|:------------:|:----------:|:----------:|
| ![Gallery](screenshots/gallery.png) | ![Albums](screenshots/albums.png) | ![Fullscreen](screenshots/fullscreen.png) |

## Technology Stack

- **Language:** Kotlin
- **Minimum SDK:** API 21 (Android 5.0)
- **Target SDK:** API 34 (Android 14)
- **Architecture:** MVVM
- **Libraries:**
  - AndroidX Core, AppCompat, ConstraintLayout
  - Material Design 3
  - Lifecycle (ViewModel, LiveData)
  - RecyclerView, ViewPager2
  - Navigation Component
  - Glide for image loading
  - ExifInterface for metadata

## Installation

### From Releases
1. Go to [Releases](https://github.com/GoKanaz/KanazGallery/releases)
2. Download the latest APK
3. Install on your Android device

### Build from Source

**Prerequisites:**
- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17
- Android SDK with API level 34

**Steps:**
```bash
git clone https://github.com/GoKanaz/KanazGallery.git
cd KanazGallery
./gradlew build
```

To generate APK:

```bash
./gradlew assembleDebug
```

APK location: app/build/outputs/apk/debug/

Project Structure

```
KanazGallery/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/gokanaz/kanazgallery/
│   │   │   │   ├── activities/      # Activity classes
│   │   │   │   ├── fragments/       # Fragment classes
│   │   │   │   ├── adapters/        # RecyclerView adapters
│   │   │   │   ├── models/          # Data models
│   │   │   │   ├── viewmodels/      # ViewModels
│   │   │   │   ├── utils/           # Utility classes
│   │   │   │   └── constants/       # Constants
│   │   │   └── res/                 # Resources
│   │   ├── androidTest/              # Instrumentation tests
│   │   └── test/                     # Unit tests
│   └── build.gradle                  # App-level build config
├── .github/                           # GitHub configuration
├── gradle/                            # Gradle wrapper
└── build.gradle                       # Project-level build config
```

Contributing

Contributions are welcome! Please read our Contributing Guidelines first.

1. Fork the repository
2. Create your feature branch (git checkout -b feature/AmazingFeature)
3. Commit your changes (git commit -m 'Add some AmazingFeature')
4. Push to the branch (git push origin feature/AmazingFeature)
5. Open a Pull Request

License

This project is licensed under the MIT License - see the LICENSE file for details.

Author

GoKanaz

· GitHub: @GoKanaz

Support

· 📧 Email: gokanaz@example.com
· 🐛 Issues: GitHub Issues

Acknowledgments

· Material Design team for the amazing design system
· All contributors and users of this app
· Open source community for the wonderful libraries

Changelog

Version 1.0.0 (2024-01-15)

· Initial release
· Basic gallery functionality
· Album view
· Favorites
· Dark/Light theme
  EOF

cat > LICENSE << 'EOF'
MIT License

Copyright (c) 2024 GoKanaz

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
