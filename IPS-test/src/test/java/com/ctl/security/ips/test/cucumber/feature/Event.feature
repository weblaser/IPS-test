@REGRESSION
Feature: Security - Event


  Scenario: Send an event to a set notification destination URL
    Given an event occurs for a valid configuration item
    And the notification destination is valid
    When the event notification is posted to the events endpoint
    Then the event information is sent to the correct URL


  Scenario: Send an event to a invalid notification destination URL
    Given an event occurs for a valid configuration item
    And the notification destination is invalid
    When the event notification is posted to the events endpoint
    Then the event information is attempted to be sent to the URL multiple times

@WIP
  Scenario: Persist successful event notifications in product user activity document
    Given an event occurs for a valid configuration item
    And the notification destination is valid
    When the event notification is posted to the events endpoint
    Then the notification is persisted in the product user activity document