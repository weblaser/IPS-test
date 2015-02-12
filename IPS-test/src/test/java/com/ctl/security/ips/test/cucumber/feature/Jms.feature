@ALL_TESTS @Regression
Feature: Security - Jms - The ips security project is able to produce and consume messages on the ActiveMQ queue.


@WIP
Scenario: Successfully send a message to the queue
Given a message is ready to be sent to the queue
When the message is sent to the queue
Then the message is accepted and placed on the queue
