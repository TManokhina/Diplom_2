package site.nomoreparties.stellarburger;

import com.github.javafaker.Faker;
import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.junit.Test;
import site.nomoreparties.stellarburger.api.client.OrderClient;
import site.nomoreparties.stellarburger.api.client.UserClient;
import site.nomoreparties.stellarburger.ingredient.Ingredient;
import site.nomoreparties.stellarburger.ingredient.IngredientRequest;
import site.nomoreparties.stellarburger.ingredient.IngredientResponse;
import site.nomoreparties.stellarburger.order.get.Orders;
import site.nomoreparties.stellarburger.user.auth.UserAuthData;
import site.nomoreparties.stellarburger.user.auth.UserCredentials;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static junit.framework.TestCase.assertEquals;
import static org.assertj.core.api.Assertions.assertThatList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UserOrderTest extends SetUp {
    private final Faker faker = new Faker();
    static String name;
    static String email;
    static String password;

    @Test
    @Description("Проверка возможности получения заказов авторизованного пользователя:запрос должен " + "вернуть 200.")
    public void checkGettingOrderAuthorizedUser() {
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
        OrderClient.createOrderResponse(accessToken, ingredients);

        Response receivedOrderResponse = OrderClient.receivedOrderResponseWithAuth(accessToken);
        Orders receivedOrders = receivedOrderResponse.as(Orders.class);

        assertEquals("Неверный статус код при получении заказов авторизованного пользователя", HTTP_OK, receivedOrderResponse.statusCode());
        assertTrue("Неверное значение поля success", receivedOrders.isSuccess());
        assertThatList(receivedOrders.getOrders()).withFailMessage("Неверный размер массива заказов пользователя").hasSize(1);
        assertThatList(receivedOrders.getOrders().get(0).getIngredients()).hasSameSizeAs(ids).containsAll(ids);
    }

    @Test
    @Description("Проверка возможности получения заказов неавторизованного пользователя:запрос должен " + "вернуть 401.")
    public void checkGettingOrderUnauthorizedUser() {
        Response receivedOrderResponse = OrderClient.receivedOrderResponseWithoutAuth();
        FailedResponse failedResponse = receivedOrderResponse.as(FailedResponse.class);

        assertEquals("Неверный статус код при попытке получения заказов неавторизованного пользователя", HTTP_UNAUTHORIZED, receivedOrderResponse.statusCode());
        assertFalse("Неверное значение поля success", failedResponse.isSuccess());
        assertEquals("Неверное значение поля message", "You should be authorised", failedResponse.getMessage());

    }
}
