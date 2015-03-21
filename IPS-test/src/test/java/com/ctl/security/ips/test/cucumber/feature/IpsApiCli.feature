@REGRESSION
Feature:  Security - IpsCli



  Scenario: Get valid response when getting a single policy from an account
    Given there is a policy to retrieve
    When the ips-cli is used to retrieve that policy
    Then the policy is retrieved