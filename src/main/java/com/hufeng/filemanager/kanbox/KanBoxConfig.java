package com.hufeng.filemanager.kanbox;

import com.hufeng.filemanager.Constants;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by feng on 13-11-21.
 */
public class KanBoxConfig {

    public static final String KANBOX_PAKCAGE_NAME = "com.kanbox.wp";

//    public static final String KANBOX_APK_DOWNLOAD_URL = "http://download.kanbox.com/kcn/android/kanbox_21021.apk";

//    public static final String CLIENT_ID = "ff303a509f97842dabcea6ff90b565b0";		//filemanager client_id
//    public static final String CLIENT_SECRET = "9b9ac155a6fbc11288c84d01e5a404b2";	//filemanager client_secret

//    public static final String CLIENT_ID = "82afd34093ff568d08db2ce9f1b4c09e";		//kanbox sdk client_id
//    public static final String CLIENT_SECRET = "af2d1b0f028284feabd30507a75a6ced";	//kanbox sdk client_secret

    public static final String LOCAL_STORAGE_DIRECTORY = "KanBox";

    public static final String OAUTH_URL ="https://auth.kanbox.com/0/auth";
    public static final String GET_AUTH_REDIRECT_URI = "https://www.kanbox.com";		//重定向url，可自行修改
    public static final String GET_TOKEN_REDIRECT_URI = "https://www.kanbox.com";		//重定向url，可自行修改


    public static final String getOAuthUrl() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("response_type","code");
        params.put("client_id", Constants.CLIENT_ID);
        params.put("platform","android");
        params.put("redirect_uri", "kanbox:oauth:success");
        params.put("user_language", "EN");
//        return appendUrl(OAUTH_URL, params);
        return null;
    }

    public static final String appendUrl(String url, HashMap<String,String> keys) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(url);
        if( keys!=null && keys.size()>0 ) {
            if (!url.endsWith("?")) {
                buffer.append('?');
            }
            boolean first = true;
            Iterator<String> iterator = keys.keySet().iterator();
            while(iterator.hasNext()) {
                String key = iterator.next();
                String value = keys.get(key);
                if(first) {
                    buffer.append(key).append(value);
                    first = false;
                } else {
                    buffer.append('&').append(key).append(value);
                }
            }
        }
        return buffer.toString();
    }
}
