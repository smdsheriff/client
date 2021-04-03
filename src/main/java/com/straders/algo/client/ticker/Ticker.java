package com.straders.algo.client.ticker;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.straders.algo.client.ticker.models.Depth;
import com.straders.algo.client.ticker.models.Tick;
import com.straders.algo.client.ticker.operation.OnConnect;
import com.straders.algo.client.ticker.operation.OnDisconnect;
import com.straders.algo.client.ticker.operation.OnError;
import com.straders.algo.client.ticker.operation.OnTicks;

public class Ticker {

	private String wsuri;
	private OnTicks onTickerArrivalListener;
	private OnConnect onConnectedListener;
	private OnDisconnect onDisconnectedListener;
	private OnError onErrorListener;
	private WebSocket ws;

	public final int NseCM = 1, NseFO = 2, NseCD = 3, BseCM = 4, BseFO = 5, BseCD = 6, McxFO = 7, McxSX = 8,
			Indices = 9;

	private final String mSubscribe = "subscribe", mUnSubscribe = "unsubscribe", mSetMode = "mode";

	public static String marketData = "marketdata", compactMarketData = "compact_marketdata", snapQuote = "snapquote";

	private long lastPongAt = 0;
	private Set<Long> subscribedTokens = new HashSet<>();
	private int maxRetries = 10;
	private int count = 0;
	private Timer timer = null;
	private boolean tryReconnection = false;
	private final int pingInterval = 2500;
	private final int pongCheckInterval = 2500;
	private int nextReconnectInterval = 0;
	private int maxRetryInterval = 30000;
	private Map<Long, String> modeMap;
	private Timer canReconnectTimer = null;
	/** Used to reconnect after the specified delay. */
	private boolean canReconnect = true;

	public Ticker(String accessToken) {

		if (wsuri == null) {
			createUrl(accessToken);
		}

		try {
			ws = new WebSocketFactory().createSocket(wsuri);
		} catch (IOException e) {
			if (onErrorListener != null) {
				onErrorListener.onError(e);
			}
			return;
		}
		ws.addListener(getWebsocketAdapter());
		modeMap = new HashMap<>();
	}

	private TimerTask getTask() {
		TimerTask checkForRestartTask = new TimerTask() {
			@Override
			public void run() {
				if (lastPongAt == 0)
					return;

				Date currentDate = new Date();
				long timeInterval = (currentDate.getTime() - lastPongAt);
				if (timeInterval >= 2 * pingInterval) {
					doReconnect();
				}
			}
		};
		return checkForRestartTask;
	}

	public void doReconnect() {
		if (!tryReconnection)
			return;

		if (nextReconnectInterval == 0) {
			nextReconnectInterval = (int) (2000 * Math.pow(2, count));
		} else {
			nextReconnectInterval = (int) (nextReconnectInterval * Math.pow(2, count));
		}

		if (nextReconnectInterval > maxRetryInterval) {
			nextReconnectInterval = maxRetryInterval;
		}
		if (count <= maxRetries) {
			if (canReconnect) {
				count++;
				reconnect(new ArrayList<>(subscribedTokens));
				canReconnect = false;
				canReconnectTimer = new Timer();
				canReconnectTimer.schedule(new TimerTask() {
					@Override
					public void run() {
						canReconnect = true;
					}
				}, nextReconnectInterval);
			}
		} else if (count > maxRetries) {
			// if number of tries exceeds maximum number of retries then stop
			// timer.
			if (timer != null) {
				timer.cancel();
				timer = null;
			}
		}
	}

	public void setTryReconnection(boolean retry) {
		tryReconnection = retry;
	}

	public void setOnErrorListener(OnError listener) {
		onErrorListener = listener;
	}

	public void setMaximumRetries(int maxRetries) throws Exception {
		if (maxRetries > 0) {
			this.maxRetries = maxRetries;
		} else {
			throw new Exception("Maximum retries can't be less than 0");
		}
	}

	/* Set a maximum interval for every retry. */
	public void setMaximumRetryInterval(int interval) throws Exception {
		if (interval >= 5) {
			// convert to milliseconds
			maxRetryInterval = interval * 1000;
		} else {
			throw new Exception("Maximum retry interval can't be less than 0");
		}
	}

	/** Creates url for websocket connection. */
	private void createUrl(String accessToken) {
		// wsuri =
		// "wss://ant.aliceblueonline.com/hydrasocket/v2/websocket?access_token="
		// + accessToken;
		wsuri = "wss://ant.aliceblueonline.com/socket/websocket?token=" + accessToken;
	}

