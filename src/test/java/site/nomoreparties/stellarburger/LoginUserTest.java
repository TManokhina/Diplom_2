package site.nomoreparties.stellarburger;

import com.github.javafaker.Faker;
import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.junit.Test;
import site.nomoreparties.stellarburger.api.client.UserClient;
import site.nomoreparties.stellarburger.user.auth.UserAuthData;
import site.nomoreparties.stellarburger.user.auth.UserCredentials;

import java.util.UUID;

import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LoginUserTest extends SetUp {
    private final Faker faker = new Faker();
    static String name;
    static String email;
    static String password;

    @Test
    @Description("Проверка возможности залогиниться под существующим пользователем - запрос должен вернуть 200.")
    public void checkLoginUser() {
        name = String.valueOf(UUID.randomUUID());
        email = faker.internet().emailAddress();
        password = faker.bothify("???????");

        UserCredentials userCredentials = new UserCredentials();
        userCredentials.withName(name);
        userCredentials.withEmail(email);
        userCredentials.withPassword(password);
        //создание пользователя
        Response createUserResponse = UserClient.createUserResponse(userCredentials);
        accessToken = createUserResponse.as(UserAuthData.class).getAccessToken();
        //регистрация
        Response authUserResponse = UserClient.authUserResponse(userCredentials);
        UserAuthData authUser = authUserResponse.as(UserAuthData.class);

        assertEquals("Неверный статус код при логине под существующим пользователем", HTTP_OK,
                authUserResponse.statusCode());
        assertTrue("Неверное значение поля success", authUser.isSuccess());
        assertEquals("Некорректное имя пользователя", name, authUser.getUser().getName());
        assertEquals("Некорректный email пользователя", email, authUser.getUser().getEmail());
    }

    @Test
    @Description("Проверка возможности залогиниться под существующим пользователем, используя некорректный логин и " +
            "пароль" +
            " - запрос должен вернуть 401.")
    public void checkLoginUserWithIncorrectCreds() {
        name = String.valueOf(UUID.randomUUID());
        email = faker.internet().emailAddress();
        password = faker.bothify("???????");

        UserCredentials userCredentials = new UserCredentials();
        userCredentials.withName(name);
        userCredentials.withEmail(email);
        userCredentials.withPassword(password);
        //создание пользователя
        Response createUserResponse = UserClient.createUserResponse(userCredentials);
        accessToken = createUserResponse.as(UserAuthData.class).getAccessToken();
        //регистрация
        String incorrectName = String.valueOf(UUID.randomUUID());
        String incorrectPassword = faker.bothify("???????");
        UserCredentials incorrectUserCredentials = new UserCredentials();
        incorrectUserCredentials.withName(incorrectName);
        incorrectUserCredentials.withEmail(email);
        incorrectUserCredentials.withPassword(incorrectPassword);

        Response authUserResponse = UserClient.authUserResponse(incorrectUserCredentials);

        assertEquals("Неверный статус код при логине с несуществующим логином и паролем", HTTP_UNAUTHORIZED,
                authUserResponse.statusCode());
        FailedResponse failedResponse = authUserResponse.as(FailedResponse.class);
        assertFalse("Неверное значение поля success", failedResponse.isSuccess());
        assertEquals("Неверное значение поля message", "email or password are incorrect", failedResponse.getMessage());
    }
}
