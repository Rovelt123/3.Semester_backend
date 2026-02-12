package app.daos;

import app.entities.Announcement;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

public class AnnouncementDAO implements DAOI<Announcement, Integer>{

    private final EntityManager em;

    public AnnouncementDAO(EntityManager em) {
        this.em = em;
    }

    @Override
    public Announcement create(Announcement announcement) {
        em.getTransaction().begin();
        em.persist(announcement);
        em.getTransaction().commit();
        return announcement;
    }

    @Override
    public List<Announcement> getAll() {
        List<Announcement> announcements =
                em.createQuery("SELECT c FROM Announcement c", Announcement.class)
                        .getResultList();
        return announcements;
    }

    @Override
    public Optional<Announcement> getById(Integer id) {
        Announcement announcement = em.find(Announcement.class, id);
        return Optional.ofNullable(announcement);
    }

    @Override
    public Announcement update(Announcement announcement) {
        em.getTransaction().begin();
        Announcement merged = em.merge(announcement);
        em.getTransaction().commit();
        return merged;
    }

    @Override
    public void delete(Integer id) {
        em.getTransaction().begin();
        Announcement announcement = em.find(Announcement.class, id);
        if (announcement != null) {
            em.remove(announcement);
        }
        em.getTransaction().commit();
    }
}

