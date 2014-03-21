package com.kanbox.api;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Map;

public class KanboxHttp {
	
	public static HttpGet doGet(String url) {
		return doGet(url, null, null);
	}
	
	@SuppressWarnings("rawtypes")
	public static HttpGet doGet(String url, Map params) {
		return doGet(url, params, null);
	}
	
	public static HttpGet doGet(String url, Token token) {
		return doGet(url, null, token);
	}
	
	@SuppressWarnings("rawtypes")
	public static HttpGet doGet(String url, Map params, Token token) {
		if(params != null && params.size() > 0) {
			String paramStr = "";
			Iterator iter = params.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				Object key = entry.getKey();
				Object val = entry.getValue();
				paramStr += paramStr = "&" + key + "=" + val;
			}
	
			if (!paramStr.equals("")) {
				paramStr = paramStr.replaceFirst("&", "?");
				url += paramStr;
			}
		}
		
		HttpGet httpRequest = new HttpGet(url);
		if(token != null) {
			httpRequest.setHeader("Authorization", "Bearer " + token.getAcceccToken());
		}
		return httpRequest;
	}
	
	
	@SuppressWarnings("rawtypes")
	public static HttpPost doPost(String url, Map params) throws UnsupportedEncodingException {
		String paramStr = "";
		
		if(params != null && params.size() > 0) {
			Iterator iter = params.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				Object key = entry.getKey();
				Object val = entry.getValue();
				paramStr += "&" + key + "=" + val;
			}
	
			if (!paramStr.equals("")) {
				paramStr = paramStr.substring(1);
			}
		}
		return doPost(url, paramStr, null);
	}
	
	public static HttpPost doPost(String url, String params) throws UnsupportedEncodingException {
		return doPost(url, params, null);
	}
	
	public static HttpPost doPost(String url, String params, Token token) throws UnsupportedEncodingException {
		HttpPost httpRequest = new HttpPost(url);
		httpRequest.setHeader("Content-Type", "application/x-www-form-urlencoded");
		
		if(params != null && params.length() > 0) {
			httpRequest.setEntity(new StringEntity(params, HTTP.UTF_8));
		}
		
		if(token != null) {
			httpRequest.setHeader("Authorization", "Bearer " + token.getAcceccToken());
		}
		return httpRequest;
	}
	
}
