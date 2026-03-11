package app.security;

import io.javalin.http.Context;

public class JwtMiddleware {

    public static void handle(Context ctx) {

        String header = ctx.header("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            ctx.status(401).result("Missing token");
            return;
        }

        String token = header.substring(7);

        try {

            var decoded = SecurityConfig.TOKEN_MANAGER.verifyToken(token);

            ctx.attribute("username", decoded.getSubject());
            ctx.attribute("role", decoded.getClaim("role").asString());

        } catch (Exception e) {

            ctx.status(401).result("Invalid token");

        }
    }

}