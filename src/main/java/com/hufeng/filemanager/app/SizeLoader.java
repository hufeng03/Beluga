package com.hufeng.filemanager.app;

import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.widget.TextView;

import com.hufeng.filemanager.FileManager;
import com.hufeng.filemanager.browser.QueueedWorkerThread;
import com.hufeng.filemanager.browser.QueueedWorkerThread.AsyncLoader;
import com.hufeng.filemanager.browser.QueueedWorkerThread.Task;
import com.hufeng.filemanager.utils.FileUtil;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class SizeLoader implements Callback{
	
	private static final int MESSAGE_REFRESH_SIZE = 1;
	
	public class AppSize{
		private long cachesize ;   //缓存大小
		private long datasize ;    //数据大小
		private long codesize ;   //应用程序大小
		private long totalsize ; //全部大小
		
		public AppSize(long cache_size, long data_size, long code_size) {
			this.cachesize = cache_size;
			this.datasize = data_size;
			this.codesize = code_size;
			this.totalsize = cache_size + data_size + code_size;
		}
	}

    private static final String TAG = SizeLoader.class.getSimpleName();
    
    private final Handler mHandler = new Handler(this);
    
	/**
	 * Handler for messages sent to the UI thread.
	 */
//	private final Handler mMainThreadHandler = new Handler(this);
    
    private QueueedWorkerThread mWorkerThread = new QueueedWorkerThread(
          "size-loader");
    
    private Map<TextView, String> mPendingLoad = Collections.synchronizedMap(new HashMap<TextView, String>());
    
    private Map<String, AppSize> mCache = Collections
            .synchronizedMap(new HashMap<String, AppSize>());

//    private boolean has(String key){
//    	return mCache.containsKey(key);
//    }
//    
//    private AppSize get(String key) {
//        return mCache.get(key);
//    }
//
//    private void set(String key, AppSize value) {
//        AppSize size = mCache.get(key);
////        if (bm != null && bm != value)
////            bm.recycle();
//        mCache.put(key, value);
//    }
    
    public void loadSize(TextView view, String pkg) {
    	if(mCache.containsKey(pkg)){
    		mPendingLoad.remove(view);
    		view.setText(FileUtil.normalize(mCache.get(pkg).totalsize));
    		return;
    	}else{
    		if(!mPendingLoad.containsKey(view)){
				Task task = new Task();
				task.param = pkg;
				task.loader = mSizeLoader;
				mWorkerThread.addTask(task);
				mPendingLoad.put(view,pkg);
    		}else{
    			String val = mPendingLoad.get(view);
    			if(!pkg.equals(val)){
    				mPendingLoad.remove(view);
    				Task task = new Task();
    				task.param = pkg;
    				task.loader = mSizeLoader;
    				mWorkerThread.addTask(task);
    				mPendingLoad.put(view,pkg);
    			}
    		}
    	}
    }
    
    public void start() {
    	mWorkerThread.start();
    }

    public void clear() {
        mCache.clear();
        mPendingLoad.clear();
        mWorkerThread.destroySelf();
    }
    
    
	private AsyncLoader mSizeLoader = new AsyncLoader() {

		@Override
		public void load(Task task) {
			try {
				doLoad(task);
			} catch (OutOfMemoryError e) {
				clear();
				e.printStackTrace();
			}
		}

		private void doLoad(Task task) {
			final String pkg = (String) task.param;
			try {
				queryPackageSize(pkg);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
	};
	

	private final PkgSizeObserver mPkgSizeObserver = new PkgSizeObserver();
	
    public void  queryPackageSize(String pkgName) throws Exception{
    	if ( pkgName != null){
    		//使用放射机制得到PackageManager类的隐藏函数getPackageSizeInfo
    		PackageManager pm = FileManager.getAppContext().getPackageManager();  //得到pm对象
    		if(android.os.Build.VERSION.SDK_INT < /*android.os.Build.VERSION_CODES.JELLY_BEAN_MR1*/17) {
	    		try {
	    			//通过反射机制获得该隐藏函数
					Method getPackageSizeInfo = pm.getClass().getDeclaredMethod("getPackageSizeInfo", String.class,IPackageStatsObserver.class);
				    //调用该函数，并且给其分配参数 ，待调用流程完成后会回调PkgSizeObserver类的函数
				    getPackageSizeInfo.invoke(pm, pkgName, mPkgSizeObserver);
				} 
	        	catch(Exception ex){
	        		Log.e(TAG, "NoSuchMethodException") ;
	        		ex.printStackTrace() ;
	        		throw ex ;  // 抛出异常
	        	} 
    		} else {
    			try {
	    			//通过反射机制获得该隐藏函数
					Method getPackageSizeInfo = pm.getClass().getDeclaredMethod("getPackageSizeInfo", String.class, int.class, IPackageStatsObserver.class);
				    //调用该函数，并且给其分配参数 ，待调用流程完成后会回调PkgSizeObserver类的函数
				    getPackageSizeInfo.invoke(pm, pkgName, android.os.Process.myUid()/100000, mPkgSizeObserver);
				} 
	        	catch(Exception ex){
	        		Log.e(TAG, "NoSuchMethodException") ;
	        		ex.printStackTrace() ;
	        		throw ex ;  // 抛出异常
	        	} 
    		}
    	}
    }
    
    //aidl文件形成的Bindler机制服务类
    public class PkgSizeObserver extends IPackageStatsObserver.Stub{
    	
        /*** 回调函数，
         * @param pStats ,返回数据封装在PackageStats对象中
         * @param succeeded  代表回调成功
         */ 
		@Override
		public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
				throws RemoteException {
			// TODO Auto-generated method stub
			String pkg = pStats.packageName;
			Log.i(TAG, pkg+": cachesize--->"+pStats.cacheSize+" datasize---->"+pStats.dataSize+ " codeSize---->"+pStats.codeSize);
			mCache.put(pkg, new AppSize(pStats.cacheSize, pStats.dataSize, pStats.codeSize));
			
			Message msg = mHandler.obtainMessage(MESSAGE_REFRESH_SIZE, pkg);
			msg.sendToTarget();
		}
    }

	@Override
	public boolean handleMessage(Message msg) {
		switch(msg.what){
		case MESSAGE_REFRESH_SIZE:
			String pkg = (String)msg.obj;
			Log.i(TAG,"receiver MESSAGE_REFRESH_SIZE for pkg = "+pkg);
			for(TextView key : mPendingLoad.keySet()){
				if(pkg.equals(mPendingLoad.get(key))){
					key.setText(FileUtil.normalize(mCache.get(pkg).totalsize));
					mPendingLoad.remove(key);
					Log.i(TAG,"refresh view for size "+key);
					break;
				}
			}
			return true;
		default:
			break;
		}
		return false;
	}

}
