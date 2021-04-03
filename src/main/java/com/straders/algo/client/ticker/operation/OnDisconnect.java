package com.straders.algo.client.ticker.operation;

/**
 * Callback to listen to com.zerodhatech.ticker websocket disconnected event.
 */
public interface OnDisconnect {
    void onDisconnected();
}
