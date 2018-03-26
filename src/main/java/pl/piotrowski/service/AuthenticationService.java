package pl.piotrowski.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.piotrowski.util.SimplePasswordProperty;

import java.util.Arrays;

@Service
public class AuthenticationService {

    private SimplePasswordProperty password;

    @Autowired
    public AuthenticationService(SimplePasswordProperty loginPassword){
        this.password = loginPassword;
    }

    public boolean authenticate(char[] password){
        return Arrays.equals(this.password.getPassword(), password);
    }
}
