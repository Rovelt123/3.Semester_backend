package app.services.Mappers;

import app.dtos.ShiftDTO;
import app.entities.Shift;

public class ShiftMapper  implements Mapper<Shift, ShiftDTO> {

    @Override
    public Shift toEntity(ShiftDTO dto) {
        return Shift.builder()
            .id(dto.getId())
            .title(dto.getTitle())
            .date(dto.getDate())
            .startTime(dto.getStartTime())
            .endTime(dto.getEndTime())
            .build();
    }

    // ________________________________________________________

    @Override
    public ShiftDTO toDTO(Shift entity) {
        return ShiftDTO.builder()
            .id(entity.getId())
            .title(entity.getTitle())
            .date(entity.getDate())
            .startTime(entity.getStartTime())
            .endTime(entity.getEndTime())
            .build();
    }
}
