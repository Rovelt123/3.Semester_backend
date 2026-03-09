package app.dtos;

import app.Main;
import app.entities.Response;
import app.enums.ShiftStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseDTO {
    private int id;
    private ShiftStatus status;
    private UserDTO user;
    private ShiftRequestDTO request;

    public ResponseDTO(Response response) {
        this.id = response.getId();
        this.status = response.getStatus();
        this.user = new UserDTO(Main.setup.getUserDAO().getById(response.getUserID()));
        this.request = new ShiftRequestDTO(Main.setup.getShiftRequestDAO().getById(response.getShiftRequestID()));
    }
}
