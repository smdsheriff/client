package com.straders.algo.client.ticker.operation;

/**
 * Created by sujith on 11/21/17.
 */
public interface OnError {

	public void onError(Exception exception);

	void onError(String error);
}
