package app.dtos;

import app.enums.HolidayStatus;
import lombok.*;
import java.time.LocalDate;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class HolidayDTO {

    private int id;
    private LocalDate startDate;
    private LocalDate endDate;
    private HolidayStatus status;

}

