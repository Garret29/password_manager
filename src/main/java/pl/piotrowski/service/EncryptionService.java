package pl.piotrowski.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.piotrowski.service.util.Decryptor;
import pl.piotrowski.service.util.Encryptor;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;

@Service
public class EncryptionService {
    private Encryptor encryptor;
    private Decryptor decryptor;
    private PasswordStorageService passwordStorageService;

    @Autowired
    public EncryptionService(Encryptor encryptor, Decryptor decryptor) {
        this.encryptor = encryptor;
        this.decryptor = decryptor;
    }

    String getEncryption(String string) throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException {
        SecretKey secretKey = encryptor.generateKey();
        passwordStorageService.storeSecretKey(secretKey);
        return encryptor.encrypt(string, secretKey);
    }

    String getDecryption(String string) throws UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException, InvalidKeyException, BadPaddingException, NoSuchPaddingException, IllegalBlockSizeException {
        return decryptor.decrypt(string, passwordStorageService.loadSecretKey());
    }

    @Autowired
    public void setPasswordStorageService(PasswordStorageService passwordStorageService) {
        this.passwordStorageService = passwordStorageService;
    }
}
