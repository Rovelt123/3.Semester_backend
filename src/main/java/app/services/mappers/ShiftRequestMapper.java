package app.services.mappers;

import app.dtos.ShiftRequestDTO;
import app.entities.ShiftRequest;

public class ShiftRequestMapper  implements IMapper<ShiftRequest, ShiftRequestDTO> {

    @Override
    public ShiftRequest toEntity(ShiftRequestDTO dto) {
        return ShiftRequest.builder()
            .id(dto.getId())
            .status(dto.getStatus())
            .build();
    }

    @Override
    public ShiftRequestDTO toDTO(ShiftRequest entity) {
        return ShiftRequestDTO.builder()
            .id(entity.getId())
            .status(entity.getStatus())
            .build();
    }
}
