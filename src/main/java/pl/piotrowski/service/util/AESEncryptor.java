package pl.piotrowski.service.util;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.util.Base64;

public class AESEncryptor implements Encryptor, Decryptor {

    private final String ALGO = "AES";
    private byte[] key;
    private byte[] iv = new byte[16];

    public AESEncryptor(SecureRandom secureRandom) {
        init(secureRandom);
    }

    public String encrypt(String string, Key key) {
        byte[] encrypted = null;
        try {
            Cipher cipher = Cipher.getInstance(ALGO);
//            cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
            cipher.init(Cipher.ENCRYPT_MODE, key);
            encrypted = cipher.doFinal(string.getBytes());

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }

        return Base64.getEncoder().encodeToString(encrypted);
    }

    public String decrypt(String string, Key key) {

        byte[] decrypted = null;
        try {
            Cipher cipher = Cipher.getInstance(ALGO);
//            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decoded = Base64.getDecoder().decode(string);
            decrypted = cipher.doFinal(decoded);
        } catch (NoSuchAlgorithmException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
        return new String(decrypted);
    }

    public SecretKey generateKey() {
        return new SecretKeySpec(key, "AES");
    }

    private void init(SecureRandom secureRandom){
        KeyGenerator keyGenerator;
        secureRandom.nextBytes(iv);
        try {
            keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(128, secureRandom);
            key = keyGenerator.generateKey().getEncoded();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }


}
