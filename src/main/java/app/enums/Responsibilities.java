package app.enums;

public enum Responsibilities {
    PLANNER("Planning holidays"),
    CASHIER("Kassemedarbejder"),
    DRIVER("Chauffør"),
    RECEPTUR("Receptur ansvarlig");

    private final String displayName;

    Responsibilities(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
