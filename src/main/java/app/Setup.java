package app;

import app.daos.*;
import jakarta.persistence.EntityManager;
import lombok.Getter;

@Getter
public class Setup {
    EntityManager em;
    UserDAO userDAO;
    ResponsibilityDAO respDAO;
    HolidayDAO holidayDAO;
    MessageDAO messageDAO;
    AnnouncementDAO announcementDAO;
    ShiftRequestDAO shiftRequestDAO;

    Setup(EntityManager em) {
        this.em = em;
    }

    public void initialize(){
        userDAO = new UserDAO(em);
        respDAO = new ResponsibilityDAO(em);
        holidayDAO = new HolidayDAO(em);
        messageDAO = new MessageDAO(em);
        announcementDAO = new AnnouncementDAO(em);
        shiftRequestDAO = new ShiftRequestDAO(em);
        registerSetup();
    }

    public void registerSetup() {
        testSetup test = new testSetup();
        test.testSetup();
    }

    public void endSession() {
        em.close();
    }
}
