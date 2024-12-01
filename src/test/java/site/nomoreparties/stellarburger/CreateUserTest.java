package site.nomoreparties.stellarburger;

import com.github.javafaker.Faker;
import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Test;
import site.nomoreparties.stellarburger.api.client.UserClient;
import site.nomoreparties.stellarburger.user.auth.UserAuthData;
import site.nomoreparties.stellarburger.user.auth.UserCredentials;

import java.util.UUID;

import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static java.net.HttpURLConnection.HTTP_OK;
import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;

public class CreateUserTest extends SetUp {
    private final Faker faker = new Faker();
    static String name;
    static String email;
    static String password;

    @Test
    @Description("Проверка возможности создать пользователя при заполнении всех полей: имя, имэйл, пароль - запрос " +
            "должен " +
            "вернуть 200.")
    public void checkCreationUser() {
        name = String.valueOf(UUID.randomUUID());
        email = faker.internet().emailAddress();
        password = faker.bothify("???????");

        UserCredentials userCredentials = new UserCredentials();
        userCredentials.withName(name);
        userCredentials.withEmail(email);
        userCredentials.withPassword(password);
        Response createUserResponse = UserClient.createUserResponse(userCredentials);
        accessToken = createUserResponse.as(UserAuthData.class).getAccessToken();

        assertEquals("Неверный статус код при создании пользователя", HTTP_OK,
                createUserResponse.statusCode());
        UserAuthData updatedUser = createUserResponse.as(UserAuthData.class);
        Assert.assertTrue("Неверное значение поля success", updatedUser.isSuccess());
        assertEquals("Неверное значение имя пользователя", name, updatedUser.getUser().getName());
        assertEquals("Неверный email пользователя", email, updatedUser.getUser().getEmail());
    }

    @Test
    @Description("Проверка отсутствия возможности создать пользователя с логином, который уже есть - запрос должен вернуть 409.")
    public void checkCreationIdenticalUsers() {
        name = String.valueOf(UUID.randomUUID());
        email = faker.internet().emailAddress();
        password = faker.bothify("???????");

        UserCredentials userCredentials = new UserCredentials();
        userCredentials.withName(name);
        userCredentials.withEmail(email);
        userCredentials.withPassword(password);

        Response createUniqueUserResponse = UserClient.createUserResponse(userCredentials);
        accessToken = createUniqueUserResponse.as(UserAuthData.class).getAccessToken();
        //повторный запрос на создание пользователя, который существует
        Response createSameUserResponse = UserClient.createUserResponse(userCredentials);

        assertEquals("Неверный статус код при создании польователя, который уже есть", HTTP_FORBIDDEN,
                createSameUserResponse.statusCode());
        FailedResponse failedResponse = createSameUserResponse.as(FailedResponse.class);
        assertFalse("Неверное значение поля success", failedResponse.isSuccess());
        assertEquals("Неверное значение поля message", "User already exists", failedResponse.getMessage());
    }

    @Test
    @Description("Проверка отсутствия возможности создать пользователя без указания имени - запрос должен вернуть " +
            "401.")
    public void checkCreationUserWithoutName() {
        email = faker.internet().emailAddress();
        password = faker.bothify("???????");

        UserCredentials userCredentials = new UserCredentials();
        userCredentials.withEmail(email);
        userCredentials.withPassword(password);

        Response createUserResponse = UserClient.createUserResponse(userCredentials);
        accessToken = createUserResponse.as(UserAuthData.class).getAccessToken();

        assertEquals("Неверный статус код при создании пользователя без имени", HTTP_FORBIDDEN,
                createUserResponse.statusCode());
        FailedResponse failedResponse = createUserResponse.as(FailedResponse.class);
        assertFalse("Неверное значение поля success", failedResponse.isSuccess());
        assertEquals("Неверное значение поля message", "Email, password and name are required fields", failedResponse.getMessage());
    }

    @Test
    @Description("Проверка отсутствия возможности создать пользователя без указания имэйла - запрос должен вернуть " +
            "401.")
    public void checkCreationCourierWithoutEmail() {
        name = String.valueOf(UUID.randomUUID());
        password = faker.bothify("???????");

        UserCredentials userCredentials = new UserCredentials();
        userCredentials.withName(name);
        userCredentials.withPassword(password);

        Response createUserResponse = UserClient.createUserResponse(userCredentials);
        accessToken = createUserResponse.as(UserAuthData.class).getAccessToken();

        assertEquals("Неверный статус код при создании пользователя без имэйла", HTTP_FORBIDDEN,
                createUserResponse.statusCode());
        FailedResponse failedResponse = createUserResponse.as(FailedResponse.class);
        assertFalse("Неверное значение поля success", failedResponse.isSuccess());
        assertEquals("Неверное значение поля message", "Email, password and name are required fields", failedResponse.getMessage());
    }

    @Test
    @Description("Проверка отсутствия возможности создать пользователя без указания пароля - запрос должен вернуть " +
            "401.")
    public void checkCreationCourierWithoutPassword() {
        name = String.valueOf(UUID.randomUUID());
        email = faker.internet().emailAddress();

        UserCredentials userCredentials = new UserCredentials();
        userCredentials.withName(name);
        userCredentials.withEmail(email);

        Response createUserResponse = UserClient.createUserResponse(userCredentials);
        accessToken = createUserResponse.as(UserAuthData.class).getAccessToken();

        assertEquals("Неверный статус код при создании пользователя без пароля", HTTP_FORBIDDEN,
                createUserResponse.statusCode());
        FailedResponse failedResponse = createUserResponse.as(FailedResponse.class);
        assertFalse("Неверное значение поля success", failedResponse.isSuccess());
        assertEquals("Неверное значение поля message", "Email, password and name are required fields", failedResponse.getMessage());

    }
}