package app.dtos;

import app.entities.ShiftRequest;
import app.enums.ShiftStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShiftRequestDTO {

    private int id;
    private ShiftStatus status;
    private ShiftDTO shift;

    // ________________________________________________________

    public ShiftRequestDTO(ShiftRequest shiftRequest) {
        this.id = shiftRequest.getId();
        this.status = shiftRequest.getStatus();
        this.shift = new ShiftDTO(shiftRequest.getShift());
    }

}
