package app.entities;

import app.enums.ShiftStatus;
import jakarta.persistence.*;
import lombok.*;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "responses")
public class Response {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private ShiftStatus status = ShiftStatus.NO_RESPONSE;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "shift_request")
    private ShiftRequest shiftRequest;
}