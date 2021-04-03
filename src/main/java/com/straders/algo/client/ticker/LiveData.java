package com.straders.algo.client.ticker;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import com.straders.algo.client.ticker.models.Tick;
import com.straders.algo.client.ticker.operation.OnConnect;
import com.straders.algo.client.ticker.operation.OnDisconnect;
import com.straders.algo.client.ticker.operation.OnError;
import com.straders.algo.client.ticker.operation.OnTicks;

public class LiveData {

	/**
	 * Demonstrates com.zerodhatech.ticker connection, subcribing for
	 * instruments, unsubscribing for instruments, set mode of tick data,
	 * com.zerodhatech.ticker disconnection
	 */
	public void tickerUsage(ArrayList<Long> tokens, String accessToken) throws Exception {

		final Ticker tickerProvider = new Ticker(accessToken);
		/**
		 * To get live price use websocket connection. It is recommended to use
		 * only one websocket connection at any point of time and make sure you
		 * stop connection, once user goes out of app. custom url points to new
		 * endpoint which can be used till complete Kite Connect 3 migration is
		 * done.
		 */

		tickerProvider.setOnConnectedListener(new OnConnect() {
			@Override
			public void onConnected() {
				/**
				 * Subscribe ticks for token. By default, all tokens are
				 * subscribed for modeQuote.
				 */
				System.out.println("Connected");
				tickerProvider.subscribe(tokens);
				tickerProvider.setMode(tokens, tickerProvider.marketData);
			}
		});

		tickerProvider.setOnDisconnectedListener(new OnDisconnect() {
			@Override
			public void onDisconnected() {
				// tickerProvider.doReconnect();
				System.out.println("Disconnected");
			}
		});

		/** Set error listener to listen to errors. */
		tickerProvider.setOnErrorListener(new OnError() {
			@Override
			public void onError(String error) {
				System.out.println("Error Listner" + error);
			}

			@Override
			public void onError(Exception exception) {
				exception.printStackTrace();
			}
		});

		tickerProvider.setOnTickerArrivalListener(new OnTicks() {
			@Override
			public void onTicks(ArrayList<Tick> ticks) {
				NumberFormat formatter = new DecimalFormat();
				System.out.println("ticks size " + ticks.size());
				if (ticks.size() > 0) {
					System.out.println("last price " + ticks.get(0).getLastTradedPrice());
					System.out.println("open interest " + formatter.format(ticks.get(0).getOi()));
					System.out.println("day high OI " + formatter.format(ticks.get(0).getOpenInterestDayHigh()));
					System.out.println("day low OI " + formatter.format(ticks.get(0).getOpenInterestDayLow()));
					System.out.println("change " + formatter.format(ticks.get(0).getChange()));
					System.out.println("tick timestamp " + ticks.get(0).getTickTimestamp());
					System.out.println("tick timestamp date " + ticks.get(0).getTickTimestamp());
					System.out.println("last traded time " + ticks.get(0).getLastTradedTime());
					System.out.println(ticks.get(0).getMarketDepth().get("buy").size());
				}
			}
		});
		// Make sure this is called before calling connect.
		tickerProvider.setTryReconnection(true);
		// maximum retries and should be greater than 0
		tickerProvider.setMaximumRetries(10);
		// set maximum retry interval in seconds
		tickerProvider.setMaximumRetryInterval(30);

		/**
		 * connects to com.zerodhatech.com.zerodhatech.ticker server for getting
		 * live quotes
		 */
		tickerProvider.connect();

		/**
		 * You can check, if websocket connection is open or not using the
		 * following method.
		 */
		boolean isConnected = tickerProvider.isConnectionOpen();
		System.out.println(isConnected);

		/**
		 * set mode is used to set mode in which you need tick for list of
		 * tokens. Ticker allows three modes, modeFull, modeQuote, modeLTP. For
		 * getting only last traded price, use modeLTP For getting last traded
		 * price, last traded quantity, average price, volume traded today,
		 * total sell quantity and total buy quantity, open, high, low, close,
		 * change, use modeQuote For getting all data with depth, use modeFull
		 */
		tickerProvider.setMode(tokens, tickerProvider.marketData);

		// tickerProvider.subscribe(tokens);

		// Unsubscribe for a token.
		tickerProvider.unsubscribe(tokens);

		// After using com.zerodhatech.com.zerodhatech.ticker, close websocket
		// connection.
		tickerProvider.disconnect();
	}

}
