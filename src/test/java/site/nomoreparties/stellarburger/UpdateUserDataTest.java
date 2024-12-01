package site.nomoreparties.stellarburger;

import com.github.javafaker.Faker;
import com.google.gson.Gson;
import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.junit.Test;
import site.nomoreparties.stellarburger.api.client.UserClient;
import site.nomoreparties.stellarburger.user.auth.UserAuthData;
import site.nomoreparties.stellarburger.user.auth.UserCredentials;
import site.nomoreparties.stellarburger.user.update.UpdateUser;

import java.util.UUID;

import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UpdateUserDataTest extends SetUp {
    private final Faker faker = new Faker();
    static String name;
    static String email;
    static String password;

    @Test
    @Description("Проверка возможности изменения всех данных авторизованного пользователя:запрос должен вернуть 200.")
    public void checkEditingUserDataWithAuthorization() {
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

        Gson gson = new Gson();
        String newName = String.valueOf(UUID.randomUUID());
        String newEmail = faker.internet().emailAddress();
        String newPassword = faker.bothify("???????");
        UserCredentials updatingData = new UserCredentials(newName, newEmail, newPassword);
        String jsonUpdatingData = gson.toJson(updatingData);
        //запрос на обновление данных пользователя
        Response updatingUserDataResponse = UserClient.updatingUserDataResponse(accessToken, jsonUpdatingData);

        UpdateUser updatedUser = updatingUserDataResponse.as(UpdateUser.class);

        assertEquals("Неверный статус код при обновлении данных авторизованного пользователя", HTTP_OK,
                updatingUserDataResponse.statusCode());
        assertTrue("Неверное значение поля success", updatedUser.getSuccess());
        assertEquals("Неверное значение обновлённого имени пользователя", newName, updatedUser.getUser().getName());
        assertEquals("Неверное значение обновлённого емэйла пользователя", newEmail, updatedUser.getUser().getEmail());
    }

    @Test
    @Description("Проверка возможности изменения всех данных пользователя без авторизации:запрос должен вернуть 401.")
    public void checkEditingUserDataWithoutAuthorization() {
        name = String.valueOf(UUID.randomUUID());
        email = faker.internet().emailAddress();
        password = faker.bothify("???????");

        UserCredentials userCredentials = new UserCredentials();
        userCredentials.withName(name);
        userCredentials.withEmail(email);
        userCredentials.withPassword(password);

        Response createUserResponse = UserClient.createUserResponse(userCredentials);
        accessToken = createUserResponse.as(UserAuthData.class).getAccessToken();

        Gson gson = new Gson();
        name = String.valueOf(UUID.randomUUID());
        email = faker.internet().emailAddress();
        password = faker.bothify("???????");
        UserCredentials updatingData = new UserCredentials(name, email, password);
        String jsonUpdatingData = gson.toJson(updatingData);
        //запрос на обновление данных неавторизованного клиента
        Response updatingUserDataResponse = UserClient.updatingUserDataResponseWithoutToken(jsonUpdatingData);

        assertEquals("Неверный статус код при обновлении данных неавторизованного пользователя", HTTP_UNAUTHORIZED,
                updatingUserDataResponse.statusCode());
        FailedResponse failedResponse = updatingUserDataResponse.as(FailedResponse.class);
        assertFalse("Неверное значение поля success", failedResponse.isSuccess());
        assertEquals("Неверное значение поля message", "You should be authorised", failedResponse.getMessage());
    }

}
