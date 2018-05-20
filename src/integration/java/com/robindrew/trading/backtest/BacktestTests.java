package com.robindrew.trading.backtest;

import static com.robindrew.common.test.UnitTests.getProperty;

import org.junit.Test;

import com.robindrew.trading.Instruments;
import com.robindrew.trading.backtest.context.BacktestContext;
import com.robindrew.trading.backtest.context.BacktestContextBuilder;
import com.robindrew.trading.backtest.platform.BacktestTradingPlatform;
import com.robindrew.trading.backtest.platform.streaming.BacktestInstrumentPriceStream;
import com.robindrew.trading.price.precision.PricePrecision;
import com.robindrew.trading.provider.TradingProvider;

public class BacktestTests {

	@Test
	public void simpleTest() {

		String rootDirectory = getProperty("root.dir");
		TradingProvider provider = TradingProvider.valueOf(getProperty("provider"));

		BacktestContextBuilder builder = new BacktestContextBuilder();
		builder.setRootDirectory(rootDirectory);
		builder.setProvider(provider);
		builder.setInstrument(Instruments.GBP_USD);
		BacktestContext context = builder.build();

		BacktestTradingPlatform platform = context.getPlatform();

		IBacktestInstrument instrument = new BacktestInstrument(Instruments.GBP_USD);
		platform.getPositionService().setPrecision(instrument, new PricePrecision(1));
		BacktestInstrumentPriceStream priceStream = (BacktestInstrumentPriceStream) platform.getStreamingService().getPriceStream(instrument);
		priceStream.register(new SimpleVolatilityStrategy(platform, instrument));
		priceStream.run();
	}

}
