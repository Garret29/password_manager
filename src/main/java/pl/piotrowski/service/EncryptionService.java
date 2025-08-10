package pl.piotrowski.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.piotrowski.service.util.Decryptor;
import pl.piotrowski.service.util.Encryptor;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.security.*;

@Service
public class EncryptionService {
    private final Encryptor encryptor;
    private final Decryptor decryptor;

    @Autowired
    public EncryptionService(Encryptor encryptor, Decryptor decryptor) {
        this.encryptor = encryptor;
        this.decryptor = decryptor;
    }

    public String getEncryption(String string, Key key) throws NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException {
        return encryptor.encrypt(string, key);
    }

    public String getDecryption(String string, Key key) throws NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, NoSuchPaddingException, IllegalBlockSizeException {
        return decryptor.decrypt(string, key);
    }

    public SecretKey generateSecretKey() {
        return encryptor.generateKey();
    }
}
