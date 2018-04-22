package com.robindrew.trading.backtest;

import java.util.concurrent.atomic.AtomicBoolean;

import com.robindrew.common.util.Check;
import com.robindrew.trading.platform.streaming.StreamingService;
import com.robindrew.trading.price.history.IInstrumentPriceHistory;

public class BacktestStreamingService extends StreamingService<IBacktestInstrument> {

	private final AtomicBoolean connected = new AtomicBoolean(false);
	private BacktestHistoryService history;

	public BacktestStreamingService(BacktestHistoryService history) {
		this.history = Check.notNull("history", history);
	}

	@Override
	public boolean subscribe(IBacktestInstrument instrument) {
		IInstrumentPriceHistory priceHistory = history.getPriceHistory(instrument);
		BacktestInstrumentPriceStream stream = new BacktestInstrumentPriceStream(instrument, priceHistory);
		registerStream(stream);
		return true;
	}

	@Override
	public boolean unsubscribe(IBacktestInstrument instrument) {
		unregisterStream(instrument);
		return true;
	}

	@Override
	public void connect() {
		connected.set(true);
	}

	@Override
	public boolean isConnected() {
		return connected.get();
	}

}
