package app.entities;

import app.enums.HolidayStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Holiday {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private LocalDate startDate;
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private HolidayStatus status = HolidayStatus.PENDING;

    @ManyToOne
    private User user;

    protected Holiday() {}

    public Holiday(User user, LocalDate start, LocalDate end) {
        this.user = user;
        this.startDate = start;
        this.endDate = end;
    }

    public void approve() {
        status = HolidayStatus.APPROVED;
    }

    public void reject() {
        status = HolidayStatus.REJECTED;
    }
}
