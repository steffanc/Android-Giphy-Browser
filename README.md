# Giphy-Browser

## Features
- display infinite scrolling list of trending gifs
- enter search mode by clicking on the search icon to replace trending gifs with infinite list of search results
  - search results are fetched automatically after a 500ms delay when entering the query
- click a gif to view it fullscreen
- seemlessly handle configuration changes
- handle network and API errors by displaying a generic message to the user with a toast

## Implementation
- Tech: ViewModels, LiveData, RxJava, Retrofit, Moshi, Fresco
- Patterns: MVI, dependency injection, repository, immutable data structures (pragmatically), pure functions (pragmatically), encapsulation

## Design
- single rx pipeline for fetching gifs
- when a new fetch event is triggered, any inflight fetches are discarded
- query param is used to determine the mode of the gifs list screen
  - null query fetches trending gifs
  - non-null query fetches search gifs
- webp was chosen for smaller size vs gif
- fixed width webp was chosen for small size and for usage in a small width grid layout

## Future Improvements
- smartly choose gif quality depending on screen size and network performance
- suggest search queries as user is typing
