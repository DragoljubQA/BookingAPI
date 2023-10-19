package Tests;

import PojoClasses.BookingDates;
import PojoClasses.CreateBooking;
import PojoClasses.Variables;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class ApiTests {
    String pingParam;
    String bookingParam;
    Variables variables;
    @BeforeClass
    public void setUp() {
        variables = new Variables();
        RestAssured.baseURI = "https://restful-booker.herokuapp.com/";
        pingParam = "ping";
        bookingParam = "booking";
    }

    @Test(priority = 10)
    public void healthCheck() {
        given()
                .log().all()
                .header("Content-Type", "application/json")
                .when()
                .get(pingParam)
                .then()
                .log().all()
                .statusCode(201);
    }

    @Test(priority = 20)
    public void getAllBookings() {
        String response = given()
                .log().all()
                .header("Content-Type", "application/json")
                .when()
                .get(bookingParam)
                .then()
                .log().all()
                .statusCode(200)
                .extract().response().asString();

        Assert.assertFalse(response.isEmpty());
    }

    @Test(priority = 30)
    public void createBooking() {
        CreateBooking payload = new CreateBooking();
        BookingDates bookingDates = new BookingDates();
        bookingDates.setCheckin("2023-10-10");
        bookingDates.setCheckout("2023-11-10");
        payload.setFirstname("Dragoljub");
        payload.setLastname("Boranijasevic");
        payload.setTotalprice(200);
        payload.setDepositpaid(true);
        payload.setBookingdates(bookingDates);
        payload.setAdditionalneeds("Dinner");

        String response = given()
                .log().all()
                .header("Content-Type", "application/json")
                .body(payload)
                .when()
                .post(bookingParam)
                .then()
                .statusCode(200)
                .log().all()
                .extract().response().asString();

        JsonPath jp = new JsonPath(response);
        variables.setBookingid(jp.get("bookingid"));
    }

    @Test(priority = 40)
    public void getCertainBooking() {
        given()
                .log().all()
                .header("Content-Type", "application/json")
                .when()
                .get(bookingParam+"/"+variables.getBookingid())
                .then()
                .statusCode(200)
                .log().all();
    }




}
