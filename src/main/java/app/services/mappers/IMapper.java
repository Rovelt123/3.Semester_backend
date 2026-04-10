package app.services.mappers;

public interface IMapper<E, D> {
    E toEntity(D dto);
    D toDTO(E entity);
}
