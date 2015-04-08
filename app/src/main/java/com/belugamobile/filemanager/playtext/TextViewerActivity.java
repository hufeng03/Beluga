package com.belugamobile.filemanager.playtext;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.belugamobile.filemanager.BelugaBaseActionBarActivity;
import com.belugamobile.filemanager.BelugaNavigationDrawerFragment;
import com.belugamobile.filemanager.FileManager;
import com.belugamobile.filemanager.PreferenceKeys;
import com.belugamobile.filemanager.R;
import com.belugamobile.filemanager.ui.BelugaTextView;
import com.belugamobile.filemanager.ui.TextViewPager;
import com.belugamobile.filemanager.BelugaNavigationDrawerFragment.DrawerItem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import refactor.com.android.contacts.common.util.ViewUtil;

/**
 * Created by Feng Hu on 15-02-11.
 * <p/>
 * TODO: Add a class header comment.
 */
public class TextViewerActivity extends BelugaBaseActionBarActivity {


    private static final String TAG = "TextViewerActivity";

    BelugaTextViewerTaskFragment mTaskFragment;

    BelugaNavigationDrawerFragment mNavigationDrawerFragment;

    TextViewPager mTextViewPager;
    TextPagerAdapter mTextPagerAdapter;
    TextPagerChangeListener mTextPagerChangeListener;

    private Uri mUri;

    private long mTextPos = 0;
    private int mSelectedPage = 0;
    private int mTotalPage = 0;

    private int mFontSize;
    private int mTextColor;
    private int mBackgroundColor;
    private int mPadding;

    private int mViewPagerWidth;
    private int mViewPagerHeight;

