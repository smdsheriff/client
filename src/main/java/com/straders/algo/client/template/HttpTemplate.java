package com.straders.algo.client.template;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import com.straders.service.algobase.utils.DataUtils;

public class HttpTemplate extends DataUtils {

	public String getMethod(String url, String brokerId, String token) {
		HttpClient httpClient = HttpClientBuilder.create().build();
		try {
			HttpGet getRequest = new HttpGet(url);
			getRequest.addHeader("Connection", "keep-alive");
			getRequest.addHeader("client_id", brokerId);
			getRequest.addHeader("authorization", "Bearer " + token);
			getRequest.addHeader("Content-Type", "application/json");
			HttpResponse response = httpClient.execute(getRequest);
			int reponseCode = response.getStatusLine().getStatusCode();
			BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
			String temp = StringUtils.EMPTY;
			StringBuilder reponseText = new StringBuilder();
			while ((temp = br.readLine()) != null) {
				reponseText = reponseText.append(temp);
			}
			System.out.println(reponseText);
			return String.valueOf(reponseText);
		} catch (Exception exception) {
			exception.printStackTrace();
			return StringUtils.EMPTY;
		} finally {
			HttpClientUtils.closeQuietly(httpClient);
		}
	}

	public String postMethod(String url, String brokerId, String token, HttpEntity entity) {
		HttpClient httpClient = HttpClientBuilder.create().build();
		try {
			HttpPost postRequest = new HttpPost(url);
			postRequest.addHeader("client_id", brokerId);
			postRequest.addHeader("authorization", "Bearer " + token);
			postRequest.addHeader("Content-Type", "application/json");
			postRequest.setEntity(entity);
			HttpResponse response = httpClient.execute(postRequest);
			int reponseCode = response.getStatusLine().getStatusCode();
			Header contentType = response.getEntity().getContentType();
			if (reponseCode == 404 || reponseCode == 403) {
				System.out.println("Data not Retrieved : " + String.valueOf(reponseCode));
				return StringUtils.EMPTY;
			} else {
				BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
				String temp = StringUtils.EMPTY;
				StringBuilder reponseText = new StringBuilder();
				while ((temp = br.readLine()) != null) {
					reponseText = reponseText.append(temp);
				}
				return String.valueOf(reponseText);
			}
		} catch (Exception exception) {
			exception.printStackTrace();
			return StringUtils.EMPTY;
		} finally {
			HttpClientUtils.closeQuietly(httpClient);
		}
	}

	public String putMethod(String url, String brokerId, String token, HttpEntity entity) {
		HttpClient httpClient = HttpClientBuilder.create().build();
		try {
			HttpPut putRequest = new HttpPut(url);
			putRequest.addHeader("client_id", brokerId);
			putRequest.addHeader("authorization", "Bearer " + token);
			putRequest.addHeader("Content-Type", "application/json");
			putRequest.setEntity(entity);
			HttpResponse response = httpClient.execute(putRequest);
			int reponseCode = response.getStatusLine().getStatusCode();
			if (reponseCode == 404 || reponseCode == 401) {
				System.out.println("Data not Retrieved : " + String.valueOf(reponseCode));
				return StringUtils.EMPTY;
			} else if (!((reponseCode >= 200) && (reponseCode <= 299))) {
				return String.valueOf(response.getStatusLine().getStatusCode());
			}
			BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
			String temp = StringUtils.EMPTY;
			StringBuilder reponseText = new StringBuilder();
			while ((temp = br.readLine()) != null) {
				reponseText = reponseText.append(temp);
			}
			System.out.println(reponseText);
			return String.valueOf(reponseText);
		} catch (Exception exception) {
			exception.printStackTrace();
			return StringUtils.EMPTY;
		} finally {
			HttpClientUtils.closeQuietly(httpClient);
		}
	}

	public String deleteMethod(String url, String brokerId, String token) {
		HttpClient httpClient = HttpClientBuilder.create().build();
		try {
			HttpDelete deleteRequest = new HttpDelete(url);
			deleteRequest.addHeader("client_id", brokerId);
			deleteRequest.addHeader("authorization", "Bearer " + token);
			deleteRequest.addHeader("Content-Type", "application/json");
			HttpResponse response = httpClient.execute(deleteRequest);
			int reponseCode = response.getStatusLine().getStatusCode();
			if (reponseCode == 404 || reponseCode == 401) {
				System.out.println("Data not Retrieved : " + String.valueOf(reponseCode));
				return StringUtils.EMPTY;
			} else if (!((reponseCode >= 200) && (reponseCode <= 299))) {
				return String.valueOf(response.getStatusLine().getStatusCode());
			}
			BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
			String temp = StringUtils.EMPTY;
			StringBuilder reponseText = new StringBuilder();
			while ((temp = br.readLine()) != null) {
				reponseText = reponseText.append(temp);
			}
			System.out.println(reponseText);
			return String.valueOf(reponseText);
		} catch (Exception exception) {
			exception.printStackTrace();
			return StringUtils.EMPTY;
		} finally {
			HttpClientUtils.closeQuietly(httpClient);
		}
	}
}
