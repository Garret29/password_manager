package pl.piotrowski.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.stage.Window;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.piotrowski.service.AuthenticationService;

import java.io.IOException;

@Component
public class LoginViewController {
    private AuthenticationService authenticationService;
    @FXML
    public PasswordField passwordField;

    public void login(ActionEvent actionEvent) {
        if (authenticationService.authenticate(passwordField.getText().toCharArray())){
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("views/mainView.fxml"));

            Parent pane = null;
            Stage stage = new Stage();
            Node node = (Node) actionEvent.getSource();
            Window source = node.getScene().getWindow();
            try {
                pane = loader.load();
                Scene scene = new Scene(pane);
                stage.setTitle("Password manager");
                stage.setScene(scene);
                stage.setResizable(false);
                source.hide();
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Autowired
    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }
}

