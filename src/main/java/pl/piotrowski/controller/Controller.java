package pl.piotrowski.controller;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Callback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.piotrowski.model.Account;
import pl.piotrowski.service.AuthenticationService;

import java.io.IOException;

@Component
public class Controller {
    private AuthenticationService authenticationService;
    private FXMLLoader loader;
    private Stage stage;

    @FXML
    public PasswordField confirmPasswordField;
    @FXML
    public Label confirmLabel;
    @FXML
    public Label newPasswordLabel;
    @FXML
    public Label matchLabel;
    @FXML
    private PasswordField loginPasswordField;
    @FXML
    public Label wrongLabel;
    @FXML
    public CheckBox editCheckbox;
    @FXML
    private TextField accountField;
    @FXML
    public PasswordField passwordField;
    @FXML
    private TableView<Account> tableView;

    private ObservableList<Account> accounts;

    public void addAccount() {
        if (!accountField.getText().isEmpty() || !passwordField.getText().isEmpty()) {
            accounts.add(new Account(new SimpleStringProperty(accountField.getText()), new SimpleStringProperty(passwordField.getText())));
            tableView.setItems(accounts);
            tableView.refresh();

            accountField.clear();
            passwordField.clear();
        }
    }

    public void login(ActionEvent actionEvent) {
        if (authenticationService.authenticate(loginPasswordField.getText())) {
            Node node = (Node) actionEvent.getSource();
            initMainView(node);
        } else {
            wrongLabel.setVisible(true);
        }
    }

    private void initMainView(Node node) {
        Parent pane;
        stage = new Stage();
        Window source = node.getScene().getWindow();
        try {
            pane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        Scene scene = new Scene(pane);
        stage.setTitle("Password manager");
        stage.setScene(scene);
        stage.setResizable(false);
        source.hide();
        stage.show();

        TableColumn<Account, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Account, String> passwordCol = new TableColumn<>("Password");
        passwordCol.setCellValueFactory(new PropertyValueFactory<>("password"));
        TableColumn<Account, Boolean> actionShowCol = new TableColumn<>("Show");
        TableColumn<Account, Boolean> actionDelCol = new TableColumn<>("Delete");
        actionShowCol.setSortable(false);
        actionDelCol.setSortable(false);

        actionShowCol.setCellValueFactory(features -> new SimpleBooleanProperty(features.getValue() != null));
        actionDelCol.setCellValueFactory(features -> new SimpleBooleanProperty(features.getValue() != null));

        actionDelCol.setCellFactory(booleanTableColumn -> new DeleteActionCell(tableView));
        actionShowCol.setCellFactory(booleanTableColumn -> new ShowActionCell(tableView));

        tableView.getColumns().add(nameCol);
        tableView.getColumns().add(passwordCol);
        // TODO: 28.03.2018 show password button
//        tableView.getColumns().add(actionShowCol);
        tableView.getColumns().add(actionDelCol);

        tableView.refresh();
    }

    private class DeleteActionCell extends TableCell<Account, Boolean> {
        final Button deleteButton = new Button("Delete");
        final StackPane paddedButton = new StackPane();

        DeleteActionCell(final TableView<Account> table) {
            paddedButton.setPadding(new Insets(3));
            paddedButton.getChildren().add(deleteButton);
            deleteButton.setOnAction(actionEvent -> {
                int index = getTableRow().getIndex();
                table.getSelectionModel().select(index);
                accounts.remove(table.getSelectionModel().getSelectedItem());
                table.setItems(accounts);
                table.getSelectionModel().select(index);
                table.refresh();
            });
        }

        @Override protected void updateItem(Boolean item, boolean empty) {
            super.updateItem(item, empty);
            if (!empty) {
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                setGraphic(paddedButton);
            } else {
                setGraphic(null);
            }
        }
    }

    private class ShowActionCell extends TableCell<Account, Boolean> {
        final Button showButton = new Button("Show");
        final StackPane paddedButton = new StackPane();

        ShowActionCell(final TableView table) {
            paddedButton.setPadding(new Insets(3));
            paddedButton.getChildren().add(showButton);

            showButton.setOnMousePressed(actionEvent -> {
                int index = getTableRow().getIndex();
                table.getSelectionModel().select(index);

            });

            showButton.setOnMouseReleased(actionEvent -> {
                int index = getTableRow().getIndex();
                table.getSelectionModel().select(index);

            });
        }

        @Override protected void updateItem(Boolean item, boolean empty) {
            super.updateItem(item, empty);
            if (!empty) {
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                setGraphic(paddedButton);
            } else {
                setGraphic(null);
            }
        }
    }


    @Autowired
    public void setAccounts(ObservableList<Account> accounts) {
        this.accounts = accounts;
    }

    @Autowired
    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    public void changeControllerFactory(Callback<Class<?>, Object> callback) {
        loader.setControllerFactory(callback);
    }

    @Autowired
    public void setLoader(FXMLLoader loader) {
        this.loader = loader;
    }
}

