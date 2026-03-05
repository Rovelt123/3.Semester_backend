package app.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@Setter
public class Shift {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    private User owner;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;

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
