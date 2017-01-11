
package com.krebai.cucumbercassandra;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

@Repository
public class DefaultAccountDao implements AccountDao {

	private static final String SELECT_FROM_ACCOUNT_BALANCE = "SELECT * FROM ACCOUNT_BALANCE WHERE account = ?";

	private static final String ACCOUNT_COLUMN = "account";

	private static final String BALANCE_COLUMN = "balance";

	private final Session session;

	private final PreparedStatement accountBalancePreparedStatement;

	public DefaultAccountDao(@Autowired CassandraClusterManager clusterManager) {
		this.session = clusterManager.getSession();

		accountBalancePreparedStatement = this.session.prepare(SELECT_FROM_ACCOUNT_BALANCE);
	}

	@Override
	public BigDecimal getAccountBalance(String accountNumber) throws AccountNotFoundException {
		ResultSet resultSet = session.execute(accountBalancePreparedStatement.bind(accountNumber));
		Row row = resultSet.one();

		if (row == null) {
			throw new AccountNotFoundException();
		}
		return new Account(row.getString(ACCOUNT_COLUMN), row.getDecimal(BALANCE_COLUMN)).getBalance();
	}
}
