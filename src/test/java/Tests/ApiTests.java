package Tests;

import PojoClasses.BookingDates;
import PojoClasses.CreateBooking;
import PojoClasses.CreateToken;
import PojoClasses.Variables;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static io.restassured.RestAssured.given;

public class ApiTests {
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

    @AfterMethod
    public void log(ITestResult result) {
        System.out.println("TEST: " + result.getName() + " FINISHED");
        switch (result.getStatus()) {
            case 1:
                System.out.println("RESULT: SUCCESS");
                break;
            case 2:
                System.out.println("RESULT: FAILED");
                break;
            case 3:
                System.out.println("RESULT: SKIPPED");
                break;
            default:
                System.out.println("RESULT: UNKNOWN");
        }
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
        String response = given()
                .log().all()
                .header("Content-Type", "application/json")
                .body(CreateBooking.setRandomPayload())
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

    @Test(priority = 50)
    public void createCustomBooking() {
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

    @Test(priority = 60)
    public void bookingHasAllInfo() {
        String response = given()
                .log().all()
                .header("Content-Type", "application/json")
                .when()
                .get(bookingParam+"/"+variables.getBookingid())
                .then()
                .statusCode(200)
                .log().all()
                .extract().response().asString();

        JsonPath jp = new JsonPath(response);
        Assert.assertEquals(jp.get("firstname"), payload.getFirstname());
        Assert.assertEquals(jp.get("lastname"), payload.getLastname());
        Assert.assertEquals(jp.get("totalprice").toString(), String.valueOf(payload.getTotalprice()));
        Assert.assertEquals(jp.get("depositpaid"), payload.isDepositpaid());
        Assert.assertEquals(jp.get("bookingdates.checkin"), bookingDates.getCheckin());
        Assert.assertEquals(jp.get("bookingdates.checkout"), bookingDates.getCheckout());
        Assert.assertEquals(jp.get("additionalneeds"), payload.getAdditionalneeds());
    }

    @Test(priority = 70)
    public void bookingCanBeUpdated() {

        //Create booking

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

        //Read booking

        response = given()
                .log().all()
                .header("Content-Type", "application/json")
                .when()
                .get(bookingParam+"/"+variables.getBookingid())
                .then()
                .statusCode(200)
                .log().all()
                .extract().response().asString();

        jp = new JsonPath(response);
        Assert.assertEquals(jp.get("firstname"), payload.getFirstname());
        Assert.assertEquals(jp.get("lastname"), payload.getLastname());
        Assert.assertEquals(jp.get("totalprice").toString(), String.valueOf(payload.getTotalprice()));
        Assert.assertEquals(jp.get("depositpaid"), payload.isDepositpaid());
        Assert.assertEquals(jp.get("bookingdates.checkin"), bookingDates.getCheckin());
        Assert.assertEquals(jp.get("bookingdates.checkout"), bookingDates.getCheckout());
        Assert.assertEquals(jp.get("additionalneeds"), payload.getAdditionalneeds());

        //Create update for the same booking

        updateBookingDates.setCheckin("2023-12-12");
        updateBookingDates.setCheckout("2023-12-15");
        updatePayload.setFirstname("John");
        updatePayload.setLastname("Smith");
        updatePayload.setTotalprice(310);
        updatePayload.setDepositpaid(false);
        updatePayload.setBookingdates(updateBookingDates);
        updatePayload.setAdditionalneeds("Lunch");

        given()
                .log().all()
                .header("Content-Type", "application/json")
                .header("Cookie", "token="+variables.getToken())
                .body(updatePayload)
                .when()
                .put(bookingParam+"/"+variables.getBookingid())
                .then()
                .statusCode(200)
                .log().all();

        //Read booking again to verify changes

        response = given()
                .log().all()
                .header("Content-Type", "application/json")
                .when()
                .get(bookingParam+"/"+variables.getBookingid())
                .then()
                .statusCode(200)
                .log().all()
                .extract().response().asString();

        jp = new JsonPath(response);
        Assert.assertEquals(jp.get("firstname"), updatePayload.getFirstname());
        Assert.assertEquals(jp.get("lastname"), updatePayload.getLastname());
        Assert.assertEquals(jp.get("totalprice").toString(), String.valueOf(updatePayload.getTotalprice()));
        Assert.assertEquals(jp.get("depositpaid"), updatePayload.isDepositpaid());
        Assert.assertEquals(jp.get("bookingdates.checkin"), updateBookingDates.getCheckin());
        Assert.assertEquals(jp.get("bookingdates.checkout"), updateBookingDates.getCheckout());
        Assert.assertEquals(jp.get("additionalneeds"), updatePayload.getAdditionalneeds());
    }

    @Test(priority = 80)
    public void bookingCanBePartiallyUpdated() {

        //Create booking

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

        //Read booking

        response = given()
                .log().all()
                .header("Content-Type", "application/json")
                .when()
                .get(bookingParam+"/"+variables.getBookingid())
                .then()
                .statusCode(200)
                .log().all()
                .extract().response().asString();

        jp = new JsonPath(response);
        Assert.assertEquals(jp.get("firstname"), payload.getFirstname());
        Assert.assertEquals(jp.get("lastname"), payload.getLastname());
        Assert.assertEquals(jp.get("totalprice").toString(), String.valueOf(payload.getTotalprice()));
        Assert.assertEquals(jp.get("depositpaid"), payload.isDepositpaid());
        Assert.assertEquals(jp.get("bookingdates.checkin"), bookingDates.getCheckin());
        Assert.assertEquals(jp.get("bookingdates.checkout"), bookingDates.getCheckout());
        Assert.assertEquals(jp.get("additionalneeds"), payload.getAdditionalneeds());

        //Create update for the same booking

        updatePayload.setTotalprice(310);
        updatePayload.setDepositpaid(false);
        updatePayload.setAdditionalneeds("Lunch");

        given()
                .log().all()
                .header("Content-Type", "application/json")
                .header("Cookie", "token="+variables.getToken())
                .body(updatePayload)
                .when()
                .patch(bookingParam+"/"+variables.getBookingid())
                .then()
                .statusCode(200)
                .log().all();

        //Read booking again to verify changes

        response = given()
                .log().all()
                .header("Content-Type", "application/json")
                .when()
                .get(bookingParam+"/"+variables.getBookingid())
                .then()
                .statusCode(200)
                .log().all()
                .extract().response().asString();

        jp = new JsonPath(response);
        Assert.assertEquals(jp.get("totalprice").toString(), String.valueOf(updatePayload.getTotalprice()));
        Assert.assertEquals(jp.get("depositpaid"), updatePayload.isDepositpaid());
        Assert.assertEquals(jp.get("additionalneeds"), updatePayload.getAdditionalneeds());

    }

    @Test(priority = 90)
    public void bookingCanBeRemoved() {

        //Create booking

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

        //Read booking

        response = given()
                .log().all()
                .header("Content-Type", "application/json")
                .when()
                .get(bookingParam+"/"+variables.getBookingid())
                .then()
                .statusCode(200)
                .log().all()
                .extract().response().asString();

        jp = new JsonPath(response);
        Assert.assertEquals(jp.get("firstname"), payload.getFirstname());
        Assert.assertEquals(jp.get("lastname"), payload.getLastname());
        Assert.assertEquals(jp.get("totalprice").toString(), String.valueOf(payload.getTotalprice()));
        Assert.assertEquals(jp.get("depositpaid"), payload.isDepositpaid());
        Assert.assertEquals(jp.get("bookingdates.checkin"), bookingDates.getCheckin());
        Assert.assertEquals(jp.get("bookingdates.checkout"), bookingDates.getCheckout());
        Assert.assertEquals(jp.get("additionalneeds"), payload.getAdditionalneeds());

        // Remove booking

        given()
                .log().all()
                .header("Content-Type", "application/json")
                .header("Cookie", "token=" + variables.getToken())
                .when()
                .delete(bookingParam+"/"+variables.getBookingid())
                .then()
                .statusCode(201)
                .log().all()
                .extract().response().asString();

        // Read booking after removing

         response = given()
                .log().all()
                .header("Content-Type", "application/json")
                .when()
                .get(bookingParam+"/"+variables.getBookingid())
                .then()
                .statusCode(404)
                .log().all()
                .extract().response().asString();

         Assert.assertEquals(response, "Not Found");
    }

    @Test(priority = 100)
    public void invalidPathParameterCantBeFound() {
        String response = given()
                .log().all()
                .header("Content-Type", "application/json")
                .when()
                .get("invalid")
                .then()
                .log().all()
                .statusCode(404)
                .extract().response().asString();

        Assert.assertEquals(response, "Not Found");
    }

    @Test(priority = 110)
    public void verifyMandatoryFieldsInBody() throws IOException {
        for (int i = 1; i <= 6; i++) {
            String response = given()
                    .log().all()
                    .header("Content-Type", "application/json")
                    .body(new String(Files.readAllBytes(Paths.get("JSONfiles\\Case"+i+".json"))))
                    .when()
                    .post(bookingParam)
                    .then()
                    .statusCode(500)
                    .log().all()
                    .extract().response().asString();

            Assert.assertEquals(response, "Internal Server Error");
        }
    }

    @Test(priority = 130)
    public void verifyAdditionalNeedsIsNotMandatory() throws IOException {
        String response = given()
                .log().all()
                .header("Content-Type", "application/json")
                .body(new String(Files.readAllBytes(Paths.get("JSONfiles\\Case7.json"))))
                .when()
                .post(bookingParam)
                .then()
                .statusCode(200)
                .log().all()
                .extract().response().asString();

        JsonPath jp = new JsonPath(response);
        Assert.assertEquals((String) jp.get("additionalneeds"), null);
    }




}
