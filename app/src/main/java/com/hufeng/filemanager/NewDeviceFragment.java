package com.hufeng.filemanager;

import android.graphics.drawable.Drawable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hufeng.filemanager.storage.StorageUnit;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Feng Hu on 15-01-30.
 * <p/>
 * TODO: Add a class header comment.
 */
public class NewDeviceFragment extends BelugaRecyclerFragment implements LoaderManager.LoaderCallbacks<List<StorageUnit>> {

    public static final String TAG = "NewDeviceFragment";

    private static final int LOADER_ID_DEVICE = 1;

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

        if (getLoaderManager().getLoader(LOADER_ID_DEVICE) != null) {
            getLoaderManager().restartLoader(LOADER_ID_DEVICE, null, this);
        } else {
            getLoaderManager().initLoader(LOADER_ID_DEVICE, null, this);
        }
    }

    @Override
    public Loader<List<StorageUnit>> onCreateLoader(int id, Bundle args) {
        if (id == LOADER_ID_DEVICE) {
            return new DeviceLoader(getActivity());
        } else {
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<List<StorageUnit>> loader, List<StorageUnit> data) {
        int internalCount = 0;
        int externalCount = 0;

        List<DeviceItem> items = new ArrayList<DeviceItem>();

        for (StorageUnit unit: data) {
            String path = unit.path;
            String name;
            if (unit.isRemovable()) {
                externalCount++;
                name = getResources().getString(R.string.external_storage)
                        + (externalCount > 1 ? " "+externalCount : "");
            } else {
                internalCount++;
                name = getResources().getString(R.string.internal_storage)
                        + (internalCount > 1 ? " "+internalCount : "");
            }
            Drawable icon = getResources().getDrawable(
                    unit.isRemovable()?R.drawable.ic_action_sd_card:R.drawable.ic_action_phone_android);
            items.add(new DeviceItem(icon, name, path));
        }

        ((BelugaDeviceRecyclerAdapter) getRecyclerAdapter()).addAll(items);
    }

    @Override
    public void onLoaderReset(Loader<List<StorageUnit>> loader) {

    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.device_fragment_menu, menu);
//    }

    public class DeviceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @InjectView(R.id.icon)
        ImageView icon;
        @InjectView(R.id.name)
        TextView name;

        private DeviceItem item;

        public DeviceViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
            itemView.setOnClickListener(this);
        }

        public void bindDeviceItem(DeviceItem item) {
            this.item = item;
            icon.setImageDrawable(item.icon);
            name.setText(item.name);
        }

        @Override
        public void onClick(View v) {
            BusProvider.getInstance().post(new DeviceSelectEvent(System.currentTimeMillis(), item.path));
        }
    }

    private class DeviceItem {
        private Drawable icon;
        private String name;
        private String path;

        DeviceItem(Drawable icon, String name, String path) {
            this.icon = icon;
            this.name = name;
            this.path = path;
        }
    }

    private class BelugaDeviceRecyclerAdapter extends RecyclerView.Adapter<DeviceViewHolder> {

        List<DeviceItem> mItems = new ArrayList<DeviceItem>();

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

        public void setData(final List<DeviceItem> entries) {
            clear();
            addAll(entries);
        }

        public void clear() {
            int count = getItemCount();
            mItems.clear();
            notifyItemRangeRemoved(0, count);
        }

        public void addAll(final List<DeviceItem> entries) {
            if (entries != null && entries.size() > 0) {
                int count = getItemCount();
                mItems.addAll(entries);
                notifyItemRangeInserted(count, entries.size());
            }
        }
    }
}
