package pl.piotrowski.model;


import javafx.beans.property.SimpleStringProperty;

public class Account {
    private SimpleStringProperty name;
    private SimpleStringProperty password;

    public Account(javafx.beans.property.SimpleStringProperty name, SimpleStringProperty password) {
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

    public String getPassword() {
        return password.get();
    }

    public SimpleStringProperty passwordProperty() {
        return password;
    }

    public void setPassword(String password) {
        this.password.set(password);
    }
}
