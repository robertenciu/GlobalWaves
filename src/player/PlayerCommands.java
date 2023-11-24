package player;

import com.fasterxml.jackson.databind.node.ObjectNode;

interface PlayerCommands {
    void load(Integer timestamp);
    void playPause(Integer timestamp);
    void updateStatus(Integer timestamp);
    void like(ObjectNode obj);
    void forward(ObjectNode obj);
    void backward(ObjectNode obj);
    void next(ObjectNode obj, Integer timestamp);
    void prev(ObjectNode obj, Integer timestamp);
    void addRemoveInPlaylist(Integer playlistId, ObjectNode obj);
    void repeat();
}

