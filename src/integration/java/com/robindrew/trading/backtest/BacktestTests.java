package com.robindrew.trading.backtest;

import java.io.File;

import org.junit.Test;

import com.robindrew.trading.Instruments;
import com.robindrew.trading.backtest.platform.BacktestTradingPlatform;
import com.robindrew.trading.backtest.platform.account.BacktestAccountService;
import com.robindrew.trading.backtest.platform.history.BacktestHistoryService;
import com.robindrew.trading.backtest.platform.position.BacktestPositionService;
import com.robindrew.trading.backtest.platform.streaming.BacktestInstrumentPriceStream;
import com.robindrew.trading.backtest.platform.streaming.BacktestStreamingService;
import com.robindrew.trading.price.candle.format.pcf.source.IPcfSourceManager;
import com.robindrew.trading.price.candle.format.pcf.source.file.PcfFileManager;
import com.robindrew.trading.price.precision.PricePrecision;
import com.robindrew.trading.trade.balance.Balance;
import com.robindrew.trading.trade.cash.Cash;

public class BacktestTests {

	private static final String PCF_DATA_DIR = "C:\\development\\repository\\git\\robindrew2\\robindrew-tradedata\\data\\pcf";

	@Test
	public void simpleTest() {

		BacktestTradingPlatform platform = getPlatform();

		IBacktestInstrument instrument = new BacktestInstrument(Instruments.GBP_USD);
		platform.getPositionService().setPrecision(instrument, new PricePrecision(1));
		BacktestInstrumentPriceStream priceStream = (BacktestInstrumentPriceStream) platform.getStreamingService().getPriceStream(instrument);
		priceStream.register(new SimpleVolatilityStrategy(platform, instrument));
		priceStream.run();
	}

	private BacktestTradingPlatform getPlatform() {

		String accountId = "12345";
		Balance balance = new Balance(new Cash(10000));
		File directory = new File(PCF_DATA_DIR);

		IPcfSourceManager manager = new PcfFileManager(directory);
		BacktestHistoryService history = new BacktestHistoryService(manager);

		BacktestAccountService account = new BacktestAccountService(accountId, balance);
		BacktestStreamingService streaming = new BacktestStreamingService(history);
		BacktestPositionService position = new BacktestPositionService(balance, streaming);
		BacktestTradingPlatform platform = new BacktestTradingPlatform(account, history, streaming, position);

		return platform;
	}

}
