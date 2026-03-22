package app.dtos;

import app.entities.Announcement;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AnnouncementDTO {

    private int id;
    private String title;
    private String context;
    private LocalDateTime lastUpdated;
    private int userID;

    // ________________________________________________________

    public AnnouncementDTO(Announcement announcement){
        this.id = announcement.getId();
        this.title = announcement.getTitle();
        this.context = announcement.getContent();
        this.lastUpdated = announcement.getLastUpdated();
    }

}
