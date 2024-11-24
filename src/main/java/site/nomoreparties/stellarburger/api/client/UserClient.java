package site.nomoreparties.stellarburger.api.client;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import site.nomoreparties.stellarburger.user.auth.UserCredentials;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.apache.http.HttpHeaders.AUTHORIZATION;

public class UserClient implements Client {
    public static final String REGISTRATION_USER_PATH = "/api/auth/register";
    public static final String AUTHORIZATION_USER_PATH = "/api/auth/login";
    public static final String UPDATING_USER_DATA_PATH = "/api/auth/user";

    @Step("Send POST request to create user.")
    public static Response createUserResponse(UserCredentials userCredentials) {
        return given()
                .contentType(JSON)
                .and()
                .body(userCredentials)
                .post(REGISTRATION_USER_PATH);
    }

    @Step("Send POST request to authorize user.")
    public static Response authUserResponse(UserCredentials userCredentials) {
        return given()
                .contentType(JSON)
                .and()
                .body(userCredentials)
                .post(AUTHORIZATION_USER_PATH);
    }
    @Step("Send PATCH request to update user data.")
    public static Response updatingUserDataResponse(String accessToken, String jsonUpdatingData) {
        return given()
                .contentType(JSON)
                .header(AUTHORIZATION, accessToken)
                .and()
                .body(jsonUpdatingData)
                .patch(UPDATING_USER_DATA_PATH);
    }
    @Step("Send PATCH request to update data unauthorized user.")
    public static Response updatingUserDataResponseWithoutToken(String jsonUpdatingData) {
        return given()
                .contentType(JSON)
                .and()
                .body(jsonUpdatingData)
                .patch(UPDATING_USER_DATA_PATH);
    }
}
