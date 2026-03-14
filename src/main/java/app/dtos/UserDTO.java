package app.dtos;

import app.entities.Message;
import app.entities.Responsibility;
import app.entities.User;
import app.enums.Role;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
public class UserDTO {
    private int id;
    private String name;
    private String username;
    private List<HolidayDTO> holidays;
    private Set<Role> role;
    private List<ResponsibilityDTO> responsibilities;
    private List<ShiftDTO> shifts;
    private List<AnnouncementDTO> announcements;
    private List<MessageDTO> messages;

    public UserDTO(User owner) {
        this.id = owner.getId();
        this.name = owner.getName();
        this.username = owner.getUsername();
        this.role = owner.getRoles();
        this.holidays =  owner.getHolidays().stream().map(HolidayDTO::new).collect(Collectors.toList());
        this.responsibilities = owner.getResponsibilities().stream().map(ResponsibilityDTO::new).collect(Collectors.toList());
        this.shifts = owner.getShifts().stream().map(ShiftDTO::new).collect(Collectors.toList());
        this.messages = owner.getMessages().stream().map(MessageDTO::new).collect(Collectors.toList());
    }
}

