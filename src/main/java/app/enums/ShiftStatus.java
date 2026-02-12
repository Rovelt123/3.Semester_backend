package app.enums;

public enum ShiftStatus {
    NO_RESPONSE("NO RESPONSE"),
    APPROVED("APPROVED"),
    REJECTED("REJECTED");

    private final String displayName;

    ShiftStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
