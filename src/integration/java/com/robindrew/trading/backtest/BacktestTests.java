package com.robindrew.trading.backtest;

import static com.robindrew.common.test.UnitTests.getProperty;

import org.junit.Test;

import com.robindrew.trading.Instruments;
import com.robindrew.trading.backtest.context.BacktestContext;
import com.robindrew.trading.backtest.context.BacktestContextBuilder;
import com.robindrew.trading.backtest.platform.BacktestTradingPlatform;
import com.robindrew.trading.backtest.platform.IBacktestTradingPlatform;
import com.robindrew.trading.backtest.platform.streaming.IBacktestInstrumentPriceStream;
import com.robindrew.trading.backtest.platform.streaming.IBacktestStreamingService;
import com.robindrew.trading.price.precision.PricePrecision;
import com.robindrew.trading.provider.TradingProvider;

public class BacktestTests {

	@Test
	public void simpleTest() {

		String dataDirectory = getProperty("data.dir");
		TradingProvider provider = TradingProvider.valueOf(getProperty("provider"));
		IBacktestInstrument instrument = new BacktestInstrument(Instruments.GBP_USD, new PricePrecision(1));
		
		BacktestContextBuilder builder = new BacktestContextBuilder();
		builder.setDataDirectory(dataDirectory);
		builder.setProvider(provider);
		builder.setInstrument(instrument);
		BacktestContext context = builder.build();

		IBacktestTradingPlatform platform = context.getPlatform();
		IBacktestStreamingService streaming = platform.getStreamingService();
		IBacktestInstrumentPriceStream priceStream = streaming.getPriceStream(instrument);
		priceStream.register(new SimpleVolatilityStrategy(platform, instrument));
		priceStream.run();
	}

}
