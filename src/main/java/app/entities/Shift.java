package app.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Shift {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String title;

    @ManyToOne
    @JoinColumn(name = "ownerid")
    private User owner;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;

    @OneToMany(mappedBy = "shift", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ShiftRequest> shiftRequests = new ArrayList<>();

    public Shift(String title, User owner, LocalDate date, LocalTime now, LocalTime localTime) {
        this.title = title;
        this.owner = owner;
        this.date = date;
        this.startTime = now;
        this.endTime = localTime;
    }

    // ________________________________________________________

    public Shift() {

    }
}
