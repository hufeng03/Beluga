package com.hufeng.playimage;

import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hufeng.filemanager.BaseFragment;
import com.hufeng.filemanager.FileManagerLoaders;
import com.hufeng.filemanager.LoaderIDs;
import com.hufeng.filemanager.R;
import com.hufeng.filemanager.browser.FileEntry;
import com.hufeng.filemanager.browser.FileEntryFactory;
import com.hufeng.filemanager.browser.FileUtils;
import com.hufeng.filemanager.browser.ImageEntry;
import com.hufeng.filemanager.provider.DataStructures;

import java.io.File;
import java.io.FileFilter;
import java.lang.ref.WeakReference;

/**
 * Created by feng on 14-1-19.
 */
public class ImageGalleryFragment extends BaseFragment implements
        ViewPager.OnPageChangeListener, LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {

    private static final String TAG = "ImageGalleryFragment";

    private ImageGalleryPagerAdapter mImageGalleryPagerAdapter;
    private ImagePreviewFragmentLifeCycleListener mLifeCycleListener;


    private ImageViewPager mImageViewPager;
    private String mInitPath;
    private String mCollectionUri;

    private View.OnClickListener mOnClickLisnter;


    public void setLifeCycleListener(ImagePreviewFragmentLifeCycleListener l) {
        mLifeCycleListener = l;
    }

    public static ImageGalleryFragment newImageGalleryFragment(
            Uri uri, String data) {
        ImageGalleryFragment fr = new ImageGalleryFragment();
        final Bundle bundle = new Bundle();
        bundle.putString("uri", uri.toString());
        bundle.putString("data", data);
        fr.setArguments(bundle);
        return fr;
    }

    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mLifeCycleListener != null) {
            mLifeCycleListener.onResume();
        }

//        this.mImageViewPager.setCurrentItem(mInitIndex);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLifeCycleListener != null) {
            mLifeCycleListener.onDestroy();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mLifeCycleListener != null) {
            mLifeCycleListener.onPause();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            // 在Gallery重启之后，这个将消失
            getFragmentManager().beginTransaction().detach(this).commit();
        }
        mCollectionUri = this.getArguments().getString("uri");
        mInitPath = getArguments().getString("data");
    }

    @SuppressWarnings("unchecked")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(
                R.layout.fragment_image_viewer, container, false);
