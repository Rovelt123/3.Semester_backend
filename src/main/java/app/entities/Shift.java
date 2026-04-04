package app.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter

//Secures no duplications of shifts to owners at same date (ENSURES NO RACE CONDITION!)
@Table(
    name = "shifts",
    uniqueConstraints = @UniqueConstraint(columnNames = {"owner_id", "date"})
)
public class Shift {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String title;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;

    @OneToMany(mappedBy = "shift", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ShiftRequest> shiftRequests = new ArrayList<>();

}
