package com.hufeng.filemanager.ui;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;

import com.hufeng.filemanager.FileManager;
import com.hufeng.filemanager.R;
import com.hufeng.filemanager.utils.IconUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import com.hufeng.filemanager.SettingsActivity;
//import com.umeng.analytics.MobclickAgent;

public class AppSelectPreference extends DialogPreference{

//	private static final String filemanager_ns = "http://schemas.android.com/apk/res/com.hufeng.filemanager";
    private static final String filemanager_ns = "http://schemas.android.com/apk/res-auto";

	private static final int CATEGORY_IMAGE = 1;
	private static final int CATEGORY_AUDIO = 2;
	private static final int CATEGORY_VIDEO = 3;
	
	
	private String mCategoryString;
	private int mCategory;
	private ResolveInfo[] mAvailableApps;
	private ImageView mAppIcon;
	
	public AppSelectPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		setPersistent(true);
		mCategoryString = attrs.getAttributeValue(filemanager_ns, "category");
		
		if( "image".equalsIgnoreCase(mCategoryString) ) {
			mCategory = 1;
			buildAppListForImage();
		} else if( "audio".equalsIgnoreCase(mCategoryString) ) {
			mCategory = 2;
			buildAppListForAudio();
		} else if( "video".equalsIgnoreCase(mCategoryString) ) {
			mCategory = 3;
			buildAppListForVideo();
		}
		
		setDialogLayoutResource(R.layout.app_select_view);
		
