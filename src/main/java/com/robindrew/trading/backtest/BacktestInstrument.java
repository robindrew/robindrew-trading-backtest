package com.robindrew.trading.backtest;

import com.robindrew.common.util.Check;
import com.robindrew.trading.IInstrument;
import com.robindrew.trading.Instrument;
import com.robindrew.trading.price.precision.IPricePrecision;

public class BacktestInstrument extends Instrument implements IBacktestInstrument {

	public static BacktestInstrument of(IInstrument instrument) {
		return new BacktestInstrument(instrument, instrument.getPrecision());
	}

	public static BacktestInstrument of(IInstrument instrument, IPricePrecision precision) {
		return new BacktestInstrument(instrument, precision);
	}

	private final IPricePrecision precision;

	private BacktestInstrument(IInstrument underlying, IPricePrecision precision) {
		super(underlying.getName(), underlying);
		this.precision = Check.notNull("precision", precision);
	}


	@Override
	public IPricePrecision getPrecision() {
		return precision;
	}

}
