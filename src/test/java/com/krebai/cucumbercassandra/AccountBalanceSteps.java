
package com.krebai.cucumbercassandra;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class AccountBalanceSteps {

	private String accountNumber;

	private BigDecimal balance;

	private boolean isAccountNotFoundExceptionThrown;

	@Given("^A client with the account number (\\d+)$")
	public void a_client_with_the_account_number(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	@When("^I ask for the available balance$")
	public void i_ask_for_the_available_balance() {
		try {
			this.balance = getAccountBalance(accountNumber);
		} catch (AccountNotFoundException e) {
			this.isAccountNotFoundExceptionThrown = true;
		}
	}

	@Then("^I get the corresponding balance (\\d+)$")
	public void i_get_the_corresponding_balance(String balance) {
		assertThat(this.balance).isEqualTo(new BigDecimal(balance));
	}

	@Given("^A client with an invalid account number (\\d+)$")
	public void a_client_with_an_invalid_account_number(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	@Then("^I get the error Account not found$")
	public void i_get_the_error_Account_not_found() {
		assertThat(this.isAccountNotFoundExceptionThrown).isTrue();
	}

	private BigDecimal getAccountBalance(String accountNumber) throws AccountNotFoundException {
		if ("40000001939".equals(accountNumber)) {
			return new BigDecimal("1200");
		}
		throw new AccountNotFoundException();
	}
}