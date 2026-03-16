package app.services.security;

import app.Main;
import app.dtos.UserDTO;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import java.text.ParseException;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

public class JWTTokenGenerator {

    // ________________________________________________________

    public static String createToken(UserDTO user, String issuer, String expireMillis, String secretKey) throws JOSEException {

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
            .subject(user.getUsername())
            .issuer(issuer)
            .claim("username", user.getUsername())
            .expirationTime(new Date((new Date()).getTime() + (long)Integer.parseInt(expireMillis)))
            .build();

        Payload payload = new Payload(claims.toJSONObject());
        JWSSigner signer = new MACSigner(secretKey);
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);
        JWSObject jwsObject = new JWSObject(header, payload);
        jwsObject.sign(signer);

        return jwsObject.serialize();
    }

    // ________________________________________________________

    public static boolean tokenIsValid(String token, String secretKey) throws JOSEException, ParseException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        return signedJWT.verify(new MACVerifier(secretKey));
    }

    // ________________________________________________________

    public static boolean tokenNotExpired(String token) throws ParseException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        Date expiration = signedJWT.getJWTClaimsSet().getExpirationTime();
        return expiration != null && expiration.after(new Date());
    }

    // ________________________________________________________

    public static UserDTO getUserFromToken(String token) throws ParseException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        String username = signedJWT.getJWTClaimsSet().getStringClaim("username");
        return new UserDTO(Main.setup.getUserDAO().getByUsername(username));
    }
}