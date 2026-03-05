package app.enums;

import lombok.Getter;

@Getter
public enum Role {
    USER("Medarbejder"),
    CHEF("Chef");

    // ________________________________________________________

    private final String displayName;

    // ________________________________________________________

    Role(String displayName) {
        this.displayName = displayName;
    }

    // ________________________________________________________

}
