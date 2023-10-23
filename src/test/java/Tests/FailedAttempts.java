package Tests;

import PojoClasses.BookingDates;
import PojoClasses.CreateBooking;
import PojoClasses.CreateToken;
import PojoClasses.Variables;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;

import static io.restassured.RestAssured.given;

public class FailedAttempts {

    String pingParam;
    String bookingParam;
    String tokenParam;
    Variables variables;
    CreateBooking payload = new CreateBooking();
    CreateBooking updatePayload = new CreateBooking();
    BookingDates bookingDates = new BookingDates();
    BookingDates updateBookingDates = new BookingDates();
    CreateToken createToken = new CreateToken();
    @BeforeClass
    public void setUp() {
        variables = new Variables();
        RestAssured.baseURI = "https://restful-booker.herokuapp.com/";
        pingParam = "ping";
        bookingParam = "booking";
        tokenParam = "auth";
        createToken();
    }

    public void createToken() {
        createToken.setUsername("admin");
        createToken.setPassword("password123");
        String response = given()
                .log().all()
                .header("Content-Type", "application/json")
                .body(createToken)
                .when()
                .post(tokenParam)
                .then().log().all()
                .extract().response().asString();

        JsonPath jp = new JsonPath(response);
        variables.setToken(jp.get("token"));
    }
    @Test(priority = 110)
    public void allFieldsAreMandatoryInBody() {
        ArrayList list = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            list.set(0, "2023-10-10");
            list.set(1, "2023-11-10");
            list.set(2, "Dragoljub");
            list.set(3, "Boranijasevic");
            list.set(4, 200);
            list.set(5, true);
            list.set(6, "Dinner");
            list.set(i, null);
            bookingDates.setCheckin(String.valueOf(list.get(0)));
            bookingDates.setCheckout(String.valueOf(list.get(1)));
            payload.setFirstname(String.valueOf(list.get(2)));
            payload.setLastname(String.valueOf(list.get(3)));
            payload.setTotalprice(Integer.parseInt(String.valueOf(list.get(4))));
            payload.setDepositpaid(Boolean.parseBoolean(String.valueOf(list.get(5))));
            payload.setBookingdates(bookingDates);
            payload.setAdditionalneeds(String.valueOf(list.get(6)));

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
    }

    @Test(priority = 120)
    public void fieldsAreMandatoryInBody() {
        for (int i = 0; i < 7; i++) {
            BookingDates newBookingDates = new BookingDates();
            CreateBooking newPayload = new CreateBooking();
            newBookingDates.setCheckin("2023-10-10");
            newBookingDates.setCheckout("2023-11-10");
            newPayload.setFirstname("Dragoljub");
            newPayload.setLastname("Boranijasevic");
            newPayload.setTotalprice(200);
            newPayload.setDepositpaid(true);
            newPayload.setBookingdates(newBookingDates);
            newPayload.setAdditionalneeds("Dinner");

            if (i==0) {
                newBookingDates.setCheckin(null);
            } else if (i==1) {
                newBookingDates.setCheckout(null);
            } else if (i==2) {
                newPayload.setFirstname(null);
            } else if (i==3) {
                newPayload.setLastname(null);
            } else if (i==4) {
                newPayload.setTotalprice(null);
            } else if (i==5) {
                newPayload.setDepositpaid(null);
            } else {
                newPayload.setAdditionalneeds(null);
            }

            String response = given()
                    .log().all()
                    .header("Content-Type", "application/json")
                    .body(newPayload)
                    .when()
                    .post(bookingParam)
                    .then()
                    .statusCode(500)
                    .log().all()
                    .extract().response().asString();

            Assert.assertEquals(response, "Internal Server Error");
        }


    }
}
