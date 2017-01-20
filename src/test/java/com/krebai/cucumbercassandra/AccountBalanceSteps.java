
package com.krebai.cucumbercassandra;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.cassandraunit.utils.EmbeddedCassandraServerHelper;
import org.junit.Assert;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class AccountBalanceSteps {

	final static String LOCALHOST = "127.0.0.1";

	final static int DEFAULT_PORT = 9142;

	final static String THE_KEY_SPACE = "the_keyspace";

	private final AccountDao sut = applicationContext.getBean(AccountDao.class);

	private static ApplicationContext applicationContext;

	private String accountNumber;

	private BigDecimal balance;

	private boolean isAccountNotFoundExceptionThrown;

	static {
		try {
			EmbeddedCassandraServerHelper.startEmbeddedCassandra();
			new CassandraUnitDataLoader(LOCALHOST, DEFAULT_PORT).load(THE_KEY_SPACE, "account_balance_dataset.cql");
			applicationContext = new AnnotationConfigApplicationContext(Configuration.class);
		} catch (Exception e) {
			Assert.fail("Fail to initialize test context: " + e.getMessage());
		}
	}

	@Given("^A client with the account number (\\d+)$")
	public void a_client_with_the_account_number(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	@When("^I ask for the available balance$")
	public void i_ask_for_the_available_balance() {
		try {
			this.balance = sut.getAccountBalance(accountNumber);
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

}
