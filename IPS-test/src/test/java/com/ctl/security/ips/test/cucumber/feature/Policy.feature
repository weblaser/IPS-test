@ALL_TESTS @Regression
Feature:  Security - policy


  Scenario: Get valid response when creating a policy for an account

    Given I have a valid account
    When I POST a policy
    Then I receive a response that contains a uuid for the created policy


  Scenario: Get invalid response when creating a policy for an account

    Given I have an invalid account
    When I POST a policy
    Then I receive a response with error message 403 Forbidden.


  Scenario: Get valid response when getting all policies for account

    Given I have a valid account
    When I GET the policies
    Then I receive a response that contains the expected list of policies


  Scenario: Get invalid response when getting all policies for account

    Given I have an invalid account
    When I GET the policies
    Then I receive a response with error message 403 Forbidden.


  Scenario: Get valid response when getting a single policy from an account

    Given I have a valid account
    When I GET a valid policy
    Then I receive a response that contains the expected policy


  Scenario: Get invalid response when getting a single policy for an account

    Given I have an invalid account
    When I GET a valid policy
    Then I receive a response with error message 403 Forbidden.

@SMOKE
  Scenario: Get valid response when updating a policy for an account

    Given I have an valid account
    When I PUT a valid policy
    Then I receive a response that does not contain an error message


  Scenario: Get valid response when deleting a policy for an account

    Given I have an valid account
    And I POST a policy
    When I DELETE a valid policy
    Then I see that the policy has been deleted


  Scenario: Get invalid response when deleting a policy for an account

    Given I have an invalid account
    And I POST a policy
    When I DELETE a valid policy
    Then I receive a response with error message 403 Forbidden.
