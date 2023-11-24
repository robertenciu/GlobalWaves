package player;

import com.fasterxml.jackson.databind.node.ObjectNode;
import main.User;

interface PlayerCommands {
    void load(Integer timestamp, User user);
    void playPause(Integer timestamp, User user);
    void updateStatus(Integer timestamp, User user);
    void like(User user, ObjectNode obj);
    void forward(User user, ObjectNode obj);
    void backward(User user, ObjectNode obj);
    void next(User user, ObjectNode obj, Integer timestamp);
    void prev(User user, ObjectNode obj, Integer timestamp);
    void addRemoveInPlaylist(User user,
                             Integer playlistId,
                             ObjectNode obj);
    void repeat();
}

