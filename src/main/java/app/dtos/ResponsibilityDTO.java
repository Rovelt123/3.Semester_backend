package app.dtos;


import app.entities.Responsibility;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponsibilityDTO {

    private int id;
    private String name;

    // ________________________________________________________

    public ResponsibilityDTO(Responsibility responsibility) {
        this.id = responsibility.getId();
        this.name = responsibility.getName();
    }
}
