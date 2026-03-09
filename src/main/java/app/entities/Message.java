package app.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String content;
    private LocalDateTime sentAt;

    private int senderID;

    protected Message() {}

    // ________________________________________________________

    public Message(User sender, String content) {
        this.senderID = sender.getId();
        this.content = content;
        this.sentAt = LocalDateTime.now();
    }
}
