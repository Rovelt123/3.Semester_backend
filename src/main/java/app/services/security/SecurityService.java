package app.services.security;

import app.Main;
import app.dtos.UserDTO;
import app.entities.User;
import app.enums.Role;
import app.exceptions.ApiException;
import app.exceptions.NotAuthorizedException;
import app.utils.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;
import io.javalin.http.UnauthorizedResponse;
import io.javalin.security.RouteRole;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Purpose: To handle security in the API
 * Author: Thomas Hartmann
 */
public class SecurityService implements ISecurityService {
    ObjectMapper objectMapper = new ObjectMapper();
    private static SecurityService instance;
    private static Logger logger = LoggerFactory.getLogger(SecurityService.class);

    public SecurityService() { }

    public static SecurityService getInstance() { // Singleton because we don't want multiple instances of the same class
        if (instance == null) {
            instance = new SecurityService();
        }
        return instance;
    }

    @Override
    public Handler authenticate() throws UnauthorizedResponse {

        ObjectNode returnObject = objectMapper.createObjectNode();
        return (ctx) -> {
            // This is a preflight request => OK
            if (ctx.method().toString().equals("OPTIONS")) {
                ctx.status(200);
                return;
            }
            String header = ctx.header("Authorization");

            if (header == null) {
                throw new UnauthorizedResponse("Authorization header missing");
            }

            String[] headerParts = header.split(" ");
            if (headerParts.length != 2) {
                throw new UnauthorizedResponse("Authorization header malformed");
            }

            String token = headerParts[1];
            UserDTO verifiedTokenUser = verifyToken(token);

            if (verifiedTokenUser == null) {
                throw new UnauthorizedResponse("Invalid User or Token");
            }
            logger.info("User verified: " + verifiedTokenUser);
            ctx.attribute("user", verifiedTokenUser);
        };
    }

    @Override
    // Check if the user's roles contain any of the allowed roles
    public boolean authorize(UserDTO userDTO, Set<RouteRole> allowedRoles) {
        User user = Main.setup.getUserDAO().getById(userDTO.getId());
        if (user == null) {
            throw new UnauthorizedResponse("You need to log in, dude!");
        }

        Set<String> roleNames = allowedRoles.stream()
                .map(RouteRole::toString)  // Convert RouteRoles to  Set of Strings
                .collect(Collectors.toSet());

        Set<Role> roles = user.getRoles();

        if (roles.stream().anyMatch(role -> role.equals(Role.CHEF))) {
            return true;
        }

        return user.getRoles().stream().anyMatch(allowedRoles::contains);
    }

    @Override
    public String createToken(UserDTO user) {
        try {
            String ISSUER;
            String TOKEN_EXPIRE_TIME;
            String SECRET_KEY;

            if (System.getenv("DEPLOYED") != null) {
                ISSUER = System.getenv("ISSUER");
                TOKEN_EXPIRE_TIME = System.getenv("TOKEN_EXPIRE_TIME");
                SECRET_KEY = System.getenv("SECRET_KEY");
            } else {
                ISSUER = Utils.getPropertyValue("ISSUER", "config.properties");
                TOKEN_EXPIRE_TIME = Utils.getPropertyValue("TOKEN_EXPIRE_TIME", "config.properties");
                SECRET_KEY = Utils.getPropertyValue("SECRET_KEY", "config.properties");
            }
            return JWTTokenGenerator.createToken(user, ISSUER, TOKEN_EXPIRE_TIME, SECRET_KEY);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApiException(500, "Could not create token");
        }
    }

    @Override
    public UserDTO verifyToken(String token) {
        try {
            String SECRET_KEY = Utils.getPropertyValue("SECRET_KEY", "config.properties");
            if (!JWTTokenGenerator.tokenIsValid(token, SECRET_KEY) || !JWTTokenGenerator.tokenNotExpired(token)) {
                throw new NotAuthorizedException(403, "Token is invalid or expired");
            }
            return JWTTokenGenerator.getUserFromToken(token);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApiException(HttpStatus.UNAUTHORIZED.getCode(), "Unauthorized. Could not verify token");
        }
    }


    // Health check for the API. Used in deployment
    public void healthCheck(@NotNull Context ctx) {
        ctx.status(200).json("{\"msg\": \"API is up and running\"}");
    }
}