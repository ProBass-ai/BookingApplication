@delete-booking
Feature: REST - Delete Booking Feature
  Background:
    Given the user has basic authorization

  Scenario Outline: Validate that a user may delete a booking
    Given the user has all bookings
    When the user deletes booking "<booking index>"
    Then the status code is "<status>"
    Then the booking is not in the database

    Examples:
    | booking index | status |
    | 2             | 201    |
