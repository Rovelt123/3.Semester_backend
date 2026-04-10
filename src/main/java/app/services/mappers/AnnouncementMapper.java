package app.services.mappers;

import app.dtos.AnnouncementDTO;
import app.entities.Announcement;

public class AnnouncementMapper  implements IMapper<Announcement, AnnouncementDTO> {

    @Override
    public Announcement toEntity(AnnouncementDTO dto) {
        return Announcement.builder()
            .id(dto.getId())
            .title(dto.getTitle())
            .content(dto.getContext())
            .lastUpdated(dto.getLastUpdated())
            .build();
    }

    // ________________________________________________________

    @Override
    public AnnouncementDTO toDTO(Announcement entity) {
        return AnnouncementDTO.builder()
            .id(entity.getId())
            .title(entity.getTitle())
            .context(entity.getContent())
            .lastUpdated(entity.getLastUpdated())
            .build();
    }
}