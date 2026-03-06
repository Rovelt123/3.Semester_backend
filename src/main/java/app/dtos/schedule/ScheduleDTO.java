package app.dtos.schedule;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScheduleDTO {

    private String user_id;
    private int months;

    private DayScheduleDTO monday;
    private DayScheduleDTO tuesday;
    private DayScheduleDTO wednesday;
    private DayScheduleDTO thursday;
    private DayScheduleDTO friday;
    private DayScheduleDTO saturday;
    private DayScheduleDTO sunday;

}