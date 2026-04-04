package app.services.Mappers;

import app.dtos.HolidayDTO;
import app.entities.Holiday;

public class HolidayMapper  implements Mapper<Holiday, HolidayDTO> {

    @Override
    public Holiday toEntity(HolidayDTO dto) {
        return Holiday.builder()
            .id(dto.getId())
            .startDate(dto.getStartDate())
            .endDate(dto.getEndDate())
            .status(dto.getStatus())
            .build();
    }

    // ________________________________________________________

    @Override
    public HolidayDTO toDTO(Holiday entity) {
        return HolidayDTO.builder()
            .id(entity.getId())
            .startDate(entity.getStartDate())
            .endDate(entity.getEndDate())
            .status(entity.getStatus())
            .build();
    }
}