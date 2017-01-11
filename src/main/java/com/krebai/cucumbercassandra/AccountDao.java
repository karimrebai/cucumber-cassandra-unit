
package com.krebai.cucumbercassandra;

import java.math.BigDecimal;

public interface AccountDao {

	BigDecimal getAccountBalance(String accountNumber) throws AccountNotFoundException;

}