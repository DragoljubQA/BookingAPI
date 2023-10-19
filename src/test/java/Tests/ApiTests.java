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

import static io.restassured.RestAssured.given;

public class ApiTests {
    String pingParam;
    String bookingParam;
    String tokenParam;
    Variables variables;
    CreateBooking payload = new CreateBooking();
    BookingDates bookingDates = new BookingDates();
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

        bookingDates.setCheckin("2023-12-12");
        bookingDates.setCheckout("2023-12-15");
        payload.setFirstname("John");
        payload.setLastname("Smith");
        payload.setTotalprice(310);
        payload.setDepositpaid(false);
        payload.setBookingdates(bookingDates);
        payload.setAdditionalneeds("Lunch");

        given()
                .log().all()
                .header("Content-Type", "application/json")
                .header("Cookie", "token="+variables.getToken())
                .body(payload)
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
        Assert.assertEquals(jp.get("firstname"), payload.getFirstname());
        Assert.assertEquals(jp.get("lastname"), payload.getLastname());
        Assert.assertEquals(jp.get("totalprice").toString(), String.valueOf(payload.getTotalprice()));
        Assert.assertEquals(jp.get("depositpaid"), payload.isDepositpaid());
        Assert.assertEquals(jp.get("bookingdates.checkin"), bookingDates.getCheckin());
        Assert.assertEquals(jp.get("bookingdates.checkout"), bookingDates.getCheckout());
        Assert.assertEquals(jp.get("additionalneeds"), payload.getAdditionalneeds());


    }






}
