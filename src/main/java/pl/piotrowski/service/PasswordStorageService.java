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

    public void save(Account account) {
        accountsSet.add(account);
        try {
            persist();
        } catch (IOException e) {
            accountsSet.remove(account);
            e.printStackTrace();
        }
    }

    private void persist() throws IOException {
        String encryptedString=null;
        try {
            String string = objectMapper.writeValueAsString(accountsSet);
             encryptedString = encryptionService.getEncryption(string);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Files.write(Paths.get(encryptedFile.toURI()), encryptedString.getBytes());
    }

    public Account[] load(){
        Account[] loadedAccounts = null;
        try {
            String encryptedString = new String(Files.readAllBytes(Paths.get(encryptedFile.toURI())));
            System.out.println(encryptionService.getDecryption(encryptedString));
            accountsSet =  objectMapper.readValue(encryptionService.getDecryption(encryptedString), new TypeReference<HashSet<Account>>(){});
            loadedAccounts = new  Account[accountsSet.toArray().length];
        } catch (IOException e) {
            e.printStackTrace();
        }
        return accountsSet.toArray(loadedAccounts);
    }
}
