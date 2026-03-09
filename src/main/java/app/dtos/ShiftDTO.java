package app.dtos;

import app.Main;
import app.entities.Shift;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class ShiftDTO {
    private int id;
    private String title;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;

    public ShiftDTO(Shift shift) {
        this.id = shift.getId();
        this.title = shift.getTitle();
        this.date = shift.getDate();
        this.startTime = shift.getStartTime();
        this.endTime = shift.getEndTime();

    }
}

