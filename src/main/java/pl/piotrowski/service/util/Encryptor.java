package pl.piotrowski.service.util;

import javax.crypto.SecretKey;
import java.security.Key;

public interface Encryptor {
    String encrypt(String string, Key key);
    SecretKey generateKey();
}
