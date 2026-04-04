package app.services.Mappers;

public interface Mapper<E, D> {
    E toEntity(D dto);
    D toDTO(E entity);
}
