package com.kanbox.api;

import android.text.TextUtils;
import android.util.Log;

import com.hufeng.filemanager.Constants;
import com.hufeng.filemanager.FileManager;
import com.hufeng.filemanager.kanbox.KanBoxApi;
import com.hufeng.filemanager.kanbox.KanBoxConfig;
import com.hufeng.filemanager.utils.TimeUtil;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

public class Token {

    private static final String TAG = "kb_token";

	private String acceccToken;
	private String refreshToken;
	private long expires;
	private static Token mToken;
    private long expiresTill;

    private String appCode;
	
	private Token() {
        PushSharePreference sPreference = new PushSharePreference(FileManager.getAppContext(), KanBoxApi.PUSH_SHAREPREFERENCE_NAME);
        expiresTill = sPreference.getLongValueByKey("expriesTill");
        acceccToken = sPreference.getStringValueByKey("accecc_token");
        refreshToken = sPreference.getStringValueByKey("refresh_token");
        expires = sPreference.getLongValueByKey("expries");
    }

    public String getAccessToken() {
        return acceccToken;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() < expiresTill;
    }

    public void setCode(String code) {
        appCode = code;
    }

    synchronized public static final Token getInstance() {
		if(mToken == null) {
			mToken = new Token();

		}
		return mToken;
	}

    synchronized public void clear(){
        mToken = null;
    }

    synchronized public String refreshTokenIfExpired(HttpClient httpClient) {
        if (TextUtils.isEmpty(refreshToken) || System.currentTimeMillis() > expiresTill) {
            return refreshToken(httpClient);
        } else {
            return null;
        }
    }

    synchronized public String getToken(HttpClient httpClient) {
        if (!TextUtils.isEmpty(acceccToken)) {
            return "success";
        }
        String token_result = null;
        String getTokenUrl = "https://auth.kanbox.com/0/token";
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("grant_type", "authorization_code");
        params.put("client_id", Constants.CLIENT_ID);
        params.put("client_secret", Constants.CLIENT_SECRET);
        params.put("code", appCode);
        params.put("redirect_uri", KanBoxConfig.GET_TOKEN_REDIRECT_URI);

        HttpRequestBase httpMethod = null;
        try {
            httpMethod = KanboxHttp.doPost("https://auth.kanbox.com/0/token", params);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        HttpResponse sHttpResponse = null;
        try {
            sHttpResponse = httpClient.execute(httpMethod);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int statusCode = sHttpResponse.getStatusLine().getStatusCode();
        if (statusCode == 200) {
            try {
                token_result = EntityUtils.toString(sHttpResponse.getEntity());
                try {
                    parseToken(token_result);
                    saveToken();
                } catch (JSONException e) {
                    token_result = "error";
                }
            } catch (IOException e) {
                e.printStackTrace();
                token_result = "error";
            }
        } else {
            token_result = "error";
        }
        return token_result;
    }

    synchronized public String refreshToken(HttpClient httpClient) {
        if (!TextUtils.isEmpty(acceccToken) && expiresTill > System.currentTimeMillis()) {
            return "success";
        }
        String token_result = null;
            HashMap<String, String> token_refresh_params = new HashMap<String, String>();
            token_refresh_params.put("grant_type", "refresh_token");
            token_refresh_params.put("client_id", Constants.CLIENT_ID);
            token_refresh_params.put("client_secret", Constants.CLIENT_SECRET);
            token_refresh_params.put("refresh_token", refreshToken);

            HttpRequestBase httpMethod = null;
            try {
                httpMethod = KanboxHttp.doPost("https://auth.kanbox.com/0/token", token_refresh_params);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            HttpResponse sHttpResponse = null;
            try {
                sHttpResponse = httpClient.execute(httpMethod);
            } catch (IOException e) {
                e.printStackTrace();
            }
            int statusCode = sHttpResponse.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                try {
                    token_result = EntityUtils.toString(sHttpResponse.getEntity());
                    try {
                        parseToken(token_result);
                        saveToken();
                    } catch (JSONException e) {
                        token_result = "error";
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    token_result = "error";
                }
            } else {
                token_result = "error";
            }
        return token_result;
    }

    private void saveToken() {
        PushSharePreference sPreference = new PushSharePreference(FileManager.getAppContext(), KanBoxApi.PUSH_SHAREPREFERENCE_NAME);
        sPreference.saveStringValueToSharePreferences("accecc_token", acceccToken);
        sPreference.saveStringValueToSharePreferences("refresh_token", refreshToken);
        Log.i(TAG, "save token expires " + TimeUtil.getDateString(expiresTill) + "|" + expires);
        sPreference.saveLongValueToSharePreferences("expriesTill", expiresTill);
        sPreference.saveLongValueToSharePreferences("expries", expires);
    }
	
	/**
	 * 解析token内容
	 * @param response
	 * @throws org.json.JSONException
	 */
	private void parseToken(String response) throws JSONException {
		JSONObject sData = new JSONObject(response);
		acceccToken = sData.getString("access_token");
		expires = sData.getLong("expires_in");
		refreshToken = sData.getString("refresh_token");
        expiresTill = System.currentTimeMillis()+expires*1000/2;
	}

//	public void setToken(String acceccToken, String refreshToken, long expires) {
//		this.acceccToken = acceccToken;
//		this.refreshToken = refreshToken;
//		this.expires = expires;
//        this.expiresTill = System.currentTimeMillis()+expires*2000/3;
//	}

	public void getToken(RequestListener listener) throws UnsupportedEncodingException {
//		String getTokenUrl = "https://auth.kanbox.com/0/token";
//		HashMap<String, String> params = new HashMap<String, String>();
//		params.put("grant_type", "authorization_code");
//		params.put("client_id", clientId);
//		params.put("client_secret", clientSecret);
//		params.put("code", code);
//		params.put("redirect_uri", redirectUrl);
//		HttpRequestBase httpMethod = KanboxHttp.doPost(getTokenUrl, params);
		new KanboxAsyncTask(null, null, null, listener, RequestListener.OP_GET_TOKEN, false).execute();
	}

	public void refreshToken(RequestListener listener) throws UnsupportedEncodingException {
//		String refreshTokenUrl = "https://auth.kanbox.com/0/token";
//
//		HashMap<String, String> params = new HashMap<String, String>();
//		params.put("grant_type", "refresh_token");
//		params.put("client_id", clientId);
//		params.put("client_secret", clientSecret);
//		params.put("refresh_token", refreshToken);
//
//		HttpRequestBase httpMethod = KanboxHttp.doPost(refreshTokenUrl, params);
		new KanboxAsyncTask(null, null, null, listener, RequestListener.OP_REFRESH_TOKEN, false).execute();
	}
}
