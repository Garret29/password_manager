package pl.piotrowski.controller;

import javafx.beans.property.SimpleBooleanProperty;
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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Callback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.piotrowski.model.Account;
import pl.piotrowski.service.AuthenticationService;
import pl.piotrowski.service.EncryptionService;
import pl.piotrowski.service.PasswordStorageService;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

@Component
public class Controller {

    private AuthenticationService authenticationService;
    private PasswordStorageService passwordStorageService;
    private EncryptionService encryptionService;
    private FXMLLoader loader;

    @FXML
    public Button loginButton;
    @FXML
    public Button firstLoginButton;
    @FXML
    public PasswordField confirmPasswordField;
    @FXML
    public Label confirmLabel;
    @FXML
    public Label newPasswordLabel;
    @FXML
    public Label matchLabel;
    @FXML
    public Label wrongLabel;
    @FXML
    public CheckBox editCheckbox;
    @FXML
    private TextField accountField;
    @FXML
    public PasswordField loginPasswordField;
    @FXML
    private TableView<Account> tableView;
    @FXML
    public PasswordField passwordField;

    private ObservableList<Account> accounts;

    public void addAccount() {
        if (!accountField.getText().isEmpty() && !passwordField.getText().isEmpty()) {

            Account account = new Account(accountField.getText(), passwordField.getText());

            try {
                passwordStorageService.save(account);
                if (!accounts.contains(account)){
                    accounts.add(account);
                }
                tableView.setItems(accounts);
                tableView.refresh();

                accountField.clear();
                passwordField.clear();
            } catch (IOException | KeyStoreException | NoSuchAlgorithmException | CertificateException e) {
                showExceptionDialog(e);
            }
        }
    }

    public void initLoginView() {
        if (isFirstLogin()) {
            confirmPasswordField.setVisible(true);
            confirmLabel.setVisible(true);
            newPasswordLabel.setVisible(true);
            loginButton.setVisible(false);
            firstLoginButton.setVisible(true);
        }
    }

    public void firstLogin(ActionEvent actionEvent) {
        if (!loginPasswordField.getText().isEmpty() && !confirmPasswordField.getText().isEmpty() && loginPasswordField.getText().equals(confirmPasswordField.getText())) {
            try {
                initServices();
            } catch (NoSuchAlgorithmException | IOException | CertificateException e) {
                showExceptionDialog(e);
            }
            Node node = (Node) actionEvent.getSource();
            initMainView(node);
        } else {
            matchLabel.setVisible(true);
        }
    }

    private void showExceptionDialog(Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(e.getMessage());
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        String text = stringWriter.toString();
        Label label = new Label("Error details:");
        TextArea textArea = new TextArea(text);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        alert.getDialogPane().setExpandableContent(expContent);

        alert.showAndWait();
    }

    public void login(ActionEvent actionEvent) {

            Node node = (Node) actionEvent.getSource();
            try {
                initServices();
                accounts.addAll(passwordStorageService.load());
                initMainView(node);
            } catch (IOException e) {
                if(e.getCause() instanceof UnrecoverableEntryException){
                    wrongLabel.setVisible(true);
                } else {
                    showExceptionDialog(e);
                }
            } catch (CertificateException | NoSuchAlgorithmException | UnrecoverableEntryException | KeyStoreException e) {
                showExceptionDialog(e);
            }
    }

    private void initServices() throws NoSuchAlgorithmException, IOException, CertificateException {
        encryptionService.initKeyStore(loginPasswordField.getText().toCharArray());
    }


    private void initMainView(Node node) {
        Parent pane;
        Stage stage = new Stage();
        Window source = node.getScene().getWindow();
        try {
            pane = loader.load();
        } catch (IOException e) {
            showExceptionDialog(e);
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

        tableView.setItems(accounts);
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
                remove(table.getSelectionModel().getSelectedItem());
                table.setItems(accounts);
                table.getSelectionModel().select(index);
                table.refresh();
            });
        }

        @Override
        protected void updateItem(Boolean item, boolean empty) {
            super.updateItem(item, empty);
            if (!empty) {
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                setGraphic(paddedButton);
            } else {
                setGraphic(null);
            }
        }
    }

    private void remove(Account account){
        try {
            passwordStorageService.remove(account);
            accounts.remove(account);
        } catch (CertificateException | IOException | KeyStoreException | NoSuchAlgorithmException e) {
            showExceptionDialog(e);
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

        @Override
        protected void updateItem(Boolean item, boolean empty) {
            super.updateItem(item, empty);
            if (!empty) {
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                setGraphic(paddedButton);
            } else {
                setGraphic(null);
            }
        }
    }

    private boolean isFirstLogin() {

        File parent = new File(System.getProperty("user.home"), ".Garret29PasswordManager");
        File file = new File(parent, "ks_Data_xD");

        return !file.exists();
    }

    @Autowired
    public void setAccounts(ObservableList<Account> accounts) {
        this.accounts = accounts;
    }

    @Autowired
    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Autowired
    public void setPasswordStorageService(PasswordStorageService passwordStorageService) {
        this.passwordStorageService = passwordStorageService;
    }

    @Autowired
    public void setEncryptionService(EncryptionService encryptionService) {
        this.encryptionService = encryptionService;
    }

    public void changeControllerFactory(Callback<Class<?>, Object> callback) {
        loader.setControllerFactory(callback);
    }

    @Autowired
    public void setLoader(FXMLLoader loader) {
        this.loader = loader;
    }
}

