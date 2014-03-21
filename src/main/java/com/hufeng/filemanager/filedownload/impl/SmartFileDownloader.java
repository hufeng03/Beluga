package com.hufeng.filemanager.filedownload.impl;

import android.content.Context;
import android.util.Log;

import com.hufeng.filemanager.filedownload.db.FileService;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
/**
 * 文件下载器
 * @author lihuoming@sohu.com
 */
public class SmartFileDownloader {
	private static final String TAG = "SmartFileDownloader";
	private Context context;
	private FileService fileService;	
	/* 已下载文件长度 */
	private int downloadSize = 0;
	/* 原始文件长度 */
	private int fileSize = 0;
	/* 线程数 */
	private SmartDownloadThread[] threads;
	/* 本地保存文件 */
	private File saveFile;
	/* 缓存各线程下载的长度*/
	private Map<Integer, Integer> data = new ConcurrentHashMap<Integer, Integer>();
	/* 每条线程下载的长度 */
	private int block;
	/* 下载路径  */
	private String downloadUrl;
	/* 暂停下载*/
	private boolean mPause;
	private boolean mDownloading;
    /* 存储的文件名*/
    private String mFilename;
	
	/**
	 * 获取线程数
	 */
	public int getThreadSize() {
		return threads.length;
	}
	/**
	 * 获取文件大小
	 * @return
	 */
	public int getFileSize() {
		return fileSize;
	}
	
	public int getDownloadSize(){
		return downloadSize;
	}

