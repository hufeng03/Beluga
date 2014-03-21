package com.kanbox.api;

import android.util.Log;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

public class Kanbox {
	private static Kanbox mKanbox;
	
	private Kanbox() {
	}
	
	public static Kanbox getInstance() {
		if(mKanbox == null) {
			mKanbox = new Kanbox();
		}
		return mKanbox;
	}
	
	/**
	 * 获取帐号信息
	 * @param token
	 * @param listener
	 */
	public void getAccountInfo(Token token, RequestListener listener) {
		String getAccountInfoUrl = "https://api.kanbox.com/0/info";
		HttpRequestBase httpMethod = KanboxHttp.doGet(getAccountInfoUrl, token);
		new KanboxAsyncTask(null, null, httpMethod, listener, RequestListener.OP_GET_ACCCOUNT_INFO).serialExecute();
	}
	
	/**
	 * 获取文件列表
	 * @param token
	 * @param path:要请求列表的路径
	 * @param listener
	 */
	public void getFileList(Token token, String path, RequestListener listener) {
		String getFileListUrl = "https://api.kanbox.com/0/list";
		HttpRequestBase httpMethod = KanboxHttp.doGet(getFileListUrl + path, token);
		new KanboxAsyncTask(path, null, httpMethod, listener, RequestListener.OP_GET_FILELIST).serialExecute();
	}
	
	/**
	 * 移动文件
	 * @param token
	 * @param sourcePath：源路径
	 * @param desPath：目标路径
	 * @param listener
	 */
	public void moveFile(Token token, String sourcePath, String desPath, RequestListener listener) {
		String moveUrl = "https://api.kanbox.com/0/move";
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("path", sourcePath);
		params.put("destination_path", desPath);
		
		HttpRequestBase httpMethod = KanboxHttp.doGet(moveUrl, params, token);
		new KanboxAsyncTask(sourcePath, desPath, httpMethod, listener, RequestListener.OP_MOVE).serialExecute();
	}
	
	/**
	 * 复制文件
	 * @param token
	 * @param sourcePath：源路径
	 * @param desPath：目标路径
	 * @param listener
	 */
	public void copyFile(Token token, String sourcePath, String desPath, RequestListener listener) {
		String moveUrl = "https://api.kanbox.com/0/copy";
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("path", sourcePath);
		params.put("destination_path", desPath);
		
		HttpRequestBase httpMethod = KanboxHttp.doGet(moveUrl, params, token);
		new KanboxAsyncTask(sourcePath, desPath, httpMethod, listener, RequestListener.OP_COPY).serialExecute();
	}
	
	/**
	 * 删除文件
	 * @param token
	 * @param path：文件路径
	 * @param listener
	 */
	public void deleteFile(Token token, String path, RequestListener listener) {
		String moveUrl = "https://api.kanbox.com/0/delete";
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("path", path);
		
		HttpRequestBase httpMethod = KanboxHttp.doGet(moveUrl, params, token);
		new KanboxAsyncTask(path, null, httpMethod, listener, RequestListener.OP_DELETE).serialExecute();
	}
	
	/**
	 * 创建文件夹
	 * @param token
	 * @param path：文件路径
	 * @param listener
	 */
	public void makeDir(Token token, String path, RequestListener listener) {
		String moveUrl = "https://api.kanbox.com/0/create_folder";
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("path", path);
	
		HttpRequestBase httpMethod = KanboxHttp.doGet(moveUrl, params, token);
		new KanboxAsyncTask(path, null, httpMethod, listener, RequestListener.OP_MAKE_DIR).serialExecute();
	}
	
	/**
	 * 创建共享文件夹
	 * @param emails：邀请 邮件地址列表
	 * @param token
	 * @param path：共享文件夹路径
	 * @param listener
	 * @throws java.io.UnsupportedEncodingException
	 */
	public void makeShareDir(ArrayList<String> emails, Token token, String path, RequestListener listener) throws UnsupportedEncodingException {
		String makeShareDirUrl = "https://api.kanbox.com/0/share";
		JSONArray params = new JSONArray();
		for (String string : emails) {
			params.put(string);
		}

		HttpRequestBase httpMethod = KanboxHttp.doPost(makeShareDirUrl + path, params.toString(), token);
		new KanboxAsyncTask(path, null, httpMethod, listener, RequestListener.OP_MAKE_SHARE_DIR).serialExecute();
	}

