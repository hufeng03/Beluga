package com.hufeng.filemanager.app;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.hufeng.filemanager.R;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AppGridAdapter extends BaseAdapter implements Filterable{

	private List<AppInfo> mOriginalInstalledApps;
	private List<AppInfo> mInstalledApps;
	protected LayoutInflater mInflater;
	private int mLastPosition = 0;
	private Context mContext;
	 private boolean mIsScrolling = false;
	 private String mHighLightText;
	private SizeLoader mSizeLoader;
	 
	private int mIconApk;
//    private IconCache mIconCache = new IconCache();
//    private NameCache mNameCache = new NameCache();
//    private QueueedWorkerThread mWorkerThread = new QueueedWorkerThread(
//            "icon-loader");
//    private HashSet<String> mPendingLoad = new HashSet<String>();
	
	public AppGridAdapter(Context context, List<AppInfo> apps, SizeLoader sizeLoader) {
		// TODO Auto-generated constructor stub
		mContext = context;
		mInflater = LayoutInflater.from(context);
		mInstalledApps = apps;
		mOriginalInstalledApps = mInstalledApps;
		mIconApk = R.drawable.file_category_icon_apk;
		mSizeLoader = sizeLoader;
//		if(mInstalledApps!=null)
//			mInstalledAppSize = mInstalledApps.size()/*+2*/;
//		mWorkerThread.start();
	}
	
//    private void setupNameFast(ViewHolder holder, String pkg) {
//        String value = mNameCache.get(pkg);
// 
//            if (value != null)
//                holder.name.setText(value);
//            else
//                holder.name.setText(pkg);
//    }
//
//    private void setupNameSlow(ViewHolder holder, String pkg) {
//        String value = mNameCache.get(pkg);
//
//
//            holder.name.setText(pkg);
//            if (value != null)
//                holder.name.setText(value);
//            else {
//                if (!mPendingLoad.contains(pkg)) {
//                    Task task = new Task();
//                    task.param = pkg;
//                    task.loader = mIconLoader;
//                    mWorkerThread.addTask(task);
//                    mPendingLoad.add(pkg);
//                }
//            }
//    }
//    
//    private void setupName(ViewHolder holder, String pkg) {
//        holder.name.setText(pkg);
//        if (mIsScrolling) {
//            setupNameFast(holder, pkg);
//        } else {
//            setupNameSlow(holder, pkg);
//        }
//    }
	
	private void setHighLight(String highlight) {
		mHighLightText = highlight;
	}
	
    public void setApps(List<AppInfo> apps)
    {
    	mInstalledApps = apps;
    	mOriginalInstalledApps = mInstalledApps;
    }

    
	
//    private void setupIconFast(ViewHolder holder, String pkg) {
//        Drawable value = mIconCache.get(pkg);
////        String val = mNameCache.get(pkg);
//
//
////        holder.name.setText(pkg);
////        if (val != null)
////            holder.name.setText(val);
////        else {
////        	holder.name.setText(pkg);
////        }
//        
//            if (value != null)
//                holder.icon.setImageDrawable(value);
//            else
//                holder.icon.setImageResource(mIconApk);
//    }
//
//    private void setupIconSlow(ViewHolder holder, String pkg) {
//    	
////        String val = mNameCache.get(pkg);
////        
////        if (val != null)
////            holder.name.setText(val);
////        else
////            holder.name.setText(pkg);
//        
//        Drawable value = mIconCache.get(pkg);
//
//
//            holder.icon.setImageResource(mIconApk);
//            if (value != null)
//                holder.icon.setImageDrawable(value);
//            else {
//                if (!mPendingLoad.contains(pkg)) {
//                    Task task = new Task();
//                    task.id = FileUtils.FILE_TYPE_APK;
//                    task.param = pkg;
//                    task.loader = mIconLoader;
//                    mWorkerThread.addTask(task);
//                    mPendingLoad.add(pkg);
//                }
//            }
//    }
//    
//    private void setupIcon(ViewHolder holder, String pkg) {
//        holder.icon.setImageResource(mIconApk);
//        if (mIsScrolling) {
//            setupIconFast(holder, pkg);
//        } else {
//            setupIconSlow(holder, pkg);
//        }
//    }

	@Override
	public int getCount() {
		if(mInstalledApps!=null)
    		return mInstalledApps.size();
    	else
    		return 0;
	}
	
//	@Override
//	public boolean isEnabled(int position) {
//		// TODO Auto-generated method stub
//		if(position==0 || position==mInstalledAppSize-1)
//			return false;
//		return super.isEnabled(position);
//	}

//	@Override
//	public Object getItem(int position) {
//		// TODO Auto-generated method stub
//		
//		if(position==0)
//			return "Installed Apps";
//		else if(position==mInstalledAppSize-1)
//			return "Downloaded Apks";
//		else if(position<mInstalledAppSize)
//			return mInstalledApps.get(position-1);
//		else
//			return super.getItem(position-mInstalledAppSize);
//	}

//	@Override
//	public long getItemId(int position) {
//		// TODO Auto-generated method stub
//		return super.getItemId(position);
//	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if (convertView != null)
			holder = (ViewHolder) convertView.getTag();

		if (holder == null) {
			convertView = mInflater.inflate(R.layout.app_grid_row, null);
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.appname);
			holder.version = (TextView) convertView
					.findViewById(R.id.appversion);
			holder.size = (TextView) convertView.findViewById(R.id.appsize);
			holder.icon = (ImageView) convertView.findViewById(R.id.appicon);
			// holder.button = (Button)
			// convertView.findViewById(R.id.appremove);
			convertView.setTag(holder);
		} 
		
		Object item = getItem(position);
        bindApp((AppInfo)item, holder);
        

