package com.hufeng.filemanager.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

public class MultiSimUtil {
	
	public static boolean isMultiSim_Spreadtrum_Eton()
	{
    	boolean value = false;
    	try {
			Class<?> classType = Class.forName("com.android.internal.telephony.PhoneFactory");
			try {
				Method method;
				try {
					method = classType.getDeclaredMethod("isMultiSim", (Class[])null);
					Object obj = method.invoke(classType, (Object[])null);
					value = (Boolean)obj;
				} catch (NoSuchMethodException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return value;
	}
	
	public static boolean setActualDefaultRingtoneUri_Spreadtrum_Eton(Context context, int type, Uri uri, int simid)
	{
    	try {
			Class<?> classType = Class.forName("android.media.RingtoneManager");
			try {
				Method method;
				try {
					method = classType.getDeclaredMethod("setActualDefaultRingtoneUri", new Class[]{Context.class, int.class,Uri.class,int.class});
					/*Object obj = */method.invoke(classType, new Object[]{context, type, uri, simid});
					return true;
				} catch (NoSuchMethodException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return false;
	}

}
