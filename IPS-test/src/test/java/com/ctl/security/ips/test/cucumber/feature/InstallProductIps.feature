Feature: Security - LLFT
  Testing to install the IPS from start to finish.
@WIP
  Scenario: Install all pieces of IPS
    Given a customer wants to install IPS on a VM
    When the blueprint finishes in CLC
    Then the new policy is persisted in all correct areas