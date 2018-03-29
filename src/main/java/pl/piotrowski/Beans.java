package pl.piotrowski;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import pl.piotrowski.model.Account;
import pl.piotrowski.service.util.DESEncryptor;
import pl.piotrowski.service.util.Decryptor;
import pl.piotrowski.service.util.Encryptor;


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
        return new DESEncryptor();
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
}
