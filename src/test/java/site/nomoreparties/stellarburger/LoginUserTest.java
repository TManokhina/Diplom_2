package site.nomoreparties.stellarburger;

import com.github.javafaker.Faker;
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
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static junit.framework.TestCase.assertEquals;
import static org.apache.http.HttpHeaders.AUTHORIZATION;
import static site.nomoreparties.stellarburger.CreateUserTest.DELETE_USER_PATH;
import static site.nomoreparties.stellarburger.CreateUserTest.REGISTRATION_USER_PATH;

public class LoginUserTest {
    private final Faker faker = new Faker();
    public static final String AUTHORIZATION_USER_PATH = "/api/auth/login";
    public static final String BASE_URI_PATH = "https://stellarburgers.nomoreparties.site";
    public static final String REGISTRATION_USER_PATH = "/api/auth/register";

    static String name;
    static String email;
    static String password;
    static String accessToken;

    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URI_PATH;
    }

    @Test
    @Description("Проверка возможности залогиниться под существующим пользователем - запрос должен вернуть 200.")
    public void checkLoginUser() {
        name = String.valueOf(UUID.randomUUID());
        email = faker.internet().emailAddress();
        password = faker.bothify("???????");

        User user = new User();
        user.withName(name);
        user.withEmail(email);
        user.withPassword(password);
        //создание пользователя
        Response createUserResponse = given().contentType(JSON).and().body(user).post(REGISTRATION_USER_PATH);
        accessToken = createUserResponse.as(UserTokens.class).getAccessToken();
        //регистрация
        Response authUserResponse = given()
                .contentType(JSON)
                .and()
                .body(user)
                .post(AUTHORIZATION_USER_PATH);

        assertEquals("Неверный статус код при логине под существующим пользователем", HTTP_OK,
                authUserResponse.statusCode());

    }

    @Test
    @Description("Проверка возможности залогиниться под существующим пользователем, используя некорректный логин и " +
            "пароль" +
            " - запрос должен вернуть 401.")
    public void checkLoginUserWithIncorrectCreds() {
        name = String.valueOf(UUID.randomUUID());
        email = faker.internet().emailAddress();
        password = faker.bothify("???????");

        User user = new User();
        user.withName(name);
        user.withEmail(email);
        user.withPassword(password);
        //создание пользователя
        Response createUserResponse = given().contentType(JSON).and().body(user).post(REGISTRATION_USER_PATH);
        accessToken = createUserResponse.as(UserTokens.class).getAccessToken();
        //регистрация
        String incorrectName = String.valueOf(UUID.randomUUID());
        String incorrectPassword = faker.bothify("???????");
        User incorrectUser = new User();
        incorrectUser.withName(incorrectName);
        incorrectUser.withEmail(email);
        incorrectUser.withPassword(incorrectPassword);

        Response authUserResponse = given()
                .contentType(JSON)
                .and()
                .body(incorrectUser)
                .post(AUTHORIZATION_USER_PATH);

        assertEquals("Неверный статус код при логине с несуществующим логином и паролем", HTTP_UNAUTHORIZED,
                authUserResponse.statusCode());

    }

    @After
    public void tearDown() {
        if (accessToken !=null)
            with().contentType(JSON).header(AUTHORIZATION, accessToken).delete(DELETE_USER_PATH);
    }

}
