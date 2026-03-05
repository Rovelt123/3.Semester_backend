package app.dtos;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class HolidayDTO {
    private int id;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private int userId;
}

