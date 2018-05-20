package com.robindrew.trading.backtest.context;

import static com.robindrew.common.util.Check.notNull;

import com.robindrew.common.util.Check;
import com.robindrew.trading.backtest.platform.BacktestTradingPlatform;
import com.robindrew.trading.price.candle.format.pcf.source.IPcfSourceSet;

public class BacktestContext {

	private final IPcfSourceSet sourceSet;
	private final BacktestTradingPlatform platform;

	public BacktestContext(BacktestTradingPlatform platform, IPcfSourceSet sourceSet) {
		this.platform = Check.notNull("platform", platform);
		this.sourceSet = notNull("sourceSet", sourceSet);
	}

	public BacktestTradingPlatform getPlatform() {
		return platform;
	}

	public IPcfSourceSet getSourceSet() {
		return sourceSet;
	}

}
