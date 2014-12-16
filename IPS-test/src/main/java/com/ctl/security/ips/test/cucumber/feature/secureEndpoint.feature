@ALL_TESTS @SECURE_ENDPOINT @Regression
Feature:  Security - secureEndpoint


Scenario: Receive an unauthorized response when a non-authenticated caller attempts a get on a secure endpoint
Given I want to execute a GET on a secure endpoint
And I am not authorized 
When I execute a GET on a secure endpoint
Then I receive an unauthorized response

