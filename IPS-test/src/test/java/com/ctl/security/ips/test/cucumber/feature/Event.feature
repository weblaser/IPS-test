@ALL_TESTS
Feature: Security - Event

  @WIP
  Scenario: Send an event to a set notification destination URL
    Given an event occurs
    When the event notification is posted to the events endpoint
    Then the event information is sent to the correct URL