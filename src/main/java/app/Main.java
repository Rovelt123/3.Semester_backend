package app;

import app.configs.HibernateConfig;
import app.entities.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

public class Main {
    public static Setup setup;

    public static void main(String[] args) {
        setup = new Setup(app.configs.HibernateConfig.getEntityManagerFactory().createEntityManager());
        setup.initialize();
        setup.endSession();
    }

}

