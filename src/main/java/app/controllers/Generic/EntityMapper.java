package app.controllers.Generic;

public interface EntityMapper<T, DTO> {
    DTO map(T entity);
}