@ALL_TESTS
Feature: Security - Event

  Scenario: Send an event to a set notification destination URL
    Given the costumer has a notification destination that has a set URL
    When an event occurs
    Then the URL given has been updated with the event information