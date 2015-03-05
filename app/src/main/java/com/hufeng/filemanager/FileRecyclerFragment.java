package com.hufeng.filemanager;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.hufeng.filemanager.data.FileEntry;
import com.hufeng.filemanager.ui.BelugaActionController;

/**
 * Created by feng on 13-9-9.
 */
public abstract class FileRecyclerFragment extends BelugaRecyclerFragment implements BelugaFragmentInterface{

    protected String mSearchString;

    private BelugaDisplayModeAdapter mDisplayModeAdapter;

    private BelugaActionController mBelugaActionController = null;

    private BelugaActionControllerActivity mOperationActivity;

    public FileRecyclerFragment() {

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof BelugaActionControllerActivity) {
            mOperationActivity = (BelugaActionControllerActivity) activity;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOperationActivity = null;
    }

    @Override
    public BelugaActionController getActionController() {
        if (mBelugaActionController != null) {
            return mBelugaActionController;
        } else {
            return mOperationActivity == null ? null : mOperationActivity.getGlobalActionController();
        }
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    protected void switchDisplay() {
        int position = getLayoutManager().findFirstVisibleItemPosition();
        mDisplayModeAdapter.switchDisplayMode();
        refreshLayoutManager();
        getLayoutManager().scrollToPosition(position);
    }

    protected void changeSort() {

    }


    protected void refreshLayoutManager() {
        int padding = getResources().getDimensionPixelSize(R.dimen.recycler_view_padding);
        if (mDisplayModeAdapter.getDisplayMode() == BelugaDisplayMode.GRID) {
            //In case that mRecyclerView is not visible.
            int width = mRootView.getWidth()-mRecyclerView.getPaddingRight()-mRecyclerView.getPaddingLeft();
            int height = mRootView.getHeight()-mRecyclerView.getPaddingTop()-mRecyclerView.getPaddingBottom();

            int size = (int)Math.sqrt((double)(width*height)/12.0d);
            int columns = width/size;
            setLayoutManager(new GridLayoutManager(getActivity(), columns));
            mRecyclerView.setPadding(padding, padding, padding, padding);
        } else {
            setLayoutManager(new GridLayoutManager(getActivity(), 1));
            mRecyclerView.setPadding(0, padding, 0, padding);
        }
    }

    public void refreshUI() {
        RecyclerView.Adapter adapter = getRecyclerAdapter();
        adapter.notifyDataSetChanged();
    }

    public abstract FileEntry[] getAllFiles();

    public BelugaDisplayMode getDisplayMode() {
        return mDisplayModeAdapter.getDisplayMode();
    }

    @Override
    public void setRecyclerAdapter(RecyclerView.Adapter adapter) {
        super.setRecyclerAdapter(adapter);
        mDisplayModeAdapter = (BelugaDisplayModeAdapter)adapter;
    }
}
