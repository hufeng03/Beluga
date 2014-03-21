package com.hufeng.filemanager.tools;

import android.content.Context;

import com.hufeng.filemanager.R;

import java.util.ArrayList;

public class ToolsManager {
	
	public static final String RECOMMEND = "recommend";
	
	public static Tool[] getAllTools(Context context){
//		if("CN".equals(locale) || "TW".equals(locale)){
//			tools = new Tool[3];
//			tools[0] = new Tool(R.string.tool_name_ftp, R.string.tool_description_ftp, R.drawable.tool_icn_ftp, "com.hufeng.filemanager","com.hufeng.swiftp.ServerControlActivity");
//			tools[1] = new Tool(R.string.tool_name_http, R.string.tool_description_http, R.drawable.tool_icn_http, "com.hufeng.filemanager","com.hufeng.nanohttpd.ServerControlActivity");
//			tools[2] = new Tool(R.string.tool_name_recommend, R.string.tool_description_recommend, R.drawable.tool_icn_http, "com.hufeng.filemanager",RECOMMEND);
//		}else{
//			tools = new Tool[3];
//			tools[0] = new Tool(R.string.tool_name_ftp, R.string.tool_description_ftp, R.drawable.tool_icn_ftp, "com.hufeng.filemanager","com.hufeng.swiftp.ServerControlActivity");
//			tools[1] = new Tool(R.string.tool_name_http, R.string.tool_description_http, R.drawable.tool_icn_http, "com.hufeng.filemanager","com.hufeng.nanohttpd.ServerControlActivity");
//			tools[2] = new Tool(R.string.tool_name_recommend, R.string.tool_description_recommend, R.drawable.tool_icn_http, "com.hufeng.filemanager",RECOMMEND);
//		}
		ArrayList<Tool> toolArray = new ArrayList<Tool>();
		toolArray.add(new Tool(R.string.tool_name_ftp, R.string.tool_description_ftp, R.drawable.tool_icn_ftp, "com.hufeng.filemanager","com.hufeng.swiftp.ServerControlActivity"));
		toolArray.add(new Tool(R.string.tool_name_http, R.string.tool_description_http, R.drawable.tool_icn_http, "com.hufeng.filemanager","com.hufeng.nanohttpd.ServerControlActivity"));
    	toolArray.add(new Tool(R.string.tool_name_safe, R.string.tool_description_safe, R.drawable.tool_icn_safe, "com.hufeng.filemanager", "com.hufeng.filemanager.SafeBoxActivity"));
		toolArray.add(new Tool(R.string.tool_name_kanbox, R.string.tool_description_kanbox, R.drawable.tool_icn_kanbox, "com.hufeng.filemanager", "com.hufeng.filemanager.KanBoxActivity"));
        toolArray.add(new Tool(R.string.tool_name_selected, R.string.tool_description_selected, R.drawable.tool_icn_recommend, "com.hufeng.filemanager", "com.hufeng.filemanager.ResourceActivity"));

//        toolArray.add(new Tool(R.string.tool_name_ftp, R.string.tool_description_ftp, R.drawable.tool_icn_ftp, "com.kanbox.filemanager","com.kanbox.swiftp.ServerControlActivity"));
//        toolArray.add(new Tool(R.string.tool_name_http, R.string.tool_description_http, R.drawable.tool_icn_http, "com.kanbox.filemanager","com.kanbox.nanohttpd.ServerControlActivity"));
//        toolArray.add(new Tool(R.string.tool_name_safe, R.string.tool_description_safe, R.drawable.tool_icn_safe, "com.kanbox.filemanager", "com.kanbox.filemanager.SafeBoxActivity"));
//        toolArray.add(new Tool(R.string.tool_name_kanbox, R.string.tool_description_kanbox, R.drawable.tool_icn_kanbox, "com.kanbox.filemanager", "com.kanbox.filemanager.KanBoxActivity"));
//        toolArray.add(new Tool(R.string.tool_name_selected, R.string.tool_description_selected, R.drawable.tool_icn_recommend, "com.kanbox.filemanager", "com.kanbox.filemanager.SelectedActivity"));

//        if(Constants.SHOW_AD){
//			String locale = context.getResources().getConfiguration().locale.getCountry();
//			if("CN".equals(locale) || "TW".equals(locale)){
//				//if(!ChannelUtil.isDOOV_ROOMChannel(context)){
//					toolArray.add(new Tool(R.string.tool_name_recommend, R.string.tool_description_recommend, R.drawable.tool_icn_recommend, "com.hufeng.filemanager",RECOMMEND));
//				//}
//			}
//    	}
		return toolArray.toArray(new Tool[toolArray.size()]);
		
	}
}
