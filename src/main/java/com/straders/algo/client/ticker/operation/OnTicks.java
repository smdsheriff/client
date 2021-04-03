package com.straders.algo.client.ticker.operation;

import java.util.ArrayList;

import com.straders.algo.client.ticker.models.Tick;

/**
 * Callback to listen to com.zerodhatech.ticker websocket on tick arrival event.
 */

/** OnTicks interface is called once ticks arrive. */
public interface OnTicks {
	void onTicks(ArrayList<Tick> ticks);
}
