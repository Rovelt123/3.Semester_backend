package app.dtos;

import app.entities.Message;
import app.entities.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MessageDTO {
    private int id;
    private String context;
    private LocalDateTime sentAt;
    private User user;

    public MessageDTO(Message message) {
        this.id = message.getId();
        this.context = message.getContent();
        this.sentAt = message.getSentAt();
        this.user = message.getSender();
    }
}