		this.setDialogTitle(R.string.app_select_dialog_title);
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		String pkg = "", cls = "";
		switch(mCategory){
		case CATEGORY_IMAGE:
			pkg = getSharedPreferences().getString("IMAGE_DEFAULT_VIEW_APP", "");
			cls = getSharedPreferences().getString("IMAGE_DEFAULT_VIEW_ACTIVITY", "");
			break;
		case CATEGORY_AUDIO:
			pkg = getSharedPreferences().getString("AUDIO_DEFAULT_VIEW_APP", "");
			cls = getSharedPreferences().getString("AUDIO_DEFAULT_VIEW_ACTIVITY", "");
			break;
		case CATEGORY_VIDEO:
			pkg = getSharedPreferences().getString("VIDEO_DEFAULT_VIEW_APP", "");
			cls = getSharedPreferences().getString("VIDEO_DEFAULT_VIEW_ACTIVITY", "");
			break;
		}
		return pkg+":"+cls;
	}
	
	private void buildAppListForImage() {
		Intent intent = new Intent(Intent.ACTION_VIEW,null); 
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		 Uri uri = Uri.fromFile(new File("/sdcard/test.jpg")); 
		intent.setDataAndType(uri, "image/*"); 
	//	intent.setType("image/*");
		List<ResolveInfo> resolveInfos= getContext().getPackageManager().queryIntentActivities (intent, 0); 
		mAvailableApps = resolveInfos.toArray(new ResolveInfo[resolveInfos.size()]);
	}
	
	private void buildAppListForAudio() {
		Intent intent = new Intent(Intent.ACTION_VIEW,null); 
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		Uri uri = Uri.fromFile(new File("/sdcard/test.mp3")); 
		intent.setDataAndType(uri, "audio/*"); 
	//	intent.setType("audio/*");
		List<ResolveInfo> resolveInfos= getContext().getPackageManager().queryIntentActivities (intent, 0); 
		mAvailableApps = resolveInfos.toArray(new ResolveInfo[resolveInfos.size()]);
	}
	
	private void buildAppListForVideo() {
		Intent intent = new Intent(Intent.ACTION_VIEW/*"com.cooliris.media.action.REVIEW"*/); 
		Uri uri = Uri.fromFile(new File("/sdcard/test.mp4")); 
		intent.setDataAndType(uri, "video/*"); 
//		intent.setType("video/*");
		List<ResolveInfo> resolveInfos= getContext().getPackageManager().queryIntentActivities (intent, 0); 
		Intent intent2 = new Intent("com.cooliris.media.action.REVIEW");
		intent2.setDataAndType(uri, "video/*"); 
		List<ResolveInfo> resolveInfos2= getContext().getPackageManager().queryIntentActivities (intent2, 0); 
		for(ResolveInfo ri:resolveInfos2)
		{
			String new_pkg = ri.activityInfo.applicationInfo.packageName;
			boolean flag = false;
			for(ResolveInfo app:resolveInfos)
			{
				String pkg = app.activityInfo.applicationInfo.packageName;
				if(pkg.equals(new_pkg))
				{
					flag = true;
				}
			}
			if(!flag)
			{
				resolveInfos.add(ri);
			}
		}
		mAvailableApps = resolveInfos.toArray(new ResolveInfo[resolveInfos.size()]);
	}
	

	@Override
	protected void onBindView(View view) {
		// TODO Auto-generated method stub
		super.onBindView(view);
		
		mAppIcon = (ImageView)view.findViewById(R.id.image);
		TextView text = (TextView)view.findViewById(R.id.text);
		
		switch(mCategory){
		case CATEGORY_IMAGE:
			text.setText(R.string.category_picture_default_app);
			Drawable drawable_image = getContext().getResources().getDrawable(R.drawable.file_category_icon_image);
			drawable_image = drawable_image.mutate();
			int width = drawable_image.getIntrinsicWidth();
			int height = drawable_image.getIntrinsicHeight();
			drawable_image.setBounds(0, 0, width*3/4, height*3/4);
			text.setCompoundDrawables(drawable_image, null, null, null);
			String image_app_pkg = getSharedPreferences().getString("IMAGE_DEFAULT_VIEW_APP", "");
			String image_app_cls = getSharedPreferences().getString("IMAGE_DEFAULT_VIEW_ACTIVITY", "");
			bindPreferenceAppView(image_app_pkg, image_app_cls, width, height);
			break;
		case CATEGORY_AUDIO:
			text.setText(R.string.category_music_default_app);
			Drawable drawable_music = getContext().getResources().getDrawable(R.drawable.file_category_icon_audio);
			drawable_music = drawable_music.mutate();
			width = drawable_music.getIntrinsicWidth();
			height = drawable_music.getIntrinsicHeight();
			drawable_music.setBounds(0, 0, width*3/4, height*3/4);
			text.setCompoundDrawables(drawable_music, null, null, null);
			String audio_app_pkg = getSharedPreferences().getString("AUDIO_DEFAULT_VIEW_APP", "");
			String audio_app_cls = getSharedPreferences().getString("AUDIO_DEFAULT_VIEW_ACTIVITY", "");
			bindPreferenceAppView(audio_app_pkg, audio_app_cls, width, height);
			break;
		case CATEGORY_VIDEO:
			text.setText(R.string.category_video_default_app);
			Drawable drawable_video = getContext().getResources().getDrawable(R.drawable.file_category_icon_video);
			drawable_video = drawable_video.mutate();
			width = drawable_video.getIntrinsicWidth();
			height = drawable_video.getIntrinsicHeight();
			drawable_video.setBounds(0, 0, width*3/4, height*3/4);
			text.setCompoundDrawables(drawable_video, null, null, null);
			String video_app_pkg = getSharedPreferences().getString("VIDEO_DEFAULT_VIEW_APP", "");
			String video_app_cls = getSharedPreferences().getString("VIDEO_DEFAULT_VIEW_ACTIVITY", "");
			bindPreferenceAppView(video_app_pkg, video_app_cls, width, height);
			break;
		}
	}
	
	private void bindPreferenceAppView(String pkg, String cls, int width, int height){
		PackageManager pm = getContext().getPackageManager();	
		

		Drawable image_app = null;
		if(!TextUtils.isEmpty(pkg))
		{
			try {
				if(TextUtils.isEmpty(cls)){
					image_app = pm.getApplicationIcon(pkg);
				}
				else
				{
					ComponentName componentName = new ComponentName(pkg, cls);
					image_app = pm.getActivityIcon(componentName);					
				}

				if(image_app!=null){
//					width = image_app.getIntrinsicWidth();
//					height = image_app.getIntrinsicHeight();
					Bitmap bitmap = ((BitmapDrawable) image_app).getBitmap();
					// Scale it to 30 x 30
					image_app = new BitmapDrawable(/*Bitmap.createScaledBitmap(bitmap, 30, 30, true)*/bitmap);
//					image_app.setBounds(0, 0, 30, 30);
					image_app.setBounds(0, 0, width, height);
				}
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
        if(image_app==null)
        {
        	image_app = getContext().getResources().getDrawable(R.drawable.arrow_right);
//        	image_app.setBounds(0, 0, 30, 30);
        	width = image_app.getIntrinsicWidth();
			height = image_app.getIntrinsicHeight();
			image_app.setBounds(0, 0, width, height);
        }

//		mCategoryMatchPicture.setCompoundDrawables(drawable_image, null, image_app, null);
		mAppIcon.setImageDrawable(image_app);
        mAppIcon.invalidateDrawable(image_app);
//		mImage.invalidateDrawable(image_app);
	}
	

	
	
	@Override
	protected void onBindDialogView(View view) {
		// TODO Auto-generated method stub
		super.onBindDialogView(view);
		
		ListView list = (ListView) view.findViewById(R.id.app_select_list);

		List<Map<String, Object>> listdata = new ArrayList<Map<String, Object>>();

		PackageManager pm = FileManager.getAppContext().getPackageManager();

		for (ResolveInfo app : mAvailableApps) {
			Map<String, Object> map = new HashMap<String, Object>();
			try {
				// Drawable icon = pm.getApplicationIcon(pkg);
				String pkg = app.activityInfo.applicationInfo.packageName;
				String cls = app.activityInfo.name;
				ComponentName componentName = new ComponentName(pkg, cls);
				Drawable icon = pm.getActivityIcon(componentName);
				CharSequence name = app.activityInfo.loadLabel(pm);

				// String name = pm.getApplicationInfo(pkg,
				// PackageManager.GET_META_DATA).loadLabel(pm).toString();

				map.put("name", name);
				map.put("icon", icon);
				listdata.add(map);
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (mAvailableApps.length > 1) {
			Map<String, Object> map = new HashMap<String, Object>();
			// String pkg = app.activityInfo.applicationInfo.packageName;
			try {
				ArrayList<Drawable> dws = new ArrayList<Drawable>();
				for (Map<String, Object> map2 : listdata) {
					Drawable dw = (Drawable) map2.get("icon");
					if (dw != null) {
						dws.add(dw);
					}
				}
				Bitmap bm = IconUtil.makeGroupAvatarWithChildren(getContext()
						.getApplicationContext(), dws, 50);
				// Drawable icon = pm.getApplicationIcon(pkg);
				// String name = pm.getApplicationInfo(pkg,
				// PackageManager.GET_META_DATA).loadLabel(pm).toString();
				Drawable gdw = new BitmapDrawable(bm);
				map.put("name",
						getContext().getString(R.string.cancel_set_default));
				map.put("icon", gdw);
				listdata.add(map);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (mAvailableApps.length == 1) {
			Map<String, Object> map = new HashMap<String, Object>();
			// String pkg = app.activityInfo.applicationInfo.packageName;
			try {
				ArrayList<Drawable> dws = new ArrayList<Drawable>();
				for (Map<String, Object> map2 : listdata) {
					Drawable dw = (Drawable) map2.get("icon");
					if (dw != null) {
						dws.add(dw);
					}
				}
				Drawable dw2 = getContext().getResources().getDrawable(
						R.drawable.ic_app_default);
				dws.add(dw2);
				Bitmap bm = IconUtil.makeGroupAvatarWithChildren(getContext()
						.getApplicationContext(), dws, 50);
				// Drawable icon = pm.getApplicationIcon(pkg);
				// String name = pm.getApplicationInfo(pkg,
				// PackageManager.GET_META_DATA).loadLabel(pm).toString();
				Drawable gdw = new BitmapDrawable(bm);
				map.put("name",
						getContext().getString(R.string.cancel_set_default_one));
				map.put("icon", gdw);
				listdata.add(map);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		SimpleAdapter mAdapter = new SimpleAdapter(getContext(), listdata,
				R.layout.select_app_list_item, new String[] { "name", "icon" },
				new int[] { R.id.name, R.id.icon });

		mAdapter.setViewBinder(new ViewBinder() {

			@Override
			public boolean setViewValue(View view, Object data,
					String textRepresentation) {
				// TODO Auto-generated method stub
				if (data instanceof Drawable) {
					((ImageView) view).setImageDrawable((Drawable) data);
					return true;
				} else {
					return false;
				}
			}

		});

		list.setAdapter(mAdapter);

		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				String pkg = "";
				String cls = "";
				if (arg2 < arg0.getCount() - 1) {
					pkg = mAvailableApps[arg2].activityInfo.packageName;
					cls = mAvailableApps[arg2].activityInfo.name;
				}
				SharedPreferences sp = getSharedPreferences();
				SharedPreferences.Editor edit = sp.edit();
				switch (mCategory) {
				case CATEGORY_VIDEO:
					edit.putString("VIDEO_DEFAULT_VIEW_APP", pkg);
					edit.putString("VIDEO_DEFAULT_VIEW_ACTIVITY", cls);
					edit.commit();
					break;
				case CATEGORY_AUDIO:
					edit.putString("AUDIO_DEFAULT_VIEW_APP", pkg);
					edit.putString("AUDIO_DEFAULT_VIEW_ACTIVITY", cls);
					edit.commit();
					break;
				case CATEGORY_IMAGE:
					edit.putString("IMAGE_DEFAULT_VIEW_APP", pkg);
					edit.putString("IMAGE_DEFAULT_VIEW_ACTIVITY", cls);
					edit.commit();
					break;
				}

//				Map default_app_set = new HashMap();
//				default_app_set.put("type", "" + mCategory);
//				default_app_set.put("default_app", pkg);
//				MobclickAgent.onEvent(getContext(), "default_app_set",
//						default_app_set);
				// dismiss dialog
				Dialog dialog = getDialog();
				if(dialog!=null)
					dialog.dismiss();
			}

		});

	}
	
	@Override
	protected void onPrepareDialogBuilder(Builder builder) {
		// TODO Auto-generated method stub
		super.onPrepareDialogBuilder(builder);
		builder.setPositiveButton(null, null);
		builder.setNegativeButton(null, null);
	}
	
	@Override
	protected void onDialogClosed(boolean positiveResult) {
		// TODO Auto-generated method stub
		super.onDialogClosed(positiveResult);
		
		if (callChangeListener(toString())) {
            persistString(toString());
    		switch(mCategory){
    		case CATEGORY_IMAGE:
    			Drawable drawable_image = getContext().getResources().getDrawable(R.drawable.file_category_icon_image);
    			int width = drawable_image.getIntrinsicWidth();
    			int height = drawable_image.getIntrinsicHeight();
    			String image_app_pkg = getSharedPreferences().getString("IMAGE_DEFAULT_VIEW_APP", "");
    			String image_app_cls = getSharedPreferences().getString("IMAGE_DEFAULT_VIEW_ACTIVITY", "");
    			bindPreferenceAppView(image_app_pkg, image_app_cls, width*3/4, height*3/4);
    			break;
    		case CATEGORY_AUDIO:
    			Drawable drawable_music = getContext().getResources().getDrawable(R.drawable.file_category_icon_audio);
    			width = drawable_music.getIntrinsicWidth();
    			height = drawable_music.getIntrinsicHeight();
    			String audio_app_pkg = getSharedPreferences().getString("AUDIO_DEFAULT_VIEW_APP", "");
    			String audio_app_cls = getSharedPreferences().getString("AUDIO_DEFAULT_VIEW_ACTIVITY", "");
    			bindPreferenceAppView(audio_app_pkg, audio_app_cls, width*3/4, height*3/4);
    			break;
    		case CATEGORY_VIDEO:
    			Drawable drawable_video = getContext().getResources().getDrawable(R.drawable.file_category_icon_video);
    			width = drawable_video.getIntrinsicWidth();
    			height = drawable_video.getIntrinsicHeight();
    			String video_app_pkg = getSharedPreferences().getString("VIDEO_DEFAULT_VIEW_APP", "");
    			String video_app_cls = getSharedPreferences().getString("VIDEO_DEFAULT_VIEW_ACTIVITY", "");
    			bindPreferenceAppView(video_app_pkg, video_app_cls, width*3/4, height*3/4);
    			break;
    		}
        }
	}

	@Override
	protected Parcelable onSaveInstanceState() {
		// TODO Auto-generated method stub
		return super.onSaveInstanceState();
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(state);
	}

	

}
