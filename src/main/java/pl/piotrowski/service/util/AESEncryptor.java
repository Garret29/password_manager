package pl.piotrowski.service.util;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class AESEncryptor implements Encryptor, Decryptor {

    private final String ALGO = "AES";

    public AESEncryptor() {
    }

    public String encrypt(String string, Key key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        byte[] encrypted;
        Cipher cipher = Cipher.getInstance(ALGO);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        encrypted = cipher.doFinal(string.getBytes());

        return Base64.getEncoder().encodeToString(encrypted);
    }

    public String decrypt(String string, Key key) throws NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchPaddingException {
        byte[] decrypted;
        Cipher cipher = Cipher.getInstance(ALGO);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decoded = Base64.getDecoder().decode(string);
        decrypted = cipher.doFinal(decoded);
        return new String(decrypted);
    }

    public SecretKey generateKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGO);
            keyGenerator.init(128, new SecureRandom());
            byte[] key = keyGenerator.generateKey().getEncoded();
            return new SecretKeySpec(key, ALGO);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
