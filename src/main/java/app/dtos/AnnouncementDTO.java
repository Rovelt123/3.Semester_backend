package app.dtos;

import app.entities.Announcement;
import app.entities.User;

import java.time.LocalDateTime;

public class AnnouncementDTO {
    private int id;
    private String title;
    private String context;
    private LocalDateTime lastUpdated;
    private User author;

    public AnnouncementDTO(Announcement announcement){
        this.id = announcement.getId();
        this.title = announcement.getTitle();
        this.context = announcement.getContent();
        this.lastUpdated = announcement.getLastUpdated();
        this.author = announcement.getAuthor();
    }

}
