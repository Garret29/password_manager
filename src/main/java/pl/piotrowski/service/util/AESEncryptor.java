package pl.piotrowski.service.util;

import org.springframework.beans.factory.annotation.Autowired;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class AESEncryptor implements Encryptor, Decryptor {

    private final String ALGO = "AES";
    private byte[] key;

    public AESEncryptor(SecureRandom secureRandom) {
        init(secureRandom);
    }

    public String encrypt(String string) {
        Key key = generateKey();
        byte[] encrypted = null;
        try {
            Cipher cipher = Cipher.getInstance(ALGO);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            encrypted = cipher.doFinal(string.getBytes());

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }

        return Base64.getEncoder().encodeToString(encrypted);
    }

    public String decrypt(String string) {
        Key key = generateKey();
        byte[] decrypted = null;
        try {
            Cipher cipher = Cipher.getInstance(ALGO);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decoded = Base64.getDecoder().decode(string);
            decrypted = cipher.doFinal(decoded);
        } catch (NoSuchAlgorithmException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
        return new String(decrypted);
    }

    private Key generateKey() {
        return new SecretKeySpec(key, ALGO);
    }

    private void init(SecureRandom secureRandom){
        KeyGenerator keyGenerator;
        try {
            keyGenerator = KeyGenerator.getInstance(ALGO);
            keyGenerator.init(256, secureRandom);
            key = keyGenerator.generateKey().getEncoded();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }


}
