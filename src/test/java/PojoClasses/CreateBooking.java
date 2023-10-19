package PojoClasses;

import com.github.javafaker.Faker;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

public class CreateBooking {
    private String firstname;
    private String lastname;
    private int totalprice;
    private boolean depositpaid;
    private Object bookingdates;
    private String additionalneeds;
    static Faker faker = new Faker();

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public int getTotalprice() {
        return totalprice;
    }

    public void setTotalprice(int totalprice) {
        this.totalprice = totalprice;
    }

    public boolean isDepositpaid() {
        return depositpaid;
    }

    public void setDepositpaid(boolean depositpaid) {
        this.depositpaid = depositpaid;
    }

    public Object getBookingdates() {
        return bookingdates;
    }

    public void setBookingdates(Object bookingdates) {
        this.bookingdates = bookingdates;
    }

    public String getAdditionalneeds() {
        return additionalneeds;
    }

    public void setAdditionalneeds(String additionalneeds) {
        this.additionalneeds = additionalneeds;
    }

    public static Object setRandomPayload() {
        BookingDates bookingDates = new BookingDates();
        CreateBooking payload = new CreateBooking();
        bookingDates.setCheckin(String.valueOf(faker.date().future(1, TimeUnit.DAYS)));
        bookingDates.setCheckout(String.valueOf(faker.date().future(1, TimeUnit.DAYS)));
        payload.setFirstname(faker.name().firstName());
        payload.setLastname(faker.name().lastName());
        payload.setTotalprice(faker.number().numberBetween(100, 1000));
        payload.setDepositpaid(faker.bool().bool());
        payload.setBookingdates(bookingDates);
        payload.setAdditionalneeds(faker.food().dish());
        return payload;
    }

    @Test
    public void test() {
        System.out.println(faker.date().future(1, TimeUnit.DAYS));
        System.out.println(faker.date().future(2, TimeUnit.DAYS));
    }
}
