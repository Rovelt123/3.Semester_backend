package app.entities;

import app.enums.ShiftStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ShiftRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Enumerated(EnumType.STRING)
    private ShiftStatus status = ShiftStatus.NO_RESPONSE;

    @ManyToOne(fetch = FetchType.LAZY)
    private User requester;

    @ManyToOne(fetch = FetchType.LAZY)
    private Shift shift;

    public ShiftRequest() {}

    // ________________________________________________________

    public ShiftRequest(User user, Shift shift) {
        this.requester = user;
        this.shift = shift;
    }

    // ________________________________________________________

    public void approve() {
        status = ShiftStatus.APPROVED;
    }

    // ________________________________________________________

    public void reject() {
        status = ShiftStatus.REJECTED;
    }
}
