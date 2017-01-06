
package com.krebai.cucumbercassandra;

import fr.ing.authorizationengine.core.dao.entity.AccountBalanceEntity;

public interface AccountDao {

	AccountBalanceEntity getAccountBalance(String accountNumber);

}