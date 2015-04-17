package com.belugamobile.filemanager;

import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;


import com.belugamobile.filemanager.helper.FileCategoryHelper;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Feng Hu on 15-01-28.
 * <p/>
 * TODO: Add a class header comment.
 */
public class NewCategoryFragment extends BelugaRecyclerFragment/* implements LoaderManager.LoaderCallbacks<Cursor>*/{

    private static final String TAG = "NewCategoryFragment";

    private static final int LOADER_ID_CATEGORY = 1;

    private int[] mGridItemSize = {0,0};

    private int mSelectedCategory;

    private CategoryItem[] mCategoryItems = {
        new CategoryItem(R.drawable.ic_category_audio, R.string.category_audio, 0, FileCategoryHelper.CATEGORY_TYPE_AUDIO),
        new CategoryItem(R.drawable.ic_category_video, R.string.category_video, 0, FileCategoryHelper.CATEGORY_TYPE_VIDEO),
        new CategoryItem(R.drawable.ic_category_photo, R.string.category_picture, 0,FileCategoryHelper.CATEGORY_TYPE_IMAGE),
        new CategoryItem(R.drawable.ic_category_doc, R.string.category_document, 0, FileCategoryHelper.CATEGORY_TYPE_DOCUMENT),
        new CategoryItem(R.drawable.ic_category_apk, R.string.category_apk, 0, FileCategoryHelper.CATEGORY_TYPE_APK),
        new CategoryItem(R.drawable.ic_category_zip, R.string.category_zip, 0, FileCategoryHelper.CATEGORY_TYPE_ZIP),
        new CategoryItem(R.drawable.ic_category_download, R.string.category_download, 0, FileCategoryHelper.CATEGORY_TYPE_DOWNLOAD),
        new CategoryItem(R.drawable.ic_category_favorite, R.string.category_favorite, 0, FileCategoryHelper.CATEGORY_TYPE_FAVORITE),
//        new CategoryItem(R.drawable.file_category_icon_app, R.string.category_app, 0, CategorySelectEvent.CategoryType.APP),
    };

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        mRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (mRootView.getWidth() > 0 && mRootView.getHeight() > 0) {
                    calculateViewHolderSize();
                    if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        mRootView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        mRootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();

    }

//    @Override
//    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
//        Uri baseUri = null;
//        String[] projection = new String[] { "count(*) as count" };
//        switch(arg0){
//            case LOADER_ID_CATEGORY:
//                baseUri = DataStructures.CategoryColumns.CONTENT_URI;
//                projection = DataStructures.CategoryColumns.PROJECTION;
//                break;
//            default:
//                break;
//        }
//        if(baseUri!=null){
//            return new CursorLoader(getActivity(), baseUri,
//                    projection, null, null,
//                    null);
//        }
//        else{
//            return null;
//        }
//    }
//
//    @Override
//    public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
//        // TODO Auto-generated method stub
//        if(arg0 == null) {
//            return;
//        }
//        if(arg0.getId() == LOADER_ID_CATEGORY){
//            bindCategoryData(arg1);
//        }
//    }
//
//    @Override
//    public void onLoaderReset(Loader<Cursor> arg0) {
//
//    }
//
//    private void bindCategoryData(Cursor cursor){
//        if (getActivity() == null) {
//            return;
//        }
//        if(cursor!=null)
//        {
//            Map<Integer, Long> map_size = new HashMap<Integer, Long>();
//            Map<Integer, Long> map_number = new HashMap<Integer, Long>();
//            boolean flag = false;
//            while(cursor.moveToNext())
//            {
//                int category = cursor.getInt(DataStructures.CategoryColumns.CATEGORY_FIELD_INDEX);
//                long size = cursor.getLong(DataStructures.CategoryColumns.SIZE_FIELD_INDEX);
//                long number = cursor.getLong(DataStructures.CategoryColumns.NUMBER_FIELD_INDEX);
//                String storage = cursor.getString(DataStructures.CategoryColumns.STORAGE_FIELD_INDEX);
//                if(map_size.containsKey(category)){
//                    map_size.put(category, size + map_size.get(category));
//                }else{
//                    map_size.put(category, size);
//                }
//                if(map_number.containsKey(category)){
//                    map_number.put(category, number + map_number.get(category));
//                }else{
//                    map_number.put(category, number);
//                }
//                if(number!=0){
//                    flag = true;
//                }
//            }
//
//            if(!map_size.isEmpty() && !map_number.isEmpty()){
//                Iterator<Integer> iterator_number = map_size.keySet().iterator();
//                while(iterator_number.hasNext()){
//                    Integer type = iterator_number.next();
//                    Long size_ = map_size.get(type);
//                    Long number_ = map_number.get(type);
//                    if(size_!=null && number_!=null){
//                        long size = size_;
//                        long number = number_;
//                        String info;
//                        if (number==0 && size==0) {
//                            if(flag){
//                                info = "(0)";
//                            }else{
//                                info = "";
//                            }
//                        } else if (number==0) {
//                            info = "("+ FileUtil.normalize(size)+")";
//                        } else if (size==0) {
//                            info = "("+number+")";
//                        } else {
//                            info = "("+FileUtil.normalize(size)+", "+number+")";
////                            switch((int)type)
////                        {
////                            case FileUtils.FILE_TYPE_APK:
////                                mCategoryApkCountInfo.setText(info);
////                                break;
////                            case FileUtils.FILE_TYPE_AUDIO:
////                                mCategoryMusicCountInfo.setText(info);
////                                break;
////                            case FileUtils.FILE_TYPE_IMAGE:
////                                mCategoryPictureCountInfo.setText(info);
////                                break;
////                            case FileUtils.FILE_TYPE_VIDEO:
////                                mCategoryVideoCountInfo.setText(info);
////                                break;
////                            case FileUtils.FILE_TYPE_DOCUMENT:
////                                mCategoryDocumentCountInfo.setText(info);
////                                break;
////                            case FileUtils.FILE_TYPE_ZIP:
////                                mCategoryZipCountInfo.setText(info);
////                                break;
////                        }
//                        }
//                    }
//                }
//            }
//        }
//    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @InjectView(R.id.icon) ImageView icon;
        @InjectView(R.id.name) TextView name;
//        @InjectView(R.id.count) TextView count;

