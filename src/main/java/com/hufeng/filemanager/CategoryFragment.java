package com.hufeng.filemanager;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.hufeng.filemanager.browser.FileUtils;
import com.hufeng.filemanager.data.DataStructures;
import com.hufeng.filemanager.data.DataStructures.CategoryColumns;
import com.hufeng.filemanager.services.IUiImpl;
import com.hufeng.filemanager.services.UiServiceHelper;
import com.hufeng.filemanager.storage.StorageManager;
import com.hufeng.filemanager.utils.FileUtil;
import com.hufeng.filemanager.utils.LogUtil;
import com.hufeng.filemanager.view.CategoryBar;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CategoryFragment extends BaseFragment implements OnClickListener,
        IUiImpl.UiCallback,
        LoaderManager.LoaderCallbacks<Cursor>{

	private static final String LOG_TAG = CategoryFragment.class.getSimpleName();

    LinearLayout mCategoryOthersPanel;
    LinearLayout mCategorySelectedPanel;
	LinearLayout mSDCardMiss;
	LinearLayout mCategoryMusic;
	LinearLayout mCategoryVideo;
	LinearLayout mCategoryPicture;
	LinearLayout mCategoryDownload;
	LinearLayout mCategoryDocument;
	LinearLayout mCategoryZip;
	LinearLayout mCategoryApk;
	LinearLayout mCategoryFavorite;
	LinearLayout mCategoryApp;
    LinearLayout mCategorySelectedGame;
    LinearLayout mCategorySelectedApp;
    LinearLayout mCategorySelectedDoc;
	
	TextView mCategoryMusicCountInfo;
	TextView mCategoryVideoCountInfo;
	TextView mCategoryPictureCountInfo;
	TextView mCategoryDocumentCountInfo;
	TextView mCategoryZipCountInfo;
	TextView mCategoryApkCountInfo;
	TextView mCategoryFavoriteCount;
	TextView mCategoryAppCount;
	TextView mCategoryDownloadCount;
    TextView mCategorySelectedGameCount;
    TextView mCategorySelectedAppCount;
    TextView mCategorySelectedDocCount;
	
	LinearLayout mInformationLayout;
	
	private static final int LOADER_ID_CATEGORY = 1;

    private WeakReference<CategoryFragmentListener> mWeakListener;

    public static interface CategoryFragmentListener {
        public void onCategorySelected(int category);
    }

    public void setListener(CategoryFragmentListener listener) {
        mWeakListener = new WeakReference<CategoryFragmentListener>(listener);
    }
	

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
        mCategoryOthersPanel = (LinearLayout)view.findViewById(R.id.category_others_panel);
        mCategorySelectedPanel = (LinearLayout)view.findViewById(R.id.category_selected_panel);
		mInformationLayout = (LinearLayout)view.findViewById(R.id.information_panel);
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
        mCategorySelectedGame = (LinearLayout)view.findViewById(R.id.category_selected_game);
        mCategorySelectedApp = (LinearLayout)view.findViewById(R.id.category_selected_app);
        mCategorySelectedDoc = (LinearLayout)view.findViewById(R.id.category_selected_doc);

		mCategoryMusicCountInfo = (TextView)view.findViewById(R.id.category_music_count);
		mCategoryVideoCountInfo = (TextView)view.findViewById(R.id.category_video_count);
		mCategoryPictureCountInfo = (TextView)view.findViewById(R.id.category_picture_count);
		mCategoryDownloadCount = (TextView)view.findViewById(R.id.category_download_count);
		mCategoryDocumentCountInfo = (TextView)view.findViewById(R.id.category_document_count);
		mCategoryZipCountInfo = (TextView)view.findViewById(R.id.category_zip_count);
		mCategoryApkCountInfo = (TextView)view.findViewById(R.id.category_apk_count);
		mCategoryFavoriteCount = (TextView)view.findViewById(R.id.category_favorite_count);
		mCategoryAppCount = (TextView)view.findViewById(R.id.category_app_count);
        mCategorySelectedGameCount = (TextView)view.findViewById(R.id.category_selected_game_count);
        mCategorySelectedAppCount = (TextView)view.findViewById(R.id.category_selected_app_count);
        mCategorySelectedDocCount = (TextView)view.findViewById(R.id.category_selected_doc_count);
		
		mCategoryMusic.setOnClickListener(this);
		mCategoryVideo.setOnClickListener(this);
		mCategoryPicture.setOnClickListener(this);
		mCategoryDownload.setOnClickListener(this);
		mCategoryDocument.setOnClickListener(this);
		mCategoryZip.setOnClickListener(this);
		mCategoryApk.setOnClickListener(this);
		mCategoryApp.setOnClickListener(this);
		mCategoryFavorite.setOnClickListener(this);
        mCategorySelectedGame.setOnClickListener(this);
        mCategorySelectedApp.setOnClickListener(this);
        mCategorySelectedDoc.setOnClickListener(this);

//        if(ChannelUtil.isKanBoxChannel(getActivity())) {
////            ((ImageView)mCategoryZip.findViewById(R.id.category_zip_icon)).setImageResource(R.drawable.file_category_icon_kanbox);
////            ((TextView)mCategoryZip.findViewById(R.id.category_zip_name)).setText(R.string.kanbox);
//
////            mCategoryOthersPanel.setVisibility(View.GONE);
////            mCategorySelectedPanel.setVisibility(View.VISIBLE);
////            view.findViewById(R.id.category_legend_zip).setVisibility(View.GONE);
////            mCategoryZipCountInfo.setVisibility(View.INVISIBLE);
//
//            ((ImageView)mCategoryApp.findViewById(R.id.category_app_icon)).setImageResource(R.drawable.file_category_icon_kanbox);
//            ((TextView)mCategoryApp.findViewById(R.id.category_app_name)).setText(R.string.kanbox);
//        }
//
        if (Constants.SHOW_SELECTED_CATEGORY) {
            mCategoryOthersPanel.setVisibility(View.GONE);
            mCategorySelectedPanel.setVisibility(View.VISIBLE);
            if (Constants.SHOW_KANBOX_CATEGORY) {
                ((ImageView)mCategoryZip.findViewById(R.id.category_zip_icon)).setImageResource(R.drawable.file_category_icon_kanbox);
                ((TextView)mCategoryZip.findViewById(R.id.category_zip_name)).setText(R.string.kanbox);
                mCategoryZip.setId(R.id.category_kanbox);
            }
        } else {
            if (Constants.SHOW_KANBOX_CATEGORY) {
                ((ImageView)mCategoryApp.findViewById(R.id.category_app_icon)).setImageResource(R.drawable.file_category_icon_kanbox);
                ((TextView)mCategoryApp.findViewById(R.id.category_app_name)).setText(R.string.kanbox);
                mCategoryApp.setId(R.id.category_kanbox);
            }
        }



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
                if (mWeakListener != null) {
                    CategoryFragmentListener listener = mWeakListener.get();
                    if (listener != null) {
                        listener.onCategorySelected(FileUtils.FILE_TYPE_AUDIO);
                    }
                }
				break;
			case R.id.category_video:
                if (mWeakListener != null) {
                    CategoryFragmentListener listener = mWeakListener.get();
                    if (listener != null) {
                        listener.onCategorySelected(FileUtils.FILE_TYPE_VIDEO);
                    }
                }
				break;
			case R.id.category_picture:
                if (mWeakListener != null) {
                    CategoryFragmentListener listener = mWeakListener.get();
                    if (listener != null) {
                        listener.onCategorySelected(FileUtils.FILE_TYPE_IMAGE);
                    }
                }
				break;
			case R.id.category_document:
                if (mWeakListener != null) {
                    CategoryFragmentListener listener = mWeakListener.get();
                    if (listener != null) {
                        listener.onCategorySelected(FileUtils.FILE_TYPE_DOCUMENT);
                    }
                }
				break;
			case R.id.category_zip:
                if (mWeakListener != null) {
                    CategoryFragmentListener listener = mWeakListener.get();
                    if (listener != null) {
//                        if(ChannelUtil.isKanBoxChannel(getActivity())) {
//                            listener.onCategorySelected(FileUtils.FILE_TYPE_CLOUD);
//                        } else {
                            listener.onCategorySelected(FileUtils.FILE_TYPE_ZIP);
//                        }
                    }
                }
				break;
			case R.id.category_apk:
                if (mWeakListener != null) {
                    CategoryFragmentListener listener = mWeakListener.get();
                    if (listener != null) {
                        listener.onCategorySelected(FileUtils.FILE_TYPE_APK);
                    }
                }
				break;
			case R.id.category_app:
                if (mWeakListener != null) {
                    CategoryFragmentListener listener = mWeakListener.get();
                    if (listener != null) {
//                        if(ChannelUtil.isKanBoxChannel(getActivity())) {
//                            listener.onCategorySelected(FileUtils.FILE_TYPE_CLOUD);
//                        } else {
                            listener.onCategorySelected(FileUtils.FILE_TYPE_APP);
//                        }
                    }
                }
			    break;
            case R.id.category_kanbox:
                if (mWeakListener != null) {
                    CategoryFragmentListener listener = mWeakListener.get();
                    if (listener != null) {
                        listener.onCategorySelected(FileUtils.FILE_TYPE_CLOUD);
                    }
                }
                break;
			case R.id.category_download:
                if (mWeakListener != null) {
                    CategoryFragmentListener listener = mWeakListener.get();
                    if (listener != null) {
                        listener.onCategorySelected(FileUtils.FILE_TYPE_DOWNLOAD);
                    }
                }
				break;
			case R.id.category_favorite:
                if (mWeakListener != null) {
                    CategoryFragmentListener listener = mWeakListener.get();
                    if (listener != null) {
                        listener.onCategorySelected(FileUtils.FILE_TYPE_FAVORITE);
                    }
                }
                break;
            case R.id.category_selected_game:
                if (mWeakListener != null) {
                    CategoryFragmentListener listener = mWeakListener.get();
                    if (listener != null) {
                        listener.onCategorySelected(FileUtils.FILE_TYPE_RESOURCE_GAME);
                    }
                }
                break;
            case R.id.category_selected_app:
                if (mWeakListener != null) {
                    CategoryFragmentListener listener = mWeakListener.get();
                    if (listener != null) {
                        listener.onCategorySelected(FileUtils.FILE_TYPE_RESOURCE_APP);
                    }
                }
                break;
            case R.id.category_selected_doc:
                if (mWeakListener != null) {
                    CategoryFragmentListener listener = mWeakListener.get();
                    if (listener != null) {
                        listener.onCategorySelected(FileUtils.FILE_TYPE_RESOURCE_DOC);
                    }
                }
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
			return new CursorLoader(this.getSherlockActivity(), baseUri,
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
        if (UiServiceHelper.getInstance().isScanning()) {
            refresh();
        } else {
            completeRefresh();
        }
        UiServiceHelper.getInstance().addCallback(this);
	}
	
	@Override
	public void onPause(){
		super.onPause();
        UiServiceHelper.getInstance().removeCallback(this);
        completeRefresh();
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
			
			String[] paths = StorageManager.getInstance(getActivity()).getMountedStorages();
			LayoutInflater inflater = LayoutInflater.from(getActivity());
			mInformationLayout.removeAllViews();
			for(int i=0;i<paths.length;i++){
				Map map = new HashMap<Integer, Long>();
				long total_size = 0;
				View child = inflater.inflate(R.layout.space_stats, null);
				child.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, 0, 1.0f));
				CategoryBar bar = (CategoryBar)child.findViewById(R.id.space_volume_stats);
				TextView label = (TextView)child.findViewById(R.id.space_volume_label);
				TextView description = (TextView)child.findViewById(R.id.space_volume_description);
				String path = paths[i];
				cursor.moveToPosition(-1);
				map.clear();
				while(cursor.moveToNext()){
					String path1 = cursor.getString(CategoryColumns.STORAGE_FIELD_INDEX);
					if(path.equalsIgnoreCase(path1)){
						long size = cursor.getLong(DataStructures.CategoryColumns.SIZE_FIELD_INDEX);
						long number = cursor.getLong(DataStructures.CategoryColumns.NUMBER_FIELD_INDEX);
						int category = cursor.getInt(DataStructures.CategoryColumns.CATEGORY_FIELD_INDEX);
						map.put(category, size);
						if(category!=0)
							total_size += size;
					}
				}
				long all_size = StorageManager.getInstance(getSherlockActivity()).getAllSize(path);
				long available_size = StorageManager.getInstance(getSherlockActivity()).getAvailableSize(path);
				long other_size =  all_size
						- available_size
						- total_size;
				if(other_size<0)
					other_size = 0;
				map.put(0, other_size);
				map.put(-1, all_size);
				bar.refresh(map);
				
				label.setText(paths[i]);
				description.setText(getString(R.string.sdcard_description, FileUtil.normalize(all_size), FileUtil.normalize(available_size)));
				mInformationLayout.addView(child, i);
			}

            if(UiServiceHelper.getInstance().isScanning())
            {
                refresh();
            }
            else
            {
                completeRefresh();
            }
		}
	}	
	
	public void setCategoryInformation(int download, int favorite, int app) {
		mCategoryDownloadCount.setText("("+download+")");
//		mCategoryFavoriteCount.setText("("+favorite+")");
		mCategoryAppCount.setText("("+app+")");
	}


	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
		completeRefresh();
		mRefreshItem = null;
		inflater.inflate(R.menu.category_fragment_menu, menu);

		mRefreshItem = menu.findItem(R.id.menu_refresh);
		if(mRefreshItem!=null)
			LogUtil.i(LOG_TAG, "new refresh item is "+mRefreshItem);
		if(mRefreshItem!=null && mRefreshItem.isVisible()){
			if(UiServiceHelper.getInstance().isScanning()) {
				LogUtil.i(LOG_TAG, "set refresh menu item to scanning");
				refresh();
			}else{
				LogUtil.i(LOG_TAG, "set refresh menu item to complete scanning");
				completeRefresh();
			}
		}
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public void onDestroyOptionsMenu(){
		LogUtil.i(LOG_TAG, "onDestroyOptionsMenu");
	    completeRefresh();
	    mRefreshItem = null;
		super.onDestroyOptionsMenu();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean flag = true;
		switch(item.getItemId()){
			case R.id.menu_refresh:
			{
                UiServiceHelper.getInstance().startScan();
				flag = true;
				break;
			}
			case R.id.menu_settings:
			{
				Intent intent3 = new Intent(getSherlockActivity(), FileManagerPreferenceActivity.class);
				startActivity(intent3);
				flag = true;
				break;
			}
		}
		return flag;
	}

	MenuItem mRefreshItem;
	Animation mRefreshAnimation;
	ImageView mRefreshView;
	boolean mRefreshing = false;
	
	private void refresh(){
		if(!mRefreshing) {
			if(mRefreshItem!=null && mRefreshItem.isVisible()){				
				if(mRefreshView == null){
					LayoutInflater inflater = LayoutInflater.from(getSherlockActivity());
					mRefreshView = (ImageView) inflater.inflate(R.layout.refresh_action_view, null);
				}				
				if(mRefreshAnimation == null) {
					mRefreshAnimation = AnimationUtils.loadAnimation(getSherlockActivity(), R.anim.clockwise_rotate);
					mRefreshAnimation.setRepeatCount(Animation.INFINITE);
				}
				if(mRefreshView.getAnimation()==null){
					mRefreshView.startAnimation(mRefreshAnimation);
				}

				mRefreshItem.setActionView(mRefreshView);
				mRefreshing = true;
			}
		}
	}
	
	public void completeRefresh() {
		if(mRefreshView!=null){
			mRefreshView.clearAnimation();
		}
		if(mRefreshing){
			if(mRefreshItem!=null) {
				mRefreshItem.setActionView(null);
			}
		}
		mRefreshing = false;
	}

    @Override
    public void scanStarted() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                refresh();
            }
        });
    }

    @Override
    public void scanCompleted() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                completeRefresh();
            }
        });
    }

    @Override
    public void changeMonitored(String dir) {

    }
}
