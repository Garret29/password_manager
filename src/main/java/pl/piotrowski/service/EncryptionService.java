package pl.piotrowski.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.piotrowski.service.util.AESEncryptor;
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

    public String getEncryption(String string) throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException {
        SecretKey secretKey = encryptor.generateKey();
        storeSecretKey(secretKey);
        return encryptor.encrypt(string, secretKey);
    }

    public String getDecryption(String string) throws UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException, InvalidKeyException, BadPaddingException, NoSuchPaddingException, IllegalBlockSizeException {
        return decryptor.decrypt(string, loadSecretKey());
    }

    public void initKeyStore(char[] password) throws IOException, NoSuchAlgorithmException, CertificateException {
        FileInputStream fileInputStream = null;

        if (ksFile.exists()) {
            fileInputStream = new FileInputStream(ksFile);
            keyStore.load(fileInputStream, password);
        } else {
            keyStore.load(null, password);
        }

        protParam = new KeyStore.PasswordProtection(password);
    }

    public SecretKey loadSecretKey() throws NoSuchAlgorithmException, UnrecoverableEntryException, KeyStoreException {
        SecretKey secretKey;
        KeyStore.SecretKeyEntry secretKeyEntry = (KeyStore.SecretKeyEntry) keyStore.getEntry("secretKey", protParam);
        secretKey = secretKeyEntry.getSecretKey();
        return secretKey;
    }

    public void storeSecretKey(SecretKey key) throws KeyStoreException, NoSuchAlgorithmException, IOException, CertificateException {
        KeyStore.SecretKeyEntry secretKeyEntry = new KeyStore.SecretKeyEntry(key);
        keyStore.setEntry("secretKey", secretKeyEntry, protParam);
        FileOutputStream fileOutputStream = new FileOutputStream(ksFile);
        keyStore.store(fileOutputStream, protParam.getPassword());
    }
}
