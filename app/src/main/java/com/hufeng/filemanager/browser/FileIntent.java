package com.hufeng.filemanager.browser;

import java.io.File;

import com.hufeng.filemanager.FileManager;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

public class FileIntent {
	
//    FileManager.setCategoryMatch(".txt", FileUtils.FILE_TYPE_DOCUMENT);
//    FileManager.setCategoryMatch(".epub", FileUtils.FILE_TYPE_DOCUMENT);
//    FileManager.setCategoryMatch(".umd", FileUtils.FILE_TYPE_DOCUMENT);
//    FileManager.setCategoryMatch(".pdf", FileUtils.FILE_TYPE_DOCUMENT);
//    FileManager.setCategoryMatch(".ps", FileUtils.FILE_TYPE_DOCUMENT);
//    FileManager.setCategoryMatch(".doc", FileUtils.FILE_TYPE_DOCUMENT);
//    FileManager.setCategoryMatch(".ppt", FileUtils.FILE_TYPE_DOCUMENT);
//    FileManager.setCategoryMatch(".xls", FileUtils.FILE_TYPE_DOCUMENT);
//    FileManager.setCategoryMatch(".docx", FileUtils.FILE_TYPE_DOCUMENT);
//    FileManager.setCategoryMatch(".pptx", FileUtils.FILE_TYPE_DOCUMENT);
//    FileManager.setCategoryMatch(".xlsx", FileUtils.FILE_TYPE_DOCUMENT);
//    FileManager.setCategoryMatch(".html", FileUtils.FILE_TYPE_DOCUMENT);
//    FileManager.setCategoryMatch(".htm", FileUtils.FILE_TYPE_DOCUMENT);
//    FileManager.setCategoryMatch(".xhtml", FileUtils.FILE_TYPE_DOCUMENT);
//    FileManager.setCategoryMatch(".xml", FileUtils.FILE_TYPE_DOCUMENT);
	
	public static Intent getDocumentFileIntent(File file)
	{
		Intent intent = null;
		String path = file.getPath();
		if(!TextUtils.isEmpty(path))
		{
			int idx = path.lastIndexOf(".");
			if(idx>-1)
			{
				String format = path.substring(idx);
				if(!TextUtils.isEmpty(format))
				{
					if(".txt".equalsIgnoreCase(format))
					{
						intent = getTextFileIntent(path);
					}
					else if(".epub".equalsIgnoreCase(format))
					{
						intent = getEpubFileIntent(path);
					}
					else if(".chm".equalsIgnoreCase(format))
					{
						intent = getChmFileIntent(path);
					}
					else if(".pdf".equalsIgnoreCase(format) || ".ps".equalsIgnoreCase(format))
					{
						intent = getPdfFileIntent(path);
					}
					else if(".doc".equalsIgnoreCase(format) || ".docx".equalsIgnoreCase(format))
					{
						intent = getWordFileIntent(path);
					}
					else if(".xls".equalsIgnoreCase(format) || ".xlsx".equalsIgnoreCase(format))
					{
						intent = getExcelFileIntent(path);
					}
					else if(".ppt".equalsIgnoreCase(format) || ".pptx".equalsIgnoreCase(format))
					{
						intent = getPptFileIntent(path);
					}
					else if(".html".equalsIgnoreCase(format) || ".htm".equalsIgnoreCase(format) || ".xhtml".equalsIgnoreCase(format) || ".xml".equalsIgnoreCase(format))
					{
						intent = getHtmlFileIntent(path);
					}
				}
			}
		}
		return intent;
	}
	

	 //android获取一个用于打开HTML文件的intent

	  public static Intent getHtmlFileIntent( String param )

	  {

	    Uri uri = Uri.parse(param ).buildUpon().encodedAuthority("com.android.htmlfileprovider").scheme("content").encodedPath(param ).build();

	    Intent intent = new Intent("android.intent.action.VIEW");

	    intent.setDataAndType(uri, "text/html");

	    return intent;

	  }

//	 //android获取一个用于打开图片文件的intent
//
//	  public static Intent getImageFileIntent( String param )
//
//	  {
//
//	    Intent intent = new Intent("android.intent.action.VIEW");
//
//	    intent.addCategory("android.intent.category.DEFAULT");
//
//	    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//	    Uri uri = Uri.fromFile(new File(param ));
//
//	    intent.setDataAndType(uri, "image/*");
//
//	    return intent;
//
//	  }

