package app.services.mappers;

import app.dtos.ResponsibilityDTO;
import app.entities.Responsibility;

public class ResponsibilityMapper  implements IMapper<Responsibility, ResponsibilityDTO> {

    @Override
    public Responsibility toEntity(ResponsibilityDTO dto) {
        return Responsibility.builder()
            .id(dto.getId())
            .name(dto.getName())
            .build();
    }

    // ________________________________________________________

    @Override
    public ResponsibilityDTO toDTO(Responsibility entity) {
        return ResponsibilityDTO.builder()
            .id(entity.getId())
            .name(entity.getName())
            .build();
    }
}