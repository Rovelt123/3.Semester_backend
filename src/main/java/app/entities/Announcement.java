package app.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "announcements")
public class Announcement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String title;
    private String content;
    private LocalDateTime lastUpdated = LocalDateTime.now();

    @ManyToOne
    private User author;

    // ________________________________________________________

    public void updateContent(String content) {
        this.content = content;
        this.lastUpdated = LocalDateTime.now();
    }
}
