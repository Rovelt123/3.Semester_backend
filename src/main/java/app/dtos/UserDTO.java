package app.dtos;

import app.entities.Responsibility;
import app.entities.User;
import app.enums.Role;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserDTO {
    private int id;
    private String name;
    private Role role;
    private List<Responsibility> responsibilities;

    public UserDTO(User owner) {
        this.id = owner.getId();
        this.name = owner.getName();
        this.role = owner.getRole();
        this.responsibilities = owner.getResponsibilities();
    }
}

