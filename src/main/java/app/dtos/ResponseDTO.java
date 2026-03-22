package app.dtos;

import app.entities.Response;
import app.enums.ShiftStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseDTO {

    private int id;
    private ShiftStatus status;

    // ________________________________________________________

    public ResponseDTO(Response response) {
        this.id = response.getId();
        this.status = response.getStatus();
    }
}
