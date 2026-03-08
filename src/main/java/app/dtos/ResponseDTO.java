package app.dtos;

import app.entities.Response;
import app.entities.ShiftRequest;
import app.entities.User;
import app.enums.ShiftStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseDTO {
    private int id;
    private ShiftStatus status;
    private User user;
    private ShiftRequest request;

    public ResponseDTO(Response response) {
        this.id = response.getId();
        this.status = response.getStatus();
        this.user = response.getUser();
        this.request = response.getShiftRequest();
    }
}
