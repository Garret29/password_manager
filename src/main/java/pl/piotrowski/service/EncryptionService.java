package pl.piotrowski.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.piotrowski.service.util.Decryptor;
import pl.piotrowski.service.util.EncryptedPassword;
import pl.piotrowski.service.util.Encryptor;

@Service
public class EncryptionService {
    private Encryptor encryptor;
    private Decryptor decryptor;
    private PasswordStorageService passwordStorageService;

    @Autowired
    public EncryptionService(Encryptor encryptor, Decryptor decryptor, PasswordStorageService passwordStorageService) {
        this.encryptor = encryptor;
        this.decryptor = decryptor;
        this.passwordStorageService = passwordStorageService;
    }

    public EncryptedPassword encrypt(String password) {
        EncryptedPassword encryptedPassword = new EncryptedPassword();

        return encryptedPassword;
    }
}
