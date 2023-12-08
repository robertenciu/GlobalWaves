package user;

import command.Commands;
import media.Library;
import media.content.Announcement;
import media.podcast.Episode;
import media.podcast.Podcast;

import java.util.ArrayList;

public final class Host extends User implements Page {
    private final ArrayList<Podcast> podcasts = new ArrayList<>();
    private final ArrayList<Announcement> announcements = new ArrayList<>();
    public Host() {
        super();
    }
    @Override
    public String printPage() {
        StringBuilder result = new StringBuilder("Podcasts:\n\t[");

        for (Podcast podcast : podcasts) {
            result.append(podcast.getName()).append(":\n\t[");

            for (Episode episode : podcast.getEpisodes()) {
                result.append(episode.getName()).append(" - ");
                result.append(episode.getDescription()).append(", ");
            }
            result.deleteCharAt(result.length() - 1);
            result.deleteCharAt(result.length() - 1);

            result.append("]\n, ");
        }
        if (!podcasts.isEmpty()) {
            result.deleteCharAt(result.length() - 1);
            result.deleteCharAt(result.length() - 1);
        }

        result.append("]\n\nAnnouncements:\n\t[");

        for (Announcement announce : announcements) {
            result.append(announce.getName()).append(":\n\t");
            result.append(announce.getDescription()).append("\n, ");
        }
        if (!announcements.isEmpty()) {
            result.deleteCharAt(result.length() - 1);
            result.deleteCharAt(result.length() - 1);
        }

        result.append("]");

        return result.toString();
    }
    public static Host getHost(final ArrayList<Host> hosts, final String name) {
        if (hosts.isEmpty() || name == null) {
            return null;
        }
        for (Host host : hosts) {
            if (host.getUsername().equals(name)) {
                return host;
            }
        }
        return null;
    }

    private boolean hasDuplicateEpisodes(final ArrayList<Episode> episodes) {
        for (int i = 0; i < episodes.size() - 1; i++) {
            for (int j = i + 1; j < episodes.size(); j++) {
                if (episodes.get(i).getName().equals(episodes.get(j).getName())) {
                    return true;
                }
            }
        }
        return false;
    }
    @Override
    public String addPodcast(final Commands command, final Library library) {
        if (Podcast.exists(podcasts, command.getName())) {
            return this.username + " has another podcast with the same name.";
        }

        if (hasDuplicateEpisodes(command.getEpisodes())) {
            return this.username + " has the same episode in this podcast.";
        }

        Podcast newPodcast = new Podcast(command.getUsername());
        newPodcast.setEpisodes(command.getEpisodes());
        for (Episode episode : newPodcast.getEpisodes()) {
            episode.setInitialDuration(episode.getDuration());
        }
        newPodcast.setName(command.getName());

        library.getPodcasts().add(newPodcast);
        this.podcasts.add(newPodcast);
        return this.username + " has added new podcast successfully.";
    }

    @Override
    public String addAnnouncement(final Commands command, final Library library) {
        Announcement announce = Announcement.getAnnouncement(announcements, command.getName());

        if (announce != null) {
            return this.username + " has already added an announcement with this name.";
        }

        Announcement newAnnouncement = new Announcement(command.getUsername());
        newAnnouncement.setDescription(command.getDescription());
        newAnnouncement.setName(command.getName());

        library.getAnnouncements().add(newAnnouncement);
        this.announcements.add(newAnnouncement);
        return this.username + " has successfully added new announcement.";
    }

    @Override
    public String removeAnnouncement(final Commands command, final Library library) {
        Announcement announce = Announcement.getAnnouncement(announcements, command.getName());

        if (announce == null) {
            return this.username + " has no announcement with the given name.";
        }

        library.getAnnouncements().remove(announce);
        this.announcements.remove(announce);

        return this.username + " has successfully deleted the announcement.";
    }

    public ArrayList<Podcast> getPodcasts() {
        return podcasts;
    }

    public ArrayList<Announcement> getAnnouncements() {
        return announcements;
    }
}
