package pl.piotrowski;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.fxml.FXMLLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import pl.piotrowski.model.Account;
import pl.piotrowski.service.util.AESEncryptor;
import pl.piotrowski.service.util.Decryptor;
import pl.piotrowski.service.util.Encryptor;

import java.io.File;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.SecureRandom;
import java.util.HashSet;


@Configuration
public class Beans {

    @Bean
    public ObservableList<Account> accounts() {
        return FXCollections.observableArrayList();
    }

    @Bean
    public Encryptor encryptor() {
        return new AESEncryptor();
    }

    @Bean
    public Decryptor decryptor() {
        return (Decryptor) encryptor();
    }

    @Bean
    @Scope("prototype")
    public FXMLLoader loader() {
        return new FXMLLoader(getClass().getClassLoader().getResource("views/mainView.fxml"));
    }

    @Bean
    public HashSet<Account> accountsSet() {
        return new HashSet<>();
    }

    @Bean
    public File encryptedFile() {
        File parent = new File(System.getProperty("user.home"), ".Garret29PasswordManager");
        return new File(parent, "data_01");
    }

    @Bean
    @Scope("prototype")
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public KeyStore keyStore() throws KeyStoreException {
        return KeyStore.getInstance("JCEKS");
    }

    @Bean
    public File ksFile() {
        File parent = new File(System.getProperty("user.home"), ".Garret29PasswordManager");
        parent.mkdir();
        return new File(parent, "data_02");
    }

    @Bean
    public ObservableSet<Account> accountWithVisiblePassword() {
        return FXCollections.observableSet();
    }
}
