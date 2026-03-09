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
import java.util.stream.Collectors;

@Getter
@Setter
public class ShiftRequestDTO {
    private int id;
    private ShiftStatus status;
    private ShiftDTO shift;
    private List<ResponseDTO> responses;

    public ShiftRequestDTO(ShiftRequest shiftRequest) {
        this.id = shiftRequest.getId();
        this.status = shiftRequest.getStatus();
        this.shift = new ShiftDTO(shiftRequest.getShift());
        this.responses = shiftRequest.getResponses().stream().map(ResponseDTO::new).collect(Collectors.toList());
    }

}
