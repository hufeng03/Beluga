package com.kanbox.api;

public interface RequestListener {
	public void onComplete(String path, String response, int operationType);
	public void onError(String path, KanboxException error, int operationType);
	public void downloadProgress(String path, long currSize);
    public void uploadProgress(String path, long currSize);

	
	public static final int OP_GET_TOKEN = 1;
	public static final int OP_REFRESH_TOKEN = 2;
	
	public static final int OP_GET_ACCCOUNT_INFO = 3;
	public static final int OP_GET_FILELIST = 4;
	
	public static final int OP_MOVE = 5;
	public static final int OP_COPY = 6;
	public static final int OP_DELETE = 7;
	public static final int OP_MAKE_DIR = 8;
	
	
	public static final int OP_MAKE_SHARE_DIR = 9;			//创建共享目录
	public static final int OP_GET_SHARE_INVITE_LIST = 10;	//获取共享邀请列表
	public static final int OP_HANDLE_SHARE_INVITE = 11;	//处理共享请求
	public static final int OP_SHARED_BY_SELF = 12;			//是否是自己创建的共享文件夹

	public static final int OP_UPLOAD = 12;
	public static final int OP_DOWNLOAD = 13;
    public static final int OP_GET_THUMBNAIL = 14;
}
