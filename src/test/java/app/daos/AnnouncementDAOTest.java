package app.daos;

import app.configs.TestHibernateConfig;
import app.entities.Announcement;
import app.entities.User;
import app.enums.Role;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AnnouncementDAOTest {

    private EntityManagerFactory emf;
    private EntityManager em;
    private AnnouncementDAO announcementDAO;
    private User testUser;

    @BeforeAll
    void setupAll() {
        emf = TestHibernateConfig.getTestEmf();
    }

    @BeforeEach
    void setup() {
        em = emf.createEntityManager();
        announcementDAO = new AnnouncementDAO(em);

        testUser = new User("Announcement Author", Role.USER);
        em.getTransaction().begin();
        em.persist(testUser);
        em.getTransaction().commit();
    }

    @AfterEach
    void tearDown() {
        if (em.isOpen()) em.close();
    }

    @AfterAll
    void tearDownAll() {
        if (emf.isOpen()) emf.close();
    }

    @Test
    void create() {
        Announcement announcement = new Announcement(testUser, "Title", "Content");
        announcementDAO.create(announcement);

        Announcement found = announcementDAO.getById(announcement.getId());
        assertEquals("Title", found.getTitle());
        assertEquals("Content", found.getContent());
        assertEquals(testUser.getId(), found.getAuthor().getId());
    }

    @Test
    void getAll() {
        Announcement a1 = new Announcement(testUser, "T1", "C1");
        Announcement a2 = new Announcement(testUser, "T2", "C2");
        announcementDAO.create(a1);
        announcementDAO.create(a2);

        List<Announcement> all = announcementDAO.getAll();
        assertTrue(all.size() >= 2);
    }

    @Test
    void update() {
        Announcement announcement = new Announcement(testUser, "Old Title", "Old Content");
        announcementDAO.create(announcement);

        announcement.updateContent("New Content");
        announcementDAO.update(announcement);

        Announcement updated = announcementDAO.getById(announcement.getId());
        assertEquals("New Content", updated.getContent());
    }

    @Test
    void delete() {
        Announcement announcement = new Announcement(testUser, "Delete Title", "Delete Content");
        announcementDAO.create(announcement);

        int id = announcement.getId();
        announcementDAO.deleteById(id);
        assertNull(announcementDAO.getById(id));
    }
}
