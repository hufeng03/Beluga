package com.hufeng.filemanager.storage;

import java.io.Serializable;

/**
 * 
 * @author feng
 *
 */
public class StorageUnit implements Serializable{
	
	private static final long serialVersionUID = -521977859303276433L;
	
	public String path;
	public String description;
	public boolean removable;
	public String state;
	public long availableSpace;
	public long allSpace;
	
	
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public boolean isRemovable() {
		return removable;
	}
	public void setRemovable(boolean removable) {
		this.removable = removable;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public long getAvailableSpace() {
		return availableSpace;
	}
	public void setAvailableSpace(long availableSpace) {
		this.availableSpace = availableSpace;
	}
	public long getAllSpace() {
		return allSpace;
	}
	public void setAllSpace(long allSpace) {
		this.allSpace = allSpace;
	}
	
	public StorageUnit(String path, String description, boolean removable, String state, long availableSpace, long allSpace){
		this.path = path;
		this.description = description;
		this.removable = removable;
		this.state = state;
		this.availableSpace = availableSpace;
		this.allSpace = allSpace;
	}
	
	@Override
	public String toString(){
		return "path="+path+", description="+description
			+", removable="+removable+", state="+state
			+", availableSpace="+availableSpace+" allSpace="+allSpace;
	}
	
}
