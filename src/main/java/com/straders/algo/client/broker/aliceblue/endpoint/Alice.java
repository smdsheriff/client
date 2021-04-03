package com.straders.algo.client.broker.aliceblue.endpoint;

import com.straders.algo.client.broker.aliceblue.service.AliceParameters;
import com.straders.algo.client.instrument.Instrument;

public class Alice extends AliceParameters {

	public Instrument instrumentService = new Instrument();

	public String appId = "bUYmXzxNi0";

	public String sceretKey = "wfQMORvH8niowrJ2SJChGhMSltVntHTqVTmb73m3SPU68bQoIDwdhDjcSnCmvi5D";

	public String base = "https://ant.aliceblueonline.com";

	public String profile = base + "/api/v2/profile";

	public String order = base + "/api/v2/order";

	public String accessToken = base + "oauth2/auth?response_type=code&state=test_state&client_id=" + appId
			+ "&redirect_uri=" + base + "/plugin/callback";

	public String cashposition = base + "/api/v2/cashposition";

	public String holdings = base + "/api/v2/holdings";

	public String cancelOrder = base + "/api/v2/order?oms_order_id=";
	
	public String script = base + "/api/v2/scripinfo?exchange=";

}
