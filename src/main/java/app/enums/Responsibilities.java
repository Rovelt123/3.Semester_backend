package app.enums;

import lombok.Getter;

@Getter
public enum Responsibilities {
    // This enum is just to make some test data, should be deleted in production!
    PLANNER("Planning holidays"),
    CASHIER("Kassemedarbejder"),
    DRIVER("Chauffør"),
    RECEPTUR("Receptur ansvarlig");

    // ________________________________________________________

    private final String displayName;

    // ________________________________________________________

    Responsibilities(String displayName) {
        this.displayName = displayName;
    }

    // ________________________________________________________

}
