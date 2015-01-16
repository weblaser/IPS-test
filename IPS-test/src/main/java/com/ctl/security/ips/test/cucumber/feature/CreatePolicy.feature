@ALL_TESTS @Regression
  Feature: Security - CreatePolicy  To log into the DSM API and create a new policy for a customer

    Scenario: Successfully create a new policy for a tenant
      Given I have a policy that I want to create in DSM
      When I execute the "create" operation against the DSM API
      Then I receive a new policy response