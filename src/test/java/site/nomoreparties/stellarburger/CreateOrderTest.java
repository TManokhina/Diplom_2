package site.nomoreparties.stellarburger;

import com.github.javafaker.Faker;
import io.qameta.allure.Description;
import io.restassured.response.Response;
import junit.framework.TestCase;
import org.junit.Test;
import site.nomoreparties.stellarburger.api.client.OrderClient;
import site.nomoreparties.stellarburger.api.client.UserClient;
import site.nomoreparties.stellarburger.ingredient.Ingredient;
import site.nomoreparties.stellarburger.ingredient.IngredientRequest;
import site.nomoreparties.stellarburger.ingredient.IngredientResponse;
import site.nomoreparties.stellarburger.order.create.CreateOrder;
import site.nomoreparties.stellarburger.user.auth.UserAuthData;
import site.nomoreparties.stellarburger.user.auth.UserCredentials;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.net.HttpURLConnection.*;
import static org.junit.Assert.*;

public class CreateOrderTest extends SetUp {
    private final Faker faker = new Faker();
    static String name;
    static String email;
    static String password;

    @Test
    @Description("Проверка возможности создания заказа авторизованным пользователем:запрос должен " + "вернуть 200. В случае успешного создания заказа, тело ответа должно содержать номер заказа.")
    public void createOrderWithAuthorization() {
        //получения полного списка всех возможных ингредиентов
        Response getIngredientsResponse = OrderClient.getIngredientsResponse();
        List<String> ids = new ArrayList<>();
        List<Ingredient> data = getIngredientsResponse.as(IngredientResponse.class).getData();
        for (Ingredient datum : data) {
            ids.add(datum.getId());
        }
        IngredientRequest ingredients = new IngredientRequest(ids);
        name = String.valueOf(UUID.randomUUID());
        email = faker.internet().emailAddress();
        password = faker.bothify("???????");

        UserCredentials userCredentials = new UserCredentials();
        userCredentials.withName(name);
        userCredentials.withEmail(email);
        userCredentials.withPassword(password);

        Response createUserResponse = UserClient.createUserResponse(userCredentials);
        accessToken = createUserResponse.as(UserAuthData.class).getAccessToken();

        Response createOrderResponse = OrderClient.createOrderResponse(accessToken, ingredients);

        assertEquals("Неверный статус код при создании заказа авторизованным пользователем", HTTP_OK, createOrderResponse.statusCode());
        CreateOrder createOrder = createUserResponse.as(CreateOrder.class);
        assertTrue("Неверное значение поля success", createOrder.isSuccess());
        assertNotNull("Не вернулся номер заказа", createOrder.getOrder());
        assertNotNull("Не вернулся номер заказа", createOrder.getOrder().getNumber());

    }

    @Test
    @Description("Проверка возможности создания заказа без авторизации:запрос должен вернуть 401.")
    public void createOrderWithoutAuthorization() {
        Response getIngredientsResponse = OrderClient.getIngredientsResponse();
        List<String> ids = new ArrayList<>();
        List<Ingredient> data = getIngredientsResponse.as(IngredientResponse.class).getData();
        for (Ingredient datum : data) {
            ids.add(datum.getId());
        }
        IngredientRequest ingredientsBody = new IngredientRequest(ids);

        Response createOrderResponse = OrderClient.createOrderWithoutAuthorization(ingredientsBody);
        assertEquals("Неверный статус код при создании заказа без авторизации", HTTP_UNAUTHORIZED, createOrderResponse.statusCode());
    }

    @Test
    @Description("Проверка возможности создания заказа без ингидиентов:запрос должен " + "вернуть 400.")
    public void createOrderWithoutIngredients() {
        List<String> ids = new ArrayList<>();
        IngredientRequest ingredients = new IngredientRequest(ids);
        name = String.valueOf(UUID.randomUUID());
        email = faker.internet().emailAddress();
        password = faker.bothify("???????");

        UserCredentials userCredentials = new UserCredentials();
        userCredentials.withName(name);
        userCredentials.withEmail(email);
        userCredentials.withPassword(password);

        Response createUserResponse = UserClient.createUserResponse(userCredentials);
        accessToken = createUserResponse.as(UserAuthData.class).getAccessToken();

        Response createOrderResponse = OrderClient.createOrderResponse(accessToken, ingredients);

        assertEquals("Неверный статус код при создании заказа без ингредиентов", HTTP_BAD_REQUEST, createOrderResponse.statusCode());
        FailedResponse failedResponse = createOrderResponse.as(FailedResponse.class);
        assertFalse("Неверное значение поля success", failedResponse.isSuccess());
        TestCase.assertEquals("Неверное значение поля message", "Ingredient ids must be provided", failedResponse.getMessage());
    }

    @Test
    @Description("Проверка возможности создания заказа c невалидным хешем ингидиентов:запрос должен " + "вернуть 500.")
    public void createOrderWithInvalidHash() {
        List<String> ids = new ArrayList<>();
        ids.add(faker.bothify("????????"));
        ids.add(faker.bothify("????????"));
        IngredientRequest ingredients = new IngredientRequest(ids);

        name = String.valueOf(UUID.randomUUID());
        email = faker.internet().emailAddress();
        password = faker.bothify("???????");

        UserCredentials userCredentials = new UserCredentials();
        userCredentials.withName(name);
        userCredentials.withEmail(email);
        userCredentials.withPassword(password);

        Response createUserResponse = UserClient.createUserResponse(userCredentials);
        accessToken = createUserResponse.as(UserAuthData.class).getAccessToken();

        Response createOrderResponse = OrderClient.createOrderResponse(accessToken, ingredients);
        assertEquals("Неверный статус код при создании заказа с невалидным хешем ингридиентов", HTTP_INTERNAL_ERROR, createOrderResponse.statusCode());

    }

}
