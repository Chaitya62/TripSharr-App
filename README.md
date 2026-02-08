# TripSharr

TripSharr is an Android application that lets users create, share, and discover travel trips. Think of it as a social platform for trips — users can plan routes with checkpoints, track ongoing trips in real-time on a map, attach photos and videos, and share their journeys publicly for others to star and fork.

## Features

- **Trip Creation & Management** — Create trips with names, descriptions, and multiple waypoints/checkpoints. Edit or delete trips as needed.
- **Real-Time Trip Tracking** — Track ongoing trips with live map updates and add checkpoints on the fly.
- **Media Attachments** — Attach photos and videos to checkpoints via Cloudinary integration.
- **Social Features** — Star and fork public trips (similar to GitHub repos). Browse a public feed of trips.
- **User Profiles** — View user profiles with trip statistics (stars, forks, trip count).
- **Google Maps Integration** — Visualize trip routes and checkpoints on interactive maps.
- **Timeline View** — View trip progress as a timeline of checkpoints.
- **Facebook Login** — Authenticate using your Facebook account.
- **Offline Caching** — Local SQLite database caches trip and user data for offline access.

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Platform | Android (Java) |
| Min SDK | 21 (Android 5.0) |
| Target SDK | 25 (Android 7.1) |
| Build System | Gradle 2.3.3 |
| Networking | Volley 1.0.0 |
| Maps | Google Play Services Maps & Location 11.0.4 |
| Auth | Facebook SDK 4.0.0 |
| Media Hosting | Cloudinary Android SDK 1.13.0 |
| Animations | Lottie 2.2.0 |
| UI | Material Design, CardView, RecyclerView, TimelineView |
| Local DB | SQLite |
| Backend | [TripSharr-Server](https://github.com/Chaitya62/TripSharr-Server) (PHP REST API) |

## Project Structure

```
app/src/main/java/io/github/chaitya62/tripsharr/
├── MainActivity.java              # Login screen (Facebook auth)
├── FeedsActivity.java             # Public trip feed
├── HomeActivity.java              # Home screen
├── CreateTripActivity.java        # Create a new trip
├── EditTripActivity.java          # Edit existing trip
├── ViewTripActivity.java          # View trip details
├── ViewTripTimeLine.java          # Timeline visualization
├── MapsActivity.java              # Map display
├── ProfileActivity.java          # User profile
├── ShareTripActivity.java         # Share trips
├── NavigationActivity.java        # Base activity with drawer nav
│
├── ongoingtrips/                  # Real-time trip tracking
│   ├── OnGoingTripActivity.java
│   ├── OngoingMapActivity.java
│   ├── CheckpointActivity.java
│   ├── AddCheckpointActivity.java
│   ├── EditCheckpointActivity.java
│   └── MediaCollectionActivity.java
│
├── primeobjects/                  # Data models
│   ├── Trip.java
│   ├── User.java
│   ├── Coordinates.java
│   └── Media.java
│
├── localDB/                       # SQLite database helpers
│   ├── TripDB.java
│   ├── UserDB.java
│   └── CoordinatesDB.java
│
├── adapters/                      # RecyclerView adapters
│   ├── FeedAdapter.java
│   ├── TripAdapter.java
│   ├── ProfileFeedAdapter.java
│   └── ...
│
├── fragments/                     # UI fragments
│   ├── fragmentOne.java
│   └── fragmentTwo.java
│
└── utils/                         # Utilities
    ├── SharedPrefs.java
    ├── NetworkUtils.java
    ├── VolleySingleton.java
    ├── HandleStarFork.java
    └── ...
```

## Getting Started

### Prerequisites

- Android Studio
- JDK 1.8+
- Android SDK (API 21–25)

### Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/Chaitya62/TripSharr-App.git
   cd TripSharr-App
   ```

2. **Open in Android Studio**
   - File → Open → select the `TripSharr-App` directory
   - Let Gradle sync complete

3. **Configure API keys** (in `gradle.properties`)
   - Google Maps API key is pre-configured
   - Facebook App ID is set in `app/src/main/res/values/strings.xml`

4. **Run the app**
   - Connect an Android device or start an emulator (API 21+)
   - Run → Run 'app'

### Build from command line

```bash
# Debug build
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug

# Run tests
./gradlew test
```

## Permissions

The app requires the following permissions:

- `INTERNET` / `ACCESS_NETWORK_STATE` — API communication
- `ACCESS_FINE_LOCATION` / `ACCESS_COARSE_LOCATION` — GPS and map features
- `READ_EXTERNAL_STORAGE` / `WRITE_EXTERNAL_STORAGE` — Media file access

## Backend

The app communicates with a PHP REST API. The server source code is available at:
[TripSharr-Server](https://github.com/Chaitya62/TripSharr-Server)

## License

This project is licensed under the Apache License 2.0 — see the [LICENSE](LICENSE) file for details.
