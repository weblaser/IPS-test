@ALL_TESTS @Regression @ENVIRONMENT
Feature: Security - Jms - The ips security project is able to produce and consume messages on the ActiveMQ queue.



Scenario: Successfully produce a message on the queue
Given a message is ready to be sent to the queue
When the message is sent to the queue
Then the message is produced on the queue



Scenario: Successfully consume a message on the queue
Given a listener is listening for messages on the queue
When a message is sent to the queue
Then the message is consumed from the queue
