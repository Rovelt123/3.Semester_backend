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


    @Builder.Default
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

}
