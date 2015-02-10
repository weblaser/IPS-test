@ALL_TESTS @Regression
Feature: Security - LogInClient To be able to log into the DSM API


  Scenario: Log in successfully to the the DSM API
    Given I have user account credentials
    When I attempt to authenticate against the dsm api
    Then I receive a valid session id token


  Scenario: Log in with incorrect User
    Given I have an incorrect user
    When I attempt to authenticate against the dsm api
    Then I receive an authentication error


  Scenario: Log in with incorrect password
    Given I have an incorrect password
    When I attempt to authenticate against the dsm api
    Then I receive an authentication error