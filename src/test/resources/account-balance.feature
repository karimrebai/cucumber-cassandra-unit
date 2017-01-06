Feature: Get the account balance given an account

  Scenario: Account not found
    Given A client with an invalid account number 00000000000
    When I ask for the available balance
    Then I get the error Account not found

  Scenario: Account found
    Given A client with the account number 40000001939
    When I ask for the available balance
    Then I get the corresponding balance 1200