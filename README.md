# WatchWise 

**WatchWise** is a modern Android movie discovery application that helps users explore, rate, and manage their favorite movies. Built with Kotlin and following clean architecture principles, it provides a seamless experience for movie enthusiasts.

## Features 

### Core Features
- **Movie Discovery**: Browse popular, top-rated, and upcoming movies
- **Search**: Find movies by title with real-time search results
- **Movie Details**: View comprehensive information including ratings, genres, descriptions, and trailers
- **User Reviews**: Rate movies (1-10 scale) and write reviews
- **Favorites**: Save movies to your personal favorites list
- **Authentication**: User registration and login with Firebase Auth

### User Interface
- **Clean Design**: Modern Material Design with gradient backgrounds
- **Responsive Layout**: Optimized for different screen sizes
- **Smooth Navigation**: Intuitive navigation between screens
- **Light Theme**: Gradient-based color scheme

## Screenshots 

<div align="center">
  <img src="https://github.com/user-attachments/assets/d468dbf7-ec0e-48d4-aeb7-eb0a700e70ca" width="200"/>
  <img src="https://github.com/user-attachments/assets/68291e6b-76fd-4dd8-b1fe-03d12313031a" width="200"/>
  <img src="https://github.com/user-attachments/assets/ba3cf59b-1de4-4b09-849c-64ba2d5ad0e2" width="200"/>
  <br>
  <img src="https://github.com/user-attachments/assets/1e279aa9-0074-4611-bac6-6a04c8d69508" width="200"/>
  <img src="https://github.com/user-attachments/assets/dfd27347-5824-4539-bb63-d83988586aaf" width="200"/>
  <img src="https://github.com/user-attachments/assets/38ae43e7-a486-4186-8b49-8e40e4a4a1de" width="200"/>
</div>

## Tech Stack 

### Architecture
- **Clean Architecture**: Separation of concerns with Domain, Data, and Presentation layers
- **MVVM Pattern**: Model-View-ViewModel architecture
- **Repository Pattern**: Abstraction layer for data sources

### Libraries & Frameworks
- **Kotlin**: Primary programming language
- **Android Jetpack**: 
  - Navigation Component
  - Lifecycle & ViewModel
  - View Binding
- **Dependency Injection**: Hilt (Dagger)
- **Networking**: Retrofit + OkHttp
- **Image Loading**: Glide
- **Database**: Firebase Firestore
- **Authentication**: Firebase Auth
- **Coroutines**: For asynchronous operations
- **API**: The Movie Database (TMDb) API

### Firebase Services
- **Authentication**: User login/registration
- **Firestore**: User reviews and ratings storage

## API Integration 

WatchWise uses [The Movie Database (TMDb) API](https://developers.themoviedb.org/3) to fetch:
- Popular movies
- Top-rated movies
- Upcoming movies
- Movie details and metadata
- Movie trailers and videos
- Search functionality

## Installation 

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK 24 or higher
- Firebase project setup

### Setup Instructions

1. **Clone the repository**
   ```bash
   git clone https://github.com/grimmatic/watchwise.git
   cd watchwise
   ```

2. **Configure Firebase**
   - Create a new Firebase project
   - Add your Android app to the project
   - Download `google-services.json` and place it in the `app/` directory
   - Enable Authentication and Firestore in Firebase Console

3. **Configure TMDb API**
   - Get your API key from [TMDb](https://developers.themoviedb.org/3)
   - Add your API key to `app/src/main/java/com/vakifbank/WatchWise/utils/Constant.kt`:
     ```kotlin
     const val TMDB_API_KEY = "your_api_key_here"
     ```

4. **Build and Run**
   - Open project in Android Studio
   - Sync project with Gradle files
   - Run the app on an emulator or physical device

## Project Structure 

```
app/
├── src/main/java/com/vakifbank/WatchWise/
│   ├── base/                 # Base classes
│   ├── data/                 # Data layer
│   │   ├── repository/       # Repository implementations
│   │   └── service/          # API service interfaces
│   ├── di/                   # Dependency injection modules
│   ├── domain/               # Domain layer
│   │   ├── model/            # Domain models
│   │   ├── repository/       # Repository interfaces
│   │   └── usecase/          # Use cases
│   ├── ui/                   # Presentation layer
│   │   ├── activity/         # Activities
│   │   ├── adapter/          # RecyclerView adapters
│   │   └── fragment/         # Fragments and ViewModels
│   └── utils/                # Utility classes
└── res/                      # Resources (layouts, drawables, etc.)
```

## Key Components 

### Data Layer
- **MovieRepository**: Handles movie data from TMDb API
- **FavoriteRepository**: Manages user's favorite movies in Firestore
- **ReviewRepository**: Handles movie reviews and ratings

### Domain Layer
- **Use Cases**: Business logic for each feature
- **Models**: Data classes representing entities
- **Repository Interfaces**: Contracts for data operations

### Presentation Layer
- **Fragments**: UI screens with ViewModels
- **Adapters**: RecyclerView adapters for lists
- **ViewModels**: State management and business logic

## Features in Detail 

### Movie Discovery
- Browse movies by category (Popular, Top Rated, Upcoming)
- Horizontal scrolling movie lists
- "See More" functionality for complete category viewing
- Movie poster loading with Glide

### Search Functionality
- Real-time search as you type
- Search results with movie posters, titles, and ratings
- Smooth transition between normal and search modes

### Movie Details
- Comprehensive movie information
- Trailer integration (YouTube)
- User rating system (1-10 stars)
- Review system with comments
- Add/remove from favorites

### User Management
- Firebase Authentication
- User registration and login
- Guest mode available
- Personalized favorites and reviews

## Contributing 

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License 

This project is licensed under the MIT License 

## Acknowledgments 

- [The Movie Database (TMDb)](https://www.themoviedb.org/) for providing the movie data API
- [Firebase](https://firebase.google.com/) for backend services
- [Material Design](https://material.io/design) for design guidelines
- All contributors and testers

## Contact 

For any questions or suggestions, please feel free to reach out:

- **Email**: sevbansaricicek@gmail.com
- **GitHub**: [@grimmatic](https://github.com/grimmatic)
- **LinkedIn**: [sevbansaricicek](https://linkedin.com/in/sevbansaricicek)

---

