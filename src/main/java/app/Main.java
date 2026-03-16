package app;

import app.Server.Setup;
import app.configs.*;

public class Main {

    public static Setup setup;
    private static final int port = 7070;


    public static void main(String[] args) {
        setup = new Setup(HibernateConfig.getEntityManagerFactory().createEntityManager(), port);

        setup.initialize();

        //setup.endSession();
    }

}

