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

    @OneToOne
    @JoinColumn(name = "requester_id")
    private User requester;

    @ManyToOne
    private Shift shift;

    @OneToMany(mappedBy = "shiftRequest", cascade = CascadeType.ALL, orphanRemoval = true)
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
