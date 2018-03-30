package pl.piotrowski.model;


import java.util.Objects;

public class Account {
    private String name;
    private String password;

    public Account() {
    }

    public Account(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
