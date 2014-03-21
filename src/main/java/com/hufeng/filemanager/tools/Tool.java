package com.hufeng.filemanager.tools;

public class Tool {
	
	public int name;
	public int description;
	public String package_name;
	public String activity_name;
	public int icon;
	
	public Tool(int name_id, int description_id, int icon_id, String package_name, String activity_name){
		this.name = name_id;
		this.description = description_id;
		this.icon = icon_id;
		this.package_name = package_name;
		this.activity_name = activity_name;
	}
}
