package pl.piotrowski.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class AuthenticationService {
    private char[] password;

    @Autowired
    public AuthenticationService(char[] password){
        this.password = password;
    }

    boolean authenticate(char[] password){
        return Arrays.equals(this.password, password);
    }
}
