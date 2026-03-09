package app.dtos;

import app.Main;
import app.entities.Response;
import app.entities.Shift;
import app.entities.ShiftRequest;
import app.entities.User;
import app.enums.ShiftStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ShiftRequestDTO {
    private int id;
    private ShiftStatus status;
    private UserDTO requester;
    private Shift shift;
    private List<Response> responses;

    public ShiftRequestDTO(ShiftRequest shiftRequest) {
        this.id = shiftRequest.getId();
        this.status = shiftRequest.getStatus();
        this.requester = new UserDTO(Main.setup.getUserDAO().getById(shiftRequest.getRequesterID()));
        this.shift = shiftRequest.getShift();
        this.responses = shiftRequest.getResponses();
    }

}
