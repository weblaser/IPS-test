@ALL_TESTS
Feature: Security - Event

  Scenario: Send an event to a set notification destination URL
    Given an event occurs
    And the notification destination is valid
    When the event notification is posted to the events endpoint
    Then the event information is sent to the correct URL

  Scenario: Send an event to a invalid notification destination URL
    Given an event occurs
    And the notification destination is invalid
    When the event notification is posted to the events endpoint
    Then the event information is attempted to be sent to the URL multiple times