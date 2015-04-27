package com.belugamobile.filemanager;

import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.belugamobile.filemanager.data.BelugaTreeFolderEntry;
import com.belugamobile.filemanager.loader.FolderTreeLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Feng Hu on 15-01-30.
 * <p/>
 * TODO: Add a class header comment.
 */
public class NewDeviceFragment extends BelugaRecyclerFragment implements SharedPreferences.OnSharedPreferenceChangeListener,
        LoaderManager.LoaderCallbacks<List<BelugaTreeFolderEntry>> {

    public static final String TAG = "NewDeviceFragment";

    private static final int LOADER_ID = 1;

    BelugaMountReceiver mBelugaMountReceiver;
    DeviceMountListener mMountListener;

    public static final int MSG_DO_MOUNTED = 0;
    public static final int MSG_DO_EJECTED = 1;
    public static final int MSG_DO_UNMOUNTED = 2;
    public static final int MSG_DO_SDSWAP = 3;

    private Handler mHandler = new MainThreadHandler();

    private String mSelectedDevice;
    private String mSelectedFolder;
    private boolean mSelectedCollapse;

    BelugaFolderTreeRecyclerAdapter mAdapter;

    public static NewDeviceFragment newInstance(String device) {
        NewDeviceFragment fragment = new NewDeviceFragment();
        return fragment;
    }

    private class MainThreadHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_DO_MOUNTED:
                    doOnMounted((String) msg.obj);
                    break;
                case MSG_DO_UNMOUNTED:
                    doOnEjected((String) msg.obj);
                    break;
                case MSG_DO_EJECTED:
                    doOnUnMounted((String) msg.obj);
                    break;
                case MSG_DO_SDSWAP:
                    doOnSdSwap();
                    break;
            }
        }
    }

    private void doOnMounted(String mountPointPath) {
        // TODO: handle only files in mountPointPath
        refreshMountPointList();
    }

    private void doOnEjected(String ejectdPointPath) {
        // TODO: handle only files in mountPointPath
        refreshMountPointList();
    }

    private void doOnUnMounted(String unmountedPointPath) {
        // TODO: handle only files in mountPointPath
        refreshMountPointList();
    }

    private void doOnSdSwap() {

    }

    public void setSelectedDeviceAndFolder(String device, String folder) {
        boolean oldEmpty = TextUtils.isEmpty(mSelectedDevice);
        boolean newEmpty = TextUtils.isEmpty(device);
        if ((oldEmpty && !newEmpty) || (!oldEmpty && newEmpty) || (!oldEmpty && !newEmpty && !mSelectedDevice.equals(device))) {
            mSelectedDevice = device;
        }
        if (!TextUtils.isEmpty(folder)) {
            mSelectedFolder = folder;
        } else {
            mSelectedFolder = null;
        }

        if (isResumed()) {
            getLoaderManager().restartLoader(LOADER_ID, null, this);
        } else {

        }
    }


    private void refreshMountPointList() {
//        List<MountPoint> mountPoints = MountPointManager.getInstance().getMountPoints();
//        if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean(PreferenceKeys.ROOT_EXPLORER_ENABLE, false)) {
//            MountPoint mountPoint = new MountPoint();
//            mountPoint.mDescription = "Root explorer (/)";
//            mountPoint.mPath = "/";
//            mountPoint.mIsMounted = true;
//            mountPoint.mIsExternal = false;
//            mountPoint.mMaxFileSize = 0;
//            Log.d(TAG, "init,description :" + mountPoint.mDescription + ",path : "
//                    + mountPoint.mPath + ",isMounted : " + mountPoint.mIsMounted
//                    + ",isExternal : " + mountPoint.mIsExternal + ", mMaxFileSize: " + mountPoint.mMaxFileSize);
//            mountPoints.add(0, mountPoint);
//        }
//        ((BelugaFolderTreeRecyclerAdapter) getRecyclerAdapter()).setData(mountPoints);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdapter = new BelugaFolderTreeRecyclerAdapter();
        setRecyclerAdapter(mAdapter);
        setRecyclerViewShown(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshMountPointList();
        mBelugaMountReceiver = BelugaMountReceiver.registerMountReceiver(getActivity());
        mMountListener = new DeviceMountListener();
        mBelugaMountReceiver.registerMountListener(mMountListener);
        PreferenceManager.getDefaultSharedPreferences(getActivity()).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mBelugaMountReceiver.unregisterMountListener(mMountListener);
        getActivity().unregisterReceiver(mBelugaMountReceiver);
        PreferenceManager.getDefaultSharedPreferences(getActivity()).unregisterOnSharedPreferenceChangeListener(this);
    }


    @Override
    public Loader<List<BelugaTreeFolderEntry>> onCreateLoader(int arg0, Bundle arg1) {
        Log.i(TAG, "onCreateLoader");
        if(arg0 ==  LOADER_ID) {
            return new FolderTreeLoader(getActivity(), mSelectedDevice, mSelectedFolder, mSelectedCollapse);
        } else {
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<List<BelugaTreeFolderEntry>> loader, List<BelugaTreeFolderEntry> folderTreeEntries) {

        mAdapter.setData(folderTreeEntries);
        setRecyclerViewShown(true);
        setEmptyViewShown(folderTreeEntries.size()==0);
    }

    @Override
    public void onLoaderReset(Loader<List<BelugaTreeFolderEntry>> loader) {

    }

    public class TreeFolderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @InjectView(R.id.icon)
        ImageView icon;
        @InjectView(R.id.name)
        TextView name;
        @InjectView(R.id.expand)
        ImageView expand;

        private BelugaTreeFolderEntry item;

        public TreeFolderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
            itemView.setOnClickListener(this);
        }

        public void bindTreeFolderEntry(BelugaTreeFolderEntry item) {
            this.item = item;
            icon.setImageResource(item.icon);
//            if (item.mIsExternal) {
//                icon.setImageResource(R.drawable.ic_sd_storage);
//            } else {
//                icon.setImageResource(R.drawable.ic_phone_android);
//            }
            name.setText(item.name);
            if (!item.expandable) {
                expand.setVisibility(View.INVISIBLE);
            } else {
                expand.setVisibility(View.VISIBLE);
                if (item.expanded) {
                    expand.setImageResource(R.drawable.ic_tree_collapse);
                } else {
                    expand.setImageResource(R.drawable.ic_tree_expand);
                }
            }
            this.itemView.setActivated(item.path.equals(mSelectedFolder));
            this.expand.setPadding((int)(24.0*getResources().getDisplayMetrics().density*((float)item.depth)),0,0,0);
        }

        @Override
        public void onClick(View v) {
            if (this.item.expanded && this.item.expanded) {
                // Todo: do something to collapse this node
                mSelectedCollapse = true;
            } else {
                mSelectedCollapse = false;
            }
            // else {
                if (this.item.isRoot) {
                    BusProvider.getInstance().post(new DeviceSelectEvent(System.currentTimeMillis(), item.path));
                } else {
                    BusProvider.getInstance().post(new FolderSelectEvent(System.currentTimeMillis(), item.path, item.root));
                }
                setSelectedDeviceAndFolder(item.root, item.path);
            //}
        }
    }

    private class BelugaFolderTreeRecyclerAdapter extends RecyclerView.Adapter<TreeFolderViewHolder> {

        List<BelugaTreeFolderEntry> mFolders = new ArrayList<BelugaTreeFolderEntry>();

        @Override
        public TreeFolderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_item_layout, parent, false);
            return new TreeFolderViewHolder(view);
        }

        @Override
        public void onBindViewHolder(TreeFolderViewHolder holder, int position) {
            holder.bindTreeFolderEntry(mFolders.get(position));
        }

        @Override
        public int getItemCount() {
            return mFolders.size();
        }

        public void setData(final List<BelugaTreeFolderEntry> entries) {
            mFolders = entries;
            notifyDataSetChanged();
        }

//        public void clear() {
//            int count = getItemCount();
//            mItems.clear();
//            notifyItemRangeRemoved(0, count);
//        }
//
//        public void addAll(final List<MountPoint> entries) {
//            if (entries != null && entries.size() > 0) {
//                int count = getItemCount();
//                mItems.addAll(entries);
//                notifyItemRangeInserted(count, entries.size());
//            }
//        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (PreferenceKeys.ROOT_EXPLORER_ENABLE.equals(key)) {
            refreshMountPointList();
        }
    }

    private class DeviceMountListener implements BelugaMountReceiver.MountListener {
        @Override
        public void onMounted(String mountPoint) {
            Message.obtain(mHandler, MSG_DO_MOUNTED, mountPoint).sendToTarget();
        }

        @Override
        public void onUnMounted(String unMountPoint) {
            Message.obtain(mHandler, MSG_DO_UNMOUNTED, unMountPoint).sendToTarget();
        }

        @Override
        public void onEjected(String unMountPoint) {
            Message.obtain(mHandler, MSG_DO_EJECTED, unMountPoint).sendToTarget();
        }

        @Override
        public void onSdSwap() {
            Message.obtain(mHandler, MSG_DO_SDSWAP).sendToTarget();
        }

    }
}
