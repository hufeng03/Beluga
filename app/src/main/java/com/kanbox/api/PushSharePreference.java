package com.kanbox.api;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Save Data To SharePreference Or Get Data from SharePreference
 * 
 * @author liweilin
 * 
 */
public class PushSharePreference {
	
	private Context ctx;
	private String  fileName;
	

	public PushSharePreference(Context ctx, String fileName) {
		this.ctx = ctx;
		this.fileName = fileName;
	}

    public void clear() {
        SharedPreferences sharePre = ctx.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharePre.edit();
        editor.clear();
        editor.commit();
    }

	/**
	 * Set int value into SharePreference
	 * 
	 * @param fileName
	 * @param key
	 * @param value
	 */
	public void saveIntValueToSharePreferences(String key, int value) {
		SharedPreferences sharePre = ctx.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharePre.edit();
		editor.putInt(key, value);
		editor.commit();
	}
	
	/**
	 * Set long value into SharePreference
	 * 
	 * @param key
	 * @param value
	 */
	public void saveLongValueToSharePreferences(String key, long value) {
		SharedPreferences sharePre = ctx.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharePre.edit();
		editor.putLong(key, value);
		editor.commit();
	}

	/**
	 * Set String value into SharePreference
	 * 
	 * @param fileName
	 * @param key
	 * @param value
	 */
	public void saveStringValueToSharePreferences(String key, String value) {
		SharedPreferences sharePre = ctx.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharePre.edit();
		editor.putString(key, value);
		editor.commit();
	}

	/**
	 * Set Boolean value into SharePreference
	 * 
	 * @param fileName
	 * @param key
	 * @param value
	 */
	public void saveBooleanValueToSharePreferences(String key, boolean value) {
		SharedPreferences sharePre = ctx.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharePre.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	/**
	 * Remove key from SharePreference
	 * 
	 * @param fileName
	 * @param key
	 */
	public void removeSharePreferences(String key) {
		SharedPreferences sharePre = ctx.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharePre.edit();
		editor.remove(key);
		editor.commit();
	}

	/**
	 * 是否包含此字段
	 * 
	 * @param fileName
	 * @param key
	 * @return
	 */
	public boolean contains(String key) {
		SharedPreferences sharePre = ctx.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		return sharePre.contains(key);
	}

	/**
	 * Get Integer Value
	 * 
	 * @param fileName
	 * @param key
	 * @return
	 */
	public Integer getIntValueByKey(String key) {
		SharedPreferences sharePre = ctx.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		return sharePre.getInt(key, -1);
	}

	public Long getLongValueByKey(String key) {
		SharedPreferences sharePre = ctx.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		return sharePre.getLong(key, 0);
	}
	
	/**
	 * Get String Value
	 * 
	 * @param fileName
	 * @param key
	 * @return
	 */
	public String getStringValueByKey(String key) {
		SharedPreferences sharePre = ctx.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		return sharePre.getString(key, null);
	}

	public Boolean getBooleanValueByKey(String key) {
		return getBooleanValueByKey(key, false);
	}
	
	public Boolean getBooleanValueByKey(String key, boolean defaultValue) {
		SharedPreferences sharePre = ctx.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		return sharePre.getBoolean(key, defaultValue);
	}

	public void clearSharePreferences() {
		SharedPreferences sharePre = ctx.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharePre.edit();
		editor.clear();
		editor.commit();
	}


	
	
}
