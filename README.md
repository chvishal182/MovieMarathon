Movie Marathon is a dynamic and responsive Android application designed for users 
to effortlessly discover and browse an extensive library of movies.
Built with modern Android development practices, it offers a smooth user experience with features like 
infinite scrolling, real-time network status indication, and robust error handling. 
The app fetches movie data from The Movie Database (TMDB) API.

## Features

*   **Dynamic Movie Listing:** Displays movies in a continuously scrollable list using the Paging 3 library.
*   **Infinite Scrolling:** Automatically loads more movies as the user scrolls down.
*   **Real-time Internet Status Signifier:**
    *   A persistent icon visually indicates the device's current internet connectivity status.
    *   The icon is expandable on click to provide users with a clear message about potential connectivity issues.
*   **Comprehensive Loading & Error States:**
    *   Clear visual feedback for initial data loading.
    *   Loading indicators when fetching subsequent pages of movies.
    *   Inline error messages with a "Retry" option if loading a page fails.
*   **Swipe-to-Refresh:** Allows users to easily refresh the movie list with a simple swipe gesture.
    *   Includes visual feedback (dimming content) during the refresh process.
*   **User-Friendly Feedback:**
    *   Toast messages for actions like successful refresh ("Fresh movies brewed!") or specific append errors.
    *   Handles empty states gracefully if no movies are available.
*   **Efficient Data Handling:** Optimized for performance and reduced memory usage when dealing with large datasets.

## Screenshots & Features Showcase

This section highlights some of the key user interface elements and features of Movie Marathon.

**1. Initial Load & Main Display**
<table width="100%">
  <tr>
    <td align="center" valign="top">
      <img src="https://github.com/user-attachments/assets/e586328f-949b-4ecf-971e-1e327963168b" alt="Primary Loading Screen" width="250"/>
      <br />
      <em>App prominently displays a loading indicator during the initial data fetch.</em>
    </td>
    <td align="center" valign="top">
      <img src="https://github.com/user-attachments/assets/335cd732-1e68-4a10-9ee6-08ed7eb1458c" alt="Main Movie List Page" width="250"/>
      <br />
      <em>Movies are displayed in a clean, scrollable list once data is available.</em>
    </td>
  </tr>
</table>

**2. Swipe-to-Refresh & Feedback**
Users can easily update the movie list, with clear visual cues during the process:
<table width="100%">
  <tr>
    <td align="center" valign="top">
      <img src="https://github.com/user-attachments/assets/b04f3b9c-09d6-42ac-973d-66dd6547105e" alt="Reloading Movie List Page" width="250"/>
      <br />
      <em>Visual feedback (dimmed content) while the movie list is being refreshed.</em>
    </td>
    <td align="center" valign="top">
      <img src="https://github.com/user-attachments/assets/5184e8d4-e10a-4d6b-af05-a9b760e1c637" alt="Toast after a successful reload" width="250"/>
      <br />
      <em>A confirmation Toast message ("Fresh movies brewed!") appears after a successful refresh.</em>
    </td>
  </tr>
</table>

**3. Incremental Loading & Error Handling**
The app seamlessly handles loading more data and potential errors during scrolling:
<table width="100%">
  <tr>
    <td align="center" valign="top">
      <img src="https://github.com/user-attachments/assets/d6e08960-a42c-4a52-bd41-dbe493488297" alt="Loading more movies indicator" width="250"/>
      <br />
      <em>A subtle loading indicator at the list's end signals more movies are being fetched on scroll.</em>
    </td>
    <td align="center" valign="top">
      <img src="https://github.com/user-attachments/assets/da2cc5f9-e885-4c6a-b87e-e026358ffdfb" alt="Retry button with error message" width="250"/>
      <br />
      <em>Handles load failures gracefully by showing an error message and a retry option.</em>
    </td>
  </tr>
</table>


**4. Real-time Internet Status**

Provides clear, persistent feedback about network connectivity:
<table width="100%">
  <tr>
    <td align="center" valign="top">
      <img src="https://github.com/user-attachments/assets/c8a7cbd3-677f-4453-bf66-b42d08a26023" alt="Internet Status Signifier Icon" width="250"/>
      <br />
      <em>Icon indicating network is offline.</em>
    </td>
    <td align="center" valign="top">
      <img src="https://github.com/user-attachments/assets/1183490e-04b6-4817-b0fb-9542773c7b77" alt="Expanded Network Status Message" width="250"/>
      <br />
      <em>Expanded message on tapping the icon.</em>
    </td>
  </tr>
