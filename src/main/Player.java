package main;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;

public class Player {
    public static boolean isLoaded = false;
    public static Integer timestarted = 0;
    public static Integer timepaused = 0;

    public static void load(Integer timestamp) {
        Stats status = Stats.getInstance();
        if(SearchBar.selectedSong != null)
            status.setRemainedTime(SearchBar.selectedSong.getDuration());
        else if(SearchBar.selectedPodcast != null)
            status.setRemainedTime(SearchBar.selectedPodcast.getRemainingTimeofLastEpisode());
        timestarted = timestamp;
        isLoaded = true;
    }
    public static void status(Integer timestamp) {
        Stats status = Stats.getInstance();
        if(SearchBar.selectedSong != null) {
            status.setName(SearchBar.selectedSong.getName());
            if(timepaused == 0) {
                if (status.getRemainedTime() <= timestamp - timestarted) {
                    status.setRemainedTime(0);
                    status.setPaused(true);
                    status.setName("");
                } else
                    status.setRemainedTime(status.getRemainedTime() - (timestamp - timestarted));
                timestarted = timestamp;
            }
        } else if(SearchBar.selectedPodcast != null) {
            status.setName(SearchBar.selectedPodcast.getName());
        }
    }
    public static void repeat() {
        Stats status = Stats.getInstance();
        if(SearchBar.selectedSong != null || SearchBar.selectedPodcast != null) {
            switch (status.getRepeat()) {
                case "No Repeat":
                    status.setRepeat("Repeat Once");
                    break;
                case "Repeat Once":
                    status.setRepeat("Repeat Infinite");
                    break;
                case "Repeat Infinite":
                    status.setRepeat("No Repeat");
                    break;
                default:
                    break;
            }
        }
    }
    public static void addRemoveInPlaylist(ArrayList<Playlist> playlists, Integer playlistId, ObjectNode objectNode) {
        Playlist playlist = Playlist.getPlaylistFromId(playlists, playlistId);
        if(playlist == null) {
            objectNode.put("message", "The specified playlist does not exist.");
            return;
        }
        if(SearchBar.selectedSong != null) {
            if(playlist.getSongs().contains(SearchBar.selectedSong)) {
                playlist.getSongs().remove(SearchBar.selectedSong);
                objectNode.put("message", "Successfully removed from playlist.");
            } else {
                playlist.getSongs().add(SearchBar.selectedSong);
                objectNode.put("message", "Successfully added to playlist.");
            }
        } else {
            objectNode.put("message", "The loaded source is not a song.");
        }
    }
    public static void like(ArrayList<User> users ,ObjectNode objectNode, String username) {
        if(SearchBar.selectedSong != null) {
            User user = User.getUserByName(users, username);
            if(user != null)
                    if(user.getLikedSongs().contains(SearchBar.selectedSong)) {
                        objectNode.put("message", "Unlike registered successfully.");
                        user.getLikedSongs().remove(SearchBar.selectedSong);
                    } else {
                        objectNode.put("message", "Like registered successfully.");
                        user.getLikedSongs().add(SearchBar.selectedSong);
                    }
        } else {
            objectNode.put("message", "Loaded source is not a song.");
        }
    }
}
