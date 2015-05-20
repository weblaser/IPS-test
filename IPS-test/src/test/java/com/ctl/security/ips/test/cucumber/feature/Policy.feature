@REGRESSION
Feature:  Security - policy

  Scenario: Get valid response when creating a policy for an account
    Given I have a valid account
    When I POST a linux policy
    Then I receive a response that contains a uuid for the created policy


  Scenario: Install a policy on a windows server
    Given I have a valid account
    When I POST a windows policy
    Then a security profile is created in the DSM
    And a policy is created in our CMDB

  Scenario: Get invalid response when creating a policy for an account
    Given I have an invalid account
    When I POST a linux policy
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
    And I POST a linux policy
    When I DELETE a valid policy
    Then I see that the policy has been deleted
    #And I see that the agent has been deleted

  Scenario: Get invalid response when deleting a policy for an account when ???
    Given I have an invalid account
    And I POST a linux policy
    When I DELETE a invalid policy
    Then I receive a response with error message 403 Forbidden.

