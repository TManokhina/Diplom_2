package site.nomoreparties.stellarburger;

import com.github.javafaker.Faker;
import com.google.gson.Gson;
import io.qameta.allure.Description;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.with;
import static io.restassured.http.ContentType.JSON;
import static java.net.HttpURLConnection.HTTP_OK;
import static junit.framework.TestCase.assertEquals;
import static org.apache.http.HttpHeaders.AUTHORIZATION;

public class UpdateUserDataTest {
    private final Faker faker = new Faker();
    public static final String BASE_URI_PATH = "https://stellarburgers.nomoreparties.site";
    public static final String REGISTRATION_USER_PATH = "/api/auth/register";
    public static final String DELETE_USER_PATH = "/api/auth/user";
    public static final String UPDATING_USER_DATA_PATH = "/api/auth/user";

    static String name;
    static String email;
    static String password;
    static String accessToken;

    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URI_PATH;
    }

    @Test
    @Description("Проверка возможности изменения всех данных авторизованного пользователя:запрос должен вернуть 200.")
    public void checkEditingUserDataWithAuthorization() {
        name = String.valueOf(UUID.randomUUID());
        email = faker.internet().emailAddress();
        password = faker.bothify("???????");

        User user = new User();
        user.withName(name);
        user.withEmail(email);
        user.withPassword(password);

        Response createUserResponse = given()
                .contentType(JSON)
                .and()
                .body(user)
                .post(REGISTRATION_USER_PATH);
        accessToken = createUserResponse.as(UserTokens.class).getAccessToken();

        Gson gson = new Gson();
        name = String.valueOf(UUID.randomUUID());
        email = faker.internet().emailAddress();
        password = faker.bothify("???????");
        UpdatingUserData updatingData = new UpdatingUserData(name, email, password);
        String jsonUpdatingData = gson.toJson(updatingData);
        Response updatingUserDataResponse = given()
                .contentType(JSON)
                .and()
                .body(jsonUpdatingData)
                .patch(UPDATING_USER_DATA_PATH);

        assertEquals("Неверный статус код при обновлении данных авторизованного пользователя", HTTP_OK,
                createUserResponse.statusCode());


    }


    @After
    public void tearDown() {
        if (accessToken !=null)
            with().contentType(JSON).header(AUTHORIZATION, accessToken).delete(DELETE_USER_PATH);
    }
}
