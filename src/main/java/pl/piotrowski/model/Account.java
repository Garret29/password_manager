package pl.piotrowski.model;

import javafx.beans.property.SimpleStringProperty;
import pl.piotrowski.util.SimplePasswordProperty;

public class Account {
    private SimpleStringProperty name;
    private SimplePasswordProperty password;

    public Account(SimpleStringProperty name, SimplePasswordProperty password) {
        this.name = name;
        this.password = password;
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public SimplePasswordProperty getPassword() {
        return password;
    }

    public void setPassword(SimplePasswordProperty password) {
        this.password = password;
    }
}
