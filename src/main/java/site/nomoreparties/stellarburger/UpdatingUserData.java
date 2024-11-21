package site.nomoreparties.stellarburger;

public class UpdatingUserData {
    private String name;
    private String email;
    private String password;


    // конструктор со всеми параметрами
    public UpdatingUserData(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    // конструктор без параметров
    public UpdatingUserData() {
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
