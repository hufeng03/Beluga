package com.hufeng.filemanager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.hufeng.safebox.SafeCategoryAdapter;
import com.hufeng.safebox.SafeCategoryEntry;
import com.hufeng.safebox.SafeCategoryManager;

/**
 * Created by feng on 13-10-3.
 */
public class SafeBoxCategoryFragment extends GridFragment{


    private SafeCategoryAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdapter = new SafeCategoryAdapter(getActivity());
        setGridAdapter(mAdapter);
        setGridShownNoAnimation(false);

        SafeCategoryEntry[] entries = SafeCategoryManager.getAllSafeCategories(getActivity());
        mAdapter.setData(entries);
        setGridShown(true);
    }

    @Override
    public void onGridItemClick(GridView g, View v, int position, long id) {

        SafeCategoryEntry entry = mAdapter.getItem(position);
        int category = entry.category;
        if (mListener != null) {
            mListener.onSafeBoxCategoryClicked(category);
        }
//        super.onGridItemClick(g, v, position, id);
    }

    public interface SafeBoxCategoryListener {
        public void onSafeBoxCategoryClicked(int category);
    }

    public SafeBoxCategoryListener mListener;

    public void setSafeBoxCategoryListener(SafeBoxCategoryListener listener) {
        mListener = listener;
    }
}
