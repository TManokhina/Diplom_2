package site.nomoreparties.stellarburger.user.auth;

public class UserCredentials {
    private String email;
    private String password;
    private String name;

    public UserCredentials(String name, String email, String password) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public UserCredentials() {
    }

    public UserCredentials withEmail(String email) {
        this.email = email;
        return this;
    }
    public UserCredentials withPassword(String password){
        this.password = password;
        return this;
    }
    public UserCredentials withName(String name){
        this.name = name;
        return this;
    }

}
