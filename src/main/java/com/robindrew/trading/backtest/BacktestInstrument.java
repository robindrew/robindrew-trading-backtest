package com.robindrew.trading.backtest;

import com.robindrew.trading.IInstrument;
import com.robindrew.trading.Instrument;

public class BacktestInstrument extends Instrument implements IBacktestInstrument {

	public BacktestInstrument(IInstrument underlying) {
		super(underlying.getName(), underlying);
	}

}
