package com.hufeng.filemanager.safe;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hufeng.filemanager.R;
import com.hufeng.filemanager.safebox.SafeDataStructs;

import java.io.File;
import java.util.ArrayList;

//import com.hufeng.filemanager.CategoryTab;
//import com.hufeng.filemanager.MainActivity;

public class SafeManager implements OnClickListener{
	
	private Context mContext;	
    
	private ArrayList<SafeInfo> mSafeList = new ArrayList<SafeInfo>();
    private LayoutInflater mInflater;
    private View mView;
    private View mLoading;
    private ListView mList;
    private GridView mGrid;
    private SafeGridAdapter mGridAdapter;
    private SafeListAdapter mListAdapter;
    private TextView mEmptyView; 
    private ProgressBar pb;
    
//    private TextView mCurrentPathView;
//    private LinearLayout mCurrentPathPane;
//    private LinearLayout mNavigationBar;
//    private String mSortOrder;
    
//    private ImageView mDisplayModeSwitch;
//    private ImageView mFileSearch;
    
    private PopupWindow mPopupWindow;
	
    public SafeManager(Context context) {
        this.mContext = context;
        
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = mInflater.inflate(R.layout.file_grouper, null);
        mLoading = mInflater.inflate(R.layout.file_row_loading, null);
        pb = (ProgressBar) mLoading.findViewById(R.id.progressBar1);
//        mView.findViewById(R.id.path_pane_up_level).setOnClickListener(this);
        
//        mCurrentPathView = (TextView)mView.findViewById(R.id.current_path_view);
//        mCurrentPathPane = (LinearLayout)mView.findViewById(R.id.current_path_pane);
//        mCurrentPathPane.setOnClickListener(this);
//        mNavigationBar = (LinearLayout)mView.findViewById(R.id.navigation_bar);
        
        mEmptyView = (TextView)mView.findViewById(R.id.empty_view);
        
        mList = (ListView) mView.findViewById(R.id.filelist);
        
        mGrid = (GridView)mView.findViewById(R.id.filegrid);
        mList.addFooterView(mLoading);
        
		mListAdapter = new SafeListAdapter(mContext, mSafeList);
		mList.setAdapter(mListAdapter);
        
        
        mList.setOnItemClickListener(mOnItemClickListener);
        mGrid.setOnItemClickListener(mOnItemClickListener);
        
//        ((Activity)mContext).registerForContextMenu(mList);
//        mList.setOnItemLongClickListener(this);
        
 //       mCurrentPathView.setText(R.string.category_safe);
        
//        mDisplayModeSwitch = (ImageView)mView.findViewById(R.id.file_display_mode);
//        mFileSearch = (ImageView)mView.findViewById(R.id.file_search);
//        mDisplayModeSwitch.setOnClickListener(this);
//        mFileSearch.setOnClickListener(this);
        
        mList.setVisibility(View.VISIBLE);
        mGrid.setVisibility(View.GONE);
//        mDisplayModeSwitch.setImageResource(R.drawable.display_grid);
        
        new QuerySafeTask().execute();
    }
    

    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                long id) {
            if (view.getId() == R.id.file_loading)
                return;
           

        }
    };
    
    
    class QuerySafeTask extends AsyncTask<Void,Void,Void>{

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			//pb.setVisibility(View.VISIBLE);
			if(mList.getFooterViewsCount()==0)
				mList.addFooterView(mLoading);
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			//super.onPostExecute(result);
//			if(mList.getVisibility()==View.VISIBLE)
//			{
				mListAdapter = new SafeListAdapter(mContext, mSafeList);
				mList.setAdapter(mListAdapter);
				if(mLoading!=null && mList.getFooterViewsCount()!=0)
					mList.removeFooterView(mLoading);
//			}
//			if(mGrid.getVisibility()==View.VISIBLE)
//			{
				mGridAdapter = new SafeGridAdapter(mContext, mSafeList);
				mGrid.setAdapter(mGridAdapter);
//				if(mLoading!=null && mList.getFooterViewsCount()!=0)
//					mList.removeFooterView(mLoading);
//			}
				//pb.setVisibility(View.GONE);
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			mSafeList = getAllSafes();
			return null;
		}
    	
    }
    
    public ArrayList<SafeInfo> getAllSafes(){
    	ArrayList<SafeInfo> safes = new ArrayList<SafeInfo>();
    	Cursor cursor = mContext.getContentResolver().query(SafeDataStructs.SafeColumns.CONTENT_URI, 
    			null,
    			null, 
    			null, 
    			null);
    	if(cursor!=null){
    		if(cursor.moveToFirst()){
    			SafeInfo safe = new SafeInfo();
    			safe.originalDate = cursor.getLong(SafeDataStructs.SafeColumns.FIELD_INDEX_ORIGINAL_DATE);
    			safe.originalPath = cursor.getString(SafeDataStructs.SafeColumns.FIELD_INDEX_ORIGINAL_PATH);
    			safe.safePath = cursor.getString(SafeDataStructs.SafeColumns.FIELD_INDEX_SAFE_PATH);
    			File safe_f = new File(safe.safePath);
    			if(safe_f.exists()){
    				safe.safeDate = safe_f.lastModified();
    			}
    			safe.safeDate = -1;
    			safes.add(safe);
    		}
    		cursor.close();
    	}
    	return safes;
    }
    
    public View getContentView() {
        return mView;
    }

    public void destroy() {
        mList.removeFooterView(mLoading);
        pb.setIndeterminate(false);
        pb.setVisibility(View.GONE);
        mLoading = null;
        mList.setAdapter(null);
        mListAdapter.destroy();
        mGridAdapter.destroy();
//        ((Activity)mContext).unregisterForContextMenu(mList);
    }
    
    
    
 
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
        switch (v.getId()) {
//        case R.id.file_search:
//        	Intent intent = new Intent(mContext, MainActivity.class);
//			intent.putExtra("tab_index", MainActivity.TAB_SEARCH_INDEX);
//			intent.putExtra("category", FileUtils.FILE_TYPE_APK);
//			mContext.startActivity(intent);
//			break;
//        case R.id.file_display_mode:
//        	int list_position = 0;
//        	int grid_position = 0;
//        	if(mList.getVisibility()==View.VISIBLE)
//        	{
//        		list_position = mList.getFirstVisiblePosition();
//        		mList.setVisibility(View.GONE);
//        		if(mGrid.getVisibility()==View.GONE)
//        		{
//        			mGrid.setVisibility(View.VISIBLE);
//        			mGrid.setSelection(list_position);
//        			mDisplayModeSwitch.setImageResource(R.drawable.display_list);
//        		}
//        	}
//        	else
//        	{
//        		if(mGrid.getVisibility() == View.VISIBLE)
//        		{
//        			grid_position = mGrid.getFirstVisiblePosition();
//        			mGrid.setVisibility(View.GONE);
//        		}
//        		mList.setVisibility(View.VISIBLE);
//        		mList.setSelection(grid_position);
//        		mDisplayModeSwitch.setImageResource(R.drawable.display_grid);
//        	}
//        	break;
        case R.id.path_pane_up_level:
//            	if (mFileQueryThread != null) {
//                    mFileQueryThread.destroySelf();
//                    mFileQueryThread = null;
//                }
//        	((CategoryTab) mContext).onKeyDown(KeyEvent.KEYCODE_BACK, null);

            break;
        }
	}
	
	public void refresh()
	{

	}
	
	public void pause()
	{
		
	}


}
