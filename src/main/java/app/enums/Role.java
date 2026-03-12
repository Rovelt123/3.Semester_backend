package app.enums;

import io.javalin.security.RouteRole;
import lombok.Getter;

@Getter
public enum Role implements RouteRole {
    ANYONE("Alle"),
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
