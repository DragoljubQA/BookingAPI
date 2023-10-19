package Tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class ApiTests {

    @Test
    public void healthCheck() {
        RestAssured.baseURI = "https://restful-booker.herokuapp.com/";
        String pingParam = "ping";
        given()
                .log().all()
                .header("Content-Type", "application/json")
                .when()
                .get(pingParam)
                .then()
                .log().all()
                .statusCode(201);
    }
    


}
