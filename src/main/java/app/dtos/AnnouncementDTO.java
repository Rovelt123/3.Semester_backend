package app.dtos;

import lombok.*;
import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AnnouncementDTO {

    private int id;
    private String title;
    private String context;
    private LocalDateTime lastUpdated;

}
