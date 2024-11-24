package site.nomoreparties.stellarburger.user.auth;

import site.nomoreparties.stellarburger.user.update.User;

public class UserAuthData {

    private boolean success;
    private User user;
    private String accessToken;
    private String refreshToken;

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public boolean isSuccess() {
        return success;
    }

    public User getUser() {
        return user;
    }
}
