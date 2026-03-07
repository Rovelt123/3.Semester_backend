package app.entities;

import app.enums.ShiftStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class ShiftRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Enumerated(EnumType.STRING)
    private ShiftStatus status = ShiftStatus.WAITING;

    @ManyToOne(fetch = FetchType.LAZY)
    private User requester;

    @ManyToOne(fetch = FetchType.LAZY)
    private Shift shift;

    @OneToMany(mappedBy = "shiftRequest", cascade = CascadeType.ALL)
    private List<Response> responses = new ArrayList<>();

    public ShiftRequest() {}

    public ShiftRequest(User user, Shift shift) {
        this.requester = user;
        this.shift = shift;
    }

    public void solve() {
        status = ShiftStatus.SOLVED;
    }
}
