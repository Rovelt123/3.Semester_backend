package app.enums;

public enum Role {
    USER("Medarbejder"),
    CHEF("Chefen");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