	/**
	 * Set listener for listening to ticks.
	 * 
	 * @param onTickerArrivalListener
	 *            is listener which listens for each tick.
	 */
	public void setOnTickerArrivalListener(OnTicks onTickerArrivalListener) {
		this.onTickerArrivalListener = onTickerArrivalListener;
	}

	/**
	 * Set listener for on connection established.
	 * 
	 * @param listener
	 *            is used to listen to onConnected event.
	 */
	public void setOnConnectedListener(OnConnect listener) {
		onConnectedListener = listener;
	}

	/**
	 * Set listener for on connection is disconnected.
	 * 
	 * @param listener
	 *            is used to listen to onDisconnected event.
	 */
	public void setOnDisconnectedListener(OnDisconnect listener) {
		onDisconnectedListener = listener;
	}

	/**
	 * Establishes a web socket connection.
	 */
	public void connect() {
		try {
			ws.setPingInterval(pingInterval);
			ws.connect();
		} catch (WebSocketException e) {
			e.printStackTrace();
			if (onErrorListener != null) {
				onErrorListener.onError(e);
			}
			if (tryReconnection) {
				if (timer == null) {
					// this is to handle reconnection first time
					if (lastPongAt == 0) {
						lastPongAt = 1;
					}
					timer = new Timer();
					timer.scheduleAtFixedRate(getTask(), 0, pongCheckInterval);
				}
			}
		}
	}

