package pl.piotrowski;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import pl.piotrowski.model.Account;
import pl.piotrowski.service.util.AESEncryptor;
import pl.piotrowski.service.util.Decryptor;
import pl.piotrowski.service.util.Encryptor;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.HashSet;


@Configuration
public class Beans {
    @Bean
    public SimpleStringProperty loginPassword(){
        return new SimpleStringProperty("1234");
    }

    @Bean
    public ObservableList<Account> accounts() {
        ObservableList<Account> accounts = FXCollections.observableArrayList();

        return accounts;
    }

    @Bean
    public Encryptor encryptor(){
        return new AESEncryptor(secureRandom());
    }
    
    @Bean
    public Decryptor decryptor() {
        return (Decryptor) encryptor();
    }

    @Bean
    @Scope("prototype")
    public FXMLLoader loader(){
        return new FXMLLoader(getClass().getClassLoader().getResource("views/mainView.fxml"));
    }

    @Bean
    @Scope("prototype")
    public SecureRandom secureRandom(){
        return new SecureRandom();
    }

    @Bean
    public HashSet<Account> accountsSet(){
        return new HashSet<>();
    }

    @Bean
    public File encryptedFile(){
        File parent = new File(System.getProperty("user.home"), ".Garret29PasswordManager");
        parent.mkdirs();
        File file = new File(parent, "importantData_xD");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
}
