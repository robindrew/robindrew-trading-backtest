package com.robindrew.trading.backtest;

import com.robindrew.common.util.Check;
import com.robindrew.trading.platform.account.IAccountService;
import com.robindrew.trading.trade.balance.Balance;
import com.robindrew.trading.trade.cash.ICash;

public class BacktestAccountService implements IAccountService {

	private final String accountId;
	private final Balance balance;

	public BacktestAccountService(String accountId, Balance balance) {
		this.accountId = Check.notEmpty("accountId", accountId);
		this.balance = Check.notNull("balance", balance);
	}

	@Override
	public String getAccountId() {
		return accountId;
	}

	@Override
	public ICash getBalance() {
		return balance.get();
	}

	public void setBalance(ICash balance) {
		this.balance.set(balance);
	}

}
