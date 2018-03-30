package pl.piotrowski.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.piotrowski.model.Account;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.HashSet;

@Service
public class PasswordStorageService {
    private EncryptionService encryptionService;
    private HashSet<Account> accountsSet;
    private File encryptedFile;
    private ObjectMapper objectMapper;

    @Autowired
    public PasswordStorageService(EncryptionService encryptionService, HashSet<Account> accountsSet, File encryptedFile, ObjectMapper objectMapper) {
        this.encryptionService = encryptionService;
        this.accountsSet = accountsSet;
        this.encryptedFile = encryptedFile;
        this.objectMapper = objectMapper;
    }

    public void save(Account account) throws IOException, CertificateException, NoSuchAlgorithmException, KeyStoreException {
        accountsSet.add(account);
        persist();
    }

    private void persist() throws IOException, CertificateException, NoSuchAlgorithmException, KeyStoreException {
        String encryptedString;
        String string = objectMapper.writeValueAsString(accountsSet);
        encryptedString = encryptionService.getEncryption(string);

        Files.write(Paths.get(encryptedFile.toURI()), encryptedString.getBytes());
    }

    public Account[] load() throws IOException, UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException {
        String encryptedString = new String(Files.readAllBytes(Paths.get(encryptedFile.toURI())));
        accountsSet =  objectMapper.readValue(encryptionService.getDecryption(encryptedString), new TypeReference<HashSet<Account>>(){});

        return getAccounts();
    }

    public void remove(Account account) throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException {
        accountsSet.remove(account);
        persist();
    }

    public Account[] getAccounts(){
        Account[] accounts = new Account[accountsSet.toArray().length];
        return accountsSet.toArray(accounts);
    }
}
