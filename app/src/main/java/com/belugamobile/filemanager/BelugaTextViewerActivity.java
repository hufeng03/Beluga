package com.belugamobile.filemanager;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.belugamobile.filemanager.ui.BelugaTextView;
import com.belugamobile.filemanager.ui.TextViewPager;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Feng Hu on 15-03-31.
 * <p/>
 * TODO: Add a class header comment.
 */
public class BelugaTextViewerActivity extends BelugaBaseActionBarActivity {

    BelugaTextViewerTaskFragment mTaskFragment;

    TextViewPager mTextViewPager;
    TextPagerAdapter mTextPagerAdapter;
    TextPagerChangeListener mTextPagerChangeListener;

    private Uri mUri;

    private int mCurrentPagePosition = 0;
    private int mStartPosInCurrentPage = 0;
    private int mEndPoseInCurrentPage = 0;

    private Map<Integer, BelugaTextView> mTextViewMap = new HashMap<Integer, BelugaTextView>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.beluga_text_viewer_activity);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.primary_color_dark));
        }

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mUri = getIntent().getData();

        mTextViewPager = (TextViewPager)findViewById(R.id.text_pager);

        mTextPagerAdapter = new TextPagerAdapter();
        mTextViewPager.setAdapter(mTextPagerAdapter);
        mTextPagerChangeListener = new TextPagerChangeListener();
        mTextViewPager.setOnPageChangeListener(mTextPagerChangeListener);

        FragmentManager mFragmentManager = getSupportFragmentManager();
        if (savedInstanceState == null) {
            FragmentTransaction mFragmentTransaction = mFragmentManager
                    .beginTransaction();
            BelugaTextViewerTaskFragment textViewerTaskFragment = new BelugaTextViewerTaskFragment();
            mFragmentTransaction.add(textViewerTaskFragment, "TextViewerTaskFragment");
            mFragmentTransaction.commit();
            mTaskFragment = textViewerTaskFragment;
        } else {
            mTaskFragment = (BelugaTextViewerTaskFragment)mFragmentManager.findFragmentByTag("TextViewerTaskFragment");
        }
    }

    private class TextPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return Math.max(mCurrentPagePosition + 2,3);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            final BelugaTextView view = new BelugaTextView(BelugaTextViewerActivity.this, mUri);

            List<String> content = getContentFromCache(position);

            if (content != null) {
                view.setLines(content);
            }

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

    private class TextPagerChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            mCurrentPagePosition = position;
            mTextPagerAdapter.notifyDataSetChanged();
            loadContentForSelfAndNeighborPages();
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    private void loadContentForSelfAndNeighborPages() {
        mTaskFragment.loadTriplePagesAsync(mCurrentPagePosition);
    }

    public static class BelugaTextViewerTaskFragment extends Fragment {

        private Map<Integer, Integer> mTextPosForPages = new HashMap<Integer, Integer>();
        private Map<Integer, List<String>> mContentForPages = new HashMap<Integer, List<String>>();

        private Uri mUri;
        private TextPaint mPaint;

        private int mScreenWidth;
        private int mScreenHeight;
        private int mPageLineNum;

        BelugaTextViewerTaskFragment newTextViewTaskFragment(Uri uri) {
            BelugaTextViewerTaskFragment fragment = new BelugaTextViewerTaskFragment();
            Bundle data = new Bundle();
            data.putParcelable("Uri", uri);
            fragment.setArguments(data);
            return fragment;
        }

        public List<String> getContentFromCache(int pos) {
            return mContentForPages.get(pos);
        }

        public void removeContentCache(int position) {
            mContentForPages.remove(position);
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
            mUri = getArguments().getParcelable("Uri");
            mPaint = new TextPaint();
            mPaint.setAntiAlias(true);
            mPaint.setTextSize(16);
            mPaint.setColor(Color.WHITE);
            mPaint.setStrokeWidth(16);

            WindowManager wm = (WindowManager)getActivity().getSystemService(Context.WINDOW_SERVICE);
            Point point = new Point();
            wm.getDefaultDisplay().getSize(point);
            mScreenWidth = point.x;
            mScreenHeight = point.y;
            Paint.FontMetrics fm = mPaint.getFontMetrics();
            int fontHeight = (int) Math.ceil(fm.bottom - fm.top) + 4;
            mPageLineNum = mScreenHeight / fontHeight;
        }

        public void loadTriplePagesAsync(int pagePos) {
            int firstPage = pagePos - 1;
            int secondPage = pagePos;
            int thirdPage = pagePos + 1;

            if (pagePos == 0) {
                firstPage = pagePos;
                secondPage = pagePos + 1;
                thirdPage = pagePos + 2;
            }

            Integer posInText  = mTextPosForPages.get(firstPage);
            if (posInText == null) {
                // Load first, second, third page
                readLinesForPages(firstPage, 3);
            } else {
                posInText  = mTextPosForPages.get(secondPage);
                if (posInText == null) {
                    //Load second and third pages
                    if (mContentForPages.containsKey(firstPage)) {
                        readLinesForPages(secondPage, 2);
                    } else {
                        readLinesForPages(firstPage, 3);
                    }
                } else {
                    posInText  = mTextPosForPages.get(thirdPage);
                    if (posInText == null) {
                        //Load third page
                        if (mContentForPages.containsKey(firstPage) && mContentForPages.containsKey(secondPage)) {
                            readLinesForPages(thirdPage, 1);
                        } else if (mContentForPages.containsKey(firstPage)) {
                            readLinesForPages(secondPage, 2);
                        } else {
                            readLinesForPages(firstPage, 3);
                        }
                    } else {
                        if (mContentForPages.containsKey(firstPage) && mContentForPages.containsKey(secondPage) && mContentForPages.containsKey(thirdPage)) {
                        } else if (mContentForPages.containsKey(firstPage) && mContentForPages.containsKey(secondPage)) {
                            readLinesForPages(thirdPage, 1);
                        } else if (mContentForPages.containsKey(firstPage)) {
                            readLinesForPages(secondPage, 2);
                        } else {
                            readLinesForPages(firstPage, 3);
                        }
                    }
                }
            }
        }

        public List<String> readLinesForPages(long pos, int pageCount) {
            char buffer[] = new char[1024];
            List<String> lines = new ArrayList<String>();

            InputStream fInStream = null;
            try {
                fInStream = FileManager.getAppContext().getContentResolver().openInputStream(mUri);
                BufferedReader br = new BufferedReader(new InputStreamReader(fInStream, "UTF-8"));
                br.skip(pos);
                StringBuffer line = new StringBuffer();
                int lineWidth = 0;
                //Find self and next lines
                while (lines.size() < mPageLineNum*pageCount) {
                    int count = br.read(buffer, 0, 1024);
                    float[] chWidths = new float[count];
                    mPaint.getTextWidths(interceptBuffer(buffer, 0, count), chWidths);
                    for (int i = 0; i < count; i++) {
                        char ch = buffer[i];
                        if (ch == '\n') {
                            lines.add(line.toString());
                            line.setLength(0);
                            lineWidth = 0;
                        } else {
                            int width = (int) (Math.ceil(chWidths[i]));
                            if (lineWidth + width > mScreenWidth) {
                                lines.add(line.toString());
                                line.setLength(0);
                                i--;
                                lineWidth = 0;
                            } else {
                                line.append(ch);
                                lineWidth += width;
                            }
                        }

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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
