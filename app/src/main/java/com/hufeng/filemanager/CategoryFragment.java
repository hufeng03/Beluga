package com.hufeng.filemanager;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hufeng.filemanager.browser.FileUtils;
import com.hufeng.filemanager.provider.DataStructures;
import com.hufeng.filemanager.services.IUiImpl;
import com.hufeng.filemanager.services.UiCallServiceHelper;
import com.hufeng.filemanager.storage.StorageManager;
import com.hufeng.filemanager.utils.FileUtil;
import com.hufeng.filemanager.utils.LogUtil;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CategoryFragment extends Fragment implements OnClickListener,
        LoaderManager.LoaderCallbacks<Cursor>{

	private static final String LOG_TAG = CategoryFragment.class.getSimpleName();

	LinearLayout mSDCardMiss;

	LinearLayout mCategoryMusic;
	LinearLayout mCategoryVideo;
	LinearLayout mCategoryPicture;
	LinearLayout mCategoryDocument;
	LinearLayout mCategoryZip;
	LinearLayout mCategoryApk;
    LinearLayout mCategoryDownload;
	LinearLayout mCategoryFavorite;
	LinearLayout mCategoryApp;
    LinearLayout mCategorySafe;
    LinearLayout mCategoryCloud;
	
	TextView mCategoryMusicCountInfo;
	TextView mCategoryVideoCountInfo;
	TextView mCategoryPictureCountInfo;
	TextView mCategoryDocumentCountInfo;
	TextView mCategoryZipCountInfo;
	TextView mCategoryApkCountInfo;
	
	private static final int LOADER_ID_CATEGORY = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.category_fragment, container, false);
		return view;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mSDCardMiss = (LinearLayout)view.findViewById(R.id.sd_not_available_page);
		mCategoryMusic = (LinearLayout)view.findViewById(R.id.category_music);
		mCategoryVideo = (LinearLayout)view.findViewById(R.id.category_video);
		mCategoryPicture = (LinearLayout)view.findViewById(R.id.category_picture);
		mCategoryDownload = (LinearLayout)view.findViewById(R.id.category_download);
		mCategoryDocument = (LinearLayout)view.findViewById(R.id.category_document);
		mCategoryZip = (LinearLayout)view.findViewById(R.id.category_zip);
		mCategoryApk = (LinearLayout)view.findViewById(R.id.category_apk);
		mCategoryFavorite = (LinearLayout)view.findViewById(R.id.category_favorite);
		mCategoryApp = (LinearLayout)view.findViewById(R.id.category_app);
        mCategorySafe = (LinearLayout)view.findViewById(R.id.category_safe);
        mCategoryCloud = (LinearLayout)view.findViewById(R.id.category_cloud);

		mCategoryMusicCountInfo = (TextView)view.findViewById(R.id.category_music_count);
		mCategoryVideoCountInfo = (TextView)view.findViewById(R.id.category_video_count);
		mCategoryPictureCountInfo = (TextView)view.findViewById(R.id.category_picture_count);
		mCategoryDocumentCountInfo = (TextView)view.findViewById(R.id.category_document_count);
		mCategoryZipCountInfo = (TextView)view.findViewById(R.id.category_zip_count);
		mCategoryApkCountInfo = (TextView)view.findViewById(R.id.category_apk_count);
		
		mCategoryMusic.setOnClickListener(this);
		mCategoryVideo.setOnClickListener(this);
		mCategoryPicture.setOnClickListener(this);
		mCategoryDownload.setOnClickListener(this);
		mCategoryDocument.setOnClickListener(this);
		mCategoryZip.setOnClickListener(this);
		mCategoryApk.setOnClickListener(this);
		mCategoryApp.setOnClickListener(this);
		mCategoryFavorite.setOnClickListener(this);
        mCategorySafe.setOnClickListener(this);
        mCategoryCloud.setOnClickListener(this);

        if (getLoaderManager().getLoader(LOADER_ID_CATEGORY) != null) {
		    getLoaderManager().restartLoader(LOADER_ID_CATEGORY, null, this);
        } else {
            getLoaderManager().initLoader(LOADER_ID_CATEGORY, null, this);
        }

	}

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.category_music:
                BusProvider.getInstance().post(new CategorySelectEvent(System.currentTimeMillis(), CategorySelectEvent.CategoryType.AUDIO));
				break;
			case R.id.category_video:
                BusProvider.getInstance().post(new CategorySelectEvent(System.currentTimeMillis(), CategorySelectEvent.CategoryType.VIDEO));
                break;
			case R.id.category_picture:
                BusProvider.getInstance().post(new CategorySelectEvent(System.currentTimeMillis(), CategorySelectEvent.CategoryType.PHOTO));
                break;
			case R.id.category_document:
                BusProvider.getInstance().post(new CategorySelectEvent(System.currentTimeMillis(), CategorySelectEvent.CategoryType.DOC));
                break;
			case R.id.category_zip:
                BusProvider.getInstance().post(new CategorySelectEvent(System.currentTimeMillis(), CategorySelectEvent.CategoryType.ZIP));
                break;
			case R.id.category_apk:
                BusProvider.getInstance().post(new CategorySelectEvent(System.currentTimeMillis(), CategorySelectEvent.CategoryType.APK));
                break;
			case R.id.category_app:
                BusProvider.getInstance().post(new CategorySelectEvent(System.currentTimeMillis(), CategorySelectEvent.CategoryType.APP));
                break;
			case R.id.category_download:
                BusProvider.getInstance().post(new CategorySelectEvent(System.currentTimeMillis(), CategorySelectEvent.CategoryType.DOWNLOAD));
                break;
			case R.id.category_favorite:
                BusProvider.getInstance().post(new CategorySelectEvent(System.currentTimeMillis(), CategorySelectEvent.CategoryType.FAVORITE));
                break;
			}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		Uri baseUri = null;
		String[] projection = new String[] { "count(*) as count" };
		switch(arg0){
		case LOADER_ID_CATEGORY:
			baseUri = DataStructures.CategoryColumns.CONTENT_URI;
			projection = DataStructures.CategoryColumns.PROJECTION;
			break;
		default:
			break;
		}
		if(baseUri!=null){
			return new CursorLoader(getActivity(), baseUri,
	            projection, null, null,
	            null);
		}
		else{
			return null;
		}
	}
	
	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
		// TODO Auto-generated method stub
		if(arg0 == null) {
			return;
		}
		if(arg0.getId() == LOADER_ID_CATEGORY){
			bindCategoryData(arg1);	
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		
	}
	
	
	@Override
	public void onResume(){
		super.onResume();
	}
	
	@Override
	public void onPause(){
		super.onPause();
	}	
	
	private void bindCategoryData(Cursor cursor){
        if (getActivity() == null) {
            return;
        }
		if(cursor!=null)
		{
			Map<Integer, Long> map_size = new HashMap<Integer, Long>();
			Map<Integer, Long> map_number = new HashMap<Integer, Long>();
			boolean flag = false;
			while(cursor.moveToNext())
			{
				int category = cursor.getInt(DataStructures.CategoryColumns.CATEGORY_FIELD_INDEX);
				long size = cursor.getLong(DataStructures.CategoryColumns.SIZE_FIELD_INDEX);
				long number = cursor.getLong(DataStructures.CategoryColumns.NUMBER_FIELD_INDEX);
				String storage = cursor.getString(DataStructures.CategoryColumns.STORAGE_FIELD_INDEX);
				if(map_size.containsKey(category)){
					map_size.put(category, size + map_size.get(category));
				}else{
					map_size.put(category, size);
				}
				if(map_number.containsKey(category)){
					map_number.put(category, number + map_number.get(category));
				}else{
					map_number.put(category, number);
				}	
				if(number!=0){
					flag = true;
				}
			}
			
			if(!map_size.isEmpty() && !map_number.isEmpty()){
				Iterator<Integer> iterator_number = map_size.keySet().iterator();
				while(iterator_number.hasNext()){
					Integer type = iterator_number.next();
					Long size_ = map_size.get(type);
					Long number_ = map_number.get(type);
					if(size_!=null && number_!=null){
						long size = size_;
						long number = number_;
						String info;
						if (number==0 && size==0) {
							if(flag){
								info = "(0)";
							}else{
								info = "";
							}
						} else if (number==0) {
							info = "("+FileUtil.normalize(size)+")";
						} else if (size==0) {
							info = "("+number+")";
						} else {
							info = "("+FileUtil.normalize(size)+", "+number+")";
						}			
						
						switch((int)type)
						{
						case FileUtils.FILE_TYPE_APK:
							mCategoryApkCountInfo.setText(info);
							break;
						case FileUtils.FILE_TYPE_AUDIO:
							mCategoryMusicCountInfo.setText(info);
							break;
						case FileUtils.FILE_TYPE_IMAGE:
							mCategoryPictureCountInfo.setText(info);
							break;
						case FileUtils.FILE_TYPE_VIDEO:
							mCategoryVideoCountInfo.setText(info);
							break;
						case FileUtils.FILE_TYPE_DOCUMENT:
							mCategoryDocumentCountInfo.setText(info);
							break;
						case FileUtils.FILE_TYPE_ZIP:
							mCategoryZipCountInfo.setText(info);
							break;
						}
					}
				}
			}
		}
	}	



	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        LogUtil.i(LOG_TAG, "onCreateOptionsMenu");
        inflater.inflate(R.menu.category_fragment_menu, menu);
	}

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        final MenuItem categorySearchMenu = menu.findItem(R.id.menu_category_search);
        final MenuItem categoryRefreshMenu = menu.findItem(R.id.menu_category_refresh);

        final Fragment parentFragment = getParentFragment();
        boolean isFragmentVisible = true;
        if(parentFragment != null && (parentFragment instanceof FileTabFragment)) {
            isFragmentVisible = parentFragment.getUserVisibleHint();
        }
        final Activity parentActivity = getActivity();
        boolean isSearchMode = false;
        if (parentActivity != null && (parentActivity instanceof BelugaDrawerActivity)) {
            isSearchMode = ((BelugaDrawerActivity)getActivity()).isSearchMode();
        }

        final boolean menuVisible = isFragmentVisible && !isSearchMode;

        categorySearchMenu.setVisible(menuVisible);
        categoryRefreshMenu.setVisible(menuVisible);

    }

    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
			case R.id.menu_category_refresh:
                StorageManager.clear();
                Intent intent = new Intent("SHOW_ROOT_FILES_ACTION");
                LocalBroadcastManager.getInstance(FileManager.getAppContext()).sendBroadcast(intent);
                UiCallServiceHelper.getInstance().startScan();
				return true;
			case R.id.menu_category_search:
                getActivity().onSearchRequested();
				return true;
            default:
                return super.onOptionsItemSelected(item);
		}
	}

}
