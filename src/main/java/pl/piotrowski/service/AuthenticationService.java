package pl.piotrowski.service;

import javafx.beans.property.SimpleStringProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {


    public boolean authenticate(String password){
//        return this.password.get().equals(password);
        return true;
    }
}
