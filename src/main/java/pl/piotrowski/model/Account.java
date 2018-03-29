package pl.piotrowski.model;


import javafx.beans.property.SimpleStringProperty;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Account)) return false;
        Account account = (Account) o;
        return Objects.equals(getName(), account.getName()) &&
                Objects.equals(getPassword(), account.getPassword());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getName(), getPassword());
    }
}
