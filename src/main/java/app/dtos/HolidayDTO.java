package app.dtos;

import app.entities.Holiday;
import app.entities.User;
import app.enums.HolidayStatus;
import app.enums.ShiftStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class HolidayDTO {
    private int id;
    private LocalDate startDate;
    private LocalDate endDate;
    private HolidayStatus status;
    private User user;

    public HolidayDTO(Holiday holiday) {
        this.id = holiday.getId();
        this.startDate = holiday.getStartDate();
        this.endDate = holiday.getEndDate();
        this.status = holiday.getStatus();
        this.user = holiday.getUser();
    }
}

