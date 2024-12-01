package site.nomoreparties.stellarburger.api.client;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import site.nomoreparties.stellarburger.ingredient.IngredientRequest;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.apache.http.HttpHeaders.AUTHORIZATION;

public class OrderClient implements Client {
    public static final String INGREDIENTS_PATH = "/api/ingredients";
    public static final String ORDERS_PATH = "/api/orders";

    @Step("Send GET request to get info about all possible ingredients.")
    public static Response getIngredientsResponse() {
        return given().get(INGREDIENTS_PATH);
    }

    @Step("Send POST request to create order with authorization token.")
    public static Response createOrderResponse(String accessToken, IngredientRequest ingredients) {
        return given().contentType(JSON).header(AUTHORIZATION, accessToken).body(ingredients).post(ORDERS_PATH);
    }

    @Step("Send POST request to create order without authorization.")
    public static Response createOrderWithoutAuthorization(IngredientRequest ingredientsBody) {
        return given().contentType(JSON).body(ingredientsBody).post(ORDERS_PATH);
    }

    @Step("Send GET request to get orders specify user with authorization.")
    public static Response receivedOrderResponseWithAuth(String accessToken) {
        return given().contentType(JSON).header(AUTHORIZATION, accessToken).get(ORDERS_PATH);
    }
    @Step("Send GET request to get orders specify user without authorization.")
    public static Response receivedOrderResponseWithoutAuth () {
        return given().contentType(JSON).get(ORDERS_PATH);
    }
}
