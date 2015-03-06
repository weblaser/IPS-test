@ALL_TESTS
Feature:  Security - Notification

  Scenario: Update the notification destination
    Given the customer wants to update a notification for a server
    When the notification destination is updated via the notification resource
    Then the server notification destination is updated with new destination