package pl.piotrowski;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import pl.piotrowski.model.Account;


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
    @Scope("prototype")
    public FXMLLoader loader(){
        return new FXMLLoader(getClass().getClassLoader().getResource("views/mainView.fxml"));
    }
}
