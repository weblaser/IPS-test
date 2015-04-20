@REGRESSION
Feature: Security - Informant


  Scenario: An account is notified when a event is triggered on a configuration item
    Given there is 15 configuration item running
    And the notification destination is set for all configuration items
    When an event are posted to DSM for 7 of the configuration items
    Then the events are posted to the attacked notification destinations
    And no events are posted to the safe notification destinations
