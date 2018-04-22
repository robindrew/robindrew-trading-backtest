package com.robindrew.trading.backtest;

import java.io.File;

import org.junit.Test;

import com.robindrew.trading.Instruments;
import com.robindrew.trading.price.candle.format.pcf.source.IPcfSourceManager;
import com.robindrew.trading.price.candle.format.pcf.source.file.PcfFileManager;
import com.robindrew.trading.price.precision.PricePrecision;
import com.robindrew.trading.trade.funds.AccountFunds;
import com.robindrew.trading.trade.funds.Cash;

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

		AccountFunds funds = new AccountFunds(new Cash(10000));
		File directory = new File(PCF_DATA_DIR);

		IPcfSourceManager manager = new PcfFileManager(directory);
		BacktestHistoryService history = new BacktestHistoryService(manager);

		BacktestStreamingService streaming = new BacktestStreamingService(history);
		BacktestPositionService position = new BacktestPositionService(funds, streaming);
		BacktestTradingPlatform platform = new BacktestTradingPlatform(history, streaming, position);

		return platform;
	}

}
