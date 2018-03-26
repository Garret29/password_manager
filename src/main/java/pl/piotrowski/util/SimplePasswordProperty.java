package pl.piotrowski.util;

public class SimplePasswordProperty {
    private char[] password;

    public SimplePasswordProperty(char[] password) {
        this.password = password;
    }

    public SimplePasswordProperty(String password) {
        this.password = password.toCharArray();
    }

    public char[] getPassword() {
        return password;
    }

    public void setPassword(char[] password) {
        this.password = password;
    }

    public void setPassword(String password) {
        this.password = password.toCharArray();
    }
}
