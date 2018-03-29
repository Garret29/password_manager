package pl.piotrowski.service;

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

    @Autowired
    public PasswordStorageService(EncryptionService encryptionService, HashSet<Account> accountsSet, File encryptedFile) {
        this.encryptionService = encryptionService;
        this.accountsSet = accountsSet;
        this.encryptedFile = encryptedFile;
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
        ObjectMapper objectMapper = new ObjectMapper();
        String encryptedString=null;
        try {
            String string = objectMapper.writeValueAsString(accountsSet);
             encryptedString = encryptionService.getEncryption(string);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Files.write(Paths.get(encryptedFile.toURI()), encryptedString.getBytes());
    }
}
