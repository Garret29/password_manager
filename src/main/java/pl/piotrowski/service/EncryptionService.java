package pl.piotrowski.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.piotrowski.service.util.Decryptor;
import pl.piotrowski.service.util.Encryptor;

@Service
public class EncryptionService {
    private Encryptor encryptor;
    private Decryptor decryptor;

    @Autowired
    public EncryptionService(Encryptor encryptor, Decryptor decryptor) {
        this.encryptor = encryptor;
        this.decryptor = decryptor;
    }

    public String getEncryption(String string) {
        return encryptor.encrypt(string);
    }

    public String getDecryption(String string){
         return decryptor.decrypt(string);
    }
}
