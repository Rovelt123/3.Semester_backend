package app.services;

import org.mindrot.jbcrypt.BCrypt;

public class HashService {

    public static boolean hashEquals(String hash, String enteredHash) {
        return BCrypt.checkpw(hash, enteredHash);
    }

    // ________________________________________________________

    public static String hashHelper(String hash){
        return BCrypt.hashpw(hash, BCrypt.gensalt());
    }
}
