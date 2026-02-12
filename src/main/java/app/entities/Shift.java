package app.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

    private int owner;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;

    public Shift(int owner, LocalDate date, LocalTime now, LocalTime localTime) {
        this.owner = owner;
        this.date = date;
        this.startTime = now;
        this.endTime = localTime;
    }

    public Shift() {

    }
}
