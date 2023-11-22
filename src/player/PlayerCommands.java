package player;

import com.fasterxml.jackson.databind.node.ObjectNode;
import main.User;

interface PlayerCommands {
    void load(Integer timestamp, User user);
    void playPause(Integer timestamp, User user);
    void updateStatus(Integer timestamp, User user);
    void like(User user, ObjectNode obj);
    void forward(User user, ObjectNode obj);
    void backward(final User user, final ObjectNode obj);
    void next(final User user, final ObjectNode obj, final Integer timestamp);
    void prev(final User user, final ObjectNode obj, final Integer timestamp);
    void addRemoveInPlaylist(User user,
                             Integer playlistId,
                             ObjectNode obj);
    void repeat();
}