//		holder.button.setOnClickListener(holder);
//        try {
//            setupIcon(holder, holder.pkg);
////            setupName(holder, holder.pkg);
//        } catch (OutOfMemoryError e) {
//            mIconCache.clear();
//            e.printStackTrace();
//        }
		return convertView;
	}

	
	private void bindApp(AppInfo app, ViewHolder holder) {
		holder.icon.setImageDrawable(app.appIcon);
		String name = app.appName;
		holder.pkg = app.packageName;
		mSizeLoader.loadSize(holder.size, holder.pkg);
		
		String pkg = holder.pkg;
		
//		if(TextUtils.isEmpty(name)){
//			name = mNameCache.get(pkg);
//		}
		
		if(!TextUtils.isEmpty(name)){
			name = name.replaceAll(String.valueOf((char) 160), " ").trim();
		}

		if(!TextUtils.isEmpty(mHighLightText))
        {
    		SpannableStringBuilder buf = new SpannableStringBuilder();
    		int size = 0;
        	if(!TextUtils.isEmpty(name))
        	{
        		size = name.length();
            	Pattern pattern  = Pattern.compile(Pattern.quote(mHighLightText), Pattern.CASE_INSENSITIVE);
            	Matcher matcher = pattern.matcher(name);
            	if(matcher!=null)
            	{
            		buf.append(name);
            		while(matcher.find())
            		{
            			int j = matcher.start();
            			int k = matcher.end();
            			buf.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.highlight_color)), j, k, 0);
            		}
            		//
            	}
        	}
        	
        	if(!TextUtils.isEmpty(pkg))
        	{
            	Pattern pattern  = Pattern.compile(Pattern.quote(mHighLightText), Pattern.CASE_INSENSITIVE);
            	Matcher matcher = pattern.matcher(pkg);
            	if(matcher!=null)
            	{
            		buf.append('['+pkg+']');
            		while(matcher.find())
            		{
            			int j = matcher.start();
            			int k = matcher.end();
            			buf.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.highlight_color)), j+1+size, k+1+size, 0);
            		}
            		//
            	}
        	}
        	holder.name.setText(buf);
        }
		else{
			if(!TextUtils.isEmpty(name))
			{	
				holder.name.setText(name);
			}		
			else
			{
				holder.name.setText('['+pkg+']');
			}
		}
		
		holder.version.setText(app.versionName);
	}

	@Override
	public Object getItem(int position) {
		return mInstalledApps.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
    public void setIsScrolling(boolean isScrolling) {
        mIsScrolling = isScrolling;
//        if (mIsScrolling) {
//            mWorkerThread.clearTask();
//            mPendingLoad.clear();
//        }
    }
	
    public void setLastPosition(int lastPosition) {
        if (lastPosition >= 0 && lastPosition < mInstalledApps.size())
            this.mLastPosition = lastPosition;
    }

    public int getLastPosition() {
        return mLastPosition;
    }
    
    public void destroy() {
//        if (mWorkerThread != null)
//            mWorkerThread.destroySelf();
//        if (mIconCache != null)
//            mIconCache.clear();
    }

    
    public final class ViewHolder implements OnClickListener{
        public TextView name;
        public TextView version;
        public TextView size;
        public ImageView icon;
        public Button button;
        public String pkg;
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Uri packageURI = Uri.parse("package:"+pkg);  
			Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);  
			mContext.startActivity(uninstallIntent);
		}
    }
    
