package com.hufeng.backup;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hufeng.filemanager.utils.LogUtil;
import com.hufeng.filemanager.utils.TimeUtil;

import android.os.Environment;

public class BackupUtil {
	private static final String LOG_TAG = BackupUtil.class.getSimpleName();
	
	public static final int MESSAGE_DELETE_ONE_HISTORY_ITEM = 1;
	
	public static String CONTACT_BACKUP_DIRECTORY = Environment.getExternalStorageDirectory()+File.separator+"filemanager"+File.separator+"contact_backup";
	
	public static String SMS_BACKUP_DIRECTORY = Environment.getExternalStorageDirectory()+File.separator+"filemanager"+File.separator+"sms_backup";
	
	public static long getBackupDateFromFileName(String name)
	{
		if(name.endsWith(".vcf"))
		{
			return getContactBackupDateFromFileName(name);
		}
		else
		{
			return getSmsBackupDateFromFileName(name);
		}
	}
	
	public static long getContactBackupDateFromFileName(String name)
	{
		Pattern pattern = Pattern.compile("contact_backup_\\d{1,15}.vcf");
		Matcher match = pattern.matcher(name);
		if(match.find())
		{
			int s = "contact_backup_".length();
			int e = name.length()-4;
			String d = name.substring(s, e);
			LogUtil.i(LOG_TAG, "parse long : "+d);
			return Long.parseLong(d);
		}
		return -1;
	}
	
	public static long getSmsBackupDateFromFileName(String name)
	{
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
	
	public static int getSmsBackupCount()
	{
		File file = new File(SMS_BACKUP_DIRECTORY);
		String[] files = file.list();
		int count = 0;
		if(files!=null && files.length>0)
        {
			for(String tmp:files)
			{
				long date = getSmsBackupDateFromFileName(tmp);
				if(date!=-1)
				{
					count++;
				}
			}
        }
		return count;
	}
	
	public static String getLastSmsBackupDate()
	{
		File file = new File(SMS_BACKUP_DIRECTORY);
		String[] files = file.list();
		long lastest = -1;
		if(files!=null && files.length>0)
        {
			for(String tmp:files)
			{
				long date = getSmsBackupDateFromFileName(tmp);
				if(date!=-1)
				{
					if(date>lastest)
						lastest = date;
				}
			}
        }
		if(lastest!=-1)
		{
			return TimeUtil.getRegularDateTimeStr(lastest);
		}
		else
		{
			return null;
		}
	}
	
	public static int getContactBackupCount()
	{
		File file = new File(CONTACT_BACKUP_DIRECTORY);
		String[] files = file.list();
		int count = 0;
		if(files!=null && files.length>0)
        {
			for(String tmp:files)
			{
				long date = getContactBackupDateFromFileName(tmp);
				if(date!=-1)
				{
					count++;
				}
			}
        }
		return count;
	}
	
	public static String getLastContactBackupDate()
	{
		File file = new File(CONTACT_BACKUP_DIRECTORY);
		String[] files = file.list();
		long lastest = -1;
		if(files!=null && files.length>0)
        {
			for(String tmp:files)
			{
				long date = getContactBackupDateFromFileName(tmp);
				if(date!=-1)
				{
					if(date>lastest)
						lastest = date;
				}
			}
        }
		if(lastest!=-1)
		{
			return TimeUtil.getRegularDateTimeStr(lastest);
		}
		else
		{
			return null;
		}
	}

}
