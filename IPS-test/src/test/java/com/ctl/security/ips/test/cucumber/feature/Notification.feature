@REGRESSION @WIP
Feature:  Security - Notification

  Scenario: Update the notification destination
    Given the customer has a notification destination for a server
    When the notification destination is updated via the notification resource
    Then the server notification destination is updated with new destination


  Scenario: Delete the notification destination
    Given the customer has a notification destination for a server
    When the notification destination is deleted via the notification resource
    Then there is no notification destination in the configuration item