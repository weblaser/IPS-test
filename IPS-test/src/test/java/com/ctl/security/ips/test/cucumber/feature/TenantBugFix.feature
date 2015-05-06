
Feature:  Security - TenantBugFix

@TENANT_BUG
  Scenario: Get valid response when creating a policy for an account
    Given I have a valid account
    And a tenant will be created
    When I POST a policy
    Then I receive a response that contains a uuid for the created policy