    private Map<Integer, BelugaTextView> mTextViewMap = new HashMap<Integer, BelugaTextView>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.beluga_text_viewer_activity);

        mUri = getIntent().getData();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(TextViewerActivity.this);
        mFontSize = preferences.getInt(PreferenceKeys.TEXT_VIEWER_TEXT_SIZE, 32);
        mTextColor = preferences.getInt(PreferenceKeys.TEXT_VIEWER_TEXT_COLOR, getResources().getColor(R.color.black));
        mBackgroundColor = preferences.getInt(PreferenceKeys.TEXT_VIEWER_BACKGROUND_COLOR, getResources().getColor(R.color.white));
        mPadding = preferences.getInt(PreferenceKeys.TEXT_VIEWER_PADDING, 32);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mNavigationDrawerFragment = (BelugaNavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        final DrawerLayout drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mNavigationDrawerFragment.setUp(drawerLayout, toolbar, new DrawerItem[] {
                new DrawerItem(R.id.drawer_item_text_viewer_settings, getResources().getDrawable(R.drawable.ic_settings_24dp), getString(R.string.settings_label), true),
        }, 0);


        mTextViewPager = (TextViewPager)findViewById(R.id.text_pager);

        mTextPagerAdapter = new TextPagerAdapter();
        mTextViewPager.setAdapter(mTextPagerAdapter);
        mTextPagerChangeListener = new TextPagerChangeListener();
        mTextViewPager.setOnPageChangeListener(mTextPagerChangeListener);

        FragmentManager mFragmentManager = getSupportFragmentManager();
        if (savedInstanceState == null) {
            FragmentTransaction mFragmentTransaction = mFragmentManager
                    .beginTransaction();
            BelugaTextViewerTaskFragment textViewerTaskFragment = BelugaTextViewerTaskFragment.newTextViewTaskFragment(mUri, mFontSize);
            mFragmentTransaction.add(textViewerTaskFragment, "TextViewerTaskFragment");
            mFragmentTransaction.commit();
            mTaskFragment = textViewerTaskFragment;
        } else {
            mTextPos = savedInstanceState.getLong("TextPos", 0);
            mTaskFragment = (BelugaTextViewerTaskFragment)mFragmentManager.findFragmentByTag("TextViewerTaskFragment");
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Add shadow under toolbar
            ViewUtil.addRectangularOutlineProvider(findViewById(R.id.toolbar_parent), getResources());
        }

        mTextViewPager.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mViewPagerWidth = mTextViewPager.getWidth();
                mViewPagerHeight = mTextViewPager.getHeight();
                mTaskFragment.setTextSize(mSelectedPage, mViewPagerWidth - mPadding * 2, mViewPagerHeight - mPadding * 2);

                // Initial start
                mTaskFragment.loadTriplePagesAsync(mSelectedPage);
                // Remove listener
                mTextViewPager.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putLong("TextPos", getTextPosForPage(mSelectedPage));
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mNavigationDrawerFragment.getDrawerToggle().syncState();
    }

    private class TextPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            Log.i(TAG, "getCount: "+mTotalPage);
            return mTotalPage;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            final BelugaTextView view = new BelugaTextView(TextViewerActivity.this, mPadding, mFontSize);

            List<String> content = getContentFromCache(position);

            view.setLines(content);

            container.addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mTextViewMap.put(position, view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View)object);
            mTextViewMap.remove(position);
            removeContentCache(position);
        }
    }

    private void removeContentCache(int pos) {
        mTaskFragment.removeContentCache(pos);
    }

    private List<String> getContentFromCache(int pos) {
        return mTaskFragment.getContentFromCache(pos);
    }

    private long getTextPosForPage(int page) {
        return mTaskFragment.getTextPosForPage(page);
    }

    private class TextPagerChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            mSelectedPage = position;
            loadContentForSelfAndNeighborPages();
            Toast.makeText(TextViewerActivity.this, "Page " + (position+1)+"/"+mTotalPage, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    private void loadContentForSelfAndNeighborPages() {
        mTaskFragment.loadTriplePagesAsync(mSelectedPage);
    }

    public void refreshPages(int startPage, int pageNum, int pageTotal) {
        Log.i(TAG,"refreshPages: "+startPage+","+pageNum+","+pageTotal);
        mTotalPage = pageTotal;
        for (int i = 0; i < pageNum; i++) {
            BelugaTextView view = mTextViewMap.get(startPage + i);
            if (view != null) {
                List<String> lines = getContentFromCache(startPage + i);
                if (lines != null) {
                    view.setLines(lines);
                }
            }
        }
        mTextPagerAdapter.notifyDataSetChanged();
    }


    public static class BelugaTextViewerTaskFragment extends Fragment {

        private Map<Integer, Long> mTextPosForPages = new HashMap<Integer, Long>();
        private Map<Integer, List<String>> mContentForPages = new HashMap<Integer, List<String>>();
        private int mTotalPage;

        private Uri mUri;
        private TextPaint mPaint;

        private int mTextWidth;
        private int mTextHeight;
        private int mPageLineNum;
        private int mFontSize;

        static BelugaTextViewerTaskFragment newTextViewTaskFragment(Uri uri, int fontSize) {
            BelugaTextViewerTaskFragment fragment = new BelugaTextViewerTaskFragment();
            Bundle data = new Bundle();
            data.putParcelable("Uri", uri);
            data.putInt("FontSize", fontSize);

            fragment.setArguments(data);
            return fragment;
        }

        public void setTextSize(int page, int width, int height) {
            if (mTextWidth != width || mTextHeight != height) {
                mTextWidth = width;
                mTextHeight = height;
                Log.i(TAG, "set text size " + mTextWidth + "," + mTextHeight);
                Long textPos = mTextPosForPages.get(page);
                mTextPosForPages.clear();
                mContentForPages.clear();
                if (textPos!=null) {
                    mTextPosForPages.put(page, textPos);
                }
            }
        }

        public List<String> getContentFromCache(int page) {
            return mContentForPages.get(page);
        }

        public long getTextPosForPage(int page) {
            return mTextPosForPages.get(page);
        }

        public void removeContentCache(int position) {
            mContentForPages.remove(position);
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
            mUri = getArguments().getParcelable("Uri");
            mFontSize = getArguments().getInt("FontSize");
            mPaint = new TextPaint();
            mPaint.setAntiAlias(true);
            mPaint.setTextSize(mFontSize);
            mPaint.setColor(Color.WHITE);
            mPaint.setStrokeWidth(mFontSize);
        }

        public void loadTriplePagesAsync(int pagePos) {
            Log.i(TAG, "use text size "+mTextWidth+","+mTextHeight);

            Paint.FontMetrics fm = mPaint.getFontMetrics();
            int fontHeight = (int) Math.ceil(fm.bottom - fm.top) + 4;
            mPageLineNum = mTextHeight / fontHeight;

            int firstPage = pagePos - 1;
            int secondPage = pagePos;
            int thirdPage = pagePos + 1;

            if (pagePos == 0) {
                firstPage = pagePos;
                secondPage = pagePos + 1;
                thirdPage = pagePos + 2;
            }

            int pageNum = 0;

            int startPage = 0;

            if (mContentForPages.containsKey(firstPage) && mContentForPages.containsKey(secondPage) && mContentForPages.containsKey(thirdPage)) {
                // We are good
                Log.i(TAG, "NO NEED TO LOAD ");
            } else if (mContentForPages.containsKey(firstPage) && mContentForPages.containsKey(secondPage)) {
                startPage = thirdPage;
                pageNum = 1;
                Log.i(TAG, "NEED TO LOAD 1 FROM "+thirdPage);
            } else if (mContentForPages.containsKey(firstPage)) {
                startPage = secondPage;
                pageNum = 2;
                Log.i(TAG, "NEED TO LOAD 2 FROM "+secondPage);
            } else {
                startPage = firstPage;
                pageNum = 3;
                Log.i(TAG, "NEED TO LOAD 3 FROM "+firstPage);
            }
            if (pageNum > 0) {
                List<String> lines = readLinesForPages(startPage, pageNum);
                int usedLine = 0;
                int leftLine = lines.size();
                for (int i=0; i < pageNum; i++) {
                    int number = Math.min(leftLine, mPageLineNum);
                    List<String> linesForPage = lines.subList(usedLine, usedLine+number);
                    usedLine += number;
                    leftLine -= number;
                    mContentForPages.put(startPage + i, linesForPage);
                    if (linesForPage.size() > 0) {
                        mTotalPage = Math.max(mTotalPage, startPage + i + 1);
                    }
                    Log.i(TAG, "SAVE PAGE " + (startPage + i) + " "+linesForPage.size()+"/"+lines.size()+" "+(linesForPage.size() == 0? "Empty":linesForPage.get(0)));
                }
                ((TextViewerActivity)getActivity()).refreshPages(startPage, pageNum, mTotalPage);
            }
        }

        public List<String> readLinesForPages(int pageStart, int pageCount) {
            char buffer[] = new char[1024];
            List<String> lines = new ArrayList<String>();

            long textPos = 0;
            if (pageStart > 0) {
                Long posVal = mTextPosForPages.get(pageStart);
                if (posVal != null) {
                    textPos = posVal.longValue();
                    Log.i(TAG, "retrieve text pos "+pageStart+":"+textPos);
                }
            }
            InputStream fInStream = null;
            try {
                fInStream = FileManager.getAppContext().getContentResolver().openInputStream(mUri);
                BufferedReader br = new BufferedReader(new InputStreamReader(fInStream, "UTF-8"));
                br.skip(textPos);
                StringBuffer line = new StringBuffer();
                int lineWidth = 0;
                int totalCount = 0;
                while (lines.size() < mPageLineNum*pageCount) {
                    int count = br.read(buffer, 0, 1024);
                    float[] chWidths = new float[count];
                    mPaint.getTextWidths(interceptBuffer(buffer, 0, count), chWidths);
                    for (int i = 0; i < count; i++) {
                        if (line.length() ==0 && lines.size() % mPageLineNum == 0) {
                            // Mark it
                            int page = pageStart + lines.size() / mPageLineNum;
                            mTextPosForPages.put(page, textPos + totalCount);
                            Log.i(TAG, "save text pos "+page+":"+(textPos+totalCount));
                        }
                        char ch = buffer[i];
                        if (ch == '\n') {
                            lines.add(line.toString());
                            line.setLength(0);
                            lineWidth = 0;
                        } else {
                            int width = (int) (Math.ceil(chWidths[i]));
                            if (lineWidth + width > mTextWidth) {
                                lines.add(line.toString());
                                line.setLength(0);
                                i--;
                                lineWidth = 0;
                            } else {
                                line.append(ch);
                                lineWidth += width;
                            }
                        }
                        totalCount++;
                        if (lines.size() >= mPageLineNum*pageCount) {
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fInStream != null) {
                    try {
                        fInStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return lines;
        }

        private String interceptBuffer(char[] buffer, int start, int end) {
            StringBuffer sBuffer = new StringBuffer();
            for (int i = start; i < end; i++) {
                sBuffer.append(buffer[i]);
            }
            return sBuffer.toString();
        }
    }

}
