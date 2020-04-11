package com.robindrew.trading.backtest.streaming;

import static com.robindrew.trading.price.precision.PricePrecision.withDecimalPlaces;

import java.io.File;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Test;

import com.robindrew.trading.IInstrument;
import com.robindrew.trading.Instruments;
import com.robindrew.trading.backtest.BacktestInstrument;
import com.robindrew.trading.backtest.IBacktestInstrument;
import com.robindrew.trading.backtest.platform.history.BacktestHistoryService;
import com.robindrew.trading.backtest.platform.streaming.BacktestStreamingService;
import com.robindrew.trading.backtest.platform.streaming.IBacktestInstrumentPriceStream;
import com.robindrew.trading.price.candle.format.pcf.source.IPcfSourceProviderLocator;
import com.robindrew.trading.price.candle.format.pcf.source.IPcfSourceProviderManager;
import com.robindrew.trading.price.candle.format.pcf.source.file.PcfFileProviderLocator;
import com.robindrew.trading.provider.ITradingProvider;
import com.robindrew.trading.provider.TradingProvider;

import junit.framework.Assert;

public class BacktestStreamingTest {

	@Test
	public void subscribeToStream() {

		File rootDirectory = new File("src/test/resources/pcf");
		ITradingProvider provider = TradingProvider.FXCM;
		IInstrument instrument = Instruments.GBP_USD;

		// Source the price candles
		IPcfSourceProviderLocator locator = new PcfFileProviderLocator(rootDirectory);
		IPcfSourceProviderManager manager = locator.getProvider(provider);
		BacktestHistoryService history = new BacktestHistoryService(manager);

		// Create streaming service
		try (BacktestStreamingService streaming = new BacktestStreamingService(history)) {

			// Subscribe
			IBacktestInstrument backtestInstrument = BacktestInstrument.of(instrument, withDecimalPlaces(4));
			Assert.assertTrue(streaming.subscribe(backtestInstrument));

			// Get the stream
			IBacktestInstrumentPriceStream stream = streaming.getPriceStream(backtestInstrument);

			// Register the sink
			AtomicLong candleCount = new AtomicLong(0);
			stream.register(candle -> {
				Assert.assertEquals(2017, candle.getCloseDate().getYear());
				candleCount.incrementAndGet();
			});

			// Run the stream
			stream.run();

			Assert.assertEquals(368248l, candleCount.get());
		}
	}

}
