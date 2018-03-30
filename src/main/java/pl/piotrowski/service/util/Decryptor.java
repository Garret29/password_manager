package pl.piotrowski.service.util;

import java.security.Key;

public interface Decryptor {
    String decrypt(String string, Key key);
}
