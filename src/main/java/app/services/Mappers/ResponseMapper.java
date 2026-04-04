package app.services.Mappers;

import app.dtos.ResponseDTO;
import app.entities.Response;

public class ResponseMapper  implements Mapper<Response, ResponseDTO> {

    @Override
    public Response toEntity(ResponseDTO dto) {
        return Response.builder()
            .id(dto.getId())
            .status(dto.getStatus())
            .build();
    }

    // ________________________________________________________

    @Override
    public ResponseDTO toDTO(Response entity) {
        return ResponseDTO.builder()
            .id(entity.getId())
            .status(entity.getStatus())
            .build();
    }
}