	  //android获取一个用于打开PDF文件的intent

	  public static Intent getPdfFileIntent( String param )

	  {

	    Intent intent = new Intent("android.intent.action.VIEW");

	    intent.addCategory("android.intent.category.DEFAULT");

	    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

	    Uri uri = Uri.fromFile(new File(param ));

	    intent.setDataAndType(uri, "application/pdf");

	    return intent;

	  }
	  
	  public static Intent getEpubFileIntent( String param )

	  {

	    Intent intent = new Intent("android.intent.action.VIEW");

	    intent.addCategory("android.intent.category.DEFAULT");


	    Uri uri = Uri.fromFile(new File(param ));

	    intent.setDataAndType(uri, "application/epub+zip");

	    return intent;

	  }

	 

	 //android获取一个用于打开文本文件的intent

	 public static Intent getTextFileIntent( String param) 

	{

	 Intent intent = new Intent("android.intent.action.VIEW");

	  intent.addCategory("android.intent.category.DEFAULT");

	 intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

//	 if (paramBoolean)
//
//	 {
//
//	Uri uri1 = Uri.parse(param );
//
//	 intent.setDataAndType(uri1, "text/plain");
//
//	 }
//
//	 else
//
//	 {

	Uri uri2 = Uri.fromFile(new File(param ));

	intent.setDataAndType(uri2, "text/plain");

//	 }

	 return intent;

	}

	 

	 //android获取一个用于打开音频文件的intent

//	  public static Intent getAudioFileIntent( String param )
//
//	  {
//
//	    Intent intent = new Intent("android.intent.action.VIEW");
//
//	    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//	  intent.putExtra("oneshot", 0);
//
//	    intent.putExtra("configchange", 0);
//
//	    Uri uri = Uri.fromFile(new File(param ));
//
//	    intent.setDataAndType(uri, "audio/*");
//
//	    return intent;
//
//	  }

	  //android获取一个用于打开视频文件的intent

//	  public static Intent getVideoFileIntent( String param )
//
//	  {
//
//	     Intent intent = new Intent("android.intent.action.VIEW");
//
//	    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//	    intent.putExtra("oneshot", 0);
//
//	    intent.putExtra("configchange", 0);
//
//	    Uri uri = Uri.fromFile(new File(param ));
//
//	    intent.setDataAndType(uri, "video/*");
//
//	    return intent;
//
//	  }

	  //android获取一个用于打开CHM文件的intent

	  public static Intent getChmFileIntent( String param )

	  { 

	    Intent intent = new Intent("android.intent.action.VIEW");

	    intent.addCategory("android.intent.category.DEFAULT");

	    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

	    Uri uri = Uri.fromFile(new File(param ));

	    intent.setDataAndType(uri, "application/x-chm");

	    return intent;

	  }

	 

	 //android获取一个用于打开Word文件的intent

	   public static Intent getWordFileIntent( String param )

	  {

	     Intent intent = new Intent("android.intent.action.VIEW");

	     intent.addCategory("android.intent.category.DEFAULT");

	     intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

	     Uri uri = Uri.fromFile(new File(param ));

	     intent.setDataAndType(uri, "application/msword");

	     return intent;

	   }

	 

	 //android获取一个用于打开Excel文件的intent

	   public static Intent getExcelFileIntent( String param )

	   {

	     Intent intent = new Intent("android.intent.action.VIEW");

	     intent.addCategory("android.intent.category.DEFAULT");

	     intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

	     Uri uri = Uri.fromFile(new File(param ));

	     intent.setDataAndType(uri, "application/vnd.ms-excel");

	     return intent;

	   }

	 

	 //android获取一个用于打开PPT文件的intent

	   public static Intent getPptFileIntent( String param )

	   {

	     Intent intent = new Intent("android.intent.action.VIEW");

	     intent.addCategory("android.intent.category.DEFAULT");

	     intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

	     Uri uri = Uri.fromFile(new File(param ));

	     intent.setDataAndType(uri, "application/vnd.ms-powerpoint");

	     return intent;

	   }
}
