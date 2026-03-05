package app.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Announcement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String title;
    private String content;
    private LocalDateTime lastUpdated = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    private User author;

    protected Announcement() {}

    // ________________________________________________________

    public Announcement(User author, String title, String content) {
        this.author = author;
        this.title = title;
        this.content = content;
    }

    // ________________________________________________________

    public void updateContent(String content) {
        this.content = content;
        this.lastUpdated = LocalDateTime.now();
    }
}
