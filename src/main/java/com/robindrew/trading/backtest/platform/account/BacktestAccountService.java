package com.robindrew.trading.backtest.platform.account;

import com.robindrew.common.util.Check;
import com.robindrew.trading.platform.account.AbstractAccountService;
import com.robindrew.trading.provider.ITradingProvider;
import com.robindrew.trading.trade.balance.Balance;
import com.robindrew.trading.trade.cash.ICash;

public class BacktestAccountService extends AbstractAccountService {

	private final String accountId;
	private final Balance balance;

	public BacktestAccountService(ITradingProvider provider, String accountId, Balance balance) {
		super(provider);
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
