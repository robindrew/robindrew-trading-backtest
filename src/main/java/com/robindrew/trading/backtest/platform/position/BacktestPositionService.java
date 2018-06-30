package com.robindrew.trading.backtest.platform.position;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.robindrew.common.util.Check;
import com.robindrew.trading.backtest.IBacktestInstrument;
import com.robindrew.trading.backtest.platform.streaming.BacktestInstrumentPriceStream;
import com.robindrew.trading.backtest.platform.streaming.BacktestStreamingService;
import com.robindrew.trading.platform.positions.AbstractPositionService;
import com.robindrew.trading.position.IPosition;
import com.robindrew.trading.position.PositionBuilder;
import com.robindrew.trading.position.closed.ClosedPosition;
import com.robindrew.trading.position.closed.IClosedPosition;
import com.robindrew.trading.position.order.IPositionOrder;
import com.robindrew.trading.price.candle.IPriceCandle;
import com.robindrew.trading.price.candle.streaming.IStreamingCandlePrice;
import com.robindrew.trading.price.decimal.IDecimal;
import com.robindrew.trading.price.precision.IPricePrecision;
import com.robindrew.trading.provider.ITradingProvider;
import com.robindrew.trading.trade.money.IMoney;

public class BacktestPositionService extends AbstractPositionService {

	private static final Logger log = LoggerFactory.getLogger(BacktestPositionService.class);

	private final IMoney balance;
	private final AtomicLong nextId = new AtomicLong(0);
	private final Set<IPosition> openPositions = new CopyOnWriteArraySet<>();
	private final List<IClosedPosition> closedPositions = new CopyOnWriteArrayList<>();
	private final BacktestStreamingService streaming;
	private final IDecimal spread;

	public BacktestPositionService(ITradingProvider provider, IMoney balance, BacktestStreamingService streaming, IDecimal spread) {
		super(provider);
		this.balance = Check.notNull("balance", balance);
		this.streaming = Check.notNull("streaming", streaming);
		this.spread = Check.notNull("spread", spread);
	}

	protected String getNextId() {
		return "POS#" + nextId.incrementAndGet();
	}

	@Override
	public List<IPosition> getAllPositions() {
		return ImmutableList.copyOf(openPositions);
	}

	@Override
	public IClosedPosition closePosition(IPosition position) {
		if (!openPositions.remove(position)) {
			throw new IllegalArgumentException("Position does not exist: " + position + " in " + openPositions);
		}

		IBacktestInstrument instrument = (IBacktestInstrument) position.getInstrument();
		IPricePrecision precision = instrument.getPrecision();

		BacktestInstrumentPriceStream stream = getPriceStream(instrument);
		IStreamingCandlePrice price = stream.getPrice();

		IPriceCandle latest = price.getSnapshot().getLatest();
		BigDecimal closePrice = precision.toBigDecimal(latest.getMidClosePrice());
		IClosedPosition closed = new ClosedPosition(position, latest.getCloseDate(), closePrice);
		if (closed.isProfit()) {
			balance.add(closed.getProfit());
			log.info("Profit: {} ({})", closed.getProfit(), position);
		} else {
			balance.subtract(closed.getLoss());
			log.info("Loss: {} ({})", closed.getLoss(), position);
		}
		log.info("Funds: {}", balance);

		closedPositions.add(closed);
		return closed;
	}

	protected BacktestInstrumentPriceStream getPriceStream(IBacktestInstrument instrument) {
		return (BacktestInstrumentPriceStream) streaming.getPriceStream(instrument);
	}

	@Override
	public IPosition openPosition(IPositionOrder order) {

		IBacktestInstrument instrument = (IBacktestInstrument) order.getInstrument();
		IPricePrecision precision = instrument.getPrecision();

		BacktestInstrumentPriceStream stream = getPriceStream(instrument);
		IStreamingCandlePrice price = stream.getPrice();

		String id = getNextId();
		IPriceCandle latest = price.getSnapshot().getLatest();
		BigDecimal openPrice = precision.toBigDecimal(latest.getMidClosePrice());

		PositionBuilder builder = new PositionBuilder();
		builder.setId(id);
		builder.setInstrument(order.getInstrument());
		builder.setDirection(order.getDirection());
		builder.setOpenDate(latest.getCloseDate());
		builder.setCurrency(order.getTradeCurrency());
		builder.setOpenPrice(openPrice);
		builder.setTradeSize(order.getTradeSize());
		if (order.getDirection().isBuy()) {
			builder.setProfitLimitPrice(openPrice.add(new BigDecimal(order.getProfitLimitDistance())));
			builder.setStopLossPrice(openPrice.subtract(new BigDecimal(order.getStopLossDistance())));
		} else {
			builder.setProfitLimitPrice(openPrice.subtract(new BigDecimal(order.getProfitLimitDistance())));
			builder.setStopLossPrice(openPrice.add(new BigDecimal(order.getStopLossDistance())));
		}
		IPosition position = builder.build();

		openPositions.add(position);
		return position;
	}

}
