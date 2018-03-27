package pl.piotrowski.service;

import javafx.beans.property.SimpleStringProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.Arrays;

@Service
public class AuthenticationService {

    private SimpleStringProperty password;

    @Autowired
    public AuthenticationService(SimpleStringProperty loginPassword){
        this.password = loginPassword;
    }

    public boolean authenticate(String password){
        return this.password.get().equals(password);
    }
}
