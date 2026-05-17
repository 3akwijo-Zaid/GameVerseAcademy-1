package ma.ac.esi.gameverseacademy.model;

public class User {
    private String email;
    private String login;
    private String password;
    private String role;

    public User() {}

    public User(String email, String password, String login, String role) {
        this.email = email;
        this.password = password;
        this.login = login;
        this.role = role;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    @Override
    public String toString() {
        return "User [login=" + login + ", role=" + role + "]";
    }
}