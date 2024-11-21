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
import static java.net.HttpURLConnection.*;
import static junit.framework.TestCase.assertEquals;
import static org.apache.http.HttpHeaders.AUTHORIZATION;

public class CreateUserTest {

    private final Faker faker = new Faker();
    public static final String BASE_URI_PATH = "https://stellarburgers.nomoreparties.site";
    public static final String REGISTRATION_USER_PATH = "/api/auth/register";
    public static final String DELETE_USER_PATH = "/api/auth/user";

    static String name;
    static String email;
    static String password;
    static String accessToken;

    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URI_PATH;
    }

    @Test
    @Description("Проверка возможности создать пользователя при заполнении всех полей: имя, имэйл, пароль - запрос " +
            "должен " +
            "вернуть 200.")
    public void checkCreationUser() {
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
        assertEquals("Неверный статус код при создании пользователя", HTTP_OK,
                createUserResponse.statusCode());

    }

    @Test
    @Description("Проверка отсутствия возможности создать пользователя с логином, который уже есть - запрос должен вернуть 409.")
    public void checkCreationIdenticalUsers() {
        name = String.valueOf(UUID.randomUUID());
        email = faker.internet().emailAddress();
        password = faker.bothify("???????");

        User user = new User();
        user.withName(name);
        user.withEmail(email);
        user.withPassword(password);

        Response createUniqueUserResponse = given()
                .contentType(JSON)
                .and()
                .body(user)
                .post(REGISTRATION_USER_PATH);
        accessToken = createUniqueUserResponse.as(UserTokens.class).getAccessToken();
        //повторный запрос на создание пользователя, который существует
        Response createSameUserResponse = given()
                .contentType(JSON)
                .and()
                .body(user)
                .post(REGISTRATION_USER_PATH);
        assertEquals("Неверный статус код при создании польователя, который уже есть", HTTP_FORBIDDEN,
                createSameUserResponse.statusCode());
    }

    @Test
    @Description("Проверка отсутствия возможности создать пользователя без указания имени - запрос должен вернуть " +
            "401.")
    public void checkCreationUserWithoutName() {
        email = faker.internet().emailAddress();
        password = faker.bothify("???????");

        User user = new User();
        user.withEmail(email);
        user.withPassword(password);

        Response createUserResponse = given()
                .contentType(JSON)
                .and()
                .body(user)
                .post(REGISTRATION_USER_PATH);
        accessToken = createUserResponse.as(UserTokens.class).getAccessToken();
        assertEquals("Неверный статус код при создании пользователя без имени", HTTP_FORBIDDEN,
                createUserResponse.statusCode());
    }

    @Test
    @Description("Проверка отсутствия возможности создать пользователя без указания имэйла - запрос должен вернуть " +
            "401.")
    public void checkCreationCourierWithoutEmail() {
        name = String.valueOf(UUID.randomUUID());
        password = faker.bothify("???????");

        User user = new User();
        user.withName(name);
        user.withPassword(password);

        Response createUserResponse = given()
                .contentType(JSON)
                .and()
                .body(user)
                .post(REGISTRATION_USER_PATH);
        accessToken = createUserResponse.as(UserTokens.class).getAccessToken();
        assertEquals("Неверный статус код при создании пользователя без имэйла", HTTP_FORBIDDEN,
                createUserResponse.statusCode());
    }

    @Test
    @Description("Проверка отсутствия возможности создать пользователя без указания пароля - запрос должен вернуть " +
            "401.")
    public void checkCreationCourierWithoutPassword() {
        name = String.valueOf(UUID.randomUUID());
        email = faker.internet().emailAddress();

        User user = new User();
        user.withName(name);
        user.withEmail(email);

        Response createUserResponse = given()
                .contentType(JSON)
                .and()
                .body(user)
                .post(REGISTRATION_USER_PATH);
        accessToken = createUserResponse.as(UserTokens.class).getAccessToken();
        assertEquals("Неверный статус код при создании пользователя без пароля", HTTP_FORBIDDEN,
                createUserResponse.statusCode());

    }

    @After
    public void tearDown() {
        if (accessToken !=null)
        with().contentType(JSON).header(AUTHORIZATION, accessToken).delete(DELETE_USER_PATH);
    }

}