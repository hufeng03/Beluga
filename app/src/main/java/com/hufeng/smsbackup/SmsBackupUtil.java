package com.hufeng.smsbackup;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmsBackupUtil {
	
	static long getDateFromFileName(String name)
	{
		long date = -1;
		Pattern pattern = Pattern.compile("sms_backup_\\d{1,15}.xml");
		Matcher match = pattern.matcher(name);
		if(match.find())
		{
			int s = "sms_backup_".length();
			int e = name.length()-4;
			String d = name.substring(s, e);
			return Long.parseLong(d);
		}
		return -1;
	}

}