	/** Returns a WebSocketAdapter to listen to ticker related events. */
	public WebSocketAdapter getWebsocketAdapter() {
		return new WebSocketAdapter() {

			@Override
			public void onConnected(WebSocket websocket, Map<String, List<String>> headers) {
				count = 0;
				nextReconnectInterval = 0;

				if (onConnectedListener != null) {
					onConnectedListener.onConnected();
				}

				if (tryReconnection) {
					if (timer != null) {
						timer.cancel();
					}
					timer = new Timer();
					timer.scheduleAtFixedRate(getTask(), 0, pongCheckInterval);

				}
			}

			@Override
			public void onTextMessage(WebSocket websocket, String message) {
				System.out.println("Ticket On Message " + message);
				parseTextMessage(message);
			}

			@Override
			public void onBinaryMessage(WebSocket websocket, byte[] binary) {
				try {
					System.out.println("Ticket On Binary Message " + binary);
					super.onBinaryMessage(websocket, binary);
				} catch (Exception e) {
					e.printStackTrace();
					if (onErrorListener != null) {
						onErrorListener.onError(e);
					}
				}

				ArrayList<Tick> tickerData = parseBinary(binary);

				if (onTickerArrivalListener != null) {
					onTickerArrivalListener.onTicks(tickerData);
				}
			}

			@Override
			public void onPongFrame(WebSocket websocket, WebSocketFrame frame) {
				try {
					super.onPongFrame(websocket, frame);
					Date date = new Date();
					lastPongAt = date.getTime();
				} catch (Exception e) {
					e.printStackTrace();
					if (onErrorListener != null) {
						onErrorListener.onError(e);
					}
				}
			}

			/**
			 * On disconnection, return statement ensures that the thread ends.
			 *
			 * @param websocket
			 * @param serverCloseFrame
			 * @param clientCloseFrame
			 * @param closedByServer
			 * @throws Exception
			 */
			@Override
			public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame,
					WebSocketFrame clientCloseFrame, boolean closedByServer) {
				if (onDisconnectedListener != null) {
					onDisconnectedListener.onDisconnected();
				}
				return;
			}

			@Override
			public void onError(WebSocket websocket, WebSocketException cause) {
				try {
					super.onError(websocket, cause);
				} catch (Exception e) {
					e.printStackTrace();
					if (onErrorListener != null) {
						onErrorListener.onError(e);
					}
				}
			}

		};
	}

	/** Disconnects websocket connection. */
	public void disconnect() {
		if (timer != null) {
			timer.cancel();
		}
		if (ws != null && ws.isOpen()) {
			ws.disconnect();
			subscribedTokens = new HashSet<>();
			modeMap.clear();
		}
	}

	/** Disconnects websocket connection only for internal use */
	private void nonUserDisconnect() {
		if (ws != null) {
			ws.disconnect();
		}
	}

	/**
	 * Returns true if websocket connection is open.
	 * 
	 * @return boolean
	 */
	public boolean isConnectionOpen() {
		if (ws != null) {
			if (ws.isOpen()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Setting different modes for an arraylist of tokens.
	 * 
	 * @param tokens
	 *            an arraylist of tokens
	 * @param mode
	 *            the mode that needs to be set. Scroll up to see different kind
	 *            of modes
	 */
	public void setMode(ArrayList<Long> tokens, String mode) {
		JSONObject jobj = new JSONObject();
		try {
			// int a[] = {256265, 408065, 779521, 738561, 177665, 25601};
			JSONArray list = new JSONArray();
			JSONArray listMain = new JSONArray();
			// listMain.put(0, mode);
			for (int i = 0; i < tokens.size(); i++) {
				list.put(1, tokens.get(i));
			}
			listMain.put(1, list);
			jobj.put("a", mSubscribe);
			jobj.put("v", listMain);
			jobj.put("m", mode);
			for (int i = 0; i < tokens.size(); i++) {
				modeMap.put(tokens.get(i), mode);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		if (ws != null) {
			ws.sendText(jobj.toString());
		}
	}

	/**
	 * Subscribes for list of tokens.
	 * 
	 * @param tokens
	 *            is list of tokens to be subscribed for.
	 */
	public void subscribe(ArrayList<Long> tokens) {
		if (ws != null) {
			if (ws.isOpen()) {
				JSONObject object = createTickerJsonObject(tokens, mSubscribe);
				ws.sendText(object.toString());
				subscribedTokens.addAll(tokens);
				for (int i = 0; i < tokens.size(); i++) {
					modeMap.put(tokens.get(i), marketData);
				}
			} else {
				if (onErrorListener != null) {
					System.out.println(new Exception("Ticker is not availanle"));
				}
			}
		} else {
			if (onErrorListener != null) {
				System.out.println(new Exception("Ticker is not availanle"));
			}
		}
	}

	/** Create a JSONObject to send message to server. */
	private JSONObject createTickerJsonObject(ArrayList<Long> tokens, String action) {
		JSONObject jobj = new JSONObject();
		try {
			JSONArray list = new JSONArray();
			for (int i = 0; i < tokens.size(); i++) {
				list.put(1, tokens.get(i));
			}
			jobj.put("v", list);
			jobj.put("a", action);
			jobj.put("m", marketData);
		} catch (JSONException e) {
		}

		return jobj;
	}

	/**
	 * Unsubscribes ticks for list of tokens.
	 * 
	 * @param tokens
	 *            is the list of tokens that needs to be unsubscribed.
	 */
	public void unsubscribe(ArrayList<Long> tokens) {
		if (ws != null) {
			if (ws.isOpen()) {
				ws.sendText(createTickerJsonObject(tokens, mUnSubscribe).toString());
				subscribedTokens.removeAll(tokens);
				for (int i = 0; i < tokens.size(); i++) {
					modeMap.remove(tokens.get(i));
				}
			}
		}
	}

	/*
	 * This method parses binary data got from kite server to get ticks for each
	 * token subscribed. we have to keep a main Array List which is global and
	 * keep deleting element in the list and add new data element in that place
	 * and call notify data set changed.
	 * 
	 * @return List of parsed ticks.
	 */
	private ArrayList<Tick> parseBinary(byte[] binaryPackets) {
		ArrayList<Tick> ticks = new ArrayList<Tick>();
		ArrayList<byte[]> packets = splitPackets(binaryPackets);
		for (int i = 0; i < packets.size(); i++) {
			byte[] bin = packets.get(i);
			byte[] t = Arrays.copyOfRange(bin, 0, 4);
			int x = ByteBuffer.wrap(t).getInt();

			// int token = x >> 8;
			int segment = x & 0xff;

			int dec1 = (segment == NseCD) ? 10000000 : 100;

			if (bin.length == 28 || bin.length == 32) {
				Tick tick = getIndeciesData(bin, x, segment != Indices);
				ticks.add(tick);
			} else if (bin.length == 44) {
				Tick tick = getQuoteData(bin, x, dec1, segment != Indices);
				ticks.add(tick);
			} else if (bin.length == 184) {
				Tick tick = getQuoteData(bin, x, dec1, segment != Indices);
				tick.setMode(marketData);
				ticks.add(getFullData(bin, dec1, tick));
			}
		}
		return ticks;
	}

	/**
	 * Parses NSE indices data.
	 * 
	 * @return Tick is the parsed index data.
	 */
	private Tick getIndeciesData(byte[] bin, int x, boolean tradable) {
		int dec = 100;
		Tick tick = new Tick();
		tick.setMode(marketData);
		tick.setTradable(tradable);
		tick.setInstrumentToken(x);
		tick.setLastTradedPrice(convertToDouble(getBytes(bin, 4, 8)) / dec);
		tick.setHighPrice(convertToDouble(getBytes(bin, 8, 12)) / dec);
		tick.setLowPrice(convertToDouble(getBytes(bin, 12, 16)) / dec);
		tick.setOpenPrice(convertToDouble(getBytes(bin, 16, 20)) / dec);
		tick.setClosePrice(convertToDouble(getBytes(bin, 20, 24)) / dec);
		tick.setNetPriceChangeFromClosingPrice(convertToDouble(getBytes(bin, 24, 28)) / dec);
		if (bin.length > 28) {
			tick.setMode(marketData);
			long tickTimeStamp = convertToLong(getBytes(bin, 28, 32)) * 1000;
			if (isValidDate(tickTimeStamp)) {
				tick.setTickTimestamp(new Date(tickTimeStamp));
			} else {
				tick.setTickTimestamp(null);
			}
		}
		return tick;
	}

	/**
	 * Get quote data (last traded price, last traded quantity, average traded
	 * price, volume, total bid(buy quantity), total ask(sell quantity), open,
	 * high, low, close.)
	 */
	private Tick getQuoteData(byte[] bin, int x, int dec1, boolean tradable) {
		Tick tick2 = new Tick();
		tick2.setMode(marketData);
		tick2.setInstrumentToken(x);
		tick2.setTradable(tradable);
		double lastTradedPrice = convertToDouble(getBytes(bin, 4, 8)) / dec1;
		tick2.setLastTradedPrice(lastTradedPrice);
		tick2.setLastTradedQuantity(convertToDouble(getBytes(bin, 8, 12)));
		tick2.setAverageTradePrice(convertToDouble(getBytes(bin, 12, 16)) / dec1);
		tick2.setVolumeTradedToday(convertToDouble(getBytes(bin, 16, 20)));
		tick2.setTotalBuyQuantity(convertToDouble(getBytes(bin, 20, 24)));
		tick2.setTotalSellQuantity(convertToDouble(getBytes(bin, 24, 28)));
		tick2.setOpenPrice(convertToDouble(getBytes(bin, 28, 32)) / dec1);
		tick2.setHighPrice(convertToDouble(getBytes(bin, 32, 36)) / dec1);
		tick2.setLowPrice(convertToDouble(getBytes(bin, 36, 40)) / dec1);
		double closePrice = convertToDouble(getBytes(bin, 40, 44)) / dec1;
		tick2.setClosePrice(closePrice);
		setChangeForTick(tick2, lastTradedPrice, closePrice);
		return tick2;
	}

	private void setChangeForTick(Tick tick, double lastTradedPrice, double closePrice) {
		if (closePrice != 0)
			tick.setNetPriceChangeFromClosingPrice((lastTradedPrice - closePrice) * 100 / closePrice);
		else
			tick.setNetPriceChangeFromClosingPrice(0);

	}

	/** Parses full mode data. */
	private Tick getFullData(byte[] bin, int dec, Tick tick) {
		long lastTradedtime = convertToLong(getBytes(bin, 44, 48)) * 1000;
		if (isValidDate(lastTradedtime)) {
			tick.setLastTradedTime(new Date(lastTradedtime));
		} else {
			tick.setLastTradedTime(null);
		}
		tick.setOi(convertToDouble(getBytes(bin, 48, 52)));
		tick.setOpenInterestDayHigh(convertToDouble(getBytes(bin, 52, 56)));
		tick.setOpenInterestDayLow(convertToDouble(getBytes(bin, 56, 60)));
		long tickTimeStamp = convertToLong(getBytes(bin, 60, 64)) * 1000;
		if (isValidDate(tickTimeStamp)) {
			tick.setTickTimestamp(new Date(tickTimeStamp));
		} else {
			tick.setTickTimestamp(null);
		}
		tick.setMarketDepth(getDepthData(bin, dec, 64, 184));
		return tick;
	}

	/** Reads all bytes and returns map of depth values for offer and bid */
	private Map<String, ArrayList<Depth>> getDepthData(byte[] bin, int dec, int start, int end) {
		byte[] depthBytes = getBytes(bin, start, end);
		int s = 0;
		ArrayList<Depth> buy = new ArrayList<Depth>();
		ArrayList<Depth> sell = new ArrayList<Depth>();
		for (int k = 0; k < 10; k++) {
			s = k * 12;
			Depth depth = new Depth();
			depth.setQuantity((int) convertToDouble(getBytes(depthBytes, s, s + 4)));
			depth.setPrice(convertToDouble(getBytes(depthBytes, s + 4, s + 8)) / dec);
			depth.setOrders((int) convertToDouble(getBytes(depthBytes, s + 8, s + 10)));

			if (k < 5) {
				buy.add(depth);
			} else {
				sell.add(depth);
			}
		}
		Map<String, ArrayList<Depth>> depthMap = new HashMap<String, ArrayList<Depth>>();
		depthMap.put("buy", buy);
		depthMap.put("sell", sell);
		return depthMap;
	}

	/**
	 * Each byte stream contains many packets. This method reads first two bits
	 * and calculates number of packets in the byte stream and split it.
	 */
	private ArrayList<byte[]> splitPackets(byte[] bin) {

		ArrayList<byte[]> packets = new ArrayList<byte[]>();
		int noOfPackets = getLengthFromByteArray(getBytes(bin, 0, 2)); // in.read(bin,
																		// 0,
																		// 2);
		int j = 2;

		for (int i = 0; i < noOfPackets; i++) {
			int sizeOfPacket = getLengthFromByteArray(getBytes(bin, j, j + 2));// in.read(bin,
																				// j,
																				// j+2);
			byte[] packet = Arrays.copyOfRange(bin, j + 2, j + 2 + sizeOfPacket);
			packets.add(packet);
			j = j + 2 + sizeOfPacket;
		}
		return packets;
	}

	/** Reads values of specified position in byte array. */
	private byte[] getBytes(byte[] bin, int start, int end) {
		return Arrays.copyOfRange(bin, start, end);
	}

	/** Convert binary data to double datatype */
	private double convertToDouble(byte[] bin) {
		ByteBuffer bb = ByteBuffer.wrap(bin);
		bb.order(ByteOrder.BIG_ENDIAN);
		if (bin.length < 4)
			return bb.getShort();
		else if (bin.length < 8)
			return bb.getInt();
		else
			return bb.getDouble();
	}

	/* Convert binary data to long datatype */
	private long convertToLong(byte[] bin) {
		ByteBuffer bb = ByteBuffer.wrap(bin);
		bb.order(ByteOrder.BIG_ENDIAN);
		return bb.getInt();
	}

	/** Returns length of packet by reading byte array values. */
	private int getLengthFromByteArray(byte[] bin) {
		ByteBuffer bb = ByteBuffer.wrap(bin);
		bb.order(ByteOrder.BIG_ENDIAN);
		return bb.getShort();
	}

	/** Disconnects and reconnects com.zerodhatech.ticker. */
	private void reconnect(final ArrayList<Long> tokens) {
		nonUserDisconnect();
		try {
			ws = new WebSocketFactory().createSocket(wsuri);
		} catch (IOException e) {
			if (onErrorListener != null) {
				onErrorListener.onError(e);
			}
			return;
		}
		ws.addListener(getWebsocketAdapter());
		connect();
		final OnConnect onUsersConnectedListener = this.onConnectedListener;
		setOnConnectedListener(new OnConnect() {
			@Override
			public void onConnected() {
				if (subscribedTokens.size() > 0) {
					// take a backup of mode map as it will be overriden to
					// modeQuote after subscribe
					Map<Long, String> backupModeMap = new HashMap<>();
					backupModeMap.putAll(modeMap);
					ArrayList<Long> tokens = new ArrayList<>();
					tokens.addAll(subscribedTokens);
					subscribe(tokens);

					Map<String, ArrayList<Long>> modes = new HashMap<>();
					for (Map.Entry<Long, String> item : backupModeMap.entrySet()) {
						if (!modes.containsKey(item.getValue())) {
							modes.put(item.getValue(), new ArrayList<Long>());
						}
						modes.get(item.getValue()).add(item.getKey());
					}
					for (Map.Entry<String, ArrayList<Long>> modeArrayItem : modes.entrySet()) {
						setMode(modeArrayItem.getValue(), modeArrayItem.getKey());
					}
				}
				lastPongAt = 0;
				count = 0;
				nextReconnectInterval = 0;
				onConnectedListener = onUsersConnectedListener;
			}
		});
	}

	private boolean isValidDate(long date) {
		if (date <= 0) {
			return false;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setLenient(false);
		calendar.setTimeInMillis(date);
		try {
			calendar.getTime();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/** Parses incoming text message. */
	private void parseTextMessage(String message) {
		JSONObject data;
		try {
			data = new JSONObject(message);
			if (!data.has("type")) {
				return;
			}

			String type = data.getString("type");
			if (type.equals("order")) {
			}

			if (type.equals("error")) {
				if (onErrorListener != null) {
					onErrorListener.onError(data.getString("data"));
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}