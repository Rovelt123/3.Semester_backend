package app.security;


public class SecurityConfig {

    private static final String SECRET = "supersecretkey123";

    public static final TokenManager TOKEN_MANAGER = new TokenManager(
            SECRET,
            TokenType.JWT,
            30,
            60,
            "myapp",
            "users"
    );
}
