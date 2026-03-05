package app.daos;

import app.entities.Announcement;
import jakarta.persistence.EntityManager;

public class AnnouncementDAO extends EntityManagerDAO<Announcement>{

    public AnnouncementDAO(EntityManager em) {
        super(em, Announcement.class);
    }

}

