package app.daos;

import app.entities.Announcement;

import java.util.List;
import java.util.Optional;

public interface DAOI<T, ID> {

    T create(T entity);

    List<T> getAll();

    Optional<T> getById(ID id);

    T update(T entity);

    void delete(ID id);
}