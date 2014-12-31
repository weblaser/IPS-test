@ALL_TESTS @WIP @Regression
Feature:  Security - policies


  Scenario: Get valid response when getting all policies for account

    Given I have a valid account
    When I GET the policies
    Then I receive a response that contains the expected list of policies

  Scenario: Get invalid response when getting all policies for account

    Given I have an invalid account
    When I GET the policies
    Then I receive a response with an error message

  Scenario: Get valid response when getting a single policy from an account

    Given I have a valid account
    When I GET a valid policy
    Then I receive a response that contains the expected policy

  Scenario: Get invalid response when getting a single policy for an account

    Given I have an invalid account
    When I GET a valid policy
    Then I receive a response with error message 403 Forbidden.

  Scenario: Get valid response when creating a policy for an account

    Given I have a valid account
    When I POST a policy
    Then I receive a response that contains a uuid for the created policy

  Scenario: Get invalid response when creating a policy for an account

    Given I have an invalid account
    When I POST a policy
    Then I receive a response with error message 403 Forbidden.

  Scenario: Get valid response when updating a policy for an account

    Given I have an valid account
    When I PUT a valid policy
    Then I receive a response that does not contain an error message

  Scenario: Get invalid response when updating a policy for an account

    Given I have an invalid account
    When I PUT a valid policy
    Then I receive a response with error message 400 Bad Request.

  Scenario: Get valid response when updating a policy for an account

    Given I have a valid account
    When I PUT an invalid policy
    Then I receive a response with error message 400 Bad Request.

  Scenario: Get valid response when deleting a policy for an account

    Given I have an valid account
    When I DELETE a valid policy
    Then I receive a response that does not contain an error message

  Scenario: Get invalid response when deleting a policy for an account

    Given I have an invalid account
    When I DELETE a valid policy
    Then I receive a response with error message 403 Forbidden.

  Scenario: Get valid response when deleting a policy for an account

    Given I have a valid account
    When I DELETE an invalid policy
    Then I receive a response with error message 400 Bad Request.