        private CategoryItem item;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
            itemView.setOnClickListener(this);
        }

        public void bindCategoryItem(CategoryItem item) {
            this.item = item;
            icon.setImageResource(item.icon);
            name.setText(item.name);
//            count.setText("("+String.valueOf(item.count)+")");
            this.itemView.setActivated(item.category == mSelectedCategory);
        }

        @Override
        public void onClick(View v) {
            BusProvider.getInstance().post(new CategorySelectEvent(System.currentTimeMillis(), item.category));
        }
    }

    private class CategoryItem {
        private int icon;
        private int name;
        private int count;
        private int category;

        CategoryItem(int icon, int name, int count, int category) {
            this.icon = icon;
            this.name = name;
            this.count = count;
            this.category = category;
        }
    }

    private class BelugaCategoryRecyclerAdapter extends RecyclerView.Adapter<CategoryViewHolder> {
        @Override
        public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Log.i(TAG, "onCreateViewHolder "+parent.getWidth()+","+parent.getHeight());
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_grid_item, parent, false);
            if (mGridItemSize[0] != 0) {
                view.setMinimumWidth(mGridItemSize[0]);
            }
            if (mGridItemSize[1] != 0) {
                view.setMinimumHeight(mGridItemSize[1]);
            }
            return new CategoryViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CategoryViewHolder holder, int position) {
            holder.bindCategoryItem(mCategoryItems[position]);
        }

        @Override
        public int getItemCount() {
            return mCategoryItems.length;
        }
    }

    public void setSelectedCategory(int category) {
        if (category != mSelectedCategory) {
            mSelectedCategory = category;
            if (getRecyclerAdapter() != null) {
                getRecyclerAdapter().notifyDataSetChanged();
            }
        }
    }

    private void calculateViewHolderSize() {
        int width = mRootView.getWidth()-mRecyclerView.getPaddingLeft()-mRecyclerView.getPaddingRight();
        int height = mRootView.getHeight()-mRecyclerView.getPaddingTop()-mRecyclerView.getPaddingBottom();
        if (width <= 200 * getResources().getDisplayMetrics().density) {
            mGridItemSize[0] = width;
            mGridItemSize[1] = 0;
            setLayoutManager(new GridLayoutManager(mRecyclerView.getContext(), 1));
            setRecyclerAdapter(new BelugaCategoryRecyclerAdapter());
            setRecyclerViewShown(true);
        } else {
            final int count = mCategoryItems.length;
            final int size = (int) Math.sqrt((double) (width * height) / (double) count);
            final int columns = width % size == 0 ? width / size : width / size + 1;
            final int rows = count % columns == 0 ? count / columns : count / columns + 1;
            mGridItemSize[0] = width / columns;
            mGridItemSize[1] = height / rows;

            setLayoutManager(new GridLayoutManager(mRecyclerView.getContext(), columns));
            setRecyclerAdapter(new BelugaCategoryRecyclerAdapter());
            setRecyclerViewShown(true);
        }
    }
}
