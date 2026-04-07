package app.daos;

import app.exceptions.ApiException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class EntityManagerDAO<T> implements IDAO<T> {

    protected EntityManager em;
    protected Class<T> classSpecific;

    // ________________________________________________________

    protected EntityManagerDAO(EntityManager em, Class<T> entityClass){
        this.em = em;
        this.classSpecific = entityClass;
    }

    // ________________________________________________________

    @Override
    public T create(T t) {
        return executeQuery(() -> {
            if (!em.contains(t)) {
                em.persist(t);
            }
            return t;
        });
    }

    // ________________________________________________________

    @Override
    public T update(T t) {
        return executeQuery(() -> em.merge(t));
    }

    // ________________________________________________________

    @Override
    public T delete(T t) {
        return executeQuery(() -> {
            T managed = em.contains(t) ? t : em.merge(t);
            em.remove(managed);
            return managed;
        });
    }

    // ________________________________________________________

    @Override
    public T deleteById(Object id) {
        return executeQuery(() -> {
            T entity = findById(id);
            if (entity != null) {
                em.remove(entity);
            }
            return entity;
        });
    }

    // ________________________________________________________

    @Override
    public T getById(Object id) {
        return executeQuery(() -> findById(id));
    }

    // ________________________________________________________

    @Override
    public <R> R getColumnById(Object id, String column, Class<R> type) {
        return executeQuery(() -> {
            String JPQL = "SELECT x." + column + " FROM " + classSpecific.getSimpleName() + " x WHERE x.id = :id";
            return em.createQuery(JPQL, type)
            .setParameter("id", id)
            .getSingleResult();
        });
    }

    // ________________________________________________________

    public List<T> getByColumn(Object value, String column) {
        return executeQuery(() -> {
            String JPQL = "SELECT x FROM " + classSpecific.getSimpleName() + " x WHERE x." + column + " = :value";
            return em.createQuery(JPQL, classSpecific)
                    .setParameter("value", value)
                    .getResultList();
        });
    }

    // ________________________________________________________

    @Override
    public int updateColumnById(Object id, String column, Object value) {
        return executeQuery(() -> {
            String JPQL = "UPDATE " + classSpecific.getSimpleName() + " x SET x." + column + " = :value WHERE x.id = :id";
            return em.createQuery(JPQL)
                    .setParameter("value", value)
                    .setParameter("id", id)
                    .executeUpdate();
        });
    }

    // ________________________________________________________

    @Override
    public boolean existByColumn(Object value, String column) {
        String JPQL = "SELECT COUNT(x) FROM " + classSpecific.getSimpleName() + " x WHERE x." + column + " = :value";
        Long count = executeQuery(() -> em.createQuery(JPQL, Long.class)
                .setParameter("value", value)
                .getSingleResult());
        return count != null && count > 0;
    }

    // ________________________________________________________

    @Override
    public T findEntityByColumn(Object value, String column) {
        return executeQuery(() -> {
            String JPQL = "SELECT x FROM " + classSpecific.getSimpleName() + " x WHERE x." + column + " = :value";
            List<T> results = em.createQuery(JPQL, classSpecific)
                    .setParameter("value", value)
                    .getResultList();
            return results.isEmpty() ? null : results.get(0);
        });
    }

    // ________________________________________________________

    @Override
    public List<T> getAll() {
        return executeQuery(() -> {
            String JPQL = "SELECT x FROM " + classSpecific.getSimpleName() + " x";
            return em.createQuery(JPQL, classSpecific)
                    .getResultList();
        });
    }

    // ________________________________________________________

    @Override
    public void deleteAll() {
        executeQuery(() -> {
            List<T> entities = getAll();
            for (T entity : entities) {
                em.remove(entity);
            }
            return null;
        });
    }

    // ________________________________________________________

    protected <R> R executeQuery(Supplier<R> query) {
        boolean startedTransaction = false;
        try {
            if (!em.getTransaction().isActive()) {
                em.getTransaction().begin();
                startedTransaction = true;
            }
            R result = query.get();
            if (startedTransaction) {
                em.getTransaction().commit();
            }
            return result;
        } catch (NoResultException e){
            if (startedTransaction && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            return null;
        } catch (RuntimeException e) {
            if (startedTransaction && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new ApiException(500, e.getMessage());
        }
    }

    // ________________________________________________________

    private T findById(Object id) {
        return em.find(classSpecific, id);
    }

}