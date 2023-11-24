
![Logo](https://ocw.cs.pub.ro/courses/_media/poo-ca-cd/teme/proiect/poo_baner_etapa_1.png?w=800&tok=241c41)


# GlobalWaves

Assignment Link: https://ocw.cs.pub.ro/courses/poo-ca-cd/teme/proiect/etapa1
## Overview
This project involves the implementation of an application with functionalities similar to Spotify, simulating various user actions. These actions will be simulated using commands received in input files. The perspective from which you'll be addressing this project is that of an admin, who observes all user actions and can generate various reports related to all users.
## Features

User Actions Simulation: Users' interactions with the application will be simulated through commands provided in input files(json).

Admin Perspective: The implementation focuses on the administrator's viewpoint, allowing for a comprehensive understanding of user activities.

Report Generation: The admin has the capability to generate diverse reports pertaining to all users. These reports can offer valuable insights into user behavior and usage patterns.
## How to Use
Input Files: Prepare input files containing commands that simulate user actions. These could include actions like song plays, playlist creations, etc.

Run the Application: Execute the application, and it will process the input files, simulating the specified user actions.

Generate Reports: Utilize the admin functionalities to generate reports based on user activities. These reports can be valuable for analyzing user engagement and application usage.


## Skel Structure
* src/
  * checker/ - checker files
  * fileio/ - contains classes used to read data from the json files
  * main/
      * Main - the Main class runs the checker on your implementation. Add the entry point to your implementation in it. Run Main to test your implementation from the IDE or from command line.
      * Test - run the main method from Test class with the name of the input file from the command line and the result will be written
        to the out.txt file. Thus, you can compare this result with ref.
      * InputProcessor - takes all the input commands and processes them to display the correct result.
  * command/
      * Commands - contains all the input commands.
      * Filters - contains the filters of the search.
  * media/ - contains all the entities(song, playlist, podcast,etc.)
  * player/ 
      * PlayerCommands - interface that contains all the player commands.
      * Player - abstract player containing general attributes like: isLoaded, timeUpdated (last timestamp the player has been updated). Holds a method to 
      * PlaylistPlayer - class that implements a playlist player.
      * PodcastPlayer - class that implements a podcast player.
      * SongPlayer - class that implements a music player.
      * Status - class that holds the status of the running player.
  * searchbar/
      * Search - abstract search
      * SearchPlaylist - class that deals with searching for playlists
      * SearchPodcast - class that deals with searching for podcasts
      * SearchSong - class that deals with searching for songs.
  * stats/
      * Statistics - SINGLETON class for statistics (Top 5 songs, etc.).
  * user/ - contains the user class.
* input/ - contains the tests and library in JSON format
* ref/ - contains all reference output for the tests in JSON format
## Implementation
### How It Works
Firstly, the program copies the library given as an input for future processing: 
SongInput -> Song,
PlaylistInput -> Playlist,
PodcastInput -> Podcast,
EpisodeInput -> Episode,
UserInput -> User.

Nextly, it iterates through the input commands and takes the current user. Creates a new inputProcessor and assuming each user has his individual search bar, player and status the inputProcessor class holds the reference to them. Depending on the command name, the switcher chooses what action to take (example: search, select, etc.) and calls the specific method from the inputProcessor.

#### Explaining Methods

#### InputProcessor
* search: Creates a new search(SearchPlaylist, SearchPodcast,etc.) based on the given type.
* load: Creates a new player depending on the type(SongPlayer, PlaylistPlayer,etc.).
* getTop5Playlists and getTop5Songs: Both methods get the instance of the Statistics(since is SINGLETON) and output the media list.
The rest of the methods call search/player related methods.

#### Search
* getSearchResultArray: Gets the result array by calling functions that return arrays of the media based on the given filter. Maximizes the result size to 5 or lower.
* select: Iterates through the specific file array and selects a media.

#### Player
* playPause: Sets the player status to paused or unpaused.
* repeat: Sets repeat status for specific media.
* shuffle: Shuffles the songs in the playlist by using the Collections.shuffle method.
* forward/backward: Moves through the episode with an amount of 90 seconds. At forwarding, it goes to the next episode if the current episode duration it's less than the amount.
* next/prev: It checks the current repeat status and based on that it goes to the next/prev file. For example, if the status is "No Repeat" and we reached the final file, for command next, it resets the status to the implicit values.
* like: Checks if the user has liked the song before. If case true, it removes the song from the liked songs list, otherwise it adds. The method updates the number of the likes for the specific song.
* addRemoveInPlaylist: Gets the playlist provided by the id and adds/removes the specific song from it.
* updateStatus: The method ends if the player is on paused. The timeElapsed attribute calculates the time passed since the last update. If the time elapsed is lower than the file remaining time, it literally subtracts the time elapsed from the player status remained time. Otherwise, checks the repeat status:
  * no repeat: (for podcast and playlist) calls a function that determines what is the current file that has been reached(getCurrent Song/Episode - subtracts the remaining time of the current file and then iterates through the next files UNTIL the time remaining is lower then the file duration)
  * repeat infinite/ current song: Calculates the time based on a particular formula.

#### Playlist
* createPlaylist: Checks if the playlist already exists. If not, creates a new playlist and puts it in the user playlist list and also in the global list of the playlists.
* switchVisibility: Gets the playlist based on the given id and updates the visibility status.
* follow: Checks if the user has followed the song before. If case true, it removes the playlist from the followed playlist list, otherwise it adds. The method updates the number of the followers for the specific playlist.
* showPlaylists: Lists the playlists of the current user.
### Technologies Used from Course
* Design Pattern - Singleton Class (Statistics)
* Lambda Expressions (SearchPlaylist line 36)
* Interface for Player
* Abstract Classes for Search/Player
## Authors

- [@robertenciu](https://www.github.com/robertenciu)

<div align="center"><img src="https://tenor.com/view/homework-time-gif-24854817.gif" width="500px"></div>