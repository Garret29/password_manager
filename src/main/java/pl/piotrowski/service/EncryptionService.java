package pl.piotrowski.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.piotrowski.service.util.AESEncryptor;
import pl.piotrowski.service.util.Decryptor;
import pl.piotrowski.service.util.Encryptor;

import javax.crypto.SecretKey;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

@Service
public class EncryptionService {
    private Encryptor encryptor;
    private Decryptor decryptor;
    private KeyStore keyStore;
    private File ksFile;
    private KeyStore.PasswordProtection protParam;

    @Autowired
    public EncryptionService(Encryptor encryptor, Decryptor decryptor, KeyStore keyStore, File ksFile) {
        this.encryptor = encryptor;
        this.decryptor = decryptor;
        this.keyStore = keyStore;
        this.ksFile = ksFile;
    }

    public String getEncryption(String string) {
        SecretKey secretKey = encryptor.generateKey();
        storeSecretKey(secretKey);
        return encryptor.encrypt(string, secretKey);
    }

    public String getDecryption(String string) {
        return decryptor.decrypt(string, loadSecretKey());
    }

    public void initKeyStore(char[] password) {
        FileInputStream fileInputStream = null;

        try {
            if (ksFile.exists()) {
                fileInputStream = new FileInputStream(ksFile);
                keyStore.load(fileInputStream, password);
            } else {
                keyStore.load(null, password);
            }

            protParam = new KeyStore.PasswordProtection(password);
        } catch (IOException | NoSuchAlgorithmException | CertificateException e) {
            e.printStackTrace();
        }
    }

    public SecretKey loadSecretKey() {
        SecretKey secretKey = null;
        try {
            KeyStore.SecretKeyEntry secretKeyEntry = (KeyStore.SecretKeyEntry) keyStore.getEntry("secretKey", protParam);
            secretKey = secretKeyEntry.getSecretKey();
        } catch (NoSuchAlgorithmException | UnrecoverableEntryException | KeyStoreException e) {
            e.printStackTrace();
        }
        return secretKey;
    }

    public void storeSecretKey(SecretKey key) {
        KeyStore.SecretKeyEntry secretKeyEntry = new KeyStore.SecretKeyEntry(key);
        try {
            keyStore.setEntry("secretKey", secretKeyEntry, protParam);
            FileOutputStream fileOutputStream = new FileOutputStream(ksFile);
            keyStore.store(fileOutputStream, protParam.getPassword());
        } catch (KeyStoreException | NoSuchAlgorithmException | IOException | CertificateException e) {
            e.printStackTrace();
        }
    }
}
