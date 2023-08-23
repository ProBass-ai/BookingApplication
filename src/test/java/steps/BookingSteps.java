package steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.assertEquals;


public class BookingSteps {
    private RequestSpecification request;
    private Response response;
    private String requestBody;
    private String firstname;
    private String lastname;
    private int totalprice;
    private boolean depositpaid;
    private LocalDate checkin;
    private LocalDate checkout;
    private String additionalneeds;
    private String token;
    private Integer bookingId;

    @Given("the base uri is {string}")
    public void theBaseUrlIs(String baseUri){
        RestAssured.baseURI = baseUri;
        request = RestAssured.given();
    }


    private String getRequestBody(String firstname, String lastname, int totalprice,
                                       boolean depositpaid, String checkin, String checkout,
                                       String additionalneeds){
        return "<booking>\n" +
                     "    <firstname>" + firstname + "</firstname>\n" +
                     "    <lastname>" + lastname + "</lastname>\n" +
                     "    <totalprice>" + totalprice + "</totalprice>\n" +
                     "    <depositpaid>" + depositpaid + "</depositpaid>\n" +
                     "    <bookingdates>\n" +
                     "      <checkin>" + checkin + "</checkin>\n" +
                     "      <checkout>" + checkout + "</checkout>\n" +
                     "    </bookingdates>\n" +
                     "    <additionalneeds>" + additionalneeds + "</additionalneeds>\n" +
                     "  </booking>";
    }

    private String getAuthJsonBody(String username, String password){
        return "{\n" +
                "    \"username\": \"" + username + "\",\n" +
                "    \"password\": \"" + password + "\"\n" +
                "}";
    }


    @Given("the firstname is {string}, and the lastname is {string} and the totalprice is {string}")
    public void theFirstnameIsAndTheLastnameIsAndTheTotalpriceIs(String firstname, String lastname, String totalPrice) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.totalprice = Integer.parseInt(totalPrice);
    }

    @Given("the check-in is {string} and the check-out is {string}")
    public void theCheckInIsAndTheCheckOutIs(String checkIn, String checkOut) {
        checkin = LocalDate.now().plusDays(Integer.parseInt(checkIn));
        checkout = LocalDate.now().plusDays(Integer.parseInt(checkOut));
    }

    @Given("the deposit paid is {string}, and the additional needs are {string}")
    public void theDepositPaidIsAndTheAdditionalNeedsAre(String deposit, String additionalneeds) {
        this.depositpaid = Boolean.parseBoolean(deposit);
        this.additionalneeds = additionalneeds;
    }

    @When("the user creates the booking")
    public void theUserCreatesTheBooking() {
        String checkIn = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(checkin);
        String checkOut = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(checkout);
        requestBody = getRequestBody(firstname, lastname, totalprice, depositpaid, checkIn, checkOut, additionalneeds);

        request.body(requestBody);
        request.contentType("text/xml");
        response = request.post();
    }

    @Then("the status code must be {string}")
    public void theStatusCodeMustBe(String expectedCode) {
        assertEquals(Integer.parseInt(expectedCode), response.statusCode());
    }

    @Then("the user booking details are correct")
    public void theUserBookingDetailsAreCorrect() {

    }

    @Given("the user has all bookings")
    public void theUserHasAllBookings() {
        theBaseUrlIs("https://restful-booker.herokuapp.com/booking");
        request.contentType("application/json");
        response = request.get();
    }

    @When("the user deletes booking {string}")
    public void theUserDeletesBooking(String bookingIndex) {
        bookingId = request.get().jsonPath().get("[" + (Integer.parseInt(bookingIndex) - 1) + "].bookingid");

        theBaseUrlIs("https://restful-booker.herokuapp.com/booking/" + bookingId);
        request.contentType("application/json");
        request.header(new Header("Authorization", "Basic YWRtaW46cGFzc3dvcmQxMjM="));
        response = request.delete();
    }

    @Then("the status code is {string}")
    public void theStatusCodeIs(String statusCode) {
        assertEquals(Integer.parseInt(statusCode), response.statusCode());
    }

    @Given("the user has basic authorization")
    public void theUserHasBaseAuthorization() {
        requestBody = getAuthJsonBody("admin", "password123");

        theBaseUrlIs("https://restful-booker.herokuapp.com/auth");
        request.body(requestBody);
        request.contentType("application/json");
        response = request.post();
        token = response.jsonPath().get("token");
    }

    @Then("the booking is not in the database")
    public void theBookingIsNotInTheDatabase() {

        theBaseUrlIs("https://restful-booker.herokuapp.com/booking/" + bookingId);
        request.contentType("application/json");
        response = request.get();
        theStatusCodeIs("404");

    }
}
