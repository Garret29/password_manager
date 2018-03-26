package pl.piotrowski.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import pl.piotrowski.service.AuthenticationService;

import java.io.IOException;

public class LoginViewController {
    private AuthenticationService authenticationService;

    @FXML
    public PasswordField passwordField;

    public void login(ActionEvent actionEvent) {

        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("views/mainView.fxml"));

        Parent pane = null;
        Stage stage = new Stage();
        try {
            pane = loader.load();
            Scene scene = new Scene(pane);
            stage.setTitle("Password manager");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Autowired
    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }
}