    public String getFileName() {
        return saveFile.getAbsolutePath();
    }
	/**
	 * 累计已下载大小
	 * @param size
	 */
	protected synchronized void append(int size) {
		downloadSize += size;
	}
	/**
	 * 更新指定线程最后下载位置
	 * @param threadId 线程id
	 * @param pos 最后下载的位置
	 */
	protected void update(int threadId, int pos) {
		this.data.put(threadId, pos);
	}
	/**
	 * 保存记录文件
	 */
	protected synchronized void saveLogFile() {
		this.fileService.update(this.downloadUrl, this.data);
	}
	/**
	 * 构建文件下载器
	 * @param downloadUrl 下载路径
	 * @param fileSaveDir 文件保存目录
	 * @param threadNum 下载线程数
	 */
	public SmartFileDownloader(Context context, String downloadUrl, File fileSaveDir, String name, boolean append, int threadNum) {
		try {
			this.context = context;
			this.downloadUrl = downloadUrl;
			fileService = new FileService(this.context);
			URL url = new URL(this.downloadUrl);
			if(!fileSaveDir.exists()) fileSaveDir.mkdirs();
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5*1000);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
//			conn.setRequestProperty("Accept-Language", "zh-CN");
			conn.setRequestProperty("Referer", downloadUrl); 
			conn.setRequestProperty("Charset", "UTF-8");
//			conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
			//conn.setRequestProperty("Connection", "Keep-Alive");
			conn.connect();
			printResponseHeader(conn);
			if (conn.getResponseCode()==200) {
                mFilename = getFileName(fileSaveDir, name, append);
                this.saveFile = new File(mFilename);/* 保存文件 */

				this.fileSize = conn.getContentLength();//根据响应获得文件大小
                if (this.fileSize <= 0) throw new Exception("Unkown file size, chunk mode");
                this.threads = new SmartDownloadThread[threadNum];
                this.block = this.fileSize / this.threads.length;
//				if (this.fileSize <= 0) {
//                    print("Unkown file size, chunk mode");
//                    this.threads = new SmartDownloadThread[1];
//                    this.block = 0;
//                } else {
//                    this.threads = new SmartDownloadThread[threadNum];
//                    this.block = this.fileSize / this.threads.length;
//                }
                Map<Integer, Integer> logdata = fileService.getData(downloadUrl);
                if (logdata.size() > 0) {
                    for (Map.Entry<Integer, Integer> entry : logdata.entrySet())
                        data.put(entry.getKey(), entry.getValue());
                }
                if (this.data.size() == this.threads.length) {
                    for (int i = 0; i < this.threads.length; i++) {
                        this.downloadSize += this.data.get(i + 1);
                    }
                    print("已经下载的长度" + this.downloadSize);
                }
            }else{
				throw new RuntimeException("server no response ");
			}
		} catch (Exception e) {
			print(e.toString());
			throw new RuntimeException("don't connection this url");
		}
	}
	/**
	 * 根据连接获取文件名
	 */
	private String getFileName(File dir, String name, boolean append) {
//		String filename = null;
//		String content_type = conn.getContentType();
//		if("application/zip".equals(content_type))
//		{
//			filename = this.downloadUrl.substring(this.downloadUrl.lastIndexOf('?') + 1).replace("&", "_")+".zip";
//		}
//		if(filename==null || "".equals(filename.trim())){//如果获取不到文件名称
//			for (int i = 0;; i++) {
//				String mine = conn.getHeaderField(i);
//				if (mine == null) break;
//				if("content-disposition".equals(conn.getHeaderFieldKey(i).toLowerCase())){
//					Matcher m = Pattern.compile(".*filename=(.*)").matcher(mine.toLowerCase());
//					if(m.find()) return m.group(1);
//				}
//			}
//			filename = UUID.randomUUID()+ ".tmp";//默认取一个文件名
//		}
//		String filename =  Long.toHexString(Double.doubleToLongBits(Math
//                .random()))+".zip";
        File file = new File(dir, name);
        if (!append && file.exists()) {
            int i = 1;
            int idx = name.lastIndexOf('.');
            if (idx>0) {
                String real_name = name.substring(0,idx);
                String extension = name.substring(idx);
                while(!file.exists()) {
                    file = new File(dir, real_name+"("+(i++)+")"+extension);
                }
            } else {
                while(!file.exists()) {
                    file = new File(dir, name+"("+(i++)+")");
                }
            }
        }
		return file.getAbsolutePath();
	}
	
	public void pause(){
		mPause = true;
	}
	
	public boolean isPaused()
	{
		return mPause && !mDownloading;
	}

    private boolean mFailed = false;

    public boolean isFailed() {
        return mFailed && !mDownloading;
    }
	
	/**
	 *  开始下载文件
	 * @param listener 监听下载数量的变化，如果不需要了解实时下载的数量，可以设置为null
	 * @return 已下载文件大小
	 * @throws Exception
	 */
	public int download(SmartDownloadProgressListener listener) throws Exception{
		try {
			this.mPause = false;
			RandomAccessFile randOut = new RandomAccessFile(this.saveFile, "rw");
			if(this.fileSize>0) randOut.setLength(this.fileSize);
			randOut.close();
			URL url = new URL(this.downloadUrl);
			if(this.data.size() != this.threads.length){
				this.data.clear();//清除数据
				for (int i = 0; i < this.threads.length; i++) {
					this.data.put(i+1, 0);
				}
			}
			for (int i = 0; i < this.threads.length; i++) {
				int downLength = this.data.get(i+1);
				if(downLength < this.block && this.downloadSize<this.fileSize){ //该线程未完成下载时，继续下载
                    int startPos = block * i;//开始位置
                    int endPos = block * (i+1) -1;//结束位置
                    if(i==this.threads.length-1) {
                        endPos = this.fileSize-1;
                    }
					this.threads[i] = new SmartDownloadThread(this, url, this.saveFile, startPos, endPos, this.data.get(i+1), i+1);
					this.threads[i].setPriority(7);
					this.threads[i].start();
				}else{
					this.threads[i] = null;
				}
			}
			this.fileService.save(this.downloadUrl, this.data);
			boolean notFinish = true;//下载未完成
			mDownloading = true;
            boolean fail = false;
            boolean pause = false;
            int count = 0;
            while (notFinish) {// 循环判断是否下载完成
                int threadCount = threads.length;
                if (mPause) {
                    int pauseCount = 0;
                    for (int i=0; i<threadCount; i++) {
                        SmartDownloadThread thread = threads[i];
                        if (thread != null && !thread.isFinish()) {
                            thread.pause();
                            pauseCount++;
                        }
                    }
                    if(pauseCount > 0){
                        pause = true;
                    }
                    break;
                }
                Thread.sleep(800);
                notFinish = false;//假定下载完成
                String exceptionStr = "pass";
                Exception exception = null;
                int failCount = 0;
                int workCount = 0;
                for (int i=0; i<threadCount; i++) {
                    SmartDownloadThread thread = threads[i];
                    if (thread != null && !thread.isFinish()) {
                        notFinish = true;//下载没有完成
                        Log.i(TAG, "thread "+(i+1)+" downLength "+this.threads[i].getDownLength()+" ("+this.block+")");
                        if (thread.getDownLength() == -1) {//如果下载失败，不再自动重新下载
//                              LogUtil.i(TAG, "smartfiledownloader reset download thread "+i);
//                              this.threads[i] = new SmartDownloadThread(this, url, this.saveFile, this.block, this.data.get(i+1), i+1);
//                              this.threads[i].setPriority(7);
//                              this.threads[i].start();
                            failCount++;
                        } else {
                            workCount++;
                        }
                    }
                }
                if (count == 3){
                    if(listener != null) {
                        listener.onDownloadSize(this.downloadSize);
                    }
                    count = 0;
                }else {
                    count ++;
                }

                if (failCount > 0 && workCount == 0) {
                    fail = true;
                    break;
                }
            }
            if(pause)
            {
                listener.onDownloadSize(-300);
            }
            else if(fail)
            {
                mFailed = true;
                listener.onDownloadSize(-200);
            }
            else
            {
                if(downloadSize < fileSize){
                    listener.onDownloadSize(-200);
                }else{
                    listener.onDownloadSize(-100);
                    fileService.delete(this.downloadUrl);
                }
            }
            mDownloading = false;
		} catch (Exception e) {
			print(e.toString());
			throw new Exception("file download fail");
		}
		return this.downloadSize;
	}
	/**
	 * 获取http响应头字段
	 * @param http
	 * @return
	 */
	public static Map<String, String> getHttpResponseHeader(HttpURLConnection http) {
		Map<String, String> header = new LinkedHashMap<String, String>();
		for (int i = 0;; i++) {
			String mine = http.getHeaderField(i);
			if (mine == null) break;
			header.put(http.getHeaderFieldKey(i), mine);
		}
		return header;
	}
	/**
	 * 打印Http头字段
	 * @param http
	 */
	public static void printResponseHeader(HttpURLConnection http){
		Map<String, String> header = getHttpResponseHeader(http);
		for(Map.Entry<String, String> entry : header.entrySet()){
			String key = entry.getKey()!=null ? entry.getKey()+ ":" : "";
			print(key+ entry.getValue());
		}
	}
	//打印日志־
	private static void print(String msg){
		Log.i(TAG, msg);
	}
	
	

}
