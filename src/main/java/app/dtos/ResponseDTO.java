package app.dtos;

import app.enums.ShiftStatus;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ResponseDTO {

    private int id;
    private ShiftStatus status;

}
