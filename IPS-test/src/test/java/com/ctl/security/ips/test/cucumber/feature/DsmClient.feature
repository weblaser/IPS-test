@REGRESSION
Feature: Security - DsmClient  To log into the DSM API and create a new policy for a customer

  Scenario: Successfully create a new policy for a tenant
    Given I have a policy that I want to create in DSM
    When I execute the "create" operation against the DSM API
    Then I receive a new policy response
    And I am able to retrieve the newly created policy


  Scenario: Successfully create a tenant for a customer
    Given a customer tenant is ready to be created
    When the dsm rest client is used to create the tenant
    Then the tenant has been created in DSM


  Scenario: Successfully retrieve a tenant from the DSM
    Given a tenant already exists in the DSM
    When the dsm rest client is used to retrieve the tenant
    Then the correct tenant is returned


  Scenario: Successfully delete tenant from DSM
    Given a tenant already exists in the DSM
    When the dsm rest client is used to delete the tenant
    Then the tenant is pending deletion