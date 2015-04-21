@REGRESSION @WIP
Feature: Security - Informant


  Scenario: An account is notified when a event is triggered on a configuration item
    Given there is 3 configuration item running
    And the notification destination is set for all configuration items
    When an event are posted to DSM for 1 of the configuration items
    Then the events are posted to the attacked notification destinations
    And no events are posted to the safe notification destinations

