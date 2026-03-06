package app.dtos.schedule;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DayScheduleDTO {

    private boolean offDay;
    private String title;
    private String start_time;
    private String end_time;

}