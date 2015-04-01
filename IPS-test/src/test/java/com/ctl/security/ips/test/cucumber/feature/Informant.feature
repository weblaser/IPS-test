@REGRESSION
Feature: Security - Informant

  Scenario: Request one event from DSM
    Given a DSM agent is running on configuration item
    When events are posted to DSM
    Then the events are posted to the correct notification destination