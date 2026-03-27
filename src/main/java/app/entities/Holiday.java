package app.entities;

import app.enums.HolidayStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;



@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "holidays")
public class Holiday {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private LocalDate startDate;
    private LocalDate endDate;


    @Builder.Default
    @Enumerated(EnumType.STRING)
    private HolidayStatus status = HolidayStatus.PENDING;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    // ________________________________________________________

    public void approve() {
        status = HolidayStatus.APPROVED;
    }

    // ________________________________________________________

    public void reject() {
        status = HolidayStatus.REJECTED;
    }
}
