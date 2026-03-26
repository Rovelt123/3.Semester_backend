package app.entities;

import app.enums.ShiftStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "shift_requests")
public class ShiftRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Enumerated(EnumType.STRING)
    private ShiftStatus status = ShiftStatus.WAITING;

    @ManyToOne
    @JoinColumn(name = "requester_id")
    private User requester;

    @ManyToOne
    private Shift shift;

    @Builder.Default
    @OneToMany(mappedBy = "shiftRequest", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Response> responses = new ArrayList<>();

    // ________________________________________________________

    public void solve() {
        status = ShiftStatus.SOLVED;
    }

    // ________________________________________________________

    public void addResponse(Response response) {
        responses.add(response);
    }

    // ________________________________________________________

    public void removeResponse(Response response) {
        responses.remove(response);
    }
}
