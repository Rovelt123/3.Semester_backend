package app.enums;

import lombok.Getter;

@Getter
public enum ShiftStatus {
    NO_RESPONSE("NO RESPONSE"),
    APPROVED("APPROVED"),
    REJECTED("REJECTED"),
    SOLVED("SOLVED"),
    WAITING("WAITING"),
    ACCEPTED("ACCEPTED"),
    DENIED("DENIED");

    // ________________________________________________________

    private final String displayName;

    // ________________________________________________________

    ShiftStatus(String displayName) {
        this.displayName = displayName;
    }

    // ________________________________________________________

}
