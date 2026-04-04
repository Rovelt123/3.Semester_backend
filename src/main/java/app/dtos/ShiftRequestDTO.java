package app.dtos;

import app.enums.ShiftStatus;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ShiftRequestDTO {

    private int id;
    private ShiftStatus status;

}
