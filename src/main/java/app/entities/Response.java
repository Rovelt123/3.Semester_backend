package app.entities;

import app.enums.ShiftStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Response {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Enumerated(EnumType.STRING)
    private ShiftStatus status = ShiftStatus.NO_RESPONSE;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "shift_request")
    private ShiftRequest shiftRequest;

    public Response(User user, ShiftRequest request) {
        this.user = user;
        this.shiftRequest = request;
    }

    // ________________________________________________________

    public Response() {

    }
}