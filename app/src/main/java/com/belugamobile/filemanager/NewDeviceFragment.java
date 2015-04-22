package com.belugamobile.filemanager;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Environment;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.belugamobile.filemanager.mount.MountPoint;
import com.belugamobile.filemanager.mount.MountPointManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Feng Hu on 15-01-30.
 * <p/>
 * TODO: Add a class header comment.
 */
public class NewDeviceFragment extends BelugaRecyclerFragment implements SharedPreferences.OnSharedPreferenceChangeListener /*implements LoaderManager.LoaderCallbacks<List<MountPoint>>*/ {

    public static final String TAG = "NewDeviceFragment";

    BelugaMountReceiver mBelugaMountReceiver;
    DeviceMountListener mMountListener;

    public static final int MSG_DO_MOUNTED = 0;
    public static final int MSG_DO_EJECTED = 1;
    public static final int MSG_DO_UNMOUNTED = 2;
    public static final int MSG_DO_SDSWAP = 3;

    private Handler mHandler = new MainThreadHandler();

    private String mSelectedDevicePath;

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

    public void setSelectedDevice(String devicePath) {
        boolean oldEmpty = TextUtils.isEmpty(mSelectedDevicePath);
        boolean newEmpty = TextUtils.isEmpty(devicePath);
        if ((oldEmpty && !newEmpty) || (!oldEmpty && newEmpty) || (!oldEmpty && !newEmpty && !mSelectedDevicePath.equals(devicePath))) {
            mSelectedDevicePath = devicePath;
            if (getRecyclerAdapter() != null) {
                getRecyclerAdapter().notifyDataSetChanged();
            }
        }
    }


    private void refreshMountPointList() {
        List<MountPoint> mountPoints = MountPointManager.getInstance().getMountPoints();
        if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean(PreferenceKeys.ROOT_EXPLORER_ENABLE, false)) {
            MountPoint mountPoint = new MountPoint();
            mountPoint.mDescription = "Root explorer (/)";
            mountPoint.mPath = "/";
            mountPoint.mIsMounted = true;
            mountPoint.mIsExternal = false;
            mountPoint.mMaxFileSize = 0;
            Log.d(TAG, "init,description :" + mountPoint.mDescription + ",path : "
                    + mountPoint.mPath + ",isMounted : " + mountPoint.mIsMounted
                    + ",isExternal : " + mountPoint.mIsExternal + ", mMaxFileSize: " + mountPoint.mMaxFileSize);
            mountPoints.add(0, mountPoint);
        }
        ((BelugaDeviceRecyclerAdapter) getRecyclerAdapter()).setData(mountPoints);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setRecyclerAdapter(new BelugaDeviceRecyclerAdapter());
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

//    @Override
//    public void onLoadFinished(Loader<List<MountPoint>> loader, List<MountPoint> data) {
//        int internalCount = 0;
//        int externalCount = 0;
//
//        List<DeviceItem> items = new ArrayList<DeviceItem>();
//
//        for (MountPoint unit: data) {
//            String path = unit.path;
//            String name;
//            if (unit.) {
//                externalCount++;
//                name = getResources().getString(R.string.external_storage)
//                        + (externalCount > 1 ? " "+externalCount : "");
//            } else {
//                internalCount++;
//                name = getResources().getString(R.string.internal_storage)
//                        + (internalCount > 1 ? " "+internalCount : "");
//            }
//            Drawable icon = getResources().getDrawable(
//                    unit.isRemovable()?R.drawable.ic_action_sd_card:R.drawable.ic_action_phone_android);
//            items.add(new DeviceItem(icon, name, path));
//        }
//
//        ((BelugaDeviceRecyclerAdapter) getRecyclerAdapter()).addAll(items);
//    }
//
//    @Override
//    public void onLoaderReset(Loader<List<MountPoint>> loader) {
//
//    }
//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.device_fragment_menu, menu);
//    }

    public class DeviceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @InjectView(R.id.icon)
        ImageView icon;
        @InjectView(R.id.name)
        TextView name;

        private MountPoint item;

        public DeviceViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
            itemView.setOnClickListener(this);
        }

        public void bindDeviceItem(MountPoint item) {
            this.item = item;
            if (item.mIsExternal) {
                icon.setImageResource(R.drawable.ic_sd_storage);
            } else {
                icon.setImageResource(R.drawable.ic_phone_android);
            }
            name.setText(item.mDescription);
            this.itemView.setActivated(item.mPath.equals(mSelectedDevicePath));
        }

        @Override
        public void onClick(View v) {
            BusProvider.getInstance().post(new DeviceSelectEvent(System.currentTimeMillis(), item.mPath));
        }
    }

    private class BelugaDeviceRecyclerAdapter extends RecyclerView.Adapter<DeviceViewHolder> {

        List<MountPoint> mItems = new ArrayList<MountPoint>();

        @Override
        public DeviceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_list_item, parent, false);
            return new DeviceViewHolder(view);
        }

        @Override
        public void onBindViewHolder(DeviceViewHolder holder, int position) {
            holder.bindDeviceItem(mItems.get(position));
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        public void setData(final List<MountPoint> entries) {
            clear();
            addAll(entries);
        }

        public void clear() {
            int count = getItemCount();
            mItems.clear();
            notifyItemRangeRemoved(0, count);
        }

        public void addAll(final List<MountPoint> entries) {
            if (entries != null && entries.size() > 0) {
                int count = getItemCount();
                mItems.addAll(entries);
                notifyItemRangeInserted(count, entries.size());
            }
        }
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
