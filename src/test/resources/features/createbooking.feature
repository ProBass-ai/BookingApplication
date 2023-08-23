@create-booking
Feature: REST - Create Booking Feature

  Background:
    Given the base uri is "https://restful-booker.herokuapp.com/booking"

  Scenario Outline: Validate that as a user I may create a booking
    Given the firstname is "<firstname>", and the lastname is "<lastname>" and the totalprice is "<totalprice>"
    And the check-in is "<check-in>" and the check-out is "<checkout>"
    And the deposit paid is "<depositpaid>", and the additional needs are "<additionalneeds>"
    When the user creates the booking
    Then the status code must be "200"
    And the user booking details are correct

    Examples:
      | firstname | lastname    | totalprice | depositpaid | check-in | checkout | additionalneeds   | status_code |
      | Vusi      | Thembekwayo | 1000       | 500         | 0        | 2        | Breakfast, Supper | 200         |