	/**
	 * 获取共享邀请列表
	 * @param token
	 * @param listener
	 */
	public void getShareInviteList(Token token, RequestListener listener) {
		String getShareInviteUrl = "https://api.kanbox.com/0/pendingshares";
		HttpRequestBase httpMethod = KanboxHttp.doGet(getShareInviteUrl, token);
		new KanboxAsyncTask(null, null, httpMethod, listener, RequestListener.OP_GET_SHARE_INVITE_LIST).serialExecute();
	}

	/**
	 * 处理共享请求
	 * @param shareDir：共享目录路径
	 * @param inviter：邀请者 邮箱地址
	 * @param accept：是否接受共享:0:拒绝， 1：接受
	 * @param token
	 * @param listener
	 * @throws org.json.JSONException
	 * @throws java.io.UnsupportedEncodingException
	 */
	public void handleShareInvite(String shareDir, String inviter, boolean accept, Token token, RequestListener listener) throws JSONException, UnsupportedEncodingException {
		String handleShareInviteUrl = "https://api.kanbox.com/0/pendingshares";
		JSONObject params = new JSONObject();
		params.put("path", shareDir);
		params.put("user", inviter);
		params.put("accept", accept);
		String strParams = params.toString();
//		strParams.replaceAll("\\\\/", "/");
		Log.e("test", strParams);
		HttpRequestBase httpMethod = KanboxHttp.doPost(handleShareInviteUrl, strParams, token);
		new KanboxAsyncTask(shareDir, null, httpMethod, listener, RequestListener.OP_HANDLE_SHARE_INVITE).serialExecute();
	}

	/**
	 * 是否是自己创建的共享文件夹
	 * @param token
	 */
	public void checkSharedOwner(Token token, String path, RequestListener listener) {
		String url = "https://api.kanbox.com/0/checkowner";
		HttpRequestBase httpMethod = KanboxHttp.doGet(url + path, token);
		new KanboxAsyncTask(path, null, httpMethod, listener, RequestListener.OP_SHARED_BY_SELF).serialExecute();
	}

	/**
	 * 下载文件
	 * @param path：文件路径
	 * @param destPath：要下载的本地路径
	 * @param token
	 * @param listener
	 */
	public KanboxAsyncTask download(String path, String destPath, Token token, RequestListener listener) {
		String downloadUrl = "https://api.kanbox.com/0/download";

		HttpRequestBase httpMethod = KanboxHttp.doGet(downloadUrl + path, token);
		KanboxAsyncTask task = new KanboxAsyncTask(path, destPath, httpMethod, listener, RequestListener.OP_DOWNLOAD);
        task.serialExecute();
        return task;
	}


    public void getThumbnail(String path, Token token, RequestListener listener){
        String thumbnailUrl = "https://api.kanbox.com/0/thumbnail";
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("size", "small");
        HttpRequestBase httpMethod = KanboxHttp.doGet(thumbnailUrl + path, params, token);
        new KanboxAsyncTask(path, null, httpMethod, listener, RequestListener.OP_GET_THUMBNAIL).serialExecute();
    }

	/**
	 * 上传文件
	 * @param localPath：要上传文件的本地路径
	 * @param destPath：服务器路径
	 * @param token
	 * @param listener
	 * @throws java.io.IOException
	 */
	public KanboxAsyncTask upload(String localPath, String destPath, Token token, RequestListener listener) throws UnsupportedEncodingException {
		String uploadUrl = "https://api-upload.kanbox.com/0/upload";
		
		HttpPost httpMethod = KanboxHttp.doPost(uploadUrl + destPath, null, token);
//		InputStream is = new FileInputStream(localPath);
//		httpMethod.setEntity(new KanboxAsyncTask.CountingInputStreamEntity(is, is.available()));
        KanboxAsyncTask task = new KanboxAsyncTask(localPath, destPath, httpMethod, listener, RequestListener.OP_UPLOAD);
        task.serialExecute();
        return task;
	}
}
