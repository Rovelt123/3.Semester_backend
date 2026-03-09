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

    @JoinColumn(name = "user_id")
    private int userID;

    @JoinColumn(name = "shift_request_id")
    private int shiftRequestID;

    public Response(User user, ShiftRequest request) {
        this.userID = user.getId();
        this.shiftRequestID = request.getId();
    }

    public Response() {

    }
}