//    private AsyncLoader mIconLoader = new AsyncLoader() {
//
//        @Override
//        public void load(Task task) {
//            try {
//                doLoad(task);
//            } catch (OutOfMemoryError e) {
//                mIconCache.clear();
//                e.printStackTrace();
//            }
//        }
//
//        private void doLoad(Task task) {
//            final String pkg = (String) task.param;
//            PackageManager pm = FileManager.getAppContext().getPackageManager();
////            String name = null;
//            Drawable drawable = null;
//			try {
//				drawable = pm.getApplicationIcon(pkg);
////				name = pm.getApplicationInfo(pkg, PackageManager.GET_META_DATA).loadLabel(pm).toString();
//			} catch (NameNotFoundException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//            if(drawable==null)
//            	return;
//            final Drawable draw = drawable;
////            final String nam = name;
//            if (mContext instanceof Activity) {
//                ((Activity) mContext).runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (!mWorkerThread.isDestroyed()) {
//                            mIconCache.set(pkg, draw);
////                            mNameCache.set(pkg, nam);
//                            notifyDataSetChanged();
//                            mPendingLoad.remove(pkg);
//                        } else {
//                        }
//                    }
//                });
//            }
//        }
//
//    };

	@Override
	public Filter getFilter() {
		// TODO Auto-generated method stub
		//return null;
		Filter filter = new Filter(){

			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				// TODO Auto-generated method stub
				FilterResults results = new FilterResults();
                ArrayList<AppInfo> FilteredItems = new ArrayList<AppInfo>();
                if(constraint == null || constraint.length() == 0){
                    results.count = mOriginalInstalledApps.size();
                    results.values = mOriginalInstalledApps;
                }
                else{
                	constraint = constraint.toString().toLowerCase();
                    for(int i = 0; i < mOriginalInstalledApps.size(); i++){
                        AppInfo item = mOriginalInstalledApps.get(i);
                        if(item.appName.toLowerCase().contains(constraint.toString()) || item.packageName.toLowerCase().contains(constraint.toString())){
                            FilteredItems.add(item);
                        }
                    }

                    results.count = FilteredItems.size();

                    results.values = FilteredItems;
                }
                
                return results;
			}

			@Override
			protected void publishResults(CharSequence constraint,
					FilterResults results) {
				mInstalledApps = (ArrayList<AppInfo>)results.values;
				setHighLight(constraint==null?"":constraint.toString());
				notifyDataSetChanged();
			}
			
		};
		return filter;
	}


}
