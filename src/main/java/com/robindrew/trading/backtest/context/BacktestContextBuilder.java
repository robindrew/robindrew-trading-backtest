package com.robindrew.trading.backtest.context;

import static com.robindrew.common.util.Check.notNull;

import java.io.File;

import com.robindrew.common.util.Check;
import com.robindrew.trading.IInstrument;
import com.robindrew.trading.backtest.platform.BacktestTradingPlatform;
import com.robindrew.trading.backtest.platform.account.BacktestAccountService;
import com.robindrew.trading.backtest.platform.history.BacktestHistoryService;
import com.robindrew.trading.backtest.platform.position.BacktestPositionService;
import com.robindrew.trading.backtest.platform.streaming.BacktestStreamingService;
import com.robindrew.trading.price.candle.format.pcf.source.IPcfSourceProviderManager;
import com.robindrew.trading.price.candle.format.pcf.source.IPcfSourceSet;
import com.robindrew.trading.price.candle.format.pcf.source.file.PcfFileManager;
import com.robindrew.trading.provider.ITradingProvider;
import com.robindrew.trading.trade.balance.Balance;

public class BacktestContextBuilder {

	private File rootDirectory;
	private ITradingProvider provider;
	private IInstrument instrument;
	private String accountId = "BacktestAccount001";
	private Balance balance = Balance.fromCash(10000);

	public BacktestContextBuilder setRootDirectory(File rootDirectory) {
		this.rootDirectory = Check.existsDirectory("rootDirectory", rootDirectory);
		return this;
	}

	public BacktestContextBuilder setRootDirectory(String rootDirectory) {
		return setRootDirectory(new File(rootDirectory));
	}

	public BacktestContextBuilder setProvider(ITradingProvider provider) {
		this.provider = notNull("provider", provider);
		return this;
	}

	public BacktestContextBuilder setInstrument(IInstrument instrument) {
		this.instrument = notNull("instrument", instrument);
		return this;
	}

	public BacktestContext build() {
		if (rootDirectory == null) {
			throw new IllegalStateException("rootDirectory not set");
		}
		if (provider == null) {
			throw new IllegalStateException("provider not set");
		}
		if (instrument == null) {
			throw new IllegalStateException("instrument not set");
		}

		PcfFileManager manager = new PcfFileManager(rootDirectory);
		IPcfSourceProviderManager providerManager = manager.getProvider(provider);
		IPcfSourceSet sourceSet = providerManager.getSourceSet(instrument);
		BacktestHistoryService history = new BacktestHistoryService(providerManager);

		BacktestAccountService account = new BacktestAccountService(provider, accountId, balance);
		BacktestStreamingService streaming = new BacktestStreamingService(history);
		BacktestPositionService position = new BacktestPositionService(provider, balance, streaming);

		BacktestTradingPlatform platform = new BacktestTradingPlatform(account, history, streaming, position);

		return new BacktestContext(platform, sourceSet);
	}
}
