package pl.piotrowski.service.util;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.util.Base64;

public class AESEncryptor implements Encryptor, Decryptor {

    private final String ALGO = "AES";
    private byte[] key;

    public AESEncryptor(SecureRandom secureRandom) {
        init(secureRandom);
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
        return new SecretKeySpec(key, "AES");
    }

    private void init(SecureRandom secureRandom){
        KeyGenerator keyGenerator;
        try {
            keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(128, secureRandom);
            key = keyGenerator.generateKey().getEncoded();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }


}