//        if (medias.size() > 1) {
//            mIndicatorView = (IndicatorView) view.findViewById(R.id.indicator);
//            mIndicatorView.setDotCount(medias.size());
//            mIndicatorView.setCurrentDot(index);
//        }
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mImageGalleryPagerAdapter = new ImageGalleryPagerAdapter(this);
        ImageViewPager pager = (ImageViewPager) view.findViewById(R.id.pager);
        pager.setAdapter(mImageGalleryPagerAdapter);
        pager.setPageMargin((int) (20 * this.getResources().getDisplayMetrics().density + 0.5f));
        pager.setPageMarginDrawable(new ColorDrawable(0xff000000));
        pager.setOnPageChangeListener(this);
        mImageViewPager = pager;
        mImageViewPager.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        android.os.Debug.waitForDebugger();
        if (mCollectionUri.startsWith("file://")) {
            String parent = mCollectionUri.substring("file://".length());
            new DirectoryImageCursorLoader(this).execute(parent);
        } else {
            if( getLoaderManager().getLoader(LoaderIDs.LOADER_ID_CATEGORY_IMAGE)==null)	{
                getLoaderManager().initLoader(LoaderIDs.LOADER_ID_CATEGORY_IMAGE, null, this);
            }else{
                getLoaderManager().restartLoader(LoaderIDs.LOADER_ID_CATEGORY_IMAGE, null, this);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (mOnClickLisnter != null) {
            mOnClickLisnter.onClick(v);
        }
    }

    public void setOnImageSingleTapListener(
            android.view.View.OnClickListener listener) {
        mOnClickLisnter = listener;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return FileManagerLoaders.getCursorLoader(getActivity(), FileUtils.FILE_TYPE_IMAGE, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        boolean flag_hit = false;
        int idx_hit = 0;
        if (data != null && !TextUtils.isEmpty(mInitPath)) {
            while (data.moveToNext()) {
                String path = data.getString(DataStructures.ImageColumns.FILE_PATH_FIELD_INDEX);
                if (mInitPath.equals(path)) {
                    flag_hit = true;
                    break;
                } else {
                    idx_hit ++;
                }
            }
        }
        mImageGalleryPagerAdapter.swapCursor(data);
        if (flag_hit) {
            mImageViewPager.setCurrentItem(idx_hit, false);

            mImageViewPager.setVisibility(View.VISIBLE);
            if (mLifeCycleListener != null) {
                mLifeCycleListener.onImageShown();
            }
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mImageGalleryPagerAdapter.swapCursor(null);
    }

    private static final class DirectoryImageCursorLoader extends AsyncTask<String, Void, Cursor> {

        WeakReference<ImageGalleryFragment> mFragment;

        public DirectoryImageCursorLoader(ImageGalleryFragment fr) {
            mFragment = new WeakReference<ImageGalleryFragment>(fr);
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            super.onPostExecute(cursor);
            if (mFragment!= null) {
                ImageGalleryFragment fragment = mFragment.get();
                fragment.onLoadFinished(null, cursor);
            }
        }

        @Override
        protected Cursor doInBackground(String... params) {
            if (params == null || params.length == 0) {
                return null;
            }
            String root_directory = params[0];
            if (!new File(root_directory).isDirectory()) {
                return null;
            }
            File[] images = new File(root_directory).listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return FileUtils.getFileType(pathname) == FileUtils.FILE_TYPE_IMAGE;
                }
            });
            if (images == null || images.length == 0) {
                return null;
            }
            MatrixCursor cursor = new MatrixCursor(DataStructures.ImageColumns.IMAGE_PROJECTION);
            for (int i=0; i<images.length; i++) {
                MatrixCursor.RowBuilder builder = cursor.newRow();
                builder.add(DataStructures.ImageColumns.FILE_PATH_FIELD, images[i].getAbsolutePath());
            }
            return cursor;
        }
    }

    private static final class ImageGalleryPagerAdapter extends PagerAdapter {

        Cursor mImageCursor;
        WeakReference<ImageGalleryFragment> mFragment;

        public ImageGalleryPagerAdapter(ImageGalleryFragment fr) {
            mFragment = new WeakReference<ImageGalleryFragment>(fr);
        }


        @Override
        public int getCount() {
            return mImageCursor == null ? 0 : mImageCursor.getCount();
        }

        public void swapCursor(Cursor c) {
            if (mImageCursor == c)
                return;

            mImageCursor = c;
            Log.i(TAG, "swapCursor with cursor count = " + (mImageCursor == null ? 0 : mImageCursor.getCount()));

            notifyDataSetChanged();
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            if (mImageCursor == null) {
                return null; //shouldn't happen
            }
            mImageCursor.moveToPosition(position);
            String path = mImageCursor.getString(DataStructures.ImageColumns.FILE_PATH_FIELD_INDEX);
            FileEntry entry = FileEntryFactory.makeFileObject(path);
            if (!ImageEntry.class.isInstance(entry)) {
                return null; //shouldn't happen
            }
            final ZoomableImageView imageView = new ZoomableImageView(
                    mFragment.get().getActivity());
            imageView.setId((1 + position) * 1000);
            imageView.setImageData((ImageEntry)entry);
//            imageView.setOnImageLongClickListener(mFragment.get());
            imageView.setOnImageClickListener(mFragment.get());
            container.addView(imageView, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            return imageView;
        }
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int index) {
        if (mLifeCycleListener != null) {
            mLifeCycleListener.onPageSelected(index);
        }
//        this.mInitIndex = index;
//        if (mIndicatorView != nul) {
//            mIndicatorView.setCurrentDot(index);
//        }
    }



}
