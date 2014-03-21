package com.kanbox.api;

import org.apache.http.client.methods.HttpRequestBase;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

public class Token {
	private String acceccToken;
	private String refreshToken;
	private long expires;
	private static Token mToken;
    private long expiresTill;
	
	private Token() {
	}
	
	public static final Token getInstance() {
		if(mToken == null) {
			mToken = new Token();
		}
		return mToken;
	}

    public static void clear(){
        mToken = null;
    }
	
	/**
	 * 解析token内容
	 * @param response
	 * @throws org.json.JSONException
	 */
	public void parseToken(String response) throws JSONException {
		JSONObject sData = new JSONObject(response);
		acceccToken = sData.getString("access_token");
		expires = sData.getLong("expires_in");
		refreshToken = sData.getString("refresh_token");
        expiresTill = System.currentTimeMillis()+expires*2000/3;
	}

//	public void setToken(String acceccToken, String refreshToken, long expires) {
//		this.acceccToken = acceccToken;
//		this.refreshToken = refreshToken;
//		this.expires = expires;
//        this.expiresTill = System.currentTimeMillis()+expires*2000/3;
//	}

	/**
	 * 用code换取token
	 * @param clientId
	 * @param clientSecret
	 * @param code
	 * @param redirectUrl
	 * @param listener
	 * @throws java.io.UnsupportedEncodingException
	 */
	public void getToken(String clientId, String clientSecret, String code, String redirectUrl, RequestListener listener) throws UnsupportedEncodingException {
		String getTokenUrl = "https://auth.kanbox.com/0/token";
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("grant_type", "authorization_code");
		params.put("client_id", clientId);
		params.put("client_secret", clientSecret);
		params.put("code", code);
		params.put("redirect_uri", redirectUrl);

		HttpRequestBase httpMethod = KanboxHttp.doPost(getTokenUrl, params);
		new KanboxAsyncTask(null, null, httpMethod, listener, RequestListener.OP_GET_TOKEN).execute();
	}

	/**
	 * 刷新access_token
	 * @param clientId
	 * @param clientSecret
	 * @param listener
	 * @throws java.io.UnsupportedEncodingException
	 */
	public void refreshToken(String clientId, String clientSecret, RequestListener listener) throws UnsupportedEncodingException {
		String refreshTokenUrl = "https://auth.kanbox.com/0/token";
		
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("grant_type", "refresh_token");
		params.put("client_id", clientId);
		params.put("client_secret", clientSecret);
		params.put("refresh_token", refreshToken);
		
		HttpRequestBase httpMethod = KanboxHttp.doPost(refreshTokenUrl, params);
		new KanboxAsyncTask(null, null, httpMethod, listener, RequestListener.OP_REFRESH_TOKEN).execute();
	}


    public long getExpiresTill() {
        return expiresTill;
    }

	public String getAcceccToken() {
		return acceccToken;
	}

	public void setAcceccToken(String acceccToken) {
		this.acceccToken = acceccToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public long getExpires() {
		return expires;
	}

	public void setExpires(long expires) {
		this.expires = expires;
	}

    public void setExpiresTill(long expires) {
        this.expiresTill = expires;
    }
	
	
}
