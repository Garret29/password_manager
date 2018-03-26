package pl.piotrowski.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Callback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.piotrowski.model.Account;
import pl.piotrowski.service.AuthenticationService;
import pl.piotrowski.util.SimplePasswordProperty;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Component
public class ViewController{
    private AuthenticationService authenticationService;
    private FXMLLoader loader;

    @FXML
    private PasswordField loginPasswordField;
    @FXML
    public Label wrongLabel;
    @FXML
    public CheckBox editCheckbox;
    @FXML
    private TextField acountField;
    @FXML
    public PasswordField passwordField;
    @FXML
    private TableView<Account> tableView;

    private ObservableList<Account> accounts;

    public void addAccount(ActionEvent actionEvent) {
        if(!acountField.getText().isEmpty() || !passwordField.getText().isEmpty()){
            accounts.add(new Account(new SimpleStringProperty(acountField.getText()), new SimplePasswordProperty(passwordField.getText().toCharArray())));
            tableView.setItems(accounts);
        }
    }

    public void login(ActionEvent actionEvent) {
        if (authenticationService.authenticate(loginPasswordField.getText().toCharArray())) {
            Parent pane;
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
                initMainView();

            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            wrongLabel.setVisible(true);
        }
    }

    private void initMainView(){
        TableColumn nameCol = new TableColumn("Name");
        tableView.getColumns().add(nameCol);
        TableColumn passwordCol = new TableColumn("Password");
        tableView.getColumns().add(passwordCol);
        tableView.refresh();
    }

    @Autowired
    public void setAccounts(ObservableList<Account> accounts) {
        this.accounts = accounts;
    }

    @Autowired
    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    public void changeControllerFactory(Callback<Class<?>, Object> callback){
        loader.setControllerFactory(callback);
    }

    @Autowired
    public void setLoader(FXMLLoader loader) {
        this.loader = loader;
    }
}

