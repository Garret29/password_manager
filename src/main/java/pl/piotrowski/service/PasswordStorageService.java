package pl.piotrowski.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.piotrowski.model.Account;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.HashSet;

@Service
public class PasswordStorageService {
    private EncryptionService encryptionService;
    private HashSet<Account> accountsSet;
    private File encryptedFile;
    private ObjectMapper objectMapper;
    private KeyStore keyStore;
    private File ksFile;
    private KeyStore.ProtectionParameter protParam;

    @Autowired
    public PasswordStorageService(HashSet<Account> accountsSet, File encryptedFile, ObjectMapper objectMapper, KeyStore keyStore, File ksFile) {
        this.accountsSet = accountsSet;
        this.encryptedFile = encryptedFile;
        this.objectMapper = objectMapper;
        this.keyStore = keyStore;
        this.ksFile = ksFile;
    }

    public void save(Account account) throws IOException, CertificateException, NoSuchAlgorithmException, KeyStoreException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, InvalidKeyException {
        accountsSet.add(account);
        persist();
    }

    public void persist() throws IOException, CertificateException, NoSuchAlgorithmException, KeyStoreException, InvalidKeyException, BadPaddingException, NoSuchPaddingException, IllegalBlockSizeException {
        String encryptedString;
        String string = objectMapper.writeValueAsString(accountsSet);
        encryptedString = encryptionService.getEncryption(string);

        Files.write(Paths.get(encryptedFile.toURI()), encryptedString.getBytes());
    }

    public void initKeyStore(char[] password) throws IOException, NoSuchAlgorithmException, CertificateException, KeyStoreException {

        if (ksFile.exists()) {
            FileInputStream fileInputStream = new FileInputStream(ksFile);
            keyStore.load(fileInputStream, password);
        } else {
            ksFile.createNewFile();
            keyStore.load(null, password);
            FileOutputStream fileOutputStream = new FileOutputStream(ksFile);
            keyStore.store(fileOutputStream, password);
        }

        protParam = new KeyStore.PasswordProtection(password);
    }

    SecretKey loadSecretKey() throws NoSuchAlgorithmException, UnrecoverableEntryException, KeyStoreException {
        KeyStore.SecretKeyEntry secretKeyEntry = (KeyStore.SecretKeyEntry) keyStore.getEntry("secretKey", protParam);
        return secretKeyEntry.getSecretKey();
    }

    void storeSecretKey(SecretKey key) throws KeyStoreException, NoSuchAlgorithmException, IOException, CertificateException {
        KeyStore.SecretKeyEntry secretKeyEntry = new KeyStore.SecretKeyEntry(key);
        keyStore.setEntry("secretKey", secretKeyEntry, protParam);
        FileOutputStream fileOutputStream = new FileOutputStream(ksFile);
        keyStore.store(fileOutputStream, ((KeyStore.PasswordProtection) protParam).getPassword());
    }

    public Account[] load() throws IOException, UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, InvalidKeyException {
        String encryptedString = new String(Files.readAllBytes(Paths.get(encryptedFile.toURI())));
        accountsSet = objectMapper.readValue(encryptionService.getDecryption(encryptedString), new TypeReference<HashSet<Account>>() {
        });

        return getAccounts();
    }

    public void remove(Account account) throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, InvalidKeyException {
        accountsSet.remove(account);
        persist();
    }

    public void changePassword(char[] oldPassword, char[] newPassword) throws IOException, CertificateException, NoSuchAlgorithmException, KeyStoreException, UnrecoverableEntryException {

        InputStream keyStoreStream = new FileInputStream(ksFile);
        keyStore.load(keyStoreStream, oldPassword);

        KeyStore.SecretKeyEntry secretKeyEntry = (KeyStore.SecretKeyEntry) keyStore.getEntry("secretKey", protParam);
        protParam = new KeyStore.PasswordProtection(newPassword);
        keyStore.setEntry("secretKey", secretKeyEntry, protParam);

        FileOutputStream fileOutputStream = new FileOutputStream(ksFile);
        keyStore.store(fileOutputStream, newPassword);
        fileOutputStream.close();
    }

    private Account[] getAccounts() {
        Account[] accounts = new Account[accountsSet.toArray().length];
        return accountsSet.toArray(accounts);
    }

    @Autowired
    public void setEncryptionService(EncryptionService encryptionService) {
        this.encryptionService = encryptionService;
    }
}