</table>




## Technologies & Architecture

This project is built using **Java** and follows the **MVVM (Model-View-ViewModel)** architecture pattern. Key technologies and components include:

*   **Android Jetpack:**
    *   **ViewModel:** Manages UI-related data in a lifecycle-conscious way.
    *   **LiveData:** Observes data changes for reactive UI updates (e.g., movie data, network status).
    *   **Paging 3 Library:** For efficient pagination of movie data from the TMDB API.
        *   `PagingSource`: Defines data loading logic.
        *   `Pager`: Configures and creates the `PagingData` stream.
        *   `PagingDataAdapter`: Binds paged data to `RecyclerView`.
        *   `LoadStateAdapter`: Displays loading/error states in the list.
    *   **View Binding:** Provides type-safe access to views.
*   **Networking:**
    *   **Retrofit 2:** A type-safe HTTP client for Android and Java to consume the TMDB API.
    *   **Gson:** For parsing JSON responses from the API into Java objects.
    *   **RxJava 3:** Used with Retrofit for handling asynchronous network operations and managing data streams.
*   **Dependency Injection:**
    *   **Hilt:** For managing dependencies and simplifying dependency provisioning throughout the application.
*   **UI Components:**
    *   **RecyclerView:** For displaying the scrollable list of movies.
    *   **SwipeRefreshLayout:** For implementing pull-to-refresh functionality.
    *   **ConstraintLayout:** For building flexible and responsive UIs.
*   **Asynchronous Programming:**
    *   **RxJava 3:** For background network calls and data processing.
    *   **Coroutines (via Paging 3):** Paging 3 internally uses coroutines, and `cachedIn` leverages a `CoroutineScope`.
*   **System Services:**
    *   **ConnectivityManager & NetworkCallback:** Used in `NetworkStatusLiveData` to monitor real-time device network connectivity.

 ## Project Structure

The project follows the MVVM (Model-View-ViewModel)  architecture and is organized into the following main packages and directories:
```
 MovieMarathonApp/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/yourdomain/moviemarathon/
│   │   │   │   ├── activities/             # UI Controllers (MainActivity)
│   │   │   │   ├── adapters/               # RecyclerView Adapters (MoviesAdapter, MoviesLoadStateAdapter)
│   │   │   │   ├── data/
│   │   │   │   │   ├── model/              # Data models/POJOs (Movie.java, MovieResponse.java)
│   │   │   │   │   └── network/            # Networking (TMDB_API_Interface, MoviePagingSource, NetworkModule)
│   │   │   │   ├── di/                     # Dependency Injection (Hilt Modules)
│   │   │   │   ├── ui/
│   │   │   │   │   └── viewmodels/         # ViewModels (MovieViewModel)
│   │   │   │   ├── utils/                  # Utility classes (NetworkStatusLiveData, Constants)
│   │   │   │   └── MovieMarathonApplication.java # Application class for Hilt
│   │   │   │
│   │   │   ├── res/
│   │   │   │   ├── layout/                 # XML layouts for activities and RecyclerView items
│   │   │   │   ├── drawable/               # Images and custom drawables
│   │   │   │   ├── values/                 # Strings, colors, themes, dimensions
│   │   │   │   └── ...
│   │   │   │
│   │   │   └── AndroidManifest.xml
│   │   │
│   │   ├── test/                         # Unit tests
│   │   └── androidTest/                  # Instrumentation (UI) tests
│   │
│   └── build.gradle                      # App-level Gradle build script
│
└── build.gradle                          # Project-level Gradle build script
```

## Code Highlights

*   **`NetworkStatusLiveData`:** A custom `LiveData` subclass that observes and broadcasts the device's internet connectivity status, enabling the global "Internet Status Signifier" feature.
*   **`MoviePagingSource`:** Implements the logic to fetch movie data page by page from the TMDB API using Retrofit and RxJava.
*   **`MoviesLoadStateAdapter`:** Provides a user-friendly way to display loading progress, errors, and retry actions directly within the `RecyclerView` footer.
*   **MVVM Implementation:** Clear separation between UI (`MainActivity`), UI logic/state (`MovieViewModel`), and data sources (Repository pattern implicitly used by ViewModel with PagingSource).
