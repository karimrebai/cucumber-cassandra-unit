
package com.krebai.cucumbercassandra;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class AccountBalanceSteps {

	private final AccountDao sut = applicationContext.getBean(AccountDao.class);

	private static ApplicationContext applicationContext;

	private String accountNumber;

	private BigDecimal balance;

	private boolean isAccountNotFoundExceptionThrown;

	static {
		try {
			CassandraUnitBuilder cassandraUnitBuilder = new CassandraUnitBuilder();
			cassandraUnitBuilder.startLocalCassandraInstance(CassandraUnitBuilder.LOCALHOST,
					CassandraUnitBuilder.DEFAULT_PORT, "create_account_balance_table.cql",
					CassandraUnitBuilder.KEY_SPACE);
			cassandraUnitBuilder.saveFixture(accounts());

			applicationContext = new AnnotationConfigApplicationContext(Configuration.class);
		} catch (Exception e) {
			Assert.fail("Fail to initialize test context: " + e.getMessage());
		}
	}

	private static List<Account> accounts() {
		List<Account> accounts = new ArrayList<>();
		accounts.add(new Account("40000001939", new BigDecimal("95.38")));
		return accounts;
	}

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
		return sut.getAccountBalance(accountNumber);
	}
}