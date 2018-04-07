package pl.piotrowski.controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.piotrowski.model.Account;
import pl.piotrowski.service.EncryptionService;
import pl.piotrowski.service.PasswordStorageService;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Arrays;

@Component
public class Controller {

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
    public PasswordField loginPasswordField;
    @FXML
    public PasswordField passwordField;
    private PasswordStorageService passwordStorageService;
    private EncryptionService encryptionService;
    private FXMLLoader loader;
    @FXML
    private TextField accountField;
    @FXML
    private TableView<Account> tableView;

    private ObservableList<Account> accounts;
    private ObservableSet<Account> accountWithVisiblePassword;

    private File encryptedFile;
    private File ksFile;

    public static String mask(String string, char ch) {
        char[] chars = new char[string.length()];
        Arrays.fill(chars, ch);
        return new String(chars);
    }

    public void addAccount() {
        if (!accountField.getText().isEmpty() && !passwordField.getText().isEmpty()) {

            Account account = new Account(accountField.getText(), passwordField.getText());

            try {
                passwordStorageService.save(account);
                if (!accounts.contains(account)) {
                    accounts.add(account);
                    tableView.setItems(accounts);
                    tableView.refresh();
                }

                accountField.clear();
                passwordField.clear();
            } catch (IOException | KeyStoreException | NoSuchAlgorithmException | CertificateException | BadPaddingException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException e) {
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
            firstLoginButton.setDefaultButton(true);
        } else {
            loginButton.setDefaultButton(true);
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
            if (encryptedFile.exists()) {
                accounts.addAll(passwordStorageService.load());
            }
            initMainView(node);
        } catch (IOException e) {
            if (e.getMessage().contains("password was incorrect")) {
                wrongLabel.setVisible(true);
            } else {
                showExceptionDialog(e);
            }
        } catch (CertificateException | NoSuchAlgorithmException | UnrecoverableEntryException | KeyStoreException | BadPaddingException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException e) {
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
        stage.setResizable(true);
        source.hide();
        stage.show();

        TableColumn<Account, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));

        TableColumn<Account, String> passwordCol = new TableColumn<>("Password");
        passwordCol.setCellValueFactory(cellData -> {
            Account account = cellData.getValue();
            if (!accountWithVisiblePassword.contains(account)) {
                char[] chars = new char[account.getPassword().length()];
                Arrays.fill(chars, '*');
                return new SimpleStringProperty(new String(chars));
            } else {
                return new SimpleStringProperty(account.getPassword());
            }

        });

        passwordCol.setCellFactory(c -> {
            TextFieldTableCell<Account, String> textFieldTableCell = new TextFieldTableCell<>();
            textFieldTableCell.setConverter(new StringConverter<String>() {
                @Override
                public String toString(String object) {
                    return object;
                }

                @Override
                public String fromString(String string) {
                    return string;
                }
            });
            textFieldTableCell.indexProperty().addListener(
                    (obs, oldItem, newItem) -> updateCell(textFieldTableCell)
            );

            textFieldTableCell.itemProperty().addListener(
                    (obs, oldItem, newItem) -> updateCell(textFieldTableCell)
            );

            textFieldTableCell.editingProperty().addListener((obs, oldItem, newItem) -> {
                Account account = tableView.getItems().get(textFieldTableCell.getIndex());
                textFieldTableCell.setText(account.getPassword());
            });

            accountWithVisiblePassword.addListener((SetChangeListener<Account>) change -> updateCell(textFieldTableCell));

            tableView.editableProperty().addListener((obs, oldItem, newItem) -> {
                int index = textFieldTableCell.getIndex();
                if (index >= 0 && index < tableView.getItems().size()) {
                    Account account = tableView.getItems().get(textFieldTableCell.getIndex());
                    if (newItem) {
                        accountWithVisiblePassword.add(account);
                    } else {
                        accountWithVisiblePassword.remove(account);
                    }
                }
            });

            return textFieldTableCell;
        });

        passwordCol.setOnEditCommit(
                event -> {
                    Account account = event.getTableView().getItems().get(event.getTablePosition().getRow());
                    account.setPassword(event.getNewValue());
                    try {
                        passwordStorageService.persist();
                    } catch (IOException | CertificateException | KeyStoreException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException | NoSuchPaddingException e) {
                        account.setPassword(event.getOldValue());
                        showExceptionDialog(e);
                    }
                }
        );

        TableColumn<Account, Boolean> actionShowCol = new TableColumn<>("Show");
        TableColumn<Account, Boolean> actionDelCol = new TableColumn<>("Delete");
        TableColumn<Account, Boolean> actionCopyCol = new TableColumn<>("Copy");
        actionShowCol.setSortable(false);
        actionDelCol.setSortable(false);
        actionShowCol.setSortable(false);

        actionShowCol.setCellValueFactory(features -> new SimpleBooleanProperty(features.getValue() != null));
        actionDelCol.setCellValueFactory(features -> new SimpleBooleanProperty(features.getValue() != null));
        actionCopyCol.setCellValueFactory(features -> new SimpleBooleanProperty(features.getValue() != null));

        actionDelCol.setCellFactory(booleanTableColumn -> new DeleteActionCell(tableView));
        actionShowCol.setCellFactory(booleanTableColumn -> new ShowActionCell(tableView));
        actionCopyCol.setCellFactory(booleanTableColumn -> new CopyActionCell(tableView));

        nameCol.setResizable(true);
        passwordCol.setResizable(true);
        actionDelCol.setResizable(false);
        actionShowCol.setResizable(false);
        actionCopyCol.setResizable(false);

        tableView.getColumns().add(nameCol);
        tableView.getColumns().add(passwordCol);
        tableView.getColumns().add(actionShowCol);
        tableView.getColumns().add(actionDelCol);
        tableView.getColumns().add(actionCopyCol);

        tableView.setItems(accounts);
        tableView.refresh();
    }

