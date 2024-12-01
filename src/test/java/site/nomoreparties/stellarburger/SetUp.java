package site.nomoreparties.stellarburger;

import io.restassured.RestAssured;
import org.junit.After;
import org.junit.Before;

import static io.restassured.RestAssured.with;
import static io.restassured.http.ContentType.JSON;
import static org.apache.http.HttpHeaders.AUTHORIZATION;
import static site.nomoreparties.stellarburger.api.client.Client.BASE_URI_PATH;
import static site.nomoreparties.stellarburger.api.client.UserClient.UPDATING_USER_DATA_PATH;

public class SetUp {

    String accessToken;

    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URI_PATH;
    }

    @After
    public void tearDown() {
        if (accessToken != null)
            with().contentType(JSON).header(AUTHORIZATION, accessToken).delete(UPDATING_USER_DATA_PATH);
    }
}
