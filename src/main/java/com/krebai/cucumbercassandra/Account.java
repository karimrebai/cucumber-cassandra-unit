
package com.krebai.cucumbercassandra;

import java.math.BigDecimal;

public class Account {

	private String accountNumber;

	private BigDecimal balance;

	public Account(String accountNumber, BigDecimal balance) {
		this.accountNumber = accountNumber;
		this.balance = balance;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public BigDecimal getBalance() {
		return balance;
	}
}