    private void updateCell(TextFieldTableCell<Account, String> textFieldTableCell) {
        int index = textFieldTableCell.getIndex();
        if (index >= 0 && index < tableView.getItems().size()) {
            Account account = tableView.getItems().get(index);
            if (accountWithVisiblePassword.contains(account)) {
                textFieldTableCell.setText(account.getPassword());
            } else {
                textFieldTableCell.setText(mask(account.getPassword(), '*'));
            }
        }
    }

    private void remove(Account account) {
        try {
            passwordStorageService.remove(account);
            accounts.remove(account);
        } catch (CertificateException | IOException | KeyStoreException | NoSuchAlgorithmException | BadPaddingException | InvalidKeyException | IllegalBlockSizeException | NoSuchPaddingException e) {
            showExceptionDialog(e);
        }
    }

    private boolean isFirstLogin() {
        return !ksFile.exists();
    }

    @Autowired
    public void setAccounts(ObservableList<Account> accounts) {
        this.accounts = accounts;
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

    public void setEditing(ActionEvent actionEvent) {
        tableView.setEditable(((CheckBox) actionEvent.getSource()).selectedProperty().getValue());
    }

    public ObservableSet<Account> getAccountWithVisiblePassword() {
        return accountWithVisiblePassword;
    }

    @Autowired
    public void setAccountWithVisiblePassword(ObservableSet<Account> accountWithVisiblePassword) {
        this.accountWithVisiblePassword = accountWithVisiblePassword;
    }

    @Autowired
    public void setEncryptedFile(File encryptedFile) {
        this.encryptedFile = encryptedFile;
    }

    @Autowired
    public void setKsFile(File ksFile) {
        this.ksFile = ksFile;
    }

    private class CopyActionCell extends TableCell<Account, Boolean> {
        final Button copyButton = new Button("Copy");
        final StackPane paddedButton = new StackPane();

        CopyActionCell(final TableView<Account> table) {
            paddedButton.setPadding(new Insets(3));
            paddedButton.getChildren().add(copyButton);
            copyButton.setOnAction(actionEvent -> {
                int index = getTableRow().getIndex();
                table.getSelectionModel().select(index);
                Account account = table.getSelectionModel().getSelectedItem();
                Clipboard clipboard = Clipboard.getSystemClipboard();
                ClipboardContent clipboardContent = new ClipboardContent();
                clipboardContent.putString(account.getPassword());
                clipboard.setContent(clipboardContent);
                Thread thread = new Thread(()->{
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        Platform.runLater(clipboard::clear);
                    }
                });
                thread.start();

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

    private class ShowActionCell extends TableCell<Account, Boolean> {
        final Button showButton = new Button("Show");
        final StackPane paddedButton = new StackPane();

        ShowActionCell(final TableView<? extends Account> table) {
            paddedButton.setPadding(new Insets(3));
            paddedButton.getChildren().add(showButton);

            showButton.setOnMousePressed(actionEvent -> {
                int index = getTableRow().getIndex();
                table.getSelectionModel().select(index);
                accountWithVisiblePassword.add(table.getSelectionModel().getSelectedItem());
            });

            showButton.setOnMouseReleased(actionEvent -> {
                int index = getTableRow().getIndex();
                table.getSelectionModel().select(index);
                accountWithVisiblePassword.remove(table.getSelectionModel().getSelectedItem());
            });

            table.editableProperty().addListener((observable, oldValue, newValue) -> showButton.setDisable(newValue));
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
}

