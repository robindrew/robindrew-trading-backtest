package com.robindrew.trading.backtest;

import com.robindrew.common.util.Check;
import com.robindrew.trading.IInstrument;
import com.robindrew.trading.Instrument;
import com.robindrew.trading.price.precision.IPricePrecision;

public class BacktestInstrument extends Instrument implements IBacktestInstrument {

	private final IPricePrecision precision;

	public BacktestInstrument(IInstrument underlying, IPricePrecision precision) {
		super(underlying.getName(), underlying);
		this.precision = Check.notNull("precision", precision);	
	}

	@Override
	public IPricePrecision getPrecision() {
		return precision;
	}

}
