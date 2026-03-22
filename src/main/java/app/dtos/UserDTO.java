package app.dtos;

import app.entities.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
    private int id;
    private String name;
    private String username;
    private String lastName;

    public UserDTO(User owner) {
        this.id = owner.getId();
        this.name = owner.getFirstname();
        this.lastName = owner.getLastname();
        this.username = owner.getUsername();
    }
}

